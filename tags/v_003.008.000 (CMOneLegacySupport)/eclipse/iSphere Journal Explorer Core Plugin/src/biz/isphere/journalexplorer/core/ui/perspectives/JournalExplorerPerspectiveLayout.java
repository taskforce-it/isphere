/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import biz.isphere.journalexplorer.core.ui.views.JournalEntryDetailsView;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;
import biz.isphere.journalexplorer.rse.shared.ui.perspectives.IJournalExplorerPerspectiveLayout;

public class JournalExplorerPerspectiveLayout implements IPerspectiveFactory, IJournalExplorerPerspectiveLayout {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout";//$NON-NLS-1$

    private static final String NAV_FOLDER_ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout.NavFolder";//$NON-NLS-1$
    private static final String PROPS_FOLDER_ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout.PropsFolder";//$NON-NLS-1$
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
        folder = layout.createFolder(NAV_FOLDER_ID, IPageLayout.LEFT, 0.2F, editorArea);
        folder.addView(REMOTE_SYSTEMS_VIEW_ID);

        // Place journal entry details to right of journal explorer view.
        folder = layout.createFolder(JOURNAL_ENTRY_DETAILS_FOLDER_ID, IPageLayout.RIGHT, 0.7F, editorArea);
        folder.addView(JournalEntryDetailsView.ID);

        // Place journal entries view below remote system view.
        folder = layout.createFolder(PROPS_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, NAV_FOLDER_ID);
        folder.addView("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$

        // Place journal explorer view below editor area.
        folder = layout.createFolder(JOURNAL_EXPLORER_FOLDER_ID, IPageLayout.BOTTOM, 0.0F, editorArea);
        folder.addView(JournalExplorerView.ID);

        // Place journal entries view below journal explorer view.
        folder = layout.createFolder(JOURNAL_ENTRIES_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, JournalExplorerView.ID);
        folder.addView(JournalEntryViewerView.ID);

        // Place command log view below journal explorer view.
        folder.addView(COMMAND_LOG_VIEW_ID);

        layout.addShowViewShortcut(REMOTE_SYSTEMS_VIEW_ID);
        layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
        layout.addShowViewShortcut(JournalExplorerView.ID);
        layout.addShowViewShortcut(JournalEntryViewerView.ID);
        layout.addShowViewShortcut(JournalEntryDetailsView.ID);

        layout.addPerspectiveShortcut(JournalExplorerPerspectiveLayout.ID);

        layout.setEditorAreaVisible(false);
    }
}