/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.standalone.ibmi.contributions.extension.point;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCDriver;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ObjectList;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;
import biz.isphere.core.internal.Member;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.standalone.connections.Connections;

/**
 * This class connects to the
 * <i>biz.isphere.core.ibmi.contributions.extension.point
 * .IIBMiHostContributions</i> extension point of the <i>iSphere Core
 * Plugin</i>.
 * 
 * @author Thomas Raddatz
 */
public class XIBMiContributions implements IIBMiHostContributions {

    AS400 system;

    public XIBMiContributions() {
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
     * "Remote Connections - IBM i - Authentication" preference page for RDi
     * 9.5+.
     * 
     * @return <i>true</i>, if Kerberos authentication is selected, else
     *         <i>false</i>
     */
    public boolean isKerberosAuthentication() {
        return false;
    }

    /**
     * Returns <i>true</i> when the subsystem of a given connection is in
     * offline mode.
     * 
     * @return <i>true</i>, subsystem is offline, else <i>false</i>
     */
    public boolean isSubSystemOffline(String connectionName) {

        AS400 system = getSystem(connectionName);
        if (system != null && system.isConnected()) {
            return true;
        }

        return system.isConnected();
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

            String escapeMessage = null;
            CommandCall commandCall = new CommandCall(getSystem(connectionName));
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

        if (getSystem(connectionName) == null) {
            return false;
        }

        ObjectList list;
        if (memberName == null) {
            list = new ObjectList(getSystem(connectionName), libraryName, objectName, objectType);
        } else {
            list = new ObjectList(getSystem(connectionName), libraryName, objectName, objectType, memberName);
        }

        try {

            list.load();

            Object library = list.getObjects(0, 1);
            if (library == null) {
                return false;
            }

            return true;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Failed to check object ***", e); //$NON-NLS-1$
        } finally {
            try {
                list.close();
            } catch (Exception e) {
                e.printStackTrace();
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

        if (StringHelper.isNullOrEmpty(connectionName)) {
            return null;
        }

        return Connections.getInstance().getSystem(connectionName);
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
        return null;
    }

    /**
     * Returns the connection name of a given editor.
     * 
     * @param editor - that shows a remote file
     * @return name of the connection the file has been loaded from
     */
    public String getConnectionName(IEditorPart editor) {
        return null;
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
        return null;
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
        return Connections.getInstance().getConnectionNames();
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

        Connection jdbcConnection = null;
        AS400JDBCDriver as400JDBCDriver = null;

        try {

            try {

                as400JDBCDriver = (AS400JDBCDriver)DriverManager.getDriver("jdbc:as400");

            } catch (SQLException e) {

                as400JDBCDriver = new AS400JDBCDriver();
                DriverManager.registerDriver(as400JDBCDriver);

            }

            jdbcConnection = as400JDBCDriver.connect(getSystem(connectionName), properties, null);

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not produce JDBC connection ***", e);
        }

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
