/*******************************************************************************
 * Copyright (c) 2012-2018 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.sql.Connection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.spooledfiles.ISpooledFileSubSystem;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileBaseSubSystem;
import biz.isphere.core.spooledfiles.SpooledFileSubSystemAttributes;
import biz.isphere.core.spooledfiles.SpooledFileTextDecoration;
import biz.isphere.rse.connection.ConnectionManager;

import com.ibm.as400.access.AS400;
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
import com.ibm.etools.systems.core.ui.actions.SystemRefreshAction;
import com.ibm.etools.systems.dftsubsystem.impl.DefaultSubSystemImpl;
import com.ibm.etools.systems.model.ISystemMessageObject;
import com.ibm.etools.systems.model.SystemConnection;
import com.ibm.etools.systems.model.SystemRegistry;
import com.ibm.etools.systems.model.impl.SystemMessageObject;
import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractSystemManager;

public class SpooledFileSubSystem extends DefaultSubSystemImpl implements IISeriesSubSystem, ISpooledFileSubSystem {

    public static final String ID = "biz.isphere.core.spooledfiles.subsystems.factory";
    
    private SpooledFileBaseSubSystem base = new SpooledFileBaseSubSystem();
    private SpooledFileSubSystemAttributes spooledFileSubsystemAttributes;

    public SpooledFileSubSystem() {
        super();

        spooledFileSubsystemAttributes = new SpooledFileSubSystemAttributes(this);
    }

    @Override
    public AbstractSystemManager getSystemManager() {
        return ISeriesSystemManager.getTheISeriesSystemManager();
    }

    @Override
    protected Object[] internalResolveFilterString(IProgressMonitor monitor, String filterString) throws java.lang.reflect.InvocationTargetException,
        java.lang.InterruptedException {
        SpooledFileResource[] spooledFileResources;
        try {
            SpooledFile[] spooledFiles = base.internalResolveFilterString(SystemPlugin.getActiveWorkbenchShell(), getConnectionName(),
                getToolboxJDBCConnection(), filterString);
            spooledFileResources = new SpooledFileResource[spooledFiles.length];
            for (int i = 0; i < spooledFileResources.length; i++) {
                spooledFileResources[i] = new SpooledFileResource(this);
                spooledFileResources[i].setSpooledFile(spooledFiles[i]);
            }
        } catch (Exception e) {
            handleError(e);
            SystemMessage msg = SystemPlugin.getPluginMessage(ISystemMessages.MSG_GENERIC_E);
            msg.makeSubstitution(ExceptionHelper.getLocalizedMessage(e));
            SystemMessageObject msgObj = new SystemMessageObject(msg, ISystemMessageObject.MSGTYPE_ERROR, null);
            return new Object[] { msgObj };
        }
        return spooledFileResources;
    }

    @Override
    protected Object[] internalResolveFilterString(IProgressMonitor monitor, Object parent, String filterString)
        throws java.lang.reflect.InvocationTargetException, java.lang.InterruptedException {
        return internalResolveFilterString(monitor, filterString);
    }

    public CmdSubSystem getCmdSubSystem() {
        SystemConnection sc = getSystemConnection();
        SystemRegistry registry = SystemPlugin.getTheSystemRegistry();
        SubSystem[] subsystems = registry.getSubSystems(sc);
        SubSystem subsystem;
        for (int ssIndx = 0; ssIndx < subsystems.length; ssIndx++) {
            subsystem = subsystems[ssIndx];
            if (subsystem instanceof CmdSubSystemImpl) return (CmdSubSystemImpl)subsystem;
        }
        return null;
    }

    public IISeriesSubSystemCommandExecutionProperties getCommandExecutionProperties() {
        return getObjectSubSystem();
    }

    public ISeriesSystemDataStore getISeriesSystem() {
        return (ISeriesSystemDataStore)getSystem();
    }

    public FileSubSystem getObjectSubSystem() {
        return ISeriesConnection.getConnection(getSystemConnection()).getISeriesFileSubSystem();
    }

    public String getConnectionName() {
        return ConnectionManager.getConnectionName(getSystemConnection());
    }

    public AS400 getToolboxAS400Object() {
        ISeriesSystemToolbox system = (ISeriesSystemToolbox)getSystem();
        return system.getAS400Object();
    }

    public Connection getToolboxJDBCConnection() {

        Connection jdbcConnection = null;

        SystemConnection host = null;
        String connectionName = null;

        try {
            host = getSystemConnection();
            connectionName = host.getAliasName();
            jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
        } catch (Throwable e) {
            ISpherePlugin.logError(NLS.bind("*** Could not get JDBC connection for system {0} ***", connectionName), e);
        }
        return jdbcConnection;
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    @Override
    public Shell getShell() {
        if (shell != null)
            return shell;
        else
            return super.getShell();
    }

    private void handleError(Exception e) {
        ISpherePlugin.logError("*** Could not retrieve list of spooled files from host ***", e);
    }

    private void refreshFilter() {
        new SystemRefreshAction(getShell()).run();
    }

    public SpooledFileTextDecoration getDecorationTextStyle() {
        return spooledFileSubsystemAttributes.getDecorationTextStyle();
    }

    public void setDecorationTextStyle(SpooledFileTextDecoration decorationStyle) {
        spooledFileSubsystemAttributes.setDecorationTextStyle(decorationStyle);
        refreshFilter();
    }

    public String getVendorAttribute(String key) {
        return super.getVendorAttribute(SpooledFileSubSystemAttributes.VENDOR_ID, key);
    }

    public void setVendorAttribute(String key, String value) {
        super.setVendorAttribute(SpooledFileSubSystemAttributes.VENDOR_ID, key, value);
    }

    public boolean commit() {
        // Not needed for WDSCi.
        return true;
    }
}
