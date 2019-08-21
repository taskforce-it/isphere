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
        default:
            return ""; //$NON-NLS-1$
        }
    }

}
