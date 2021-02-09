/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.subsystems.qsys.jobs.QSYSRemoteJob;

/**
 * Copies the full qualified job name of a job selected from a RSE job filter to
 * the clipboard.
 */
public class CopyQualifiedJobNameRsePopupAction implements IObjectActionDelegate {

    private Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {

        Object selectedObject = structuredSelection.getFirstElement();

        if (selectedObject instanceof QSYSRemoteJob) {

            QSYSRemoteJob remoteJob = (QSYSRemoteJob)selectedObject;
            String absoluteName = remoteJob.getAbsoluteName();
            QualifiedJobName qualifiedJobName = QualifiedJobName.parse(absoluteName);
            if (qualifiedJobName == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Invalid_job_name_A, absoluteName));
                return;
            }

            ClipboardHelper.setText(qualifiedJobName.getQualifiedJobName());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        structuredSelection = ((IStructuredSelection)selection);
    }

    public void setActivePart(IAction action, IWorkbenchPart view) {
        this.shell = view.getSite().getShell();
    }

    private Shell getShell() {
        return shell;
    }
}
