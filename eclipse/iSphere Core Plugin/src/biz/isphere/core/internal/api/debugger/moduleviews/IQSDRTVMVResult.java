/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.debugger.moduleviews;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import com.ibm.as400.access.AS400;

import biz.isphere.core.internal.api.APIFormat;

/**
 * Class to hold the result of the IQSDRTVMV API.
 * 
 * @author Thomas Raddatz
 */
public class IQSDRTVMVResult extends APIFormat {

    private static final String BYTES_RETURNED = "bytesReturned"; //$NON-NLS-1$
    private static final String BYTES_AVAILABLE = "bytesAvailable"; //$NON-NLS-1$
    private static final String NUMBER_OF_VIEWS_RETURNED = "numberOfViewsReturned"; //$NON-NLS-1$
    private static final String LENGTH_OF_VIEW_ENTRY = "lengthOfViewEntry"; //$NON-NLS-1$
    private static final String OFFSET_FIRST_VIEW = "offsetFirstView"; //$NON-NLS-1$

    private String connectionName;
    private String format;

    private String object;
    private String library;
    private String objectType;

    /**
     * Constructs a IQSDRTVMVResult object.
     * 
     * @param system - System that executed the retrieve request
     * @param connectionName - name of the RDi connection
     * @param messageFile - message file
     * @param library - message file library
     * @param bytes - returned by the IQSDRTVMV API
     * @param format - format of the returned data ({@link IQSDRTVMV#VEWL0100})
     * @throws UnsupportedEncodingException
     */
    public IQSDRTVMVResult(AS400 system, String connectionName, byte[] bytes, String format) throws UnsupportedEncodingException {
        super(system, "IQSDRTVMVHeader");

        this.connectionName = connectionName;
        this.format = format;

        createStructure();

        setBytes(bytes);
    }

    public String getFormat() {
        return format;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    /**
     * Returns the number of bytes returned by the IQSDRTVMV API.
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
     * Returns the number of views returned by the IQSDRTVMV API.
     * 
     * @return number of messages returned
     */
    public int getNumberOfViewsReturned() {
        return getInt4Value(NUMBER_OF_VIEWS_RETURNED);
    }

    /**
     * Returns the length of a view entry.
     * 
     * @return length of view entry
     */
    public int getLengthOfViewEntry() {
        return getInt4Value(LENGTH_OF_VIEW_ENTRY);
    }

    /**
     * Returns the message descriptions retrieved by the IQSDRTVMV API.
     * 
     * @return list of message descriptions
     * @throws UnsupportedEncodingException
     */
    public List<DebuggerView> getViews() throws UnsupportedEncodingException {

        List<DebuggerView> debuggerViews = new LinkedList<DebuggerView>();

        SDMV0100 sdmv0100 = null;
        if (IQSDRTVMV.SDMV0100.equals(format)) {
            sdmv0100 = new SDMV0100(getSystem(), getBytes());
        } else {
            throw new IllegalArgumentException("Invalid format: " + format); //$NON-NLS-1$
        }

        int offset = getOffsetFirstView();

        for (int i = 0; i < getNumberOfViewsReturned(); i++) {

            sdmv0100.setOffset(offset);
            debuggerViews.add(sdmv0100.createDebuggerView(connectionName, getObject(), getLibrary(), getObjectType()));

            offset += sdmv0100.getLength();
        }

        return debuggerViews;
    }

    /**
     * Returns the offset to the first view.
     * 
     * @return offset of first view
     */
    private int getOffsetFirstView() {
        return getInt4Value(OFFSET_FIRST_VIEW);
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addInt4Field(BYTES_RETURNED, 0);
        addInt4Field(BYTES_AVAILABLE, 4);
        addInt4Field(NUMBER_OF_VIEWS_RETURNED, 8);
        addInt4Field(LENGTH_OF_VIEW_ENTRY, 12);
        addInt4Field(OFFSET_FIRST_VIEW, 16);
    }
}
