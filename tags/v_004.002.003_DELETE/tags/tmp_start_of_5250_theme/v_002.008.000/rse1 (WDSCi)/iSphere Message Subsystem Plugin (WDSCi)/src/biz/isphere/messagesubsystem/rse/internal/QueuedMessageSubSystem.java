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

import biz.isphere.messagesubsystem.rse.IQueuedMessageSubsystem;
import biz.isphere.messagesubsystem.rse.MonitoringAttributes;
import biz.isphere.messagesubsystem.rse.QueuedMessageFactory;
import biz.isphere.messagesubsystem.rse.QueuedMessageFilter;

import com.ibm.as400.access.AS400;
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

    private CommunicationsListener communicationsListener;
    private MonitoringAttributes monitoringAttributes;

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

            AS400 as400 = getToolboxAS400Object();
            QueuedMessageFactory factory = new QueuedMessageFactory(as400);
            QueuedMessage[] queuedMessages = factory.getQueuedMessages(queuedMessageFilter);
            queuedMessageResources = new QueuedMessageResource[queuedMessages.length];

            for (int i = 0; i < queuedMessageResources.length; i++) {
                queuedMessageResources[i] = new QueuedMessageResource(this);
                queuedMessageResources[i].setQueuedMessage(queuedMessages[i]);
            }
        } catch (Exception e) {
            SystemMessage msg = SystemPlugin.getPluginMessage(ISystemMessages.MSG_GENERIC_E);
            msg.makeSubstitution(e.getMessage());
            SystemMessageObject msgObj = new SystemMessageObject(msg, ISystemMessageObject.MSGTYPE_ERROR, null);
            return new Object[] { msgObj };
        }

        return queuedMessageResources;
    }

    @Override
    protected Object[] internalResolveFilterString(IProgressMonitor monitor, Object parent, String filterString) throws InvocationTargetException,
        InterruptedException {

        return internalResolveFilterString(monitor, filterString);
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

    public IISeriesSubSystemCommandExecutionProperties getCommandExecutionProperties() {
        return getObjectSubSystem();
    }

    public FileSubSystem getObjectSubSystem() {
        return ISeriesConnection.getConnection(getSystemConnection()).getISeriesFileSubSystem();
    }

    public AS400 getToolboxAS400Object() {
        ISeriesSystemToolbox system = (ISeriesSystemToolbox)getSystem();
        return system.getAS400Object();
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
            communicationsListener.startMonitoring();
        } else {
            communicationsListener.stopMonitoring();
        }
    }

    /*
     * Start of RDi/WDSCi specific methods.
     */

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

    public String getVendorAttribute(String key) {
        return super.getVendorAttribute(MonitoringAttributes.VENDOR_ID, key);
    }

    public void setVendorAttribute(String key, String value) {
        super.setVendorAttribute(MonitoringAttributes.VENDOR_ID, key, value);
    }
}