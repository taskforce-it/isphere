/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.externalapi;

import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor;
import biz.isphere.core.dataareaeditor.DataAreaEditor;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefileeditor.MessageFileEditor;
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
     */
    public static void openMessageFileEditor(Shell shell, String connectionName, String library, String messageFile, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connectionName, messageFile, library, ISeries.MSGF, getObjectDescription(connectionName,
            library, messageFile, ISeries.MSGF));

        String mode;
        if (readOnly) {
            mode = "*DISPLAY";
        } else {
            mode = "*EDIT";
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
     */
    public static void openBindingDirectoryEditor(Shell shell, String connectionName, String library, String bindingDirectory, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connectionName, bindingDirectory, library, ISeries.BNDDIR, getObjectDescription(connectionName,
            library, bindingDirectory, ISeries.BNDDIR));

        String mode;
        if (readOnly) {
            mode = "*DISPLAY";
        } else {
            mode = "*EDIT";
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
     */
    public static void openDataAreaEditor(Shell shell, String connectionName, String library, String dataArea, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connectionName, dataArea, library, ISeries.DTAARA, getObjectDescription(connectionName, library,
            dataArea, ISeries.DTAARA));

        String mode;
        if (readOnly) {
            mode = IEditor.BROWSE;
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
     */
    public static void openUserSpaceEditor(Shell shell, String connectionName, String library, String userSpace, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connectionName, userSpace, library, ISeries.USRSPC, getObjectDescription(connectionName,
            library, userSpace, ISeries.USRSPC));

        String mode;
        if (readOnly) {
            mode = IEditor.BROWSE;
        } else {
            mode = IEditor.EDIT;
        }

        UserSpaceEditor.openEditor(connectionName, remoteObject, mode);

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

}
