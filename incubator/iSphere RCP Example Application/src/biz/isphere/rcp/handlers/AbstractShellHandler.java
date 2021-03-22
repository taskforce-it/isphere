package biz.isphere.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractShellHandler extends AbstractHandler implements IHandler {

    private Shell shell;

    public AbstractShellHandler(Shell shell) {
        this.shell = shell;
    }

    protected Shell getShell() {
        return shell;
    }
}
