/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.action;

import java.net.UnknownHostException;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.ibm.debug.pdt.internal.core.model.DebuggeeProcess;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.joblogexplorer.jobs.rse.JobLogActiveJobLoader;
import biz.isphere.joblogexplorer.rse.Messages;
import biz.isphere.rse.internal.IBMiDebugHelper;

@SuppressWarnings("restriction")
public class OpenJobLogDebugPopupAction implements IViewActionDelegate {

    private Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {

        Object selectedObject = structuredSelection.getFirstElement();

        if (selectedObject instanceof DebuggeeProcess) {

            DebuggeeProcess debuggeeProcess = (DebuggeeProcess)selectedObject;

            String connectionName;
            try {
                connectionName = IBMiDebugHelper.getConnectionName(debuggeeProcess);
            } catch (UnknownHostException e) {
                MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                return;
            }

            if (StringHelper.isNullOrEmpty(connectionName)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                    Messages.bind(Messages.Error_Connection_not_found_A, IBMiDebugHelper.getHostName(debuggeeProcess)));
                return;
            }

            String qualifiedJobNameAttr = getJobName(debuggeeProcess);
            QualifiedJobName qualifiedJobName = QualifiedJobName.parse(qualifiedJobNameAttr);
            if (qualifiedJobName == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Error_Invalid_job_name_A, qualifiedJobNameAttr));
                return;
            }

            JobLogActiveJobLoader job = new JobLogActiveJobLoader(connectionName, qualifiedJobName);
            job.run();
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
