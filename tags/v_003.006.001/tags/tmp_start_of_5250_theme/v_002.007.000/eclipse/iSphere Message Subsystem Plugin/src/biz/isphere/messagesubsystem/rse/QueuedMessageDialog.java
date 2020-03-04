/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.BasicMessageFormatter;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.messagesubsystem.Messages;

import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QueuedMessage;

public class QueuedMessageDialog extends XDialog {

    private ReceivedMessage queuedMessage;
    private Text responseText;
    private boolean createCancelButton;
    private BasicMessageFormatter messageFormatter;

    public QueuedMessageDialog(Shell shell, ReceivedMessage queuedMessage) {
        this(shell, queuedMessage, true);
    }

    public QueuedMessageDialog(Shell shell, ReceivedMessage queuedMessage, boolean createCancelButton) {
        super(shell);
        this.queuedMessage = queuedMessage;
        this.createCancelButton = createCancelButton;
        this.messageFormatter = new BasicMessageFormatter();
    }

    @Override
    public Control createDialogArea(Composite parent) {

        Composite promptGroup = (Composite)super.createDialogArea(parent);
        parent.getShell().setText(Messages.iSeries_Message);
        GridLayout layout = new GridLayout();
        promptGroup.setLayout(layout);
        promptGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite headerGroup = new Composite(promptGroup, SWT.NONE);
        GridLayout headerLayout = new GridLayout(2, false);
        headerGroup.setLayout(headerLayout);
        headerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label idLabel = new Label(headerGroup, SWT.NONE);
        idLabel.setText(Messages.Message_ID_colon);

        Text idText = WidgetFactory.createReadOnlyText(headerGroup);
        idText.setLayoutData(new GridData(200, SWT.DEFAULT));
        idText.setEnabled(false);
        if (queuedMessage.getID() != null) {
            idText.setText(queuedMessage.getID());
        }

        Label sevLabel = new Label(headerGroup, SWT.NONE);
        sevLabel.setText(Messages.Severity_colon);

        Text sevText = WidgetFactory.createReadOnlyText(headerGroup);
        sevText.setLayoutData(new GridData(200, SWT.DEFAULT));
        sevText.setEnabled(false);
        sevText.setText(new Integer(queuedMessage.getSeverity()).toString());

        Label typeLabel = new Label(headerGroup, SWT.NONE);
        typeLabel.setText(Messages.Message_type_colon);

        Text typeText = WidgetFactory.createReadOnlyText(headerGroup);
        typeText.setLayoutData(new GridData(200, SWT.DEFAULT));
        typeText.setEnabled(false);
        typeText.setText(queuedMessage.getMessageType());

        Label dateLabel = new Label(headerGroup, SWT.NONE);
        dateLabel.setText(Messages.Date_sent_colon);

        Text dateText = WidgetFactory.createReadOnlyText(headerGroup);
        dateText.setLayoutData(new GridData(200, SWT.DEFAULT));
        dateText.setEnabled(false);
        dateText.setText(queuedMessage.getDate().getTime().toString());

        Label userLabel = new Label(headerGroup, SWT.NONE);
        userLabel.setText(Messages.From_colon);

        Text userText = WidgetFactory.createReadOnlyText(headerGroup);
        userText.setLayoutData(new GridData(200, SWT.DEFAULT));
        userText.setEnabled(false);
        if (queuedMessage.getUser() != null) {
            userText.setText(queuedMessage.getUser());
        }

        Label msgTextLabel = new Label(headerGroup, SWT.NONE);
        msgTextLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        msgTextLabel.setText(Messages.Message_colon);

        Text msgText = WidgetFactory.createReadOnlyMultilineText(headerGroup, true, false);
        msgText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
        msgText.setEditable(false);
        msgText.setRedraw(true);
        msgText.setFont(FontHelper.getFixedSizeFont());

        // Place holder label
        new Label(headerGroup, SWT.NONE);

        Text msgHelp = WidgetFactory.createReadOnlyMultilineText(headerGroup, true, false);
        msgHelp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        msgHelp.setEditable(false);
        msgHelp.setRedraw(true);
        msgHelp.setFont(FontHelper.getFixedSizeFont());

        if ((queuedMessage.getHelpFormatted() == null) || (queuedMessage.getHelpFormatted().equals(queuedMessage.getText()))) {
            msgText.setText(queuedMessage.getText());
            msgHelp.setVisible(false);
        } else {
            msgText.setText(queuedMessage.getText());
            msgHelp.setText(messageFormatter.formatHelpText(queuedMessage.getHelpFormatted()));
        }

        if (queuedMessage.getType() == QueuedMessage.INQUIRY) {
            Label replyLabel = new Label(headerGroup, SWT.NONE);
            replyLabel.setText(Messages.Reply_colon);

            responseText = WidgetFactory.createText(headerGroup);
            responseText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            responseText.setTextLimit(132);
            if (queuedMessage.getDefaultReply() != null) {
                responseText.setText(queuedMessage.getDefaultReply());
            }
            responseText.setFocus();
        }

        return promptGroup;
    }

    @Override
    public int open() {

        if (getShell() == null) {
            // create the window
            create();
        }

        getShell().forceActive();

        return super.open();
    }

    @Override
    public void okPressed() {
        if (queuedMessage.getType() == QueuedMessage.INQUIRY) {
            if ((responseText.getText() != null) && (responseText.getText().trim().length() > 0)) {
                MessageQueue messageQueue = queuedMessage.getQueue();
                try {
                    messageQueue.reply(queuedMessage.getKey(), responseText.getText());
                } catch (Exception e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage == null) errorMessage = e.toString();
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.ISeries_Message_Reply_Error, errorMessage);
                    return;
                }
            }
        }
        super.okPressed();
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        if (id == IDialogConstants.CANCEL_ID && !createCancelButton) {
            return null;
        }
        return super.createButton(parent, id, label, defaultButton);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(Size.getSize(450), SWT.DEFAULT, true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

}
