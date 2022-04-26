/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibmi.contributions.extension.point;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SecureAS400;
import com.ibm.etools.iseries.perspective.model.AbstractISeriesProject;
import com.ibm.etools.iseries.perspective.model.util.ISeriesModelUtil;
import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.rse.util.clprompter.CLPrompter;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteMember;
import com.ibm.etools.iseries.subsystems.qsys.objects.RemoteObjectContext;
import com.ibm.etools.systems.editor.IRemoteResourceProperties;
import com.ibm.etools.systems.editor.RemoteResourcePropertiesFactoryManager;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.connection.rse.ConnectionProperties;
import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;
import biz.isphere.core.internal.Member;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.rse.clcommands.ICLPrompterImpl;
import biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.RSEMember;

/**
 * This class connects to the
 * <i>biz.isphere.core.ibmi.contributions.extension.point
 * .IIBMiHostContributions</i> extension point of the <i>iSphere Core
 * Plugin</i>.
 * <p>
 * The format of the connection names is: <code>connection[:profile]</code>.
 * 
 * @author Thomas Raddatz
 */
public class XRDiContributions implements IIBMiHostContributions {

    private Map<String, JdbcConnectionManager> jdbcConnectionManagers;

    public XRDiContributions() {
        this.jdbcConnectionManagers = new HashMap<String, JdbcConnectionManager>();
    }

    /**
     * Returns <i>true</i> when the RSE sub-system has been initialized.
     * 
     * @return <i>true</i>, if RSE sub-system has been initialized, else
     *         <i>false</i>
     */
    public boolean isRseSubsystemInitialized() {

        try {
            RSECorePlugin.waitForInitCompletion();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * Returns <i>true</i> when Kerberos authentication is enabled on the
     * "Remote Systems - IBM i - Authentication" preference page for RDi 9.5+.
     * 
     * @return <i>true</i>, if Kerberos authentication is selected, else
     *         <i>false</i>
     */
    public boolean isKerberosAuthentication() {

        boolean isKerberosAuthentication = false;

        try {
            Class<?> kerberosPreferencePage = Class.forName("com.ibm.etools.iseries.connectorservice.ui.KerberosPreferencePage");
            if (kerberosPreferencePage != null) {
                Method methodIsKerberosChosen = kerberosPreferencePage.getMethod("isKerberosChosen"); //$NON-NLS-1$
                isKerberosAuthentication = (Boolean)methodIsKerberosChosen.invoke(null);
            }
        } catch (ClassNotFoundException e) {
            isKerberosAuthentication = false;
        } catch (Throwable e) {
            ISpherePlugin.logError("*** Error on calling method 'isKerberosAuthentication' ***", e); //$NON-NLS-1$
        }

        return isKerberosAuthentication;
    }

    /**
     * Returns <i>true</i> when the specified connection is known to the
     * application.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when known, else <i>false</i>
     */
    public boolean isAvailable(String qualifiedConnectionName) {

        if (getConnection(qualifiedConnectionName) != null) {
            return true;
        }

        return false;
    }

    /**
     * Returns <i>true</i> when the specified connection is in offline mode.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when offline, else <i>false</i>
     */
    public boolean isOffline(String qualifiedConnectionName) {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection == null || connection.isOffline()) {
            return true;
        }

        return false;
    }

    /**
     * Returns <i>true</i> when specified connection is connected.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when connected, else <i>false</i>
     */
    public boolean isConnected(String qualifiedConnectionName) {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection != null && connection.isConnected()) {
            return true;
        }

        return false;
    }

    /**
     * Connects the connection identified by a given connection name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when successfully connected, else <i>false</i>
     * @throws Exception
     */
    public boolean connect(String qualifiedConnectionName) throws Exception {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection != null) {
            if (!connection.isOffline() && !connection.isConnected()) {
                return connection.connect();
            }
        }

        return false;
    }

    /**
     * Changes the <i>offline</i> status of the specified connection.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     */
    public void setOffline(String qualifiedConnectionName, boolean offline) {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection != null) {
            if (!connection.isOffline()) {
                connection.getHost().setOffline(offline);
            }
        }
    }

    /**
     * Returns the name of the iSphere library that is associated to a given
     * connection.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection library is returned for
     * @return name of the iSphere library
     */
    public String getISphereLibrary(String qualifiedConnectionName) {

        ConnectionProperties connectionProperties = ConnectionManager.getInstance().getConnectionProperties(qualifiedConnectionName);
        if (connectionProperties != null && connectionProperties.useISphereLibraryName()) {
            return connectionProperties.getISphereLibraryName();
        }

        return Preferences.getInstance().getISphereLibrary(); // CHECKED
    }

    /**
     * Returns the system (AS400) identified by a given connection name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return AS400
     */
    public AS400 getSystem(String qualifiedConnectionName) {

        if (qualifiedConnectionName == null || qualifiedConnectionName.trim().length() == 0) {
            return null;
        }

        QualifiedConnectionName tQualifiedConnectionName = new QualifiedConnectionName(qualifiedConnectionName);

        return getSystem(tQualifiedConnectionName.getProfileName(), tQualifiedConnectionName.getConnectionName());
    }

    /**
     * Returns a system for a given profile and connection name.
     * 
     * @parm profile - Profile that is searched for the connection
     * @parm connectionName - Name that identifies the connection
     * @return AS400
     */
    public AS400 getSystem(String profile, String connectionName) {

        IBMiConnection connection = getConnection(profile, connectionName);
        if (connection == null) {
            return null;
        }

        try {
            return connection.getAS400ToolboxObject();
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Returns the connection name of a given editor.
     * 
     * @param file - remote file downloaded to the workspace
     * @return name of the connection the file has been loaded from
     */
    public String getConnectionName(IFile file) {

        IRemoteResourceProperties properties = RemoteResourcePropertiesFactoryManager.getInstance().getRemoteResourceProperties(file);
        String subsystemStr = properties.getRemoteFileSubSystem();
        if (subsystemStr != null) {
            ISystemRegistry registry = RSECorePlugin.getTheSystemRegistry();
            if (registry != null) {
                ISubSystem subsystem = registry.getSubSystem(subsystemStr);
                if (subsystem != null) {
                    IHost host = subsystem.getHost();
                    return produceQualifiedConnectionName(host);
                }
            }
        }

        return null;
    }

    /**
     * Returns the connection name of a given i Project.
     * 
     * @param projectName - name of an i Project
     * @return name of the connection the file has been loaded from
     */
    public String getConnectionNameOfIProject(String projectName) {

        AbstractISeriesProject iSeriesProject = findISeriesProject(projectName);
        if (iSeriesProject == null) {
            return null;
        }

        return produceQualifiedConnectionName(iSeriesProject);
    }

    /**
     * Returns the name of the associated library of a given i Project.
     * 
     * @param projectName - name of an i Project
     * @return name of the associated library
     */
    public String getLibraryNameOfIProject(String projectName) {

        AbstractISeriesProject iSeriesProject = findISeriesProject(projectName);
        if (iSeriesProject == null) {
            return null;
        }

        return iSeriesProject.getAssociatedLibraryName();
    }

    /**
     * Returns the qualified connection name of a given TCP/IP Address.
     * 
     * @param projectName - TCP/IP address
     * @param isConnected - specifies whether the connection must be connected
     * @return name of the connection
     */
    public String getConnectionNameByIPAddr(String tcpIpAddr, boolean isConnected) {

        if (StringHelper.isNullOrEmpty(tcpIpAddr)) {
            return null;
        }

        try {

            IBMiConnection[] connections = IBMiConnection.getConnections();
            for (IBMiConnection ibMiConnection : connections) {
                if (!isConnected || ibMiConnection.isConnected()) {
                    InetAddress inetAddress = InetAddress.getByName(tcpIpAddr);
                    InetAddress connTcpIpAddr = InetAddress.getByName(ibMiConnection.getHostName());
                    if (Arrays.equals(inetAddress.getAddress(), connTcpIpAddr.getAddress())) {
                        return produceQualifiedConnectionName(ibMiConnection);
                    }
                }
            }

        } catch (Exception e) {
        }

        if (isConnected) {
            return getConnectionNameByIPAddr(tcpIpAddr, false);
        } else {
            return null;
        }
    }

    private AbstractISeriesProject findISeriesProject(String projectName) {

        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject project : projects) {
            if (project.getName().equals(projectName)) {
                AbstractISeriesProject iSeriesProject = ((AbstractISeriesProject)ISeriesModelUtil.findISeriesResource(project));
                return iSeriesProject;
            }
        }

        return null;
    }

    /**
     * Returns a list of configured connections.
     * 
     * @return names of configured connections
     */
    public String[] getConnectionNames() {

        List<QualifiedConnectionName> connectionNamesList = new ArrayList<QualifiedConnectionName>();

        IBMiConnection[] connections = IBMiConnection.getConnections();
        for (IBMiConnection connection : connections) {
            connectionNamesList.add(new QualifiedConnectionName(connection));
        }

        Collections.sort(connectionNamesList);

        String[] connectionNames = new String[connectionNamesList.size()];
        for (int i = 0; i < connectionNames.length; i++) {
            connectionNames[i] = connectionNamesList.get(i).getQualifiedName();
        }

        return connectionNames;
    }

    /**
     * Returns a JDBC connection for a given connection name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return Connection
     */
    public Connection getJdbcConnection(String qualifiedConnectionName) {

        if (qualifiedConnectionName == null || qualifiedConnectionName.trim().length() == 0) {
            return null;
        }

        QualifiedConnectionName tQualifiedConnectionName = new QualifiedConnectionName(qualifiedConnectionName);

        return getJdbcConnectionWithProperties(tQualifiedConnectionName.getProfileName(), tQualifiedConnectionName.getConnectionName(), null);
    }

    /**
     * Returns a JDBC connection for a given profile and connection name.
     * 
     * @parm profile - Profile that is searched for the connection
     * @parm connectionName - Name that identifies the connection
     * @return Connection
     */
    public Connection getJdbcConnection(String profile, String connectionName) {
        return getJdbcConnectionWithProperties(profile, connectionName, null);
    }

    /**
     * Returns a JDBC connection for a given profile and connection name.
     * 
     * @parm profile - Profile that is searched for the connection
     * @parm connectionName - Name that identifies the connection
     * @param properties - JDBC connection properties
     * @return Connection
     */
    private Connection getJdbcConnectionWithProperties(String profile, String connectionName, Properties properties) {

        IBMiConnection connection = getConnection(profile, connectionName);
        if (connection == null) {
            return null;
        }

        JdbcConnectionManager manager = getJdbcConnectionManager(connection);
        if (manager == null) {
            manager = registerJdbcConnectionManager(connection);
        }

        if (properties == null) {
            properties = new Properties();
            properties.put("prompt", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.put("big decimal", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            try {
                if (connection.getAS400ToolboxObject() instanceof SecureAS400) {
                    properties.put("secure", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } catch (SystemMessageException e) {
            }
        }

        Connection jdbcConnection = manager.getJdbcConnection(properties);

        return jdbcConnection;
    }

    private JdbcConnectionManager getJdbcConnectionManager(IBMiConnection connection) {
        return jdbcConnectionManagers.get(produceQualifiedConnectionName(connection));
    }

    private JdbcConnectionManager registerJdbcConnectionManager(IBMiConnection connection) {

        JdbcConnectionManager jdbcConnectionManager = new JdbcConnectionManager(connection);
        jdbcConnectionManagers.put(produceQualifiedConnectionName(connection), jdbcConnectionManager);

        return jdbcConnectionManager;
    }

    /**
     * Internal method that returns a connection for a given connection name.
     * 
     * @parm connectionName - Name of the connection a system is returned for
     * @return IBMiConnection
     */
    private IBMiConnection getConnection(String qualifiedConnectionName) {

        if (qualifiedConnectionName == null || qualifiedConnectionName.trim().length() == 0) {
            return null;
        }

        QualifiedConnectionName tQualifiedConnectionName = new QualifiedConnectionName(qualifiedConnectionName);

        return getConnection(tQualifiedConnectionName.getProfileName(), tQualifiedConnectionName.getConnectionName());
    }

    /**
     * Internal method that returns a connection for a given profile and
     * connection name. The profile might be null.
     * 
     * @parm profile - Profile that is searched for the connection
     * @parm connectionName - Name of the connection a system is returned for
     * @return IBMiConnection
     */
    private IBMiConnection getConnection(String profile, String connectionName) {
        return ConnectionManager.getIBMiConnection(profile, connectionName);
    }

    /**
     * Returns an ICLPrompter for a given connection name.
     * 
     * @param qualifiedConnectionName - connection name to identify the
     *        connection
     * @return ICLPrompter
     */
    public ICLPrompter getCLPrompter(String qualifiedConnectionName) {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection == null) {
            return null;
        }

        CLPrompter prompter;
        try {
            prompter = new CLPrompter();
            prompter.setConnection(connection);
            return new ICLPrompterImpl(prompter);
        } catch (SystemMessageException e) {
            ISpherePlugin.logError("*** Could not create CLPrompter for connection '" + qualifiedConnectionName + "'", e);
        }

        return null;
    }

    /**
     * Returns the file member identified by library, file and member name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param libraryName - name of the library where the file is stored
     * @param fileName - name of the file that contains the member
     * @param memberName - name that identifies the member
     * @throws Exception
     */
    public Member getMember(String qualifiedConnectionName, String libraryName, String fileName, String memberName) throws Exception {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection == null) {
            return null;
        }

        IQSYSMember member = connection.getMember(libraryName, fileName, memberName, null);
        if (member == null) {
            return null;
        }

        return new RSEMember(member);
    }

    /**
     * Opens the iSphere compare editor for the given members.
     * <p>
     * The available options are:
     * <p>
     * <b>Empty member list</b> <br>
     * Opens the compare dialog to let the user specify the members that are
     * compares.
     * <p>
     * <b>One member</b> <br>
     * Opens the compare dialog with that member set as the left (editable)
     * member. The right member is initialized with the properties of the left
     * member.
     * <p>
     * <b>Two members</b> <br>
     * Opens the compare dialog with the first member set as the left (editable)
     * and the second member set as the right member.
     * <p>
     * <b>More than 2 members</b> <br>
     * Opens the compare dialog to let the user specify the source file that
     * contains the members, which are compared one by one with the selected
     * members.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param members - members that are compared
     * @param enableEditMode - specifies whether edit mode is enabled
     * @throws Exception
     */
    public void compareSourceMembers(String qualifiedConnectionName, List<Member> members, boolean enableEditMode) throws Exception {

        CompareSourceMembersHandler handler = new CompareSourceMembersHandler();

        if (enableEditMode) {
            handler.handleSourceCompare(members.toArray(new Member[members.size()]));
        } else {
            handler.handleReadOnlySourceCompare(members.toArray(new Member[members.size()]));
        }
    }

    /**
     * Returns the local resource of a given remote member.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param libraryName - name of the library where the file is stored
     * @param fileName - name of the file that contains the member
     * @param memberName - name that identifies the member
     * @param srcType - type of the member
     * @return local member resource
     */
    public IFile getLocalResource(String qualifiedConnectionName, String libraryName, String fileName, String memberName, String srcType)
        throws Exception {

        IBMiConnection connection = getConnection(qualifiedConnectionName);
        if (connection == null) {
            return null;
        }

        QSYSRemoteMember qsysMember = new QSYSRemoteMember();
        qsysMember.setLibrary(libraryName);
        qsysMember.setFile(fileName);
        qsysMember.setName(memberName);
        qsysMember.setType(srcType);
        RemoteObjectContext remoteContext = new RemoteObjectContext(connection.getQSYSObjectSubSystem());
        qsysMember.setRemoteObjectContext(remoteContext);
        QSYSEditableRemoteSourceFileMember editableMember = new QSYSEditableRemoteSourceFileMember(qsysMember);

        return editableMember.getLocalResource();
    }

    private String produceQualifiedConnectionName(AbstractISeriesProject iSeriesProject) {
        return produceQualifiedConnectionName(iSeriesProject.getConnectionProfileName(), iSeriesProject.getConnectionName());
    }

    private String produceQualifiedConnectionName(IBMiConnection connection) {
        return produceQualifiedConnectionName(connection.getProfileName(), connection.getConnectionName());
    }

    private String produceQualifiedConnectionName(IHost host) {
        return produceQualifiedConnectionName(host.getSystemProfile().getName(), host.getAliasName());
    }

    private String produceQualifiedConnectionName(String profileName, String connectionName) {
        return new QualifiedConnectionName(profileName, connectionName).getQualifiedName();
    }
}
