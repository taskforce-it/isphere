/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.api.retrievemessagedescription.IQMHRTVM;
import biz.isphere.core.messagefileeditor.FieldFormat;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.core.messagefileeditor.SpecialReplyValueEntry;
import biz.isphere.core.messagefileeditor.ValidReplyEntry;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;

public final class MessageDescriptionHelper {

    public static String mergeMessageDescription(Shell shell, MessageDescription messageDescription, String toConnectionName, String toMessageFile,
        String toLibrary) throws Exception {

        AS400 fromSystem = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());
        AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

        String message = null;
        if (fromSystem.getSystemName().equals(toSystem.getSystemName())) {
            message = mergeOnSameSystem(fromSystem, messageDescription, toMessageFile, toLibrary);
        } else {
            message = mergeOnDifferentSystems(fromSystem, messageDescription, toConnectionName, toMessageFile, toLibrary);
        }

        return message;
    }

    private static String mergeOnSameSystem(AS400 fromSystem, MessageDescription messageDescription, String toMessageFile, String toLibrary)
        throws Exception {

        StringBuilder command = new StringBuilder();
        command.append("MRGMSGF"); //$NON-NLS-1$
        command.append(" FROMMSGF(" + messageDescription.getLibrary() + "/" + messageDescription.getMessageFile() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        command.append(" TOMSGF(" + toLibrary + "/" + toMessageFile + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        command.append(" SELECT(" + messageDescription.getMessageId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        String message = executeCommand(fromSystem, command);

        return message;
    }

    private static String mergeOnDifferentSystems(AS400 fromSystem, MessageDescription messageDescription, String toConnectionName,
        String toMessageFile, String toMessageFileLibrary) throws Exception {

        refreshMessageDescription(messageDescription);

        MessageDescription remoteMessageDescription = ObjectHelper.cloneVO(messageDescription);
        remoteMessageDescription.setMessageFile(toMessageFile);
        remoteMessageDescription.setLibrary(toMessageFileLibrary);

        String message = null;
        if (!exists(toConnectionName, toMessageFile, toMessageFileLibrary, messageDescription.getMessageId())) {
            message = addMessageDescription(remoteMessageDescription);
        } else {
            message = changeMessageDescription(remoteMessageDescription);
        }

        return message;
    }

    public static String addMessageDescription(MessageDescription messageDescription) throws Exception {

        StringBuilder command = new StringBuilder();
        command.append("ADDMSGD"); //$NON-NLS-1$

        return addOrChangeMessageDescription(messageDescription, command);
    }

    public static String changeMessageDescription(MessageDescription messageDescription) throws Exception {

        StringBuilder command = new StringBuilder();
        command.append("CHGMSGD"); //$NON-NLS-1$

        return addOrChangeMessageDescription(messageDescription, command);
    }

    private static String addOrChangeMessageDescription(MessageDescription messageDescription, StringBuilder command) throws Exception {

        command.append(" MSGID(" + messageDescription.getMessageId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" MSGF(" + messageDescription.getLibrary() + "/" + messageDescription.getMessageFile() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        command.append(" MSG(" + StringHelper.addQuotes(messageDescription.getMessage()) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" SECLVL(" + getSecondLevelText(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" SEV(" + messageDescription.getSeverity().toString() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" CCSID(" + messageDescription.getCcsidAsString() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" FMT(" + getFieldFormats(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(getReplyType(messageDescription));
        command.append(" VALUES(" + getValues(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" SPCVAL(" + getSpecialValues(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" RANGE(" + getRangeOfValues(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" REL(" + getRelationshipForValidReplies(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" DFT(" + getDefaultReply(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        AS400 system = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());
        String message = executeCommand(system, command);

        return message;

    }

    public static String removeMessageDescription(MessageDescription messageDescription) throws Exception {

        AS400 system = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());

        StringBuilder command = new StringBuilder();
        command.append("RMVMSGD"); //$NON-NLS-1$
        command.append(" MSGID(" + messageDescription.getMessageId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" MSGF(" + messageDescription.getLibrary() + "/" + messageDescription.getMessageFile() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        String message = executeCommand(system, command);

        return message;
    }

    public static MessageDescription retrieveMessageDescription(String connectionName, String messageFile, String library, String messageId) {

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

        IQMHRTVM qmhrtvm = new IQMHRTVM(system, connectionName);
        qmhrtvm.setMessageFile(messageFile, library);
        MessageDescription messageDescription = qmhrtvm.retrieveMessageDescription(messageId);

        return messageDescription;
    }

    public static MessageDescription refreshMessageDescription(MessageDescription messageDescription) {

        String connectionName = messageDescription.getConnection();
        String library = messageDescription.getLibrary();
        String messageFile = messageDescription.getMessageFile();
        String messageId = messageDescription.getMessageId();

        MessageDescription currentValues = retrieveMessageDescription(connectionName, messageFile, library, messageId);
        if (currentValues != null) {
            messageDescription.setMessage(currentValues.getMessage());
            messageDescription.setHelpText(currentValues.getHelpText());
            messageDescription.setSeverity(currentValues.getSeverity());
            messageDescription.setCcsid(currentValues.getCcsid());
            messageDescription.setFieldFormats(currentValues.getFieldFormats());
        }

        return messageDescription;
    }

    private static boolean exists(String connectionName, String messageFile, String library, String messageId) {

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

        IQMHRTVM qmhrtvm = new IQMHRTVM(system, connectionName);
        qmhrtvm.setMessageFile(messageFile, library);

        return qmhrtvm.exists(messageId);
    }

    private static String getSecondLevelText(MessageDescription messageDescription) {

        if (MessageDescription.VALUE_NONE.equals(messageDescription.getHelpText())) {
            return messageDescription.getHelpText();
        }

        return StringHelper.addQuotes(messageDescription.getHelpText());
    }

    private static String getFieldFormats(MessageDescription messageDescription) {

        StringBuilder formats = new StringBuilder();

        ArrayList<?> fieldFormats = messageDescription.getFieldFormats();
        if (fieldFormats.size() == 0) {
            formats.append(MessageDescription.VALUE_NONE);
        } else {
            for (int idx = 0; idx < fieldFormats.size(); idx++) {
                FieldFormat fieldFormat = (FieldFormat)fieldFormats.get(idx);
                if (idx != 0) {
                    formats.append(" "); //$NON-NLS-1$
                }
                formats.append("("); //$NON-NLS-1$
                formats.append(fieldFormat.getType());
                if (fieldFormat.isVary()) {
                    formats.append(" *VARY " + Integer.toString(fieldFormat.getBytes())); //$NON-NLS-1$
                } else {
                    formats.append(" " + Integer.toString(fieldFormat.getLength())); //$NON-NLS-1$
                    if (fieldFormat.getType().equals("*DEC")) { //$NON-NLS-1$
                        formats.append(" " + Integer.toString(fieldFormat.getDecimalPositions())); //$NON-NLS-1$
                    }
                }
                formats.append(")"); //$NON-NLS-1$
            }
        }

        return formats.toString();
    }

    private static String getReplyType(MessageDescription messageDescription) {

        StringBuilder replyType = new StringBuilder();

        replyType.append(" TYPE("); //$NON-NLS-1$
        replyType.append(messageDescription.getReplyType());
        replyType.append(")"); //$NON-NLS-1$

        replyType.append(" LEN("); //$NON-NLS-1$

        if (MessageDescription.VALUE_NONE.equals(messageDescription.getReplyType())) {
            replyType.append(MessageDescription.VALUE_NONE);
        } else if (MessageDescription.REPLY_DEC.equals(messageDescription.getReplyType())) {
            replyType.append(messageDescription.getReplyLength());
            replyType.append(" "); //$NON-NLS-1$
            replyType.append(messageDescription.getReplyDecimalPositions());
        } else {
            replyType.append(messageDescription.getReplyLength());
        }

        replyType.append(")"); //$NON-NLS-1$

        return replyType.toString();
    }

    private static String getValues(MessageDescription messageDescription) {

        if (messageDescription.getValidReplyEntries().size() == 0) {
            return MessageDescription.VALUE_NONE;
        }

        StringBuilder replyEntries = new StringBuilder();

        for (ValidReplyEntry replyEntry : messageDescription.getValidReplyEntries()) {
            if (replyEntries.length() > 0) {
                replyEntries.append(" "); //$NON-NLS-1$
            }
            replyEntries.append(StringHelper.addQuotes(replyEntry.getValue()));
        }

        return replyEntries.toString();
    }

    private static String getSpecialValues(MessageDescription messageDescription) {

        if (messageDescription.getSpecialReplyValueEntries().size() == 0) {
            return MessageDescription.VALUE_NONE;
        }

        StringBuilder replyEntries = new StringBuilder();

        for (SpecialReplyValueEntry replyEntry : messageDescription.getSpecialReplyValueEntries()) {
            if (replyEntries.length() > 0) {
                replyEntries.append(" "); //$NON-NLS-1$
            }
            replyEntries.append("("); //$NON-NLS-1$
            replyEntries.append(StringHelper.addQuotes(replyEntry.getFromValue()));
            replyEntries.append(" "); //$NON-NLS-1$
            replyEntries.append(StringHelper.addQuotes(replyEntry.getToValue()));
            replyEntries.append(")"); //$NON-NLS-1$
        }

        return replyEntries.toString();
    }

    private static String getRangeOfValues(MessageDescription messageDescription) {

        if (MessageDescription.VALUE_NONE.equals(messageDescription.getRangeOfReplyValue().getLowerValue())) {
            return MessageDescription.VALUE_NONE;
        }

        StringBuilder rangeOfValues = new StringBuilder();

        rangeOfValues.append(StringHelper.addQuotes(messageDescription.getRangeOfReplyValue().getLowerValue()));
        rangeOfValues.append(" "); //$NON-NLS-1$
        rangeOfValues.append(StringHelper.addQuotes(messageDescription.getRangeOfReplyValue().getUpperValue()));

        return rangeOfValues.toString();
    }

    private static String getRelationshipForValidReplies(MessageDescription messageDescription) {

        if (MessageDescription.VALUE_NONE.equals(messageDescription.getRelationalTestEntry().getOperator())) {
            return MessageDescription.VALUE_NONE;
        }

        StringBuilder relationshipForValidreplies = new StringBuilder();

        relationshipForValidreplies.append(messageDescription.getRelationalTestEntry().getOperator());
        relationshipForValidreplies.append(" "); //$NON-NLS-1$
        relationshipForValidreplies.append(StringHelper.addQuotes(messageDescription.getRelationalTestEntry().getValue()));

        return relationshipForValidreplies.toString();
    }

    private static String getDefaultReply(MessageDescription messageDescription) {

        if (MessageDescription.VALUE_NONE.equals(messageDescription.getDefaultReplyValue())) {
            return MessageDescription.VALUE_NONE;
        }

        return StringHelper.addQuotes(messageDescription.getDefaultReplyValue());
    }

    private static String executeCommand(AS400 system, StringBuilder command) throws Exception {

        AS400Message[] messageList = null;
        CommandCall commandCall = new CommandCall(system);
        if (!commandCall.run(command.toString())) {
            messageList = commandCall.getMessageList();
            if (messageList.length > 0) {
                return messageList[0].getText();
            } else {
                return Messages.Unknown_error_occured;
            }
        }

        return null;
    }
}
