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
import biz.isphere.journalexplorer.base.interfaces.IJournalEntry;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.rse.shared.model.DatatypeConverterDelegate;
import biz.isphere.journalexplorer.rse.shared.model.JournalEntryDelegate;

import com.ibm.as400.access.AS400Text;

public class JournalEntry implements IJournalEntry {

    private String connectionName;

    private String outFileName;

    private String outFileLibrary;

    private int rrn;

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

    private String objectName; // JOOBJ

    private String objectLibrary; // JOLIB

    private String memberName; // JOMBR

    private int joCtrr;

    private String joFlag;

    private int commitmentCycle; // JOCCID

    private String userProfile; // JOUSPF

    private String systemName; // JOSYNM

    private String journalID; // joJid

    private String referentialConstraint; // joRcst

    private String trigger; // JOTGR

    private String incompleteData; // JOINCDAT

    private String minimizedSpecificData; // JOMINESD

    private byte[] specificData; // JOESD

    private String stringSpecificData; // JOESD (String)

    private IDatatypeConverterDelegate datatypeConverterDelegate = new DatatypeConverterDelegate();

    public JournalEntry() {
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
        return Messages.bind(Messages.Journal_RecordNum, new Object[] { connectionName, outFileLibrary, outFileName, rrn });
    }

    public String getQualifiedObjectName() {
        return String.format("%s/%s", objectLibrary, objectName);
    }

    public int getEntryLength() {
        return entryLength;
    }

    public void setEntryLength(int largoEntrada) {
        this.entryLength = largoEntrada;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getJournalCode() {
        return journalCode;
    }

    public void setJournalCode(String journalCode) {
        this.journalCode = journalCode.trim();
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType.trim();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName.trim();
    }

    public String getJobUserName() {
        return jobUserName;
    }

    public void setJobUserName(String userName) {
        this.jobUserName = userName.trim();
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName.trim();
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName.trim();
    }

    public String getObjectLibrary() {
        return objectLibrary;
    }

    public void setObjectLibrary(String objectLibrary) {
        this.objectLibrary = objectLibrary.trim();
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName.trim();
    }

    public int getJoCtrr() {
        return joCtrr;
    }

    public void setJoCtrr(int joCtrr) {
        this.joCtrr = joCtrr;
    }

    public String getJoFlag() {
        return joFlag;
    }

    public void setJoFlag(String joFlag) {
        this.joFlag = joFlag.trim();
    }

    public int getCommitmentCycle() {
        return commitmentCycle;
    }

    public void setCommitmentCycle(int commitmentCycle) {
        this.commitmentCycle = commitmentCycle;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile.trim();
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName.trim();
    }

    public String getJournalID() {
        return journalID;
    }

    public void setJournalID(String journalID) {
        this.journalID = journalID.trim();
    }

    public String getReferentialConstraint() {
        return referentialConstraint;
    }

    public void setReferentialConstraint(String referentialConstraint) {
        this.referentialConstraint = referentialConstraint.trim();
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger.trim();
    }

    public String getIncompleteData() {
        return incompleteData;
    }

    public void setIncompleteData(String incompleteData) {
        this.incompleteData = incompleteData.trim();
    }

    public String getMinimizedSpecificData() {
        return minimizedSpecificData;
    }

    public void setMinimizedSpecificData(String minimizedSpecificData) {
        this.minimizedSpecificData = minimizedSpecificData.trim();
    }

    public String getStringSpecificData() {
        return stringSpecificData;
    }

    public byte[] getSpecificData() {
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

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getRrn() {
        return rrn;
    }

    public void setRrn(int rrn) {
        this.rrn = rrn;
    }

    public void setDate(String date, int time, int dateFormat, Character dateSeparator, Character timeSeparator) {

        setDate(JournalEntryDelegate.getDate(date, dateFormat, dateSeparator));
        setTime(JournalEntryDelegate.getTime(time, timeSeparator));
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
}
