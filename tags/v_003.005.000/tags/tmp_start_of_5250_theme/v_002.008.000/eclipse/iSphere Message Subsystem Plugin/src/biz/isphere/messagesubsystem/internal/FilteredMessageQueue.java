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

import java.util.ArrayList;
import java.util.Enumeration;

import biz.isphere.messagesubsystem.rse.QueuedMessageFilter;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QueuedMessage;

public class FilteredMessageQueue extends MessageQueue {

    private static final long serialVersionUID = -9076855621450227101L;

    private QueuedMessageFilter messageFilter;

    public FilteredMessageQueue(AS400 system, String path, QueuedMessageFilter filter) {
        super(system, path);

        this.messageFilter = filter;
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

    public void setFilter(QueuedMessageFilter filter) {
        this.messageFilter = filter;
    }

    public boolean isIncluded(QueuedMessage message) {

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
