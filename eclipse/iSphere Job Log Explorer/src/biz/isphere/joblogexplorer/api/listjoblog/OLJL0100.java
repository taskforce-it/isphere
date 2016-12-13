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
import java.text.SimpleDateFormat;
import java.util.Date;

import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class OLJL0100 extends APIFormat {

    private static final String OFFSET_TO_NEXT_ENTRY = "offsetToNextEntry"; //$NON-NLS-1$
    private static final String OFFSET_TO_FIELDS_RETURNED = "offsetToFieldsReturned"; //$NON-NLS-1$
    private static final String NUMBER_OF_FIELDS_RETURNED = "numberOfFieldsReturned"; //$NON-NLS-1$
    private static final String MESSAGE_SVERITY = "messageSeverity"; //$NON-NLS-1$
    private static final String MESSAGE_IDENTIFIER = "messageIdentifier"; //$NON-NLS-1$
    private static final String MESSAGE_TYPE = "messageType"; //$NON-NLS-1$
    private static final String MESSAGE_KEY = "messageKey"; //$NON-NLS-1$
    private static final String MESSAGE_FILE_NAME = "messageFileName"; //$NON-NLS-1$
    private static final String MESSAGE_FILE_LIBRARY_SPECIFIED_AT_SEND_TIME = "messageFileLibrarySpecifiedAtSendTime"; //$NON-NLS-1$
    private static final String DATE_SENT = "dateSent"; //$NON-NLS-1$
    private static final String TIME_SENT = "timeSent"; //$NON-NLS-1$
    private static final String MICROSECONDS = "microSeconds"; //$NON-NLS-1$
    private static final String THREAD_ID = "threadId"; //$NON-NLS-1$

    private SimpleDateFormat timeFormatter;
    private SimpleDateFormat dateFormatter;

    public OLJL0100(AS400 system) {
        super(system, "OLJL0100");

        // this.timeFormatter = Preferences.getInstance().getTimeFormatter();
        // this.dateFormatter = Preferences.getInstance().getDateFormatter();

        this.timeFormatter = new SimpleDateFormat("HH:mm:ss");
        this.dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

        createStructure();
    }

    public OLJL0100_Field getField(int key) {

        if (getNumberOfFieldsReturned() == 0) {
            return null;
        }

        try {

            OLJL0100_Field field = new OLJL0100_Field(getSystem());
            field.setBytes(getBytes());

            int offset = getOffsetToFieldsReturned();

            for (int i = 0; i < getNumberOfFieldsReturned(); i++) {
                field.setOffset(offset);
                if (field.getIdentifierField() == key) {
                    return field;
                }
                offset = offset + field.getLengthOfFieldInformation();
            }

            return null;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not retrieve field from job log entry ***", e); //$NON-NLS-1$
            return null;
        }

    }

    public int getOffsetToNextEntry() {
        return getInt4Value(OFFSET_TO_NEXT_ENTRY);
    }

    public int getOffsetToFieldsReturned() {
        return getInt4Value(OFFSET_TO_FIELDS_RETURNED);
    }

    public int getNumberOfFieldsReturned() {
        return getInt4Value(NUMBER_OF_FIELDS_RETURNED);
    }

    public String getMessageSeverity() {

        String severity = Integer.toString(getInt4Value(MESSAGE_SVERITY));
        if ("0".equals(severity)) { //$NON-NLS-1$
            severity = "00"; //$NON-NLS-1$
        }

        return severity;
    }

    public String getMessageId() throws UnsupportedEncodingException {
        return getCharValue(MESSAGE_IDENTIFIER);
    }

    public String getMessageType() throws UnsupportedEncodingException {

        String type = getCharValue(MESSAGE_TYPE);

        if ("01".equals(type)) {
            return "Completion";
        } else if ("02".equals(type)) {
            return "Diagnostic";
        } else if ("04".equals(type)) {
            return "Informational";
        } else if ("05".equals(type)) {
            return "Inquery";
        } else if ("06".equals(type)) {
            return "Copy";
        } else if ("08".equals(type)) {
            return "Request";
        } else if ("10".equals(type)) {
            return "Request";
        } else if ("14".equals(type)) {
            return "Notify";
        } else if ("15".equals(type)) {
            return "Escape";
        } else if ("16".equals(type)) {
            return "Notify";
        } else if ("17".equals(type)) {
            return "Escape";
        } else if ("21".equals(type)) {
            return "Reply";
        } else if ("22".equals(type)) {
            return "Reply";
        } else if ("23".equals(type)) {
            return "Reply";
        } else if ("24".equals(type)) {
            return "Reply";
        } else if ("25".equals(type)) {
            return "Reply";
        } else if ("26".equals(type)) {
            return "Reply";
        } else {
            return "*Unknown";
        }
    }

    public String getMessageKey() throws UnsupportedEncodingException {
        return getCharValue(MESSAGE_KEY);
    }

    public String getMessageFileName() throws UnsupportedEncodingException {
        return getCharValue(MESSAGE_FILE_NAME);
    }

    public String getMessageFileLibrarySpecifiedAtSendTime() throws UnsupportedEncodingException {
        return getCharValue(MESSAGE_FILE_LIBRARY_SPECIFIED_AT_SEND_TIME);
    }

    public String getDateSent() throws UnsupportedEncodingException {
        return getFormattedDate();
    }

    public String getTimeSent() throws UnsupportedEncodingException {
        return getFormattedTime();
    }

    public String getMicroseconds() throws UnsupportedEncodingException {
        return getCharValue(MICROSECONDS);
    }

    public String getThreadId() throws UnsupportedEncodingException {
        return getCharValue(THREAD_ID);
    }

    public String getFieldData(int key) throws UnsupportedEncodingException {

        OLJL0100_Field field = field = getField(key);
        if (field == null) {
            return ""; //$NON-NLS-1$
        }

        return field.getCharData();
    }

    /**
     * Creates the PRDI0100 structure.
     */
    private void createStructure() {

        addInt4Field(OFFSET_TO_NEXT_ENTRY, 0);
        addInt4Field(OFFSET_TO_FIELDS_RETURNED, 4);
        addInt4Field(NUMBER_OF_FIELDS_RETURNED, 8);
        addInt4Field(MESSAGE_SVERITY, 12);
        addCharField(MESSAGE_IDENTIFIER, 16, 7);
        addCharField(MESSAGE_TYPE, 23, 2);
        addCharField(MESSAGE_KEY, 25, 4);
        addCharField(MESSAGE_FILE_NAME, 29, 10);
        addCharField(MESSAGE_FILE_LIBRARY_SPECIFIED_AT_SEND_TIME, 39, 10);
        addCharField(DATE_SENT, 49, 7);
        addCharField(TIME_SENT, 56, 6);
        addCharField(MICROSECONDS, 62, 6);
        addCharField(THREAD_ID, 68, 8);
    }

    private String getFormattedTime() throws UnsupportedEncodingException {

        String rawTime = getCharValue(TIME_SENT);
        Date time = IBMiHelper.hhmmssToTime(rawTime);
        String mSecs = getMicroseconds();

        return timeFormatter.format(time) + "." + mSecs; //$NON-NLS-1$
    }

    private String getFormattedDate() throws UnsupportedEncodingException {

        String rawDate = getCharValue(DATE_SENT);
        Date date = IBMiHelper.cyymmddToDate(rawDate);

        return dateFormatter.format(date);
    }
}
