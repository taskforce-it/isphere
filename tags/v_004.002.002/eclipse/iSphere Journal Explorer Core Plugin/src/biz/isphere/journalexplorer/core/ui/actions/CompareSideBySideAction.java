/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.dialogs.CompareSideBySideDialog;

public class CompareSideBySideAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_HORIZONTAL_RESULTS_VIEW;

    private Shell shell;
    private JournalEntry[] selectedItems;

    public CompareSideBySideAction(Shell shell) {
        super(Messages.JournalEntryView_CompareSideBySide);

        this.shell = shell;

        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    /**
     * Called by the JournalEntryMenuAdapter and JournalPropertiesMenuAdapter
     * when the "Compare side by side" or "Compare entries" option was selected.
     */
    public void setSelectedItems(StructuredSelection selection) {

        List<JournalEntry> selectedItems = new ArrayList<JournalEntry>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (object instanceof JournalEntry) {
                JournalEntry journalEntry = (JournalEntry)object;
                selectedItems.add(journalEntry);
            } else if (object instanceof JournalProperties) {
                JournalEntry journalEntry = ((JournalProperties)object).getJournalEntry();
                selectedItems.add(journalEntry);
            }
        }

        this.selectedItems = selectedItems.toArray(new JournalEntry[selectedItems.size()]);
    }

    /**
     * Called by the JournalEntryViewerView, when the selected items has been
     * changed.
     */
    public void setSelectedItems(JournalEntry[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    @Override
    public void run() {
        performCompareSideBySideEntries(selectedItems);
    }

    protected void performCompareSideBySideEntries(JournalEntry[] selectedItems) {

        if (selectedItems == null || selectedItems.length != 2) {
            return;
        }

        CompareSideBySideDialog sideBySideCompareDialog = new CompareSideBySideDialog(shell);
        sideBySideCompareDialog.create();

        sideBySideCompareDialog.setInput(new JournalProperties(selectedItems[0]), new JournalProperties(selectedItems[1]));
        sideBySideCompareDialog.open();
    }
}
