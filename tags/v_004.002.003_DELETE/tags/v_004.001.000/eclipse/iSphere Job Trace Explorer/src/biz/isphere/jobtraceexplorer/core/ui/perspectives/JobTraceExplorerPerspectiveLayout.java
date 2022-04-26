/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import biz.isphere.jobtraceexplorer.core.ui.views.JobTraceExplorerView;
import biz.isphere.jobtraceexplorer.rse.shared.ui.perspectives.IJobTraceExplorerPerspectiveLayout;

public class JobTraceExplorerPerspectiveLayout implements IPerspectiveFactory, IJobTraceExplorerPerspectiveLayout {

    public static final String ID = "biz.isphere.jobtraceexplorer.core.ui.perspectives.JobTraceExplorerPerspectiveLayout";//$NON-NLS-1$

    private static final String NAV_FOLDER_ID = "biz.isphere.jobtraceexplorer.core.ui.perspectives.JobTraceExplorerPerspectiveLayout.NavFolder";//$NON-NLS-1$
    private static final String PROPS_FOLDER_ID = "biz.isphere.jobtraceexplorer.core.ui.perspectives.JobTraceExplorerPerspectiveLayout.PropsFolder";//$NON-NLS-1$
    private static final String JOB_TRACE_EXPLORER_FOLDER_ID = "biz.isphere.jobtraceexplorer.core.ui.perspectives.JobTraceExplorerPerspectiveLayout.JobTraceExplorerFolder";//$NON-NLS-1$
    private static final String CMDLOG_FOLDER_ID = "biz.isphere.jobtraceexplorer.core.ui.perspectives.JobTraceExplorerPerspectiveLayout.CmdLogFolder";//$NON-NLS-1$

    public void createInitialLayout(IPageLayout layout) {

        defineLayout(layout);
    }

    private void defineLayout(IPageLayout layout) {

        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        IFolderLayout folder;

        // Place remote system view to left of job trace explorer view.
        folder = layout.createFolder(NAV_FOLDER_ID, IPageLayout.LEFT, 0.2F, editorArea);
        folder.addView(REMOTE_SYSTEMS_VIEW_ID);

        // Place job trace properties view below remote system view.
        folder = layout.createFolder(PROPS_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, NAV_FOLDER_ID);
        folder.addView("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$

        // Place job trace explorer view below editor area.
        folder = layout.createFolder(JOB_TRACE_EXPLORER_FOLDER_ID, IPageLayout.BOTTOM, 0.0F, editorArea);
        folder.addView(JobTraceExplorerView.ID);

        // Place command log view below job trace explorer view.
        folder = layout.createFolder(CMDLOG_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, JobTraceExplorerView.ID);
        folder.addView(COMMAND_LOG_VIEW_ID);

        layout.addShowViewShortcut(REMOTE_SYSTEMS_VIEW_ID);
        layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
        layout.addShowViewShortcut(JobTraceExplorerView.ID);

        layout.addPerspectiveShortcut(JobTraceExplorerPerspectiveLayout.ID);

        layout.setEditorAreaVisible(false);
    }
}