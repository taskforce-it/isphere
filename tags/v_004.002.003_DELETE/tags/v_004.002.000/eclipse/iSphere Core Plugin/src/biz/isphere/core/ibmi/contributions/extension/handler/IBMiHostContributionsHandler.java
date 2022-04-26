/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.ibmi.contributions.extension.handler;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;
import biz.isphere.core.internal.Member;
import biz.isphere.core.internal.api.retrievememberdescription.MBRD0100;
import biz.isphere.core.internal.api.retrievememberdescription.QUSRMBRD;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;

public class IBMiHostContributionsHandler {

    private static final String EXTENSION_ID = "biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions"; //$NON-NLS-1$

    private static IIBMiHostContributions factory;

    public static boolean hasContribution() {

        if (getContributionsFactory() == null) {
            return false;
        }

        return true;
    }

    /**
     * Returns <i>true</i> when the RSE sub-system has been initialized.
     * 
     * @return <i>true</i>, if RSE sub-system has been initialized, else
     *         <i>false</i>
     */
    public static boolean isRseSubsystemInitialized() {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.isRseSubsystemInitialized();
    }

    /**
     * Returns <i>true</i> when Kerberos authentication is enabled on the
     * "Remote Systems - IBM i - Authentication" preference page for RDi 9.5+.
     * 
     * @return <i>true</i>, if Kerberos authentication is selected, else
     *         <i>false</i>
     */
    public static boolean isKerberosAuthentication() {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.isKerberosAuthentication();
    }

    /**
     * Returns <i>true</i> when the specified connection is known to the
     * application.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when known, else <i>false</i>
     */
    public static boolean isAvailable(String qualifiedConnectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.isAvailable(qualifiedConnectionName);
    }

    /**
     * Returns <i>true</i> when the specified connection is in offline mode.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when offline, else <i>false</i>
     */
    public static boolean isOffline(String qualifiedConnectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.isOffline(qualifiedConnectionName);
    }

    /**
     * Returns <i>true</i> when specified connection is connected.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when connected, else <i>false</i>
     */
    public static boolean isConnected(String qualifiedConnectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.isConnected(qualifiedConnectionName);
    }

    /**
     * Connects the connection identified by a given connection name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return <i>true</i>, when successfully connected, else <i>false</i>
     * @throws Exception
     */
    public static boolean connect(String qualifiedConnectionName) throws Exception {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.connect(qualifiedConnectionName);
    }

    /**
     * Changes the <i>offline</i> status of the specified connection.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     */
    public void setOffline(String qualifiedConnectionName, boolean offline) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return;
        }

        factory.setOffline(qualifiedConnectionName, offline);
    }

    /**
     * Executes a given command for a given connection.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param command - command that is executed
     * @param rtnMessages - list of error messages or <code>null</code>
     * @return error message text on error or <code>null</code> on success
     */
    public static String executeCommand(String qualifiedConnectionName, String command) {
        return executeCommand(qualifiedConnectionName, command, null);
    }

    public static String executeCommand(String connectionName, String command, List<AS400Message> rtnMessages) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return "RDi plug-in not installed."; //$NON-NLS-1$
        }

        return factory.executeCommand(connectionName, command, rtnMessages);
    }

    /**
     * Returns whether a given library exists or not.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param libraryName - library that is tested
     * @return <code>true</code>, when the library exists, else
     *         <code>false</code>.
     */
    public static boolean checkLibrary(String qualifiedConnectionName, String libraryName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.checkLibrary(qualifiedConnectionName, libraryName);
    }

    /**
     * Checks whether a given file exists or not.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param libraryName - library that should contain the file
     * @param fileName - file that is tested
     * @return <code>true</code>, when the file exists, else <code>false</code>.
     */
    public static boolean checkFile(String qualifiedConnectionName, String libraryName, String fileName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        return factory.checkFile(qualifiedConnectionName, libraryName, fileName);
    }

    /**
     * Checks whether a given member exists or not.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param libraryName - library that should contain the file
     * @param fileName - file that should contain the member
     * @param memberName - name of the member that is tested
     * @return <code>true</code>, when the library exists, else
     *         <code>false</code>.
     */
    public static boolean checkMember(String qualifiedConnectionName, String libraryName, String fileName, String memberName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return false;
        }

        String tMemberName = memberName;

        if (memberName.startsWith("*")) {
            try {
                tMemberName = resolveMemberName(qualifiedConnectionName, libraryName, fileName, tMemberName);
            } catch (Exception e) {
                return false;
            }
        }

        return factory.checkMember(qualifiedConnectionName, libraryName, fileName, tMemberName);
    }

    /**
     * Returns the name of the iSphere library that is associated to a given
     * connection.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection library is returned for
     * @return name of the iSphere library
     */
    public static String getISphereLibrary(String qualifiedConnectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getISphereLibrary(qualifiedConnectionName);
    }

    /**
     * Returns the system (AS400) identified by a given connection name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return AS400
     */
    public static AS400 getSystem(String qualifiedConnectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getSystem(qualifiedConnectionName);
    }

    /**
     * Returns the connection name of a given editor.
     * 
     * @param editor - that shows a remote file
     * @return name of the connection the file has been loaded from
     */
    public static String getConnectionName(IEditorPart editor) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput)editorInput).getFile();
            return factory.getConnectionName(file);
        }

        return null;
    }

    /**
     * Returns the connection name of a given i Project.
     * 
     * @param projectName - name of an i Project
     * @return name of the connection the file has been loaded from
     */
    public static String getConnectionNameOfIProject(String projectName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getConnectionNameOfIProject(projectName);
    }

    /**
     * Returns the name of the associated library of a given i Project.
     * 
     * @param projectName - name of an i Project
     * @return name of the associated library
     */
    public static String getLibraryNameOfIProject(String projectName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getLibraryNameOfIProject(projectName);
    }

    /**
     * Returns the qualified connection name of a given TCP/IP Address.
     * 
     * @param projectName - TCP/IP address
     * @param isConnected - specifies whether the connection must be connected
     * @return name of the connection
     */
    public static String getConnectionNameByIPAddr(String tcpIpAddr, boolean isConnected) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getConnectionNameByIPAddr(tcpIpAddr, isConnected);
    }

    /**
     * Returns a list of configured connections.
     * 
     * @return names of configured connections
     */
    public static String[] getConnectionNames() {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getConnectionNames();
    }

    /**
     * Returns a JDBC connection for a given connection name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @return Connection
     */
    public static Connection getJdbcConnection(String qualifiedConnectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getJdbcConnection(qualifiedConnectionName);
    }

    /**
     * Returns an ICLPrompter for a given connection name.
     * 
     * @param qualifiedConnectionName - connection name to identify the
     *        connection
     * @return ICLPrompter
     */
    public static ICLPrompter getCLPrompter(String qualifiedConnectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getCLPrompter(qualifiedConnectionName);
    }

    /**
     * Returns the file member identified by library, file and member name.
     * 
     * @param qualifiedConnectionName - name that uniquely identifies the
     *        connection
     * @param libraryName - name of the library where the file is stored
     * @param fileName - name of the file that contains the member
     * @param memberName - name that identifies the member
     * @return Member
     * @throws Exception
     */
    public static Member getMember(String connectionName, String libraryName, String fileName, String memberName) throws Exception {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getMember(connectionName, libraryName, fileName, memberName);
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
    public static void compareSourceMembers(String connectionName, List<Member> members, boolean enableEditMode) throws Exception {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return;
        }

        factory.compareSourceMembers(connectionName, members, enableEditMode);
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
    public IFile getLocalResource(String connectionName, String libraryName, String fileName, String memberName, String srcType) throws Exception {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getLocalResource(connectionName, libraryName, fileName, memberName, srcType);
    }

    public static String resolveMemberName(String connectionName, String libraryName, String fileName, String memberName) throws AS400Exception,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        try {

            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            MBRD0100 mbrd0100 = new MBRD0100(system);

            QUSRMBRD memberDescription = new QUSRMBRD(system);
            memberDescription.setFile(fileName, libraryName, memberName);
            if (memberDescription.execute(mbrd0100)) {
                return mbrd0100.getMemberName();
            }

        } catch (PropertyVetoException e) {
            ISpherePlugin.logError("*** Failed to retrieve member description " + libraryName + "/" + fileName + "(" + memberName + ")" + " ***", e);
        }

        return null;
    }

    public static int getSystemCcsid(String connectionName) {

        AS400 system = getSystem(connectionName);
        if (system == null) {
            return -1;
        }

        return system.getCcsid();
    }

    /**
     * Returns the RDi contributions if there is a registered extension for
     * that.
     * 
     * @return RDi contributions factory or null
     */
    private static IIBMiHostContributions getContributionsFactory() {

        if (factory == null) {

            IExtensionRegistry tRegistry = Platform.getExtensionRegistry();
            IConfigurationElement[] configElements = tRegistry.getConfigurationElementsFor(EXTENSION_ID);

            if (configElements != null && configElements.length > 0) {
                try {
                    final Object tempDialog = configElements[0].createExecutableExtension("class");
                    if (tempDialog instanceof IIBMiHostContributions) {
                        factory = (IIBMiHostContributions)tempDialog;
                    }
                } catch (CoreException e) {
                }
            }

        }

        return factory;
    }

}
