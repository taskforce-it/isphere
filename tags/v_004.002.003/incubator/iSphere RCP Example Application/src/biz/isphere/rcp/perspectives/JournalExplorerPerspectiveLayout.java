package biz.isphere.rcp.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import biz.isphere.journalexplorer.core.ui.views.JournalEntryDetailsView;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

public class JournalExplorerPerspectiveLayout implements IPerspectiveFactory {

    public static final String ID = "biz.isphere.rcp.perspectives.JournalExplorerPerspectiveLayout";//$NON-NLS-1$

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

        layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
        layout.addShowViewShortcut(JournalExplorerView.ID);
        layout.addShowViewShortcut(JournalEntryViewerView.ID);
        layout.addShowViewShortcut(JournalEntryDetailsView.ID);

        layout.addPerspectiveShortcut(JournalExplorerPerspectiveLayout.ID);

        layout.setEditorAreaVisible(false);
    }
}