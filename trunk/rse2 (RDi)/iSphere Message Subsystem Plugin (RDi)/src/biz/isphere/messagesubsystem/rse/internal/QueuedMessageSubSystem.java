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
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.IProperty;
import org.eclipse.rse.core.model.IPropertySet;
import org.eclipse.rse.core.model.PropertySet;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessage;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.RSEUIPlugin;
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
import com.ibm.etools.iseries.subsystems.qsys.IISeriesSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.commands.QSYSCommandSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class QueuedMessageSubSystem extends SubSystem implements IISeriesSubSystem, IQueuedMessageSubsystem {

    private Object syncObject = new Object();

    private CommunicationsListener communicationsListener;
    private MonitoringAttributes monitoringAttributes;
    private boolean isStarting;
    private MonitoredMessageQueue pendingMonitoredMessageQueue;
    private MonitoredMessageQueue currentMonitoredMessageQueue;

    public QueuedMessageSubSystem(IHost host, IConnectorService connectorService) {
        super(host, connectorService);

        this.monitoringAttributes = new MonitoringAttributes(this);

        this.communicationsListener = new CommunicationsListener(this);
        getConnectorService().addCommunicationsListener(communicationsListener);
    }

    @Override
    protected Object[] internalResolveFilterString(String filterString, IProgressMonitor monitor) throws InvocationTargetException,
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
        // System.out.println(message);
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
    protected Object[] internalResolveFilterString(Object parent, String filterString, IProgressMonitor monitor) throws InvocationTargetException,
        InterruptedException {

        return internalResolveFilterString(filterString, monitor);
    }

    public QSYSObjectSubSystem getCommandExecutionProperties() {
        return IBMiConnection.getConnection(getHost()).getQSYSObjectSubSystem();
    }

    public QSYSCommandSubSystem getCmdSubSystem() {

        IHost iHost = getHost();
        ISubSystem[] iSubSystems = iHost.getSubSystems();
        for (int ssIndx = 0; ssIndx < iSubSystems.length; ssIndx++) {
            SubSystem subsystem = (SubSystem)iSubSystems[ssIndx];
            if ((subsystem instanceof QSYSCommandSubSystem)) {
                return (QSYSCommandSubSystem)subsystem;
            }
        }
        return null;
    }

    public ISubSystem getObjectSubSystem() {

        IHost iHost = getHost();
        ISubSystem[] iSubSystems = iHost.getSubSystems();
        for (int ssIndx = 0; ssIndx < iSubSystems.length; ssIndx++) {
            ISubSystem iSubSystem = iSubSystems[ssIndx];
            if ((iSubSystem instanceof QSYSObjectSubSystem)) {
                return iSubSystem;
            }
        }

        return null;
    }

    private SystemMessageObject createErrorMessage(Throwable e) {

        SystemMessage msg = RSEUIPlugin.getPluginMessage("RSEO1012"); //$NON-NLS-1$
        msg.makeSubstitution(e.getMessage());
        SystemMessageObject msgObj = new SystemMessageObject(msg, 0, null);

        return msgObj;
    }

    private AS400 getToolboxAS400Object() {

        try {
            return IBMiConnection.getConnection(getHost()).getAS400ToolboxObject();
        } catch (SystemMessageException e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public int getCcsid() {
        return IBMiHostContributionsHandler.getSystemCcsid(getHostAliasName());
    }

    public String getVendorAttribute(String key) {

        IProperty property = getVendorAttributes().getProperty(key);
        if (property == null) {
            return null;
        }

        return property.getValue();
    }

    public void setVendorAttribute(String key, String value) {
        getVendorAttributes().addProperty(key, value);
    }

    private IPropertySet getVendorAttributes() {

        IPropertySet propertySet = getPropertySet(MonitoringAttributes.VENDOR_ID);
        if (propertySet == null) {
            propertySet = new PropertySet(MonitoringAttributes.VENDOR_ID);
            addPropertySet(propertySet);
        }

        return propertySet;
    }
}