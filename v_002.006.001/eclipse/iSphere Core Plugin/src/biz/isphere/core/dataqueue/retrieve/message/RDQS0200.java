/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.message;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

import com.ibm.as400.access.AS400;

/**
 * Format RDQS0200 of the <i>Retrieve Data Queue Message</i> (QMHRDQM) API. This
 * format is used to specify the selection criteria when retrieving messages
 * from keyed data queues.
 * 
 * @author Thomas Raddatz
 */
public class RDQS0200 extends RDQS0100 {

    /**
     * All messages are to be returned in the order based on the type of data
     * queue. FIFO queues are returned in FIFO order, LIFO queues are returned
     * in LIFO order and keyed queues are returned in ascending key order.
     */
    public static final String SELECT_KEYED = "K"; //$NON-NLS-1$

    /**
     * All messages with a key greater than that specified in the key field are
     * to be returned.
     */
    public static final String ORDER_GT = "GT"; //$NON-NLS-1$

    /**
     * All messages with a key less than that specified in the key field are to
     * be returned.
     */
    public static final String ORDER_LT = "LT"; //$NON-NLS-1$

    /**
     * All messages with a key not equal to that specified in the key field are
     * to be returned.
     */
    public static final String ORDER_NE = "NE"; //$NON-NLS-1$

    /**
     * All messages with a key equal to that specified in the key field are to
     * be returned.
     */
    public static final String ORDER_EQ = "EQ"; //$NON-NLS-1$

    /**
     * All messages with a key greater than or equal to that specified in the
     * key field are to be returned.
     */
    public static final String ORDER_GE = "GE"; //$NON-NLS-1$

    /**
     * All messages with a key less than or equal to that specified in the key
     * field are to be returned.
     */
    public static final String ORDER_LE = "LE"; //$NON-NLS-1$

    private static final String KEY_SEARCH_ORDER = "keySearchOrder"; //$NON-NLS-1$
    private static final String NUMBER_OF_MESSAGE_KEY_BYTES_TO_RETRIEVE = "numberOfMessageKeyBytesToRetrieve"; //$NON-NLS-1$
    private static final String LENGTH_OF_KEY = "lengthOfKey"; //$NON-NLS-1$
    private static final String KEY = "key"; //$NON-NLS-1$

    /**
     * Constructs a RDQS0200 object for a given system.
     * 
     * @param system - system used to create the data converter
     * @param keyLength - length of key
     * @throws CharConversionException
     * @throws UnsupportedEncodingException
     */
    public RDQS0200(AS400 system, String key) throws CharConversionException, UnsupportedEncodingException {
        this(system, key, 20, 100);
    }

    /**
     * Constructs a RDQS0200 object for a given system.
     * 
     * @param system - system used to create the data converter
     * @param keyLength - length of key
     * @param keyLengthToRetrieve - number of message key bytes to retrieve
     * @param messageLengthToRetrieve - number of message text bytes to retrieve
     * @throws CharConversionException
     * @throws UnsupportedEncodingException
     */
    public RDQS0200(AS400 system, String key, int keyLengthToRetrieve, int messageLengthToRetrieve) throws CharConversionException,
        UnsupportedEncodingException {
        super(system, "RDQS0200"); //$NON-NLS-1$

        createStructure();

        setCharValue(SELECTION_TYPE, SELECT_KEYED);
        setInt4Value(NUMBER_OF_MESSAGE_TEXT_BYTES_TO_RETRIEVE, messageLengthToRetrieve);
        setInt4Value(NUMBER_OF_MESSAGE_KEY_BYTES_TO_RETRIEVE, keyLengthToRetrieve);
        setInt4Value(LENGTH_OF_KEY, key.length());
        setCharValue(KEY, key);
    }

    /**
     * Sets the relational operator specifying the comparison criteria between
     * the message key specified in the RDQS0200 format and the actual keys of
     * messages in the data queue.
     * 
     * @param keySearchOrder - key search order. Must be one of the ORDER_*
     *        constants
     * @throws CharConversionException
     * @throws UnsupportedEncodingException
     */
    public void setKeySearchOrder(String keySearchOrder) throws CharConversionException, UnsupportedEncodingException {
        setCharValue(KEY_SEARCH_ORDER, keySearchOrder);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addCharField(SELECTION_TYPE, 0, 1);
        addCharField(KEY_SEARCH_ORDER, 1, 2);
        addCharField(RESERVED_1, 3, 1);
        addInt4Field(NUMBER_OF_MESSAGE_TEXT_BYTES_TO_RETRIEVE, 4);
        addInt4Field(NUMBER_OF_MESSAGE_KEY_BYTES_TO_RETRIEVE, 8);
        addInt4Field(LENGTH_OF_KEY, 12);
        addCharField(KEY, 16, 256);
    }
}
