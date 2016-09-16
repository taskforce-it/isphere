/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.description;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

/**
 * Format RDQD0200 of the <i>Retrieve Data Queue Description</i> (QMHQRDQD) API.
 * This format is used to retrieve remote attributes of a given data queue.
 * 
 * @author Thomas Raddatz
 */
public class RDQD0200 extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String APPC_DEVICE_DESCRIPTION = "APPCDeviceDescription"; //$NON-NLS-1$
    private static final String MODE = "mode"; //$NON-NLS-1$
    private static final String REMOTE_LOCATION_NAME = "remoteLocationName"; //$NON-NLS-1$
    private static final String LOCAL_LOCATION_NAME = "localLocationName"; //$NON-NLS-1$
    private static final String REMOTE_NETWORK_IDENTIFIER = "remoteNetworkIdentifier"; //$NON-NLS-1$
    private static final String REMOTE_DATA_QUEUE_NAME = "remoteDataQueueName"; //$NON-NLS-1$
    private static final String REMOTE_DATA_QUEUE_LIBRARY_NAME = "remoteDataQueueLibraryName"; //$NON-NLS-1$
    private static final String DATA_QUEUE_NAME_USED = "dataQueueNameUsed"; //$NON-NLS-1$
    private static final String DATA_QUEUE_LIBRARY_USED = "dataQueueLibraryUsed"; //$NON-NLS-1$
    private static final String RELATIONAL_DATABASE_NAME = "relationalDatabaseName"; //$NON-NLS-1$

    /**
     * Constructs a RDQD0100 object. This constructor is used to create the
     * object before calling the QMHQRDQD API. The data returned by the API is
     * set by {@link #setBytes(byte[])} afterwards.
     * 
     * @param system - system used to create the data converter
     * @throws UnsupportedEncodingException
     */
    public RDQD0200(AS400 system) throws UnsupportedEncodingException {
        this(system, null);
    }

    /**
     * Constructs a RDQD0100 object.
     * 
     * @param system - system used to create the data converter
     * @param bytes - data returned by the QMHQRDQD API
     * @throws UnsupportedEncodingException
     */
    public RDQD0200(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "RDQD0200"); //$NON-NLS-1$

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
     * The name of the APPC device description on the source system that is used
     * with this DDM data queue. The special value *LOC can be returned. This is
     * the name that was specified on the DEV parameter of the CRTDTAQ command.
     * This will be blank for RDB type DDM data queues.
     * 
     * @return APPC device description
     * @throws UnsupportedEncodingException
     */
    public String getAppcDeviceDescription() throws UnsupportedEncodingException {
        return getCharValue(APPC_DEVICE_DESCRIPTION);
    }

    /**
     * The mode name used with the remote location name to communicate with the
     * target system. The special value *NETATR can be returned. This is the
     * name that was specified on the MODE parameter of the CRTDTAQ command.
     * This will be blank for RDB type DDM data queues.
     * 
     * @return mode
     * @throws UnsupportedEncodingException
     */
    public String getMode() throws UnsupportedEncodingException {
        return getCharValue(MODE);
    }

    /**
     * The name of the remote location that is used with this object. This is
     * the name that was specified on the RMTLOCNAME parameter of the CRTDTAQ
     * command. A special value of *RDB indicates that the remote location
     * information from the relational database entry returned in the relational
     * database entry name field is used to determine the remote system.
     * 
     * @return remote location name
     */
    public String getRemoteLocationName() throws UnsupportedEncodingException {
        return getCharValue(REMOTE_LOCATION_NAME);
    }

    /**
     * The name of the local location. The special values *LOC and *NETATR can
     * be returned. This is the name that was specified on the LCLLOCNAME
     * parameter of the CRTDTAQ command. This will be blank for RDB type DDM
     * data queues.
     * 
     * @return local location name
     * @throws UnsupportedEncodingException
     */
    public String getLocalLocationName() throws UnsupportedEncodingException {
        return getCharValue(LOCAL_LOCATION_NAME);
    }

    /**
     * The remote network identifier in which the remote location used to
     * communcate with the target system. The special values *LOC, *NETATR, and
     * *NONE can be returned. This is the name that was specified on the
     * RMTNETID parameter of the CRTDTAQ command. This will be blank for RDB
     * type DDM data queues.
     * 
     * @return remote network identifier
     * @throws UnsupportedEncodingException
     */
    public String getRemoteNetworkIdentifier() throws UnsupportedEncodingException {
        return getCharValue(REMOTE_NETWORK_IDENTIFIER);
    }

    /**
     * The name of the remote data queue on the target system. This is the data
     * queue name that was specified on the RMTDTAQ parameter of the CRTDTAQ
     * command.
     * 
     * @return remote data queue name
     * @throws UnsupportedEncodingException
     */
    public String getRemoteDataQueueName() throws UnsupportedEncodingException {
        return getCharValue(REMOTE_DATA_QUEUE_NAME);
    }

    /**
     * The name of the library for the remote data queue on the target system.
     * The special values *LIBL and *CURLIB can be returned. This is the data
     * queue name that was specified on the RMTDTAQ parameter of the CRTDTAQ
     * command.
     * 
     * @return remote data queue library name
     * @throws UnsupportedEncodingException
     */
    public String getRemoteDataQueueLibraryName() throws UnsupportedEncodingException {
        return getCharValue(REMOTE_DATA_QUEUE_LIBRARY_NAME);
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
     * The name of the relational database entry that identifies the target
     * system or target ASP group. This field will be set to blanks unless the
     * data queue is an RDB type DDM data queue. This is the name that was
     * specified on the RDB parameter of the CRTDTAQ command.
     * 
     * @return relational database name
     * @throws UnsupportedEncodingException
     */
    public String getRelationalDatabaseName() throws UnsupportedEncodingException {
        return getCharValue(RELATIONAL_DATABASE_NAME);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addCharField(APPC_DEVICE_DESCRIPTION, 8, 10);
        addCharField(MODE, 18, 8);
        addCharField(REMOTE_LOCATION_NAME, 26, 8);
        addCharField(LOCAL_LOCATION_NAME, 34, 8);
        addCharField(REMOTE_NETWORK_IDENTIFIER, 42, 8);
        addCharField(REMOTE_DATA_QUEUE_NAME, 50, 10);
        addCharField(REMOTE_DATA_QUEUE_LIBRARY_NAME, 60, 10);
        addCharField(DATA_QUEUE_NAME_USED, 70, 10);
        addCharField(DATA_QUEUE_LIBRARY_USED, 80, 10);
        addCharField(RELATIONAL_DATABASE_NAME, 90, 18);
    }
}
