/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrievemessagedescription;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.core.messagefileeditor.FieldFormat;
import biz.isphere.core.messagefileeditor.MessageDescription;

import com.ibm.as400.access.AS400;

/**
 * Class to hold the result of the IQMHRTVM API.
 * 
 * @author Thomas Raddatz
 */
public class IQMHRTVMResult extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String NUMBER_OF_MESSAGES_RETURNED = "numberOfMessagesReturned"; //$NON-NLS-1$
    private static final String FIRST_MESSAGE_ID_RETURNED = "firstMessageIdReturned"; //$NON-NLS-1$
    private static final String LAST_MESSAGE_ID_RETURNED = "lastMessageIdReturned"; //$NON-NLS-1$
    private static final String RESERVED = "reserved"; //$NON-NLS-1$
    private static final String OFFSET_TO_FIRST_MESSAGE = "offsetToFirstMessage"; //$NON-NLS-1$

    private String messageFile;
    private String library;
    private String connectionName;

    /**
     * Constructs a IQMHRTVMResult object.
     * 
     * @param system - System that executed the retrieve request
     * @param connectionName - name of the RDi connection
     * @param messageFile - message file
     * @param library - message file library
     * @param result, returned by the IQMHRTVM API
     * @throws UnsupportedEncodingException
     */
    public IQMHRTVMResult(AS400 system, String connectionName, String messageFile, String library, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "IQMHRTVMHeader");

        this.messageFile = messageFile;
        this.library = library;
        this.connectionName = connectionName;

        createStructure();

        setBytes(bytes);
    }

    /**
     * Returns the number of bytes returned by the IQMHRTVM API.
     * 
     * @return number of bytes returned
     */
    public int getBytesReturned() {
        return getInt4Value(BYTES_RETURNED);
    }

    /**
     * Returns the number of bytes available.
     * 
     * @return bytes available
     */
    public int getBytesAvailable() {
        return getInt4Value(BYTES_AVAILABLE);
    }

    /**
     * Returns the number of messages returned by the IQMHRTVM API.
     * 
     * @return number of messages returned
     */
    public int getNumberOfMessagesReturned() {
        return getInt4Value(NUMBER_OF_MESSAGES_RETURNED);
    }

    /**
     * Returns the first message ID returned by the IQMHRTVM API.
     * 
     * @return first message ID returned
     * @throws UnsupportedEncodingException
     */
    public String getFirstMessageIdReturned() throws UnsupportedEncodingException {
        return getCharValue(FIRST_MESSAGE_ID_RETURNED);
    }

    /**
     * Returns the last message ID returned by the IQMHRTVM API.
     * 
     * @return last message ID returned
     * @throws UnsupportedEncodingException
     */
    public String getLastMessageIdReturned() throws UnsupportedEncodingException {
        return getCharValue(LAST_MESSAGE_ID_RETURNED);
    }

    /**
     * Returns the message descriptions retrieved by the IQMHRTVM API.
     * 
     * @return list of message descriptions
     * @throws UnsupportedEncodingException
     */
    public List<MessageDescription> getMessages() throws UnsupportedEncodingException {

        ArrayList<MessageDescription> messages = new ArrayList<MessageDescription>();

        RTVM0300 rtvm0300 = new RTVM0300(getSystem(), getBytes());
        int offset = getOffsetFirstMessage();

        for (int i = 0; i < getNumberOfMessagesReturned(); i++) {

            rtvm0300.setOffset(offset);

            ArrayList<FieldFormat> fieldFormats = new ArrayList<FieldFormat>();

            SubstitutionVariable variable = new SubstitutionVariable(getSystem(), getBytes());
            int offsetFirstSubstitutionVariable = offset + rtvm0300.getOffsetSubstitutionVariables();

            int offsetVariable = offsetFirstSubstitutionVariable;
            for (int f = 0; f < rtvm0300.getNumberOfSubstitutionVariables(); f++) {

                variable.setOffset(offsetVariable);

                FieldFormat fieldFormat = new FieldFormat();

                fieldFormat.setType(variable.getType());
                if (variable.getLengthOfReplacementData() == -1) {
                    fieldFormat.setVary(true);
                    fieldFormat.setBytes(variable.getDecimalPositions());
                } else {
                    fieldFormat.setVary(false);
                    fieldFormat.setLength(variable.getLengthOfReplacementData());
                    fieldFormat.setDecimalPositions(variable.getDecimalPositions());
                }

                fieldFormats.add(fieldFormat);
                
                offsetVariable = offsetVariable + rtvm0300.getLengthOfSubstitutionVariableFormatElement();
            }

            String helpText = rtvm0300.getMessageHelp();
            if (helpText == null || helpText.trim().length() == 0) {
                helpText = "*NONE"; //$NON-NLS-1$
            }
            
            MessageDescription messageDescription = new MessageDescription();
            messageDescription.setConnection(connectionName);
            messageDescription.setLibrary(library);
            messageDescription.setMessageFile(messageFile);
            messageDescription.setMessageId(rtvm0300.getMessageId());
            messageDescription.setMessage(rtvm0300.getMessage());
            messageDescription.setHelpText(helpText);
            messageDescription.setFieldFormats(fieldFormats);
            messageDescription.setSeverity(rtvm0300.getMessageSeverity());
            messageDescription.setCcsid(rtvm0300.getCcsid());
            messages.add(messageDescription);

            offset += rtvm0300.getBytesReturned();
        }

        return messages;
    }

    /**
     * Returns the offset to the first message description.
     * 
     * @return offset of first message description
     */
    private int getOffsetFirstMessage() {
        return getInt4Value(OFFSET_TO_FIRST_MESSAGE);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(NUMBER_OF_MESSAGES_RETURNED, 8);
        addCharField(FIRST_MESSAGE_ID_RETURNED, 12, 7);
        addCharField(LAST_MESSAGE_ID_RETURNED, 19, 7);
        addCharField(RESERVED, 26, 2);
        addInt4Field(OFFSET_TO_FIRST_MESSAGE, 28);
    }
}
