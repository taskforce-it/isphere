/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.spooledfiles.ConfirmDeletionSpooledFiles;
import biz.isphere.core.spooledfiles.SpooledFile;

public class SpooledFileDeleteAction extends AbstractSpooledFileAction {

    private ArrayList<SpooledFileResource> spooledFileResources;

    @Override
    public void init() {
        spooledFileResources = new ArrayList<SpooledFileResource>();
    }

    @Override
    public String execute(SpooledFileResource spooledFileResource) {

        spooledFileResource.getSpooledFile().setData(spooledFileResource);

        spooledFileResources.add(spooledFileResource);

        return null;

    }

    @Override
    public String finish() {

        ArrayList<SpooledFile> spooledFiles = new ArrayList<SpooledFile>();
        for (SpooledFileResource resource : spooledFileResources) {
            spooledFiles.add(resource.getSpooledFile());
        }

        ConfirmDeletionSpooledFiles dialog = new ConfirmDeletionSpooledFiles(getShell(), spooledFiles.toArray(new SpooledFile[spooledFiles.size()]));
        if (dialog.open() == Dialog.OK) {

            DeletePostRun postRun = new DeletePostRun();
            postRun.setWorkbenchWindow(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

            new DeleteExec().execute(spooledFileResources, postRun);
        }

        return null;
    }

}