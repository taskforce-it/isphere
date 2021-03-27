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

public class Access {

    public static void openMessageFileEditor(Shell shell, String connection, String library, String messageFile, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connection, messageFile, library, ISeries.MSGF, getObjectDescription(connection, library,
            messageFile, ISeries.MSGF));

        String mode;
        if (readOnly) {
            mode = "*DISPLAY";
        } else {
            mode = "*EDIT";
        }

        MessageFileEditor.openEditor(connection, remoteObject, mode);

    }

    public static void openBindingDirectoryEditor(Shell shell, String connection, String library, String bindingDirectory, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connection, bindingDirectory, library, ISeries.BNDDIR, getObjectDescription(connection, library,
            bindingDirectory, ISeries.BNDDIR));

        String mode;
        if (readOnly) {
            mode = "*DISPLAY";
        } else {
            mode = "*EDIT";
        }

        BindingDirectoryEditor.openEditor(connection, remoteObject, mode);

    }

    public static void openDataAreaEditor(Shell shell, String connection, String library, String dataArea, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connection, dataArea, library, ISeries.DTAARA, getObjectDescription(connection, library,
            dataArea, ISeries.DTAARA));

        String mode;
        if (readOnly) {
            mode = IEditor.BROWSE;
        } else {
            mode = IEditor.EDIT;
        }

        DataAreaEditor.openEditor(connection, remoteObject, mode);

    }

    public static void openUserSpaceEditor(Shell shell, String connection, String library, String userSpace, boolean readOnly) {

        RemoteObject remoteObject = new RemoteObject(connection, userSpace, library, ISeries.USRSPC, getObjectDescription(connection, library,
            userSpace, ISeries.USRSPC));

        String mode;
        if (readOnly) {
            mode = IEditor.BROWSE;
        } else {
            mode = IEditor.EDIT;
        }

        UserSpaceEditor.openEditor(connection, remoteObject, mode);

    }

    private static String getObjectDescription(String connection, String library, String object, String objectType) {

        String description = "";

        String _objectType = objectType.substring(1);

        AS400 system = IBMiHostContributionsHandler.getSystem(connection);
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
