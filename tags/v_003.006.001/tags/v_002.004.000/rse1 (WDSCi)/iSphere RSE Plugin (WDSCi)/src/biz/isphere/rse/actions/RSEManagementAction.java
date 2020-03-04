package biz.isphere.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import biz.isphere.rse.rsemanagement.filter.FilterEntryDialog;

public class RSEManagementAction implements IViewActionDelegate {

    private Shell shell;
    
	public void init(IViewPart viewPart) {
	    shell = viewPart.getViewSite().getShell();
	}

	public void run(IAction action) {
	       new FilterEntryDialog(shell).open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
