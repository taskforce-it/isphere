/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import biz.isphere.base.internal.KeyHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.core.internal.viewmanager.IPinableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesJobInputData;

import com.ibm.etools.iseries.subsystems.qsys.jobs.QSYSRemoteJob;

/**
 * Opens a iSpehere 'Work With Spooled Files' view for a job selected from a RSE
 * job filter. Holding the Ctrl key while clicking the menu option opens the
 * view and sets the 'pinned' state.
 */
public class WorkWithSpooledFilesRsePopupAction implements IObjectActionDelegate, IActionDelegate2 {

    private Shell shell;
    private IStructuredSelection structuredSelection;
    private boolean isCtrlKey;

    public void runWithEvent(IAction action, Event event) {
        isCtrlKey = KeyHelper.isCtrlKey(event);
        run(action);
    }

    public void run(IAction action) {

        boolean isPinned;
        if (structuredSelection.size() > 1 || isCtrlKey) {
            isPinned = true;
        } else {
            isPinned = false;
        }

        Iterator<?> selectionIterator = structuredSelection.iterator();
        while (selectionIterator.hasNext()) {
            Object selectedObject = (Object)selectionIterator.next();
            if (selectedObject instanceof QSYSRemoteJob) {

                QSYSRemoteJob remoteJob = (QSYSRemoteJob)selectedObject;
                String absoluteName = remoteJob.getAbsoluteName();
                QualifiedJobName qualifiedJobName = QualifiedJobName.parse(absoluteName);
                if (qualifiedJobName == null) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Invalid_job_name_A, absoluteName));
                    return;
                }

                String connectionName = remoteJob.getRemoteJobContext().getJobSubsystem().getHostAliasName();
                if (connectionName != null) {
                    IWorkbenchWindow window = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();
                        if (page != null) {
                            openWorkWithSpooledFilesView(connectionName, qualifiedJobName, page, isPinned);
                        }
                    }
                }
            }
        }
    }

    protected void openWorkWithSpooledFilesView(String connectionName, QualifiedJobName qualifiedJobName, IWorkbenchPage page, boolean isPinned) {

        try {

            WorkWithSpooledFilesJobInputData inputData = new WorkWithSpooledFilesJobInputData(connectionName, qualifiedJobName);

            String contentId = inputData.getContentId();
            IViewManager viewManager = ISphereRSEPlugin.getDefault().getViewManager(IViewManager.SPOOLED_FILES_VIEWS);
            IPinableView view = (IPinableView)viewManager.getView(WorkWithSpooledFilesView.ID, contentId);

            if (view instanceof WorkWithSpooledFilesView) {
                WorkWithSpooledFilesView wrkSplfView = (WorkWithSpooledFilesView)view;
                wrkSplfView.setInputData(inputData);
                wrkSplfView.setPinned(isPinned);
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        structuredSelection = ((IStructuredSelection)selection);
    }

    public void setActivePart(IAction action, IWorkbenchPart view) {
        this.shell = view.getSite().getShell();
    }

    public void init(IAction action) {
    }

    public void dispose() {
    }

    private Shell getShell() {
        return shell;
    }
}
