/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.rse.Messages;

import com.ibm.debug.pdt.internal.core.model.DebuggeeProcess;

@SuppressWarnings("restriction")
public class CopyQualifiedJobNameDebugPopupAction implements IViewActionDelegate {

    private Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {

        Object selectedObject = structuredSelection.getFirstElement();

        if (selectedObject instanceof DebuggeeProcess) {

            DebuggeeProcess debuggeeProcess = (DebuggeeProcess)selectedObject;

            String qualifiedJobNameAttr = getJobName(debuggeeProcess);
            QualifiedJobName qualifiedJobName = QualifiedJobName.parse(qualifiedJobNameAttr);
            if (qualifiedJobName == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Invalid_job_name_A, qualifiedJobNameAttr));
                return;
            }

            ClipboardHelper.setText(qualifiedJobName.getQualifiedJobName());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (isIBMiJob(selection)) {
            structuredSelection = ((IStructuredSelection)selection);
            action.setEnabled(true);
        } else {
            structuredSelection = null;
            action.setEnabled(false);
        }
    }

    private String getJobName(IProcess debuggeeProcess) {
        return debuggeeProcess.getAttribute(null);
    }

    private boolean isIBMiJob(ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
            Object selectedObject = structuredSelection.getFirstElement();
            if ((selectedObject instanceof IProcess)) {
                IProcess process = (IProcess)selectedObject;
                if (QualifiedJobName.parse(process.getAttribute(null)) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public void init(IViewPart view) {
        this.shell = view.getSite().getShell();
    }

    private Shell getShell() {
        return shell;
    }
}
