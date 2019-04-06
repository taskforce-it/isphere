/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.api.retrievefielddescription;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.journalexplorer.core.model.MetaColumn;

import com.ibm.as400.access.AS400;

/**
 * Class to hold the result of the IQDBRTVFD API.
 * 
 * @author Thomas Raddatz
 */
public class IQDBRTVFDResult extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String NUMBER_OF_FIELDS_RETURNED = "numberOfFieldsReturned"; //$NON-NLS-1$
    private static final String FILE_RETURNED = "fileReturned"; //$NON-NLS-1$
    private static final String LIBRARY_RETURNED = "libraryReturned"; //$NON-NLS-1$
    private static final String OFFSET_TO_FIRST_FIELD = "offsetToFirstField"; //$NON-NLS-1$

    private String format;

    /**
     * Constructs a IQDBRTVFDResult object.
     * 
     * @param system - System that executes the retrieve request
     * @param bytes - returned by the IQDBRTVFD API
     * @param format - format of the returned data ({@link IQDBRTVFD#RVFD0100})
     * @throws UnsupportedEncodingException
     */
    public IQDBRTVFDResult(AS400 system, byte[] bytes, String format) throws UnsupportedEncodingException {
        super(system, "IQDBRTVFDHeader"); //$NON-NLS-1$

        this.format = format;

        createStructure();

        setBytes(bytes);
    }

    /**
     * Returns the number of bytes returned by the IQDBRTVFD API.
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
     * Returns the number of field descriptions returned by the IQDBRTVFD API.
     * 
     * @return number of field descriptions returned
     */
    public int getNumberOfFieldsReturned() {
        return getInt4Value(NUMBER_OF_FIELDS_RETURNED);
    }

    /**
     * Returns the name of the actual file returned by the IQDBRTVFD API.
     * 
     * @return file name
     * @throws UnsupportedEncodingException
     */
    public String getFileReturned() throws UnsupportedEncodingException {
        return getCharValue(FILE_RETURNED);
    }

    /**
     * Returns the name of the library that contains the actual file.
     * 
     * @return library name
     * @throws UnsupportedEncodingException
     */
    public String getLibraryReturned() throws UnsupportedEncodingException {
        return getCharValue(LIBRARY_RETURNED);
    }

    /**
     * Returns the field descriptions retrieved by the IQDBRTVFD API.
     * 
     * @return list of field descriptions
     * @throws UnsupportedEncodingException
     */
    public List<MetaColumn> getFieldDescriptions() throws Exception {

        List<MetaColumn> metaColumns = new ArrayList<MetaColumn>();

        RVFD0100 rtvm0100 = null;
        if (IQDBRTVFD.RVFD0100.equals(format)) {
            rtvm0100 = new RVFD0100(getSystem(), getBytes());
        } else {
            throw new IllegalArgumentException("Invalid format: " + format); //$NON-NLS-1$
        }

        int offset = getOffsetFirstField();

        for (int i = 0; i < getNumberOfFieldsReturned(); i++) {

            rtvm0100.setOffset(offset);
            metaColumns.add(new MetaColumn(i, rtvm0100));

            offset += rtvm0100.getLength();
        }

        return metaColumns;
    }

    /**
     * Returns the offset to the first field description.
     * 
     * @return offset of first field description
     */
    private int getOffsetFirstField() {
        return getInt4Value(OFFSET_TO_FIRST_FIELD);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(NUMBER_OF_FIELDS_RETURNED, 8);
        addCharField(FILE_RETURNED, 12, 10);
        addCharField(LIBRARY_RETURNED, 22, 10);
        addInt4Field(OFFSET_TO_FIRST_FIELD, 32);
    }
}
