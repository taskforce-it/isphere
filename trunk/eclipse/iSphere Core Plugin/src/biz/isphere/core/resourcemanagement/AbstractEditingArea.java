/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractEditingArea extends Composite implements IEditingArea {

    protected static final String TABLE_RESOURCE_QUALIFIER = "Qualifier";

    private AbstractResource[] resources;
    private TableViewer tableViewerResources;
    private Table tableResources;
    private Button buttonDeselectAll;
    private Button[] buttonActions;
    private Button buttonUndoAction;

    private class LabelProviderResources extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == tableResources.getColumnCount() - 1) {
                String action = ((AbstractResource)element).getAction();
                if (action != null) {
                    return AbstractResource.getActionText(action);
                } else {
                    return "";
                }
            } else {
                return getTableColumnText(element, columnIndex);
            }
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return getTableColumnImage(element, columnIndex);
        }

        public Color getBackground(Object element, int columnIndex) {
            return null;
        }

        public Color getForeground(Object element, int columnIndex) {
            if (columnIndex == tableResources.getColumnCount() - 1) {
                String action = ((AbstractResource)element).getAction();
                if (action == null) {
                    return null;
                } else if (action.equals(AbstractResource.PUSH_TO_REPOSITORY) || action.equals(AbstractResource.PUSH_TO_WORKSPACE)) {
                    return Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
                } else if (action.equals(AbstractResource.DELETE_FROM_REPOSITORY) || action.equals(AbstractResource.DELETE_FROM_WORKSPACE)
                    || action.equals(AbstractResource.DELETE_FROM_BOTH)) {
                    return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private class ContentProviderResources implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return resources;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class SorterResources extends ViewerSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            return compareResources(e1, e2);
        }
    }

    public AbstractEditingArea(Composite parent, AbstractResource[] resources, boolean both) {
        this(parent, resources, both, null);
    }

    public AbstractEditingArea(Composite parent, AbstractResource[] resources, boolean both, Object qualifier) {
        super(parent, SWT.NONE);
        this.resources = resources;
        createDialog(both, qualifier);
    }

    private void createDialog(boolean both, Object qualifier) {

        setLayout(new GridLayout(2, false));

        Composite tableArea = new Composite(this, SWT.NONE);
        tableArea.setLayout(new GridLayout(1, false));
        tableArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewerResources = new TableViewer(tableArea, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewerResources.setLabelProvider(new LabelProviderResources());
        tableViewerResources.setContentProvider(new ContentProviderResources());
        tableViewerResources.setSorter(new SorterResources());
        tableViewerResources.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                AbstractResource[] selectedItems = getSelectedResources();
                if (selectedItems.length == 0) {
                    setButtonsEnabled(false);
                } else {
                    setButtonsEnabled(true);
                }
            }
        });

        tableResources = tableViewerResources.getTable();
        tableResources.setLinesVisible(true);
        tableResources.setHeaderVisible(true);
        tableResources.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        if (qualifier != null) {
            tableResources.setData(TABLE_RESOURCE_QUALIFIER, qualifier);
        }

        addTableColumns(tableResources);
        addTablePopupMenu(tableResources);

        TableColumn columnAction = new TableColumn(tableResources, SWT.NONE);
        columnAction.setWidth(Size.getSize(150));
        columnAction.setText(Messages.Action);

        Composite buttonArea = new Composite(this, SWT.NONE);
        buttonArea.setLayout(new GridLayout(1, false));
        buttonArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));

        Button buttonSelectAll = WidgetFactory.createPushButton(buttonArea);
        buttonSelectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        buttonSelectAll.setText(Messages.Select_all);
        buttonSelectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] objects = new Object[tableResources.getItemCount()];
                for (int idx = 0; idx < tableResources.getItemCount(); idx++) {
                    objects[idx] = tableViewerResources.getElementAt(idx);
                }
                tableViewerResources.setSelection(new StructuredSelection(objects), true);
                tableResources.setFocus();
            }
        });

        buttonDeselectAll = WidgetFactory.createPushButton(buttonArea);
        buttonDeselectAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        buttonDeselectAll.setText(Messages.Deselect_all);
        buttonDeselectAll.setEnabled(false);
        buttonDeselectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tableViewerResources.setSelection(new StructuredSelection(), true);
                tableResources.setFocus();
            }
        });

        String[] actions = getActions(both);
        Button buttonAction;
        buttonActions = new Button[actions.length];
        for (int idx = 0; idx < actions.length; idx++) {
            buttonAction = WidgetFactory.createPushButton(buttonArea);
            buttonAction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            buttonAction.setText(AbstractResource.getActionText(actions[idx]));
            buttonAction.setEnabled(false);
            buttonAction.setData("Action", actions[idx]);
            buttonAction.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String action = (String)((Button)e.getSource()).getData("Action");
                    AbstractResource[] selectedItems = getSelectedResources();
                    for (int idx = 0; idx < selectedItems.length; idx++) {
                        selectedItems[idx].setAction(action);
                    }
                    tableViewerResources.setSelection(new StructuredSelection(), true);
                    tableViewerResources.refresh();
                }
            });
            buttonActions[idx] = buttonAction;
        }

        buttonUndoAction = WidgetFactory.createPushButton(buttonArea);
        buttonUndoAction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        buttonUndoAction.setText(Messages.Undo_action);
        buttonUndoAction.setEnabled(false);
        buttonUndoAction.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AbstractResource[] selectedItems = getSelectedResources();
                for (int idx = 0; idx < selectedItems.length; idx++) {
                    selectedItems[idx].setAction(null);
                }
                tableViewerResources.setSelection(new StructuredSelection(), true);
                tableViewerResources.refresh();
            }
        });

    }

    protected void addTablePopupMenu(Table tableResources2) {
    }

    private AbstractResource[] getSelectedResources() {

        List<AbstractResource> selectedResources = new LinkedList<AbstractResource>();

        IStructuredSelection selection = (IStructuredSelection)tableViewerResources.getSelection();
        Object[] selectedItems = selection.toArray();
        for (Object item : selectedItems) {
            if (item instanceof AbstractResource) {
                selectedResources.add((AbstractResource)item);
            }
        }

        return selectedResources.toArray(new AbstractResource[selectedResources.size()]);
    }

    private void setButtonsEnabled(boolean enabled) {

        buttonDeselectAll.setEnabled(enabled);

        buttonUndoAction.setEnabled(enabled);

        for (int idx = 0; idx < buttonActions.length; idx++) {
            buttonActions[idx].setEnabled(enabled);
        }

        // Override button enablement depending on selected resources
        AbstractResource[] selectedItems = getSelectedResources();

        // Disable "Push to workspace" button
        Button pushToWorkspaceButton = getActionButton(AbstractResource.PUSH_TO_WORKSPACE);
        if (pushToWorkspaceButton != null && pushToWorkspaceButton.isEnabled()) {
            if (!isActionEnabled(AbstractResource.PUSH_TO_WORKSPACE, selectedItems)) {
                pushToWorkspaceButton.setEnabled(false);
            }
        }

        // Disable "Delete from workspace" button
        Button deleteFromWorkspaceButton = getActionButton(AbstractResource.DELETE_FROM_WORKSPACE);
        if (deleteFromWorkspaceButton != null && deleteFromWorkspaceButton.isEnabled()) {
            if (!isActionEnabled(AbstractResource.DELETE_FROM_WORKSPACE, selectedItems)) {
                deleteFromWorkspaceButton.setEnabled(false);
            }
        }

        // Disable "Delete from both" button
        Button deleteFromBothButton = getActionButton(AbstractResource.DELETE_FROM_BOTH);
        if (deleteFromBothButton != null && deleteFromBothButton.isEnabled()) {
            if (!isActionEnabled(AbstractResource.DELETE_FROM_BOTH, selectedItems)) {
                deleteFromBothButton.setEnabled(false);
            }
        }
    }

    protected boolean isActionEnabled(String action, AbstractResource[] selectedItems) {
        return true;
    }

    protected boolean allResourcesEditable(AbstractResource[] selectedItems) {

        for (AbstractResource selectedItem : selectedItems) {
            if (!selectedItem.isEditable()) {
                return false;
            }
        }

        return true;
    }

    private Button getActionButton(String action) {

        for (Button button : buttonActions) {
            String buttonAction = (String)button.getData("Action");
            if (action.equals(buttonAction)) {
                return button;
            }
        }

        return null;
    }

    public String[] getActionsWorkspace(boolean both) {
        String[] actions;
        if (both) {
            actions = new String[2];
            actions[0] = AbstractResource.PUSH_TO_REPOSITORY;
            actions[1] = AbstractResource.DELETE_FROM_WORKSPACE;
        } else {
            actions = new String[1];
            actions[0] = AbstractResource.DELETE_FROM_WORKSPACE;
        }
        return actions;
    }

    public String[] getActionsRepository(boolean both) {
        String[] actions;
        if (both) {
            actions = new String[2];
            actions[0] = AbstractResource.PUSH_TO_WORKSPACE;
            actions[1] = AbstractResource.DELETE_FROM_REPOSITORY;
        } else {
            actions = new String[1];
            actions[0] = AbstractResource.DELETE_FROM_REPOSITORY;
        }
        return actions;
    }

    public String[] getActionsBothDifferent() {
        String[] actions = new String[5];
        actions[0] = AbstractResource.PUSH_TO_REPOSITORY;
        actions[1] = AbstractResource.PUSH_TO_WORKSPACE;
        actions[2] = AbstractResource.DELETE_FROM_REPOSITORY;
        actions[3] = AbstractResource.DELETE_FROM_WORKSPACE;
        actions[4] = AbstractResource.DELETE_FROM_BOTH;
        return actions;
    }

    public String[] getActionsBothEqual() {
        String[] actions = new String[3];
        actions[0] = AbstractResource.DELETE_FROM_REPOSITORY;
        actions[1] = AbstractResource.DELETE_FROM_WORKSPACE;
        actions[2] = AbstractResource.DELETE_FROM_BOTH;
        return actions;
    }

    public String getTitleWorkspace() {
        return Messages.only_in_workspace;
    }

    public String getTitleRepository() {
        return Messages.only_in_repository;
    }

    public String getTitleBothDifferent() {
        return Messages.in_workspace_and_in_repository_with_different;
    }

    public String getTitleBothEqual() {
        return Messages.in_workspace_and_in_repository_with_equal;
    }

    public void setInput() {
        tableViewerResources.setInput(new Object());
    }

    public int getNumberOfItems() {
        return resources.length;
    }

    protected abstract void addTableColumns(Table tableResources);

    protected abstract String getTableColumnText(Object resource, int columnIndex);

    protected abstract Image getTableColumnImage(Object resource, int columnIndex);

    protected abstract int compareResources(Object resource1, Object resource2);

    protected abstract String[] getActions(boolean both);

    @Override
    protected void checkSubclass() {
    }

}
