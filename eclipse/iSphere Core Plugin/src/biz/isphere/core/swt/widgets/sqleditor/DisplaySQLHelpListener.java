package biz.isphere.core.swt.widgets.sqleditor;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.PlatformUI;

public class DisplaySQLHelpListener extends MouseAdapter {
    @Override
    public void mouseUp(MouseEvent event) {
        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/biz.isphere.base.help/html/sql/sql_reference.html");
    }
}
