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
import biz.isphere.messagesubsystem.internal.FilteredMessageQueue;
import biz.isphere.messagesubsystem.internal.MessageMonitorThread;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QueuedMessage;

public class MonitoredMessageQueue extends FilteredMessageQueue {

    private static final long serialVersionUID = 2988890902520435974L;

    private IMessageHandler messageHandler;
    private MonitoringAttributes monitoringAttributes;

    private boolean monitoring;
    private MessageMonitorThread monitoringThread;

    public MonitoredMessageQueue(AS400 system, String path, QueuedMessageFilter filter, IMessageHandler messageHandler,
        MonitoringAttributes monitoringAttributes) {
        super(system, path, filter);

        this.messageHandler = messageHandler;
        this.monitoringAttributes = monitoringAttributes;
    }

    public QueuedMessage[] getFilteredMessages() throws Exception {

        ArrayList<QueuedMessage> messages = new ArrayList<QueuedMessage>();

        Enumeration<?> enumx = getMessages();
        while (enumx.hasMoreElements()) {
            QueuedMessage message = (QueuedMessage)enumx.nextElement();
            if (isIncluded(message)) {
                messages.add(message);
            }
        }

        QueuedMessage[] messageArray = new QueuedMessage[messages.size()];
        messages.toArray(messageArray);

        return messageArray;
    }

    public void startMonitoring() {

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

    private MessageMonitorThread createMonitoringThread() {

        MessageMonitorThread monitorThread = new MessageMonitorThread(this, monitoringAttributes, messageHandler);
        return monitorThread;
    }
}
