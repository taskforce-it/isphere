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

package biz.isphere.journalexplorer.core.ui.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import biz.isphere.base.swt.events.TreeAutoSizeControlListener;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperty;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalPropertiesContentProvider;
import biz.isphere.journalexplorer.core.ui.dialogs.ColumnResizeListener;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalPropertiesLabelProvider;
import biz.isphere.journalexplorer.core.ui.widgets.menues.JournalEntryMenuAdapter;

/**
 * This widget display the properties of a journal entry item.
 * 
 * @see JournalProperties
 */
public class JournalEntryDetailsViewer extends TreeViewer implements IPropertyChangeListener {

    private static final String COLUMN_KEY = "ID";
    private static final String COLUMN_PROPERTY = "PROPERTY";
    private static final String COLUMN_VALUE = "VALUE";

    private DisplayChangedFieldsOnlyFilter displayChangedFieldsOnlyFilter;
    private Map<String, TreeColumn> columnsByName;
    private ColumnResizeListener columnResizeListener;
    private TreeAutoSizeControlListener treeAutoSizeListener;

    public JournalEntryDetailsViewer(Composite parent) {
        this(parent, 240);
    }

    private JournalEntryDetailsViewer(Composite parent, int minValueColumnWidth) {
        super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

        this.displayChangedFieldsOnlyFilter = null;
        this.columnsByName = new HashMap<String, TreeColumn>();
        this.columnResizeListener = null;

        initializeComponents(minValueColumnWidth);
        createContextMenu();

        Preferences.getInstance().addPropertyChangeListener(this);
    }

    private void createContextMenu() {

        MenuManager menuMgr = new MenuManager();

        Menu menu = menuMgr.createContextMenu(this.getControl());
        menuMgr.addMenuListener(new JournalEntryMenuAdapter(this));

        menuMgr.setRemoveAllWhenShown(true);
        this.getControl().setMenu(menu);
    }

    public Set<String> getColumnNames() {

        Set<String> columnNames = new HashSet<String>();

        for (TreeColumn column : getTree().getColumns()) {
            String columnName = (String)column.getData(COLUMN_KEY);
            columnNames.add(columnName);
        }

        return columnNames;
    }

    public int getColumnWidth(String columName) {

        TreeColumn column = columnsByName.get(columName);
        int width = column.getWidth();

        return width;
    }

    public void setColumnWidth(String columName, int width) {

        TreeColumn column = columnsByName.get(columName);
        column.setWidth(width);

        if (treeAutoSizeListener != null) {
            getTree().removeControlListener(treeAutoSizeListener);
            treeAutoSizeListener = null;
        }
    }

    @Override
    public void refresh(boolean updateLabels) {
        super.refresh(updateLabels);
    }

    private void initializeComponents(int minValueColumnWidth) {

        setAutoExpandLevel(1);
        Tree tree = getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        setContentProvider(new JournalPropertiesContentProvider());
        setLabelProvider(new JournalPropertiesLabelProvider());

        TreeColumn property = new TreeColumn(tree, SWT.LEFT);
        property.setAlignment(SWT.LEFT);
        property.setWidth(240);
        property.setText(Messages.JournalEntryViewer_Property);
        property.setData(COLUMN_KEY, COLUMN_PROPERTY);
        columnsByName.put(COLUMN_PROPERTY, property);

        TreeColumn value = new TreeColumn(tree, SWT.LEFT);
        value.setAlignment(SWT.LEFT);
        value.setWidth(240);
        value.setText(Messages.JournalEntryViewer_Value);
        value.setData(COLUMN_KEY, COLUMN_VALUE);
        columnsByName.put(COLUMN_VALUE, value);

        treeAutoSizeListener = new TreeAutoSizeControlListener(tree, TreeAutoSizeControlListener.USE_FULL_WIDTH);
        treeAutoSizeListener.addResizableColumn(property, 1, 120, 240);
        treeAutoSizeListener.addResizableColumn(value, 1, minValueColumnWidth);
        tree.addControlListener(treeAutoSizeListener);
    }

    public void setColumnResizeListener(ColumnResizeListener columnResizeListener) {

        this.columnResizeListener = columnResizeListener;

        for (TreeColumn column : getTree().getColumns()) {
            column.addControlListener(this.columnResizeListener);
        }
    }

    public String getColumnName(TreeColumn column) {

        String columnName = (String)column.getData(COLUMN_KEY);

        return columnName;
    }

    public void setConnectedColumnWidth(TreeColumn column) {

        String columnName = (String)column.getData(COLUMN_KEY);
        TreeColumn connectedColumn = columnsByName.get(columnName);
        connectedColumn.setWidth(column.getWidth());
    }

    public boolean isDisplayChangedFieldsOnly() {

        if (displayChangedFieldsOnlyFilter != null) {
            return true;
        } else {
            return false;
        }
    }

    public void setDisplayChangedFieldsOnly(boolean enabled) {

        if (enabled && displayChangedFieldsOnlyFilter == null) {
            displayChangedFieldsOnlyFilter = new DisplayChangedFieldsOnlyFilter();
            addFilter(displayChangedFieldsOnlyFilter);
            refresh();
        } else if (!enabled && displayChangedFieldsOnlyFilter != null) {
            removeFilter(displayChangedFieldsOnlyFilter);
            displayChangedFieldsOnlyFilter = null;
            refresh();
        }
    }

    public void setAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.setSelectionProviderDelegate(this);
    }

    public void propertyChange(PropertyChangeEvent event) {

        if (event.getProperty() == null) {
            return;
        }

        if (Preferences.HIGHLIGHT_USER_ENTRIES.equals(event.getProperty())) {
            refresh();
            return;
        }

        if (Preferences.ENABLED.equals(event.getProperty())) {
            refresh();
            return;
        }

        if (event.getProperty().startsWith(Preferences.COLORS)) {
            JournalPropertiesLabelProvider labelProvider = (JournalPropertiesLabelProvider)getLabelProvider();
            String columnName = event.getProperty().substring(Preferences.COLORS.length());
            Object object = event.getNewValue();
            if (object instanceof String) {
                String rgb = (String)event.getNewValue();
                if (columnName != null) {
                    Color color = ISphereJournalExplorerCorePlugin.getDefault().getColor(rgb);
                    labelProvider.setColumnColor(columnName, color);
                }
            }
            refresh();
            return;
        }
    }

    public void dispose() {
        Preferences.getInstance().removePropertyChangeListener(this);
    }

    class DisplayChangedFieldsOnlyFilter extends ViewerFilter {

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {

            if (element instanceof JournalProperty) {
                JournalProperty property = (JournalProperty)element;
                if (property.parent instanceof JournalProperty) {
                    JournalProperty parentProperty = (JournalProperty)property.parent;
                    if (ColumnsDAO.JOESD.name().equals(parentProperty.name)) {
                        if (!property.highlighted) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }
}
