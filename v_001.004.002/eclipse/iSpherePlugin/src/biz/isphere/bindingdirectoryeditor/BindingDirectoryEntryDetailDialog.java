/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.bindingdirectoryeditor;

import java.util.ArrayList;

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


public class BindingDirectoryEntryDetailDialog extends Dialog {

	private String level;
	private int actionType;
	private BindingDirectoryEntry _bindingDirectoryEntry;
	private ArrayList<BindingDirectoryEntry> _bindingDirectoryEntries;
	private BindingDirectoryEntryDetail _bindingDirectoryEntryDetail;
	
	public BindingDirectoryEntryDetailDialog(
			Shell parentShell, 
			String level,
			int actionType, 
			BindingDirectoryEntry _bindingDirectoryEntry,
			ArrayList<BindingDirectoryEntry> _bindingDirectoryEntries) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.level = level;
		this.actionType = actionType;
		this._bindingDirectoryEntry = _bindingDirectoryEntry;
		this._bindingDirectoryEntries = _bindingDirectoryEntries;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout());
        	
		_bindingDirectoryEntryDetail = new BindingDirectoryEntryDetail(
				level,
				actionType, 
				_bindingDirectoryEntry,
				_bindingDirectoryEntries);
		_bindingDirectoryEntryDetail.createContents(container);
		
		return container;
	}
	
	protected void okPressed() {
		if (_bindingDirectoryEntryDetail.processButtonPressed()) {
			super.okPressed();
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("Cancel"), false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("Binding_Directory_Entry"));
	}
	
	protected Point getInitialSize() {
		return getShell().computeSize(Size.getSize(450), SWT.DEFAULT, true);
	}
	
}
