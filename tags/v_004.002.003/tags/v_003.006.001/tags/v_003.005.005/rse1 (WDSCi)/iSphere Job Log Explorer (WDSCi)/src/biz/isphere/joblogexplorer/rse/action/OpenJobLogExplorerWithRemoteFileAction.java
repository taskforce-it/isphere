/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

import biz.isphere.joblogexplorer.rse.jobs.LoadIRemoteFileJob;

import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;
import com.ibm.etools.systems.subsystems.IRemoteFile;

public class OpenJobLogExplorerWithRemoteFileAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

    public static final String ID = "biz.isphere.joblogexplorer.rse.action.OpenJobLogExplorerWithRemoteFileAction"; //$NON-NLS-1$

    protected void execute(Object object) {

        if (object instanceof IRemoteFile) {
            IRemoteFile remoteFile = (IRemoteFile)object;
            LoadIRemoteFileJob job = new LoadIRemoteFileJob(remoteFile);
            job.run();
        }
    }

    @Override
    public void run() {

        Object[] selection = getSelectedRemoteObjects();
        for (int i = 0; i < selection.length; i++) {
            execute(selection[i]);
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);
    }

}
