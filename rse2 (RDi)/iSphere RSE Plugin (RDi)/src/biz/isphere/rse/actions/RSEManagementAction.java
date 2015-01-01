/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import biz.isphere.rse.resourcemanagement.filter.FilterEntryDialog;

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
