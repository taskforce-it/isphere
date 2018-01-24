/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.IProperty;
import org.eclipse.rse.core.model.IPropertySet;
import org.eclipse.rse.core.model.PropertySet;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessage;
import org.eclipse.rse.ui.RSEUIPlugin;
import org.eclipse.rse.ui.actions.SystemRefreshAction;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.spooledfiles.ISpooledFileSubSystem;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileBaseSubSystem;
import biz.isphere.core.spooledfiles.SpooledFileSubSystemAttributes;
import biz.isphere.core.spooledfiles.SpooledFileTextDecoration;
import biz.isphere.rse.connection.ConnectionManager;

import com.ibm.etools.iseries.subsystems.qsys.IISeriesSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.commands.QSYSCommandSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class SpooledFileSubSystem extends SubSystem implements IISeriesSubSystem, ISpooledFileSubSystem {

    private SpooledFileBaseSubSystem base = new SpooledFileBaseSubSystem();
    private SpooledFileSubSystemAttributes spooledFileSubsystemAttributes;

    public SpooledFileSubSystem(IHost host, IConnectorService connectorService) {
        super(host, connectorService);

        spooledFileSubsystemAttributes = new SpooledFileSubSystemAttributes(this);
    }

    @Override
    protected Object[] internalResolveFilterString(String filterString, IProgressMonitor monitor) throws InvocationTargetException,
        InterruptedException {
        SpooledFileResource[] spooledFileResources;
        try {
            SpooledFile[] spooledFiles = base.internalResolveFilterString(RSEUIPlugin.getActiveWorkbenchShell(), getConnectionName(),
                getToolboxJDBCConnection(), filterString);
            spooledFileResources = new SpooledFileResource[spooledFiles.length];
            for (int i = 0; i < spooledFileResources.length; i++) {
                spooledFileResources[i] = new SpooledFileResource(this);
                spooledFileResources[i].setSpooledFile(spooledFiles[i]);
            }
        } catch (Exception e) {
            handleError(e);
            SystemMessage msg = RSEUIPlugin.getPluginMessage("RSEO1012"); //$NON-NLS-1$
            msg.makeSubstitution(ExceptionHelper.getLocalizedMessage(e));
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

    public String getConnectionName() {
        return ConnectionManager.getConnectionName(getHost());
    }

    public Connection getToolboxJDBCConnection() {

        Connection jdbcConnection = null;

        IHost host = null;
        String connectionName = null;

        try {
            host = getHost();
            connectionName = host.getAliasName();
            jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
        } catch (Throwable e) {
            ISpherePlugin.logError(NLS.bind("*** Could not get JDBC connection for system {0} ***", connectionName), e); //$NON-NLS-1$
        }
        return jdbcConnection;
    }

    /*
     * public void setShell(Shell shell) { this.shell = shell; } public Shell
     * getShell() { if (this.shell != null) { return this.shell; } return
     * super.getShell(); }
     */

    private void handleError(Exception e) {
        ISpherePlugin.logError("*** Could not retrieve list of spooled files from host ***", e); //$NON-NLS-1$
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

        IPropertySet propertySet = getPropertySet(SpooledFileSubSystemAttributes.VENDOR_ID);
        if (propertySet == null) {
            propertySet = new PropertySet(SpooledFileSubSystemAttributes.VENDOR_ID);
            addPropertySet(propertySet);
        }

        return propertySet;
    }
}
