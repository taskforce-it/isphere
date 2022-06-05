/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.externalapi.Access;
import biz.isphere.jobtraceexplorer.core.ui.dialogs.OpenJobTraceSessionDialog;

public class OpenJobTraceAction extends Action {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_OPEN_JOB_TRACE_SESSION;

    private Shell shell;

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
    }

    private void performOpenJournalOutputFile() {

        OpenJobTraceSessionDialog openJournalOutputFileDialog = new OpenJobTraceSessionDialog(shell);
        openJournalOutputFileDialog.create();

        if (openJournalOutputFileDialog.open() == Window.OK) {
            if (ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                openJournalOutputFileDialog.getConnectionName())) {

                String connectionName = openJournalOutputFileDialog.getConnectionName();
                String libraryName = openJournalOutputFileDialog.getLibraryName();
                String sessionID = openJournalOutputFileDialog.getSessionID();
                boolean isIBMDataExcluded = openJournalOutputFileDialog.isIBMDataExcluded();

                try {
                    Access.openJobTraceExplorer(shell, connectionName, libraryName, sessionID, isIBMDataExcluded);
                } catch (Exception e) {
                    MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
                }
            }
        }
    }
}
