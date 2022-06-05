/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.messagesubsystem.rse.IQueuedMessageSubsystem;
import biz.isphere.messagesubsystem.rse.MonitoredMessageQueue;
import biz.isphere.messagesubsystem.rse.MonitoringAttributes;
import biz.isphere.messagesubsystem.rse.QueuedMessageFactory;
import biz.isphere.messagesubsystem.rse.QueuedMessageFilter;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QueuedMessage;
import com.ibm.etools.iseries.core.IISeriesSubSystem;
import com.ibm.etools.iseries.core.IISeriesSubSystemCommandExecutionProperties;
import com.ibm.etools.iseries.core.ISeriesSystemDataStore;
import com.ibm.etools.iseries.core.ISeriesSystemManager;
import com.ibm.etools.iseries.core.ISeriesSystemToolbox;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.as400cmdsubsys.CmdSubSystem;
import com.ibm.etools.systems.as400cmdsubsys.impl.CmdSubSystemImpl;
import com.ibm.etools.systems.as400filesubsys.FileSubSystem;
import com.ibm.etools.systems.core.ISystemMessages;
import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.core.messages.SystemMessage;
import com.ibm.etools.systems.dftsubsystem.impl.DefaultSubSystemImpl;
import com.ibm.etools.systems.model.ISystemMessageObject;
import com.ibm.etools.systems.model.SystemConnection;
import com.ibm.etools.systems.model.SystemRegistry;
import com.ibm.etools.systems.model.impl.SystemMessageObject;
import com.ibm.etools.systems.subsystems.ISystem;
import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractSystemManager;

public class QueuedMessageSubSystem extends DefaultSubSystemImpl implements IISeriesSubSystem, IQueuedMessageSubsystem {

    private Object syncObject = new Object();

    private CommunicationsListener communicationsListener;
    private MonitoringAttributes monitoringAttributes;
    private boolean isStarting;
    private MonitoredMessageQueue pendingMonitoredMessageQueue;
    private MonitoredMessageQueue currentMonitoredMessageQueue;

    public QueuedMessageSubSystem() {
        super();

        this.monitoringAttributes = new MonitoringAttributes(this);
    }

    @Override
    protected Object[] internalResolveFilterString(IProgressMonitor monitor, String filterString) throws InvocationTargetException,
        InterruptedException {

        QueuedMessageResource[] queuedMessageResources;
        QueuedMessageFilter queuedMessageFilter = new QueuedMessageFilter(filterString);

        try {

            QueuedMessageFactory factory = new QueuedMessageFactory(getToolboxAS400Object());
            QueuedMessage[] queuedMessages = factory.getQueuedMessages(queuedMessageFilter);
            queuedMessageResources = new QueuedMessageResource[queuedMessages.length];

            for (int i = 0; i < queuedMessageResources.length; i++) {
                queuedMessageResources[i] = new QueuedMessageResource(this);
                queuedMessageResources[i].setQueuedMessage(queuedMessages[i]);
            }
        } catch (Exception e) {
            return new Object[] { createErrorMessage(e) };
        }

        return queuedMessageResources;
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    @Override
    public Shell getShell() {
        // Damn, this caused me a lot of grief! Phil
        if (shell != null) {
            return shell;
        } else {
            return super.getShell();
        }
    }

    public void restartMessageMonitoring() {

        if (monitoringAttributes.isMonitoringEnabled()) {
            startMonitoring();
        } else {
            stopMonitoring();
        }
    }

    public boolean isMonitored(MessageQueue messageQueue) {

        if (messageQueue == null) {
            ISpherePlugin.logError("*** Null value passed to QueuedMessageSubSystem.isMonitored() ***", null); //$NON-NLS-1$
            return false;
        }

        synchronized (syncObject) {
            MonitoredMessageQueue monitoredMessageQueue = getMonitoredMessageQueue();
            if (monitoredMessageQueue != null && monitoredMessageQueue.getPath().equals(messageQueue.getPath())) {
                return true;
            }
        }

        return false;
    }

    public void removedFromMonitoredMessageQueue(QueuedMessage queuedMessage) {

        synchronized (syncObject) {
            MonitoredMessageQueue monitoredMessageQueue = getMonitoredMessageQueue();
            if (monitoredMessageQueue != null) {
                monitoredMessageQueue.remove(queuedMessage);
            }
        }
    }
    
    public void messageMonitorStarted(MonitoredMessageQueue messageQueue) {

        synchronized (syncObject) {
            if (messageQueue != pendingMonitoredMessageQueue) {
                ISpherePlugin.logError("*** Unexpected message queue passed to QueuedMessageSubSystem.messageMonitorStopped() ***", null); //$NON-NLS-1$
                return;
            }

            currentMonitoredMessageQueue = messageQueue;
            pendingMonitoredMessageQueue = null;
        }

        debugPrint("==> Subsystem: Thread " + messageQueue.hashCode() + " started."); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void messageMonitorStopped(MonitoredMessageQueue messageQueue) {

        synchronized (syncObject) {
            if (messageQueue != currentMonitoredMessageQueue) {
                ISpherePlugin.logError("*** Unexpected message queue passed to QueuedMessageSubSystem.messageMonitorStopped() ***", null); //$NON-NLS-1$
                return;
            }

            // currentMonitoredMessageQueue = pendingMonitoredMessageQueue;
            // pendingMonitoredMessageQueue = null;
            currentMonitoredMessageQueue = null;
        }

        debugPrint("<== Subsystem: Thread " + messageQueue.hashCode() + " stopped."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private MonitoredMessageQueue getMonitoredMessageQueue() {

        if (pendingMonitoredMessageQueue != null) {
            return pendingMonitoredMessageQueue;
        }

        if (currentMonitoredMessageQueue != null) {
            return currentMonitoredMessageQueue;
        }

        return null;
    }

    private void debugPrint(String message) {
        // Xystem.out.println(message);
    }

    /*
     * Start/Stop message monitor thread
     */

    public boolean hasPendingRequest() {

        if (pendingMonitoredMessageQueue != null) {
            debugPrint("Subsystem: have pending requests."); //$NON-NLS-1$
            return true;
        }

        debugPrint("Subsystem: OK - no pending requests."); //$NON-NLS-1$
        return false;
    }

    public void startMonitoring() {

        if (!monitoringAttributes.isMonitoringEnabled()) {
            return;
        }

        if (isStarting) {
            return;
        }

        try {

            isStarting = true;

            synchronized (syncObject) {

                // Start new message monitor
                debugPrint("Subsystem: Starting message monitor thread ..."); //$NON-NLS-1$
                pendingMonitoredMessageQueue = new MonitoredMessageQueue(this, new AS400(getToolboxAS400Object()), monitoringAttributes);
                pendingMonitoredMessageQueue.startMonitoring();

                // End running message monitor
                if (currentMonitoredMessageQueue != null) {
                    debugPrint("Subsystem: Stopping previous message monitor thread ..."); //$NON-NLS-1$
                    currentMonitoredMessageQueue.stopMonitoring();
                }
            }

        } finally {
            isStarting = false;
        }
    }

    public void stopMonitoring() {

        synchronized (syncObject) {

            if (currentMonitoredMessageQueue == null) {
                return;
            }

            // if (pendingMonitoredMessageQueue != null) {
            // debugPrint("Subsystem: Stopping pending queue: " +
            // pendingMonitoredMessageQueue.hashCode());
            // pendingMonitoredMessageQueue.stopMonitoring();
            // } else {
            debugPrint("Subsystem: Stopping current queue: " + currentMonitoredMessageQueue.hashCode()); //$NON-NLS-1$
            currentMonitoredMessageQueue.stopMonitoring();
            // }
        }
    }

    /*
     * Start of RDi/WDSCi specific methods.
     */
    
    @Override
    protected Object[] internalResolveFilterString(IProgressMonitor monitor, Object parent, String filterString) throws InvocationTargetException,
        InterruptedException {

        return internalResolveFilterString(monitor, filterString);
    }

    public IISeriesSubSystemCommandExecutionProperties getCommandExecutionProperties() {
        return getObjectSubSystem();
    }

    public CmdSubSystem getCmdSubSystem() {

        SystemConnection sc = getSystemConnection();
        SystemRegistry registry = SystemPlugin.getTheSystemRegistry();
        SubSystem[] subsystems = registry.getSubSystems(sc);
        SubSystem subsystem;

        for (int ssIndx = 0; ssIndx < subsystems.length; ssIndx++) {
            subsystem = subsystems[ssIndx];
            if (subsystem instanceof CmdSubSystemImpl) {
                return (CmdSubSystemImpl)subsystem;
            }
        }

        return null;
    }

    public FileSubSystem getObjectSubSystem() {
        return ISeriesConnection.getConnection(getSystemConnection()).getISeriesFileSubSystem();
    }

    private SystemMessageObject createErrorMessage(Throwable e) {
        
        SystemMessage msg = SystemPlugin.getPluginMessage(ISystemMessages.MSG_GENERIC_E);
        msg.makeSubstitution(e.getMessage());
        SystemMessageObject msgObj = new SystemMessageObject(msg, ISystemMessageObject.MSGTYPE_ERROR, null);
        
        return msgObj;
    }

    public AS400 getToolboxAS400Object() {
        ISeriesSystemToolbox system = (ISeriesSystemToolbox)getSystem();
        return system.getAS400Object();
    }

    @Override
    public ISystem getSystem() {

        ISystem system = super.getSystem();

        if (communicationsListener == null) {
            communicationsListener = new CommunicationsListener(this);
            system.addCommunicationsListener(communicationsListener);
        }

        return system;
    }

    @Override
    public AbstractSystemManager getSystemManager() {
        return ISeriesSystemManager.getTheISeriesSystemManager();
    }

    public ISeriesSystemDataStore getISeriesSystem() {
        ISeriesSystemDataStore iSeriesSystemDataStore = (ISeriesSystemDataStore)getSystem();
        return iSeriesSystemDataStore;
    }

    public int getCcsid() {
        return IBMiHostContributionsHandler.getSystemCcsid(getSystemConnectionName());
    }

    public String getVendorAttribute(String key) {
        return super.getVendorAttribute(MonitoringAttributes.VENDOR_ID, key);
    }

    public void setVendorAttribute(String key, String value) {
        super.setVendorAttribute(MonitoringAttributes.VENDOR_ID, key, value);
    }
}