/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

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
