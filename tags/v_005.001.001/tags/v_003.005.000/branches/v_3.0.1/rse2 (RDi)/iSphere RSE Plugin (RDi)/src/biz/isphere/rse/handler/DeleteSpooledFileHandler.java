/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.handler;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import biz.isphere.core.spooledfiles.ConfirmDeletionSpooledFiles;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.rse.spooledfiles.DeleteExec;
import biz.isphere.rse.spooledfiles.DeletePostRun;
import biz.isphere.rse.spooledfiles.SpooledFileResource;

/**
 * This class handles requests for deleting spooled files. It had been
 * introduced upon a request for enabling the 'Delete' key to delete spooled
 * files from an iSphere spooled file subsystem. Before that class existed,
 * spooled files were deleted by the 'SpooledFileDeleteAction'.
 */
public class DeleteSpooledFileHandler extends AbstractHandler implements IHandler {

    public static final String ID = "biz.isphere.rse.handler.DeleteSpooledFileHandler";

    private Shell shell;

    /**
     * This constructor is called by the Eclipse framework to construct a new
     * DeleteSpooledFileHandler object.
     */
    public DeleteSpooledFileHandler() {
        super();
    }

    /**
     * This constructor is called by the iSphere 'SpooledFileDeleteAction' to
     * delete the selected spooled files.
     */
    public DeleteSpooledFileHandler(Shell shell) {
        super();

        setShell(shell);
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {

        setShell(event);

        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (!(selection instanceof StructuredSelection)) {
            return null;
        }

        ArrayList<SpooledFileResource> spooledFileResources = new ArrayList<SpooledFileResource>();

        StructuredSelection selectedSpooledFiles = (StructuredSelection)selection;
        for (Iterator<?> iterator = selectedSpooledFiles.iterator(); iterator.hasNext();) {
            Object selectedItem = iterator.next();
            if (selectedItem instanceof SpooledFileResource) {
                SpooledFileResource spooledFileResource = (SpooledFileResource)selectedItem;
                spooledFileResources.add(spooledFileResource);
            }
        }

        deleteSpooledFiles(spooledFileResources);

        return null;
    }

    public Object deleteSpooledFiles(ArrayList<SpooledFileResource> spooledFileResources) {

        if (spooledFileResources == null || spooledFileResources.size() == 0) {
            return null;
        }

        if (isConfirmed(spooledFileResources)) {
            DeletePostRun postRun = new DeletePostRun();
            postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
            new DeleteExec().execute(spooledFileResources, postRun);
        }

        return null;
    }

    private boolean isConfirmed(ArrayList<SpooledFileResource> spooledFileResources) {

        ArrayList<SpooledFile> spooledFiles = new ArrayList<SpooledFile>();

        for (SpooledFileResource spooledFileResource : spooledFileResources) {
            spooledFiles.add(spooledFileResource.getSpooledFile());
        }

        ConfirmDeletionSpooledFiles dialog = new ConfirmDeletionSpooledFiles(shell, spooledFiles.toArray(new SpooledFile[spooledFiles.size()]));
        if (dialog.open() == Dialog.OK) {
            return true;
        }

        return false;
    }

    private void setShell(ExecutionEvent event) {
        shell = HandlerUtil.getActivePart(event).getSite().getShell();
    }

    private void setShell(Shell shell) {
        this.shell = shell;
    }
}
