/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
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
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.core.internal.listener.PartCloseAdapter;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.actions.CollapseAllAction;
import biz.isphere.journalexplorer.core.ui.actions.CompareJournalPropertiesAction;
import biz.isphere.journalexplorer.core.ui.actions.CompareSideBySideAction;
import biz.isphere.journalexplorer.core.ui.actions.DisplayChangedFieldsOnlyAction;
import biz.isphere.journalexplorer.core.ui.actions.GenericRefreshAction;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalPropertiesContentProvider;
import biz.isphere.journalexplorer.core.ui.popupmenus.JournalPropertiesMenuAdapter;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryDetailsViewer;

/**
 * This view displays the journal entries that are selected on the tabs of the
 * "Journal Explorer" view.
 * 
 * @see JournalExplorerView
 * @see JournalEntriesViewerForOutputFilesTab
 */
public class JournalEntryViewerView extends ViewPart implements ISelectionListener, ISelectionChangedListener {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView"; //$NON-NLS-1$

    private static final String COLUMN_WIDTH = "COLUMN_WIDTH_"; //$NON-NLS-1$
    private final static String DISPLAY_CHANGED_RECORDS_ONLY = "DISPLAY_CHANGED_RECORDS_ONLY"; //$NON-NLS-1$

    private DialogSettingsManager dialogSettingsManager = null;

    private JournalEntryDetailsViewer viewer;

    private CollapseAllAction collapseAllAction;
    private CompareJournalPropertiesAction compareJournalPropertiesAction;
    private CompareSideBySideAction compareSideBySideAction;
    private DisplayChangedFieldsOnlyAction displayChangesOnlyAction;
    private GenericRefreshAction reParseJournalEntriesAction;

    private SelectionProviderIntermediate selectionProviderIntermediate;

    public JournalEntryViewerView() {
        this.selectionProviderIntermediate = new SelectionProviderIntermediate();
    }

    @Override
    public void createPartControl(Composite parent) {

        viewer = new JournalEntryDetailsViewer(parent);
        viewer.addSelectionChangedListener(this);

        getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);

        createActions();
        createToolBar();
        setActionEnablement(getSelection());

        Control control = viewer.getControl();
        if (control instanceof Tree) {
            Tree tree = (Tree)control;
            final Menu menuTableMembers = new Menu(tree);
            menuTableMembers.addMenuListener(new JournalPropertiesMenuAdapter(menuTableMembers, viewer));
            tree.setMenu(menuTableMembers);
        }

        viewer.setAsSelectionProvider(selectionProviderIntermediate);
        getSite().setSelectionProvider(selectionProviderIntermediate);

        loadScreenValues();

        addPartCloseListener(parent);
    }

    private void addPartCloseListener(Composite parent) {

        new PartCloseListener(getSite().getPage());
    }

    @Override
    public void dispose() {

        ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
        selectionService.removeSelectionListener(this);

        viewer.dispose();

        super.dispose();
    };

    private void createActions() {

        collapseAllAction = new CollapseAllAction(viewer);

        compareJournalPropertiesAction = new CompareJournalPropertiesAction(viewer);

        compareSideBySideAction = new CompareSideBySideAction(getSite().getShell());

        compareSideBySideAction.setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(
            ISphereJournalExplorerCorePlugin.IMAGE_HORIZONTAL_RESULTS_VIEW));

        displayChangesOnlyAction = new DisplayChangedFieldsOnlyAction(getSite().getShell()) {
            public void run() {
                viewer.setDisplayChangedFieldsOnly(displayChangesOnlyAction.isChecked());
                storeScreenValues();
            };
        };

        reParseJournalEntriesAction = new GenericRefreshAction() {
            @Override
            protected void postRunAction() {
                performReparseJournalEntries();
            }
        };
    }

    private void performReparseJournalEntries() {

        for (Object object : getInput()) {
            if (object instanceof JournalProperties) {
                JournalProperties journalProperties = (JournalProperties)object;
                journalProperties.getJOESDProperty().executeParsing();
            }
        }

        viewer.refresh(true);
    }

    private Object[] getInput() {

        JournalPropertiesContentProvider journalPropertiesContentProvider = (JournalPropertiesContentProvider)viewer.getContentProvider();
        Object[] input = journalPropertiesContentProvider.getElements(null);

        return input;
    }

    private void createToolBar() {
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(collapseAllAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(compareJournalPropertiesAction);
        toolBarManager.add(compareSideBySideAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(displayChangesOnlyAction);
        toolBarManager.add(new Separator());
        toolBarManager.add(reParseJournalEntriesAction);
    }

    @Override
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Called when the selected items has been changed. Updates the input of the
     * JournalEntryViewerView.
     */
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

                if (input.size() == 2 && sameTables(input.toArray())) {
                    viewer.setSelection(new StructuredSelection(input.toArray()));
                } else {
                    viewer.setSelection(null);
                }

                // Restore tree state
                viewer.setExpandedElements(expandedElements);
                viewer.setExpandedTreePaths(expandedTreePaths);

            }
        }

        setActionEnablement(getSelection());
    }

    private boolean sameTables(Object[] array) {

        String objectName = null;
        String objectLibrary = null;

        for (Object object : array) {
            if (object instanceof JournalProperties) {
                JournalProperties properties = (JournalProperties)object;
                if (objectName != null && objectLibrary != null) {
                    if (!objectName.equals(properties.getJournalEntry().getObjectName())
                        || !objectLibrary.equals(properties.getJournalEntry().getObjectLibrary())) {
                        return false;
                    }
                }
                objectName = properties.getJournalEntry().getObjectName();
                objectLibrary = properties.getJournalEntry().getObjectLibrary();
            }

        }

        return true;
    }

    public void selectionChanged(SelectionChangedEvent event) {

        ITreeSelection selection = getSelection(event);
        setActionEnablement(selection);

        List<JournalEntry> journalEntriesList = new ArrayList<JournalEntry>();
        List<JournalProperties> journalPropertiesList = new ArrayList<JournalProperties>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (object instanceof JournalProperties) {
                JournalProperties journalProperties = (JournalProperties)object;
                JournalEntry journalEntry = journalProperties.getJournalEntry();
                journalPropertiesList.add(journalProperties);
                journalEntriesList.add(journalEntry);
                if (journalEntriesList.size() > 2) {
                    journalEntriesList.clear();
                    break;
                }
            }
        }

        compareSideBySideAction.setSelectedItems(journalEntriesList.toArray(new JournalEntry[journalEntriesList.size()]));
        compareJournalPropertiesAction.setSelectedItems(journalPropertiesList.toArray(new JournalProperties[journalPropertiesList.size()]));
    }

    private void setActionEnablement(ITreeSelection selection) {

        JournalProperties[] journalProperties = getSelectedItems(selection);

        if (journalProperties.length == 2) {
            compareJournalPropertiesAction.setEnabled(true);
            compareSideBySideAction.setEnabled(true);
        } else {
            compareSideBySideAction.setEnabled(false);
            compareJournalPropertiesAction.setEnabled(false);
        }

        displayChangesOnlyAction.setEnabled(true);

        Object[] items = getInput();

        if (items != null && items.length > 0) {
            collapseAllAction.setEnabled(true);
            reParseJournalEntriesAction.setEnabled(true);
        } else {
            collapseAllAction.setEnabled(false);
            reParseJournalEntriesAction.setEnabled(false);
        }
    }

    private JournalProperties[] getSelectedItems(ITreeSelection selection) {

        List<JournalProperties> selectedItems = new ArrayList<JournalProperties>();

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

    private void storeScreenValues() {

        getDialogSettingsManager().storeValue(DISPLAY_CHANGED_RECORDS_ONLY, displayChangesOnlyAction.isChecked());

        Set<String> columnNames = viewer.getColumnNames();
        for (String columnName : columnNames) {
            int width = viewer.getColumnWidth(columnName);
            getDialogSettingsManager().storeValue(COLUMN_WIDTH + columnName, width);
        }
    }

    private void loadScreenValues() {

        displayChangesOnlyAction.setChecked(getDialogSettingsManager().loadBooleanValue(DISPLAY_CHANGED_RECORDS_ONLY, false));
        viewer.setDisplayChangedFieldsOnly(displayChangesOnlyAction.isChecked());

        Set<String> columnNames = viewer.getColumnNames();
        for (String columnName : columnNames) {
            int width = getDialogSettingsManager().loadIntValue(COLUMN_WIDTH + columnName, -1);
            if (width > 0) {
                viewer.setColumnWidth(columnName, width);
            }
        }
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISphereJournalExplorerCorePlugin.getDefault().getDialogSettings(),
                JournalEntryViewerView.class);
        }
        return dialogSettingsManager;
    }

    private class PartCloseListener extends PartCloseAdapter {

        public PartCloseListener(IWorkbenchPage page) {
            super(page);
        }

        public void partOpened(IWorkbenchPart paramIWorkbenchPart) {
        }

        public void partDeactivated(IWorkbenchPart paramIWorkbenchPart) {
        }

        public void partClosed(IWorkbenchPart paramIWorkbenchPart) {
            storeScreenValues();
            super.partClosed(paramIWorkbenchPart);
        }

        public void partBroughtToTop(IWorkbenchPart paramIWorkbenchPart) {
        }

        public void partActivated(IWorkbenchPart paramIWorkbenchPart) {
        }
    }
}