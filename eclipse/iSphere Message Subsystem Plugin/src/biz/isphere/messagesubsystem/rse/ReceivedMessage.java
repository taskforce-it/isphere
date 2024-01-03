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

package biz.isphere.messagesubsystem.rse;

import java.util.Calendar;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.MessageFile;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.QueuedMessage;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.messagesubsystem.internal.QueuedMessageHelper;

public class ReceivedMessage {

    private QueuedMessage queuedMessage;
    private InquiryMessageDelegate inquiryMessageDelegate;

    public ReceivedMessage(QueuedMessage message) {
        this.queuedMessage = message;
        this.inquiryMessageDelegate = new InquiryMessageDelegate(this.queuedMessage);
    }

    public String getID() {
        return queuedMessage.getID();
    }

    public int getType() {
        return queuedMessage.getType();
    }

    public String getMessageType() {
        return QueuedMessageHelper.getMessageTypeAsText(queuedMessage);
    }

    public String getText() {
        return queuedMessage.getText();
    }

    public String getHelpFormatted() {

        try {

            // Check library and message file names
            if (StringHelper.isNullOrEmpty(queuedMessage.getLibraryName()) || StringHelper.isNullOrEmpty(queuedMessage.getFileName())) {
                return queuedMessage.getHelp();
            }

            String libraryName = queuedMessage.getLibraryName();
            String fileName = queuedMessage.getFileName();

            // Check system
            if (queuedMessage.getQueue() == null) {
                return queuedMessage.getHelp();
            }

            if (queuedMessage.getQueue().getSystem() == null) {
                return queuedMessage.getHelp();
            }

            AS400 system = queuedMessage.getQueue().getSystem();

            if (ISphereHelper.checkLibrary(system, libraryName)) {
                return queuedMessage.getHelp();
            }

            if (!ISphereHelper.checkObject(system, libraryName, fileName, "*MSGF")) {
                return queuedMessage.getHelp();
            }

            String messageFilePath = new QSYSObjectPathName(libraryName, fileName, "MSGF").getPath(); //$NON-NLS-1$
            MessageFile file = new MessageFile(system, messageFilePath);
            file.setHelpTextFormatting(MessageFile.RETURN_FORMATTING_CHARACTERS);
            AS400Message as400Message;
            if (queuedMessage.getSubstitutionData() != null) {
                as400Message = file.getMessage(queuedMessage.getID(), queuedMessage.getSubstitutionData());
            } else {
                as400Message = file.getMessage(queuedMessage.getID());
            }

            return as400Message.getHelp();

        } catch (Exception e) {
            return queuedMessage.getHelp();
        }
    }

    public byte[] getKey() {
        return queuedMessage.getKey();
    }

    public MessageQueue getQueue() {
        return queuedMessage.getQueue();
    }

    public int getSeverity() {
        if (StringHelper.isNullOrEmpty(getID())) {
            /*
             * See QMHRCVM API: Message severity. The severity of the message
             * received. Possible values are 0 through 99. If the message being
             * received is an immediate message, the message severity is not
             * returned.
             * https://www.ibm.com/docs/en/i/7.3?topic=electronic-business-web-
             * serving
             */
            return 0;
        }
        return queuedMessage.getSeverity();
    }

    public Calendar getDate() {
        return queuedMessage.getDate();
    }

    public String getUser() {
        return queuedMessage.getUser();
    }

    public String getFromJobName() {
        return queuedMessage.getFromJobName();
    }

    public String getFromJobNumber() {
        return queuedMessage.getFromJobNumber();
    }

    public String getFromProgram() {
        return queuedMessage.getFromProgram();
    }

    public String getDefaultReply() {
        return inquiryMessageDelegate.getDefaultReply();
    }

    public boolean isInquiryMessage() {
        return inquiryMessageDelegate.isInquiryMessage();
    }

    public String getReplyStatus() {
        return inquiryMessageDelegate.getReplyStatus();
    }

    public String getReplyStatusAsText() {
        return QueuedMessageHelper.getMessageReplyStatusAsText(queuedMessage);
    }

    public boolean isPendingReply() {
        return inquiryMessageDelegate.isPendingReply();
    }

}
