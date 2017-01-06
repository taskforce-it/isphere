/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse;

import com.ibm.as400.access.QueuedMessage;

public class InquiryMessageDelegate {
    
    /*
     * The following reply status values have been copied from
     * com.ibm.as400.resource.RQueuedMessage, because RQueuedMessage has been
     * marked as deprecated.
     */
    public static final String REPLY_STATUS_ACCEPTS_SENT = "A"; //$NON-NLS-1$
    public static final String REPLY_STATUS_ACCEPTS_NOT_SENT = "W"; //$NON-NLS-1$
    public static final String REPLY_STATUS_NOT_ACCEPT = "N"; //$NON-NLS-1$

    private QueuedMessage queuedMessage;

    public InquiryMessageDelegate(QueuedMessage queuedMessage) {
        this.queuedMessage = queuedMessage;
    }

    public String getDefaultReply() {
        return queuedMessage.getDefaultReply();
    }

    public boolean isInquiryMessage() {
        return queuedMessage.getType() == QueuedMessage.INQUIRY;
    }

    public String getReplyStatus() {
        return queuedMessage.getReplyStatus();
    }

    public boolean isPendingReply() {
        return isInquiryMessage() && REPLY_STATUS_ACCEPTS_NOT_SENT.equals(getReplyStatus());
    }

}
