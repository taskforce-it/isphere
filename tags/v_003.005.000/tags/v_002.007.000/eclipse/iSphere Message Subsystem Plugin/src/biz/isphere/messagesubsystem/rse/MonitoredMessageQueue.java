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
import java.util.Enumeration;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.MessageMonitorThread;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QueuedMessage;

public class MonitoredMessageQueue extends MessageQueue {

    private static final long serialVersionUID = 2988890902520435974L;

    private QueuedMessageFilter messageFilter;
    private IMessageHandler messageHandler;
    private MonitoringAttributes monitoringAttributes;

    private boolean monitoring;
    private String messageAction;
    private String messageType;
    private MessageMonitorThread monitoringThread;

    public MonitoredMessageQueue(AS400 system, String path, QueuedMessageFilter filter) {
        this(system, path, filter, null, null);
    }

    public MonitoredMessageQueue(AS400 system, String path, QueuedMessageFilter filter, IMessageHandler messageHandler,
        MonitoringAttributes monitoringAttributes) {
        super(system, path);

        this.messageFilter = filter;
        this.messageHandler = messageHandler;
        this.monitoringAttributes = monitoringAttributes;
    }

    public QueuedMessage[] getFilteredMessages() throws Exception {

        ArrayList<QueuedMessage> messages = new ArrayList<QueuedMessage>();

        Enumeration<?> enumx = getMessages();
        while (enumx.hasMoreElements()) {
            QueuedMessage message = (QueuedMessage)enumx.nextElement();
            if (includeMessage(message)) {
                messages.add(message);
            }
        }

        QueuedMessage[] messageArray = new QueuedMessage[messages.size()];
        messages.toArray(messageArray);

        return messageArray;
    }

    public void startMonitoring(String action, String type) {

        messageAction = action;
        messageType = type;

        if (monitoringThread == null) {
            monitoring = true;
            monitoringThread = createMonitoringThread();
            monitoringThread.setDaemon(true);
            monitoringThread.start();
        }
    }

    public void stopMonitoring() {

        if (monitoringThread == null) {
            return;
        }

        try {
            monitoringThread.stopMonitoring();
        } catch (Exception e) {
            String errorMessage = null;
            if (e.getMessage() == null)
                errorMessage = e.toString();
            else
                errorMessage = e.getMessage();
            MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Message_Queue_Monitoring_Error, errorMessage);
        }
    }

    public void messageMonitorStopped() {
        monitoringThread = null;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setFilter(QueuedMessageFilter filter) {
        this.messageFilter = filter;
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

    private MessageMonitorThread createMonitoringThread() {

        MessageMonitorThread monitorThread = new MessageMonitorThread(this, monitoringAttributes, messageFilter, messageHandler, messageAction,
            messageType);
        return monitorThread;
    }
}
