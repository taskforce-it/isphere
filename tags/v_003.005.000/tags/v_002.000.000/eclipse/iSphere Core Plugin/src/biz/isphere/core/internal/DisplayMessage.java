/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DisplayMessage extends Thread {

	private Shell shell;
	private String text;
	private String message;

	public DisplayMessage(Shell shell, String text, String message) {
		this.shell = shell;
		this.text = text;
		this.message = message;
	}

	public void run() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
				errorBox.setText(text);
				errorBox.setMessage(message);
				errorBox.open();
			}
		});
	}
}
