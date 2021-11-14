/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.ibmi.contributions.extension.handler;

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

import com.ibm.as400.access.AS400;

import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;
import biz.isphere.core.internal.Member;

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
