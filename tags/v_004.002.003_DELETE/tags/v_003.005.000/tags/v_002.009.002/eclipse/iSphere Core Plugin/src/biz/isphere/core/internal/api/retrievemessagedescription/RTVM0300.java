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

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.core.messagefileeditor.FieldFormat;
import biz.isphere.core.messagefileeditor.MessageDescription;

import com.ibm.as400.access.AS400;

/**
 * Format RTVM0300 of the Retrieve Message (QMHRTVM) API.
 * 
 * @author Thomas Raddatz
 */
public class RTVM0300 extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String MESSAGE_SEVERITY = "messageSeverity"; //$NON-NLS-1$
    private static final String ALERT_INDEX = "alertIndex"; //$NON-NLS-1$
    private static final String ALERT_OPTION = "alertOption"; //$NON-NLS-1$
    private static final String LOG_INDICATOR = "logIndicator"; //$NON-NLS-1$
    private static final String MESSAGE_ID = "messageID"; //$NON-NLS-1$
    private static final String RESERVED_1 = "reserved_1"; //$NON-NLS-1$
    private static final String NUMBER_OF_SUBSTITUTION_VARIABLE_FORMATS = "numberOfSubstitutionVariableFormats"; //$NON-NLS-1$
    private static final String CCSID_CONVERSION_STATUS_INDICATOR_TEXT = "CCSIDConversionStatusIndicatorText"; //$NON-NLS-1$
    private static final String CCSID_CONVERSION_STATUS_INDICATOR_OF_REPLACEMENT_DATA = "CCSIDConversionStatusIndicatorOfReplacementData";
    private static final String CCSID_OF_TEXT_RETURNED = "ccsIdOfTextReturned"; //$NON-NLS-1$
    private static final String OFFSET_OF_DEFAULT_REPLY = "OffsetOfDefaultReply";
    private static final String LENGTH_OF_DEFAULT_REPLY_RETURNED = "lengthOfDefaultReplyReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_DEFAULT_REPLY_AVAILABLE = "lengthOfDefaultReplyAvailable"; //$NON-NLS-1$
    private static final String OFFSET_MESSAGE = "offsetMessage"; //$NON-NLS-1$
    private static final String LENGTH_OF_MESSAGE_RETURNED = "lengthOfMessageReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_MESSAGE_AVAILABLE = "lengthOfMessageAvailable"; //$NON-NLS-1$
    private static final String OFFSET_OF_MESSAGE_HELP = "offsetOfMessageHelp"; //$NON-NLS-1$
    private static final String LENGTH_OF_MESSAGE_HELP_RETURNED = "lengthOfMessageHelpReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_MESSAGE_HELP_AVAILABLE = "lengthOfMessageHelpAvailable"; //$NON-NLS-1$
    private static final String OFFSET_OF_SUBSTITUTION_VARIABLE_FORMATS = "offsetOfSubstitutionVariableFormats"; //$NON-NLS-1$
    private static final String LENGTH_OF_SUBSTITUTION_VARIABLE_FORMATS_RETURNED = "lengthOfSubstitutionVariableFormatsReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_SUBSTITUTION_VARIABLE_FORMATS_AVAILABLE = "lengthOfSubstitutionVariableFormatsAvailable"; //$NON-NLS-1$
    private static final String LENGTH_OF_SUBSTITUTION_VARIABLE_FORMAT_ELEMENT = "lengthOfSubstitutionVariableFormatElement"; //$NON-NLS-1$

    /**
     * Constructs a RTVM0300 object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public RTVM0300(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "RTVM0300");

        createStructure();

        setBytes(bytes);
    }

    protected RTVM0300(AS400 system, byte[] bytes, String name) throws UnsupportedEncodingException {
        super(system, name);

        createStructure();

        setBytes(bytes);

    }

    /**
     * Sets the offset to a particular message description.
     */
    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    /**
     * Returns the number of bytes returned for the message description.
     * 
     * @return number of bytes returned
     */
    public int getBytesReturned() {
        return getInt4Value(BYTES_RETURNED);
    }

    /**
     * Returns the number of bytes available for the message description.
     * 
     * @return number of bytes available
     */
    public int getBytesAvailable() {
        return getInt4Value(BYTES_AVAILABLE);
    }

    /**
     * Returns the severity of the message description.
     * 
     * @return severity
     */
    public int getMessageSeverity() {
        return getInt4Value(MESSAGE_SEVERITY);
    }

    /**
     * returns the message ID of the message description.
     * 
     * @return message ID
     * @throws UnsupportedEncodingException
     */
    public String getMessageId() throws UnsupportedEncodingException {
        return getCharValue(MESSAGE_ID);
    }

    /**
     * Returns the number of substitution variable of the message description.
     * 
     * @return number of substitution variables
     */
    public int getNumberOfSubstitutionVariables() {
        return getInt4Value(NUMBER_OF_SUBSTITUTION_VARIABLE_FORMATS);
    }

    /**
     * returns the message text (first level text) of the message description.
     * 
     * @return message text
     * @throws UnsupportedEncodingException
     */
    public String getMessage() throws UnsupportedEncodingException {

        int offset = getInt4Value(OFFSET_MESSAGE);
        int length = getInt4Value(LENGTH_OF_MESSAGE_RETURNED);
        if (length > 0) {
            return convertToText(getBytesAt(offset, length));
        }

        return "";
    }

    /**
     * Returns the message help text (second level text) of the message
     * description.
     * 
     * @return message help text
     * @throws UnsupportedEncodingException
     */
    public String getMessageHelp() throws UnsupportedEncodingException {

        int offset = getInt4Value(OFFSET_OF_MESSAGE_HELP);
        int length = getInt4Value(LENGTH_OF_MESSAGE_HELP_RETURNED);
        if (length > 0) {
            return convertToText(getBytesAt(offset, length));
        }

        return "";
    }

    /**
     * Returns the CCSID of the message text.
     * 
     * @return CCSID of message text
     */
    public int getCcsid() {
        return getInt4Value(CCSID_OF_TEXT_RETURNED);
    }

    /**
     * Returns the offset to the substitution variables of the message
     * description.
     * 
     * @return offset to substitution variables
     */
    public int getOffsetSubstitutionVariables() {
        return getInt4Value(OFFSET_OF_SUBSTITUTION_VARIABLE_FORMATS);
    }

    /**
     * Returns the length of the substitution variable format element.
     * 
     * @return length of substitution variable format
     */
    public int getLengthOfSubstitutionVariableFormatElement() {
        return getInt4Value(LENGTH_OF_SUBSTITUTION_VARIABLE_FORMAT_ELEMENT);
    }

    /**
     * Returns the default reply value.
     * 
     * @return default reply value
     * @throws UnsupportedEncodingException
     */
    public String getDefaultReplyValue() throws UnsupportedEncodingException {

        int offset = getInt4Value(OFFSET_OF_DEFAULT_REPLY);
        int length = getInt4Value(LENGTH_OF_DEFAULT_REPLY_RETURNED);
        if (length > 0) {
            return StringHelper.trimR(convertToText(getBytesAt(offset, length)));
        }

        return MessageDescription.VALUE_NONE;
    }

    /**
     * Factory methods to create a message description.
     * 
     * @param connectionName - connection name
     * @param messageFile - message file
     * @param library - message file library name
     * @return message description
     * @throws UnsupportedEncodingException
     */
    public MessageDescription createMessageDescription(String connectionName, String messageFile, String library) throws UnsupportedEncodingException {

        String helpText = getMessageHelp();
        if (helpText == null || helpText.trim().length() == 0) {
            helpText = MessageDescription.VALUE_NONE;
        }

        MessageDescription messageDescription = new MessageDescription();

        messageDescription.setConnection(connectionName);
        messageDescription.setLibrary(library);
        messageDescription.setMessageFile(messageFile);
        messageDescription.setMessageId(getMessageId());
        messageDescription.setMessage(getMessage());
        messageDescription.setHelpText(helpText);
        messageDescription.setFieldFormats(getFieldFormats());
        messageDescription.setSeverity(getMessageSeverity());
        messageDescription.setCcsid(getCcsid());
        messageDescription.setDefaultReplyValue(getDefaultReplyValue());

        return messageDescription;
    }

    /**
     * Returns the field formats of a given message description.
     * 
     * @param rtvm0400 - message description
     * @param offset - offset from the start of the returned data
     * @return field formats
     * @throws UnsupportedEncodingException
     */
    private ArrayList<FieldFormat> getFieldFormats() throws UnsupportedEncodingException {

        ArrayList<FieldFormat> fieldFormats = new ArrayList<FieldFormat>();

        SubstitutionVariableFormat substitutionVariableFormat = new SubstitutionVariableFormat(getSystem(), getBytes());
        int offsetFirstSubstitutionVariable = getOffset() + getOffsetSubstitutionVariables();

        int offsetVariable = offsetFirstSubstitutionVariable;
        for (int f = 0; f < getNumberOfSubstitutionVariables(); f++) {

            substitutionVariableFormat.setOffset(offsetVariable);
            fieldFormats.add(substitutionVariableFormat.createFieldFormat());

            offsetVariable = offsetVariable + getLengthOfSubstitutionVariableFormatElement();
        }

        return fieldFormats;
    }

    /**
     * Creates the RTVM0300 structure.
     */
    protected void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(MESSAGE_SEVERITY, 8);
        addInt4Field(ALERT_INDEX, 12);
        addCharField(ALERT_OPTION, 16, 9);
        addCharField(LOG_INDICATOR, 25, 1);
        addCharField(MESSAGE_ID, 26, 7);
        addCharField(RESERVED_1, 33, 3);
        addInt4Field(NUMBER_OF_SUBSTITUTION_VARIABLE_FORMATS, 36);
        addInt4Field(CCSID_CONVERSION_STATUS_INDICATOR_TEXT, 40);
        addInt4Field(CCSID_CONVERSION_STATUS_INDICATOR_OF_REPLACEMENT_DATA, 44);
        addInt4Field(CCSID_OF_TEXT_RETURNED, 48);
        addInt4Field(OFFSET_OF_DEFAULT_REPLY, 52);
        addInt4Field(LENGTH_OF_DEFAULT_REPLY_RETURNED, 56);
        addInt4Field(LENGTH_OF_DEFAULT_REPLY_AVAILABLE, 60);
        addInt4Field(OFFSET_MESSAGE, 64);
        addInt4Field(LENGTH_OF_MESSAGE_RETURNED, 68);
        addInt4Field(LENGTH_OF_MESSAGE_AVAILABLE, 72);
        addInt4Field(OFFSET_OF_MESSAGE_HELP, 76);
        addInt4Field(LENGTH_OF_MESSAGE_HELP_RETURNED, 80);
        addInt4Field(LENGTH_OF_MESSAGE_HELP_AVAILABLE, 84);
        addInt4Field(OFFSET_OF_SUBSTITUTION_VARIABLE_FORMATS, 88);
        addInt4Field(LENGTH_OF_SUBSTITUTION_VARIABLE_FORMATS_RETURNED, 92);
        addInt4Field(LENGTH_OF_SUBSTITUTION_VARIABLE_FORMATS_AVAILABLE, 96);
        addInt4Field(LENGTH_OF_SUBSTITUTION_VARIABLE_FORMAT_ELEMENT, 100);
    }
}
