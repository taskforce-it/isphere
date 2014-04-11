package org.bac.gati.tools.journalexplorer.ui.widgets;

import org.bac.gati.tools.journalexplorer.internals.Messages;
import org.bac.gati.tools.journalexplorer.ui.contentProviders.JournalPropertiesContentProvider;
import org.bac.gati.tools.journalexplorer.ui.labelProviders.JournalEntryViewLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class JournalEntryViewer extends TreeViewer {

	public JournalEntryViewer(Composite parent) {
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION );
		this.initializeComponents();
	}
	
	private void initializeComponents() {
		this.setAutoExpandLevel(1);
		Tree tree = this.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		this.setContentProvider(new JournalPropertiesContentProvider());
		this.setLabelProvider(new JournalEntryViewLabelProvider());
		
		TreeColumn property = new TreeColumn(tree, SWT.LEFT);
		property.setAlignment(SWT.LEFT);
		property.setWidth(250);
		property.setText(Messages.JournalEntryViewer_Property);
		
		TreeColumn value = new TreeColumn(tree, SWT.LEFT);
		value.setAlignment(SWT.LEFT);
		value.setWidth(250);
		value.setText(Messages.JournalEntryViewer_Value);
	}
}
