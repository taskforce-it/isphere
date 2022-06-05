/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import java.util.Iterator;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.core.internal.QualifiedJobName;
import biz.isphere.rse.Messages;

import com.ibm.debug.pdt.internal.core.model.DebuggeeProcess;

@SuppressWarnings("restriction")
public class CopyQualifiedJobNameDebugPopupAction implements IViewActionDelegate {

    private static final String LINE_FEED = "\n";

    private Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {

        StringBuilder buffer = new StringBuilder();

        if (structuredSelection != null && !structuredSelection.isEmpty()) {

            Iterator<?> selectionIterator = structuredSelection.iterator();
            while (selectionIterator.hasNext()) {

                DebuggeeProcess debuggeeProcess = getDebuggeeProcess(selectionIterator.next());
                if (debuggeeProcess != null) {

                    String qualifiedJobNameAttr = getJobName(debuggeeProcess);
                    QualifiedJobName qualifiedJobName = QualifiedJobName.parse(qualifiedJobNameAttr);
                    if (qualifiedJobName == null) {
                        MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.bind(Messages.Invalid_job_name_A, qualifiedJobNameAttr));
                        return;
                    }

                    if (buffer.length() > 0) {
                        buffer.append(LINE_FEED);
                    }
                    buffer.append(qualifiedJobName.getQualifiedJobName());
                }
            }
        }

        if (buffer.length() > 0) {
            ClipboardHelper.setText(buffer.toString());
        }
    }

    private String getJobName(IProcess debuggeeProcess) {
        return debuggeeProcess.getAttribute(null);
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

    private boolean isIBMiJob(Object object) {

        if ((object instanceof IProcess)) {
            IProcess process = (IProcess)object;
            if (QualifiedJobName.parse(process.getAttribute(null)) != null) {
                return true;
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
