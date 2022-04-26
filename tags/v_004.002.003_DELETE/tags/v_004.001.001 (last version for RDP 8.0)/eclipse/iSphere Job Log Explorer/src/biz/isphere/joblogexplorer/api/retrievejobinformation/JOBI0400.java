/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.retrievejobinformation;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class JOBI0400 extends APIFormat {

    private static final String NUM_BYTES_RETURNED = "numberOfBytesReturned"; //$NON-NLS-1$
    private static final String NUM_BYTES_AVAILABLE = "numberOfBytesAvailable"; //$NON-NLS-1$
    private static final String JOB_NAME = "jobName"; //$NON-NLS-1$
    private static final String JOB_USER = "userName"; //$NON-NLS-1$
    private static final String JOB_NUMBER = "jobNumber"; //$NON-NLS-1$
    private static final String INTERNAL_JOB_IDENTIFIER = "internalJobIdentifier"; //$NON-NLS-1$
    private static final String JOB_STATUS = "jobStatus"; //$NON-NLS-1$
    private static final String JOB_TYPE = "jobType"; //$NON-NLS-1$
    private static final String JOB_SUB_TYPE = "jobSubType"; //$NON-NLS-1$
    private static final String DATE_TIME_ENTERED = "dateTimeJobEnteredSystem"; //$NON-NLS-1$
    private static final String DATE_TIME_ACTIVE = "dateTimeJobBecameActive"; //$NON-NLS-1$
    private static final String ACCOUNTING_CODE = "jobAccountingCode"; //$NON-NLS-1$
    private static final String JOB_DESCRIPTION_NAME = "jobDescriptionName"; //$NON-NLS-1$
    private static final String JOB_DESCRIPTION_LIBRARY_NAME = "jobDescriptionLibraryName"; //$NON-NLS-1$
    private static final String UNIT_OF_WORK_ID = "unitOfWorkId"; //$NON-NLS-1$
    private static final String MODE_NAME = "modeName"; //$NON-NLS-1$
    private static final String INQUIRY_MSG_REPLY = "inquiryMessageReply"; //$NON-NLS-1$
    private static final String LOG_CL = "loggingOfClPrograms"; //$NON-NLS-1$
    private static final String BREAK_MSG_HANDLING = "breakMessageHandling"; //$NON-NLS-1$
    private static final String STATUS_MSG_HANDLING = "statusMessageHandling"; //$NON-NLS-1$
    private static final String DEVICE_RECOVERY_ACTION = "deviceRecoveryAction"; //$NON-NLS-1$
    private static final String DDM_CONV_HANDLING = "ddmConversationHandling"; //$NON-NLS-1$
    private static final String DATE_SEPARATOR = "dateSeparator"; //$NON-NLS-1$
    private static final String DATE_FORMAT = "dateFormat"; //$NON-NLS-1$
    private static final String PRINT_TEXT = "printText"; //$NON-NLS-1$
    private static final String SBM_JOB_NAME = "submittersJobName"; //$NON-NLS-1$
    private static final String SBM_JOB_USER = "submittersJobUser"; //$NON-NLS-1$
    private static final String SBM_JOB_NUMBER = "submittersJobNumber"; //$NON-NLS-1$
    private static final String SBM_JOB_MSGQ_NAME = "submittersJobMsgQName"; //$NON-NLS-1$
    private static final String SBM_JOB_MSGQ_LIBRARY_NAME = "submittersJobMsgQLibraryName"; //$NON-NLS-1$
    private static final String TIME_SEPARATOR = "timeSeparator"; //$NON-NLS-1$
    private static final String CCSID = "ccsid"; //$NON-NLS-1$
    private static final String DATE_TIME_SCHEDULED = "dateTimeJobScheduledToRun"; //$NON-NLS-1$
    private static final String PRINT_KEY_FORMAT = "printKeyFormat"; //$NON-NLS-1$
    private static final String SORT_SEQ_TABLE_NAME = "sortSequenceTableName"; //$NON-NLS-1$
    private static final String SORT_SEQ_TABLE_LIBRARY_NAME = "sortSequenceTableLibraryName"; //$NON-NLS-1$
    private static final String LANGUAGE_ID = "languageId"; //$NON-NLS-1$
    private static final String COUNTRY_ID = "countryOrRegionId"; //$NON-NLS-1$
    private static final String COMPLETION_STATUS = "completionStatus"; //$NON-NLS-1$
    private static final String SIGNED_ON_JOB = "signedOnJob"; //$NON-NLS-1$
    private static final String JOB_SWITCHES = "jobSwitches"; //$NON-NLS-1$
    private static final String JOB_MSGQ_FULL_ACTION = "jobMsgQFullAction"; //$NON-NLS-1$
    private static final String RESERVED_1 = "reserved_1"; //$NON-NLS-1$
    private static final String JOB_MSGQ_MAX_SIZE = "jobMsgQMaxSize"; //$NON-NLS-1$
    private static final String DEFAULT_CCSID = "defaultCcsid"; //$NON-NLS-1$
    private static final String ROUTING_DATA = "routingData"; //$NON-NLS-1$
    private static final String DECIMAL_FORMAT = "decimalFormat"; //$NON-NLS-1$
    private static final String CHARACTER_IDENTIFIER_CONTROL = "characterIdentifierControl"; //$NON-NLS-1$
    private static final String SERVER_TYPE = "serverType"; //$NON-NLS-1$
    private static final String ALLOW_MULTIPLE_THREADS = "allowMultipleThreads"; //$NON-NLS-1$
    private static final String JOB_LOG_PENDING = "jobLogPending"; //$NON-NLS-1$
    private static final String RESERVED_2 = "reserved_2"; //$NON-NLS-1$
    private static final String JOB_END_REASON = "jobEndReason"; //$NON-NLS-1$
    private static final String JOB_TYPE_ENHANCED = "jobTypeEnhanced"; //$NON-NLS-1$
    private static final String DATE_TIME_ENDED = "dateTimeJobEnded"; //$NON-NLS-1$
    private static final String RESERVED_3 = "reserved_3"; //$NON-NLS-1$
    private static final String SPOOLED_FILE_ACTION = "spooledFileAction"; //$NON-NLS-1$
    private static final String OFFSET_ASP_GROUP_INFORMATION = "offsetToASPGroupInformation"; //$NON-NLS-1$
    private static final String NUMBER_OF_ENTRIES_IN_ASP_GROUP_INFORMATION = "numberOfEntriesInASPGroupInformation"; //$NON-NLS-1$
    private static final String LENGTH_ASP_GROUP_INFORMATION_ENTRY = "lengthOfASPGroupInformationEntry"; //$NON-NLS-1$
    private static final String TIME_ZONE_DESCRIPTION_NAME = "timeZoneDescriptionName"; //$NON-NLS-1$
    private static final String JOB_LOG_OUTPUT = "jobLogOutput"; //$NON-NLS-1$
    private static final String JOB_DESCRIPTION_LIBRARY_ASP_DEVICE_NAME = "jobDescriptionLibraryASPDeviceName"; //$NON-NLS-1$

    public static final String JOB_NOT_FOUND_MSGID = "CPF3C53"; //$NON-NLS-1$

    private String jobName;
    private String jobUser;
    private String jobNumber;
    private String jobDescriptionName;
    private String jobDescriptionLibraryName;

    public JOBI0400(AS400 system) {
        super(system, "JOBI0400"); //$NON-NLS-1$

        createStructure();
    }

    protected void loadValues() throws UnsupportedEncodingException {

        jobName = getCharValue(JOB_NAME);
        jobUser = getCharValue(JOB_USER);
        jobNumber = getCharValue(JOB_NUMBER);
        jobDescriptionName = getCharValue(JOB_DESCRIPTION_NAME);
        jobDescriptionLibraryName = getCharValue(JOB_DESCRIPTION_LIBRARY_NAME);
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobUser() {
        return jobUser;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public String getJobDescriptionName() {
        return jobDescriptionName;
    }

    public String getJobDescriptionLibraryName() {
        return jobDescriptionLibraryName;
    }

    /**
     * Creates the JOBI0400 structure.
     */
    private void createStructure() {

        addInt4Field(NUM_BYTES_RETURNED, 0);
        addInt4Field(NUM_BYTES_AVAILABLE, 4);
        addCharField(JOB_NAME, 8, 10);
        addCharField(JOB_USER, 18, 10);
        addCharField(JOB_NUMBER, 28, 6);
        addCharField(INTERNAL_JOB_IDENTIFIER, 34, 16);
        addCharField(JOB_STATUS, 50, 10);
        addCharField(JOB_TYPE, 60, 1);
        addCharField(JOB_SUB_TYPE, 61, 1);
        addCharField(DATE_TIME_ENTERED, 62, 13);
        addCharField(DATE_TIME_ACTIVE, 75, 13);
        addCharField(ACCOUNTING_CODE, 88, 15);
        addCharField(JOB_DESCRIPTION_NAME, 103, 10);
        addCharField(JOB_DESCRIPTION_LIBRARY_NAME, 113, 10);
        addCharField(UNIT_OF_WORK_ID, 123, 24);
        addCharField(MODE_NAME, 147, 8);
        addCharField(INQUIRY_MSG_REPLY, 155, 10);
        addCharField(LOG_CL, 165, 10);
        addCharField(BREAK_MSG_HANDLING, 175, 10);
        addCharField(STATUS_MSG_HANDLING, 185, 10);
        addCharField(DEVICE_RECOVERY_ACTION, 195, 13);
        addCharField(DDM_CONV_HANDLING, 208, 10);
        addCharField(DATE_SEPARATOR, 218, 1);
        addCharField(DATE_FORMAT, 219, 4);
        addCharField(PRINT_TEXT, 223, 30);
        addCharField(SBM_JOB_NAME, 253, 10);
        addCharField(SBM_JOB_USER, 263, 10);
        addCharField(SBM_JOB_NUMBER, 273, 6);
        addCharField(SBM_JOB_MSGQ_NAME, 279, 10);
        addCharField(SBM_JOB_MSGQ_LIBRARY_NAME, 289, 10);
        addCharField(TIME_SEPARATOR, 299, 1);
        addInt4Field(CCSID, 300);
        addCharField(DATE_TIME_SCHEDULED, 304, 13);
        addCharField(PRINT_KEY_FORMAT, 312, 10);
        addCharField(SORT_SEQ_TABLE_NAME, 322, 10);
        addCharField(SORT_SEQ_TABLE_LIBRARY_NAME, 332, 10);
        addCharField(LANGUAGE_ID, 342, 3);
        addCharField(COUNTRY_ID, 345, 2);
        addCharField(COMPLETION_STATUS, 347, 1);
        addCharField(SIGNED_ON_JOB, 348, 1);
        addCharField(JOB_SWITCHES, 349, 8);
        addCharField(JOB_MSGQ_FULL_ACTION, 357, 10);
        addCharField(RESERVED_1, 367, 1);
        addInt4Field(JOB_MSGQ_MAX_SIZE, 368);
        addInt4Field(DEFAULT_CCSID, 372);
        addCharField(ROUTING_DATA, 376, 80);
        addCharField(DECIMAL_FORMAT, 456, 1);
        addCharField(CHARACTER_IDENTIFIER_CONTROL, 457, 10);
        addCharField(SERVER_TYPE, 467, 30);
        addCharField(ALLOW_MULTIPLE_THREADS, 497, 1);
        addCharField(JOB_LOG_PENDING, 498, 1);
        addCharField(RESERVED_2, 499, 1);
        addInt4Field(JOB_END_REASON, 500);
        addInt4Field(JOB_TYPE_ENHANCED, 504);
        addCharField(DATE_TIME_ENDED, 508, 13);
        addCharField(RESERVED_3, 521, 1);
        addCharField(SPOOLED_FILE_ACTION, 522, 10);
        addInt4Field(OFFSET_ASP_GROUP_INFORMATION, 532);
        addInt4Field(NUMBER_OF_ENTRIES_IN_ASP_GROUP_INFORMATION, 536);
        addInt4Field(LENGTH_ASP_GROUP_INFORMATION_ENTRY, 540);
        addCharField(TIME_ZONE_DESCRIPTION_NAME, 544, 10);
        addCharField(JOB_LOG_OUTPUT, 554, 10);
        addCharField(JOB_DESCRIPTION_LIBRARY_ASP_DEVICE_NAME, 564, 10);

    }

}
