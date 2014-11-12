package biz.isphere.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public class MessageDialogUIJob extends UIJob {

    MessageDialog dialog;

    public MessageDialogUIJob(Display jobDisplay, MessageDialog dialog) {
        super(jobDisplay, "");
        this.dialog = dialog;
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        dialog.open();
        return Status.OK_STATUS;
    }

}
