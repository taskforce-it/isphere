/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IDirectoryDialog;
import biz.isphere.rse.Messages;
import biz.isphere.rse.spooledfiles.SaveSpooledFilesToDirectoryExec;
import biz.isphere.rse.spooledfiles.SpooledFileResource;

/**
 * This class handles requests for deleting spooled files. It had been
 * introduced upon a request for enabling the 'Delete' key to delete spooled
 * files from an iSphere spooled file subsystem. Before that class existed,
 * spooled files were deleted by the 'SpooledFileDeleteAction'.
 */
public class SaveSpooledFilesToDirectoryHandler extends AbstractHandler implements IHandler {

    public static final String ID = "biz.isphere.rse.handler.SaveSpooledFilesToDirectoryHandler";

    private Shell shell;
    private String format;

    /**
     * This constructor is called by the Eclipse framework to construct a new
     * DeleteSpooledFileHandler object.
     */
    public SaveSpooledFilesToDirectoryHandler() {
        super();
    }

    /**
     * This constructor is called by the iSphere 'SpooledFilesExportAs*Action'
     * to export the selected spooled files.
     */
    public SaveSpooledFilesToDirectoryHandler(Shell shell, String format) {
        super();

        setShell(shell);
        setFormat(format);
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

        ArrayList<SpooledFile> spooledFiles = new ArrayList<SpooledFile>();

        for (SpooledFileResource spooledFileResource : spooledFileResources) {
            spooledFiles.add(spooledFileResource.getSpooledFile());
        }

        exportSpooledFiles(spooledFiles.toArray(new SpooledFile[spooledFiles.size()]));

        return null;
    }

    public Object exportSpooledFiles(SpooledFile[] spooledFiles) {

        if (spooledFiles == null || spooledFiles.length == 0) {
            return null;
        }

        IDirectoryDialog dialog = WidgetFactory.getDirectoryDialog(shell);
        dialog.setText(Messages.Select_directory);
        dialog.setFilterPath(Preferences.getInstance().getSpooledFileSaveDirectory());

        String directory = dialog.open();
        if (directory != null) {
            new SaveSpooledFilesToDirectoryExec().execute(spooledFiles, format, directory);
        }

        return null;
    }

    private void setShell(ExecutionEvent event) {
        shell = HandlerUtil.getActivePart(event).getSite().getShell();
    }

    private void setShell(Shell shell) {
        this.shell = shell;
    }

    private void setFormat(String format) {
        this.format = format;
    }
}
