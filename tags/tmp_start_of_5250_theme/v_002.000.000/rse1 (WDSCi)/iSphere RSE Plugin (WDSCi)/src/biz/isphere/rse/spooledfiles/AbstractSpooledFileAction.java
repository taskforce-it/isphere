/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ibm.etools.iseries.core.ui.actions.isv.ISeriesAbstractQSYSPopupMenuExtensionAction;

import biz.isphere.core.Messages;

public abstract class AbstractSpooledFileAction extends ISeriesAbstractQSYSPopupMenuExtensionAction {

	public AbstractSpooledFileAction() {
		super();
	}
	
	public void run() {
		
		init();
		
		String message = null;
		
		Object[] selection = getSelectedRemoteObjects();
		for (int i = 0; i < selection.length; i++) {
			if (selection[i] instanceof SpooledFileResource) {
				SpooledFileResource spooledFileResource = (SpooledFileResource)selection[i];
				message = execute(spooledFileResource);
				if (message != null) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.getString("Error"), message);
					break;
				}
			}
		}
		
		if (message == null) {
			message = finish();
			if (message != null) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.getString("Error"), message);
			}
		}
		
	}

	public void init() {}

	public abstract String execute(SpooledFileResource spooledFileResource);
	
	public String finish() {
		return null;
	}
	
}