/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.sql.Time;
import java.util.Date;

import biz.isphere.journalexplorer.base.interfaces.IDatatypeConverterDelegate;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.rse.shared.model.DatatypeConverterDelegate;
import biz.isphere.journalexplorer.rse.shared.model.JournalEntryDelegate;

import com.ibm.as400.access.AS400Text;

public class JournalEntry {

    public static final String USER_GENERATED = "U"; //$NON-NLS-1$

    private File outputFile;

    private String connectionName;
    private String outFileName;
    private String outFileLibrary;
    private int id;
    private int entryLength; // JOENTL
    private long sequenceNumber; // JOSEQN
    private String journalCode; // JOCODE
    private String entryType; // JOENTT
    private Date date; // JODATE
    private Time time; // JOTIME
    private String jobName; // JOJOB
    private String jobUserName; // JOUSER
    private int jobNumber; // JONBR
    private String programName; // JOPGM
    private String programLibrary; // JOLIB
    private String objectName; // JOOBJ
    private String objectLibrary; // JOLIB
    private String memberName; // JOMBR
    private int countRrn; // JOCTRR
    private String flag; // JOFLAG
    private int commitmentCycle; // JOCCID
    private String userProfile; // JOUSPF
    private String systemName; // JOSYNM
    private String journalID; // JOJID
    private String referentialConstraint; // JORCST
    private String referentialConstraintText;
    private String trigger; // JOTGR
    private String triggerText;
    private String incompleteData; // JOINCDAT
    private String incompleteDataText;
    private String apyRmvJrnChg; // JOIGNAPY
    private String apyRmvJrnChgText;
    private String minimizedSpecificData; // JOMINESD
    private String minimizedSpecificDataText;
    private byte[] specificData; // JOESD
    private String stringSpecificData; // JOESD (String)
    private String programAspDevice; // JOPGMDEV
    private int programAsp; // JOPGMASP
    private String objectIndicator; // JOOBJIND
    private String objectIndicatorText;
    private String systemSequenceNumber; // JOSYSSEQ
    private String receiver; // JORCV
    private String receiverLibrary; // JORCVLIB
    private String receiverAspDevice; // JORCVDEV
    private int receiverAsp; // JORCVASP
    private int armNumber; // JOARM
    private String threadId; // JOTHDX
    private String addressFamily; // JOADF
    private String addressFamilyText;
    private int remotePort; // JORPORT
    private String remoteAddress; // JORADR
    private String logicalUnitOfWork; // JOLUW
    private String transactionIdentifier; // JOXID
    private String objectType; // JOOBJTYP
    private String fileTypeIndicator; // JOFILTYP
    private String fileTypeIndicatorText;
    private String nestedCommitLevel; // JOCMTLVL
    private byte[] nullIndicators; // JONVI

    private IDatatypeConverterDelegate datatypeConverterDelegate = new DatatypeConverterDelegate();

    public JournalEntry(File outputFile) {
        this.outputFile = outputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public boolean hasNullIndicatorTable() throws Exception {
        return MetaDataCache.INSTANCE.retrieveMetaData(outputFile).hasColumn(ColumnsDAO.JONVI);
    }

    public boolean isRecordEntryType() {

        if (JournalEntryType.find(entryType) != null) {
            return true;
        }

        return false;
    }

    // //////////////////////////////////////////////////////////
    // / Getters / Setters
    // //////////////////////////////////////////////////////////

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getKey() {
        return Messages.bind(Messages.Journal_RecordNum, new Object[] { connectionName, outFileLibrary, outFileName, id });
    }

    public String getQualifiedObjectName() {
        return String.format("%s/%s", objectLibrary, objectName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOutFileName() {
        return outFileName;
    }

    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName.trim();
    }

    public String getOutFileLibrary() {
        return outFileLibrary;
    }

    public void setOutFileLibrary(String outFileLibrary) {
        this.outFileLibrary = outFileLibrary.trim();
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
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
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
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDateAndTime(String date, int time, int dateFormat, Character dateSeparator, Character timeSeparator) {
        setDate(JournalEntryDelegate.getDate(date, dateFormat, dateSeparator));
        setTime(JournalEntryDelegate.getTime(time, timeSeparator));
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
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
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

    public void setProgramAspDevice(String programAspDevice) {
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
    public int getProgramAsp() {
        return programAsp;
    }

    public void setProgramAsp(int programAsp) {
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
        this.memberName = memberName.trim();
    }

    /**
     * Returns the 'Count or relative record number changed'.
     * <p>
     * Date type in journal output file: CHAR(20)
     * 
     * @return value of field 'JOCTRR'.
     */
    public int getCountRrn() {
        return countRrn;
    }

    public void setCountRrn(int countRrn) {
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
    public int getCommitmentCycle() {
        return commitmentCycle;
    }

    public void setCommitmentCycle(int commitmentCycle) {
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

    /**
     * Returns the 'Object Indicator'.
     * <p>
     * Date type in journal output file: CHAR(1)
     * 
     * @return value of field 'JOOBJIND'.
     * @since *TYPE5
     */
    public String getObjectIndicator() {
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

    public void setObjectIndicator(String objectIndicator) {
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

    public void setReceiver(String receiver) {
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

    public void setReceiverLibrary(String receiverLibrary) {
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

    public void setReceiverAspDevice(String receiverAspDevice) {
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

    public void setReceiverAsp(int receiverAsp) {
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

        if (threadId.replaceAll("0", "").trim().length() == 0) {
            return "-/-"; //$NON-NLS-1$
        }

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
            if ("0".equals(addressFamily)) {
                addressFamilyText = "-/-";
            } else if ("4".equals(addressFamily)) {
                addressFamilyText = "IPv4 (" + addressFamily + ")";
            } else if ("6".equals(addressFamily)) {
                addressFamilyText = "IPv6 (" + addressFamily + ")";
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
     * Returns the 'File Type'.
     * <p>
     * Date type in journal output file: CHAR(1)
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
                fileTypeIndicatorText = "PF (" + fileTypeIndicator + ")";
            } else if ("1".equals(fileTypeIndicator)) {
                fileTypeIndicatorText = "LF (" + fileTypeIndicator + ")";
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
    public String getNestedCommitLevel() {
        return nestedCommitLevel;
    }

    public void setNestedCommitLevel(String nestedCommitLevel) {
        this.nestedCommitLevel = nestedCommitLevel.trim();
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

        return nullIndicators[index] == '1';
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

    public void setStringSpecificData(String specificData) {
        AS400Text text;

        byte[] bytes = datatypeConverterDelegate.parseHexBinary(specificData);
        text = new AS400Text(bytes.length, 284);
        this.stringSpecificData = (String)text.toObject(bytes);
    }

    public void setSpecificData(byte[] specificData) {
        this.specificData = specificData;
    }
}
