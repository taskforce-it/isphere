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

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.JournalEntryComparator;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;

public abstract class CompareJournalPropertiesAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_COMPARE;

    private JournalProperties[] selectedItems;

    public CompareJournalPropertiesAction() {
        super(Messages.JournalEntryView_CompareEntries);

        setImageDescriptor(ISphereJournalExplorerCorePlugin.getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getImage(IMAGE);
    }

    public void setSelectedItems(StructuredSelection selection) {

        List<JournalProperties> selectedItems = new ArrayList<JournalProperties>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (object instanceof JournalProperties) {
                selectedItems.add((JournalProperties)object);
            }
        }

        this.selectedItems = selectedItems.toArray(new JournalProperties[selectedItems.size()]);
    }

    public void setSelectedItems(JournalProperties[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    @Override
    public void run() {
        performCompareJournalProperties(selectedItems);
        postRunAction();
    }

    protected void performCompareJournalProperties(JournalProperties[] selectedItems) {

        if (selectedItems == null || selectedItems.length != 2) {
            return;
        }

        new JournalEntryComparator().compare(selectedItems[0], selectedItems[1]);
    }

    protected abstract void postRunAction();
}
