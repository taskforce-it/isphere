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
import biz.isphere.messagesubsystem.preferences.Preferences;

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

        Composite scrollableContainer = new Composite(parent, SWT.NONE);
        scrollableContainer.setLayout(new GridLayout(1, false));
        scrollableContainer.setLayoutData(new GridData(GridData.FILL_BOTH));

        ScrolledComposite scrollable = new ScrolledComposite(scrollableContainer, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
        scrollable.setLayout(new GridLayout());
        scrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollable.setExpandHorizontal(true);
        scrollable.setExpandVertical(true);

        int numColumns = 2;
        Composite mainPanel = new Composite(scrollable, SWT.NONE);
        mainPanel.setLayout(new GridLayout(numColumns, false));
        mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollable.setContent(mainPanel);

        createMessagePanel(mainPanel);
        createSpacerPanel(mainPanel, numColumns);

        scrollable.setMinSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return scrollable;
    }

    private Composite createMessagePanel(Composite parent) {

        Composite panel = parent;

        Label idLabel = new Label(panel, SWT.NONE);
        idLabel.setText(Messages.Message_ID_colon);

        Text idText = WidgetFactory.createReadOnlyText(panel);
        idText.setLayoutData(createFieldLayoutData());
        idText.setEnabled(false);
        if (receivedMessage.getID() != null) {
            idText.setText(receivedMessage.getID());
        }

        Label sevLabel = new Label(panel, SWT.NONE);
        sevLabel.setText(Messages.Severity_colon);

        Text sevText = WidgetFactory.createReadOnlyText(panel);
        sevText.setLayoutData(createFieldLayoutData());
        sevText.setEnabled(false);
        sevText.setText(new Integer(receivedMessage.getSeverity()).toString());

        Label typeLabel = new Label(panel, SWT.NONE);
        typeLabel.setText(Messages.Message_type_colon);

        Text typeText = WidgetFactory.createReadOnlyText(panel);
        typeText.setLayoutData(createFieldLayoutData());
        typeText.setEnabled(false);
        typeText.setText(receivedMessage.getMessageType());

        Label dateLabel = new Label(panel, SWT.NONE);
        dateLabel.setText(Messages.Date_sent_colon);

        Text dateText = WidgetFactory.createReadOnlyText(panel);
        dateText.setLayoutData(createFieldLayoutData());
        dateText.setEnabled(false);
        dateText.setText(receivedMessage.getDate().getTime().toString());

        Label userLabel = new Label(panel, SWT.NONE);
        userLabel.setText(Messages.From_colon);

        Text userText = WidgetFactory.createReadOnlyText(panel);
        userText.setLayoutData(createFieldLayoutData());
        userText.setEnabled(false);
        if (receivedMessage.getUser() != null) {
            userText.setText(receivedMessage.getUser());
        }

        if (Preferences.getInstance().isReplyFieldBeforeMessageText()) {
            createReplyPanel(panel);
        }

        Label msgTextLabel = new Label(panel, SWT.NONE);
        msgTextLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        msgTextLabel.setText(Messages.Message_colon);

        Text msgText = WidgetFactory.createReadOnlyMultilineText(panel, true, false);
        msgText.setLayoutData(createTextBoxLayoutData());
        msgText.setFont(FontHelper.getFixedSizeFont());
        msgText.setText(receivedMessage.getText());

        if (receivedMessage.getHelpFormatted() != null && !receivedMessage.getHelpFormatted().equals(receivedMessage.getText())) {

            // Place holder label for message help text box
            new Label(panel, SWT.NONE);

            Text msgHelp = WidgetFactory.createReadOnlyMultilineText(panel, true, false);
            msgHelp.setLayoutData(createTextBoxLayoutData());
            msgHelp.setFont(FontHelper.getFixedSizeFont());
            msgHelp.setText(messageFormatter.formatHelpText(receivedMessage.getHelpFormatted()));
        }

        if (!Preferences.getInstance().isReplyFieldBeforeMessageText()) {
            createReplyPanel(panel);
        }

        return panel;
    }

    private Composite createReplyPanel(Composite parent) {

        Composite panel = parent;

        if (receivedMessage.isInquiryMessage()) {

            Label replyLabel = new Label(panel, SWT.NONE);
            replyLabel.setText(Messages.Reply_colon);

            responseText = WidgetFactory.createText(panel);
            responseText.setLayoutData(createFieldLayoutData());
            responseText.setTextLimit(132);
            if (receivedMessage.getDefaultReply() != null) {
                responseText.setText(receivedMessage.getDefaultReply());
            } else {
                responseText.setText(""); //$NON-NLS-1$
            }
            responseText.setFocus();

            // TODO: remove debug code
            String position;
            if (Preferences.getInstance().isReplyFieldBeforeMessageText()) {
                position = "before"; //$NON-NLS-1$
            } else {
                position = "after"; //$NON-NLS-1$
            }

            logDebugMessage("Debug --> Message reply field created " + position + " message text field."); //$NON-NLS-1$
        } else {
            logDebugMessage("Debug --> No reply message. Message type is: " + receivedMessage.getReplyStatusAsText()); //$NON-NLS-1$
        }

        return panel;
    }

    private GridData createFieldLayoutData() {

        GridData gd = new GridData(200, SWT.DEFAULT);

        return gd;
    }

    private GridData createTextBoxLayoutData() {

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 0;

        return gd;
    }

    // TODO: remove debug code
    private void logDebugMessage(String message) {

        if (Preferences.getInstance().isDebugEnabled()) {
            ISpherePlugin.logError(message, null);
        }
    }

    private Composite createSpacerPanel(Composite parent, int numColums) {

        Composite spacer = new Composite(parent, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 0;
        gd.horizontalSpan = numColums;
        spacer.setLayoutData(gd);

        return spacer;
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
            if (getTrimmedText(responseText).length() > 0) {
                MessageQueue messageQueue = receivedMessage.getQueue();
                try {
                    messageQueue.reply(receivedMessage.getKey(), getTrimmedText(responseText));
                } catch (Exception e) {
                    String errorMessage = e.getMessage();
                    if (errorMessage == null) errorMessage = e.toString();
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.ISeries_Message_Reply_Error, errorMessage);
                    return;
                }
            }
        }
    }

    private String getTrimmedText(Text text) {

        if (text == null) {
            return ""; //$NON-NLS-1$
        }

        return text.getText().trim();
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
