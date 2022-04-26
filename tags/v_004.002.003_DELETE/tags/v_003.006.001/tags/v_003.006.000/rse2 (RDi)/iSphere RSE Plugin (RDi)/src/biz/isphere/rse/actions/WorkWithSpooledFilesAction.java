/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.viewmanager.IPinnableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;
import biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesInputData;

public class WorkWithSpooledFilesAction implements IObjectActionDelegate {

    private IStructuredSelection structuredSelection;

    public WorkWithSpooledFilesAction() {
        super();
    }

    public void run(IAction action) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {
            Iterator<?> iterator = structuredSelection.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();
                SystemFilterReference filterReference = (SystemFilterReference)object;
                if ((object instanceof SystemFilterReference) && (filterReference.getSubSystem() instanceof SpooledFileSubSystem)) {
                    IWorkbenchWindow window = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();
                        if (page != null) {
                            openWorkWithSpooledFilesView(filterReference, page);
                        }
                    }
                }
            }
        }
    }

    protected void openWorkWithSpooledFilesView(SystemFilterReference filterReference, IWorkbenchPage page) {

        try {

            AbstractWorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesInputData(filterReference);

            String contentId = inputData.getContentId();
            IViewManager viewManager = ISphereRSEPlugin.getDefault().getViewManager(IViewManager.SPOOLED_FILES_VIEWS);
            IPinnableView view = (IPinnableView)viewManager.getView(WorkWithSpooledFilesView.ID, contentId);

            if (view instanceof WorkWithSpooledFilesView) {
                WorkWithSpooledFilesView wrkSplfView = (WorkWithSpooledFilesView)view;
                wrkSplfView.setInputData(inputData);
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    public String getConnectionName(ISubSystem subSystem) {
        return ConnectionManager.getConnectionName(subSystem.getHost());
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // nothing to do here
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

}
