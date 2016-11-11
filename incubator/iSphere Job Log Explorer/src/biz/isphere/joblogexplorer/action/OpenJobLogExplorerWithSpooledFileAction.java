/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import biz.isphere.joblogexplorer.jobs.LoadIRemoteFileJob;
import biz.isphere.joblogexplorer.jobs.LoadRemoteSpooledFileJob;
import biz.isphere.rse.spooledfiles.SpooledFileResource;

public class OpenJobLogExplorerWithSpooledFileAction implements IWorkbenchWindowActionDelegate, IObjectActionDelegate {

    public static final String ID = "biz.isphere.joblogexplorer.action.OpenJobLogExplorerWithSpooledFileAction"; //$NON-NLS-1$

    protected Shell shell;
    private IStructuredSelection structuredSelection;

    public void run(IAction action) {

        if (structuredSelection == null || structuredSelection.isEmpty()) {
            return;
        }

        Iterator<?> iterator = structuredSelection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof SpooledFileResource) {
                SpooledFileResource spooledFileResource = (SpooledFileResource)object;
                LoadRemoteSpooledFileJob job = new LoadRemoteSpooledFileJob(spooledFileResource);
                job.schedule();
            } else if (object instanceof IRemoteFile) {
                IRemoteFile remoteFile = (IRemoteFile)object;
                LoadIRemoteFileJob job = new LoadIRemoteFileJob(remoteFile);
                job.run();
            }
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
    }

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.shell = window.getShell();
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
        this.shell = workbenchPart.getSite().getShell();
    }
}
