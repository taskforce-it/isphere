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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.MessageQueueMailMessenger;
import biz.isphere.messagesubsystem.internal.QueuedMessageListDialog;

import com.ibm.as400.access.QueuedMessage;

/**
 * This class handles messages that have been received by the message monitor
 * thread.
 */
public class MessageHandler implements IMessageHandler {

    private MonitoringAttributes monitoringAttributes;
    private boolean createOKToAllButton;
    private boolean isOKToAll;

    /**
     * Produces a MessageHandler object.
     * 
     * @param monitoringAttributes - Monitoring attributes.
     */
    public MessageHandler(MonitoringAttributes monitoringAttributes) {
        super();

        this.monitoringAttributes = monitoringAttributes;
        this.createOKToAllButton = false;
        this.isOKToAll = false;
    }

    /**
     * Handles a list of message that have been received by the message monitor
     * thread on startup time.
     */
    public void handleMessages(List<ReceivedMessage> messages) {

        if (messages == null) {
            return;
        }

        isOKToAll = false;
        if (messages.size() == 1) {
            createOKToAllButton = false;
        } else {
            createOKToAllButton = true;
        }

        List<ReceivedMessage> dialogMessages = new ArrayList<ReceivedMessage>();

        for (ReceivedMessage receivedMessage : messages) {
            if (monitoringAttributes.isDialogHandler(receivedMessage)) {
                dialogMessages.add(receivedMessage);
            } else {
                handleMessage(monitoringAttributes, receivedMessage);
            }
        }

        Display.getDefault().syncExec(
            new UIMessageListHandler(monitoringAttributes, dialogMessages.toArray(new ReceivedMessage[dialogMessages.size()])));

        isOKToAll = false;
        createOKToAllButton = false;
    }

    /**
     * Handles a message that has been received by the message monitor thread.
     */
    public void handleMessage(ReceivedMessage message) {
        handleMessage(monitoringAttributes, message);
    }

    /**
     * Internal procedure that handles a given message.
     * 
     * @param monitoringAttributes - settings of the message monitor
     * @param message - message that must be handled
     */
    private void handleMessage(MonitoringAttributes monitoringAttributes, ReceivedMessage message) {

        if (!monitoringAttributes.isMonitoringEnabled()) {
            return;
        }

        Display.getDefault().syncExec(new UIMessageHandler(monitoringAttributes, message));
    }

    /**
     * Removes an informational message from the message queue, depending on the
     * monitor settings. The "remove informational messages" option is ignored
     * when the "Beep" handler is activated.
     * 
     * @param message - message that is removed from the message queue
     * @param monitoringAttributes - settings of the message monitor
     */
    private void removeInformationalMessage(final ReceivedMessage message, MonitoringAttributes monitoringAttributes) {

        if (monitoringAttributes.isBeepHandler(message)) {
            return;
        }

        if (monitoringAttributes.isRemoveInformationalMessages() && (message.getType() != QueuedMessage.INQUIRY)) {
            try {
                message.getQueue().remove(message.getKey());
            } catch (Exception e) {
                // gracefully ignore exceptions
            }
        }
    }

    /**
     * Internal class that handles the actual received messages.
     * <p>
     * Optionally removes informational messages from the message queue
     * afterwards, depending on the message monitor settings.
     */
    private class UIMessageHandler implements Runnable {

        private MonitoringAttributes monitoringAttributes;
        private ReceivedMessage message;

        public UIMessageHandler(MonitoringAttributes monitoringAttributes, ReceivedMessage message) {
            this.monitoringAttributes = monitoringAttributes;
            this.message = message;
        }

        public void run() {

            if (monitoringAttributes.isBeepHandler(message)) {
                Display.getDefault().beep();
            }

            if (monitoringAttributes.isEmailHandler(message)) {

                if (!monitoringAttributes.isValid()) {
                    if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), Messages.ISeries_Message_Email_Error,
                        Messages.Email_Notification_Error_Message)) {
                        if (message.getType() == QueuedMessage.INQUIRY) {
                            monitoringAttributes.setInqueryMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                        } else {
                            monitoringAttributes.setInformationalMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                        }
                    }
                } else {

                    MessageQueueMailMessenger messenger = new MessageQueueMailMessenger();
                    messenger.setRecipients(new String[] { monitoringAttributes.getEmail() });
                    messenger.setMailFrom(monitoringAttributes.getFrom());
                    messenger.setHost(monitoringAttributes.getHost());
                    messenger.setPort(monitoringAttributes.getPort());

                    try {
                        if (monitoringAttributes.isSmtpLogin()) {
                            messenger.sendMail(message, monitoringAttributes.getSmtpUser(), monitoringAttributes.getSmtpPassword());
                        } else {
                            messenger.sendMail(message);
                        }
                    } catch (Exception e) {

                        String errorMessage = e.getMessage();
                        if (errorMessage == null) {
                            errorMessage = e.toString();
                        }

                        errorMessage = errorMessage + Messages.Email_Notification_Properties_Error_message;
                        Display.getDefault().beep();
                        if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), Messages.ISeries_Message_Email_Error, errorMessage)) {
                            if (message.getType() == QueuedMessage.INQUIRY) {
                                monitoringAttributes.setInqueryMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                            } else {
                                monitoringAttributes.setInformationalMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                            }
                        }
                    }
                }
            }

            if (!isOKToAll && monitoringAttributes.isDialogHandler(message)) {

                Display.getDefault().beep();
                QueuedMessageDialog dialog;
                if (message.getType() == QueuedMessage.INQUIRY) {
                    dialog = new QueuedMessageDialog(Display.getDefault().getActiveShell(), message, false, false);
                } else {
                    dialog = new QueuedMessageDialog(Display.getDefault().getActiveShell(), message, false, createOKToAllButton);
                }

                int rc = dialog.open();
                if (rc == IDialogConstants.YES_TO_ALL_ID) {
                    createOKToAllButton = false;
                    isOKToAll = true;
                }
            }

            removeInformationalMessage(message, monitoringAttributes);
        }
    }

    /**
     * Internal class that displays the list of pending informational messages,
     * when the message monitor starts.
     * <p>
     * Optionally removes the messages from the message queue afterwards,
     * depending on the message monitor settings.
     */
    private class UIMessageListHandler implements Runnable {

        private MonitoringAttributes monitoringAttributes;
        private ReceivedMessage[] messages;

        public UIMessageListHandler(MonitoringAttributes monitoringAttributes, ReceivedMessage[] messages) {
            this.monitoringAttributes = monitoringAttributes;
            this.messages = messages;
        }

        public void run() {

            QueuedMessageListDialog dialog = new QueuedMessageListDialog(Display.getDefault().getActiveShell(), monitoringAttributes, messages);
            dialog.open();

            for (ReceivedMessage message : messages) {
                removeInformationalMessage(message, monitoringAttributes);
            }
        }
    }
}
