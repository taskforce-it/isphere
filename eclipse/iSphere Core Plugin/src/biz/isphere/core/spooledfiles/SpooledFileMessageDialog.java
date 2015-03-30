/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

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

import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;

import com.ibm.as400.access.AS400Message;

public class SpooledFileMessageDialog extends Dialog {

    private SpooledFile spooledFile;
    private AS400Message message;
    private Text replyText;

    public SpooledFileMessageDialog(Shell shell, SpooledFile spooledFile) {
        super(shell);
        this.spooledFile = spooledFile;
    }

    @Override
    public Control createDialogArea(Composite parent) {
        Composite rtnGroup = (Composite)super.createDialogArea(parent);
        parent.getShell().setText(Messages.Spooled_File_Message);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        rtnGroup.setLayout(layout);
        rtnGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        Group headerGroup = new Group(rtnGroup, SWT.NONE);
        GridLayout headerLayout = new GridLayout();
        headerLayout.numColumns = 2;
        headerGroup.setLayout(headerLayout);
        headerGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        try {
            message = spooledFile.getMessage();
            Label msgidLabel = new Label(headerGroup, SWT.NONE);
            msgidLabel.setText(Messages.Message_Id + ":");
            Text msgidText = WidgetFactory.createText(headerGroup);
            msgidText.setEnabled(false);
            msgidText.setText(message.getID());
            Label sevLabel = new Label(headerGroup, SWT.NONE);
            sevLabel.setText(Messages.Severity + ":");
            Text sevText = WidgetFactory.createText(headerGroup);
            sevText.setEnabled(false);
            sevText.setText(new Integer(message.getSeverity()).toString());
            Label typeLabel = new Label(headerGroup, SWT.NONE);
            typeLabel.setText(Messages.Message_type + ":");
            Text typeText = WidgetFactory.createText(headerGroup);
            typeText.setEnabled(false);
            switch (message.getType()) {
            case AS400Message.COMPLETION:
                typeText.setText(Messages.Completion);
                break;
            case AS400Message.DIAGNOSTIC:
                typeText.setText(Messages.Diagnostic);
                break;
            case AS400Message.ESCAPE:
                typeText.setText(Messages.Escape);
                break;
            case AS400Message.INFORMATIONAL:
                typeText.setText(Messages.Informational);
                break;
            case AS400Message.INQUIRY:
                typeText.setText(Messages.Inquiry);
                break;
            case AS400Message.NOTIFY:
                typeText.setText(Messages.Notify);
                break;
            case AS400Message.REQUEST:
                typeText.setText(Messages.Request);
                break;
            default:
            }
            Label sentLabel = new Label(headerGroup, SWT.NONE);
            sentLabel.setText(Messages.Sent + ":");
            Text sentText = WidgetFactory.createText(headerGroup);
            sentText.setEnabled(false);
            sentText.setText(message.getDate().getTime().toString());

            Text msgText = WidgetFactory.createMultilineText(rtnGroup, true, false);
            msgText.setEditable(false);
            GridData gd = new GridData();
            gd.widthHint = 400;
            gd.heightHint = 80;
            msgText.setLayoutData(gd);
            msgText.setText(message.getText());

            Text helpText = WidgetFactory.createMultilineText(rtnGroup, true, false);
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
            replyGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

            Label replyLabel = new Label(replyGroup, SWT.NONE);
            replyLabel.setText(Messages.Reply + ":");
            replyText = WidgetFactory.createText(replyGroup);
            gd = new GridData();
            gd.widthHint = 300;
            replyText.setLayoutData(gd);
            if (message.getDefaultReply() != null) {
                replyText.setText(message.getDefaultReply());
                replyText.selectAll();
            }
            replyText.setFocus();

        } catch (Exception e) {
            handleError(e);
        }

        return rtnGroup;
    }

    @Override
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
        MessageDialog.openError(getShell(), Messages.Error, e.getMessage());
    }

}