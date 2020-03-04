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
import biz.isphere.messagesubsystem.rse.IQueuedMessageSubsystem;
import biz.isphere.messagesubsystem.rse.MonitoringAttributes;
import biz.isphere.messagesubsystem.rse.QueuedMessageFactory;
import biz.isphere.messagesubsystem.rse.QueuedMessageFilter;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QueuedMessage;
import com.ibm.etools.iseries.subsystems.qsys.IISeriesSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.commands.QSYSCommandSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class QueuedMessageSubSystem extends SubSystem implements IISeriesSubSystem, IQueuedMessageSubsystem {

    private CommunicationsListener communicationsListener;
    private MonitoringAttributes monitoringAttributes;

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

            AS400 as400 = getToolboxAS400Object();
            QueuedMessageFactory factory = new QueuedMessageFactory(as400);
            QueuedMessage[] queuedMessages = factory.getQueuedMessages(queuedMessageFilter);
            queuedMessageResources = new QueuedMessageResource[queuedMessages.length];

            for (int i = 0; i < queuedMessageResources.length; i++) {
                queuedMessageResources[i] = new QueuedMessageResource(this);
                queuedMessageResources[i].setQueuedMessage(queuedMessages[i]);
            }
        } catch (Exception e) {
            SystemMessage msg = RSEUIPlugin.getPluginMessage("RSEO1012");
            msg.makeSubstitution(e.getMessage());
            SystemMessageObject msgObj = new SystemMessageObject(msg, 0, null);
            return new Object[] { msgObj };
        }

        return queuedMessageResources;
    }

    @Override
    protected Object[] internalResolveFilterString(Object parent, String filterString, IProgressMonitor monitor) throws InvocationTargetException,
        InterruptedException {

        return internalResolveFilterString(filterString, monitor);
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

    public QSYSObjectSubSystem getCommandExecutionProperties() {
        return IBMiConnection.getConnection(getHost()).getQSYSObjectSubSystem();
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

    private AS400 getToolboxAS400Object() {

        try {
            return IBMiConnection.getConnection(getHost()).getAS400ToolboxObject();
        } catch (SystemMessageException e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return null;
        }
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