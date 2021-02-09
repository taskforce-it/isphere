/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.net.UnknownHostException;
import java.util.Iterator;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.KeyHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.core.internal.viewmanager.IPinableView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.spooledfiles.view.rse.AbstractWorkWithSpooledFilesInputData;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.internal.IBMiDebugHelper;
import biz.isphere.rse.spooledfiles.view.WorkWithSpooledFilesView;
import biz.isphere.rse.spooledfiles.view.rse.WorkWithSpooledFilesJobInputData;

import com.ibm.debug.pdt.internal.core.model.DebuggeeProcess;

/**
 * Opens a iSphere 'Work With Spooled Files' view for the job that had been
 * stopped at a breakpoint. Holding the Ctrl key while clicking the menu option
 * opens the view and sets the 'pinned' state.
 */
@SuppressWarnings("restriction")
public class WorkWithSpooledFilesDebugPopupAction implements IViewActionDelegate, IActionDelegate2 {

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

            Iterator<?> selectionIterator = structuredSelection.iterator();
            while (selectionIterator.hasNext()) {

                DebuggeeProcess debuggeeProcess = getDebuggeeProcess(selectionIterator.next());
                if (debuggeeProcess != null) {

                    String connectionName;
                    try {
                        connectionName = IBMiDebugHelper.getConnectionName(debuggeeProcess);
                    } catch (UnknownHostException e) {
                        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                        return;
                    }

                    if (StringHelper.isNullOrEmpty(connectionName)) {
                        MessageDialog.openError(getShell(), Messages.E_R_R_O_R,
                            Messages.bind(Messages.Connection_not_found_A, IBMiDebugHelper.getHostName(debuggeeProcess)));
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
                            openWorkWithSpooledFilesView(connectionName, qualifiedJobName, page, isPinned);
                        }
                    }
                }
            }
        }
    }

    protected void openWorkWithSpooledFilesView(String connectionName, QualifiedJobName qualifiedJobName, IWorkbenchPage page, boolean isPinned) {

        try {

            AbstractWorkWithSpooledFilesInputData inputData = new WorkWithSpooledFilesJobInputData(connectionName, qualifiedJobName);

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

        DebuggeeProcess debuggeeProcess = getFirstDebuggeeProcess(selection);

        if (debuggeeProcess != null) {
            structuredSelection = ((IStructuredSelection)selection);
            action.setEnabled(true);
        } else {
            structuredSelection = null;
            action.setEnabled(false);
        }
    }

    private DebuggeeProcess getFirstDebuggeeProcess(ISelection selection) {

        if (selection instanceof StructuredSelection) {
            StructuredSelection structuredSelection = (StructuredSelection)selection;
            Object firstObject = structuredSelection.getFirstElement();
            return getDebuggeeProcess(firstObject);
        }

        return null;
    }

    private DebuggeeProcess getDebuggeeProcess(Object selectedObject) {

        if (isIBMiJob(selectedObject)) {
            return (DebuggeeProcess)selectedObject;
        }

        return null;
    }

    private String getJobName(IProcess debuggeeProcess) {
        return debuggeeProcess.getAttribute(null);
    }

    private boolean isIBMiJob(Object object) {

        if ((object instanceof IProcess)) {
            IProcess process = (IProcess)object;
            if (QualifiedJobName.parse(process.getAttribute(null)) != null) {
                return true;
            }
        }

        return false;
    }

    public void init(IAction action) {
        return;
    }

    public void init(IViewPart view) {
        this.shell = view.getSite().getShell();
    }

    public void dispose() {
    }

    private Shell getShell() {
        return shell;
    }
}
