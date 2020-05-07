/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.description;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import biz.isphere.core.internal.api.APIDateTimeFieldDescription;
import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;

/**
 * Format RDQD0100 of the <i>Retrieve Data Queue Description</i> (QMHQRDQD) API.
 * This format is used to retrieve the basic data queue description.
 * 
 * @author Thomas Raddatz
 */
public class RDQD0100 extends APIFormat {

    public static final String SEQUENCE_FIFO = "FIFO"; //$NON-NLS-1$
    public static final String SEQUENCE_KEYED = "KEYED"; //$NON-NLS-1$
    public static final String SEQUENCE_LIFO = "LIFO"; //$NON-NLS-1$

    public static final int SIZE_MAX16MB = -1;
    public static final int SIZE_MAX2GB = -2;

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String MESSAGE_LENGTH = "messageLength"; //$NON-NLS-1$
    private static final String KEY_LENGTH = "keyLength"; //$NON-NLS-1$
    private static final String SEQUENCE = "sequence"; //$NON-NLS-1$
    private static final String INCLUDE_SENDER_ID = "includeSenderID"; //$NON-NLS-1$
    private static final String FORCE_INDICATOR = "forceIndicator"; //$NON-NLS-1$
    private static final String TEXT_DESCRIPTION = "textDescription"; //$NON-NLS-1$
    private static final String TYPE_OF_DATA_QUEUE = "typeOfDataQueue"; //$NON-NLS-1$
    private static final String AUTOMATIC_RECLAIM = "automaticReclaim"; //$NON-NLS-1$
    private static final String ENFORCE_DATA_QUEUE_LOCK = "enforceDataQueueLock"; //$NON-NLS-1$
    private static final String NUMBER_OF_MESSAGES = "numberOfMessages"; //$NON-NLS-1$
    private static final String NUMBER_OF_ENTRIES_CURRENTLY_ALLOCATED = "numberOfEntriesCurrentlyAllocated"; //$NON-NLS-1$
    private static final String DATA_QUEUE_NAME_USED = "dataQueueNameUsed"; //$NON-NLS-1$
    private static final String DATA_QUEUE_LIBRARY_USED = "dataQueueLibraryUsed"; //$NON-NLS-1$
    private static final String MAXIMUM_NUMBER_OF_ENTRIES_ALLOWED = "maximumNumberOfEntriesAllowed"; //$NON-NLS-1$
    private static final String INITIAL_NUMBER_OF_ENTRIES = "initialNumberOfEntries"; //$NON-NLS-1$
    private static final String MAXIMUM_NUMBER_OF_ENTRIES_SPECIFIED = "maximumNumberOfEntriesSpecified"; //$NON-NLS-1$
    private static final String LAST_RECLAIM_DATE_AND_TIME = "lastReclaimDateAndTime"; //$NON-NLS-1$

    /**
     * Constructs a RDQD0100 object. This constructor is used to create the
     * object before calling the QMHQRDQD API. The data returned by the API is
     * set by {@link #setBytes(byte[])} afterwards.
     * 
     * @param system - system used to create the data converter
     * @throws UnsupportedEncodingException
     */
    public RDQD0100(AS400 system) throws UnsupportedEncodingException {
        this(system, null);
    }

    /**
     * Constructs a RDQD0100 object.
     * 
     * @param system - system used to create the data converter
     * @param bytes - data returned by the QMHQRDQD API
     * @throws UnsupportedEncodingException
     */
    public RDQD0100(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "RDQD0100"); //$NON-NLS-1$

        createStructure();
        setBytes(bytes);
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
     * The maximum length allowed for messages. The is the value that was
     * specified with the MAXLEN keyword on the CRTDTAQ command. This will be 0
     * for a DDM data queue.
     * 
     * @return message length
     */
    public int getMessageLength() {
        return getInt4Value(MESSAGE_LENGTH);
    }

    /**
     * If the specified data queue was created as a keyed type, this field
     * contains the length, in bytes, of the message reference key. Values range
     * from 1 to 256. If the specified queue is not a keyed queue or is a DDM
     * data queue, the value is 0.
     * 
     * @return key length
     */
    public int getKeyLength() {
        return getInt4Value(KEY_LENGTH);
    }

    /**
     * Returns the sequence in which messages can be removed from the queue.
     * <p>
     * Possible values returned are:
     * <ul>
     * <li>{@link #SEQUENCE_FIFO} - First-in first-out</li>
     * <li>{@link #SEQUENCE_KEYED} - Keyed</li>
     * <li>{@link #SEQUENCE_LIFO} - Last-in first-out</li>
     * </ul>
     * 
     * @return sequence (F, K or L)
     */
    public String getSequence() throws UnsupportedEncodingException {

        String sequence = getCharValue(SEQUENCE);

        if ("F".equals(sequence)) { //$NON-NLS-1$
            return SEQUENCE_FIFO;
        } else if ("K".equals(sequence)) { //$NON-NLS-1$
            return SEQUENCE_KEYED;
        } else if ("L".equals(sequence)) { //$NON-NLS-1$
            return SEQUENCE_LIFO;
        }

        return "*ERROR"; //$NON-NLS-1$
    }

    /**
     * If the queue was created to include the sender ID with sent messages.
     * <p>
     * Possible values returned are:
     * <ul>
     * <li><code>true</code> - The sender ID is included when data is sent to
     * the data queue.</li>
     * <li><code>false</code> - The sender ID is not included when data is sent
     * to the data queue.</li>
     * </ul>
     * This will be blank for a DDM data queue.
     * <p>
     * 
     * @return <code>true</code>, when the sender ID is included
     * @throws UnsupportedEncodingException
     */
    public boolean isSenderIDIncludedInMessageText() throws UnsupportedEncodingException {
        return "Y".equals(getCharValue(INCLUDE_SENDER_ID)); //$NON-NLS-1$
    }

    /**
     * Whether or not the data queue is forced to auxiliary storage when entries
     * are sent or received for the specified data queue.
     * <p>
     * Possible values returned are:
     * <ul>
     * <li><code>true</code> - The data queue is forced to auxiliary storage
     * after entries are sent or received.</li>
     * <li><code>false</code> - The data queue is not forced to auxiliary
     * storage after entries are sent or received.</li>
     * </ul>
     * This will be blank for a DDM data queue.
     * <p>
     * 
     * @return
     * @throws UnsupportedEncodingException
     */
    public boolean isForceToStorage() throws UnsupportedEncodingException {
        return "Y".equals(getCharValue(FORCE_INDICATOR));
    }

    /**
     * Whether or not the data queue is keyed.
     * 
     * @return <code>true</code> for a keyed data queue, else <code>false</code>
     */
    public boolean isKeyed() {
        return getKeyLength() > 0;
    }

    /**
     * The text description of the data queue. The field contains blanks if no
     * text description was specified when the data queue was created.
     * 
     * @return text description
     * @throws UnsupportedEncodingException
     */
    public String getTextDescription() throws UnsupportedEncodingException {
        return getCharValue(TEXT_DESCRIPTION);
    }

    /**
     * This will be set to one of the following values:
     * <p>
     * <ul>
     * <li><code>false</code> - The data queue is a standard data queue.</li>
     * <li><code>true</code> - The data queue is a DDM data queue.</li>
     * </ul>
     * 
     * @return <code>true</code>, for DDM data queues
     * @throws UnsupportedEncodingException
     */
    public boolean isDDMDataQueue() throws UnsupportedEncodingException {
        // 0 - The data queue is a standard data queue.
        // 1 - The data queue is a DDM data queue.
        return "1".equals(getCharValue(TYPE_OF_DATA_QUEUE)); //$NON-NLS-1$
    }

    /**
     * Whether or not the data queue has the amount of storage allocated for the
     * queue reclaimed when the queue is empty.
     * <p>
     * Possible values returned are:
     * <ul>
     * <li><code>false</code> - Storage is not reclaimed.</li>
     * <li><code>true</code> - Storage is reclaimed when the queue is empty. The
     * amount of storage allocated will be set to the initial number of entries.
     * </li>
     * </ul>
     * This will be blank for a DDM data queue.
     * <p>
     * 
     * @return <code>true</code>, if storage is reclaimed when the queue is
     *         empty
     * @throws UnsupportedEncodingException
     */
    public boolean isAutomaticReclaim() throws UnsupportedEncodingException {
        // 0 - Storage is not reclaimed.
        // 1 - Storage is reclaimed when the queue is empty.
        return "1".equals(getCharValue(AUTOMATIC_RECLAIM)); //$NON-NLS-1$
    }

    /**
     * Identifies whether or not IBM-supplied data queue operations will enforce
     * a lock on the data queue. This attribute cannot be specified on the
     * Create Data Queue (CRTDTAQ) CL Command. The default when a data queue is
     * created is for locks to be ignored. A data queue can be locked with the
     * Allocate Object (ALCOBJ) CL Command. When locks are enforced, performance
     * can be degraded due to the additional locking performed by all data queue
     * operations. *
     * <p>
     * Possible values returned are:
     * <ul>
     * <li><code>false</code> - Locks on the data queue are ignored by
     * IBM-supplied data queue operations.</li>
     * <li><code>true</code> - Locks on the data queue are enforced by
     * IBM-supplied data queue operations.</li>
     * </ul>
     * This will be blank for a DDM data queue.
     * <p>
     * 
     * @return <code>true</code>, if locks are enforced
     * @throws UnsupportedEncodingException
     */
    public boolean isEnforceDataQueueLock() throws UnsupportedEncodingException {
        // 0 - Locks on the data queue are ignored
        // 1 - Locks on the data queue are enforced
        return "1".equals(getCharValue(ENFORCE_DATA_QUEUE_LOCK)); //$NON-NLS-1$
    }

    /**
     * The number of messages currently on the data queue. This will be 0 for a
     * DDM data queue. *
     * 
     * @return number of messages
     * @throws UnsupportedEncodingException
     */
    public int getNumberOfMessages() {
        return getInt4Value(NUMBER_OF_MESSAGES);
    }

    /**
     * The number of entries that will fit into the data queue before it is
     * extended. When the queue is extended, additional storage is allocated for
     * the queue. The data queue can be extended until it reaches the value for
     * the maximum number of entries allowed. This will be 0 for a DDM data
     * queue.
     * 
     * @return number of allocated entries
     */
    public int getNumberOfEntriesCurrentlyAllocated() {
        return getInt4Value(NUMBER_OF_ENTRIES_CURRENTLY_ALLOCATED);
    }

    /**
     * The name of the data queue. This will be the same as the name specified
     * unless the data queue was renamed after this job first accessed the data
     * queue.
     * 
     * @return name of the data queue
     * @throws UnsupportedEncodingException
     */
    public String getDataQueueNameUsed() throws UnsupportedEncodingException {
        return getCharValue(DATA_QUEUE_NAME_USED);
    }

    /**
     * The library in which the data queue is found. If *LIBL or *CURLIB is
     * specified for the library name, this field is the actual name of the
     * library in which the data queue was found. If a specific library (not
     * *LIBL or *CURLIB) is specified, and the data queue is moved from that
     * library to a different library after this job first accessed the data
     * queue, this will be set to the name of the library in which the data
     * queue currently exists.
     * 
     * @return name of the library that contains the data queue
     * @throws UnsupportedEncodingException
     */
    public String getDataQueueLibraryUsed() throws UnsupportedEncodingException {
        return getCharValue(DATA_QUEUE_LIBRARY_USED);
    }

    /**
     * The maximum number of messages that will fit into the data queue when it
     * is full. This will be 0 for a DDM data queue.
     * 
     * @return number of entries allowed
     */
    public int getMaximumNumberOfEntriesAllowed() {
        return getInt4Value(MAXIMUM_NUMBER_OF_ENTRIES_ALLOWED);
    }

    /**
     * The number of messages that will fit into the storage allocated for the
     * data queue when it is created or when it is automatically reclaimed. This
     * will be 0 for a DDM data queue.
     * 
     * @return initial number of entries
     */
    public int getInitialNumberOfEntries() {
        return getInt4Value(INITIAL_NUMBER_OF_ENTRIES);
    }

    /**
     * The maximum number of messages that was specified on the SIZE keyword of
     * the CRTDTAQ command. This will be 0 for a DDM data queue. This will be
     * set to -1 for data queues created prior to release V4R5M0, when support
     * for the SIZE keyword was added to the CRTDTAQ command.
     * <p>
     * The number of entries specified or one of the following special values
     * will be returned:
     * <ul>
     * <li>{@link #SIZE_MAX16MB} - *MAX16MB was specified for the data queue
     * size.</li>
     * <li>{@link #SIZE_MAX2GB} - *MAX2GB was specified for the data queue size.
     * </li>
     * </ul>
     * 
     * @return maximum number of entries
     * @throws UnsupportedEncodingException
     */
    public int getMaximumNumberOfEntriesSpecified() {
        return getInt4Value(MAXIMUM_NUMBER_OF_ENTRIES_SPECIFIED);
    }

    /**
     * The date and time that the last automatic reclaim was done. Its format is
     * a system time stamp (*DTS). The Convert Date and Time Format (QWCCVTDT)
     * API can be used to convert this time stamp to a character format. This
     * will be hex zeroes for a DDM data queue or when no reclaim has occurred
     * for a standard data queue.
     * 
     * @return date and time the last automatic reclaim was done
     * @throws AS400SecurityException
     * @throws ErrorCompletingRequestException
     * @throws InterruptedException
     * @throws IOException
     * @throws ObjectDoesNotExistException
     */
    public Date getLastReclaimDateAndTime() throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException,
        ObjectDoesNotExistException {
        return getDateTimeValue(LAST_RECLAIM_DATE_AND_TIME);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(MESSAGE_LENGTH, 8);
        addInt4Field(KEY_LENGTH, 12);
        addCharField(SEQUENCE, 16, 1);
        addCharField(INCLUDE_SENDER_ID, 17, 1);
        addCharField(FORCE_INDICATOR, 18, 1);
        addCharField(TEXT_DESCRIPTION, 19, 50);
        addCharField(TYPE_OF_DATA_QUEUE, 69, 1);
        addCharField(AUTOMATIC_RECLAIM, 70, 1);
        addCharField(ENFORCE_DATA_QUEUE_LOCK, 71, 1);
        addInt4Field(NUMBER_OF_MESSAGES, 72);
        addInt4Field(NUMBER_OF_ENTRIES_CURRENTLY_ALLOCATED, 76);
        addCharField(DATA_QUEUE_NAME_USED, 80, 10);
        addCharField(DATA_QUEUE_LIBRARY_USED, 90, 10);
        addInt4Field(MAXIMUM_NUMBER_OF_ENTRIES_ALLOWED, 100);
        addInt4Field(INITIAL_NUMBER_OF_ENTRIES, 104);
        addInt4Field(MAXIMUM_NUMBER_OF_ENTRIES_SPECIFIED, 108);
        addDateTimeField(LAST_RECLAIM_DATE_AND_TIME, 112, 8, APIDateTimeFieldDescription.DTS);
    }
}
