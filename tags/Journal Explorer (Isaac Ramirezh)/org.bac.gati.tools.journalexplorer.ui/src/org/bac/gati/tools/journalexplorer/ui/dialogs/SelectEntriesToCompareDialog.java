package org.bac.gati.tools.journalexplorer.ui.dialogs;

import org.bac.gati.tools.journalexplorer.internals.Messages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowData;
import org.eclipse.wb.swt.SWTResourceManager;

public class SelectEntriesToCompareDialog extends Dialog {
	
	private Composite container;
	private ScrolledComposite scrolledComposite;
	private Composite leftEntryComposite;
	private ScrolledComposite scrolledCompositeRight;
	private Composite rightEntryComposite;
	private Label lblNewLabel;
	private Label lblSeleccioneElRegistro;
	private Object leftEntry;
	private Object rightEntry;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SelectEntriesToCompareDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		///
		/// Main container
		///
		this.container = (Composite) super.createDialogArea(parent);
		RowLayout rl_container = new RowLayout(SWT.VERTICAL);
		rl_container.fill = true;
		rl_container.marginTop = 10;
		rl_container.marginRight = 10;
		rl_container.marginLeft = 10;
		rl_container.wrap = false;
		container.setLayout(rl_container);
		
		///
		/// Left label
		///
		lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText(Messages.SelectEntriesToCompareDialog_ChooseLeftRecord);
		
		///
		/// Left scrolled composite
		///
		scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setLayoutData(new RowData(372, 100));
		
		///
		/// leftEntryComposite
		/// 
		this.leftEntryComposite = new Composite(this.scrolledComposite, SWT.NONE);
		leftEntryComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		RowLayout leftLayout = new RowLayout(SWT.VERTICAL);
		leftLayout.fill = true;
		leftLayout.wrap = false;
		this.leftEntryComposite.setLayout(leftLayout);
		this.scrolledComposite.setContent(leftEntryComposite);
		
		///
		/// rightLabel
		///
		lblSeleccioneElRegistro = new Label(container, SWT.NONE);
		lblSeleccioneElRegistro.setText(Messages.SelectEntriesToCompareDialog_ChooseRightRecord);

		///
		/// scrolledCompositeRight
		///
		this.scrolledCompositeRight = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.scrolledCompositeRight.setExpandVertical(true);
		this.scrolledCompositeRight.setExpandHorizontal(true);
		this.scrolledCompositeRight.setLayoutData(new RowData(397, 112));
		
		///
		/// rightEntryComposite
		///
		this.rightEntryComposite = new Composite(scrolledCompositeRight, SWT.NONE);
		rightEntryComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		RowLayout rightLayout = new RowLayout(SWT.VERTICAL);
		rightLayout.wrap = false;
		rightLayout.fill = true;
		this.rightEntryComposite.setLayout(rightLayout);
		this.scrolledCompositeRight.setContent(rightEntryComposite);
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.SelectEntriesToCompareDialog_ChooseEntriesToCompare);
	}
	
	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(442, 388);
	}

	public void setInput(Object[] input) {
		
		Button option;
		
		final Color white = new Color(Display.getCurrent(), new RGB(255, 255, 255));
		
		SelectionListener listenerLeft = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.getSource() instanceof Button) {
					SelectEntriesToCompareDialog.this.leftEntry = ((Button) event.getSource()).getData();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) { 
				widgetSelected(event);
			}
		};
		
		SelectionListener listenerRight = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.getSource() instanceof Button) {
					SelectEntriesToCompareDialog.this.rightEntry = ((Button) event.getSource()).getData();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent event) { 
				widgetSelected(event);
			}
		};
		
		for (Object object : input) {
			option = new Button(this.leftEntryComposite, SWT.RADIO);
			option.setText(object.toString());
			option.setData(object);
			option.addSelectionListener(listenerLeft);
			option.setBackground(white);
			
			option = new Button(this.rightEntryComposite, SWT.RADIO);
			option.setText(object.toString());
			option.setData(object);
			option.addSelectionListener(listenerRight);
			option.setBackground(white);
		}
		
		this.leftEntryComposite.layout(true);
		this.scrolledComposite.setMinSize(this.leftEntryComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		this.rightEntryComposite.layout(true);
		this.scrolledCompositeRight.setMinSize(this.rightEntryComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void okPressed() {
		if (this.leftEntry != null && this.rightEntry != null) {
			super.okPressed();
		}
		else {
			MessageDialog.openError(this.getShell(), Messages.SelectEntriesToCompareDialog_3, Messages.SelectEntriesToCompareDialog_ChooseBothRecordsToCompare);
		}
	}
	
	public Object getLeftEntry() {
		return this.leftEntry;
	}

	public Object getRightEntry() {
		return this.rightEntry;
	}
}
