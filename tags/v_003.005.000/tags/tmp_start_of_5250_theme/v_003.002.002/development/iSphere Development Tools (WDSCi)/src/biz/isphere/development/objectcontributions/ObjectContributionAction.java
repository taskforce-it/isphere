package biz.isphere.development.objectcontributions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.etools.systems.core.ui.actions.SystemAbstractRemoteFilePopupMenuExtensionAction;

public class ObjectContributionAction extends SystemAbstractRemoteFilePopupMenuExtensionAction {

    @Override
    public void run() {
        System.out.println("run: ");
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        System.out.println("selectionChanged: " + action.getId());
        if (selection instanceof StructuredSelection) {
            StructuredSelection structuredSelection = (StructuredSelection)selection;
            Object element = structuredSelection.getFirstElement();
            if (element != null) {
                System.out.println("  ==> " + element + " (" + element.getClass().getName() + ")");
            } else {
                System.out.println("  ==> [null]");
            }
        }
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        System.out.println("setActivePart: " + action.getId());
    }
}
