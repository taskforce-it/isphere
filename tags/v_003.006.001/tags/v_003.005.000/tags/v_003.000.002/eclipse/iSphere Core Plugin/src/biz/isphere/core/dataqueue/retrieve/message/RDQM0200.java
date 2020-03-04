/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.message;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

/**
 * Format RDQM0200 of the <i>Retrieve Data Queue Message</i> (QMHRDQM) API. This
 * format is used to retrieve the messages of a given data queue.
 * 
 * @author Thomas Raddatz
 */
public class RDQM0200 extends APIFormat {

    private static final int GUESSED_LENGTH_OF_RESERVED_FIELDS = 16;

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String NUMBER_OF_MESSAGES_RETURNED = "numberOfMessagesReturned"; //$NON-NLS-1$
    private static final String NUMBER_OF_MESSAGES_AVAILABLE = "numberOfMessagesAvailable"; //$NON-NLS-1$
    private static final String MESSAGE_KEY_LENGTH_RETURNED = "messageKeyLengthReturned"; //$NON-NLS-1$
    private static final String MESSAGE_KEY_LENGTH_AVAILABLE = "messageKeyLengthAvailable"; //$NON-NLS-1$
    private static final String MAXIMUM_MESSAGE_TEXT_LENGTH_REQUESTED = "maximumMessageTextLengthRequested"; //$NON-NLS-1$
    private static final String MAXIMUM_MESSAGE_TEXT_LENGTH_AVAILABLE = "maximumMessageTextLengthAvailable"; //$NON-NLS-1$
    private static final String RESERVED_1 = "reserved_1"; //$NON-NLS-1$
    private static final String OFFSET_TO_FIRST_MESSAGE_ENTRY = "offsetToFirstMessageEntry"; //$NON-NLS-1$
    private static final String ACTUAL_DATA_QUEUE_LIBRARY_NAME = "actualDataQueueLibraryName"; //$NON-NLS-1$
    private static final String MESSAGE_BUFFER = "messageBuffer"; //$NON-NLS-1$

    private boolean isSenderIDIncluded;
    private RDQM0200MessageEntry[] messages;
    private int maximumMessageLength;
    private int numMessagesToRetrieve;

    /**
     * Constructs a RDQM0200 object for a given system.
     * 
     * @param system - system used to create the data converter
     * @param numMessages - number of messages to retrieve
     * @param messageLength - message length
     * @param isSenderIdIncludedInMessageText - specifies whether or not to the
     *        sender ID is included in the message text
     * @throws UnsupportedEncodingException
     */
    public RDQM0200(AS400 system, int numMessages, int messageLength, boolean isSenderIdIncludedInMessageText) throws UnsupportedEncodingException {
        this(system, getMessageBufferSize(numMessages, messageLength, isSenderIdIncludedInMessageText), isSenderIdIncludedInMessageText, numMessages);
    }

    /**
     * Constructs a RDQM0200 object for a given system.
     * 
     * @param system - system used to create the data converter
     * @param numMessages - number of messages to retrieve
     * @param messageLength - message length
     * @param isSenderIdIncludedInMessageText - specifies whether or not to the
     *        sender ID is included in the message text
     * @param keyLength - key length
     * @throws UnsupportedEncodingException
     */
    public RDQM0200(AS400 system, int numMessages, int messageLength, boolean isSenderIdIncludedInMessageText, int keyLength)
        throws UnsupportedEncodingException {
        this(system, getMessageBufferSize(numMessages, messageLength + keyLength, isSenderIdIncludedInMessageText), isSenderIdIncludedInMessageText,
            numMessages);
    }

    /**
     * Constructs a RDQM0200 object for a given system.
     * 
     * @param system - system used to create the data converter
     * @param messageBufferSize
     * @throws UnsupportedEncodingException
     */
    private RDQM0200(AS400 system, int messageBufferSize, boolean isSenderIdIncluded, int numMessages) throws UnsupportedEncodingException {
        super(system, "RDQM0200"); //$NON-NLS-1$

        this.isSenderIDIncluded = isSenderIdIncluded;
        this.messages = null;
        this.maximumMessageLength = -1;
        this.numMessagesToRetrieve = numMessages;

        createStructure(messageBufferSize);
    }

    /**
     * The number of bytes of data returned.
     * 
     * @return number of bytes returned
     */
    public int getBytesReturned() {
        return getInt4Value(BYTES_RETURNED);
    }

    /**
     * The number of bytes of data available to be returned. All available data
     * is returned if enough space is provided.
     * 
     * @return number of bytes available
     */
    public int getBytesAvailable() {
        return getInt4Value(BYTES_AVAILABLE);
    }

    /**
     * The number of messages on the data queue that satisfy the search criteria
     * specified in the Message selection information parameter.
     * 
     * @return number of messages available
     */
    public int getNumberOfMessagesAvailable() {
        return getInt4Value(NUMBER_OF_MESSAGES_AVAILABLE);
    }

    /**
     * The number of messages retrieved.
     * 
     * @return number of messages returned
     */
    public int getNumberOfMessagesReturned() {
        return getInt4Value(NUMBER_OF_MESSAGES_RETURNED);
    }

    /**
     * The number of bytes retrieved in the message key field.
     * 
     * @return length of message key
     */
    public int getMessageKeyLengthReturned() {
        return getInt4Value(MESSAGE_KEY_LENGTH_RETURNED);
    }

    /**
     * The size (in bytes) of the key at the creation time of the data queue.
     * 
     * @return length of message key
     */
    public int getMessageKeyLengthAvailable() {
        return getInt4Value(MESSAGE_KEY_LENGTH_AVAILABLE);
    }

    /**
     * The value specified in the message selection format (RDQS0100 or
     * RDQS0200) for the number of message text bytes to retrieve.
     * 
     * @return message text length requested
     */
    public int getMaximumMessageTextLengthRequested() {
        return getInt4Value(MAXIMUM_MESSAGE_TEXT_LENGTH_REQUESTED);
    }

    /**
     * The maximum message entry size (in bytes) specified when the data queue
     * was created. For a data queue created with SENDERID(*YES), this length is
     * the maximum entry size plus 36 bytes for the sender ID.
     * 
     * @return message text length available
     */
    public int getMaximumMessageTextLengthAvailable() {
        return getInt4Value(MAXIMUM_MESSAGE_TEXT_LENGTH_AVAILABLE);
    }

    public int getMaximumMessageLengthLoaded() {

        try {
            if (messages == null) {
                collectMessage();
            }
            return maximumMessageLength;
        } catch (UnsupportedEncodingException e) {
            return -1;
        }
    }

    /**
     * The offset at which the first message entry begins. If this value is 0,
     * there is no message available.
     * 
     * @return offset first message entry
     */
    public int getOffsetFirstMessageEntry() {
        return getInt4Value(OFFSET_TO_FIRST_MESSAGE_ENTRY);
    }

    /**
     * The library in which the data queue was found. This name is found by
     * searching the library list (*LIBL) or the current library (*CURLIB). If
     * the data queue is in a library other than your current library or library
     * list, it will not be found.
     * 
     * @return actual library name
     * @throws UnsupportedEncodingException
     */
    public String getActualLibraryName() throws UnsupportedEncodingException {
        return getCharValue(ACTUAL_DATA_QUEUE_LIBRARY_NAME);
    }

    /**
     * Returns the messages that have been retrieved.
     * 
     * @return messages retrieved
     * @throws UnsupportedEncodingException
     */
    public RDQM0200MessageEntry[] getMessages() {

        if (messages == null) {
            try {
                messages = collectMessage();
            } catch (Throwable e) {
                ISpherePlugin.logError("Failed to get data queue messages.", e); //$NON-NLS-1$
                messages = new RDQM0200MessageEntry[0];
            }
        }

        return messages;
    }

    /**
     * Specifies, whether or not the sender ID is included in the message data.
     * 
     * @return <code>true</code>, when the sender ID is included, else
     *         <code>false</code>.
     * @throws UnsupportedEncodingException
     */
    public boolean isSenderIDIncluded() {
        return isSenderIDIncluded;
    }

    /**
     * Retrieves the messages from the data returned by the API.
     * 
     * @return list of messages
     * @throws UnsupportedEncodingException
     */
    private RDQM0200MessageEntry[] collectMessage() throws UnsupportedEncodingException {

        maximumMessageLength = -1;

        List<RDQM0200MessageEntry> messages = new ArrayList<RDQM0200MessageEntry>();

        int count = numMessagesToRetrieve;
        int offset = getOffsetFirstMessageEntry();
        while (offset > 0 && count > 0) {

            RDQM0200MessageEntry messageEntry = new RDQM0200MessageEntry(getSystem(), offset, this);
            messages.add(messageEntry);

            if (messageEntry.getEnqueuedMesageEntryLength() > maximumMessageLength) {
                maximumMessageLength = messageEntry.getEnqueuedMesageEntryLength();
            }

            offset = messageEntry.getOffsetToNextMessageEntry();
            count --;
        }

        return messages.toArray(new RDQM0200MessageEntry[messages.size()]);
    }

    /**
     * Calculates the size of the message buffer for a given number of messages
     * and message length.
     * 
     * @param numMessages - number of messages
     * @param messageLength - message size
     * @param isSenderIDIncluded - specifies whether or not the sender ID is
     *        included in the message text
     * @return message buffer size
     */
    private static int getMessageBufferSize(int numMessages, int messageLength, boolean isSenderIDIncluded) {

        int bufferSize = (numMessages * (RDQM0200MessageEntry.LENGTH_OF_FIRST_FIELDS_OF_MESSSAGE_ENTRY + messageLength));
        bufferSize = bufferSize + GUESSED_LENGTH_OF_RESERVED_FIELDS;

        if (isSenderIDIncluded) {
            messageLength = messageLength + RDQM0200SenderID.LENGTH_OF_SENDER_ID;
        }

        return bufferSize;
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure(int messageBufferSize) {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 0);
        addInt4Field(NUMBER_OF_MESSAGES_RETURNED, 8);
        addInt4Field(NUMBER_OF_MESSAGES_AVAILABLE, 12);
        addInt4Field(MESSAGE_KEY_LENGTH_RETURNED, 16);
        addInt4Field(MESSAGE_KEY_LENGTH_AVAILABLE, 20);
        addInt4Field(MAXIMUM_MESSAGE_TEXT_LENGTH_REQUESTED, 24);
        addInt4Field(MAXIMUM_MESSAGE_TEXT_LENGTH_AVAILABLE, 28);
        addCharField(RESERVED_1, 32, 8);
        addInt4Field(OFFSET_TO_FIRST_MESSAGE_ENTRY, 40);
        addCharField(ACTUAL_DATA_QUEUE_LIBRARY_NAME, 44, 10);
        addCharField(MESSAGE_BUFFER, 54, messageBufferSize);
    }
}
