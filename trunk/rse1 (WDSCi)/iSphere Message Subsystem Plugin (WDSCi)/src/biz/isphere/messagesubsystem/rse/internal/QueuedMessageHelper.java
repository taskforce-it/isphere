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
package biz.isphere.messagesubsystem.rse.internal;

import biz.isphere.messagesubsystem.rse.Messages;

import com.ibm.as400.access.QueuedMessage;

public final class QueuedMessageHelper {

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
