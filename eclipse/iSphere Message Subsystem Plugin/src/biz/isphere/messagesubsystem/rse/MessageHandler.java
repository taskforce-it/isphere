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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.MessageQueueMailMessenger;

import com.ibm.as400.access.QueuedMessage;

public class MessageHandler implements IMessageHandler {

    private IQueuedMessageSubsystem queuedMessageSubSystem;

    public MessageHandler(IQueuedMessageSubsystem queuedMessageSubSystem) {
        super();

        this.queuedMessageSubSystem = queuedMessageSubSystem;
    }

    public void handleMessage(ReceivedMessage message) {

        MonitoringAttributes monitoringAttributes = new MonitoringAttributes(queuedMessageSubSystem);
        if (!monitoringAttributes.isMonitoringEnabled()) {
            return;
        }

        final ReceivedMessage msg = message;
        Display.getDefault().syncExec(new Runnable() {
            public void run() {

                MonitoringAttributes monitoringAttributes = new MonitoringAttributes(queuedMessageSubSystem);

                String handling = getMessageHandling(monitoringAttributes);

                if (MonitoringAttributes.NOTIFICATION_TYPE_BEEP.equals(handling)) {
                    Display.getDefault().beep();
                }

                if (MonitoringAttributes.NOTIFICATION_TYPE_EMAIL.equals(handling)) {

                    if (!monitoringAttributes.isValid()) {
                        if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), Messages.ISeries_Message_Email_Error,
                            Messages.Email_Notification_Error_Message)) {
                            if (msg.getType() == QueuedMessage.INQUIRY) {
                                monitoringAttributes.setInqueryMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                            } else {
                                monitoringAttributes.setInformationalMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                            }
                            handling = getMessageHandling(monitoringAttributes);
                        }
                    } else {

                        MessageQueueMailMessenger messenger = new MessageQueueMailMessenger();
                        messenger.setRecipients(new String[] { monitoringAttributes.getEmail() });
                        messenger.setMailFrom(monitoringAttributes.getFrom());
                        messenger.setHost(monitoringAttributes.getHost());
                        messenger.setPort(monitoringAttributes.getPort());

                        try {
                            if (monitoringAttributes.isSmtpLogin()) {
                                messenger.sendMail(msg, monitoringAttributes.getSmtpUser(), monitoringAttributes.getSmtpPassword());
                            } else {
                                messenger.sendMail(msg);
                            }
                        } catch (Exception e) {

                            String errorMessage = e.getMessage();
                            if (errorMessage == null) {
                                errorMessage = e.toString();
                            }

                            errorMessage = errorMessage + Messages.Email_Notification_Properties_Error_message;
                            Display.getDefault().beep();
                            if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), Messages.ISeries_Message_Email_Error, errorMessage)) {
                                if (msg.getType() == QueuedMessage.INQUIRY) {
                                    monitoringAttributes.setInqueryMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                                } else {
                                    monitoringAttributes.setInformationalMessageNotificationType(MonitoringAttributes.NOTIFICATION_TYPE_DIALOG);
                                }
                                handling = getMessageHandling(monitoringAttributes);
                            }
                        }
                    }
                }

                if (MonitoringAttributes.NOTIFICATION_TYPE_DIALOG.equals(handling)) {

                    Display.getDefault().beep();
                    QueuedMessageDialog dialog = new QueuedMessageDialog(Display.getDefault().getActiveShell(), msg, false);
                    dialog.open();
                }

                if (!MonitoringAttributes.NOTIFICATION_TYPE_BEEP.equals(handling)) {
                    removeInformationalMessage(msg, monitoringAttributes);
                }
            }

            private String getMessageHandling(MonitoringAttributes monitoringAttributes) {
                if (msg.getType() == QueuedMessage.INQUIRY) {
                    return monitoringAttributes.getInqueryMessageNotificationType();
                } else {
                    return monitoringAttributes.getInformationalMessageNotificationType();
                }
            }

            private void removeInformationalMessage(final ReceivedMessage msg, MonitoringAttributes monitoringAttributes) {

                if (monitoringAttributes.removeInformationalMessages() && (msg.getType() != QueuedMessage.INQUIRY)) {
                    try {
                        msg.getQueue().remove(msg.getKey());
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
}
