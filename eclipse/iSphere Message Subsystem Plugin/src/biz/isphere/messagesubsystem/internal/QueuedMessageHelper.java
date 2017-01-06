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

import java.util.ArrayList;
import java.util.List;

import biz.isphere.messagesubsystem.Messages;

import com.ibm.as400.access.QueuedMessage;

public final class QueuedMessageHelper {

    /*
     * The following reply status values have been copied from
     * com.ibm.as400.resource.RQueuedMessage, because RQueuedMessage has been
     * marked as deprecated.
     */
    public static final String REPLY_STATUS_ACCEPTS_SENT = "A"; //$NON-NLS-1$
    public static final String REPLY_STATUS_ACCEPTS_NOT_SENT = "W"; //$NON-NLS-1$
    public static final String REPLY_STATUS_NOT_ACCEPT = "N"; //$NON-NLS-1$

    public static String getMessageTypeAnyItem() {

        return Messages.Message_Type_Text_Any;
    }

    public static String[] getMessageTypeItems() {

        List<String> messageTypes = new ArrayList<String>();

        messageTypes.add(Messages.Message_Type_Text_Any);
        messageTypes.add(Messages.Message_Type_Text_Completion);
        messageTypes.add(Messages.Message_Type_Text_Diagnostic);
        messageTypes.add(Messages.Message_Type_Text_Informational);
        messageTypes.add(Messages.Message_Type_Text_Inquiry);
        messageTypes.add(Messages.Message_Type_Text_Senders_copy);
        messageTypes.add(Messages.Message_Type_Text_Request);
        messageTypes.add(Messages.Message_Type_Text_Request_with_prompting);
        messageTypes.add(Messages.Message_Type_Text_Notify);

        return messageTypes.toArray(new String[messageTypes.size()]);
    }

    public static int getMessageTypeFromText(String messageTypeText) {

        int messageTypeInt = -1;
        if (messageTypeText == null) {
            return messageTypeInt;
        }

        if (messageTypeText.equals(Messages.Message_Type_Text_Completion)) {
            messageTypeInt = QueuedMessage.COMPLETION;
        } else if (messageTypeText.equals(Messages.Message_Type_Text_Diagnostic)) {
            messageTypeInt = QueuedMessage.DIAGNOSTIC;
        } else if (messageTypeText.equals(Messages.Message_Type_Text_Informational)) {
            messageTypeInt = QueuedMessage.INFORMATIONAL;
        } else if (messageTypeText.equals(Messages.Message_Type_Text_Inquiry)) {
            messageTypeInt = QueuedMessage.INQUIRY;
        } else if (messageTypeText.equals(Messages.Message_Type_Text_Senders_copy)) {
            messageTypeInt = QueuedMessage.SENDERS_COPY;
        } else if (messageTypeText.equals(Messages.Message_Type_Text_Request)) {
            messageTypeInt = QueuedMessage.REQUEST;
        } else if (messageTypeText.equals(Messages.Message_Type_Text_Request_with_prompting)) {
            messageTypeInt = QueuedMessage.REQUEST_WITH_PROMPTING;
        } else if (messageTypeText.equals(Messages.Message_Type_Text_Notify)) {
            messageTypeInt = QueuedMessage.NOTIFY;
        }

        return messageTypeInt;
    }

    public static String getMessageTypeAsText(QueuedMessage queuedMessage) {
        return getMessageTypeAsText(queuedMessage.getType());
    }

    public static String getMessageTypeAsText(int messageType) {

        switch (messageType) {
        case QueuedMessage.COMPLETION:
            return Messages.Message_Type_Text_Completion;
        case QueuedMessage.DIAGNOSTIC:
            return Messages.Message_Type_Text_Diagnostic;
        case QueuedMessage.INFORMATIONAL:
            return Messages.Message_Type_Text_Informational;
        case QueuedMessage.INQUIRY:
            return Messages.Message_Type_Text_Inquiry;
        case QueuedMessage.SENDERS_COPY:
            return Messages.Message_Type_Text_Senders_copy;
        case QueuedMessage.REQUEST:
            return Messages.Message_Type_Text_Request;
        case QueuedMessage.REQUEST_WITH_PROMPTING:
            return Messages.Message_Type_Text_Request_with_prompting;
        case QueuedMessage.NOTIFY:
            return Messages.Message_Type_Text_Notify;
        case QueuedMessage.ESCAPE:
            return Messages.Message_Type_Escape;
        case QueuedMessage.REPLY_NOT_VALIDITY_CHECKED:
            return Messages.Message_Type_Reply_Not_Validity_Checked;
        case QueuedMessage.REPLY_VALIDITY_CHECKED:
            return Messages.Message_Type_Reply_Validity_Checked;
        case QueuedMessage.REPLY_MESSAGE_DEFAULT_USED:
            return Messages.Message_Type_Reply_Message_Default_Used;
        case QueuedMessage.REPLY_SYSTEM_DEFAULT_USED:
            return Messages.Message_Type_Reply_System_Default_Used;
        case QueuedMessage.REPLY_FROM_SYSTEM_REPLY_LIST:
            return Messages.Message_Reply_From_System_Reply_List;
        default:
            return ""; //$NON-NLS-1$
        }
    }

    public static String getMessageReplyStatusAsText(QueuedMessage queuedMessage) {
        return getMessageReplyStatusAsText(queuedMessage.getReplyStatus());
    }

    public static String getMessageReplyStatusAsText(String replyStatus) {

        if (REPLY_STATUS_ACCEPTS_SENT.equals(replyStatus)) {
            return Messages.Message_Reply_Status_Accepts_Send;
        } else if (REPLY_STATUS_ACCEPTS_NOT_SENT.equals(replyStatus)) {
            return Messages.Message_Reply_Status_Accepts_Not_Send;
        } else if (REPLY_STATUS_NOT_ACCEPT.equals(replyStatus)) {
            return Messages.Message_Reply_Status_Not_Accept;
        } else {
            return "*NOT_SPECIFIED"; //$NON-NLS-1$
        }
    }

    public static boolean isPendingReply(QueuedMessage queuedMessage) {
        return REPLY_STATUS_ACCEPTS_NOT_SENT.equals(queuedMessage.getReplyStatus());
    }
}
