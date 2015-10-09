package biz.isphere.messagesubsystem.rse;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.messagesubsystem.Messages;

import com.ibm.as400.access.QueuedMessage;

public class QueuedMessageResourceAdapterDelegate {

    private static final String QUEUED_MESSAGE_RESOURCE = "Queued message resource"; //$NON-NLS-1$

    public String getType() {
        return QUEUED_MESSAGE_RESOURCE;
    }

    public Object getParent() {
        return null;
    }

    public boolean hasChildren() {
        return false;
    }

    public boolean showDelete() {
        return true;
    }

    public boolean canDelete() {
        return true;
    }

    public boolean doDelete(Shell shell, QueuedMessage queuedMessage, IProgressMonitor monitor) {

        try {
            queuedMessage.getQueue().remove(queuedMessage.getKey());
        } catch (Exception e) {
            final String errorMessage;
            if (e.getMessage() == null) {
                errorMessage = e.toString();
            } else {
                errorMessage = e.getMessage();
            }

            shell.getDisplay().syncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Delete_Message_Error, errorMessage);
                }
            });

            return false;
        }

        return true;
    }

}
