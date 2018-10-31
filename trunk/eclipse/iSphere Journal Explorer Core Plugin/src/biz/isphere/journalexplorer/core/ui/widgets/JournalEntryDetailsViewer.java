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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import biz.isphere.base.swt.events.TreeAutoSizeControlListener;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalPropertiesContentProvider;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalPropertiesLabelProvider;

/**
 * This widget display the properties of a journal entry item.
 * 
 * @see JournalProperties
 */
public class JournalEntryDetailsViewer extends TreeViewer {

    public JournalEntryDetailsViewer(Composite parent) {
        this(parent, 240);
    }

    public JournalEntryDetailsViewer(Composite parent, int minValueColumnWidth) {
        super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        this.initializeComponents(minValueColumnWidth);
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
}
