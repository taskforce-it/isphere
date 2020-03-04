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

package de.taskforce.tn5250j.core.session;

import java.io.File;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;

import de.taskforce.tn5250j.core.DialogActionTypes;
import de.taskforce.tn5250j.core.Messages;
import de.taskforce.tn5250j.core.TN5250JCorePlugin;

public class SessionDetail {

	private IPreferenceStore store;
	private Text textName;
	private Text textDevice;
	private Text textPort;
	private CCombo comboCodePage;
	private Button buttonScreenSize24_80;
	private Button buttonScreenSize27_132;
//	private Button buttonEnhancedMode;
	private Button buttonView;
	private Button buttonEditor;
	private Text textUser;
	private Text textPassWord;
	private Text textProgram;
	private Text textLibrary;
	private Text textMenu;
	private StatusLineManager statusLineManager;
	private String sessionDirectory;
	private int actionType;
	private Session session;
	private String[] codePages = {"37","37PT","273","280","284","285",
            					  "277-dk","277-no","278","297","424","500-ch",
            					  "870-pl","870-sk","871","875","1025-r","1026",
            					  "1112","1141","1140","1147","1148"};

	public SessionDetail(String sessionDirectory, int actionType, Session session) {
		this.sessionDirectory = sessionDirectory;
		this.actionType = actionType;
		this.session = session;
		store = TN5250JCorePlugin.getDefault().getPreferenceStore();
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void createContents(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		// Action
		
		final Label labelAction = new Label(container, SWT.CENTER | SWT.BORDER);
		labelAction.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		if (actionType == DialogActionTypes.DELETE) {
			labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		} 
		else {
			labelAction.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));			
		}
		labelAction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		labelAction.setText(DialogActionTypes.getText(actionType));
		
		// General
		
		final Composite compositeGeneral = new Composite(container, SWT.NONE);
		compositeGeneral.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayoutCompositeGeneral = new GridLayout();
		gridLayoutCompositeGeneral.numColumns = 2;
		compositeGeneral.setLayout(gridLayoutCompositeGeneral);

		// General : Connection		

		final Label labelConnection = new Label(compositeGeneral, SWT.NONE);
		labelConnection.setText(Messages.getString("Connection") + ":");
		
		final Text textConnection = new Text(compositeGeneral, SWT.BORDER);
		textConnection.setText(session.getConnection());
		textConnection.setEditable(false);
		textConnection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		// General : Name
		
		final Label labelName = new Label(compositeGeneral, SWT.NONE);
		labelName.setText(Messages.getString("Name") + ":");

		textName = new Text(compositeGeneral, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (actionType == DialogActionTypes.CREATE) {
			textName.setText(session.getName());
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textName.setText(session.getName());
		}
		if ((actionType == DialogActionTypes.CREATE && !session.getName().equals("")) || actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textName.setEnabled(false);
		}
		
		// General : Device
		
		final Label labelDevice = new Label(compositeGeneral, SWT.NONE);
		labelDevice.setText(Messages.getString("Device") + ":");

		textDevice = new Text(compositeGeneral, SWT.BORDER);
		textDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textDevice.setTextLimit(10);
		if (actionType == DialogActionTypes.CREATE) {
			textDevice.setText("");
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textDevice.setText(session.getDevice());
		}
		if (textName.getText().equals("_DESIGNER") || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textDevice.setEnabled(false);
		}
		
		// General : Port
		
		final Label labelPort = new Label(compositeGeneral, SWT.NONE);
		labelPort.setText(Messages.getString("Port") + ":");

		textPort = new Text(compositeGeneral, SWT.BORDER);
		textPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textPort.setTextLimit(5);
		if (actionType == DialogActionTypes.CREATE) {
			textPort.setText(store.getString("DE.TASKFORCE.TN5250J.PORT"));
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textPort.setText(session.getPort());
		}
		if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textPort.setEnabled(false);
		}
		
		// General : Codepage
		
		final Label labelCodePage = new Label(compositeGeneral, SWT.NONE);
		labelCodePage.setText(Messages.getString("Codepage") + ":");

		comboCodePage = new CCombo(compositeGeneral, SWT.BORDER);
		comboCodePage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboCodePage.setTextLimit(10);
		for (int idx = 0; idx < codePages.length; idx++) {
			comboCodePage.add(codePages[idx]);
		}
		if (actionType == DialogActionTypes.CREATE) {
			comboCodePage.setText(store.getString("DE.TASKFORCE.TN5250J.CODEPAGE"));
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			comboCodePage.setText(session.getCodePage());
		}
		if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			comboCodePage.setEnabled(false);
		}

		// General : Screen size
		
		final Label labelScreenSize = new Label(compositeGeneral, SWT.NONE);
		labelScreenSize.setText(Messages.getString("Screensize") + ":");

		final Group groupScreenSize = new Group(compositeGeneral, SWT.NONE);
		final GridLayout gridLayoutScreenSize = new GridLayout();
		gridLayoutScreenSize.numColumns = 2;
		groupScreenSize.setLayout(gridLayoutScreenSize);

		buttonScreenSize24_80 = new Button(groupScreenSize, SWT.RADIO);
		buttonScreenSize24_80.setText("24*80");

		buttonScreenSize27_132 = new Button(groupScreenSize, SWT.RADIO);
		buttonScreenSize27_132.setText("27*132");
		
		if (actionType == DialogActionTypes.CREATE) {
			if (store.getString("DE.TASKFORCE.TN5250J.SCREENSIZE").equals("132")) {
				buttonScreenSize27_132.setSelection(true);
			}
			else {
				buttonScreenSize24_80.setSelection(true);
			}
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			if (session.getScreenSize().equals("132")) {
				buttonScreenSize27_132.setSelection(true);
			}
			else {
				buttonScreenSize24_80.setSelection(true);
			}
		}
		if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			buttonScreenSize24_80.setEnabled(false);
			buttonScreenSize27_132.setEnabled(false);
		}

		// General : Enhanced mode
/*		
		final Label labelEnhancedMode = new Label(compositeGeneral, SWT.NONE);
		labelEnhancedMode.setText(Messages.getString("Enhanced_mode") + ":");

		buttonEnhancedMode = new Button(compositeGeneral, SWT.CHECK);
		
		if (actionType == DialogActionTypes.CREATE) {
			if (store.getString("DE.TASKFORCE.TN5250J.ENHANCEDMODE").equals("Y")) {
				buttonEnhancedMode.setSelection(true);
			}
			else {
				buttonEnhancedMode.setSelection(false);
			}
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			if (session.getEnhancedMode().equals("Y")) {
				buttonEnhancedMode.setSelection(true);
			}
			else {
				buttonEnhancedMode.setSelection(false);
			}
		}
		if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			buttonEnhancedMode.setEnabled(false);
		}
*/
		// General : Area
		
		final Label labelArea = new Label(compositeGeneral, SWT.NONE);
		labelArea.setText(Messages.getString("Area") + ":");

		final Group groupArea = new Group(compositeGeneral, SWT.NONE);
		final GridLayout gridLayoutArea = new GridLayout();
		gridLayoutArea.numColumns = 2;
		groupArea.setLayout(gridLayoutArea);

		buttonView = new Button(groupArea, SWT.RADIO);
		buttonView.setText(Messages.getString("View"));

		buttonEditor = new Button(groupArea, SWT.RADIO);
		buttonEditor.setText(Messages.getString("Editor"));
		
		if (actionType == DialogActionTypes.CREATE) {
			if (store.getString("DE.TASKFORCE.TN5250J.AREA").equals("*VIEW")) {
				buttonView.setSelection(true);
			}
			else {
				buttonEditor.setSelection(true);
			}
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			if (session.getArea().equals("*VIEW")) {
				buttonView.setSelection(true);
			}
			else {
				buttonEditor.setSelection(true);
			}
		}
		if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			buttonView.setEnabled(false);
			buttonEditor.setEnabled(false);
		}
		
		// Signon mask
		
		final Group groupSignOnMask = new Group(container, SWT.NONE);
		groupSignOnMask.setText(Messages.getString("Signon_mask"));
		groupSignOnMask.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		groupSignOnMask.setLayout(gridLayout_1);

		// Signon mask : User
		
		final Label labelUser = new Label(groupSignOnMask, SWT.NONE);
		labelUser.setText(Messages.getString("User") + ":");

		textUser = new Text(groupSignOnMask, SWT.BORDER);
		textUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textUser.setTextLimit(128);
		if (actionType == DialogActionTypes.CREATE) {
			textUser.setText("");
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textUser.setText(session.getUser());
		}
		if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textUser.setEnabled(false);
		}

		// Signon mask : Password
		
		final Label labelPassWord = new Label(groupSignOnMask, SWT.NONE);
		labelPassWord.setText(Messages.getString("Password") + ":");

		textPassWord = new Text(groupSignOnMask, SWT.PASSWORD | SWT.BORDER);
		textPassWord.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textPassWord.setTextLimit(128);
		if (actionType == DialogActionTypes.CREATE) {
			textPassWord.setText("");
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			if (session.getPassword().equals("")) {
				textPassWord.setText("");
			}
			else {
				BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
				textEncryptor.setPassword(TN5250JCorePlugin.BASIC);
				String decryptedPassword;
				try {
					decryptedPassword = textEncryptor.decrypt(session.getPassword());
				}
				catch (EncryptionOperationNotPossibleException exeption) {
					decryptedPassword = "";
				}
				textPassWord.setText(decryptedPassword);
			}
		}
		if (actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textPassWord.setEnabled(false);
		}

		// Signon mask : Program
		
		final Label labelProgram = new Label(groupSignOnMask, SWT.NONE);
		labelProgram.setText(Messages.getString("Program") + ":");

		textProgram = new Text(groupSignOnMask, SWT.BORDER);
		textProgram.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textProgram.setTextLimit(10);
		if (actionType == DialogActionTypes.CREATE) {
			textProgram.setText(session.getProgram());
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textProgram.setText(session.getProgram());
		}
		if (textName.getText().equals("_DESIGNER") || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textProgram.setEnabled(false);
		}
		
		// Signon mask : Library
		
		final Label labelLibrary = new Label(groupSignOnMask, SWT.NONE);
		labelLibrary.setText(Messages.getString("Library") + ":");

		textLibrary = new Text(groupSignOnMask, SWT.BORDER);
		textLibrary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textLibrary.setTextLimit(10);
		if (actionType == DialogActionTypes.CREATE) {
			textLibrary.setText(session.getLibrary());
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textLibrary.setText(session.getLibrary());
		}
		if (textName.getText().equals("_DESIGNER") || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textLibrary.setEnabled(false);
		}
		
		// Signon mask : Menu
		
		final Label labelMenu = new Label(groupSignOnMask, SWT.NONE);
		labelMenu.setText(Messages.getString("Menu") + ":");

		textMenu = new Text(groupSignOnMask, SWT.BORDER);
		textMenu.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		textMenu.setTextLimit(10);
		if (actionType == DialogActionTypes.CREATE) {
			textMenu.setText("");
		} 
		else if (actionType == DialogActionTypes.CHANGE || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textMenu.setText(session.getMenu());
		}
		if (textName.getText().equals("_DESIGNER") || actionType == DialogActionTypes.DELETE || actionType == DialogActionTypes.DISPLAY) {
			textMenu.setEnabled(false);
		}
		
		// Status line
		        
		statusLineManager = new StatusLineManager(); 
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();        
		final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
		statusLine.setLayoutData(gridDataStatusLine);
		
        // Set focus
        
		if (actionType == DialogActionTypes.CREATE) {
			if (textName.getText().equals("_DESIGNER")) {
		        textPort.setFocus();
			}
			else {
		        textName.setFocus();
			}
		}
		else if (actionType == DialogActionTypes.CHANGE) {
			if (textName.getText().equals("_DESIGNER")) {
		        textPort.setFocus();
			}
			else {
		        textDevice.setFocus();
			}
		}
		else {
			textConnection.setFocus();
		}

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
		switch (actionType) {
			case DialogActionTypes.CREATE: {
				convertData();
				if (checkData()) {
					transferData();
					return true;
				}
				return false;
			}
			case DialogActionTypes.CHANGE: {
				convertData();
				if (checkData()) {
					transferData();
					return true;
				}
				return false;
			}
			case DialogActionTypes.DELETE: {
				return true;
			}
			case DialogActionTypes.DISPLAY: {
				return true;
			}
		}
		return false;
	}
	
	protected void convertData() {
		textName.setText(textName.getText().trim());
		textDevice.setText(textDevice.getText().toUpperCase().trim());
		textPort.setText(textPort.getText().toUpperCase().trim());
		comboCodePage.setText(comboCodePage.getText().trim());
		textUser.setText(textUser.getText().trim());
		textPassWord.setText(textPassWord.getText().trim());
		textProgram.setText(textProgram.getText().toUpperCase().trim());
		textLibrary.setText(textLibrary.getText().toUpperCase().trim());
		textMenu.setText(textMenu.getText().toUpperCase().trim());
	}
	
	protected boolean checkData() {
		if (actionType == DialogActionTypes.CREATE) {
			
			// The value in field 'Name' is not valid.
			
			if (textName.getText().equals("")) {
				setErrorMessage(Messages.getString("The_value_in_field_'Name'_is_not_valid."));
				textName.setFocus();
				return false;
			}
			
			// The name '_DESIGNER' is reserved.
			
			if (session.getName().equals("") && textName.getText().equals("_DESIGNER")) {
				setErrorMessage(Messages.getString("The_name_'_DESIGNER'_is_reserved."));
				textName.setFocus();
				return false;
			}
			
			// The name does already exist.
			
			if (new File(sessionDirectory + File.separator + textName.getText()).exists()) {
				setErrorMessage(Messages.getString("The_name_does_already_exist."));
				textName.setFocus();
				return false;
			}			
		}
		
		// The value in field 'Device' is not valid.
		
		if (!textDevice.getText().equals("")) {
			if (textDevice.getText().equals("")) {
				setErrorMessage(Messages.getString("The_value_in_field_'Device'_is_not_valid."));
				textDevice.setFocus();
				return false;
			}
		}
		
		// The value in field 'Port' is not valid.
		boolean error = false;
		if (textPort.getText().equals("")) {
			error = true;
		}
		else {
			try {
				int result = Integer.parseInt(textPort.getText());
				if (result == 0) {
					error = true;
				}
			} 
			catch (NumberFormatException e1) {
				error = true;
			}
		}
		if (error) {	
			setErrorMessage(Messages.getString("The_value_in_field_'Port'_is_not_valid."));
			textPort.setFocus();
			return false;
		}
		
		// The value in field 'Codepage' is not valid.
		error = true;
		for (int idx = 0; idx < codePages.length; idx++) {
		    if (codePages[idx].equals(comboCodePage.getText())) {
		    	error = false;
		    	break;
		    }
		}
		if (error) {	
			setErrorMessage(Messages.getString("The_value_in_field_'Codepage'_is_not_valid."));
			comboCodePage.setFocus();
			return false;
		}
		
		// The value in field 'User' is not valid.
		
		if (textName.getText().equals("_DESIGNER") && textUser.getText().equals("")) {
			setErrorMessage(Messages.getString("The_value_in_field_'User'_is_not_valid."));
			textUser.setFocus();
			return false;
		}
		
		// The value in field 'Password' is not valid.
		
		if (textName.getText().equals("_DESIGNER") && textPassWord.getText().equals("")) {
			setErrorMessage(Messages.getString("The_value_in_field_'Password'_is_not_valid."));
			textPassWord.setFocus();
			return false;
		}
		
		// The value in field 'Program' is not valid.
		
		if (!textProgram.getText().equals("")) {
			error = false;
			if (error) {
				setErrorMessage(Messages.getString("The_value_in_field_'Program'_is_not_valid."));
				textProgram.setFocus();
				return false;
			}
		}

		// The value in field 'Library' is not valid.
		
		if (!textLibrary.getText().equals("")) {
			error = false;
			if (error) {
				setErrorMessage(Messages.getString("The_value_in_field_'Library'_is_not_valid."));
				textLibrary.setFocus();
				return false;
			}
		}
		
		// The value in field 'Menu' is not valid.
		
		if (!textMenu.getText().equals("")) {
			error = false;
			if (error) {
				setErrorMessage(Messages.getString("The_value_in_field_'Menu'_is_not_valid."));
				textMenu.setFocus();
				return false;
			}
		}
		
		// Everything is alright
		return true;
	}

	protected void transferData() {
		session.setName(textName.getText());
		session.setDevice(textDevice.getText());
		session.setPort(textPort.getText());
		session.setCodePage(comboCodePage.getText());
		if (buttonScreenSize27_132.getSelection()) {
			session.setScreenSize("132");
		}
		else {
			session.setScreenSize("");
		}
	//	if (buttonEnhancedMode.getSelection()) {
	//		session.setEnhancedMode("Y");
	//	}
	//	else {
	//		session.setEnhancedMode("");
	//	}
		if (buttonView.getSelection()) {
			session.setArea("*VIEW");
		}
		else {
			session.setArea("*EDITOR");
		}
		session.setUser(textUser.getText());
		
		if (textPassWord.getText().equals("")) {
			session.setPassword("");
		}
		else {
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			textEncryptor.setPassword(TN5250JCorePlugin.BASIC);
			String encryptedPassword = textEncryptor.encrypt(textPassWord.getText());
			session.setPassword(encryptedPassword);
		}
		
		session.setProgram(textProgram.getText());
		session.setLibrary(textLibrary.getText());
		session.setMenu(textMenu.getText());
	}

}
