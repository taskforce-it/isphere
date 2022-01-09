package org.bac.gati.tools.journalexplorer.ui.dialogs;

import org.bac.gati.tools.journalexplorer.internals.Messages;
import org.bac.gati.tools.journalexplorer.model.adapters.JournalProperties;
import org.bac.gati.tools.journalexplorer.ui.widgets.JournalEntryViewer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class SideBySideCompareDialog extends Dialog {

	private JournalEntryViewer leftEntry;
	private JournalEntryViewer rightEntry;
	private Label lblLeftEntry;
	private Label lblRightEntry;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SideBySideCompareDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		FillLayout fl_container = new FillLayout(SWT.HORIZONTAL);
		fl_container.marginHeight = 5;
		fl_container.marginWidth = 5;
		fl_container.spacing = 5;
		container.setLayout(fl_container);
		
		Composite leftComposite = new Composite(container, SWT.BORDER);
		leftComposite.setLayout(new GridLayout(1, false));
		
		lblLeftEntry = new Label(leftComposite, SWT.NONE);
		lblLeftEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));		
		
		this.leftEntry  = new JournalEntryViewer(leftComposite);
		Tree tree = leftEntry.getTree();
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
		
		Composite rightComposite = new Composite(container, SWT.BORDER);
		rightComposite.setLayout(new GridLayout(1, false));
		
		lblRightEntry = new Label(rightComposite, SWT.NONE);
		lblRightEntry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));		
		this.rightEntry = new JournalEntryViewer(rightComposite);
		Tree tree_1 = rightEntry.getTree();
		tree_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		return container;
	}

	public void setInput(JournalProperties leftEntry, JournalProperties rightEntry) {
		
		this.lblLeftEntry.setText(leftEntry.toString());
		this.leftEntry.setInput(new Object[] { leftEntry });
		this.leftEntry.expandAll();
		
		this.lblRightEntry.setText(rightEntry.toString());
		this.rightEntry.setInput(new Object[] { rightEntry });
		this.rightEntry.expandAll();
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(830, 699);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setImage(ResourceManager.getPluginImage("org.bac.gati.tools.journalexplorer.ui", "icons/horizontal_results_view.gif")); //$NON-NLS-1$ //$NON-NLS-2$
		super.configureShell(newShell);
		newShell.setText(Messages.SideBySideCompareDialog_SideBySideComparison);
	}
}
