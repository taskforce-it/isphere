// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.core.misc;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.ibm.as400.access.AS400;

import de.taskforce.tn5250j.core.Messages;

public class SignOnDialog extends Dialog {

	private String host;
	private SignOn signOn;
	
	public SignOnDialog(Shell parentShell, String host) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.host = host;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FillLayout(SWT.VERTICAL));
        	
		signOn = new SignOn(host);
		signOn.createContents(container);
				
		return container;
	}
	
	protected void okPressed() {
		if (signOn.processButtonPressed()) {
			super.okPressed();
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("Cancel"), false);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("Sign_On"));
	}
	
	protected Point getInitialSize() {
		Point point = getShell().computeSize(250, SWT.DEFAULT, true);	
		return point;
	}
	
	public AS400 getAS400() {
		return signOn.getAS400();
	}

}
