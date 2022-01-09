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

package biz.isphere.journalexplorer.core.ui.widgets;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import biz.isphere.base.swt.events.TreeAutoSizeControlListener;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalPropertiesContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalPropertiesLabelProvider;

/**
 * This widget display the properties of a journal entry item.
 * 
 * @see JournalProperties
 */
public class JournalEntryDetailsViewer extends TreeViewer implements IPropertyChangeListener {

    public JournalEntryDetailsViewer(Composite parent) {
        this(parent, 240);
    }

    private JournalEntryDetailsViewer(Composite parent, int minValueColumnWidth) {
        super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        this.initializeComponents(minValueColumnWidth);

        Preferences.getInstance().addPropertyChangeListener(this);
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

        TreeColumn value = new TreeColumn(tree, SWT.LEFT);
        value.setAlignment(SWT.LEFT);
        value.setWidth(240);
        value.setText(Messages.JournalEntryViewer_Value);

        TreeAutoSizeControlListener treeAutoSizeListener = new TreeAutoSizeControlListener(tree, TreeAutoSizeControlListener.USE_FULL_WIDTH);
        treeAutoSizeListener.addResizableColumn(property, 1, 120, 240);
        treeAutoSizeListener.addResizableColumn(value, 1, minValueColumnWidth);
        tree.addControlListener(treeAutoSizeListener);
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
}
