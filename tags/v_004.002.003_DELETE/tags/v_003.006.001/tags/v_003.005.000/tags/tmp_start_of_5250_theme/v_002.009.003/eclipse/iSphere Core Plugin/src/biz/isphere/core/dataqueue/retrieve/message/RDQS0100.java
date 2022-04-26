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

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

/**
 * Format RDQS0100 of the <i>Retrieve Data Queue Message</i> (QMHRDQM) API. This
 * format is used to specify the selection criteria.
 * 
 * @author Thomas Raddatz
 */
public class RDQS0100 extends APIFormat {

    /**
     * All messages are to be returned in the order based on the type of data
     * queue. FIFO queues are returned in FIFO order, LIFO queues are returned
     * in LIFO order and keyed queues are returned in ascending key order.
     */
    public static final String SELECT_ALL = "A"; //$NON-NLS-1$

    /**
     * The first message is to be returned.
     */
    public static final String SELECT_FIRST = "F"; //$NON-NLS-1$

    /**
     * The last message is to be returned.
     */
    public static final String SELECT_LAST = "L"; //$NON-NLS-1$

    /**
     * All messages are to be returned in reverse order of the type of data
     * queue. For example, LIFO queues are returned in FIFO order.
     */
    public static final String SELECT_REVERSE = "R"; //$NON-NLS-1$

    protected static final String SELECTION_TYPE = "selectionType"; //$NON-NLS-1$
    protected static final String RESERVED_1 = "reserved_1"; //$NON-NLS-1$
    protected static final String NUMBER_OF_MESSAGE_TEXT_BYTES_TO_RETRIEVE = "numberOfMessageTextBytesToRetrieve"; //$NON-NLS-1$

    /**
     * Constructs a RDQS0100 object for a given system.
     * 
     * @param system - system used to create the data converter
     * @throws CharConversionException
     * @throws UnsupportedEncodingException
     */
    public RDQS0100(AS400 system) throws CharConversionException, UnsupportedEncodingException {
        this(system, SELECT_ALL, 100);
    }

    /**
     * Constructs a RDQS0100 object for a given system.
     * <p>
     * Selection type depends on the format used. For the RDQS0100 format, valid
     * values are:
     * <ul>
     * <li>{@link #SELECT_ALL} - All messages are to be returned in the order
     * based on the type of data queue. FIFO queues are returned in FIFO order,
     * LIFO queues are returned in LIFO order and keyed queues are returned in
     * ascending key order.</li>
     * <li>{@link #SELECT_FIRST} - The first message is to be returned</li>
     * <li>{@link #SELECT_LAST} - The last message is to be returned</li>
     * <li>{@link #SELECT_REVERSE} - All messages are to be returned in reverse
     * order of the type of data queue. For example, LIFO queues are returned in
     * FIFO order.</li>
     * </ul>
     * 
     * @param system - system used to create the data converter
     * @param selectionType - selection type
     * @param messageLengthToRetrieve - number of message text bytes to retrieve
     * @throws CharConversionException
     * @throws UnsupportedEncodingException
     */
    public RDQS0100(AS400 system, String selectionType, int messageLengthToRetrieve) throws CharConversionException, UnsupportedEncodingException {
        this(system, "RDQS0100"); //$NON-NLS-1$

        createStructure();

        setCharValue(SELECTION_TYPE, selectionType);
        setInt4Value(NUMBER_OF_MESSAGE_TEXT_BYTES_TO_RETRIEVE, messageLengthToRetrieve);
    }

    protected RDQS0100(AS400 system, String formatName) throws UnsupportedEncodingException {
        super(system, formatName);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addCharField(SELECTION_TYPE, 0, 1);
        addCharField(RESERVED_1, 1, 3);
        addInt4Field(NUMBER_OF_MESSAGE_TEXT_BYTES_TO_RETRIEVE, 4);
    }
}
