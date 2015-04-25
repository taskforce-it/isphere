/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrievemessagedescription;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

/**
 * Format RTVM0400 of the Retrieve Message (QMHRTVM) API.
 * 
 * @author Thomas Raddatz
 */
public class RTVM0400 extends APIFormat {

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
    private static final String REPLY_TYPE = "replyType"; //$NON-NLS-1$
    private static final String RESERVED_2 = "reserved_2"; //$NON-NLS-1$
    private static final String MAXIMUM_REPLY_LENGTH = "maximumReplyLength"; //$NON-NLS-1$
    private static final String MAXIMUM_REPLY_DECIMAL_POSITIONS = "maximumReplyDecimalPositions"; //$NON-NLS-1$
    private static final String OFFSET_OFVALID_REPLY_VALUE_ENTRIES = "offsetOfvalidReplyValueEntries"; //$NON-NLS-1$
    private static final String NUMBER_OF_VALID_REPLY_VALUE_ENTRIES_RETRUEND = "numberOfValidReplyValueEntriesRetruend"; //$NON-NLS-1$
    private static final String LENGTH_OF_VALID_REPLY_VALUE_ENTRIES_RETURNED = "lengthOfValidReplyValueEntriesReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_VALID_REPLY_VALUE_ENTRIES_AVAILABLE = "lengthOfValidReplyValueEntriesAvailable"; //$NON-NLS-1$
    private static final String LENGTH_OF_VALID_REPLY_VALUE_ENTRY = "lengthOfValidReplyValueEntry"; //$NON-NLS-1$
    private static final String OFFSET_OF_SPECIAL_REPLY_VALUE_ENTRIES = "offsetOfSpecialReplyValueEntries"; //$NON-NLS-1$
    private static final String NUMBER_OF_SPECIAL_REPLY_VALUES_RETURNED = "numberOfSpecialReplyValuesReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_SPECIAL_REPLY_VALUE_ENTRIES_RETURNED = "lengthOfSpecialReplyValueEntriesReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_SPECIAL_REPLY_VALUE_ENTRIES_AVAILABLE = "lengthOfSpecialReplyValueEntriesAvailable"; //$NON-NLS-1$
    private static final String LENGTH_OF_SPECIAL_REPLY_VALUE_ENTRY = "lengthOfSpecialReplyValueEntry"; //$NON-NLS-1$
    private static final String OFFSET_OF_LOWER_RANGE_REPLY_VALUE = "offsetOfLowerRangeReplyValue"; //$NON-NLS-1$
    private static final String LENGTH_OF_LOWER_RANGE_REPLY_VALUE_RETURNED = "lengthOfLowerRangeReplyValueReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_LOWER_RANGE_REPLY_VALUE_AVAILABLE = "lengthOfLowerRangeReplyValueAvailable"; //$NON-NLS-1$
    private static final String OFFSET_OF_UPPER_RANGE_REPLY_VALUE = "offsetOfUpperRangeReplyValue"; //$NON-NLS-1$
    private static final String LENGTH_OF_UPPER_RANGE_REPLY_VALUE_RETURNED = "lengthOfUpperRangeReplyValueReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_UPPER_RANGE_REPLY_VALUE_AVAILABLE = "lengthOfUpperRangeReplyValueAvailable"; //$NON-NLS-1$
    private static final String OFFSET_OF_RELATIONAL_TEST_ENTRY = "offsetOfRelationalTestEntry"; //$NON-NLS-1$
    private static final String LENGTH_OF_RELATIONAL_TEST_ENTRY_RETURNED = "lengthOfRelationalTestEntryReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_RELATIONAL_TEST_AVAILABLE = "lengthOfRelationalTestAvailable"; //$NON-NLS-1$
    private static final String MESSAGE_CREATION_DATE = "messageCreationDate"; //$NON-NLS-1$
    private static final String RESERVED_3 = "reserved_3"; //$NON-NLS-1$
    private static final String MESSAGE_CREATION_LCVEL_NUMBER = "messageCreationLcvelNumber"; //$NON-NLS-1$
    private static final String MESSAGE_MODIFICATION_DATE = "messageModificationDate"; //$NON-NLS-1$
    private static final String RESERVED_4 = "reserved_4"; //$NON-NLS-1$
    private static final String MESSAGE_MODIFICATION_LEVEL_NUMBER = "messageModificationLevelNumber"; //$NON-NLS-1$
    private static final String STORED_CCSID_OF_MESSAGE = "storedCcsidOfMessage"; //$NON-NLS-1$
    private static final String OFFSET_OFDUMP_LIST_ENTRIES = "offsetOfdumpListEntries"; //$NON-NLS-1$
    private static final String NUMBER_OF_DUMP_LIST_ENTRIES_RETURNED = "numberOfDumpListEntriesReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_DUMP_LIST_ENTRIES_RETURNED = "lengthOfDumpListEntriesReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_DUMP_LIST_ENTRIES_AVAILABLE = "lengthOfDumpListEntriesAvailable"; //$NON-NLS-1$
    private static final String DEFAULT_PROGRAM_NAME = "defaultProgramName"; //$NON-NLS-1$
    private static final String DEFAULT_PROGRAM_LIBRARY_NAME = "defaultProgramLibraryName"; //$NON-NLS-1$

    /**
     * Constructs a RTVM0400 object.
     * 
     * @param system - System that calls the API
     * @param bytes - buffer that contains the retrieved message descriptions
     * @throws UnsupportedEncodingException
     */
    public RTVM0400(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "RTVM0400");

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
     * Creates the RTVM0400 structure.
     */
    private void createStructure() {

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
        addCharField(REPLY_TYPE, 104, 10);
        addCharField(RESERVED_2, 114, 2);
        addInt4Field(MAXIMUM_REPLY_LENGTH, 116);
        addInt4Field(MAXIMUM_REPLY_DECIMAL_POSITIONS, 120);
        addInt4Field(OFFSET_OFVALID_REPLY_VALUE_ENTRIES, 124);
        addInt4Field(NUMBER_OF_VALID_REPLY_VALUE_ENTRIES_RETRUEND, 128);
        addInt4Field(LENGTH_OF_VALID_REPLY_VALUE_ENTRIES_RETURNED, 132);
        addInt4Field(LENGTH_OF_VALID_REPLY_VALUE_ENTRIES_AVAILABLE, 136);
        addInt4Field(LENGTH_OF_VALID_REPLY_VALUE_ENTRY, 140);
        addInt4Field(OFFSET_OF_SPECIAL_REPLY_VALUE_ENTRIES, 144);
        addInt4Field(NUMBER_OF_SPECIAL_REPLY_VALUES_RETURNED, 148);
        addInt4Field(LENGTH_OF_SPECIAL_REPLY_VALUE_ENTRIES_RETURNED, 152);
        addInt4Field(LENGTH_OF_SPECIAL_REPLY_VALUE_ENTRIES_AVAILABLE, 156);
        addInt4Field(LENGTH_OF_SPECIAL_REPLY_VALUE_ENTRY, 160);
        addInt4Field(OFFSET_OF_LOWER_RANGE_REPLY_VALUE, 164);
        addInt4Field(LENGTH_OF_LOWER_RANGE_REPLY_VALUE_RETURNED, 168);
        addInt4Field(LENGTH_OF_LOWER_RANGE_REPLY_VALUE_AVAILABLE, 172);
        addInt4Field(OFFSET_OF_UPPER_RANGE_REPLY_VALUE, 176);
        addInt4Field(LENGTH_OF_UPPER_RANGE_REPLY_VALUE_RETURNED, 180);
        addInt4Field(LENGTH_OF_UPPER_RANGE_REPLY_VALUE_AVAILABLE, 184);
        addInt4Field(OFFSET_OF_RELATIONAL_TEST_ENTRY, 188);
        addInt4Field(LENGTH_OF_RELATIONAL_TEST_ENTRY_RETURNED, 192);
        addInt4Field(LENGTH_OF_RELATIONAL_TEST_AVAILABLE, 196);
        addCharField(MESSAGE_CREATION_DATE, 200, 7);
        addCharField(RESERVED_3, 207, 1);
        addInt4Field(MESSAGE_CREATION_LCVEL_NUMBER, 208);
        addCharField(MESSAGE_MODIFICATION_DATE, 212, 7);
        addCharField(RESERVED_4, 219, 1);
        addInt4Field(MESSAGE_MODIFICATION_LEVEL_NUMBER, 220);
        addInt4Field(STORED_CCSID_OF_MESSAGE, 224);
        addInt4Field(OFFSET_OFDUMP_LIST_ENTRIES, 228);
        addInt4Field(NUMBER_OF_DUMP_LIST_ENTRIES_RETURNED, 232);
        addInt4Field(LENGTH_OF_DUMP_LIST_ENTRIES_RETURNED, 236);
        addInt4Field(LENGTH_OF_DUMP_LIST_ENTRIES_AVAILABLE, 240);
        addCharField(DEFAULT_PROGRAM_NAME, 244, 10);
        addCharField(DEFAULT_PROGRAM_LIBRARY_NAME, 254, 10);
    }
}
