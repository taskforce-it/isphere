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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

public class QueuedMessageDialog extends XDialog {

    private ReceivedMessage receivedMessage;
    private Text responseText;
    private boolean createCancelButton;
    private boolean createOKToAllButton;
    private BasicMessageFormatter messageFormatter;

    public QueuedMessageDialog(Shell shell, ReceivedMessage queuedMessage) {
        this(shell, queuedMessage, true);
    }

    public QueuedMessageDialog(Shell shell, ReceivedMessage queuedMessage, boolean createCancelButton) {
        this(shell, queuedMessage, createCancelButton, false);
    }

    public QueuedMessageDialog(Shell shell, ReceivedMessage queuedMessage, boolean createCancelButton, boolean createOKToAllButton) {
        super(shell);
        this.receivedMessage = queuedMessage;
        this.createCancelButton = createCancelButton;
        this.createOKToAllButton = createOKToAllButton;
        this.messageFormatter = new BasicMessageFormatter();
    }

    @Override
    public Control createDialogArea(Composite parent) {

        parent.getShell().setText(Messages.iSeries_Message);

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        Composite mainPanel = new Composite(scrolledComposite, SWT.NONE);
        GridLayout headerLayout = new GridLayout(2, false);
        mainPanel.setLayout(headerLayout);
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label idLabel = new Label(mainPanel, SWT.NONE);
        idLabel.setText(Messages.Message_ID_colon);

        Text idText = WidgetFactory.createReadOnlyText(mainPanel);
        idText.setLayoutData(new GridData(200, SWT.DEFAULT));
        idText.setEnabled(false);
        if (receivedMessage.getID() != null) {
            idText.setText(receivedMessage.getID());
        }

        Label sevLabel = new Label(mainPanel, SWT.NONE);
        sevLabel.setText(Messages.Severity_colon);

        Text sevText = WidgetFactory.createReadOnlyText(mainPanel);
        sevText.setLayoutData(new GridData(200, SWT.DEFAULT));
        sevText.setEnabled(false);
        sevText.setText(new Integer(receivedMessage.getSeverity()).toString());

        Label typeLabel = new Label(mainPanel, SWT.NONE);
        typeLabel.setText(Messages.Message_type_colon);

        Text typeText = WidgetFactory.createReadOnlyText(mainPanel);
        typeText.setLayoutData(new GridData(200, SWT.DEFAULT));
        typeText.setEnabled(false);
        typeText.setText(receivedMessage.getMessageType());

        Label dateLabel = new Label(mainPanel, SWT.NONE);
        dateLabel.setText(Messages.Date_sent_colon);

        Text dateText = WidgetFactory.createReadOnlyText(mainPanel);
        dateText.setLayoutData(new GridData(200, SWT.DEFAULT));
        dateText.setEnabled(false);
        dateText.setText(receivedMessage.getDate().getTime().toString());

        Label userLabel = new Label(mainPanel, SWT.NONE);
        userLabel.setText(Messages.From_colon);

        Text userText = WidgetFactory.createReadOnlyText(mainPanel);
        userText.setLayoutData(new GridData(200, SWT.DEFAULT));
        userText.setEnabled(false);
        if (receivedMessage.getUser() != null) {
            userText.setText(receivedMessage.getUser());
        }

        Label msgTextLabel = new Label(mainPanel, SWT.NONE);
        msgTextLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        msgTextLabel.setText(Messages.Message_colon);

        Text msgText = WidgetFactory.createReadOnlyMultilineText(mainPanel, true, false);
        msgText.setLayoutData(new GridData(GridData.FILL_BOTH));
        msgText.setFont(FontHelper.getFixedSizeFont());
        msgText.setText(receivedMessage.getText());

        if (receivedMessage.getHelpFormatted() != null && !receivedMessage.getHelpFormatted().equals(receivedMessage.getText())) {

            // Place holder label for message help text box
            new Label(mainPanel, SWT.NONE);

            Text msgHelp = WidgetFactory.createReadOnlyMultilineText(mainPanel, true, false);
            msgHelp.setLayoutData(new GridData(GridData.FILL_BOTH));
            msgHelp.setFont(FontHelper.getFixedSizeFont());
            msgHelp.setText(messageFormatter.formatHelpText(receivedMessage.getHelpFormatted()));
        }

        if (receivedMessage.isInquiryMessage() && receivedMessage.isPendingReply()) {

            Label replyLabel = new Label(mainPanel, SWT.NONE);
            replyLabel.setText(Messages.Reply_colon);

            responseText = WidgetFactory.createText(mainPanel);
            responseText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            responseText.setTextLimit(132);
            if (receivedMessage.getDefaultReply() != null) {
                responseText.setText(receivedMessage.getDefaultReply());
            } else {
                responseText.setText("");
            }
            responseText.setFocus();
        }

        scrolledComposite.setContent(mainPanel);
        scrolledComposite.setMinSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return scrolledComposite;
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
        processInquiryMessage();
        super.okPressed();
    }

    public void okToAllPressed() {
        processInquiryMessage();
        setReturnCode(IDialogConstants.YES_TO_ALL_ID);
        close();
    }

    private void processInquiryMessage() {

        if (receivedMessage.isInquiryMessage()) {
            if ((responseText.getText() != null) && (responseText.getText().trim().length() > 0)) {
                MessageQueue messageQueue = receivedMessage.getQueue();
                try {
                    messageQueue.reply(receivedMessage.getKey(), responseText.getText());
                } catch (Exception e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage == null) errorMessage = e.toString();
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.ISeries_Message_Reply_Error, errorMessage);
                    return;
                }
            }
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button okToAll = createButton(parent, IDialogConstants.YES_TO_ALL_ID, Messages.OK_To_All_LABEL, false);
        if (okToAll != null) {
            okToAll.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent arg0) {
                    okToAllPressed();
                }

                public void widgetDefaultSelected(SelectionEvent arg0) {
                }
            });
        }
        super.createButtonsForButtonBar(parent);
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        if (id == IDialogConstants.CANCEL_ID && !createCancelButton) {
            return null;
        }
        if (id == IDialogConstants.YES_TO_ALL_ID && !createOKToAllButton) {
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
