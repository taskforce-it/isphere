/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.util.ArrayList;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class QMHRTVM {

    public MessageDescription[] run(AS400 _as400, String _connection, String _library, String _messageFile, String _messageId) {

        ArrayList<Object> messages = new ArrayList<Object>();

        String retrieveOption;
        String messageId;

        if (_messageId.equals("*ALL")) {

            retrieveOption = "*FIRST";
            messageId = "";

        } else {

            retrieveOption = "*MSGID";
            messageId = _messageId;

        }

        try {

            ProgramCallDocument pcml = new ProgramCallDocument(_as400, "biz.isphere.core.messagefileeditor.QMHRTVM", this.getClass().getClassLoader());
            pcml.setValue("QMHRTVM.messageFile", getStringWithFixLength(_messageFile, 10) + getStringWithFixLength(_library, 10));

            do {

                pcml.setValue("QMHRTVM.retrieveOption", retrieveOption);
                pcml.setValue("QMHRTVM.messageId", messageId);

                boolean rc = pcml.callProgram("QMHRTVM");

                if (rc == false) {

                    AS400Message[] msgs = pcml.getMessageList("QMHRTVM");
                    for (int idx = 0; idx < msgs.length; idx++) {
                        ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
                    }
                    ISpherePlugin.logError("*** Call to QMHRTVM failed. See previous messages ***", null); //$NON-NLS-1$
                    return null;

                } else {

                    retrieveOption = "*NEXT";
                    messageId = (String)pcml.getValue("QMHRTVM.receiver.messageId");

                    if (!messageId.equals("")) {

                        String message = (String)pcml.getValue("QMHRTVM.receiver.message");
                        String helpText = (String)pcml.getValue("QMHRTVM.receiver.help");
                        if (helpText.equals("")) {
                            helpText = "*NONE";
                        }
                        Integer severity = pcml.getIntValue("QMHRTVM.receiver.severity");
                        Integer ccsid = pcml.getIntValue("QMHRTVM.receiver.ccsid");

                        ArrayList<FieldFormat> _fieldFormats = new ArrayList<FieldFormat>();

                        int numberOfSVF = pcml.getIntValue("QMHRTVM.receiver.numberOfSVF");
                        int[] indices = new int[1];

                        for (indices[0] = 0; indices[0] < numberOfSVF; indices[0]++) {

                            String type = (String)pcml.getValue("QMHRTVM.receiver.svf.type", indices);
                            int length = pcml.getIntValue("QMHRTVM.receiver.svf.length", indices);
                            int decimalPositions = pcml.getIntValue("QMHRTVM.receiver.svf.decimalPositions", indices);

                            FieldFormat fieldFormat = new FieldFormat();

                            fieldFormat.setType(type);
                            if (length == -1) {
                                fieldFormat.setVary(true);
                                fieldFormat.setBytes(decimalPositions);
                            } else {
                                fieldFormat.setVary(false);
                                fieldFormat.setLength(length);
                                fieldFormat.setDecimalPositions(decimalPositions);
                            }

                            _fieldFormats.add(fieldFormat);

                        }

                        MessageDescription messageDescription = new MessageDescription();
                        messageDescription.setConnection(_connection);
                        messageDescription.setLibrary(_library);
                        messageDescription.setMessageFile(_messageFile);
                        messageDescription.setMessageId(messageId);
                        messageDescription.setMessage(message);
                        messageDescription.setHelpText(helpText);
                        messageDescription.setFieldFormats(_fieldFormats);
                        messageDescription.setSeverity(severity);
                        messageDescription.setCcsid(ccsid);

                        messages.add(messageDescription);

                    }
                }

            } while (_messageId.equals("*ALL") && !messageId.equals(""));

        } catch (PcmlException e) {

            // Xystem.out.println(e.getLocalizedMessage());
            // e.printStackTrace();
            // Xystem.out.println("*** Call to QMHRTVM failed. ***");
            // return null;
            ISpherePlugin.logError("Failed calling the QMHRTVM API.", e);

        }

        MessageDescription[] _messages = new MessageDescription[messages.size()];
        messages.toArray(_messages);

        return _messages;

    }

    public String getStringWithFixLength(String oldString, int length) {
        StringBuffer newString = new StringBuffer(oldString);
        while (newString.length() < length) {
            newString.append(" ");
        }
        return newString.toString();
    }

}