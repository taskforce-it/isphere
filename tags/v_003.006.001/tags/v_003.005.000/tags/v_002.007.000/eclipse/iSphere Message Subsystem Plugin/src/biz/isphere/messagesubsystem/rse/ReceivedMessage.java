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

import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.MessageFile;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.QueuedMessage;

public class ReceivedMessage {

    private QueuedMessage queuedMessage;

    public ReceivedMessage(QueuedMessage message) {
        this.queuedMessage = message;
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
            String messageFilePath = new QSYSObjectPathName(queuedMessage.getLibraryName(), queuedMessage.getFileName(), "MSGF").getPath();
            MessageFile file = new MessageFile(queuedMessage.getQueue().getSystem(), messageFilePath);
            file.setHelpTextFormatting(MessageFile.RETURN_FORMATTING_CHARACTERS);
            AS400Message as400Message = file.getMessage(queuedMessage.getID(), queuedMessage.getSubstitutionData());
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
        return queuedMessage.getDefaultReply();
    }

}
