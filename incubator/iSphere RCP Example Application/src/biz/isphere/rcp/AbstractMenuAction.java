package biz.isphere.rcp;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class AbstractMenuAction implements IWorkbenchWindowActionDelegate {

    private Shell shell;

    public void init(IWorkbenchWindow window) {
        this.shell = window.getShell();
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void dispose() {
    }

    protected Shell getShell() {
        return shell;
    }
}
