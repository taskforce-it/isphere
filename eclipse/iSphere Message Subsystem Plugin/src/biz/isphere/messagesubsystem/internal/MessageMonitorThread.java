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

import java.util.Calendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.rse.IMessageHandler;
import biz.isphere.messagesubsystem.rse.MonitoredMessageQueue;
import biz.isphere.messagesubsystem.rse.MonitoringAttributes;
import biz.isphere.messagesubsystem.rse.ReceivedMessage;

import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QueuedMessage;

public class MessageMonitorThread extends Thread {

    private MonitoredMessageQueue messageQueue;
    private MonitoringAttributes monitoringAttributes;
    private IMessageHandler messageHandler;

    private boolean monitoring;
    private String errorMessage;

    private final static String END_MONITORING = "*END_MONITORING"; //$NON-NLS-1$
    private static final String REMOVE_ALL = "*REMOVE_ALL"; //$NON-NLS-1$
    private final static int WAIT_SECS = 20;

    public MessageMonitorThread(MonitoredMessageQueue messageQueue, MonitoringAttributes monitoringAttributes, IMessageHandler messageHandler) {
        super("iSphere Message Monitor");

        this.messageQueue = messageQueue;
        this.monitoringAttributes = monitoringAttributes;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {

        monitoring = true;

        /*
         * Use a timeout for locking the message queue when starting the message
         * monitor thread, because the message queue may still be locked after
         * having restarted RDi.
         */
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.SECOND, WAIT_SECS + 1);
        long startTimeout = startTime.getTimeInMillis();

        while (monitoring && monitoringAttributes.isMonitoringEnabled()) {
            try {
                QueuedMessage message = messageQueue.receive(null, WAIT_SECS, MessageQueue.OLD, MessageQueue.ANY);
                if (monitoring && (message != null)) {
                    handleMessage(message);
                }
            } catch (Exception e) {
                if (Calendar.getInstance().getTimeInMillis() > startTimeout) {
                    monitoringAttributes.setMonitoring(false);
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
                } else {
                    try {
                        // wait a second, then try again
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                }
            }
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

        if (REMOVE_ALL.equals(message.getText())) {
            messageQueue.remove();
        } else if (END_MONITORING.equals(message.getText())) {
            messageQueue.remove(message.getKey());
            monitoringAttributes.setMonitoring(false);
            monitoring = false;
        } else {
            if (messageQueue.isIncluded(message)) {
                if (messageHandler != null) {
                    messageHandler.handleMessage(new ReceivedMessage(message));
                }
            }
        }
    }
}
