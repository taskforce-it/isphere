/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.messagesubsystem.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.Messages;

import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QueuedMessage;

public class MessageMonitorThread extends Thread {

    private MonitoredMessageQueue messageQueue;
    private MonitoringAttributes monitoringAttributes;
    private QueuedMessageFilter messageFilter;
    private IMessageHandler messageHandler;
    private String messageAction;
    private String messageType;

    private boolean monitoring;
    private String errorMessage;

    private final static String END_MONITORING = "*END_MONITORING"; //$NON-NLS-1$

    public MessageMonitorThread(MonitoredMessageQueue messageQueue, MonitoringAttributes monitoringAttributes, QueuedMessageFilter messageFilter,
        IMessageHandler messageHandler, String action, String type) {
        super("iSphere Message Monitor");

        this.messageQueue = messageQueue;
        this.monitoringAttributes = monitoringAttributes;
        this.messageFilter = messageFilter;
        this.messageHandler = messageHandler;
        this.messageAction = action;
        this.messageType = type;
    }

    @Override
    public void run() {

        monitoring = true;

        try {
            while (monitoring && monitoringAttributes.isMonitoring()) {
                QueuedMessage message = messageQueue.receive(null, 20, messageAction, messageType);
                if (monitoring && (message != null)) {
                    handleMessage(message);
                }
            }
        } catch (Exception e) {
            monitoring = false;
            if (e.getMessage() == null)
                errorMessage = e.toString();
            else
                errorMessage = e.getMessage();
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Message_Queue_Monitoring_Error, errorMessage);
                }
            });
        }

        messageQueue.messageMonitorStopped();
    }

    public void stopMonitoring() {

        if (!monitoring) {
            return;
        }

        monitoring = false;
    }

    private void handleMessage(QueuedMessage message) throws Exception {

        if (END_MONITORING.equals(message.getText())) {
            if (!MessageQueue.REMOVE.equals(messageAction)) {
                messageQueue.remove(message.getKey());
            }
            monitoring = false;
        } else {
            if (includeMessage(message)) {
                if (messageHandler != null) {
                    messageHandler.handleMessage(message, messageQueue);
                }
            }
        }
    }

    private boolean includeMessage(QueuedMessage message) {

        if (messageFilter == null) {
            return true;
        }

        if (messageFilter.getUser() != null) {
            if ((message.getUser() == null) || !message.getUser().equals(messageFilter.getUser())) {
                return false;
            }
        }

        if (messageFilter.getId() != null) {
            if ((message.getID() == null) || !message.getID().equals(messageFilter.getId())) {
                return false;
            }
        }

        if (messageFilter.getFromJobName() != null) {
            if ((message.getFromJobName() == null) || !message.getFromJobName().equals(messageFilter.getFromJobName())) {
                return false;
            }
        }

        if (messageFilter.getFromJobNumber() != null) {
            if ((message.getFromJobNumber() == null) || !message.getFromJobNumber().equals(messageFilter.getFromJobNumber())) {
                return false;
            }
        }

        if (messageFilter.getFromProgram() != null) {
            if ((message.getFromProgram() == null) || !message.getFromProgram().equals(messageFilter.getFromProgram())) {
                return false;
            }
        }

        if (messageFilter.getText() != null) {
            if ((message.getText() == null) || (message.getText().indexOf(messageFilter.getText()) < 0)) {
                return false;
            }
        }

        if (messageFilter.getSeverity() != -1) {
            if (message.getSeverity() < messageFilter.getSeverity()) {
                return false;
            }
        }

        if (messageFilter.getMessageType() != -1) {
            if (message.getType() != messageFilter.getMessageType()) {
                return false;
            }
        }

        if (messageFilter.getDate() != null) {
            if (messageFilter.getDate().after(message.getDate().getTime())) {
                return false;
            }
        }

        return true;
    }

}
