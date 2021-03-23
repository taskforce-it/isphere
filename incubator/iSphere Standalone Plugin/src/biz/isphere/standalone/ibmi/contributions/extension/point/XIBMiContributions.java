/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.standalone.ibmi.contributions.extension.point;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.MemberDescription;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.ObjectList;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;
import biz.isphere.core.internal.Member;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.standalone.connections.RemoteConnection;
import biz.isphere.standalone.connections.RemoteConnections;

/**
 * This class connects to the
 * <i>biz.isphere.core.ibmi.contributions.extension.point
 * .IIBMiHostContributions</i> extension point of the <i>iSphere Core
 * Plugin</i>.
 * 
 * @author Thomas Raddatz
 */
public class XIBMiContributions implements IIBMiHostContributions {

    private RemoteConnections remoteConnections;

    public XIBMiContributions() {
        this.remoteConnections = RemoteConnections.getInstance();
    }

    /**
     * Returns <i>true</i> when the RSE sub-system has been initialized.
     * 
     * @return <i>true</i>, if RSE sub-system has been initialized, else
     *         <i>false</i>
     */
    public boolean isRseSubsystemInitialized(String connectionName) {
        return true;
    }

    /**
     * Returns <i>true</i> when Kerberos authentication is enabled on the
     * "Remote RemoteConnections - IBM i - Authentication" preference page for
     * RDi 9.5+.
     * 
     * @return <i>true</i>, if Kerberos authentication is selected, else
     *         <i>false</i>
     */
    public boolean isKerberosAuthentication() {
        return false;
    }

    /**
     * Returns <i>true</i> when the specified connection is known to the
     * application.
     * 
     * @return <i>true</i>, when known, else <i>false</i>
     */
    public boolean isAvailable(String connectionName) {
        return remoteConnections.hasConnection(connectionName);
    }

    /**
     * Returns <i>true</i> when the specified connection is in offline mode.
     * 
     * @return <i>true</i>, when offline, else <i>false</i>
     */
    public boolean isOffline(String connectionName) {

        RemoteConnection connection = remoteConnections.getConnection(connectionName);
        if (connection == null) {
            return true;
        }

        return connection.isOffline();
    }

    /**
     * Returns <i>true</i> when specified connection is connected.
     * 
     * @return <i>true</i>, when connected, else <i>false</i>
     */
    public boolean isConnected(String connectionName) {

        RemoteConnection connection = remoteConnections.getConnection(connectionName);
        if (connection == null) {
            return false;
        }

        return connection.isConnected();
    }

    /**
     * Connects the specified connection.
     * 
     * @return <i>true</i>, when successfully connected, else <i>false</i>
     */
    public boolean connect(String connectionName) throws Exception {

        RemoteConnection connection = remoteConnections.getConnection(connectionName);
        if (connection == null) {
            return false;
        }

        if (connection.isOffline()) {
            return false;
        }

        return connection.connect();
    }

    /**
     * Changes the 'offline' status of the specified connection.
     */
    public void setOffline(String connectionName, boolean offline) {

        RemoteConnection connection = remoteConnections.getConnection(connectionName);
        if (connection == null) {
            return;
        }

        connection.setOffline(offline);
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

            RemoteConnection connection = remoteConnections.getConnection(connectionName);
            if (connection == null) {
                return "ERROR: Connection not found";
            }

            if (connection.isOffline()) {
                return "ERROR: Connection is offline.";
            }

            String escapeMessage = null;
            CommandCall commandCall = new CommandCall(connection.getAS400ToolboxObject());
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
                    escapeMessage = "Failed to execute command: " + command;
                }
            }

            return escapeMessage;

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to execute command: " + command + " for connection " + connectionName + " ***", e); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            return e.getLocalizedMessage();
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
        return checkObject(connectionName, "QSYS", libraryName, "*LIB", null);
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
        return checkObject(connectionName, libraryName, fileName, "*FILE", null);
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
        return checkObject(connectionName, libraryName, fileName, "*FILE", memberName);
    }

    private boolean checkObject(String connectionName, String libraryName, String objectName, String objectType, String memberName) {

        RemoteConnection connection = remoteConnections.getConnection(connectionName);
        if (connection == null) {
            return false;
        }

        AS400 system = connection.getAS400ToolboxObject();
        if (system == null) {
            return false;
        }

        ObjectList list = null;

        try {

            if ("*FILE".equals(objectType) && memberName != null) {

                MemberDescription memberDescription = new MemberDescription(system,
                    new QSYSObjectPathName(libraryName, objectName, memberName, "MBR"));
                Object library = memberDescription.getValue(MemberDescription.LIBRARY_NAME);
                if (library == null) {
                    return false;
                }

            } else {

                list = new ObjectList(system, libraryName, objectName, objectType);
                list.load();

                ObjectDescription[] library = list.getObjects(0, 1);
                if (library == null || library.length == 0) {
                    return false;
                }

            }

            return true;

        } catch (Exception e) {
            if (e.getMessage() == null || !e.getMessage().startsWith("CPF9815")) {
                ISpherePlugin.logError("*** Failed to check object ***", e); //$NON-NLS-1$
            }
        } finally {
            if (list != null) {
                try {
                    list.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
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
        return Preferences.getInstance().getISphereLibrary(); // CHECKED
    }

    /**
     * Finds a matching system for a given host name.
     * 
     * @param hostName - Name of the a system is searched for
     * @return AS400
     */
    public AS400 findSystem(String hostName) {
        return null;
    }

    /**
     * Returns a system for a given connection name.
     * 
     * @parm connectionName - Name of the connection a system is returned for
     * @return AS400
     */
    public AS400 getSystem(String connectionName) {

        RemoteConnection connection = remoteConnections.getConnection(connectionName);
        if (connection == null) {
            return null;
        }

        return connection.getAS400ToolboxObject();
    }

    /**
     * Returns a system for a given profile and connection name.
     * 
     * @parm profile - Profile that is searched for the connection
     * @parm connectionName - Name of the connection a system is returned for
     * @return AS400
     */
    public AS400 getSystem(String profile, String connectionName) {
        return getSystem(connectionName);
    }

    /**
     * Returns an AS400 object for a given editor.
     * 
     * @param editor - that shows a remote file
     * @return AS400 object that is associated to editor
     */
    public AS400 getSystem(IEditorPart editor) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the connection name of a given editor.
     * 
     * @param editor - that shows a remote file
     * @return name of the connection the file has been loaded from
     */
    public String getConnectionName(IEditorPart editor) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the connection name of a given i Project.
     * 
     * @param projectName - name of an i Project
     * @return name of the connection the file has been loaded from
     */
    public String getConnectionNameOfIProject(String projectName) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the connection name of a given TCP/IP Address.
     * 
     * @param projectName - TCP/IP address
     * @param isConnected - specifies whether the connection must be connected
     * @return name of the connection
     */
    public String getConnectionNameByIPAddr(String tcpIpAddr, boolean isConnected) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the name of the associated library of a given i Project.
     * 
     * @param projectName - name of an i Project
     * @return name of the associated library
     */
    public String getLibraryName(String projectName) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a list of configured connections.
     * 
     * @return names of configured connections
     */
    public String[] getConnectionNames() {
        return remoteConnections.getConnectionNames();
    }

    /**
     * Returns a JDBC connection for a given connection name.
     * 
     * @param connectionName - Name of the connection, the JDBC connection is
     *        returned for
     * @return RemoteConnection
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
     * @return RemoteConnection
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
     * @return RemoteConnection
     */
    private Connection getJdbcConnectionWithProperties(String profile, String connectionName, Properties properties) {

        RemoteConnection connection = remoteConnections.getConnection(connectionName);
        if (connection == null) {
            return null;
        }

        Connection jdbcConnection = connection.getJdbcConnection(properties);

        return jdbcConnection;
    }

    /**
     * Returns an ICLPrompter for a given connection name.
     * 
     * @param connectionName - connection name to identify the connection
     * @return ICLPrompter
     */
    public ICLPrompter getCLPrompter(String connectionName) {
        throw new UnsupportedOperationException();
    }

    public Member getMember(String connectionName, String libraryName, String fileName, String memberName) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void compareSourceMembers(String connectionName, List<Member> members, boolean enableEditMode) throws Exception {
        throw new UnsupportedOperationException();
    }

    public IFile getLocalResource(String connectionName, String libraryName, String fileName, String memberName, String srcType) throws Exception {
        throw new UnsupportedOperationException();
    }
}
