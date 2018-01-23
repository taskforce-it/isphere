/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibmi.contributions.extension.point;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCDriver;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.etools.iseries.perspective.model.AbstractISeriesProject;
import com.ibm.etools.iseries.perspective.model.util.ISeriesModelUtil;
import com.ibm.etools.iseries.rse.util.clprompter.CLPrompter;
import com.ibm.etools.iseries.services.qsys.api.IQSYSFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSLibrary;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.systems.editor.IRemoteResourceProperties;
import com.ibm.etools.systems.editor.RemoteResourcePropertiesFactoryManager;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.connection.rse.ConnectionProperties;
import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;
import biz.isphere.core.internal.Member;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.rse.Messages;
import biz.isphere.rse.clcommands.ICLPrompterImpl;
import biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.RSEMember;

/**
 * This class connects to the
 * <i>biz.isphere.core.ibmi.contributions.extension.point
 * .IIBMiHostContributions</i> extension point of the <i>iSphere Core
 * Plugin</i>.
 * 
 * @author Thomas Raddatz
 */
public class XRDiContributions implements IIBMiHostContributions {

    private Map<String, Connection> jdbcConnections;

    public XRDiContributions() {
        this.jdbcConnections = new HashMap<String, Connection>();
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
     * Executes a given command for a given connection.
     * 
     * @param connectionName - connection used for executing the command
     * @param command - command that is executed
     * @param rtnMessages - list of error messages or <code>null</code>
     * @return error message text on error or <code>null</code> on success
     */
    public String executeCommand(String connectionName, String command, List<AS400Message> rtnMessages) {

        try {

            IBMiConnection connection = IBMiConnection.getConnection(connectionName);
            if (connection == null) {
                return Messages.bind(Messages.Connection_A_not_found, connectionName);
            }

            AS400 system = connection.getAS400ToolboxObject();

            String escapeMessage = null;
            CommandCall commandCall = new CommandCall(system);
            if (!commandCall.run(command)) {
                AS400Message[] messageList = commandCall.getMessageList();
                if (messageList.length > 0) {
                    for (int idx = 0; idx < messageList.length; idx++) {
                        if (messageList[idx].getType() == AS400Message.ESCAPE) {
                            escapeMessage = messageList[idx].getHelp();
                        }
                        if (rtnMessages != null) {
                            rtnMessages.add(messageList[idx]);
                        }
                    }
                }

                if (escapeMessage == null) {
                    escapeMessage = Messages.bind(Messages.Failed_to_execute_command_A, command);
                }
            }

            return escapeMessage;

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to execute command: " + command + " for connection " + connectionName + " ***", e); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            return ExceptionHelper.getLocalizedMessage(e);
        }
    }

    /**
     * Returns whether a given library exists or not.
     * 
     * @param connectionName - connection that is checked for a given library
     * @param libraryName - library that is tested
     * @return <code>true</code>, when the library exists, else
     *         <code>false</code>.
     */
    public boolean checkLibrary(String connectionName, String libraryName) {

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);

        IQSYSLibrary library = null;
        try {
            library = connection.getLibrary(libraryName, null);
        } catch (Throwable e) {
            return false;
        }

        if (library == null) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether a given file exists or not.
     * 
     * @param connectionName - connection that is checked for a given library
     * @param libraryName - library that should contain the file
     * @param fileName - file that is tested
     * @return <code>true</code>, when the file exists, else <code>false</code>.
     */
    public boolean checkFile(String connectionName, String libraryName, String fileName) {

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);

        IQSYSFile file = null;
        try {
            file = connection.getFile(libraryName, fileName, null);
        } catch (Throwable e) {
            return false;
        }

        if (file == null) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether a given member exists or not.
     * 
     * @param connectionName - connection that is checked for a given library
     * @param libraryName - library that should contain the file
     * @param fileName - file that should contain the member
     * @param memberName - name of the member that is tested
     * @return <code>true</code>, when the library exists, else
     *         <code>false</code>.
     */
    public boolean checkMember(String connectionName, String libraryName, String fileName, String memberName) {

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);

        IQSYSMember member = null;
        try {
            member = connection.getMember(libraryName, fileName, memberName, null);
        } catch (Throwable e) {
            return false;
        }

        if (member == null) {
            return false;
        }

        return true;
    }

    /**
     * Returns the name of the iSphere library that is associated to a given
     * connection.
     * 
     * @param connectionName - name of the connection the name of the iSphere
     *        library is returned for
     * @return name of the iSphere library
     */
    public String getISphereLibrary(String connectionName) {

        ConnectionProperties connectionProperties = ConnectionManager.getInstance().getConnectionProperties(connectionName);
        if (connectionProperties != null && connectionProperties.useISphereLibraryName()) {
            return connectionProperties.getISphereLibraryName();
        }

        return Preferences.getInstance().getISphereLibrary(); // CHECKED
    }

    /**
     * Finds a matching system for a given host name.
     * 
     * @param hostName - Name of the a system is searched for
     * @return AS400
     */
    public AS400 findSystem(String hostName) {

        try {
            IBMiConnection[] connections = IBMiConnection.getConnections();
            for (IBMiConnection ibMiConnection : connections) {
                if (ibMiConnection.getHostName().equalsIgnoreCase(hostName)) {
                    return ibMiConnection.getAS400ToolboxObject();
                }
            }
        } catch (SystemMessageException e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
        }

        return null;
    }

    /**
     * Returns a system for a given connection name.
     * 
     * @parm connectionName - Name of the connection a system is returned for
     * @return AS400
     */
    public AS400 getSystem(String connectionName) {

        return getSystem(null, connectionName);
    }

    /**
     * Returns a system for a given profile and connection name.
     * 
     * @parm profile - Profile that is searched for the connection
     * @parm connectionName - Name of the connection a system is returned for
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
     * Returns an AS400 object for a given editor.
     * 
     * @param editor - that shows a remote file
     * @return AS400 object that is associated to editor
     */
    public AS400 getSystem(IEditorPart editor) {
        return getSystem(getConnectionName(editor));
    }

    /**
     * Returns the connection name of a given editor.
     * 
     * @param editor - that shows a remote file
     * @return name of the connection the file has been loaded from
     */
    public String getConnectionName(IEditorPart editor) {

        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput)editorInput).getFile();
            IRemoteResourceProperties properties = RemoteResourcePropertiesFactoryManager.getInstance().getRemoteResourceProperties(file);
            String subsystemStr = properties.getRemoteFileSubSystem();
            if (subsystemStr != null) {
                ISystemRegistry registry = RSECorePlugin.getTheSystemRegistry();
                if (registry != null) {
                    ISubSystem subsystem = registry.getSubSystem(subsystemStr);
                    if (subsystem != null) {
                        String connectionName = subsystem.getHost().getAliasName();
                        return connectionName;
                    }
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
    public String getConnectionName(String projectName) {

        AbstractISeriesProject iSeriesProject = findISeriesProject(projectName);
        if (iSeriesProject == null) {
            return null;
        }

        return iSeriesProject.getConnectionName();
    }

    /**
     * Returns the name of the associated library of a given i Project.
     * 
     * @param projectName - name of an i Project
     * @return name of the associated library
     */
    public String getLibraryName(String projectName) {

        AbstractISeriesProject iSeriesProject = findISeriesProject(projectName);
        if (iSeriesProject == null) {
            return null;
        }

        return iSeriesProject.getAssociatedLibraryName();
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

        List<String> connectionNamesList = new ArrayList<String>();

        IBMiConnection[] connections = IBMiConnection.getConnections();
        for (IBMiConnection connection : connections) {
            connectionNamesList.add(connection.getConnectionName());
        }

        String[] connectionNames = connectionNamesList.toArray(new String[connectionNamesList.size()]);
        Arrays.sort(connectionNames);

        return connectionNames;
    }

    /**
     * Returns a JDBC connection for a given connection name.
     * 
     * @param connectionName - Name of the connection, the JDBC connection is
     *        returned for
     * @return Connection
     */
    public Connection getJdbcConnection(String connectionName) {
        return getJdbcConnectionWithProperties(null, connectionName, null);
    }

    /**
     * Returns a JDBC connection for a given profile and connection name.
     * 
     * @param profile - Profile that is searched for the JDBC connection
     * @param connectionName - Name of the connection, the JDBC connection is
     *        returned for
     * @return Connection
     */
    public Connection getJdbcConnection(String profile, String connectionName) {
        return getJdbcConnectionWithProperties(profile, connectionName, null);
    }

    /**
     * Returns a JDBC connection for a given profile and connection name.
     * 
     * @param profile - Profile that is searched for the JDBC connection
     * @param connectionName - Name of the connection, the JDBC connection is
     *        returned for
     * @param properties - JDBC connection properties
     * @return Connection
     */
    private Connection getJdbcConnectionWithProperties(String profile, String connectionName, Properties properties) {

        IBMiConnection connection = getConnection(profile, connectionName);
        if (connection == null) {
            return null;
        }

        if (properties == null) {
            properties = new Properties();
            properties.put("prompt", "false");
            properties.put("big decimal", "false");
        }

        Connection jdbcConnection = null;

        if (isISphereJdbcConnectionManager()) {
            if (isKerberosAuthentication()) {
                jdbcConnection = getKerberosJdbcConnection(connection, properties);
            } else if (isISphereJdbcConnectionManager()) {
                jdbcConnection = getISphereJdbcConnection(connection, properties);
            }
        } else {
            jdbcConnection = getStandardIBMiJdbcConnection(connection, properties);
        }

        return jdbcConnection;
    }

    private boolean isISphereJdbcConnectionManager() {
        return Preferences.getInstance().isISphereJdbcConnectionManager();
    }

    private Connection getISphereJdbcConnection(IBMiConnection ibmiConnection, Properties properties) {
        return getKerberosJdbcConnection(ibmiConnection, properties);
    }

    private Connection getKerberosJdbcConnection(IBMiConnection ibmiConnection, Properties properties) {

        Connection jdbcConnection = getJdbcConnectionFromCache(ibmiConnection, properties);
        if (jdbcConnection == null) {
            jdbcConnection = produceJDBCConnection(ibmiConnection, properties);
        }

        return jdbcConnection;
    }

    private Connection getStandardIBMiJdbcConnection(IBMiConnection ibmiConnection, Properties properties) {

        Connection jdbcConnection = null;

        try {

            jdbcConnection = ibmiConnection.getJDBCConnection(null, false);

        } catch (Throwable e) {
            return null;
        }

        return jdbcConnection;
    }

    private Connection produceJDBCConnection(IBMiConnection ibmiConnection, Properties properties) {

        Connection jdbcConnection = null;
        AS400JDBCDriver as400JDBCDriver = null;

        try {

            try {

                as400JDBCDriver = (AS400JDBCDriver)DriverManager.getDriver("jdbc:as400");

            } catch (SQLException e) {

                as400JDBCDriver = new AS400JDBCDriver();
                DriverManager.registerDriver(as400JDBCDriver);

            }

            AS400 system = ibmiConnection.getAS400ToolboxObject();
            jdbcConnection = as400JDBCDriver.connect(system, properties, null);

            addConnectionToCache(ibmiConnection, properties, jdbcConnection);

        } catch (Throwable e) {
        }

        return jdbcConnection;
    }

    private Connection getJdbcConnectionFromCache(IBMiConnection ibmiConnection, Properties properties) {

        String connectionKey = getConnectionKey(ibmiConnection, properties);

        Connection jdbcConnection = jdbcConnections.get(connectionKey);
        if (jdbcConnection == null) {
            return null;
        }

        try {

            if (jdbcConnection.isClosed()) {
                jdbcConnection = null;
            } else if (!jdbcConnection.isValid(30)) {
                jdbcConnection.close();
                jdbcConnection = null;
            }

        } catch (SQLException e) {
            jdbcConnection = null;
        }

        if (jdbcConnection == null) {
            jdbcConnections.remove(connectionKey);
        }

        return jdbcConnection;
    }

    private void addConnectionToCache(IBMiConnection ibmiConnection, Properties properties, Connection jdbcConnection) {
        jdbcConnections.put(getConnectionKey(ibmiConnection, properties), jdbcConnection);
    }

    private String getConnectionKey(IBMiConnection ibmiConnection, Properties properties) {
        return ibmiConnection.getHostName() + "|" + propertiesAsString(properties); //$NON-NLS-1$
    }

    private String propertiesAsString(Properties properties) {

        StringBuilder buffer = new StringBuilder();

        for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey() instanceof String) {
                if (entry.getValue() instanceof String) {
                    buffer.append((String)entry.getKey());
                    buffer.append("="); //$NON-NLS-1$
                    buffer.append((String)entry.getValue());
                    buffer.append(";"); //$NON-NLS-1$
                }
            }
        }

        return buffer.toString();
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

        if (profile == null) {
            return IBMiConnection.getConnection(connectionName);
        }

        return IBMiConnection.getConnection(profile, connectionName);
    }

    /**
     * Returns an ICLPrompter for a given connection name.
     * 
     * @param connectionName - connection name to identify the connection
     * @return ICLPrompter
     */
    public ICLPrompter getCLPrompter(String connectionName) {

        IBMiConnection connection = getConnection(null, connectionName);
        if (connection == null) {
            return null;
        }

        CLPrompter prompter;
        try {
            prompter = new CLPrompter();
            prompter.setConnection(connection);
            return new ICLPrompterImpl(prompter);
        } catch (SystemMessageException e) {
            ISpherePlugin.logError("*** Could not create CLPrompter for connection '" + connectionName + "'", e);
        }

        return null;
    }

    public Member getMember(String connectionName, String libraryName, String fileName, String memberName) throws Exception {

        IBMiConnection connection = getConnection(null, connectionName);
        if (connection == null) {
            return null;
        }

        IQSYSMember member = connection.getMember(libraryName, fileName, memberName, null);
        if (member == null) {
            return null;
        }

        return new RSEMember(member);
    }

    public void compareSourceMembers(String connectionName, List<Member> members, boolean enableEditMode) throws Exception {

        CompareSourceMembersHandler handler = new CompareSourceMembersHandler();

        if (enableEditMode) {
            handler.handleSourceCompare(members.toArray(new Member[members.size()]));
        } else {
            handler.handleReadOnlySourceCompare(members.toArray(new Member[members.size()]));
        }
    }
}
