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
import biz.isphere.core.internal.ISphereHelper;
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

import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.QueuedMessage;

public class MessageMonitorThread extends Thread {

    private MonitoredMessageQueue messageQueue;
    private MonitoringAttributes monitoringAttributes;
    private IMessageHandler messageHandler;
    private ObjectLockManager objectLockManager;

    private boolean monitoring;
    private boolean collectMessagesAtStartUp;

    private List<ReceivedMessage> receivedMessages;

    private final static String END_MONITORING = "*END_MONITORING"; //$NON-NLS-1$
    private static final String REMOVE_ALL = "*REMOVE_ALL"; //$NON-NLS-1$
    private final static int READ_TIMEOUT_SECS = 5;
    private final static int OBJECT_LOCK_WAIT_SECS = READ_TIMEOUT_SECS + 10;

    public MessageMonitorThread(MonitoredMessageQueue messageQueue) {
        super("iSphere Message Monitor"); //$NON-NLS-1$

        this.messageQueue = messageQueue;

        this.monitoringAttributes = messageQueue.getMonitoringAttributes();
        this.messageHandler = new MessageHandler(monitoringAttributes);

        this.objectLockManager = new ObjectLockManager(OBJECT_LOCK_WAIT_SECS);

        monitoring = true;
    }

    @Override
    public void run() {

        debugPrint("*** Thread started running: " + messageQueue.hashCode() + " ***"); //$NON-NLS-1$ //$NON-NLS-2$

        // Workaround trying to catch the first message.
        // Sometimes the first one is lost.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e2) {
        }

        collectMessagesAtStartUp = monitoringAttributes.isCollectMessagesOnStartup();

        debugPrint("Thread " + messageQueue.hashCode() + ": Collecting messages at startup: " + collectMessagesAtStartUp); //$NON-NLS-1$ //$NON-NLS-2$

        /*
         * Use a timeout for locking the message queue when starting the message
         * monitor thread, because the message queue may still be locked after
         * having restarted RDi.
         */
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.SECOND, READ_TIMEOUT_SECS);
        long startTimeout = startTime.getTimeInMillis();

        QSYSObjectPathName path = new QSYSObjectPathName(messageQueue.getPath());
        RemoteObject remoteMessageQueue = new RemoteObject(messageQueue.getSystem(), path.getObjectName(), path.getLibraryName(), "*MSGQ", ""); //$NON-NLS-1$ //$NON-NLS-2$

        ObjectLock exclusiveLock = null;
        ObjectLock sharedReadLock = null;

        try {

            // Check, whether the message queue exists
            if (!ISphereHelper.checkObject(messageQueue.getSystem(), messageQueue.getPath())) {
                debugPrint("Thread " + messageQueue.hashCode() + ": Message queue does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        MessageDialog.openError(
                            Display.getDefault().getActiveShell(),
                            Messages.Message_Queue_Monitoring_Error,
                            Messages.bind(Messages.Message_queue_A_not_found_in_library_B,
                                new Object[] { messageQueue.getLibrary(), messageQueue.getName() }));
                    }
                });
                monitoringAttributes.setMonitoring(false);
            } else {

                // First try to get an exclusive lock.
                debugPrint("Thread " + messageQueue.hashCode() + ": Trying to get an *EXCL lock ..."); //$NON-NLS-1$ //$NON-NLS-2$
                exclusiveLock = objectLockManager.setExclusiveLock(remoteMessageQueue);
                if (exclusiveLock != null) {
                    debugPrint("Thread " + messageQueue.hashCode() + ": Got *EXCL lock: " + exclusiveLock.hashCode()); //$NON-NLS-1$ //$NON-NLS-2$
                    // Then add a shared for read lock to allow other job to
                    // display messages and remove the exclusive lock
                    debugPrint("Thread " + messageQueue.hashCode() + ": Adding *SHHRD lock ..."); //$NON-NLS-1$ //$NON-NLS-2$
                    sharedReadLock = objectLockManager.setSharedForReadLock(remoteMessageQueue);
                    if (sharedReadLock != null) {
                        debugPrint("Thread " + messageQueue.hashCode() + ": Got *SHRRD lock: " + sharedReadLock.hashCode()); //$NON-NLS-1$ //$NON-NLS-2$
                        debugPrint("Thread " + messageQueue.hashCode() + ": Removing *EXCL lock: " + exclusiveLock.hashCode()); //$NON-NLS-1$ //$NON-NLS-2$
                        objectLockManager.removeObjectLock(exclusiveLock);
                        exclusiveLock = null;
                    }
                }

                if (sharedReadLock == null) {
                    debugPrint("Thread " + messageQueue.hashCode() + ": Could not allocate message queue"); //$NON-NLS-1$ //$NON-NLS-2$
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.Message_Queue_Monitoring_Error,
                                objectLockManager.getErrorMessage());
                        }
                    });
                    monitoringAttributes.setMonitoring(false);
                }
            }

            messageQueue.messageMonitorStarted(messageQueue);

            while (monitoring && monitoringAttributes.isMonitoringEnabled()) {
                try {
                    QueuedMessage message;
                    if (collectMessagesAtStartUp) {
                        debugPrint("Thread " + messageQueue.hashCode() + ": receives messages from queue (COLLECT) with object lock: " //$NON-NLS-1$ //$NON-NLS-2$
                            + sharedReadLock.hashCode());
                        message = messageQueue.receive(null, 1, MessageQueue.OLD, MessageQueue.ANY);
                    } else {
                        debugPrint("Thread " + messageQueue.hashCode() + ": receives messages from queue (DEFAULT) with object lock: " //$NON-NLS-1$ //$NON-NLS-2$
                            + sharedReadLock.hashCode());
                        message = messageQueue.receive(null, READ_TIMEOUT_SECS, MessageQueue.OLD, MessageQueue.ANY);
                    }

                    removeMessagesPendingToBeRemoved();

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
            if (sharedReadLock != null) {
                debugPrint("Thread " + messageQueue.hashCode() + ": FINALLY: Removing *SHRRD lock: " + sharedReadLock.hashCode()); //$NON-NLS-1$ //$NON-NLS-2$
                objectLockManager.removeObjectLock(sharedReadLock);
            }
            if (exclusiveLock != null) {
                debugPrint("Thread " + messageQueue.hashCode() + ": FINALLY: Removing *EXCL lock: " + exclusiveLock.hashCode()); //$NON-NLS-1$ //$NON-NLS-2$
                objectLockManager.removeObjectLock(exclusiveLock);
            }
            debugPrint("Thread " + messageQueue.hashCode() + ": About to leave thread."); //$NON-NLS-1$ //$NON-NLS-2$
            messageQueue.messageMonitorStopped(messageQueue);
        }

        return;
    }

    public void stopMonitoring() {

        debugPrint("Thread " + messageQueue.hashCode() + ": Received request to stop monitoring"); //$NON-NLS-1$ //$NON-NLS-2$

        if (!monitoring) {
            debugPrint("Thread " + messageQueue.hashCode() + ": ... but I am not monitoring???"); //$NON-NLS-1$ //$NON-NLS-2$
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
                } catch (AS400Exception e) {
                    // Message key not found
                    if (!"CPF2410".equals(e.getAS400Message().getID())) { //$NON-NLS-1$
                        isError = true;
                    }
                } catch (Throwable e) {
                    isError = true;
                } finally {
                    messageQueue.confirmRemovedMessage(queuedMessage);
                }
            }

            if (isError) {
                MessageDialogAsync.displayError(Messages.One_or_more_messages_could_not_be_removed);
            }

        } catch (Throwable e) {
            MessageDialogAsync.displayError(ExceptionHelper.getLocalizedMessage(e));
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

    private void debugPrint(String message) {
        // Xystem.out.println(message);
    }
}
