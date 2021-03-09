package biz.isphere.core.externalapi;

import biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.messagefileeditor.MessageFileEditor;

public class Access {

    public static void openMessageFileEditor(
        String connection, 
        String library, 
        String messageFile,
        boolean readOnly) {
        
        RemoteObject remoteObject = 
            new RemoteObject(
                connection, 
                messageFile, 
                library, 
                ISeries.MSGF, 
                "");

        String mode;
        if (readOnly) {
            mode = "*DISPLAY";
        }
        else {
            mode = "*EDIT";
        }
        
        MessageFileEditor.openEditor(
            connection, 
            remoteObject,
            mode);
        
    }

    public static void openBindingDirectoryEditor(
        String connection, 
        String library, 
        String bindingDirectory,
        boolean readOnly) {
        
        RemoteObject remoteObject = 
            new RemoteObject(
                connection, 
                bindingDirectory, 
                library, 
                ISeries.BNDDIR, 
                "");

        String mode;
        if (readOnly) {
            mode = "*DISPLAY";
        }
        else {
            mode = "*EDIT";
        }
        
        BindingDirectoryEditor.openEditor(
            connection, 
            remoteObject,
            mode);
        
    }
    
}
