/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.spooledfiles;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.Messages;

import com.ibm.as400.access.AS400Message;


public class SpooledFileMessageDialog extends Dialog {
	
	private SpooledFile spooledFile;
	private AS400Message message;
	private Text replyText;

	public SpooledFileMessageDialog(Shell shell, SpooledFile spooledFile) {
		super(shell);
		this.spooledFile = spooledFile;
	}
	
	public Control createDialogArea(Composite parent) {
		Composite rtnGroup = (Composite)super.createDialogArea(parent);
		parent.getShell().setText(Messages.getString("Spooled_File_Message"));
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		rtnGroup.setLayout(layout);
		rtnGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Group headerGroup = new Group(rtnGroup, SWT.NONE);
		GridLayout headerLayout = new GridLayout();
		headerLayout.numColumns = 2;
		headerGroup.setLayout(headerLayout);
		headerGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		try {	
			message = spooledFile.getMessage();
			Label msgidLabel = new Label(headerGroup, SWT.NONE);
			msgidLabel.setText(Messages.getString("Message_Id.") + ":");
			Text msgidText = new Text(headerGroup, SWT.BORDER);
			msgidText.setEnabled(false);
			msgidText.setText(message.getID());
			Label sevLabel = new Label(headerGroup, SWT.NONE);
			sevLabel.setText(Messages.getString("Severity") + ":");
			Text sevText = new Text(headerGroup, SWT.BORDER);
			sevText.setEnabled(false);
			sevText.setText(new Integer(message.getSeverity()).toString());
			Label typeLabel = new Label(headerGroup, SWT.NONE);
			typeLabel.setText(Messages.getString("Message_type") + ":");
			Text typeText = new Text(headerGroup, SWT.BORDER);
			typeText.setEnabled(false);
			switch (message.getType()) {
				case AS400Message.COMPLETION: 		typeText.setText(Messages.getString("Completion")); break;
				case AS400Message.DIAGNOSTIC:		typeText.setText(Messages.getString("Diagnostic")); break; 
				case AS400Message.ESCAPE:			typeText.setText(Messages.getString("Escape")); break;
				case AS400Message.INFORMATIONAL:	typeText.setText(Messages.getString("Informational")); break;
				case AS400Message.INQUIRY:			typeText.setText(Messages.getString("Inquiry")); break;
				case AS400Message.NOTIFY:			typeText.setText(Messages.getString("Notify")); break; 
				case AS400Message.REQUEST:			typeText.setText(Messages.getString("Request")); break; 
				default:
			}
			Label sentLabel = new Label(headerGroup, SWT.NONE);
			sentLabel.setText(Messages.getString("Sent") + ":");
			Text sentText = new Text(headerGroup, SWT.BORDER);
			sentText.setEnabled(false);
			sentText.setText(message.getDate().getTime().toString());
			
			Text msgText = new Text(rtnGroup, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			msgText.setEditable(false);
			GridData gd = new GridData();
			gd.widthHint = 400;
			gd.heightHint = 80;
			msgText.setLayoutData(gd);
			msgText.setText(message.getText());
			
			Text helpText = new Text(rtnGroup, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			helpText.setEditable(false);
			gd = new GridData();
			gd.widthHint = 400;
			gd.heightHint = 160;
			helpText.setLayoutData(gd);
			if (message.getHelp() != null) helpText.setText(format(message.getHelp()));
			
			Composite replyGroup = new Composite(rtnGroup, SWT.NONE);
			GridLayout replyLayout = new GridLayout();
			replyLayout.numColumns = 2;
			replyGroup.setLayout(replyLayout);
			replyGroup.setLayoutData(
			new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			
			Label replyLabel = new Label(replyGroup, SWT.NONE);
			replyLabel.setText(Messages.getString("Reply") + ":");
			replyText = new Text(replyGroup, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 300;
			replyText.setLayoutData(gd);
			if (message.getDefaultReply() != null) {
				replyText.setText(message.getDefaultReply());
				replyText.selectAll();
			}	
			replyText.setFocus();		
			
		} catch (Exception e) {handleError(e);}
		
		return rtnGroup;
	}
	
	public void okPressed() {
		try {
			spooledFile.answerMessage(replyText.getText());
		} catch (Exception e) {
			handleError(e);
			return;
		}
		super.okPressed();
	}
	
	private String format(String text) {
		for (int i = text.indexOf("&N"); i != -1;) {
			text = text.substring(0, i) + System.getProperty("line.separator") + "  " + text.substring(i + 2);
			i = text.indexOf("&N");
		}
		for (int i = text.indexOf("&B"); i != -1;) {
			text = text.substring(0, i) + System.getProperty("line.separator") + "    " + text.substring(i + 2);
			i = text.indexOf("&B");
		}
		for (int i = text.indexOf("&P"); i != -1;) {
			text = text.substring(0, i) + System.getProperty("line.separator") + "      " + text.substring(i + 2);
			i = text.indexOf("&P");
		}
		return text;
	}
	
	private void handleError(Exception e) {
		MessageDialog.openError(getShell(), Messages.getString("Error"), e.getMessage());
	}

}