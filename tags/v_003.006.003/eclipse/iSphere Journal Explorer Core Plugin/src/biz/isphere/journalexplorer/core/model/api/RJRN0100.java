/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import biz.isphere.base.internal.StringHelper;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Structure;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.ProgramParameter;

/**
 * Class representing the receiver variable of the Retrieve Journal Information
 * (QjoRetrieveJournalInformation) API.
 */
public class RJRN0100 {

    private static final int BYTES_RETURNED = 0;
    private static final int BYTES_AVAILABLE = 1;
    private static final int OFFSET_KEY_INFORMATION = 2;
    private static final int JOURNAL_NAME = 3;
    private static final int JOURNAL_LIBRARY_NAME = 4;
    private static final int AUXILIARY_STORAGE_POOL = 5;
    private static final int MESSAGE_QUEUE = 6;
    private static final int MESSAGE_QUEUE_LIBRARY = 7;
    private static final int MANAGE_RECEIVER_OPTION = 8;
    private static final int DELETE_RECEIVER_OPTION = 9;
    private static final int RECEIVER_SIZE_OPTION_RMVINTENT = 10;
    private static final int RECEIVER_SIZE_OPTION_MINFIXLEN = 11;
    private static final int RECEIVER_SIZE_OPTION_MAXOPT1 = 12;
    private static final int RECEIVER_SIZE_OPTION_MAXOPT2 = 13;
    private static final int RECEIVER_SIZE_OPTION_MAXOPT3 = 14;
    private static final int RESERVED_1 = 15;
    private static final int JOURNAL_TYPE = 16;
    private static final int REMOTE_JOURNAL_TYPE = 17;
    private static final int JOURNAL_STATE = 18;
    private static final int JOURNAL_DELIVERY_MODE = 19;
    private static final int LOCAL_JOURNAL_NAME = 20;
    private static final int LOCAL_JOURNAL_LIBRARY_NAME = 21;
    private static final int LOCAL_JOURNAL_SYSTEM = 22;
    private static final int SOURCE_JOURNAL_NAME = 23;
    private static final int SOURCE_JOURNAL_LIBRARY_NAME = 24;
    private static final int SOURCE_JOURNAL_SYSTEM = 25;
    private static final int REDIRECTED_RECEIVER_LIBRARY_NAME = 26;
    private static final int JOURNAL_TEXT = 27;
    private static final int MINIMIZED_ESD_FOR_DATA_AREAS = 28;
    private static final int MINIMIZED_ESD_FOR_FILES = 29;
    private static final int RESERVED_2 = 30;
    private static final int JOURNAL_CACHE = 31;
    private static final int NUMBER_OF_ATTACHED_JOURNAL_RECEIVERS = 32;
    private static final int ATTACHED_JOURNAL_RECEIVER_NAME = 33;
    private static final int ATTACHED_JOURNAL_RECEIVER_LIBRARY_NAME = 34;
    private static final int LOCAL_SYSTEM_OF_ATTACHED_RECEIVER = 35;
    private static final int SOURCE_SYSTEM_OF_ATTACHED_RECEIVER = 36;
    private static final int ATTACHED_DUAL_RECEIVER_NAME = 37;
    private static final int ATTACHED_DUAL_RECEIVER_LIBRARY_NAME = 38;
    private static final int MANAGE_RECEIVER_DELAY = 39;
    private static final int DELETE_RECEIVER_DELAY = 40;
    private static final int ASP_DEVICE_NAME = 41;
    private static final int LOCAL_JOURNAL_ASP_GROUP_NAME = 42;
    private static final int SOURCE_JOURNAL_ASP_GROUP_NAME = 43;
    private static final int FIXED_LENGTH_DATA_JOB = 44;
    private static final int FIXED_LENGTH_DATA_USR = 45;
    private static final int FIXED_LENGTH_DATA_PGM = 46;
    private static final int FIXED_LENGTH_DATA_PGMLIB = 47;
    private static final int FIXED_LENGTH_DATA_SYSSEQ = 48;
    private static final int FIXED_LENGTH_DATA_RMTADR = 49;
    private static final int FIXED_LENGTH_DATA_THD = 50;
    private static final int FIXED_LENGTH_DATA_LUW = 51;
    private static final int FIXED_LENGTH_DATA_XID = 52;
    private static final int RESERVED_3 = 53;
    private static final int JOURNALED_OBJECT_LIMIT = 54;
    private static final int TOTAL_NUMBER_OF_JOURNALED_OBJECTS = 55;
    private static final int TOTAL_NUMBER_OF_JOURNALED_FILES = 56;
    private static final int TOTAL_NUMBER_OF_JOURNALED_MEMBERS = 57;
    private static final int TOTAL_NUMBER_OF_JOURNALED_DATA_AREAS = 58;
    private static final int TOTAL_NUMBER_OF_JOURNALED_DATA_QUEUES = 59;
    private static final int TOTAL_NUMBER_OF_JOURNALED_IFS_OBJECTS = 60;
    private static final int TOTAL_NUMBER_OF_JOURNALED_ACCESS_PATHS = 61;
    private static final int TOTAL_NUMBER_OF_COMMITMENT_DEFINITIONS = 62;
    private static final int JOURNAL_RECOVERY_COUNT = 63;
    private static final int TOTAL_NUMBER_OF_JOURNALED_LIBRARIES = 64;
    private static final int ESTIMATED_TIME_BEHIND = 65; // hundredths of
                                                         // seconds
    private static final int MAXIMUM_TIME_BEHIND = 66;
    private static final int MAXIMUM_TIME_BEHIND_DATE = 67;
    private static final int ACTIVATION_DATE = 68;
    private static final int JOURNAL_ENTRIES_FILTERED = 69;
    private static final int RESERVED_4 = 70;
    private static final int NUMBER_OF_KEYS = 71;

    private static final int RECEIVER_LEN = 480;
    private static final String FORMAT_NAME = "RJRN0100"; //$NON-NLS-1$
    private static final int ERROR_CODE = 0;

    private int bufferSize;
    private ProgramParameter[] parameterList;

    // Cached data structures
    private AS400Structure rjrn0100 = null;

    private Object[] data;

    public RJRN0100(AS400 aSystem) {
        this(aSystem, RECEIVER_LEN);
    }

    public RJRN0100(AS400 aSystem, int aBufferSize) {

        if ((aBufferSize % 16) != 0) {
            throw new IllegalArgumentException("Receiver Length not valid; value must be divisable by 16.");
        }

        bufferSize = aBufferSize;
    }

    public String getJournalName() {
        return getString(JOURNAL_NAME);
    }

    public String getJournalLibraryName() {
        return getString(JOURNAL_LIBRARY_NAME);
    }

    public String getAttachedReceiverName() {
        return getString(ATTACHED_JOURNAL_RECEIVER_NAME);
    }

    public String getAttachedReceiverLibraryName() {
        return getString(ATTACHED_JOURNAL_RECEIVER_LIBRARY_NAME);
    }

    public boolean isFixedLengthDataJob() {
        return getBoolean(FIXED_LENGTH_DATA_JOB);
    }

    public boolean isFixedLengthDataUser() {
        return getBoolean(FIXED_LENGTH_DATA_USR);
    }

    public boolean isFixedLengthDataPgm() {
        return getBoolean(FIXED_LENGTH_DATA_PGM);
    }

    public boolean isFixedLengthDataPgmLib() {
        return getBoolean(FIXED_LENGTH_DATA_PGMLIB);
    }

    public boolean isFixedLengthDataSysSeq() {
        return getBoolean(FIXED_LENGTH_DATA_SYSSEQ);
    }

    public boolean isFixedLengthDataRmtAdr() {
        return getBoolean(FIXED_LENGTH_DATA_RMTADR);
    }

    public boolean isFixedLengthDataThd() {
        return getBoolean(FIXED_LENGTH_DATA_THD);
    }

    public boolean isFixedLengthDataLuw() {
        return getBoolean(FIXED_LENGTH_DATA_LUW);
    }

    public boolean isFixedLengthDataXid() {
        return getBoolean(FIXED_LENGTH_DATA_XID);
    }

    private String getString(int index) {
        Object value = getData()[index];
        if (value instanceof String) {
            return ((String)value).trim();
        }
        return ""; //$NON-NLS-1$
    }

    private boolean getBoolean(int index) {
        String value = getString(index);
        if ("1".equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * Return the 6-elements array of ProgramParameter to pass to the
     * ProgramCall/ServiceProgramCall.
     * 
     * @return parameter list of the QjoRetrieveJournalEntries API.
     */
    public ProgramParameter[] getProgramParameters(JrnInfToRtv aJrnInfToRtv) {

        parameterList = new ProgramParameter[6];

        setReceiverData(new byte[bufferSize]);
        setReceiverLength(bufferSize);
        setJournal(aJrnInfToRtv.getJournal(), aJrnInfToRtv.getLibrary());
        setFormatName(FORMAT_NAME);
        setJrneToRtv();
        setErrorCode(ERROR_CODE);

        int size = 0;
        for (ProgramParameter parameter : parameterList) {
            size += parameter.getOutputDataLength();
        }

        if (size % 16 != 0) {
            throw new IllegalArgumentException("*** Parameter list must be aligned to a 16-byte-boundary ***"); //$NON-NLS-1$
        }

        return parameterList;
    }

    private void setReceiverData(byte[] aBuffer) {
        parameterList[0] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, aBuffer, aBuffer.length);
    }

    private void setReceiverLength(int rcvLen) {
        parameterList[1] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Bin4().toBytes(rcvLen));
    }

    private void setJournal(String journal, String library) {
        String jrnLib = StringHelper.getFixLength(journal, 10) + StringHelper.getFixLength(library, 10);
        parameterList[2] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Text(20).toBytes(jrnLib));
    }

    private void setFormatName(String format) {
        parameterList[3] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Text(8).toBytes(format));
    }

    private void setJrneToRtv() {
        parameterList[4] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Bin4().toBytes(0));
    }

    private void setErrorCode(int error) {
        parameterList[5] = new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, new AS400Bin4().toBytes(error));
    }

    private byte[] getOutputData() {
        return parameterList[0].getOutputData();
    }

    private Object getData(int index) {

        if (index >= getData().length) {
            return null;
        }

        return getData()[index];
    }

    private Object[] getData() {
        if (data == null) {
            data = (Object[])getStructure().toObject(getOutputData(), 0);
        }
        return data;
    }

    private AS400Structure getStructure() {
        if (rjrn0100 == null) {
            // @formatter:off formatter intentionally disabled
            AS400DataType[] dataTypes = new AS400DataType[72];     
            dataTypes[BYTES_RETURNED] = new AS400Bin4();                           //  0 Bytes returned 
            dataTypes[BYTES_AVAILABLE] = new AS400Bin4();                          //  1 Bytes available
            dataTypes[OFFSET_KEY_INFORMATION] = new AS400Bin4();                   //  2 Offset to key information
            dataTypes[JOURNAL_NAME] = new AS400Text(10);                           //  3 Journal name
            dataTypes[JOURNAL_LIBRARY_NAME] = new AS400Text(10);                   //  4 Journal library name
            dataTypes[AUXILIARY_STORAGE_POOL] = new AS400Bin4();                   //  5 Auxiliary storage pool (ASP) 
            dataTypes[MESSAGE_QUEUE] = new AS400Text(10);                          //  6 Message queue
            dataTypes[MESSAGE_QUEUE_LIBRARY] = new AS400Text(10);                  //  7 Message queue library name
            dataTypes[MANAGE_RECEIVER_OPTION] = new AS400Text(1);                  //  8 Manage receiver option
            dataTypes[DELETE_RECEIVER_OPTION] = new AS400Text(1);                  //  9 Delete receiver option
            dataTypes[RECEIVER_SIZE_OPTION_RMVINTENT] = new AS400Text(1);          // 10 Receiver size option *RMVINTENT
            dataTypes[RECEIVER_SIZE_OPTION_MINFIXLEN] = new AS400Text(1);          // 11 Receiver size option *MINFIXLEN
            dataTypes[RECEIVER_SIZE_OPTION_MAXOPT1] = new AS400Text(1);            // 12 Receiver size option *MAXOPT1
            dataTypes[RECEIVER_SIZE_OPTION_MAXOPT2] = new AS400Text(1);            // 13 Receiver size option *MAXOPT2
            dataTypes[RECEIVER_SIZE_OPTION_MAXOPT3] = new AS400Text(1);            // 14 Receiver size option *MAXOPT3
            dataTypes[RESERVED_1] = new AS400Text(2);                              // 15 Reserved
            dataTypes[JOURNAL_TYPE] = new AS400Text(1);                            // 16 Journal type
            dataTypes[REMOTE_JOURNAL_TYPE] = new AS400Text(1);                     // 17 Remote journal type
            dataTypes[JOURNAL_STATE] = new AS400Text(1);                           // 18 Journal state
            dataTypes[JOURNAL_DELIVERY_MODE] = new AS400Text(1);                   // 19 Journal delivery mode
            dataTypes[LOCAL_JOURNAL_NAME] = new AS400Text(10);                     // 20 Local journal name
            dataTypes[LOCAL_JOURNAL_LIBRARY_NAME] = new AS400Text(10);             // 21 Local journal library name
            dataTypes[LOCAL_JOURNAL_SYSTEM] = new AS400Text(8);                    // 22 Local journal system
            dataTypes[SOURCE_JOURNAL_NAME] = new AS400Text(10);                    // 23 Source journal name
            dataTypes[SOURCE_JOURNAL_LIBRARY_NAME] = new AS400Text(10);            // 24 Source journal library name
            dataTypes[SOURCE_JOURNAL_SYSTEM] = new AS400Text(8);                   // 25 Source journal system
            dataTypes[REDIRECTED_RECEIVER_LIBRARY_NAME] = new AS400Text(10);       // 26 Redirected receiver library name
            dataTypes[JOURNAL_TEXT] = new AS400Text(50);                           // 27 Journal text
            dataTypes[MINIMIZED_ESD_FOR_DATA_AREAS] = new AS400Text(1);            // 28 Minimize entry specific data for data areas
            dataTypes[MINIMIZED_ESD_FOR_FILES] = new AS400Text(1);                 // 29 Minimize entry specific data for files
            dataTypes[RESERVED_2] = new AS400Text(8);                              // 30 Reserved
            dataTypes[JOURNAL_CACHE] = new AS400Text(1);                           // 31 Journal Cache
            dataTypes[NUMBER_OF_ATTACHED_JOURNAL_RECEIVERS] = new AS400Bin4();     // 32 Number of attached journal receivers 
            dataTypes[ATTACHED_JOURNAL_RECEIVER_NAME] = new AS400Text(10);         // 33 Attached journal receiver name
            dataTypes[ATTACHED_JOURNAL_RECEIVER_LIBRARY_NAME] = new AS400Text(10); // 34 Attached journal receiver library name
            dataTypes[LOCAL_SYSTEM_OF_ATTACHED_RECEIVER] = new AS400Text(8);       // 35 Local journal system associated with the attached journal receiver
            dataTypes[SOURCE_SYSTEM_OF_ATTACHED_RECEIVER] = new AS400Text(8);      // 36 Source journal system associated with the attached journal receiver
            dataTypes[ATTACHED_DUAL_RECEIVER_NAME] = new AS400Text(10);            // 37 Attached dual journal receiver name
            dataTypes[ATTACHED_DUAL_RECEIVER_LIBRARY_NAME] = new AS400Text(10);    // 38 Attached dual journal receiver library name
            dataTypes[MANAGE_RECEIVER_DELAY] = new AS400Bin4();                    // 39 Manage receiver delay 
            dataTypes[DELETE_RECEIVER_DELAY] = new AS400Bin4();                    // 40 Delete receiver delay 
            dataTypes[ASP_DEVICE_NAME] = new AS400Text(10);                        // 41 ASP device name
            dataTypes[LOCAL_JOURNAL_ASP_GROUP_NAME] = new AS400Text(10);           // 42 Local journal ASP group name
            dataTypes[SOURCE_JOURNAL_ASP_GROUP_NAME] = new AS400Text(10);          // 43 Source journal ASP group name
            dataTypes[FIXED_LENGTH_DATA_JOB] = new AS400Text(1);                   // 44 Fixed length data JOB
            dataTypes[FIXED_LENGTH_DATA_USR] = new AS400Text(1);                   // 45 Fixed length data USR
            dataTypes[FIXED_LENGTH_DATA_PGM] = new AS400Text(1);                   // 46 Fixed length data PGM
            dataTypes[FIXED_LENGTH_DATA_PGMLIB] = new AS400Text(1);                // 47 Fixed length data PGMLIB
            dataTypes[FIXED_LENGTH_DATA_SYSSEQ] = new AS400Text(1);                // 48 Fixed length data SYSSEQ
            dataTypes[FIXED_LENGTH_DATA_RMTADR] = new AS400Text(1);                // 49 Fixed length data RMTADR
            dataTypes[FIXED_LENGTH_DATA_THD] = new AS400Text(1);                   // 50 Fixed length data THD
            dataTypes[FIXED_LENGTH_DATA_LUW] = new AS400Text(1);                   // 51 Fixed length data LUW
            dataTypes[FIXED_LENGTH_DATA_XID] = new AS400Text(1);                   // 52 Fixed length data XID
            dataTypes[RESERVED_3] = new AS400Text(4);                              // 53 Reserved
            dataTypes[JOURNALED_OBJECT_LIMIT] = new AS400Text(1);                  // 54 Journaled object limit
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_OBJECTS] = new AS400Bin4();        // 55 Total number of journaled objects 
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_FILES] = new AS400Bin4();          // 56 Total number of journaled files 
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_MEMBERS] = new AS400Bin4();        // 57 Total number of journaled members
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_DATA_AREAS] = new AS400Bin4();     // 58 Total number of journaled data areas
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_DATA_QUEUES] = new AS400Bin4();    // 59 Total number of journaled data queues
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_IFS_OBJECTS] = new AS400Bin4();    // 60 Total number of journaled integrated file system objects of type *DIR, *STMF, and *SYMLNK
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_ACCESS_PATHS] = new AS400Bin4();   // 61 Total number of journaled access paths
            dataTypes[TOTAL_NUMBER_OF_COMMITMENT_DEFINITIONS] = new AS400Bin4();   // 62 Total number of commitment definitions
            dataTypes[JOURNAL_RECOVERY_COUNT] = new AS400Bin4();                   // 63 Journal recovery count
            dataTypes[TOTAL_NUMBER_OF_JOURNALED_LIBRARIES] = new AS400Bin4();      // 64 Total number of journaled libraries
            dataTypes[ESTIMATED_TIME_BEHIND] = new AS400Bin4();                    // 65 Estimated hundredths of seconds behind
            dataTypes[MAXIMUM_TIME_BEHIND] = new AS400Bin4();                      // 66 Maximum estimated hundredths of seconds behind
            dataTypes[MAXIMUM_TIME_BEHIND_DATE] = new AS400Text(13);               // 67 Maximum estimated hundredths of seconds behind date and time
            dataTypes[ACTIVATION_DATE] = new AS400Text(13);                        // 68 Activation date and time
            dataTypes[JOURNAL_ENTRIES_FILTERED] = new AS400Text(1);                // 69 Journal entries filtered
            dataTypes[RESERVED_4] = new AS400Text(65);                             // 70 Reserved
            dataTypes[NUMBER_OF_KEYS] = new AS400Bin4();                           // 71 Number of keys in key section
            // @formatter:on
            rjrn0100 = new AS400Structure(dataTypes);
        }
        return rjrn0100;
    }
}
