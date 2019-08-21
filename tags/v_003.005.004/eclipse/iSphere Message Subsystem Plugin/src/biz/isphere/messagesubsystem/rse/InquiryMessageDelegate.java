/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse;

import biz.isphere.messagesubsystem.internal.QueuedMessageHelper;

import com.ibm.as400.access.QueuedMessage;

public class InquiryMessageDelegate {

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
        return isInquiryMessage() && QueuedMessageHelper.REPLY_STATUS_ACCEPTS_NOT_SENT.equals(getReplyStatus());
    }

}
