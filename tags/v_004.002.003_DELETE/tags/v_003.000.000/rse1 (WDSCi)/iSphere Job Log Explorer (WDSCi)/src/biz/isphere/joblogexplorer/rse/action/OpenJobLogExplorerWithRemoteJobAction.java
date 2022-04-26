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

import biz.isphere.joblogexplorer.rse.jobs.LoadRemoteJobLogJob;

import com.ibm.etools.iseries.comm.interfaces.ISeriesJobName;
import com.ibm.etools.iseries.core.api.ISeriesJob;
import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;
import com.ibm.etools.systems.dstore.core.model.DataElement;

public class OpenJobLogExplorerWithRemoteJobAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

    public static final String ID = "biz.isphere.joblogexplorer.rse.action.OpenJobLogExplorerWithRemoteJobAction"; //$NON-NLS-1$

    protected void execute(Object object) {

        if (object instanceof DataElement) { 
            DataElement dataElement = (DataElement)object;
            ISeriesJob remoteJob = new ISeriesJob( dataElement);
            String connectionName = remoteJob.getCommandSubSystem().getSystemConnectionName();
            ISeriesJobName jobName = new ISeriesJobName(remoteJob.getFullJobName());
            LoadRemoteJobLogJob job = new LoadRemoteJobLogJob(connectionName, jobName);
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
