/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.rse;

import biz.isphere.messagesubsystem.internal.QEZSNDMG;

public class SendMessageOptions {

    private String messageType;
    private String deliveryMode;
    private String messageText;
    private String recipientType;
    private String[] recipients;
    private String replyMessageQueueName;
    private String replyMessageQueueLibrary;

    public SendMessageOptions() {
        this.messageType = QEZSNDMG.TYPE_INFORMATIONAL;
        this.deliveryMode = QEZSNDMG.DELIVERY_NORMAL;
        this.messageText = ""; //$NON-NLS-1$
        this.recipientType = QEZSNDMG.RECIPIENT_TYPE_USER;
        this.recipients = new String[0];
        this.replyMessageQueueName = null;
        this.replyMessageQueueLibrary = null;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    public String getReplyMessageQueueName() {
        return this.replyMessageQueueName;
    }

    public void setReplyMessageQueueName(String messageQueue) {
        this.replyMessageQueueName = messageQueue;
    }

    public String getReplyMessageQueueLibrary() {
        return this.replyMessageQueueLibrary;
    }

    public void setReplyMessageQueueLibrary(String library) {
        this.replyMessageQueueLibrary = library;
    }

    public boolean isInquiryMessage() {
        return QEZSNDMG.TYPE_INQUERY.equals(this.messageType);
    }

}
