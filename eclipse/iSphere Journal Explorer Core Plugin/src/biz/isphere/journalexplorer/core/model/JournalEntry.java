/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.journalexplorer.base.interfaces.IDatatypeConverterDelegate;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.rse.shared.model.DatatypeConverterDelegate;
import biz.isphere.journalexplorer.rse.shared.model.JournalEntryDelegate;

import com.google.gson.annotations.Expose;
import com.ibm.as400.access.AS400Text;

public class JournalEntry {

    private static final String ADDRESS_FAMILY_UNKNOWN = "0";
    private static final String ADDRESS_FAMILY_IPV4 = "4";
    private static final String ADDRESS_FAMILY_IPV6 = "6";

    public static final String USER_GENERATED = "U"; //$NON-NLS-1$

    private static final int JOCODE = 0;
    private static final int JOENTT = 1;
    private static final int JOJOB = 2;
    private static final int JOUSER = 3;
    private static final int JONBR = 4;
    private static final int JOLIB = 5;
    private static final int JOOBJ = 6;
    private static final int JOMBR = 7;
    private static final int JODATE = 8;
    private static final int JOTIME = 9;
    private static final int JOTSTP = 10;
    private static final int JOPGM = 11;
    private static final int JOPGMLIB = 12;
    private static final int JOOBJTYP = 13;
    private static final int JOFILTYP = 14;
    private static final int JOSYNM = 15;
    private static final int JORCV = 16;
    private static final int JORCVLIB = 17;
    private static final int JOUSPF = 17;
    private static final int JOSEQN = 18;
    private static final int JOCCID = 19;

    private static HashMap<String, Integer> columnMappings;
    static {
        columnMappings = new HashMap<String, Integer>();
        columnMappings.put("JOCODE", JOCODE);
        columnMappings.put("JOENTT", JOENTT);
        columnMappings.put("JOJOB", JOJOB);
        columnMappings.put("JOUSER", JOUSER);
        columnMappings.put("JONBR", JONBR);
        columnMappings.put("JOLIB", JOLIB);
        columnMappings.put("JOOBJ", JOOBJ);
        columnMappings.put("JOMBR", JOMBR);
        columnMappings.put("JODATE", JODATE);
        columnMappings.put("JOTIME", JOTIME);
        columnMappings.put("JOTSTP", JOTSTP);
        columnMappings.put("JOPGM", JOPGM);
        columnMappings.put("JOPGMLIB", JOPGMLIB);
        columnMappings.put("JOOBJTYP", JOOBJTYP);
        columnMappings.put("JOFILTYP", JOFILTYP);
        columnMappings.put("JOSYNM", JOSYNM);
        columnMappings.put("JORCV", JORCV);
        columnMappings.put("JORCVLIB", JORCVLIB);
        columnMappings.put("JOUSPF", JOUSPF);
        columnMappings.put("JOSEQN", JOSEQN);
        columnMappings.put("JOCCID", JOCCID);
    }

    private static List<ContentAssistProposal> proposals;
    static {
        proposals = new LinkedList<ContentAssistProposal>();
        proposals.add(new ContentAssistProposal("JOCODE", "CHAR(1)" + " - " + Messages.LongFieldName_JOCODE));
        proposals.add(new ContentAssistProposal("JOENTT", "CHAR(2)" + " - " + Messages.LongFieldName_JOENTT));
        proposals.add(new ContentAssistProposal("JOJOB", "CHAR(10)" + " - " + Messages.LongFieldName_JOJOB));
        proposals.add(new ContentAssistProposal("JOUSER", "CHAR(10)" + " - " + Messages.LongFieldName_JOUSER));
        proposals.add(new ContentAssistProposal("JONBR", "INTEGER" + " - " + Messages.LongFieldName_JONBR));
        proposals.add(new ContentAssistProposal("JOLIB", "CHAR(10)" + " - " + Messages.LongFieldName_JOLIB));
        proposals.add(new ContentAssistProposal("JOOBJ", "CHAR(10)" + " - " + Messages.LongFieldName_JOOBJ));
        proposals.add(new ContentAssistProposal("JOMBR", "CHAR(10)" + " - " + Messages.LongFieldName_JOMBR));
        proposals.add(new ContentAssistProposal("JODATE", "DATE" + " - " + Messages.LongFieldName_JODATE));
        proposals.add(new ContentAssistProposal("JOTIME", "TIME" + " - " + Messages.LongFieldName_JOTIME));
        proposals.add(new ContentAssistProposal("JOTSTP", "TIMESTAMP" + " - " + Messages.LongFieldName_JOTSTP));
        proposals.add(new ContentAssistProposal("JOPGM", "CHAR(10)" + " - " + Messages.LongFieldName_JOPGM));
        proposals.add(new ContentAssistProposal("JOPGMLIB", "CHAR(10)" + " - " + Messages.LongFieldName_JOPGMLIB));
        proposals.add(new ContentAssistProposal("JOOBJTYP", "CHAR(7)" + " - " + Messages.LongFieldName_JOOBJTYP));
        proposals.add(new ContentAssistProposal("JOFILTYP", "CHAR(2)" + " - " + Messages.LongFieldName_JOFILTYP));
        proposals.add(new ContentAssistProposal("JOSYNM", "CHAR(8)" + " - " + Messages.LongFieldName_JOSYNM));
        proposals.add(new ContentAssistProposal("JORCV", "CHAR(10)" + " - " + Messages.LongFieldName_JORCV));
        proposals.add(new ContentAssistProposal("JORCVLIB", "CHAR(10)" + " - " + Messages.LongFieldName_JORCVLIB));
        proposals.add(new ContentAssistProposal("JOUSPF", "CHAR(10)" + " - " + Messages.LongFieldName_JOUSPF));
        proposals.add(new ContentAssistProposal("JOSEQN", "INTEGER" + " - " + Messages.LongFieldName_JOSEQN));
        proposals.add(new ContentAssistProposal("JOCCID", "INTEGER" + " - " + Messages.LongFieldName_JOCCID));
    }

    @Expose(serialize = true, deserialize = true)
    private String connectionName;
    @Expose(serialize = true, deserialize = true)
    private String outputFileName;
    @Expose(serialize = true, deserialize = true)
    private String outputFileLibraryName;
    @Expose(serialize = true, deserialize = true)
    private String outputFileMemberName;

    @Expose(serialize = true, deserialize = true)
    private int id;
    @Expose(serialize = true, deserialize = true)
    private java.sql.Timestamp timestamp;

    @Expose(serialize = true, deserialize = true)
    private int entryLength; // JOENTL
    @Expose(serialize = true, deserialize = true)
    private BigInteger sequenceNumber; // JOSEQN
    @Expose(serialize = true, deserialize = true)
    private String journalCode; // JOCODE
    @Expose(serialize = true, deserialize = true)
    private String entryType; // JOENTT
    @Expose(serialize = true, deserialize = true)
    private java.sql.Date date; // JODATE
    @Expose(serialize = true, deserialize = true)
    private java.sql.Time time; // JOTIME
    @Expose(serialize = true, deserialize = true)
    private String jobName; // JOJOB
    @Expose(serialize = true, deserialize = true)
    private String jobUserName; // JOUSER
    @Expose(serialize = true, deserialize = true)
    private int jobNumber; // JONBR
    @Expose(serialize = true, deserialize = true)
    private String programName; // JOPGM
    @Expose(serialize = true, deserialize = true)
    private String programLibrary; // JOLIB
    @Expose(serialize = true, deserialize = true)
    private String objectName; // JOOBJ
    @Expose(serialize = true, deserialize = true)
    private String objectLibrary; // JOLIB
    @Expose(serialize = true, deserialize = true)
    private String memberName; // JOMBR
    @Expose(serialize = true, deserialize = true)
    private BigInteger countRrn; // JOCTRR
    /**
     * Contains an indicator for the operation. The following tables show
     * specific values for this field, if applicable:
     * <p>
     * APYJRNCHG (B AT, D DD, E EQ, F AY, Q QH) and RMVJRNCHG (E EX, F RC)
     * journal entries. The results of the apply or remove operation:
     * <ul>
     * <li>0 = Command completed normally.</li>
     * <li>1 = Command completed abnormally.</li>
     * </ul>
     * COMMIT (C CM) journal entry. Whether the commit operation was initiated
     * by the system or the user:
     * <ul>
     * <li>0 = All record-level changes were committed for a commit operation
     * initiated by a user.</li>
     * <li>2 = All record-level changes were committed for a commit operation
     * initiated by the operating system.</li>
     * </ul>
     * INZPFM (F IZ) journal entry. Indicates the type of record initialization
     * that was done:
     * <ul>
     * <li>0 = *DFT (default)</li>
     * <li>1 = *DLT (delete)</li>
     * </ul>
     * IPL (J IA, J IN) and in-use (B OI, C BA, D ID, E EI, F IU, I DA, J JI, Q
     * QI) journal entries. For in-use entries, indicates whether the object was
     * synchronized with the journal:
     * <ul>
     * <li>0 = Object was synchronized with journal</li>
     * <li>1 = Object was not synchronized with journal</li>
     * </ul>
     * Journal code R, all journal entry types except IL. Whether a before-image
     * is present:
     * <ul>
     * <li>0 = Before-image is not present. If before-images are being
     * journaled, this indicates that an update operation or delete operation is
     * being requested for a record that has already been deleted.</li>
     * <li>1 = 1 = Before-image is present.</li>
     * </ul>
     * ROLLBACK (C RB) journal entry. How the rollback operation was initiated
     * and whether it was successful:
     * <ul>
     * <li>0 = All record-level changes were rolled back for a rollback
     * operation initiated by a user.</li>
     * <li>1 = Not all record-level changes were successfully rolled back for a
     * rollback operation initiated by a user.</li>
     * <li>2 = All record-level changes were rolled back for a rollback
     * operation initiated by the operating system.</li>
     * <li>3 = Not all record-level changes were rolled back for a rollback
     * operation initiated by the operating system.</li>
     * </ul>
     * Start journal (B JT, D JF, E EG, F JM, Q QB) journal entries. Indicates
     * the type of images selected:
     * <ul>
     * <li>0 = After images are journaled.</li>
     * <li>1 = Before and after images are journaled.</li>
     * </ul>
     */
    @Expose(serialize = true, deserialize = true)
    private String flag; // JOFLAG
    @Expose(serialize = true, deserialize = true)
    private BigInteger commitmentCycle; // JOCCID
    @Expose(serialize = true, deserialize = true)
    private String userProfile; // JOUSPF
    @Expose(serialize = true, deserialize = true)
    private String systemName; // JOSYNM
    @Expose(serialize = true, deserialize = true)
    private String journalID; // JOJID
    @Expose(serialize = true, deserialize = true)
    private String referentialConstraint; // JORCST
    @Expose(serialize = true, deserialize = true)
    private String referentialConstraintText;
    @Expose(serialize = true, deserialize = true)
    private String trigger; // JOTGR
    @Expose(serialize = true, deserialize = true)
    private String triggerText;
    @Expose(serialize = true, deserialize = true)
    private String incompleteData; // JOINCDAT
    @Expose(serialize = true, deserialize = true)
    private String incompleteDataText;
    @Expose(serialize = true, deserialize = true)
    private String apyRmvJrnChg; // JOIGNAPY
    @Expose(serialize = true, deserialize = true)
    private String apyRmvJrnChgText;
    @Expose(serialize = true, deserialize = true)
    private String minimizedSpecificData; // JOMINESD
    @Expose(serialize = true, deserialize = true)
    private String minimizedSpecificDataText;
    @Expose(serialize = true, deserialize = true)
    private byte[] specificData; // JOESD
    @Expose(serialize = true, deserialize = true)
    private String stringSpecificData; // JOESD (String)
    @Expose(serialize = true, deserialize = true)
    private String programAspDevice; // JOPGMDEV
    @Expose(serialize = true, deserialize = true)
    private long programAsp; // JOPGMASP
    @Expose(serialize = true, deserialize = true)
    private String objectIndicator; // JOOBJIND
    @Expose(serialize = true, deserialize = true)
    private String objectIndicatorText;
    @Expose(serialize = true, deserialize = true)
    private String systemSequenceNumber; // JOSYSSEQ
    @Expose(serialize = true, deserialize = true)
    private String receiver; // JORCV
    @Expose(serialize = true, deserialize = true)
    private String receiverLibrary; // JORCVLIB
    @Expose(serialize = true, deserialize = true)
    private String receiverAspDevice; // JORCVDEV
    @Expose(serialize = true, deserialize = true)
    private int receiverAsp; // JORCVASP
    @Expose(serialize = true, deserialize = true)
    private int armNumber; // JOARM
    @Expose(serialize = true, deserialize = true)
    private String threadId; // JOTHDX
    @Expose(serialize = true, deserialize = true)
    private String addressFamily; // JOADF
    @Expose(serialize = true, deserialize = true)
    private String addressFamilyText;
    @Expose(serialize = true, deserialize = true)
    private int remotePort; // JORPORT
    @Expose(serialize = true, deserialize = true)
    private String remoteAddress; // JORADR
    @Expose(serialize = true, deserialize = true)
    private String logicalUnitOfWork; // JOLUW
    @Expose(serialize = true, deserialize = true)
    private String transactionIdentifier; // JOXID
    @Expose(serialize = true, deserialize = true)
    private String objectType; // JOOBJTYP
    @Expose(serialize = true, deserialize = true)
    private String fileTypeIndicator; // JOFILTYP
    @Expose(serialize = true, deserialize = true)
    private String fileTypeIndicatorText;
    @Expose(serialize = true, deserialize = true)
    private long nestedCommitLevel; // JOCMTLVL
    @Expose(serialize = true, deserialize = true)
    private byte[] nullIndicators; // JONVI

    // Transient values, set on demand
    private OutputFile outputFile;
    private transient String qualifiedObjectName;
    private transient String stringSpecificDataForUI;

    // Transient values
    private transient IDatatypeConverterDelegate datatypeConverterDelegate;
    private transient DecimalFormat bin8Formatter;
    private transient DecimalFormat nestedCommitLevelFormatter;
    private transient SimpleDateFormat dateFormatter;
    private transient SimpleDateFormat timeFormatter;
    private transient SimpleDateFormat timestampFormatter;
    private transient Calendar calendar;

    /**
     * Produces a new JournalEntry object. This constructor is used by the Json
     * importer, when loading journal entries from a Json file.
     */
    public JournalEntry() {
        this(null);
    }

    /**
     * Produces a new JournalEntry. This constructor is used when loading
     * journal entries from a journal or a DSPJRN output file.
     * 
     * @param outputFile
     */
    public JournalEntry(OutputFile outputFile) {

        if (outputFile != null) {
            this.connectionName = outputFile.getConnectionName();
            this.outputFileName = outputFile.getFileName();
            this.outputFileLibraryName = outputFile.getLibraryName();
            this.outputFileMemberName = outputFile.getMemberName();
        }

        // Transient values, set on demand
        this.qualifiedObjectName = null;
        this.stringSpecificDataForUI = null;

        // Transient values
        this.datatypeConverterDelegate = new DatatypeConverterDelegate();
        this.bin8Formatter = new DecimalFormat("00000000000000000000");
        this.nestedCommitLevelFormatter = new DecimalFormat("0000000");

        this.dateFormatter = biz.isphere.core.preferences.Preferences.getInstance().getDateFormatter();
        // this.dateFormatter = new SimpleDateFormat("dd.MM.yyyy"); //$NON-NLS-1$
        this.timeFormatter = biz.isphere.core.preferences.Preferences.getInstance().getTimeFormatter();
        // this.timeFormatter = // new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
        this.timestampFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS"); //$NON-NLS-1$
        this.calendar = Calendar.getInstance();

    }

    public OutputFile getOutputFile() {
        if (outputFile == null) {
            outputFile = new OutputFile(this.connectionName, this.outputFileLibraryName, this.outputFileName, this.outputFileMemberName);
        }
        return outputFile;
    }

    public boolean hasNullIndicatorTable() throws Exception {
        return MetaDataCache.getInstance().retrieveMetaData(outputFile).hasColumn(ColumnsDAO.JONVI);
    }

    public boolean isRecordEntryType() {

        JournalEntryType journalEntryType = JournalEntryType.find(entryType);
        if (journalEntryType != null && journalEntryType.isChildOf(JournalCode.R)) {
            return true;
        }

        return false;
    }

    public static HashMap<String, Integer> getColumnMapping() {
        return columnMappings;
    }

    public static List<ContentAssistProposal> getContentAssistProposals() {
        return proposals;
    }

    public static Comparable[] getSampleRow() {

        long now = new java.util.Date().getTime();

        JournalEntry journalEntry = new JournalEntry(null);
        journalEntry.setJournalCode("R");
        journalEntry.setEntryType("DL");
        journalEntry.setJobName("TRADDATZA1");
        journalEntry.setJobUserName("RADDATZ");
        journalEntry.setJobNumber(939207);
        journalEntry.setObjectLibrary("ISPHEREDVP");
        journalEntry.setObjectName("TYPES_SQL");
        journalEntry.setMemberName("TYPES_SQL");
        journalEntry.setTimestamp(new java.sql.Timestamp(now));
        journalEntry.setProgramName("CRTTSTDTA");
        journalEntry.setProgramLibrary("*OMITTED");
        journalEntry.setObjectType("*QDDS");
        journalEntry.setFileTypeIndicator("");
        journalEntry.setSystemName("GFD400");
        journalEntry.setReceiverName("JRN003");
        journalEntry.setReceiverLibraryName("ISPHEREDVP");
        journalEntry.setUserProfile("RADDATZ");

        return journalEntry.getRow();
    }

    public Comparable[] getRow() {

        Comparable[] row = new Comparable[columnMappings.size()];

        row[JOCODE] = getJournalCode();
        row[JOENTT] = getEntryType();
        row[JOJOB] = getJobName();
        row[JOUSER] = getJobUserName();
        row[JONBR] = getJobNumber();
        row[JOLIB] = getObjectLibrary();
        row[JOOBJ] = getObjectName();
        row[JOMBR] = getMemberName();
        row[JODATE] = new java.sql.Date(getDate().getTime());
        row[JOTIME] = getTime();
        row[JOTSTP] = getTimestamp();
        row[JOPGM] = getProgramName();
        row[JOPGMLIB] = getProgramLibrary();
        row[JOOBJTYP] = getObjectType();
        row[JOFILTYP] = getFileTypeIndicator();
        row[JOSYNM] = getSystemName();
        row[JORCV] = getReceiver();
        row[JORCVLIB] = getReceiverLibrary();
        row[JOUSPF] = getUserProfile();
        row[JOSEQN] = getSequenceNumber();
        row[JOCCID] = getCommitmentCycle();

        return row;
    }

    // //////////////////////////////////////////////////////////
    // / Getters / Setters
    // //////////////////////////////////////////////////////////

    public String getConnectionName() {
        return connectionName;
    }

    // public void setConnectionName(String connectionName) {
    // this.connectionName = connectionName;
    // }

    public String getKey() {
        return Messages.bind(Messages.Journal_RecordNum, new Object[] { getConnectionName(), getOutFileLibrary(), getOutFileName(), getId() });
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOutFileName() {
        return getOutputFile().getFileName();
    }

    public String getOutFileLibrary() {
        return getOutputFile().getLibraryName();
    }

    // //////////////////////////////////////////////////////////
    // / Getters / Setters of journal entry
    // //////////////////////////////////////////////////////////

    /**
     * Returns the 'Length of Entry'.
     * <p>
     * Date type in journal output file: ZONED(5 0)
     * 
     * @return value of field 'JOENTL'.
     */
    public int getEntryLength() {
        return entryLength;
    }

    public void setEntryLength(int largoEntrada) {
        this.entryLength = largoEntrada;
    }

    /**
     * Returns the 'Sequence number'.
     * <p>
     * Date type in journal output file: CHAR(20)
     * 
     * @return value of field 'JOSEQN'.
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(BigInteger sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Returns the 'Journal Code'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOCODE'.
     */
    public String getJournalCode() {
        return journalCode;
    }

    public void setJournalCode(String journalCode) {
        this.journalCode = journalCode.trim();
    }

    /**
     * Returns the 'Entry Type'.
     * <p>
     * Date type in journal output file: CHAR(2)
     * 
     * @return value of field 'JOENTT'.
     */
    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType.trim();
    }

    /**
     * Returns the time portion of field 'Timestamp of Entry' or 'Date of
     * entry', depending on the type of the journal output file. That has been
     * changed with output file type *TYPE3.
     * <p>
     * Date type in journal output file: TIMESTAMP(26) / CHAR(6)
     * 
     * @return value of field 'JOTSTP' or 'JODATE'.
     */
    public java.sql.Date getDate() {
        return date;
    }

    private void setDate(java.sql.Timestamp timestamp) {

        calendar.clear();
        calendar.setTime(timestamp);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        this.date = new java.sql.Date(calendar.getTimeInMillis());
    }

    public void setDateAndTime(String date, int time, int dateFormat, Character dateSeparator, Character timeSeparator) {
        setTimestamp(JournalEntryDelegate.getDate(date, dateFormat, dateSeparator), JournalEntryDelegate.getTime(time, timeSeparator));
    }

    /**
     * Returns the time portion of field 'Timestamp of Entry' or 'Time of
     * entry', depending on the type of the journal output file. That has been
     * changed with output file type *TYPE3.
     * <p>
     * Date type in journal output file: TIMESTAMP(26) / ZONED(6 0)
     * 
     * @return value of field 'JOTSTP' or 'JOTIME'.
     */
    public java.sql.Time getTime() {
        return time;
    }

    private void setTime(java.sql.Timestamp timestamp) {

        calendar.clear();
        calendar.setTime(timestamp);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, 1970);

        this.time = new java.sql.Time(calendar.getTimeInMillis());
    }

    public void setTimestamp(java.sql.Date date, java.sql.Time time) {

        calendar.clear();
        calendar.setTime(time);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int milliseconds = calendar.get(Calendar.MILLISECOND);

        calendar.clear();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, milliseconds);

        setTimestamp(new java.sql.Timestamp(calendar.getTimeInMillis()));
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {

        this.timestamp = timestamp;

        setDate(timestamp);
        setTime(timestamp);
    }

    /**
     * Returns the 'Name of Job'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOJOB'.
     */
    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName.trim();
    }

    /**
     * Returns the 'Name of User'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOUSER'.
     */
    public String getJobUserName() {
        return jobUserName;
    }

    public void setJobUserName(String userName) {
        this.jobUserName = userName.trim();
    }

    /**
     * Returns the 'Job Number'.
     * <p>
     * Date type in journal output file: ZONED(6 0)
     * 
     * @return value of field 'JONBR'.
     */
    public int getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = IntHelper.tryParseInt(jobNumber, -1);
    }

    /**
     * Returns the 'Name of Program'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOPGM'.
     */
    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName.trim();
    }

    /**
     * Returns the 'Program Library'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOPGMLIB'.
     * @since *TYPE5
     */
    public String getProgramLibrary() {
        return programLibrary;
    }

    public void setProgramLibrary(String programLibrary) {
        this.programLibrary = programLibrary.trim();
    }

    /**
     * Returns the 'Program ASP Device'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOPGMDEV'.
     * @since *TYPE5
     */
    public String getProgramAspDevice() {
        return programAspDevice;
    }

    public void setProgramLibraryAspDeviceName(String programAspDevice) {
        this.programAspDevice = programAspDevice.trim();
    }

    /**
     * Returns the 'Program ASP'.
     * <p>
     * Date type in journal output file: ZONED(5 0)
     * 
     * @return value of field 'JOPGMASP'.
     * @since *TYPE5
     */
    public long getProgramAsp() {
        return programAsp;
    }

    public void setProgramLibraryAspNumber(long programAsp) {
        this.programAsp = programAsp;
    }

    /**
     * Returns the 'Name of Object'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOOBJ'.
     */
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName.trim();
        this.qualifiedObjectName = null;
    }

    /**
     * Returns the 'Object Library'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOLIB'.
     */
    public String getObjectLibrary() {
        return objectLibrary;
    }

    public void setObjectLibrary(String objectLibrary) {
        this.objectLibrary = objectLibrary.trim();
        this.qualifiedObjectName = null;
    }

    /**
     * Returns the 'Name of Member'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOMBR'.
     */
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = getValueChecked(memberName);
        this.qualifiedObjectName = null;
    }

    /**
     * Returns the 'Count or relative record number changed'.
     * <p>
     * Date type in journal output file: CHAR(20)
     * 
     * @return value of field 'JOCTRR'.
     */
    public BigInteger getCountRrn() {
        return countRrn;
    }

    public void setCountRrn(BigInteger countRrn) {
        this.countRrn = countRrn;
    }

    /**
     * Returns the 'Flag'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOFLAG'.
     */
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag.trim();
    }

    /**
     * Returns the 'Commit cycle identifier'.
     * <p>
     * Date type in journal output file: CHAR(20)
     * 
     * @return value of field 'JOCCID'.
     */
    public BigInteger getCommitmentCycle() {
        return commitmentCycle;
    }

    public void setCommitmentCycle(BigInteger commitmentCycle) {
        this.commitmentCycle = commitmentCycle;
    }

    /**
     * Returns the 'User Profile'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOUSPF'.
     * @since *TYPE2
     */
    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile.trim();
    }

    /**
     * Returns the 'System Name'.
     * <p>
     * Date type in journal output file: CHAR(8)
     * 
     * @return value of field 'JOSYNM'.
     * @since *TYPE2
     */
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName.trim();
    }

    /**
     * Returns the 'Journal Identifier'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JOJID'.
     * @since *TYPE4
     */
    public String getJournalID() {
        return journalID;
    }

    public void setJournalID(String journalID) {
        this.journalID = journalID.trim();
    }

    /**
     * Returns the 'Referential Constraint'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JORCST'.
     * @since *TYPE4
     */
    public String getReferentialConstraint() {
        return referentialConstraint;
    }

    public String getReferentialConstraintText() {
        if (referentialConstraintText == null) {
            if ("0".equals(referentialConstraint)) {
                referentialConstraintText = "no";
            } else if ("1".equals(referentialConstraint)) {
                referentialConstraintText = "yes";
            } else {
                referentialConstraintText = referentialConstraint;
            }
        }

        return referentialConstraintText;
    }

    public void setReferentialConstraint(boolean isReferentialConstraint) {
        setReferentialConstraint(toString(isReferentialConstraint));
    }

    public void setReferentialConstraint(String referentialConstraint) {
        this.referentialConstraint = referentialConstraint.trim();
        this.referentialConstraintText = null;
    }

    /**
     * Returns the 'Trigger'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOTGR'.
     * @since *TYPE4
     */
    public String getTrigger() {
        return trigger;
    }

    public String getTriggerText() {
        if (triggerText == null) {
            if ("0".equals(trigger)) {
                triggerText = "no";
            } else if ("1".equals(trigger)) {
                triggerText = "yes";
            } else {
                triggerText = trigger;
            }
        }

        return triggerText;
    }

    public void setTrigger(boolean isTrigger) {
        setTrigger(toString(isTrigger));
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger.trim();
        this.triggerText = null;
    }

    /**
     * Returns the 'Incomplete Data'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOINCDAT'.
     */
    public String getIncompleteData() {
        return incompleteData;
    }

    public String getIncompleteDataText() {
        if (incompleteDataText == null) {
            if ("0".equals(incompleteData)) {
                incompleteDataText = "no";
            } else if ("1".equals(incompleteData)) {
                incompleteDataText = "yes";
            } else {
                incompleteDataText = incompleteData;
            }
        }

        return incompleteDataText;
    }

    public void setIncompleteData(boolean isIncompleteData) {
        setIncompleteData(toString(isIncompleteData));
    }

    public void setIncompleteData(String incompleteData) {
        this.incompleteData = incompleteData.trim();
        this.incompleteDataText = null;
    }

    /**
     * Returns the 'Ignored by APY/RMVJRNCHG'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOIGNAPY'.
     * @since *TYPE4
     */
    public String getIgnoredByApyRmvJrnChg() {
        return apyRmvJrnChg;
    }

    public String getIgnoredByApyRmvJrnChgText() {
        if (apyRmvJrnChgText == null) {
            if ("0".equals(apyRmvJrnChg)) {
                apyRmvJrnChgText = "no";
            } else if ("1".equals(apyRmvJrnChg)) {
                apyRmvJrnChgText = "yes";
            } else {
                apyRmvJrnChgText = apyRmvJrnChg;
            }
        }

        return apyRmvJrnChgText;
    }

    public void setIgnoredByApyRmvJrnChg(boolean isApyRmvJrnChg) {
        setIgnoredByApyRmvJrnChg(toString(isApyRmvJrnChg));
    }

    public void setIgnoredByApyRmvJrnChg(String apyRmvJrnChg) {
        this.apyRmvJrnChg = apyRmvJrnChg.trim();
        this.apyRmvJrnChgText = null;
    }

    /**
     * Returns the 'Minimized Entry Specific Data'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOMINESD'.
     */
    public String getMinimizedSpecificData() {
        return minimizedSpecificData;
    }

    public String getMinimizedSpecificDataText() {
        if (minimizedSpecificDataText == null) {
            if ("0".equals(minimizedSpecificData)) {
                minimizedSpecificDataText = "no";
            } else if ("1".equals(minimizedSpecificData)) {
                minimizedSpecificDataText = "minimized";
            } else if ("2".equals(minimizedSpecificData)) {
                minimizedSpecificDataText = "field boundaries";
            } else {
                minimizedSpecificDataText = minimizedSpecificData;
            }
        }

        return minimizedSpecificDataText;
    }

    public void setMinimizedSpecificData(String minimizedSpecificData) {
        this.minimizedSpecificData = minimizedSpecificData.trim();
        this.minimizedSpecificDataText = null;
    }

    public void setMinimizedSpecificData(boolean minimizedSpecificData) {
        if (minimizedSpecificData) {
            setMinimizedSpecificData("1");
        } else {
            setMinimizedSpecificData("0");
        }
    }

    /**
     * Returns the 'Object Name Indicator'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * <p>
     * Either the journal entry has no object information or the object
     * information in the journal entry header does not necessarily reflect the
     * name of the object at the time the journal entry was deposited into the
     * journal.<br>
     * <b>Note:</b> This value is returned only when retrieving journal entries
     * from a journal receiver that was attached to a journal prior to V4R2M0.
     * 
     * @return value of field 'JOOBJIND'.
     * @since *TYPE5
     */
    public String getObjectNameIndicator() {
        return objectIndicator;
    }

    public String getObjectIndicatorText() {
        if (objectIndicatorText == null) {
            if ("0".equals(objectIndicator)) {
                objectIndicatorText = "-/-";
            } else if ("1".equals(objectIndicator)) {
                objectIndicatorText = "accurate";
            } else if ("2".equals(objectIndicator)) {
                objectIndicatorText = "uncertain";
            } else {
                objectIndicatorText = objectIndicator;
            }
        }

        return objectIndicatorText;
    }

    public void setObjectNameIndicator(String objectIndicator) {
        this.objectIndicator = objectIndicator.trim();
        this.objectIndicatorText = null;
    }

    /**
     * Returns the 'System Sequence Number'.
     * <p>
     * Date type in journal output file: CHAR(20)
     * 
     * @return value of field 'JOSYSSEQ'.
     * @since *TYPE5
     */
    public String getSystemSequenceNumber() {
        return systemSequenceNumber;
    }

    public void setSystemSequenceNumber(BigInteger systemSequenceNumber) {
        String tSystemSequenceNumber = bin8Formatter.format(systemSequenceNumber);
        this.systemSequenceNumber = tSystemSequenceNumber;
    }

    public void setSystemSequenceNumber(String systemSequenceNumber) {
        this.systemSequenceNumber = systemSequenceNumber.trim();
    }

    /**
     * Returns the 'Receiver'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JORCV'.
     * @since *TYPE5
     */
    public String getReceiver() {
        return receiver;
    }

    public void setReceiverName(String receiver) {
        this.receiver = receiver.trim();
    }

    /**
     * Returns the 'Receiver Library'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JORCVLIB'.
     * @since *TYPE5
     */
    public String getReceiverLibrary() {
        return receiverLibrary;
    }

    public void setReceiverLibraryName(String receiverLibrary) {
        this.receiverLibrary = receiverLibrary.trim();
    }

    /**
     * Returns the 'Receiver ASP Device'.
     * <p>
     * Date type in journal output file: CHAR(10)
     * 
     * @return value of field 'JORCVDEV'.
     * @since *TYPE5
     */
    public String getReceiverAspDevice() {
        return receiverAspDevice;
    }

    public void setReceiverLibraryASPDeviceName(String receiverAspDevice) {
        this.receiverAspDevice = receiverAspDevice.trim();
    }

    /**
     * Returns the 'Receiver ASP'.
     * <p>
     * Date type in journal output file: ZONED(5 0)
     * 
     * @return value of field 'JORCVASP'.
     * @since *TYPE5
     */
    public int getReceiverAsp() {
        return receiverAsp;
    }

    public void setReceiverLibraryASPNumber(int receiverAsp) {
        this.receiverAsp = receiverAsp;
    }

    /**
     * Returns the 'ARM Number'.
     * <p>
     * Date type in journal output file: ZONED(5 0)
     * 
     * @return value of field 'JOARM'.
     * @since *TYPE5
     */
    public int getArmNumber() {
        return armNumber;
    }

    public void setArmNumber(int armNumber) {
        this.armNumber = armNumber;
    }

    /**
     * Returns the 'Thread ID Hex'.
     * <p>
     * Date type in journal output file: CHAR(16)
     * 
     * @return value of field 'JOTHDX'.
     * @since *TYPE5
     */
    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId.trim();
    }

    /**
     * Returns the 'Address Family'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOADF'.
     * @since *TYPE5
     */
    public String getAddressFamily() {
        return addressFamily;
    }

    public String getAddressFamilyText() {
        if (addressFamilyText == null) {
            if (ADDRESS_FAMILY_UNKNOWN.equals(addressFamily)) {
                addressFamilyText = "";
            } else if (ADDRESS_FAMILY_IPV4.equals(addressFamily)) {
                addressFamilyText = "IPv4";
            } else if (ADDRESS_FAMILY_IPV6.equals(addressFamily)) {
                addressFamilyText = "IPv6";
            } else {
                addressFamilyText = addressFamily;
            }
        }

        return addressFamilyText;
    }

    public void setAddressFamily(String addressFamily) {
        this.addressFamily = addressFamily.trim();
        this.addressFamilyText = null;
    }

    /**
     * Returns the 'Remote Port'.
     * <p>
     * Date type in journal output file: ZONED(5 0)
     * 
     * @return value of field 'JORPORT'.
     * @since *TYPE5
     */
    public int getRemotePort() {
        return remotePort;
    }

    public String getRemotePortText() {
        if (remotePort > 0) {
            return Integer.toString(remotePort);
        } else {
            return "";
        }
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * Returns the 'Remote Address'.
     * <p>
     * Date type in journal output file: CHAR(46)
     * 
     * @return value of field 'JORADR'.
     * @since *TYPE5
     */
    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress.trim();
    }

    /**
     * Returns the 'Logical Unit of Work'.
     * <p>
     * Date type in journal output file: CHAR(39)
     * 
     * @return value of field 'JOLUW'.
     * @since *TYPE5
     */
    public String getLogicalUnitOfWork() {
        return logicalUnitOfWork;
    }

    public void setLogicalUnitOfWork(String logicalUnitOfWork) {
        this.logicalUnitOfWork = logicalUnitOfWork.trim();
    }

    /**
     * Returns the 'Transaction ID'.
     * <p>
     * Date type in journal output file: CHAR(140)
     * 
     * @return value of field 'JOXID'.
     * @since *TYPE5
     */
    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }

    public void setTransactionIdentifier(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier.trim();
    }

    /**
     * Returns the 'Object Type'.
     * <p>
     * Date type in journal output file: CHAR(7)
     * 
     * @return value of field 'JOOBJTYP'.
     * @since *TYPE5
     */
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType.trim();
    }

    /**
     * Returns the 'File type indicator' that indicates the type of object
     * associated with this entry. ('0' is physical, '1' is logical)
     * <p>
     * Date type in journal output file: CHAR(1)
     * <p>
     * The possible values are:
     * <ul>
     * <li>0 - This entry is not associated with a logical file.</li>
     * <li>1 - This entry is associated with a logical file.</li>
     * </ul>
     * 
     * @return value of field 'JOFILTYP'.
     * @since *TYPE5
     */
    public String getFileTypeIndicator() {
        return fileTypeIndicator;
    }

    public String getFileTypeIndicatorText() {
        if (fileTypeIndicatorText == null) {
            if ("0".equals(fileTypeIndicator)) {
                fileTypeIndicatorText = "PF";
            } else if ("1".equals(fileTypeIndicator)) {
                fileTypeIndicatorText = "LF";
            } else {
                fileTypeIndicatorText = fileTypeIndicator;
            }
        }

        return fileTypeIndicatorText;
    }

    public void setFileTypeIndicator(String fileTypeIndicator) {
        this.fileTypeIndicator = fileTypeIndicator.trim();
        this.fileTypeIndicatorText = null;
    }

    /**
     * Returns the 'Nested Commit Level'.
     * <p>
     * Date type in journal output file: CHAR(7)
     * 
     * @return value of field 'JOCMTLVL'.
     * @since *TYPE5
     */
    public long getNestedCommitLevel() {
        return nestedCommitLevel;
    }

    public void setNestedCommitLevel(long nestedCommitLevel) {
        this.nestedCommitLevel = nestedCommitLevel;
    }

    public int getNullTableLength() {

        if (nullIndicators == null) {
            return 0;
        }

        return nullIndicators.length;
    }

    public boolean isNull(int index) {

        if (nullIndicators == null) {
            return false;
        }

        if (index >= nullIndicators.length) {
            return false;
        }

        return nullIndicators[index] == '1';
    }

    public String getNullIndicators() {
        return new String(nullIndicators);
    }

    public void setNullIndicators(byte[] nullIndicators) {
        this.nullIndicators = nullIndicators;
    }

    /**
     * Returns the string representation of field 'Entry Specific Data'.
     * 
     * @return value of field 'JOESD'.
     */
    public String getStringSpecificData() {
        return stringSpecificData;
    }

    /**
     * Returns the 'Entry Specific Data'.
     * 
     * @return value of field 'JOESD'.
     */
    public int getSpecificDataLength() {
        return specificData.length;
    }

    public byte[] getSpecificData(int recordLength) {

        if (recordLength > specificData.length) {
            byte[] recordData = new byte[recordLength];
            System.arraycopy(specificData, 0, recordData, 0, specificData.length);
            return recordData;
        }

        return specificData;
    }

    public void setStringSpecificData(byte[] specificData) {

        AS400Text text = new AS400Text(specificData.length, Preferences.getInstance().getJournalEntryCcsid());
        this.stringSpecificData = StringHelper.trimR((String)text.toObject(specificData));
    }

    public void setStringSpecificData(String specificData) {

        byte[] bytes = datatypeConverterDelegate.parseHexBinary(specificData);
        setStringSpecificData(bytes);
    }

    public void setSpecificData(byte[] specificData) {
        this.specificData = specificData;
    }

    public String getValueForUi(String name) {

        String data = "?"; //$NON-NLS-1$

        if (ColumnsDAO.RRN_OUTPUT_FILE.equals(name)) {
            return Integer.toString(getId()).trim();
        } else if (ColumnsDAO.JOENTL.equals(name)) {
            return Integer.toString(getEntryLength());
        } else if (ColumnsDAO.JOSEQN.equals(name)) {
            return toString(getSequenceNumber());
        } else if (ColumnsDAO.JOCODE.equals(name)) {
            return getJournalCode();
        } else if (ColumnsDAO.JOENTT.equals(name)) {
            return getEntryType();
        } else if (ColumnsDAO.JOTSTP.equals(name)) {
            java.sql.Timestamp timestamp = getTimestamp();
            if (timestamp == null) {
                return ""; //$NON-NLS-1$
            }
            return timestampFormatter.format(timestamp);
        } else if (ColumnsDAO.JODATE.equals(name)) {
            java.sql.Date date = getDate();
            if (date == null) {
                return ""; //$NON-NLS-1$
            }
            return dateFormatter.format(date);
        } else if (ColumnsDAO.JOTIME.equals(name)) {
            java.sql.Time time = getTime();
            if (time == null) {
                return ""; //$NON-NLS-1$
            }
            return timeFormatter.format(time);
        } else if (ColumnsDAO.JOJOB.equals(name)) {
            return getJobName();
        } else if (ColumnsDAO.JOUSER.equals(name)) {
            return getJobUserName();
        } else if (ColumnsDAO.JONBR.equals(name)) {
            return Integer.toString(getJobNumber());
        } else if (ColumnsDAO.JOPGM.equals(name)) {
            return getProgramName();
        } else if (ColumnsDAO.JOPGMLIB.equals(name)) {
            return getProgramLibrary();
        } else if (ColumnsDAO.JOPGMDEV.equals(name)) {
            return getProgramAspDevice();
        } else if (ColumnsDAO.JOPGMASP.equals(name)) {
            return Long.toString(getProgramAsp());
        } else if (ColumnsDAO.JOOBJ.equals(name)) {
            return getObjectName();
        } else if (ColumnsDAO.JOLIB.equals(name)) {
            return getObjectLibrary();
        } else if (ColumnsDAO.JOMBR.equals(name)) {
            return getMemberName();
        } else if (ColumnsDAO.JOCTRR.equals(name)) {
            return toString(getCountRrn());
        } else if (ColumnsDAO.JOFLAG.equals(name)) {
            return getFlag();
        } else if (ColumnsDAO.JOCCID.equals(name)) {
            return toString(getCommitmentCycle());
        } else if (ColumnsDAO.JOUSPF.equals(name)) {
            return getUserProfile();
        } else if (ColumnsDAO.JOSYNM.equals(name)) {
            return getSystemName();
        } else if (ColumnsDAO.JOJID.equals(name)) {
            return getJournalID();
        } else if (ColumnsDAO.JORCST.equals(name)) {
            return getReferentialConstraintText();
        } else if (ColumnsDAO.JOTGR.equals(name)) {
            return getTriggerText();
        } else if (ColumnsDAO.JOINCDAT.equals(name)) {
            return getIncompleteDataText();
        } else if (ColumnsDAO.JOIGNAPY.equals(name)) {
            return getIgnoredByApyRmvJrnChgText();
        } else if (ColumnsDAO.JOMINESD.equals(name)) {
            return getMinimizedSpecificDataText();
        } else if (ColumnsDAO.JOOBJIND.equals(name)) {
            return getObjectIndicatorText();
        } else if (ColumnsDAO.JOSYSSEQ.equals(name)) {
            return getSystemSequenceNumber();
        } else if (ColumnsDAO.JORCV.equals(name)) {
            return getReceiver();
        } else if (ColumnsDAO.JORCVLIB.equals(name)) {
            return getReceiverLibrary();
        } else if (ColumnsDAO.JORCVDEV.equals(name)) {
            return getReceiverAspDevice();
        } else if (ColumnsDAO.JORCVASP.equals(name)) {
            return Integer.toString(getReceiverAsp());
        } else if (ColumnsDAO.JOARM.equals(name)) {
            return Integer.toString(getArmNumber());
        } else if (ColumnsDAO.JOTHDX.equals(name)) {
            return getThreadId();
        } else if (ColumnsDAO.JOADF.equals(name)) {
            return getAddressFamilyText();
        } else if (ColumnsDAO.JORPORT.equals(name)) {
            return getRemotePortText();
        } else if (ColumnsDAO.JORADR.equals(name)) {
            return getRemoteAddress();
        } else if (ColumnsDAO.JOLUW.equals(name)) {
            return getLogicalUnitOfWork();
        } else if (ColumnsDAO.JOXID.equals(name)) {
            return getTransactionIdentifier();
        } else if (ColumnsDAO.JOOBJTYP.equals(name)) {
            return getObjectType();
        } else if (ColumnsDAO.JOFILTYP.equals(name)) {
            return getFileTypeIndicatorText();
        } else if (ColumnsDAO.JOCMTLVL.equals(name)) {
            return toStringNestedCommitLevel(getNestedCommitLevel());
        } else if (ColumnsDAO.JONVI.equals(name)) {
            return getNullIndicators();
        } else if (ColumnsDAO.JOESD.equals(name)) {
            if (stringSpecificDataForUI == null) {
                stringSpecificDataForUI = getStringSpecificData();
                if (stringSpecificDataForUI == null) {
                    return "";
                }

                // For displaying purposes, replace 0x00 with blanks.
                // Otherwise, the string will be truncate by JFace.
                if (stringSpecificDataForUI.lastIndexOf('\0') >= 0) {
                    stringSpecificDataForUI = stringSpecificDataForUI.replace('\0', ' ');
                }

                // Display only the first 250 bytes.
                if (stringSpecificDataForUI.length() > 250) {
                    stringSpecificDataForUI = stringSpecificDataForUI.substring(0, 250) + "..."; //$NON-NLS-1$
                }
            }
            return stringSpecificDataForUI;
        }

        return data;
    }

    private String toString(BigInteger unsignedBin8Value) {
        return bin8Formatter.format(unsignedBin8Value);
    }

    private String toStringNestedCommitLevel(long longValue) {
        return nestedCommitLevelFormatter.format(longValue);
    }

    public String getQualifiedObjectName() {

        if (qualifiedObjectName == null) {

            if (!StringHelper.isNullOrEmpty(memberName)) {
                qualifiedObjectName = String.format("%s/%s(%s)", objectLibrary, objectName, memberName);
            } else {
                qualifiedObjectName = String.format("%s/%s", objectLibrary, objectName);
            }

        }

        return qualifiedObjectName;
    }

    private String getValueChecked(String value) {
        if (value != null) {
            return value.trim();
        }
        return "";
    }

    private java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    private String toString(boolean isTrue) {

        if (isTrue) {
            return "1";
        } else {
            return "0";
        }
    }
}
