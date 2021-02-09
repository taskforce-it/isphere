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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import biz.isphere.base.internal.KeyHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.viewmanager.IPinableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.spooledfiles.SpooledFileSubSystem;
import biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesFilterInputData;

/**
 * Opens a iSphere 'Work With Spooled Files' view for the spooled files selected
 * by an RSE spooled file filter. Holding the Ctrl key while clicking the menu
 * option opens the view and sets the 'pinned' state.
 */
public class WorkWithSpooledFilesAction implements IObjectActionDelegate, IActionDelegate2 {

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
                            openWorkWithSpooledFilesView(filterReference, page, isPinned);
                        }
                    }
                }
            }
        }
    }

    protected void openWorkWithSpooledFilesView(SystemFilterReference filterReference, IWorkbenchPage page, boolean isPinned) {

        try {

            AbstractWorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesFilterInputData(filterReference);

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

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
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
