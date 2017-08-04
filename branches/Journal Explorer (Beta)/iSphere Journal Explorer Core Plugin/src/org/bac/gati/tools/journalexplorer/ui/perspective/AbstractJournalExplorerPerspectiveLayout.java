package org.bac.gati.tools.journalexplorer.ui.perspective;

import org.bac.gati.tools.journalexplorer.ui.views.JournalEntryDetailsView;
import org.bac.gati.tools.journalexplorer.ui.views.JournalEntryView;
import org.bac.gati.tools.journalexplorer.ui.views.JournalExplorerView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public abstract class AbstractJournalExplorerPerspectiveLayout implements IPerspectiveFactory {

    public static final String ID = "org.bac.gati.tools.journalexplorer.ui.perspective.JournalExplorerPerspectiveLayout";//$NON-NLS-1$

    private static final String NAV_FOLDER_ID = "org.bac.gati.tools.journalexplorer.ui.perspective.JournalExplorerPerspectiveLayout.NavFolder";//$NON-NLS-1$
    private static final String PROPS_FOLDER_ID = "org.bac.gati.tools.journalexplorer.ui.perspective.JournalExplorerPerspectiveLayout.PropsFolder";//$NON-NLS-1$
    private static final String JOURNAL_EXPLORER_ID = "org.bac.gati.tools.journalexplorer.ui.perspective.JournalExplorerPerspectiveLayout.JournalExplorerFolder";//$NON-NLS-1$
    private static final String JOURNAL_ENTRIES_FOLDER_ID = "org.bac.gati.tools.journalexplorer.ui.perspective.JournalExplorerPerspectiveLayout.JournalEntriesFolder";//$NON-NLS-1$
    private static final String JOURNAL_ENTRY_DETAILS_FOLDER_ID = "org.bac.gati.tools.journalexplorer.ui.perspective.JournalExplorerPerspectiveLayout.JournalEntryDetailsFolder";//$NON-NLS-1$

    public void createInitialLayout(IPageLayout layout) {

        defineLayout(layout);
    }

    private void defineLayout(IPageLayout layout) {

        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        IFolderLayout folder;

        // Place remote system view to left of journal explorer view.
        folder = layout.createFolder(NAV_FOLDER_ID, IPageLayout.LEFT, 0.15F, editorArea);
        folder.addView(getRemoveSystemsViewID());

        // Place journal entry details to right of journal explorer view.
        folder = layout.createFolder(JOURNAL_ENTRY_DETAILS_FOLDER_ID, IPageLayout.RIGHT, 0.7F, editorArea);
        folder.addView(JournalEntryDetailsView.ID);

        // Place journal entries view below remote system view.
        folder = layout.createFolder(PROPS_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, NAV_FOLDER_ID);
        folder.addView("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$

        // Place journal explorer view below editor area.
        folder = layout.createFolder(JOURNAL_EXPLORER_ID, IPageLayout.BOTTOM, 0.0F, editorArea);
        folder.addView(JournalExplorerView.ID);

        // Place journal entries view below journal explorer view.
        folder = layout.createFolder(JOURNAL_ENTRIES_FOLDER_ID, IPageLayout.BOTTOM, 0.75F, JournalExplorerView.ID);
        folder.addView(JournalEntryView.ID);

        layout.addShowViewShortcut(getRemoveSystemsViewID());
        layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet"); //$NON-NLS-1$
        layout.addShowViewShortcut(JournalExplorerView.ID);
        layout.addShowViewShortcut(JournalEntryView.ID);
        layout.addShowViewShortcut(JournalEntryDetailsView.ID);

        layout.addPerspectiveShortcut(ID);
        
        layout.setEditorAreaVisible(false);
    }

    protected abstract String getRemoveSystemsViewID();

    protected abstract String getCommandLogViewID();
}