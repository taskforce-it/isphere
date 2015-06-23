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

import java.io.IOException;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;

import de.taskforce.tn5250j.core.Messages;
import de.taskforce.tn5250j.core.TN5250JCorePlugin;

public class SignOn {

	private String host;
	private Text textUser;
	private Text textPassword;
	private StatusLineManager statusLineManager;
	private AS400 as400;

	public SignOn(String host) {
		this.host = host;
		as400 = null;
	}
	
	public void createContents(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		
		// General
		
		final Composite compositeGeneral = new Composite(container, SWT.NONE);
		compositeGeneral.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayoutCompositeGeneral = new GridLayout();
		gridLayoutCompositeGeneral.numColumns = 2;
		compositeGeneral.setLayout(gridLayoutCompositeGeneral);
		
		// General : Host
		
		final Label labelHost = new Label(compositeGeneral, SWT.NONE);
		labelHost.setText(Messages.getString("Host") + ":");

		final Text textHost = new Text(compositeGeneral, SWT.BORDER);
		textHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textHost.setText(host);
		textHost.setEditable(false);
		
		// General : User
		
		final Label labelUser = new Label(compositeGeneral, SWT.NONE);
		labelUser.setText(Messages.getString("User") + ":");

		textUser = new Text(compositeGeneral, SWT.BORDER);
		textUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textUser.setText("");
		
		// General : Password
		
		final Label labelPassword = new Label(compositeGeneral, SWT.NONE);
		labelPassword.setText(Messages.getString("Password") + ":");

		textPassword = new Text(compositeGeneral, SWT.PASSWORD | SWT.BORDER);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textPassword.setText("");
		
		// Status line
		        
		statusLineManager = new StatusLineManager(); 
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();        
		final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
		statusLine.setLayoutData(gridDataStatusLine);
		
        // Set focus
        
		textUser.setFocus();

	}

	protected void setErrorMessage(String errorMessage) {
		if (errorMessage != null) {
			statusLineManager.setErrorMessage(TN5250JCorePlugin.getDefault().getImageRegistry().get(TN5250JCorePlugin.IMAGE_ERROR), errorMessage);
		}
		else {
			statusLineManager.setErrorMessage(null, null);
		}
	}
	
	public boolean processButtonPressed() {
		
		textUser.getText().trim();
		textPassword.getText().trim();
		
		if (textUser.getText().equals("")) {
			setErrorMessage(Messages.getString("Enter_a_user."));
			textUser.setFocus();
			return false;
		}
		
		as400 = new AS400(host, textUser.getText(), textPassword.getText());
		try {
			as400.validateSignon();
		} 
		catch (AS400SecurityException e) {
			setErrorMessage(e.getMessage());
			textUser.setFocus();
			return false;
		} 
		catch (IOException e) {
			setErrorMessage(e.getMessage());
			textUser.setFocus();
			return false;
		}
		return true;
	}
	
	public AS400 getAS400() {
		return as400;
	}

}
