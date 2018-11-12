package biz.isphere.development.objectcontributions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ObjectContributionAction implements IObjectActionDelegate {

    public void run(IAction action) {
        System.out.println("run: " + action.getId());
    }

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

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        System.out.println("setActivePart: " + action.getId());
    }

}
