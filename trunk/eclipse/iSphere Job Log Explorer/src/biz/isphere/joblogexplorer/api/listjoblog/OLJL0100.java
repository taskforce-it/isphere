/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.listjoblog;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.joblogexplorer.Messages;

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

    private static final String MsgType_Completion = "01"; //$NON-NLS-1$
    private static final String MsgType_Diagnostic = "02"; //$NON-NLS-1$
    private static final String MsgType_Informational = "04"; //$NON-NLS-1$
    private static final String MsgType_Inquiry = "05"; //$NON-NLS-1$
    private static final String MsgType_SendersCopy = "06"; //$NON-NLS-1$
    private static final String MsgType_Request = "08"; //$NON-NLS-1$
    private static final String MsgType_RequestWithPrompting = "10"; //$NON-NLS-1$
    private static final String MsgType_Notify_ExceptionAlreadyHandled = "14"; //$NON-NLS-1$
    private static final String MsgType_Escape_ExceptionAlreadyHandled = "15"; //$NON-NLS-1$
    private static final String MsgType_Notify_ExceptionNotHandled = "16"; //$NON-NLS-1$
    private static final String MsgType_Escape_ExceptionNotYetHandled = "17"; //$NON-NLS-1$
    private static final String MsgType_Reply_ValidityNotChecked = "21"; //$NON-NLS-1$
    private static final String MsgType_Reply_ValidityChecked = "22"; //$NON-NLS-1$
    private static final String MsgType_Reply_MessageDefault = "23"; //$NON-NLS-1$
    private static final String MsgType_Reply_SystemDefault = "24"; //$NON-NLS-1$
    private static final String MsgType_Reply_SystemReplyList = "25"; //$NON-NLS-1$
    private static final String MsgType_Reply_ExitProgram = "26"; //$NON-NLS-1$

    private SimpleDateFormat timeFormatter;
    private SimpleDateFormat dateFormatter;

    private Timestamp timestamp;
    private DecimalFormat mSecsFormatter = new DecimalFormat("000000");

    public OLJL0100(AS400 system) {
        super(system, "OLJL0100"); //$NON-NLS-1$

        this.timeFormatter = Preferences.getInstance().getTimeFormatter();
        this.dateFormatter = Preferences.getInstance().getDateFormatter();

        createStructure();
    }

    @Override
    public void setBytes(byte[] bytes) {
        super.setBytes(bytes);

        timestamp = null;
    }

    @Override
    public void setOffset(int offset) {
        super.setOffset(offset);

        timestamp = null;
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

        if (MsgType_Completion.equals(type)) {
            return Messages.MsgType_Completion;
        } else if (MsgType_Diagnostic.equals(type)) {
            return Messages.MsgType_Diagnostic;
        } else if (MsgType_Informational.equals(type)) {
            return Messages.MsgType_Informational;
        } else if (MsgType_Inquiry.equals(type)) {
            return Messages.MsgType_Inquery;
        } else if (MsgType_SendersCopy.equals(type)) {
            return Messages.MsgType_Copy;
        } else if (MsgType_Request.equals(type)) {
            return Messages.MsgType_Request;
        } else if (MsgType_RequestWithPrompting.equals(type)) {
            return Messages.MsgType_Request;
        } else if (MsgType_Notify_ExceptionAlreadyHandled.equals(type)) {
            return Messages.MsgType_Notify;
        } else if (MsgType_Escape_ExceptionAlreadyHandled.equals(type)) {
            return Messages.MsgType_Escape;
        } else if (MsgType_Notify_ExceptionNotHandled.equals(type)) {
            return Messages.MsgType_Notify;
        } else if (MsgType_Escape_ExceptionNotYetHandled.equals(type)) {
            return Messages.MsgType_Escape;
        } else if (MsgType_Reply_ValidityNotChecked.equals(type)) {
            return Messages.MsgType_Reply;
        } else if (MsgType_Reply_ValidityChecked.equals(type)) {
            return Messages.MsgType_Reply;
        } else if (MsgType_Reply_MessageDefault.equals(type)) {
            return Messages.MsgType_Reply;
        } else if (MsgType_Reply_SystemDefault.equals(type)) {
            return Messages.MsgType_Reply;
        } else if (MsgType_Reply_SystemReplyList.equals(type)) {
            return Messages.MsgType_Reply;
        } else if (MsgType_Reply_ExitProgram.equals(type)) {
            return Messages.MsgType_Reply;
        } else {
            return "*Unknown"; //$NON-NLS-1$
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

    public Timestamp getTimestampSent() throws UnsupportedEncodingException {

        if (timestamp == null) {
            Date time = IBMiHelper.hhmmssToTime(getCharValue(TIME_SENT));
            Date date = IBMiHelper.cyymmddToDate(getCharValue(DATE_SENT));
            timestamp = new Timestamp(DateTimeHelper.combineDateTime(date, time).getTime());
            timestamp.setNanos(Integer.parseInt(getMicroseconds()) * 1000);
        }

        return timestamp;
    }

    public String getMicroseconds() throws UnsupportedEncodingException {
        return getCharValue(MICROSECONDS);
    }

    public String getThreadId() throws UnsupportedEncodingException {
        return getCharValue(THREAD_ID);
    }

    public String getFieldData(int key) throws UnsupportedEncodingException {

        OLJL0100_Field field = getField(key);
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
        Timestamp timestampSent = getTimestampSent();
        return timeFormatter.format(timestampSent) + "." + mSecsFormatter.format(timestampSent.getNanos()); //$NON-NLS-1$
    }

    private String getFormattedDate() throws UnsupportedEncodingException {
        return dateFormatter.format(getTimestampSent());
    }
}
