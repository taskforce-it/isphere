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
package biz.isphere.messagesubsystem.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.Messages;

import com.ibm.as400.access.QueuedMessage;

public class MessageHandler implements IMessageHandler {

    private QueuedMessageSubSystem queuedMessageSubSystem;

    public MessageHandler(QueuedMessageSubSystem queuedMessageSubSystem) {
        super();

        this.queuedMessageSubSystem = queuedMessageSubSystem;
    }

    public void handleMessage(QueuedMessage message, MonitoredMessageQueue messageQueue) {

        MonitoringAttributes monitoringAttributes = new MonitoringAttributes(queuedMessageSubSystem);
        if (!monitoringAttributes.isMonitoring()) {
            return;
        }
        
        final QueuedMessage msg = message;
        Display.getDefault().syncExec(new Runnable() {
            public void run() {

                MonitoringAttributes monitoringAttributes = new MonitoringAttributes(queuedMessageSubSystem);

                String handling;

                if (msg.getType() == QueuedMessage.INQUIRY) {
                    handling = monitoringAttributes.getInqueryMessageNotificationType();
                } else {
                    handling = monitoringAttributes.getInformationalMessageNotificationType();
                }

                if (MonitoringAttributes.NOTIFICATION_TYPE_BEEP.equals(handling)) {
                    Display.getDefault().beep();
                }

                if (MonitoringAttributes.NOTIFICATION_TYPE_EMAIL.equals(handling)) {

                    if (!monitoringAttributes.isValid()) {
                        if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), Messages.ISeries_Message_Email_Error,
                            Messages.Email_Notification_Error_Message)) {
                            QueuedMessageDialog dialog = new QueuedMessageDialog(Display.getDefault().getActiveShell(), msg, false);
                            Display.getDefault().beep();
                            dialog.open();
                            return;
                        }
                    }

                    MessageQueueMailMessenger messenger = new MessageQueueMailMessenger();
                    messenger.setRecipients(new String[] { monitoringAttributes.getEmail() });
                    messenger.setMailFrom(monitoringAttributes.getFrom());
                    messenger.setHost(monitoringAttributes.getHost());
                    messenger.setPort(monitoringAttributes.getPort());

                    try {
                        messenger.sendMail(msg);
                    } catch (Exception e) {

                        String errorMessage = e.getMessage();
                        if (errorMessage == null) {
                            errorMessage = e.toString();
                        }

                        errorMessage = errorMessage + Messages.Email_Notification_Properties_Error_message;
                        Display.getDefault().beep();
                        if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), Messages.ISeries_Message_Email_Error, errorMessage)) {
                            handling = MonitoringAttributes.NOTIFICATION_TYPE_DIALOG;
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

            private void removeInformationalMessage(final QueuedMessage msg, MonitoringAttributes monitoringAttributes) {

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
