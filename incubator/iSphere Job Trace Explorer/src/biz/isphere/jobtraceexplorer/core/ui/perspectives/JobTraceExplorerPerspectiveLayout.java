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

    private static final String NAV_FOLDER_ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout.NavFolder";//$NON-NLS-1$
    private static final String PROPS_FOLDER_ID = "biz.isphere.jobtraceexplorer.core.ui.perspective.JobTraceExplorerPerspectiveLayout.PropsFolder";//$NON-NLS-1$
    private static final String JOURNAL_EXPLORER_FOLDER_ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout.JournalExplorerFolder";//$NON-NLS-1$
    private static final String JOURNAL_ENTRIES_FOLDER_ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout.JournalEntriesFolder";//$NON-NLS-1$
    private static final String JOURNAL_ENTRY_DETAILS_FOLDER_ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout.JournalEntryDetailsFolder";//$NON-NLS-1$

    public void createInitialLayout(IPageLayout layout) {

        defineLayout(layout);
    }

    private void defineLayout(IPageLayout layout) {

        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        IFolderLayout folder;

        // Place remote system view to left of journal explorer view.
        folder = layout.createFolder(NAV_FOLDER_ID, IPageLayout.LEFT, 0.15F, editorArea);
        folder.addView(REMOTE_SYSTEMS_VIEW_ID);

        // Place journal entry details to right of journal explorer view.
        // folder = layout.createFolder(JOURNAL_ENTRY_DETAILS_FOLDER_ID,
        // IPageLayout.RIGHT, 0.7F, editorArea);
        // folder.addView(JournalEntryDetailsView.ID);

        // Place journal entries view below remote system view.
        folder = layout.createFolder(PROPS_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, NAV_FOLDER_ID);
        folder.addView("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$

        // Place journal explorer view below editor area.
        folder = layout.createFolder(JOURNAL_EXPLORER_FOLDER_ID, IPageLayout.BOTTOM, 0.0F, editorArea);
        folder.addView(JobTraceExplorerView.ID);

        // Place journal entries view below journal explorer view.
        // folder = layout.createFolder(JOURNAL_ENTRIES_FOLDER_ID,
        // IPageLayout.BOTTOM, 0.75F, JournalExplorerView.ID);
        // folder.addView(JournalEntryViewerView.ID);

        // Place command log view below journal explorer view.
        folder.addView(COMMAND_LOG_VIEW_ID);

        layout.addShowViewShortcut(REMOTE_SYSTEMS_VIEW_ID);
        layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
        layout.addShowViewShortcut(JobTraceExplorerView.ID);
        // layout.addShowViewShortcut(JournalEntryViewerView.ID);
        // layout.addShowViewShortcut(JournalEntryDetailsView.ID);

        layout.addPerspectiveShortcut(JobTraceExplorerPerspectiveLayout.ID);

        layout.setEditorAreaVisible(false);
    }
}