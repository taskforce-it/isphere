/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

import com.ibm.as400.access.AS400;


public class SignOnDialog extends XDialog {

	private SignOn signOn;
	private String hostName;
	
	public SignOnDialog(Shell parentShell, String aHostName) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		hostName = aHostName;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.VERTICAL));
        	
		signOn = new SignOn();
		signOn.createContents(container, hostName);
				
		return container;
	}
	
	protected void okPressed() {
		if (signOn.processButtonPressed()) {
			super.okPressed();
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.Sign_On);
	}
	
	protected Point getDefaultSize() {
		Point point = getShell().computeSize(250, SWT.DEFAULT, true);	
		return point;
	}
	
	public AS400 getAS400() {
		return signOn.getAS400();
	}
	
	protected IDialogSettings getDialogBoundsSettings() {
	     return super.getDialogBoundsSettings(ISpherePlugin.getDefault()
	             .getDialogSettings());
	  }

}
