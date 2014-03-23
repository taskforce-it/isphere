/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagefileeditor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.Messages;
import biz.isphere.internal.Size;


public class FieldFormatDetailDialog extends Dialog {

	private int actionType;
	private FieldFormat _fieldFormat;
	private FieldFormatDetail _fieldFormatDetail;
	
	public FieldFormatDetailDialog(Shell parentShell, int actionType, FieldFormat _fieldFormat) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.actionType = actionType;
		this._fieldFormat = _fieldFormat;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout());
        	
		_fieldFormatDetail = new FieldFormatDetail(actionType, _fieldFormat);
		_fieldFormatDetail.createContents(container);
		
		return container;
	}
	
	protected void okPressed() {
		if (_fieldFormatDetail.processButtonPressed()) {
			super.okPressed();
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("Cancel"), false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("Field_format"));
	}
	
	protected Point getInitialSize() {
		return getShell().computeSize(Size.getSize(450), SWT.DEFAULT, true);
	}
	
}
