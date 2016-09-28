/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.eclipse.swt.SWT;

import biz.isphere.core.internal.api.APIDateTimeFieldDescription;
import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;

/**
 * Message entry, that is returned by the QMHRDQM API.
 * 
 * @author Thomas Raddatz
 */
public class RDQM0200MessageEntry extends APIFormat {

    protected static final int LENGTH_OF_FIRST_FIELDS_OF_MESSSAGE_ENTRY = 16;

    private static final String OFFSET_TO_NEXT_MESSAGE_ENTRY = "offsetToNextMessageEntry"; //$NON-NLS-1$
    private static final String MESSAGE_ENQUEUE_DATE_AND_TIME = "messageEnqueueDateAndTime"; //$NON-NLS-1$
    private static final String ENQUEUED_MESSAGE_ENTRY_LENGTH = "enqueuedMessageEntryLength"; //$NON-NLS-1$

    private RDQM0200 rdqm0200;

    /**
     * Constructs a RDQM0200MessageEntry object.
     * 
     * @param system - System that is used to create the converters
     * @param bytes - bytes that have be returned by the QMHRDQM API
     * @param offset - offset from the beginning of 'bytes' to this message
     *        entry
     * @param keyLength - length of the message key
     * @throws UnsupportedEncodingException
     */
    public RDQM0200MessageEntry(AS400 system, int offset, RDQM0200 rdqm0200) throws UnsupportedEncodingException {
        super(system, "RDQM0200MessageEntry"); //$NON-NLS-1$
        setOffset(offset);

        this.rdqm0200 = rdqm0200;

        createStructure(0);

        setBytes(rdqm0200.getBytes());
    }

    /**
     * Returns the offset to the next message entry.
     * 
     * @return offset to the next message entry
     */
    public int getOffsetToNextMessageEntry() {
        return getInt4Value(OFFSET_TO_NEXT_MESSAGE_ENTRY);
    }

    /**
     * The date and time that the message was placed on the data queue. Its
     * format is a system time stamp (*DTS). The Convert Date and Time Format
     * (QWCCVTDT) API can be used to convert this time stamp to a character
     * format.
     * 
     * @return data and time the message has been enqueued
     * @throws AS400SecurityException
     * @throws ErrorCompletingRequestException
     * @throws InterruptedException
     * @throws IOException
     * @throws ObjectDoesNotExistException
     */
    public Date getMessageEnqueueDateAndTime() throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException,
        ObjectDoesNotExistException {
        return getDateTimeValue(MESSAGE_ENQUEUE_DATE_AND_TIME);
    }

    /**
     * The number of bytes specified for the message entry length when the entry
     * was placed on the data queue. For a data queue created with
     * SENDERID(*YES), this length is the message entry length specified plus 36
     * bytes for the sender ID. This is the number of bytes returned in the
     * message text field unless the number of message text bytes to retrieve
     * field is less than this value. In that case, the maximum message text
     * length requested is returned in the message text field.
     * 
     * @return length of message text
     */
    public int getEnqueuedMesageEntryLength() {
        return getInt4Value(ENQUEUED_MESSAGE_ENTRY_LENGTH);
    }

    /**
     * Returns the bytes of the message key.
     * 
     * @return message key bytes
     */
    public byte[] getKeyBytes() {

        return getBytesAt(getOffsetMessageKey(), getLengthMessageKey());
    }

    /**
     * Returns the bytes of the message text.
     * 
     * @return message text bytes
     */
    public byte[] getMessageBytes() {

        return getMessageBytes(false);
    }

    /**
     * Returns the bytes of the message text. Optionally the sender ID can be
     * included in the message text.
     * 
     * @param includeSenderID - specifies whether or not the sender ID is
     *        included in the message text
     * @return message text bytes
     */
    public byte[] getMessageBytes(boolean includeSenderID) {

        int length = getLengthMessageText(includeSenderID);
        if (length <= 0) {
            return new byte[] {};
        }
        return getBytesAt(getOffsetMessageText(includeSenderID), length);
    }

    /**
     * Returns the message key.
     * 
     * @return message text
     * @throws UnsupportedEncodingException
     */
    public String getKeyText() throws UnsupportedEncodingException {

        return convertToText(getKeyBytes(), true);
    }

    /**
     * Returns the message text.
     * 
     * @return message text
     * @throws UnsupportedEncodingException
     */
    public String getMessageText() throws UnsupportedEncodingException {

        return getMessageText(false);
    }

    public int getMessageTextLength() {
        return getMessageTextLength(false);
    }

    public int getMessageTextLength(boolean includeSenderId) {
        return getLengthMessageText(includeSenderId);
    }

    /**
     * Returns the message text. Optionally the sender ID can be included in the
     * message text.
     * 
     * @param includeSenderID - specifies whether or not the sender ID is
     *        included in the message text
     * @return message text
     * @throws UnsupportedEncodingException
     */
    public String getMessageText(boolean includeSenderID) throws UnsupportedEncodingException {

        return convertToText(getMessageBytes(includeSenderID), true);
    }

    /**
     * Returns the sender ID.
     * 
     * @return sender ID
     * @throws UnsupportedEncodingException
     */
    public RDQM0200SenderID getSenderID() throws UnsupportedEncodingException {

        if (!rdqm0200.isSenderIDIncluded()) {
            return null;
        }

        return new RDQM0200SenderID(getSystem(), getOffset() + LENGTH_OF_FIRST_FIELDS_OF_MESSSAGE_ENTRY + rdqm0200.getMessageKeyLengthReturned(),
            rdqm0200);
    }

    /**
     * Returns the RDQM0200 format this message entry is associated to.
     * 
     * @return RDQM0200 format
     */
    public RDQM0200 getRDQM0200() {
        return rdqm0200;
    }

    public boolean isTableViewerDataTruncation() {

        if (isWin32() && getMessageTextLength() > getDataTruncationWarningLength()) {
            return true;
        } else {
            return false;
        }
    }

    public static int getDataTruncationWarningLength() {

        if (isWin32()) {
            return 259;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private static boolean isWin32() {
        return "win32".equals(SWT.getPlatform());
    }

    /**
     * Returns the offset to the message text.
     * 
     * @param includeSenderID - specifies whether or not the sender ID is
     *        included in the message text
     * @return offset to message text
     */
    private int getOffsetMessageText(boolean includeSenderID) {

        int offset = LENGTH_OF_FIRST_FIELDS_OF_MESSSAGE_ENTRY + rdqm0200.getMessageKeyLengthReturned();

        if (mustExcludeSenderID(includeSenderID)) {
            offset = offset + RDQM0200SenderID.LENGTH_OF_SENDER_ID;
        }

        return offset;
    }

    /**
     * Returns the length off the message text.
     * 
     * @param includeSenderID - specifies whether or not the sender ID is
     *        included in the message text
     * @return length off message text
     */
    private int getLengthMessageText(boolean includeSenderID) {

        int length = rdqm0200.getMaximumMessageTextLengthRequested();

        if (length > getEnqueuedMesageEntryLength()) {
            length = getEnqueuedMesageEntryLength();
        }

        if (length > rdqm0200.getMaximumMessageTextLengthAvailable()) {
            length = rdqm0200.getMaximumMessageTextLengthAvailable();
        }

        if (mustExcludeSenderID(includeSenderID)) {
            length = length - RDQM0200SenderID.LENGTH_OF_SENDER_ID;
        }

        return length;
    }

    /**
     * Returns the offset to the message key.
     * 
     * @return offset to message text
     */
    private int getOffsetMessageKey() {

        return LENGTH_OF_FIRST_FIELDS_OF_MESSSAGE_ENTRY;
    }

    /**
     * Returns the length off the message key.
     * 
     * @return length off message text
     */
    private int getLengthMessageKey() {

        return rdqm0200.getMessageKeyLengthReturned();
    }

    /**
     * Returns <code>true</code> if the sender ID must be excluded.
     * 
     * @param includeSenderID - original request that specifies whether or not
     *        to include/exclude the sender ID.
     * @return modified request based on the original request and whether or not
     *         the message text contains a sender ID
     */
    private boolean mustExcludeSenderID(boolean includeSenderID) {

        if (!rdqm0200.isSenderIDIncluded()) {
            return false;
        }

        if (includeSenderID) {
            return false;
        }

        return true;
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure(int offset) {

        addInt4Field(OFFSET_TO_NEXT_MESSAGE_ENTRY, offset + 0);
        addDateTimeField(MESSAGE_ENQUEUE_DATE_AND_TIME, offset + 4, 8, APIDateTimeFieldDescription.DTS);
        addInt4Field(ENQUEUED_MESSAGE_ENTRY_LENGTH, offset + 12);
    }
}
