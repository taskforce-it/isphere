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

    private String connectionName;
    private String messageFile;
    private String library;
    private String format;

    /**
     * Constructs a IQMHRTVMResult object.
     * 
     * @param system - System that executed the retrieve request
     * @param connectionName - name of the RDi connection
     * @param messageFile - message file
     * @param library - message file library
     * @param bytes - returned by the IQMHRTVM API
     * @param format - format of the returned data ({@link IQMHRTVM#RTVM0300} or
     *        {@link IQMHRTVM#RTVM0400})
     * @throws UnsupportedEncodingException
     */
    public IQMHRTVMResult(AS400 system, String connectionName, String messageFile, String library, byte[] bytes, String format)
        throws UnsupportedEncodingException {
        super(system, "IQMHRTVMHeader");

        this.connectionName = connectionName;
        this.messageFile = messageFile;
        this.library = library;
        this.format = format;

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

        RTVM0300 rtvm0000 = null;
        if (IQMHRTVM.RTVM0400.equals(format)) {
            rtvm0000 = new RTVM0400(getSystem(), getBytes());
        } else if (IQMHRTVM.RTVM0300.equals(format)) {
            rtvm0000 = new RTVM0300(getSystem(), getBytes());
        } else {
            throw new IllegalArgumentException("Invalid format: " + format); //$NON-NLS-1$
        }

        int offset = getOffsetFirstMessage();

        for (int i = 0; i < getNumberOfMessagesReturned(); i++) {

            rtvm0000.setOffset(offset);
            messages.add(rtvm0000.createMessageDescription(connectionName, messageFile, library));

            offset += rtvm0000.getBytesReturned();
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
