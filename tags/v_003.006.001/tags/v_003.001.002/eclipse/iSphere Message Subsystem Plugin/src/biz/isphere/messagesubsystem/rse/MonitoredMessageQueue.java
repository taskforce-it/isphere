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

import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import biz.isphere.messagesubsystem.Messages;
import biz.isphere.messagesubsystem.internal.FilteredMessageQueue;
import biz.isphere.messagesubsystem.internal.MessageMonitorThread;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.QueuedMessage;

public class MonitoredMessageQueue extends FilteredMessageQueue {

    private static final long serialVersionUID = 2988890902520435974L;

    private IQueuedMessageSubsystem messageSubsystem;
    private MonitoringAttributes monitoringAttributes;

    private MessageMonitorThread monitoringThread;
    private LinkedList<QueuedMessage> messagesToRemove;
    private QSYSObjectPathName messageQueuePathNaame;

    public MonitoredMessageQueue(IQueuedMessageSubsystem messageSubsystem, AS400 system, MonitoringAttributes monitoringAttributes) {
        super(system, new QueuedMessageFilter(monitoringAttributes.getFilterString()));

        this.messageSubsystem = messageSubsystem;
        this.monitoringAttributes = monitoringAttributes;
        this.messagesToRemove = new LinkedList<QueuedMessage>();
        this.messageQueuePathNaame = new QSYSObjectPathName(getPath());
    }

    public void startMonitoring() {

        monitoringThread = createMonitoringThread();
        monitoringThread.setDaemon(true);
        monitoringThread.start();
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

    public void messageMonitorStarted(MonitoredMessageQueue messageQueue) {
        messageSubsystem.messageMonitorStarted(messageQueue);
    }

    public void messageMonitorStopped(MonitoredMessageQueue messageQueue) {
        monitoringThread = null;
        messageSubsystem.messageMonitorStopped(messageQueue);
    }

    public MonitoringAttributes getMonitoringAttributes() {
        return monitoringAttributes;
    }

    public String getName() {
        return messageQueuePathNaame.getObjectName();
    }

    public String getLibrary() {
        return messageQueuePathNaame.getLibraryName();
    }

    public synchronized void remove(QueuedMessage queuedMessage) {
        messagesToRemove.add(queuedMessage);
    }

    public synchronized QueuedMessage[] getMessagesPendingToBeRemoved() {
        return messagesToRemove.toArray(new QueuedMessage[messagesToRemove.size()]);
    }

    public synchronized void confirmRemovedMessage(QueuedMessage queuedMessage) {
        messagesToRemove.remove(queuedMessage);
    }

    private MessageMonitorThread createMonitoringThread() {

        return new MessageMonitorThread(this);
    }
}
