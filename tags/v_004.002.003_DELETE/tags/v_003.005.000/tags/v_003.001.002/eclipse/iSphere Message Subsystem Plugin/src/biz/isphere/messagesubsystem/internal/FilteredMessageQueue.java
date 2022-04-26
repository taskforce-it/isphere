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

import biz.isphere.base.internal.StringHelper;
import biz.isphere.messagesubsystem.rse.QueuedMessageFilter;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QueuedMessage;

public class FilteredMessageQueue extends MessageQueue {

    private static final long serialVersionUID = -9076855621450227101L;

    private QueuedMessageFilter messageFilter;

    public FilteredMessageQueue(AS400 system, QueuedMessageFilter filter) {
        super(system, filter.getPath(system));

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

        // Data type: java.lang.String
        if (messageFilter.getUser() != null) {
            if (!matches(message.getUser(), messageFilter.getUser())) {
                return false;
            }
        }

        // Data type: java.lang.String
        if (messageFilter.getId() != null) {
            if (!matches(message.getID(), messageFilter.getId())) {
                return false;
            }
        }

        // Data type: java.lang.String
        if (messageFilter.getFromJobName() != null) {
            if (!matches(message.getFromJobName(), messageFilter.getFromJobName())) {
                return false;
            }
        }

        // Data type: java.lang.String
        if (messageFilter.getFromJobNumber() != null) {
            if (!matches(message.getFromJobNumber(), messageFilter.getFromJobNumber())) {
                return false;
            }
        }

        // Data type: java.lang.String
        if (messageFilter.getFromProgram() != null) {
            if (!matches(message.getFromProgram(), messageFilter.getFromProgram())) {
                return false;
            }
        }

        // Data type: java.lang.String
        if (messageFilter.getText() != null) {
            if (!matches(message.getText(), messageFilter.getText())) {
                return false;
            }
        }

        // Data type: int
        if (messageFilter.getSeverity() != -1) {
            if (message.getSeverity() < messageFilter.getSeverity()) {
                return false;
            }
        }

        // Data type: int
        if (messageFilter.getMessageType() != -1) {
            if (message.getType() != messageFilter.getMessageType()) {
                return false;
            }
        }

        // Data type: java.util.Date
        if (messageFilter.getDate() != null) {
            if (messageFilter.getDate().after(message.getDate().getTime())) {
                return false;
            }
        }

        return true;
    }

    private boolean matches(String text, String pattern) {
        return StringHelper.matchesGeneric(text, pattern);
    }
}
