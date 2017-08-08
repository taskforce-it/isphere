/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.JournalEntryComparator;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JOESDProperty;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalPropertiesContentProvider;
import biz.isphere.journalexplorer.core.ui.dialogs.ConfigureParsersDialog;
import biz.isphere.journalexplorer.core.ui.dialogs.SelectEntriesToCompareDialog;
import biz.isphere.journalexplorer.core.ui.dialogs.SideBySideCompareDialog;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryDetailsViewer;

public class JournalEntryView extends ViewPart implements ISelectionListener, ISelectionChangedListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalEntryView"; //$NON-NLS-1$

    private JournalEntryDetailsViewer viewer;

    private Action compare;
    private Action showSideBySide;
    private Action reParseJournalEntries;

    private SelectionProviderIntermediate selectionProviderIntermediate;

    public JournalEntryView() {
        this.selectionProviderIntermediate = new SelectionProviderIntermediate();
    }

    @Override
    public void createPartControl(Composite parent) {

        viewer = new JournalEntryDetailsViewer(parent);
        viewer.addSelectionChangedListener(this);
        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
        createActions();
        createToolBar();
        setActionEnablement(viewer.getSelection());

        viewer.setAsSelectionProvider(selectionProviderIntermediate);
        getSite().setSelectionProvider(selectionProviderIntermediate);
    }

    @Override
    public void dispose() {
        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.removeSelectionListener(this);

        super.dispose();
    };

    private void createActions() {

        // /
        // / Compare action
        // /
        compare = new Action(Messages.JournalEntryView_CompareEntries) {
            @Override
            public void run() {
                performCompareJOESDEntries();
            }
        };

        compare.setImageDescriptor(ISphereJournalExplorerCorePlugin.getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_COMPARE));

        // /
        // / showSideBySide action
        // /
        showSideBySide = new Action(Messages.JournalEntryView_ShowSideBySide) {
            @Override
            public void run() {
                performShowSideBySideEntries();
            }
        };

        showSideBySide.setImageDescriptor(ISphereJournalExplorerCorePlugin
            .getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_HORIZONTAL_RESULTS_VIEW));

        // /
        // / reParseEntries action
        // /
        reParseJournalEntries = new Action(Messages.JournalEntryView_ReloadEntries) {
            @Override
            public void run() {
                performReparseJournalEntries();
            }
        };

        reParseJournalEntries.setImageDescriptor(ISphereJournalExplorerCorePlugin.getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_REFRESH));
    }

    protected void performShowSideBySideEntries() {

        Object[] input = getSelectedItems();

        if (input instanceof Object[]) {

            SideBySideCompareDialog sideBySideCompareDialog = new SideBySideCompareDialog(getSite().getShell());
            sideBySideCompareDialog.create();

            if (input.length == 2) {
                sideBySideCompareDialog.setInput((JournalProperties)input[0], (JournalProperties)input[1]);
                sideBySideCompareDialog.open();
            } else {

                SelectEntriesToCompareDialog selectEntriesToCompareDialog = new SelectEntriesToCompareDialog(getSite().getShell());
                selectEntriesToCompareDialog.create();
                selectEntriesToCompareDialog.setInput(input);

                if (selectEntriesToCompareDialog.open() == Window.OK) {
                    sideBySideCompareDialog.setInput((JournalProperties)selectEntriesToCompareDialog.getLeftEntry(),
                        (JournalProperties)selectEntriesToCompareDialog.getRightEntry());

                    sideBySideCompareDialog.open();
                }
            }
        }
    }

    protected void performConfigureParsers() {

        ConfigureParsersDialog configureParsersDialog = new ConfigureParsersDialog(getSite().getShell());
        configureParsersDialog.create();
        configureParsersDialog.open();
    }

    private void performReparseJournalEntries() {

        reParseAllEntries();
        viewer.refresh(true);
    }

    private void reParseAllEntries() {

        Object[] input = getInput();

        for (Object inputElement : input) {

            JournalProperties journalProperties = (JournalProperties)inputElement;
            ((JOESDProperty)journalProperties.getJOESDProperty()).executeParsing();
        }
    }

    private Object[] getInput() {

        JournalPropertiesContentProvider journalPropertiesContentProvider = (JournalPropertiesContentProvider)viewer.getContentProvider();
        Object[] input = journalPropertiesContentProvider.getElements(null);

        return input;
    }

    protected void performCompareJOESDEntries() {

        Object[] input = getSelectedItems();

        if (input instanceof Object[]) {

            if (input.length == 2) {
                compareEntries(input[0], input[1]);
            } else {

                SelectEntriesToCompareDialog selectEntriesToCompareDialog = new SelectEntriesToCompareDialog(getSite().getShell());
                selectEntriesToCompareDialog.create();
                selectEntriesToCompareDialog.setInput(input);

                if (selectEntriesToCompareDialog.open() == Window.OK) {
                    compareEntries(selectEntriesToCompareDialog.getLeftEntry(), selectEntriesToCompareDialog.getRightEntry());
                }
            }
        } else {
            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, Messages.JournalEntryView_UncomparableEntries);
        }
    }

    private void compareEntries(Object leftObject, Object rightObject) {

        if (leftObject instanceof JournalProperties && rightObject instanceof JournalProperties) {

            JournalProperties left = (JournalProperties)leftObject;
            JournalProperties right = (JournalProperties)rightObject;

            new JournalEntryComparator().compare(left, right);
            viewer.refresh(true);

        } else {
            MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, Messages.JournalEntryView_UncomparableEntries);
        }
    }

    private void createToolBar() {
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(compare);
        toolBarManager.add(showSideBySide);
        toolBarManager.add(new Separator());
        toolBarManager.add(reParseJournalEntries);
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

        if (viewPart instanceof JournalExplorerView) {

            if (selection instanceof IStructuredSelection) {

                structuredSelectionList = ((IStructuredSelection)selection).iterator();

                while (structuredSelectionList.hasNext()) {

                    structuredSelectionElement = ((IStructuredSelection)structuredSelectionList.next()).iterator();

                    while (structuredSelectionElement.hasNext()) {

                        currentSelection = structuredSelectionElement.next();

                        if (currentSelection instanceof JournalEntry) {
                            input.add(new JournalProperties((JournalEntry)currentSelection));
                        }
                    }
                }

                // Save tree state
                Object[] expandedElements = viewer.getExpandedElements();
                TreePath[] expandedTreePaths = viewer.getExpandedTreePaths();

                viewer.setInput(input.toArray());

                // Restore tree state
                viewer.setExpandedElements(expandedElements);
                viewer.setExpandedTreePaths(expandedTreePaths);
            }
        }

        setActionEnablement(viewer.getSelection());
    }

    public void selectionChanged(SelectionChangedEvent event) {

        ITreeSelection selection = getSelection(event);
        setActionEnablement(selection);
    }

    private void setActionEnablement(ISelection selection) {

        ITreeSelection treeSelection;
        if (selection instanceof ITreeSelection) {
            treeSelection = (ITreeSelection)selection;
        } else {
            treeSelection = null;
        }

        if (treeSelection != null) {
            if (treeSelection != null && treeSelection.size() == 2) {
                compare.setEnabled(true);
                showSideBySide.setEnabled(true);
            } else {
                showSideBySide.setEnabled(false);
                compare.setEnabled(false);
            }

            Object[] items = getInput();

            if (items != null && items.length > 0) {
                reParseJournalEntries.setEnabled(true);
            } else {
                reParseJournalEntries.setEnabled(false);
            }
        }
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
}