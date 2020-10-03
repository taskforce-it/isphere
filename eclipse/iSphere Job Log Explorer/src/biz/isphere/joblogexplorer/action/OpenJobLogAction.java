/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.jobs.rse.LoadRemoteJobLogJob;
import biz.isphere.joblogexplorer.ui.dialogs.SelectJobDialog;

import com.ibm.etools.iseries.comm.interfaces.ISeriesJobName;

public class OpenJobLogAction extends Action {

    public static final String ID = "biz.isphere.joblogexplorer.action.OpenJobLogAction"; //$NON-NLS-1$
    private static final String IMAGE = ISphereJobLogExplorerPlugin.IMAGE_OPEN_JOB_LOG;

    private Shell shell;

    public OpenJobLogAction(Shell shell) {
        super(Messages.Explore_job_log, Action.AS_PUSH_BUTTON);
        setToolTipText(Messages.Explore_job_log);
        setImageDescriptor(ISphereJobLogExplorerPlugin.getDefault().getImageDescriptor(IMAGE));
        setId(ID);

        this.shell = shell;
    }

    public Image getImage() {
        return ISphereJobLogExplorerPlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void runWithEvent(Event event) {

        SelectJobDialog dialog = new SelectJobDialog(shell);
        if (dialog.open() == IDialogConstants.OK_ID) {

            String connectionName = dialog.getConnectionName();
            ISeriesJobName jobName = new ISeriesJobName(dialog.getJobName(), dialog.getUserName(), dialog.getJobNumber());
            LoadRemoteJobLogJob job = new LoadRemoteJobLogJob(connectionName, jobName.getName(), jobName.getUser(), jobName.getNumber());
            job.run();
        }
    }
}
