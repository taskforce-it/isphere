/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.listjoblog;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class OpenListInformation extends APIFormat {

    public static String INFORMATION_COMPLETE = "C"; //$NON-NLS-1$
    public static String INFORMATION_INCOMPLETE = "I"; //$NON-NLS-1$
    public static String INFORMATION_PARTIAL = "P"; //$NON-NLS-1$

    public static String STATUS_PENDING = "0"; //$NON-NLS-1$
    public static String STATUS_BUILDING = "1"; //$NON-NLS-1$
    public static String STATUS_COMPLETE = "2"; //$NON-NLS-1$
    public static String STATUS_ERROR = "3"; //$NON-NLS-1$
    public static String STATUS_PRIMED = "4"; //$NON-NLS-1$
    public static String STATUS_DATA_OVERFLOW = "5"; //$NON-NLS-1$

    private static final String TOTAL_RECORDS = "totalRecords"; //$NON-NLS-1$
    private static final String RECORDS_RETURNED = "recordsReturned"; //$NON-NLS-1$
    private static final String REQUEST_HANDLE = "requestHandle"; //$NON-NLS-1$
    private static final String RECORD_LENGTH = "recordLength"; //$NON-NLS-1$
    private static final String INFORMATION_COMPLETE_INDICATOR = "informationCompleteIndicator"; //$NON-NLS-1$
    private static final String DATE_AND_TIME_CREATED = "dateAndTimeCreated"; //$NON-NLS-1$
    private static final String LIST_STATUS_INDICATOR = "listStatusIndicator"; //$NON-NLS-1$
    private static final String RESERVED_1 = "reserved_1"; //$NON-NLS-1$
    private static final String LENGTH_OF_INFORMATION_RETURNED = "lengthOfInformationReturned"; //$NON-NLS-1$
    private static final String FIRST_RECORD_IN_RECEIVER_VARIABLE = "firstRecordInReceiverVariable"; //$NON-NLS-1$
    private static final String RESERVED_2 = "reserved_2"; //$NON-NLS-1$

    public OpenListInformation(AS400 system) throws CharConversionException, UnsupportedEncodingException {
        super(system, "OpenListInformation"); //$NON-NLS-1$

        createStructure();
    }

    public int getTotalRecords() {
        return getInt4Value(TOTAL_RECORDS);
    }

    public int getRecordsReturned() {
        return getInt4Value(RECORDS_RETURNED);
    }

    public String getRequestHandle() throws UnsupportedEncodingException {
        return getCharValue(REQUEST_HANDLE);
    }

    public int getRecordLength() {
        return getInt4Value(RECORD_LENGTH);
    }

    public String getInformationCompleteIndicator() throws UnsupportedEncodingException {
        return getCharValue(INFORMATION_COMPLETE_INDICATOR);
    }

    public String getDateAndTimeCreated() throws UnsupportedEncodingException {
        return getCharValue(DATE_AND_TIME_CREATED);
    }

    public String getListStatusIndicator() throws UnsupportedEncodingException {
        return getCharValue(LIST_STATUS_INDICATOR);
    }

    public int getLengthOfInformationReturned() {
        return getInt4Value(LENGTH_OF_INFORMATION_RETURNED);
    }

    public int getFirstRecordInReceiverVariable() {
        return getInt4Value(FIRST_RECORD_IN_RECEIVER_VARIABLE);
    }

    /**
     * Creates the PRDI0100 structure.
     */
    private void createStructure() {

        addInt4Field(TOTAL_RECORDS, 0);
        addInt4Field(RECORDS_RETURNED, 4);
        addCharField(REQUEST_HANDLE, 8, 4);
        addInt4Field(RECORD_LENGTH, 12);
        addCharField(INFORMATION_COMPLETE_INDICATOR, 16, 1);
        addCharField(DATE_AND_TIME_CREATED, 17, 13);
        addCharField(LIST_STATUS_INDICATOR, 30, 1);
        addCharField(RESERVED_1, 31, 1);
        addInt4Field(LENGTH_OF_INFORMATION_RETURNED, 32);
        addInt4Field(FIRST_RECORD_IN_RECEIVER_VARIABLE, 36);
        addCharField(RESERVED_2, 40, 40);
    }
}
