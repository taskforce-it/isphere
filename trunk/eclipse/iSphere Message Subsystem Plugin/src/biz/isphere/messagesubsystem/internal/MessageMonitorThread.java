/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.messagesubsystem.internal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.ObjectLock;
import biz.isphere.core.internal.ObjectLockManager;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.rse.IMessageHandler;
import biz.isphere.messagesubsystem.rse.MessageHandler;
import biz.isphere.messagesubsystem.rse.MonitoredMessageQueue;
import biz.isphere.messagesubsystem.rse.MonitoringAttributes;
import biz.isphere.messagesubsystem.rse.ReceivedMessage;

import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.QueuedMessage;

public class MessageMonitorThread extends Thread {

    private MonitoredMessageQueue messageQueue;
    private MonitoringAttributes monitoringAttributes;
    private IMessageHandler messageHandler;

    private boolean monitoring;
    private boolean collectMessagesAtStartUp;
    private List<ReceivedMessage> receivedMessages;
    private ObjectLockManager objectLockManager;

    private final static String END_MONITORING = "*END_MONITORING"; //$NON-NLS-1$
    private static final String REMOVE_ALL = "*REMOVE_ALL"; //$NON-NLS-1$
    private final static int READ_TIMEOUT_SECS = 5;
    private final static int OBJECT_LOCK_WAIT_SECS = 5;

    public MessageMonitorThread(MonitoredMessageQueue messageQueue) {
        super("iSphere Message Monitor");

        this.messageQueue = messageQueue;

        this.monitoringAttributes = messageQueue.getMonitoringAttributes();
        this.messageHandler = new MessageHandler(monitoringAttributes);

        this.objectLockManager = new ObjectLockManager(OBJECT_LOCK_WAIT_SECS);
    }

    @Override
    public void run() {

        // Workaround trying to catch the first message.
        // Sometimes the first one is lost.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e2) {
        }

        monitoring = true;
        collectMessagesAtStartUp = monitoringAttributes.isCollectMessagesOnStartup();

        /*
         * Use a timeout for locking the message queue when starting the message
         * monitor thread, because the message queue may still be locked after
         * having restarted RDi.
         */
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.SECOND, READ_TIMEOUT_SECS);
        long startTimeout = startTime.getTimeInMillis();

        QSYSObjectPathName path = new QSYSObjectPathName(messageQueue.getPath());
        RemoteObject remoteMessageQueue = new RemoteObject(messageQueue.getSystem(), path.getObjectName(), path.getLibraryName(), "*MSGQ", "");

        ObjectLock objectLock = null;

        try {

            objectLock = objectLockManager.setSharedForReadLock(remoteMessageQueue);
            if (objectLock == null) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Message_Queue_Monitoring_Error,
                            objectLockManager.getErrorMessage());
                    }
                });
                monitoringAttributes.setMonitoring(false);
            }

            while (monitoring && monitoringAttributes.isMonitoringEnabled()) {
                try {
                    QueuedMessage message;
                    if (collectMessagesAtStartUp) {
                        message = messageQueue.receive(null, 1, MessageQueue.OLD, MessageQueue.ANY);
                    } else {
                        message = messageQueue.receive(null, READ_TIMEOUT_SECS, MessageQueue.OLD, MessageQueue.ANY);
                    }

                    if (monitoring) {
                        if (message != null) {
                            handleMessage(message, collectMessagesAtStartUp);
                        } else {
                            if (receivedMessages != null) {
                                handleBufferedMessages(receivedMessages);
                                receivedMessages = null;
                            }
                            collectMessagesAtStartUp = false;
                        }
                    }

                    removeMessagesPendingToBeRemoved();

                } catch (Throwable e) {
                    if (Calendar.getInstance().getTimeInMillis() > startTimeout) {
                        monitoringAttributes.setMonitoring(false);
                        monitoring = false;
                        MessageDialogAsync.displayError(Messages.Message_Queue_Monitoring_Error, ExceptionHelper.getLocalizedMessage(e));
                    } else {
                        try {
                            // wait a second, then try again
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            }

        } finally {
            messageQueue.messageMonitorStopped();
            if (objectLock != null) {
                objectLockManager.removeObjectLock(objectLock);
            }
        }
    }

    public void stopMonitoring() {

        if (!monitoring) {
            return;
        }

        monitoring = false;
    }

    private void removeMessagesPendingToBeRemoved() {

        try {

            boolean isError = false;

            QueuedMessage[] messageToRemove = messageQueue.getMessagesPendingToBeRemoved();
            for (QueuedMessage queuedMessage : messageToRemove) {
                try {
                    queuedMessage.getQueue().remove(queuedMessage.getKey());
                } catch (Throwable e) {
                    isError = true;
                } finally {
                    messageQueue.confirmRemovedMessage(queuedMessage);
                }
            }

            if (isError) {
                MessageDialogAsync.displayError(Display.getDefault().getActiveShell(), "One or more messages could not be removed.");
            }

        } catch (Throwable e) {
            MessageDialogAsync.displayError("One or message could not be removed.");
        }
    }

    private void handleMessage(QueuedMessage message, boolean isStartUp) throws Exception {

        if (REMOVE_ALL.equals(message.getText())) {
            messageQueue.remove();
        } else if (END_MONITORING.equals(message.getText())) {
            messageQueue.remove(message.getKey());
            monitoringAttributes.setMonitoring(false);
            monitoring = false;
        } else {
            if (messageQueue.isIncluded(message)) {
                if (isStartUp) {
                    if (receivedMessages == null) {
                        receivedMessages = new ArrayList<ReceivedMessage>();
                    }
                    receivedMessages.add(new ReceivedMessage(message));
                } else {
                    if (messageHandler != null) {
                        messageHandler.handleMessage(new ReceivedMessage(message));
                    }
                }
            }
        }
    }

    private void handleBufferedMessages(List<ReceivedMessage> messages) throws Exception {
        messageHandler.handleMessages(messages);
    }
}
