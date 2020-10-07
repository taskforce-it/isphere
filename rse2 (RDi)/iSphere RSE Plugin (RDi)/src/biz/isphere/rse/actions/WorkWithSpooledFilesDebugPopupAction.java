/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.core.internal.viewmanager.IPinableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesJobInputData;

import com.ibm.debug.pdt.internal.core.PDTDebugTarget;
import com.ibm.debug.pdt.internal.core.model.DebuggeeProcess;

@SuppressWarnings("restriction")
public class WorkWithSpooledFilesDebugPopupAction implements IViewActionDelegate {

    private Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {

        Object selectedObject = structuredSelection.getFirstElement();

        if (selectedObject instanceof DebuggeeProcess) {

            DebuggeeProcess debuggeeProcess = (DebuggeeProcess)selectedObject;

            String hostName = getHostName(debuggeeProcess);
            String connectionName = getConnectionName(hostName);
            if (StringHelper.isNullOrEmpty(connectionName)) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Connection_not_found_A, hostName));
                return;
            }

            String qualifiedJobNameAttr = getJobName(debuggeeProcess);
            QualifiedJobName qualifiedJobName = QualifiedJobName.parse(qualifiedJobNameAttr);
            if (qualifiedJobName == null) {
                MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Invalid_job_name_A, qualifiedJobNameAttr));
                return;
            }

            IWorkbenchWindow window = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

            if (window != null) {
                IWorkbenchPage page = window.getActivePage();
                if (page != null) {
                    openWorkWithSpooledFilesView(connectionName, qualifiedJobName, page);
                }
            }
        }
    }

    protected void openWorkWithSpooledFilesView(String connectionName, QualifiedJobName qualifiedJobName, IWorkbenchPage page) {

        try {

            WorkWithSpooledFilesJobInputData inputData = new WorkWithSpooledFilesJobInputData(connectionName, qualifiedJobName);

            String contentId = inputData.getContentId();
            IViewManager viewManager = ISphereRSEPlugin.getDefault().getViewManager(IViewManager.SPOOLED_FILES_VIEWS);
            IPinableView view = (IPinableView)viewManager.getView(WorkWithSpooledFilesView.ID, contentId);

            if (view instanceof WorkWithSpooledFilesView) {
                WorkWithSpooledFilesView wrkSplfView = (WorkWithSpooledFilesView)view;
                wrkSplfView.setInputData(inputData);
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
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

    private String getConnectionName(String hostName) {

        if (!StringHelper.isNullOrEmpty(hostName)) {

            String tcpAddr = null;

            try {
                tcpAddr = InetAddress.getByName(hostName).getHostAddress();
            } catch (UnknownHostException e1) {
                MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e1));
            }

            if (!StringHelper.isNullOrEmpty(tcpAddr)) {
                String connectionName = IBMiHostContributionsHandler.getConnectionNameByIPAddr(tcpAddr, true);
                return connectionName;
            }
        }

        return null;
    }

    private String getHostName(DebuggeeProcess debuggeeProcess) {

        if (debuggeeProcess.getDebugTarget() instanceof PDTDebugTarget) {
            PDTDebugTarget debugTarget = (PDTDebugTarget)debuggeeProcess.getDebugTarget();

            String hostName = debugTarget.getSocket().getInetAddress().getHostName();
            return hostName;
        }

        return null;
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
