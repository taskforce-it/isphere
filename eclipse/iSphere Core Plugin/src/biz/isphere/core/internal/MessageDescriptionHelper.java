/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.messagefileeditor.FieldFormat;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.core.messagefileeditor.QMHRTVM;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;

// TODO: refactor to a full featured 'MessageFile' object
// consider including a list of message IDs.
public final class MessageDescriptionHelper {

    public static String mergeMessageDescription(Shell shell, MessageDescription messageDescription, String toConnectionName, String toMessageFile,
        String toLibrary) {

        AS400 fromSystem = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());
        AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

        String message = null;
        if (fromSystem.getSystemName().equals(toSystem.getSystemName())) {
            message = mergeOnSameSystem(shell, fromSystem, messageDescription, toMessageFile, toLibrary);
        } else {
            message = mergeOnDifferentSystems(shell, fromSystem, messageDescription, toConnectionName, toMessageFile, toLibrary);
        }

        return message;
    }

    private static String mergeOnSameSystem(Shell shell, AS400 fromSystem, MessageDescription messageDescription, String toMessageFile,
        String toLibrary) {

        StringBuilder command = new StringBuilder();
        command.append("MRGMSGF"); //$NON-NLS-1$
        command.append(" FROMMSGF(" + messageDescription.getLibrary() + "/" + messageDescription.getMessageFile() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        command.append(" TOMSGF(" + toLibrary + "/" + toMessageFile + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        command.append(" SELECT(" + messageDescription.getMessageId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        String message = executeCommand(shell, fromSystem, command);

        return message;
    }

    private static String mergeOnDifferentSystems(Shell shell, AS400 fromSystem, MessageDescription messageDescription, String toConnectionName,
        String toMessageFile, String toMessageFileLibrary) {

        refreshMessageDescription(shell, messageDescription);

        MessageDescription remoteMessageDescription = ObjectHelper.cloneVO(messageDescription);
        remoteMessageDescription.setMessageFile(toMessageFile);
        remoteMessageDescription.setLibrary(toMessageFileLibrary);

        MessageDescription tmpMessageDescription = retrieveMessageDescription(shell, toConnectionName, toMessageFile, toMessageFileLibrary,
            messageDescription.getMessageId());

        String message = null;
        if (tmpMessageDescription == null) {
            message = addMessageDescription(shell, remoteMessageDescription);
        } else {
            message = changeMessageDescription(shell, remoteMessageDescription);
        }

        return message;
    }

    public static String addMessageDescription(Shell shell, MessageDescription messageDescription) {

        StringBuilder command = new StringBuilder();
        command.append("ADDMSGD"); //$NON-NLS-1$

        return addOrChangeMessageDescription(shell, messageDescription, command);
    }

    public static String changeMessageDescription(Shell shell, MessageDescription messageDescription) {

        StringBuilder command = new StringBuilder();
        command.append("CHGMSGD"); //$NON-NLS-1$

        return addOrChangeMessageDescription(shell, messageDescription, command);
    }

    private static String addOrChangeMessageDescription(Shell shell, MessageDescription messageDescription, StringBuilder command) {

        command.append(" MSGID(" + messageDescription.getMessageId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" MSGF(" + messageDescription.getLibrary() + "/" + messageDescription.getMessageFile() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        command.append(" MSG('" + StringHelper.addQuotes(messageDescription.getMessage()) + "')"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" SECLVL('" + StringHelper.addQuotes(messageDescription.getHelpText()) + "')"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" SEV(" + messageDescription.getSeverity().toString() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" CCSID(" + messageDescription.getCcsidAsString() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" FMT(" + getFieldFormats(messageDescription) + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        AS400 system = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());
        String message = executeCommand(shell, system, command);

        return message;

    }

    public static String removeMessageDescription(Shell shell, MessageDescription messageDescription) {

        AS400 system = IBMiHostContributionsHandler.getSystem(messageDescription.getConnection());

        StringBuilder command = new StringBuilder();
        command.append("RMVMSGD"); //$NON-NLS-1$
        command.append(" MSGID(" + messageDescription.getMessageId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        command.append(" MSGF(" + messageDescription.getLibrary() + "/" + messageDescription.getMessageFile() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        String message = executeCommand(shell, system, command);

        return message;
    }

    public static MessageDescription retrieveMessageDescription(Shell shell, String connectionName, String messageFile, String library,
        String messageId) {

        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);

        QMHRTVM qmhrtvm = new QMHRTVM();
        MessageDescription[] messageDescription = qmhrtvm.run(system, connectionName, library, messageFile, messageId);
        if (messageDescription != null && messageDescription.length == 1) {
            return messageDescription[0];
        }

        return null;
    }

    public static MessageDescription refreshMessageDescription(Shell shell, MessageDescription messageDescription) {

        String connectionName = messageDescription.getConnection();
        String library = messageDescription.getLibrary();
        String messageFile = messageDescription.getMessageFile();
        String messageId = messageDescription.getMessageId();

        MessageDescription currentValues = retrieveMessageDescription(shell, connectionName, messageFile, library, messageId);
        if (currentValues != null) {
            messageDescription.setMessage(currentValues.getMessage());
            messageDescription.setHelpText(currentValues.getHelpText());
            messageDescription.setSeverity(currentValues.getSeverity());
            messageDescription.setCcsid(currentValues.getCcsid());
            messageDescription.setFieldFormats(currentValues.getFieldFormats());
        }

        return messageDescription;
    }

    private static String getFieldFormats(MessageDescription messageDescription) {

        StringBuilder formats = new StringBuilder();

        ArrayList<?> fieldFormats = messageDescription.getFieldFormats();
        if (fieldFormats.size() == 0) {
            formats.append("*NONE"); //$NON-NLS-1$
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

    private static String executeCommand(Shell shell, AS400 system, StringBuilder command) {

        String message = null;

        try {
            AS400Message[] messageList = null;
            CommandCall commandCall = new CommandCall(system);
            if (!commandCall.run(command.toString())) {
                messageList = commandCall.getMessageList();
                if (messageList.length > 0) {
                    message = messageList[0].getText();
                } else {
                    message = Messages.Unknown_error_occured;
                }
            }
        } catch (Exception e) {
            message = e.getLocalizedMessage();
        }

        displayError(shell, message);

        return message;
    }

    private static void displayError(Shell shell, String message) {

        if (shell == null || message == null) {
            return;
        }

        MessageDialog.openError(shell, Messages.E_R_R_O_R, message);
    }
}
