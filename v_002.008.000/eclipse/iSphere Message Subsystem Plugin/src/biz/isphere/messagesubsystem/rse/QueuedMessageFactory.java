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

import biz.isphere.messagesubsystem.internal.FilteredMessageQueue;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QueuedMessage;

public class QueuedMessageFactory {
    private AS400 as400;

    public QueuedMessageFactory(AS400 as400) {
        super();
        this.as400 = as400;
    }

    public QueuedMessage[] getQueuedMessages(QueuedMessageFilter filter) throws Exception {
        FilteredMessageQueue messageQueue = new FilteredMessageQueue(as400, filter.getPath(), filter);
        return messageQueue.getFilteredMessages();
    }

}
