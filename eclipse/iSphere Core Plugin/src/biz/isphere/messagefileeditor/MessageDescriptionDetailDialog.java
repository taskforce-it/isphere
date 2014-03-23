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

import com.ibm.as400.access.AS400;


public class MessageDescriptionDetailDialog extends Dialog {

	private AS400 as400;
	private int actionType;
	private MessageDescription _messageDescription;
	private MessageDescriptionDetail _messageDescriptionDetail;
	
	public MessageDescriptionDetailDialog(Shell parentShell, AS400 as400, int actionType, MessageDescription _messageDescription) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.as400 = as400;
		this.actionType = actionType;
		this._messageDescription = _messageDescription;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout());
        	
		_messageDescriptionDetail = new MessageDescriptionDetail(as400, actionType, _messageDescription);
		_messageDescriptionDetail.createContents(container);
		
		return container;
	}
	
	protected void okPressed() {
		if (_messageDescriptionDetail.processButtonPressed()) {
			super.okPressed();
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("Cancel"), false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("Message_description"));
	}
	
	protected Point getInitialSize() {
		Point point = getShell().computeSize(Size.getSize(600), SWT.DEFAULT, true);	
		point.y = point.y + _messageDescriptionDetail.getFieldFormatViewer().getTableHeight(4);
		return point;

	}
	
}
