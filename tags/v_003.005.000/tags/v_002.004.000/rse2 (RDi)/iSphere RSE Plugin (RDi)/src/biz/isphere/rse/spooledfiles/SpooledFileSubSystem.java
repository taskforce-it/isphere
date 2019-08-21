/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessage;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.RSEUIPlugin;

import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileBaseSubSystem;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.subsystems.qsys.IISeriesSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.commands.QSYSCommandSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class SpooledFileSubSystem extends SubSystem implements IISeriesSubSystem, ISpooledFileSubSystem {

    private SpooledFileBaseSubSystem base = new SpooledFileBaseSubSystem();

    public SpooledFileSubSystem(IHost host, IConnectorService connectorService) {
        super(host, connectorService);
    }

    @Override
    protected Object[] internalResolveFilterString(String filterString, IProgressMonitor monitor) throws InvocationTargetException,
        InterruptedException {
        SpooledFileResource[] spooledFileResources;
        try {
            SpooledFile[] spooledFiles = base.internalResolveFilterString(RSEUIPlugin.getActiveWorkbenchShell(), getToolboxAS400Object(),
                getToolboxJDBCConnection(), filterString);
            spooledFileResources = new SpooledFileResource[spooledFiles.length];
            for (int i = 0; i < spooledFileResources.length; i++) {
                spooledFileResources[i] = new SpooledFileResource(this);
                spooledFileResources[i].setSpooledFile(spooledFiles[i]);
            }
        } catch (Exception e) {
            handleError(e);
            SystemMessage msg = RSEUIPlugin.getPluginMessage("RSEO1012");
            msg.makeSubstitution(e.getMessage());
            SystemMessageObject msgObj = new SystemMessageObject(msg, 0, null);
            return new Object[] { msgObj };
        }
        return spooledFileResources;
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
            ISubSystem iSubSystem = iSubSystems[ssIndx];
            if ((iSubSystem instanceof QSYSCommandSubSystem)) return (QSYSCommandSubSystem)iSubSystem;
        }

        return null;
    }

    public QSYSObjectSubSystem getCommandExecutionProperties() {
        return getObjectSubSystem();
    }

    public QSYSObjectSubSystem getObjectSubSystem() {
        return IBMiConnection.getConnection(getHost()).getQSYSObjectSubSystem();
    }

    public AS400 getToolboxAS400Object() {
        AS400 as400 = null;
        try {
            as400 = IBMiConnection.getConnection(getHost()).getAS400ToolboxObject();
        } catch (SystemMessageException e) {
        }
        return as400;
    }

    public Connection getToolboxJDBCConnection() {
        Connection jdbcConnection = null;
        try {
            jdbcConnection = IBMiConnection.getConnection(getHost()).getJDBCConnection(null, false);
        } catch (SQLException e) {
        }
        return jdbcConnection;
    }

    /*
     * public void setShell(Shell shell) { this.shell = shell; } public Shell
     * getShell() { if (this.shell != null) { return this.shell; } return
     * super.getShell(); }
     */

    private void handleError(Exception e) {
    }

}
