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
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.core.messagefileeditor.RangeOfReplyValues;
import biz.isphere.core.messagefileeditor.RelationalTestEntry;
import biz.isphere.core.messagefileeditor.SpecialReplyValueEntry;
import biz.isphere.core.messagefileeditor.ValidReplyEntry;

import com.ibm.as400.access.AS400;

/**
 * Format RTVM0400 of the Retrieve Message (QMHRTVM) API.
 * 
 * @author Thomas Raddatz
 */
public class RTVM0400 extends RTVM0300 {

    private static final String REPLY_TYPE = "replyType"; //$NON-NLS-1$
    private static final String RESERVED_2 = "reserved_2"; //$NON-NLS-1$
    private static final String MAXIMUM_REPLY_LENGTH = "maximumReplyLength"; //$NON-NLS-1$
    private static final String MAXIMUM_REPLY_DECIMAL_POSITIONS = "maximumReplyDecimalPositions"; //$NON-NLS-1$
    private static final String OFFSET_OF_VALID_REPLY_VALUE_ENTRIES = "offsetOfvalidReplyValueEntries"; //$NON-NLS-1$
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
        super(system, bytes, "RTVM0400");
    }

    /**
     * Sets the offset to a particular message description.
     */
    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    /**
     * Returns the reply type.
     * 
     * @return reply type.
     * @throws UnsupportedEncodingException
     */
    public String getReplyType() throws UnsupportedEncodingException {
        return getCharValue(REPLY_TYPE).trim();
    }

    /**
     * Returns the maximum reply length.
     * 
     * @return reply length
     */
    public int getReplyLength() {
        return getInt4Value(MAXIMUM_REPLY_LENGTH);
    }

    /**
     * Returns the maximum reply decimal positions.
     * 
     * @return decimal positions
     */
    public int getReplyDecimalPositions() {
        return getInt4Value(MAXIMUM_REPLY_DECIMAL_POSITIONS);
    }

    /**
     * Returns the offset to the list of valid reply entries.
     * 
     * @return offset to reply entries
     */
    public int getOffsetValidReplyEntries() {
        return getInt4Value(OFFSET_OF_VALID_REPLY_VALUE_ENTRIES);
    }

    /**
     * Returns the number of valid reply entries.
     * 
     * @return number of reply entries
     */
    public int getNumberOfValidReplyEntries() {
        return getInt4Value(NUMBER_OF_VALID_REPLY_VALUE_ENTRIES_RETRUEND);
    }

    /**
     * Returns the length of the valid reply entry.
     * 
     * @return length of reply entry
     */
    public int getLengthOfValidReplyEntry() {
        return getInt4Value(LENGTH_OF_VALID_REPLY_VALUE_ENTRY);
    }

    /**
     * Returns the offset to the list of valid reply entries.
     * 
     * @return offset to reply entries
     */
    public int getOffsetSpecialReplyValueEntries() {
        return getInt4Value(OFFSET_OF_SPECIAL_REPLY_VALUE_ENTRIES);
    }

    /**
     * Returns the number of valid reply entries.
     * 
     * @return number of reply entries
     */
    public int getNumberOfSpecialReplyValueEntries() {
        return getInt4Value(NUMBER_OF_SPECIAL_REPLY_VALUES_RETURNED);
    }

    /**
     * Returns the length of the special reply value entry.
     * 
     * @return length of reply entry
     */
    public int getLengthOfSpecialReplyValueEntry() {
        return getInt4Value(LENGTH_OF_SPECIAL_REPLY_VALUE_ENTRY);
    }

    /**
     * Returns the length of the special reply value entry.
     * 
     * @return length of reply entry
     */
    public int getStoredCcsid() {
        return getInt4Value(STORED_CCSID_OF_MESSAGE);
    }

    public RangeOfReplyValues getRangeOfReplyValue() throws UnsupportedEncodingException {
        
        RangeOfReplyValues rangeOfReplyValue = new RangeOfReplyValues();
        rangeOfReplyValue.setLowerValue(getLowerRangeReplyValue());
        rangeOfReplyValue.setUpperValue(getUpperRangeReplyValue());
        
        return rangeOfReplyValue;
    }
    
    /**
     * Returns the 'lower range reply value.
     * 
     * @return lower range reply value
     * @throws UnsupportedEncodingException
     */
    private String getLowerRangeReplyValue() throws UnsupportedEncodingException {

        int offset = getInt4Value(OFFSET_OF_LOWER_RANGE_REPLY_VALUE);
        int length = getInt4Value(LENGTH_OF_LOWER_RANGE_REPLY_VALUE_RETURNED);
        if (length > 0) {
            return StringHelper.trimR(convertToText(getBytesAt(offset, length)));
        }

        return MessageDescription.VALUE_NONE;
    }

    /**
     * Returns the 'upper range reply value.
     * 
     * @return upper range reply value
     * @throws UnsupportedEncodingException
     */
    private String getUpperRangeReplyValue() throws UnsupportedEncodingException {

        int offset = getInt4Value(OFFSET_OF_UPPER_RANGE_REPLY_VALUE);
        int length = getInt4Value(LENGTH_OF_UPPER_RANGE_REPLY_VALUE_RETURNED);
        if (length > 0) {
            return StringHelper.trimR(convertToText(getBytesAt(offset, length)));
        }

        return "";
    }

    /**
     * Returns the offset to the relational test entry.
     * 
     * @return offset to relational test entry
     */
    public int getOffsetRelationalTestEntry() {
        return getInt4Value(OFFSET_OF_RELATIONAL_TEST_ENTRY);
    }

    /**
     * Returns the length of the relational test entry.
     * 
     * @return length of relational test entry
     */
    public int getLengthOfRelationalTestEntry() {
        return getInt4Value(LENGTH_OF_RELATIONAL_TEST_ENTRY_RETURNED);
    }

    public MessageDescription createMessageDescription(String connectionName, String messageFile, String library) throws UnsupportedEncodingException {

        MessageDescription messageDescription = super.createMessageDescription(connectionName, messageFile, library);

        messageDescription.setReplyType(getReplyType());
        messageDescription.setReplyLength(getReplyLength());
        messageDescription.setReplyDecimalPositions(getReplyDecimalPositions());
        messageDescription.setValidReplyEntries(getValidReplyEntries());
        messageDescription.setSpecialReplyValueEntries(getSpecialReplyValueEntries());
        messageDescription.setRangeOfReplyValue(getRangeOfReplyValue());
        messageDescription.setRelationalTestEntry(getRelationalTestEntry());

        return messageDescription;  
    }

    /**
     * Returns the valid reply value entries of a given message description.
     * 
     * @param rtvm0400 - message description
     * @param offset - offset from the start of the returned data
     * @return valid reply entries
     * @throws UnsupportedEncodingException
     */
    private ArrayList<ValidReplyEntry> getValidReplyEntries() throws UnsupportedEncodingException {

        ArrayList<ValidReplyEntry> replyEntries = new ArrayList<ValidReplyEntry>();

        ValidReplyEntryFormat validReplyEntryFormat = new ValidReplyEntryFormat(getSystem(), getBytes());
        int offsetFirstReplyEntry = getOffset() + getOffsetValidReplyEntries();

        int offsetVariable = offsetFirstReplyEntry;
        for (int f = 0; f < getNumberOfValidReplyEntries(); f++) {

            validReplyEntryFormat.setOffset(offsetVariable);
            replyEntries.add(validReplyEntryFormat.createValidReplyEntry());

            offsetVariable = offsetVariable + getLengthOfValidReplyEntry();
        }

        return replyEntries;
    }

    /**
     * Returns the special reply value entries of a given message description.
     * 
     * @param rtvm0400 - message description
     * @param offset - offset from the start of the returned data
     * @return special reply entries
     * @throws UnsupportedEncodingException
     */
    private ArrayList<SpecialReplyValueEntry> getSpecialReplyValueEntries() throws UnsupportedEncodingException {

        ArrayList<SpecialReplyValueEntry> specialReplyValueEntries = new ArrayList<SpecialReplyValueEntry>();

        SpecialReplyValueEntryFormat specialReplyValueFormat = new SpecialReplyValueEntryFormat(getSystem(), getBytes());
        int offsetFirstReplyEntry = getOffset() + getOffsetSpecialReplyValueEntries();

        int offsetVariable = offsetFirstReplyEntry;
        for (int f = 0; f < getNumberOfSpecialReplyValueEntries(); f++) {

            specialReplyValueFormat.setOffset(offsetVariable);
            specialReplyValueEntries.add(specialReplyValueFormat.createSpecialReplyValueEntry());

            offsetVariable = offsetVariable + getLengthOfSpecialReplyValueEntry();
        }

        return specialReplyValueEntries;
    }

    /**
     * Returns the relational test entry of a given message description.
     * 
     * @param rtvm0400 - message description
     * @param offset - offset from the start of the returned data
     * @return relational test entry
     * @throws UnsupportedEncodingException
     */
    private RelationalTestEntry getRelationalTestEntry() throws UnsupportedEncodingException {

        if (getLengthOfRelationalTestEntry() == 0) {
            return new RelationalTestEntry();
        }
        
        RelationalTestEntryFormat relationalTestEntryFormat = new RelationalTestEntryFormat(getSystem(), getBytes());
        relationalTestEntryFormat.setOffset(getOffset() + getOffsetRelationalTestEntry());

        return relationalTestEntryFormat.createRelationalTestEntry();
    }

    /**
     * Creates the RTVM0400 structure.
     */
    protected void createStructure() {
        super.createStructure();

        addCharField(REPLY_TYPE, 104, 10);
        addCharField(RESERVED_2, 114, 2);
        addInt4Field(MAXIMUM_REPLY_LENGTH, 116);
        addInt4Field(MAXIMUM_REPLY_DECIMAL_POSITIONS, 120);
        addInt4Field(OFFSET_OF_VALID_REPLY_VALUE_ENTRIES, 124);
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
