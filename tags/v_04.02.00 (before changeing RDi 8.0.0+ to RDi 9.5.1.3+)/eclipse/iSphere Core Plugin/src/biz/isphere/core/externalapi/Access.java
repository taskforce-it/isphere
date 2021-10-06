/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.externalapi;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor;
import biz.isphere.core.dataareaeditor.DataAreaEditor;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefilecompare.rse.MessageFileCompareEditor;
import biz.isphere.core.messagefileeditor.MessageFileEditor;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.userspaceeditor.UserSpaceEditor;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.ObjectDoesNotExistException;

/**
 * This class is the public API of the iSphere Core Plug-in.
 */
public class Access {

    /**
     * Opens the message file editor for a given message file.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param library - name of the library where the message file is stored.
     * @param messageFile - name of the message file whose content is edited.
     * @param readOnly - specifies whether to open the message file in
     *        <i>display</i> oder <i>edit</i> mode.
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openMessageFileEditor(Shell shell, String connectionName, String library, String messageFile, boolean readOnly)
        throws Exception {

        RemoteObject remoteObject = createRemoteObject(connectionName, library, messageFile, ISeries.MSGF);

        String mode;
        if (readOnly) {
            mode = IEditor.DISPLAY;
        } else {
            mode = IEditor.EDIT;
        }

        MessageFileEditor.openEditor(connectionName, remoteObject, mode);

    }

    /**
     * Opens the binding directory editor for a given binding directory.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param library - name of the library where the binding directory is
     *        stored.
     * @param bindingDirectory - name of the binding directory whose content is
     *        edited.
     * @param readOnly - specifies whether to open the binding directory in
     *        <i>display</i> oder <i>edit</i> mode.
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openBindingDirectoryEditor(Shell shell, String connectionName, String library, String bindingDirectory, boolean readOnly)
        throws Exception {

        RemoteObject remoteObject = createRemoteObject(connectionName, library, bindingDirectory, ISeries.BNDDIR);

        String mode;
        if (readOnly) {
            mode = IEditor.DISPLAY;
        } else {
            mode = IEditor.EDIT;
        }

        BindingDirectoryEditor.openEditor(connectionName, remoteObject, mode);

    }

    /**
     * Opens the data area editor for a given data area.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param library - name of the library where the data area is stored.
     * @param dataArea - name of the data area whose content is edited.
     * @param readOnly - specifies whether to open the data area in
     *        <i>display</i> oder <i>edit</i> mode.
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openDataAreaEditor(Shell shell, String connectionName, String library, String dataArea, boolean readOnly) throws Exception {

        RemoteObject remoteObject = createRemoteObject(connectionName, library, dataArea, ISeries.DTAARA);

        String mode;
        if (readOnly) {
            mode = IEditor.DISPLAY;
        } else {
            mode = IEditor.EDIT;
        }

        DataAreaEditor.openEditor(connectionName, remoteObject, mode);

    }

    /**
     * Opens the user space editor for a given user space.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param library - name of the library where the user space is stored.
     * @param userSpace - name of the user space whose content is edited.
     * @param readOnly - specifies whether to open the user space in
     *        <i>display</i> oder <i>edit</i> mode.
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openUserSpaceEditor(Shell shell, String connectionName, String library, String userSpace, boolean readOnly) throws Exception {

        RemoteObject remoteObject = createRemoteObject(connectionName, library, userSpace, ISeries.USRSPC);

        String mode;
        if (readOnly) {
            mode = IEditor.DISPLAY;
        } else {
            mode = IEditor.EDIT;
        }

        UserSpaceEditor.openEditor(connectionName, remoteObject, mode);

    }

    /**
     * @param shell - the parent shell.
     * @param leftConnectionName - name of the connection where the left message
     *        file is loaded from.
     * @param leftLibrary - library name of the left message file.
     * @param leftMessageFile - name of the message file that is loaded to the
     *        left side of the editor.
     * @param rightConnectionName - name of the connection where the right
     *        message file is loaded from.
     * @param rightLibrary - library name of the right message file.
     * @param rightMessageFile - name of the message file that is loaded to the
     *        right side of the editor.
     * @param configuration - message file editor configuration
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openMessageFileCompareEditor(Shell shell, String leftConnectionName, String leftLibrary, String leftMessageFile,
        String rightConnectionName, String rightLibrary, String rightMessageFile, IMessageFileCompareEditorConfiguration configuration)
        throws Exception {

        RemoteObject leftRemoteMessageFile = createRemoteObject(leftConnectionName, leftLibrary, leftMessageFile, ISeries.MSGF);
        RemoteObject rightRemoteMessageFile = createRemoteObject(rightConnectionName, rightLibrary, rightMessageFile, ISeries.MSGF);

        openMessageFileCompareEditor(shell, leftRemoteMessageFile, rightRemoteMessageFile, configuration);

    }

    /**
     * @param shell - the parent shell.
     * @param leftMessageFile - remote message file that is loaded to the left
     *        side of the editor.
     * @param rightMessageFile - remote message file that is loaded to the left
     *        side of the editor.
     * @param configuration - message file editor configuration
     * @throws Exception
     */
    public static void openMessageFileCompareEditor(Shell shell, RemoteObject leftMessageFile, RemoteObject rightMessageFile,
        IMessageFileCompareEditorConfiguration configuration) throws Exception {

        MessageFileCompareEditor.openEditor(leftMessageFile, rightMessageFile, configuration);

    }

    /**
     * Produces a remote object of a given type.
     * 
     * @param connectionName - connection name.
     * @param library - name of the library where the object is stored.
     * @param object - name of the object.
     * @param objectType - type of the object.
     * @return remote object
     * @see QualifiedConnectionName
     */
    private static RemoteObject createRemoteObject(String connectionName, String library, String object, String objectType) {

        if (StringHelper.isNullOrEmpty(connectionName) || StringHelper.isNullOrEmpty(library) || StringHelper.isNullOrEmpty(object)
            || StringHelper.isNullOrEmpty(objectType)) {
            return null;
        }

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system == null || !ISphereHelper.checkObject(system, library, object, objectType)) {
            return null;
        }

        String description = getObjectDescription(connectionName, library, object, objectType);
        RemoteObject remoteObject = new RemoteObject(connectionName, object, library, objectType, description);

        return remoteObject;
    }

    /**
     * Private method for retrieving the object description (text) of a given
     * object.
     * 
     * @param connectionName - connection name.
     * @param library - name of the library where the object is stored.
     * @param object - name of the object whose object description (text) is
     *        retrieved.
     * @param objectType - object type.
     * @return object description (text)
     * @see QualifiedConnectionName
     */
    private static String getObjectDescription(String connectionName, String library, String object, String objectType) {

        String description = ""; //$NON-NLS-1$

        String _objectType = objectType.substring(1);

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system != null) {
            ObjectDescription objectDescription = new ObjectDescription(system, library, object, _objectType);
            if (objectDescription != null) {
                try {
                    description = objectDescription.getValueAsString(ObjectDescription.TEXT_DESCRIPTION);
                } catch (AS400Exception e) {
                    e.printStackTrace();
                } catch (AS400SecurityException e) {
                } catch (ErrorCompletingRequestException e) {
                } catch (InterruptedException e) {
                } catch (IOException e) {
                } catch (ObjectDoesNotExistException e) {
                }
            }
        }

        return description;
    }
    
    /**
     * Opens a spooled file.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param file - The name of the spooled file.
     * @param fileNumber - The number of the spooled file.
     * @param jobName - The name of the job that created the spooled file.
     * @param jobUser - The user who created the spooled file.
     * @param jobNumber - The number of the job that created the spooled file.
     * @param jobSystem - The name of the system where the spooled file was created.
     * @param creationDate - The date the spooled file was created on the system. 
     *        The date is encoded in a character string with the format CYYMMDD.
     * @param creationTime - The time the spooled file was created on the system. 
     *        The time is encoded in a character string with the format HHMMSS.
     * @param format - The output format of the spooled file. 
     *        IPreferences.OUTPUT_FORMAT_PDF 
     *        IPreferences.OUTPUT_FORMAT_HTML 
     *        IPreferences.OUTPUT_FORMAT_TEXT 
     *        IPreferences.OUTPUT_FORMAT_DFT
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openSpooledFile(Shell shell, String connectionName, String file, int fileNumber, String jobName, String jobUser, String jobNumber, String jobSystem, String creationDate, String creationTime, String format)
        throws Exception {

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

        if (system != null) {
            
            if (ISphereHelper.checkISphereLibrary(shell, connectionName)) {
                
                SpooledFile spooledFile = new SpooledFile();
                spooledFile.setConnectionName(connectionName);
                spooledFile.setAS400(system);
                spooledFile.setFile(file);
                spooledFile.setFileNumber(fileNumber);
                spooledFile.setJobName(jobName);
                spooledFile.setJobUser(jobUser);
                spooledFile.setJobNumber(jobNumber);
                spooledFile.setJobSystem(jobSystem);
                spooledFile.setCreationDate(creationDate);
                spooledFile.setCreationTime(creationTime);
                
                String _format = format;
                if (_format.equals(IPreferences.OUTPUT_FORMAT_DFT)) {
                    _format = Preferences.getInstance().getSpooledFileConversionDefaultFormat();
                }
                
                String message = spooledFile.open(_format);
                
                if (message != null) {
                    MessageDialog.openError(shell, Messages.Error, message);
                }
                
            }
            
        }

    }

    /**
     * Searches for strings in source files and returns the search results.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param _searchOptions - Contains the strings to search for and other search options.
     * @param _searchElements - Contains the elements (Source file members) to search for strings.
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static biz.isphere.core.sourcefilesearch.SearchResult[] searchStringInSourceFile(Shell shell, String connectionName, SearchOptions _searchOptions, ArrayList<biz.isphere.core.sourcefilesearch.SearchElement> _searchElements)
        throws Exception {

        AS400 _as400 = IBMiHostContributionsHandler.getSystem(connectionName);
        if (_as400 != null) {
            Connection _jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
            if (_jdbcConnection != null) {
                
                return new biz.isphere.core.sourcefilesearch.SearchExec().executeJoin(
                        _as400,
                        _jdbcConnection,
                        _searchOptions,
                        _searchElements);
                
            }
        }
        
        return new biz.isphere.core.sourcefilesearch.SearchResult[0];
        
    }

    /**
     * Searches for strings in message files and returns the search results.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name.
     * @param _searchOptions - Contains the strings to search for and other search options.
     * @param _searchElements - Contains the elements (Message files) to search for strings.
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static biz.isphere.core.messagefilesearch.SearchResult[] searchStringInMessageFile(Shell shell, String connectionName, SearchOptions _searchOptions, ArrayList<biz.isphere.core.messagefilesearch.SearchElement> _searchElements)
        throws Exception {

        AS400 _as400 = IBMiHostContributionsHandler.getSystem(connectionName);
        if (_as400 != null) {
            Connection _jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
            if (_jdbcConnection != null) {
                
                return new biz.isphere.core.messagefilesearch.SearchExec().executeJoin(
                        _as400,
                        connectionName,
                        _jdbcConnection,
                        _searchOptions,
                        _searchElements);
                
            }
        }
        
        return new biz.isphere.core.messagefilesearch.SearchResult[0];
        
    }
 
}
