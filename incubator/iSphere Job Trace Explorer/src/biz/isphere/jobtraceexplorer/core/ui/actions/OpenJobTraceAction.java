/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.ui.dialogs.OpenJobTraceSessionDialog;

public abstract class OpenJobTraceAction extends Action {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_OPEN_JOB_TRACE_SESSION;

    private Shell shell;
    private JobTraceSession jobTraceSession;

    public OpenJobTraceAction(Shell shell) {
        super(Messages.JobTraceExplorerView_OpenJobTraceSession);

        this.shell = shell;

        setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        performOpenJournalOutputFile();
        postRunAction();
    }

    public JobTraceSession getJobTraceSession() {
        return jobTraceSession;
    }

    private void performOpenJournalOutputFile() {

        OpenJobTraceSessionDialog openJournalOutputFileDialog = new OpenJobTraceSessionDialog(shell);
        openJournalOutputFileDialog.create();
        int result = openJournalOutputFileDialog.open();

        jobTraceSession = null;

        if (result == Window.OK) {
            if (ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                openJournalOutputFileDialog.getConnectionName())) {
                jobTraceSession = new JobTraceSession(openJournalOutputFileDialog.getConnectionName(), openJournalOutputFileDialog.getLibraryName(),
                    openJournalOutputFileDialog.getSessionID());
                jobTraceSession.setExcludeIBMData(openJournalOutputFileDialog.isIBMDataExcluded());
                jobTraceSession.setWhereClause(""); // //$NON-NLS-1$
            }
        }
    }

    protected abstract void postRunAction();
}
