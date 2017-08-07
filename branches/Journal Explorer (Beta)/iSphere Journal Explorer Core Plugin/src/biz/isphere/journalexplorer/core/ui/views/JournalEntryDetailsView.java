/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryDetailsViewer;

public class JournalEntryDetailsView extends ViewPart implements ISelectionListener, ISelectionChangedListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalEntryDetailsView"; //$NON-NLS-1$

    private JournalEntryDetailsViewer viewer;

    public JournalEntryDetailsView() {
    }

    @Override
    public void createPartControl(Composite parent) {

        this.viewer = new JournalEntryDetailsViewer(parent);
        this.viewer.addSelectionChangedListener(this);
        this.getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        this.createActions();
        this.createToolBar();
        this.setActionEnablement(viewer.getSelection());
    }

    @Override
    public void dispose() {
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.removeSelectionListener(this);

        super.dispose();
    };

    private void createActions() {

    }

    private void createToolBar() {
        // no toolbar defined
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    // /
    // / ISelectionListener methods
    // /
    public void selectionChanged(IWorkbenchPart viewPart, ISelection selection) {

        @SuppressWarnings("rawtypes")
        Iterator structuredSelectionList;

        @SuppressWarnings("rawtypes")
        Iterator structuredSelectionElement;
        Object currentSelection;
        ArrayList<JournalProperties> input = new ArrayList<JournalProperties>();

        if (viewPart instanceof JournalExplorerView || viewPart instanceof JournalEntryView) {
            if (selection instanceof IStructuredSelection) {
                structuredSelectionList = ((IStructuredSelection)selection).iterator();
                while (input.size() < 2 && structuredSelectionList.hasNext()) {
                    structuredSelectionElement = ((IStructuredSelection)structuredSelectionList.next()).iterator();
                    while (input.size() < 2 && structuredSelectionElement.hasNext()) {
                        currentSelection = structuredSelectionElement.next();
                        if (currentSelection instanceof JournalEntry) {
                            Runnable loadPropertiesJob = new LoadPropertiesRunnable(input, (JournalEntry)currentSelection);
                            BusyIndicator.showWhile(getSite().getShell().getDisplay(), loadPropertiesJob);
                        } else if (currentSelection instanceof JournalProperties) {
                            input.add((JournalProperties)currentSelection);
                        }

                    }
                }

                if (input.size() > 1) {
                    input.clear();
                }

                refreshViewer(input);
            }
        }

        setActionEnablement(viewer.getSelection());
    }

    private void refreshViewer(ArrayList<JournalProperties> input) {

        this.viewer.setInput(input.toArray());

        // Restore tree state
        this.viewer.expandAll();
    }

    public void selectionChanged(SelectionChangedEvent event) {

        ITreeSelection selection = getSelection(event);
        setActionEnablement(selection);
    }

    private void setActionEnablement(ISelection selection) {

        // no actions implemented
    }

    private JournalProperties[] getSelectedItems() {

        List<JournalProperties> selectedItems = new ArrayList<JournalProperties>();

        ITreeSelection selection = getSelection();
        Iterator<?> iterator = selection.iterator();

        Object currentItem;
        while (iterator.hasNext()) {

            currentItem = iterator.next();
            if (currentItem instanceof JournalProperties) {
                selectedItems.add((JournalProperties)currentItem);
            }
        }

        return selectedItems.toArray(new JournalProperties[selectedItems.size()]);
    }

    private ITreeSelection getSelection() {

        ISelection selection = viewer.getSelection();
        if (selection instanceof ITreeSelection) {
            return (ITreeSelection)selection;
        }

        return null;
    }

    private ITreeSelection getSelection(SelectionChangedEvent event) {

        ISelection selection = event.getSelection();
        if (selection instanceof ITreeSelection) {
            return (ITreeSelection)selection;
        }

        return null;
    }

    private class LoadPropertiesRunnable implements Runnable {

        private JournalEntry journalEntry;
        private ArrayList<JournalProperties> input;

        public LoadPropertiesRunnable(ArrayList<JournalProperties> input, JournalEntry journalEntry) {
            this.input = input;
            this.journalEntry = journalEntry;
        }

        public void run() {
            input.add(new JournalProperties(journalEntry));
        }
    }
}