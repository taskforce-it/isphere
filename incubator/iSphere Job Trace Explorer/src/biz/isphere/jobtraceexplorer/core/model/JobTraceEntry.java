/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.dao.ColumnsDAO;

import com.google.gson.annotations.Expose;

public class JobTraceEntry {

    private static HashMap<String, Integer> columnMappings;
    static {
        columnMappings = new HashMap<String, Integer>();
        columnMappings.put(ColumnsDAO.ID.name(), ColumnsDAO.ID.ordinal());
        columnMappings.put(ColumnsDAO.NANOS_SINE_STARTED.name(), ColumnsDAO.NANOS_SINE_STARTED.ordinal());
        columnMappings.put(ColumnsDAO.TIMESTAMP.name(), ColumnsDAO.TIMESTAMP.ordinal());
        columnMappings.put(ColumnsDAO.PGM_NAME.name(), ColumnsDAO.PGM_NAME.ordinal());
        columnMappings.put(ColumnsDAO.PGM_LIB.name(), ColumnsDAO.PGM_LIB.ordinal());
        columnMappings.put(ColumnsDAO.MOD_NAME.name(), ColumnsDAO.MOD_NAME.ordinal());
        columnMappings.put(ColumnsDAO.MOD_LIB.name(), ColumnsDAO.MOD_LIB.ordinal());
        columnMappings.put(ColumnsDAO.HLL_STMT_NBR.name(), ColumnsDAO.HLL_STMT_NBR.ordinal());
        columnMappings.put(ColumnsDAO.PROC_NAME.name(), ColumnsDAO.PROC_NAME.ordinal());
        columnMappings.put(ColumnsDAO.CALL_LEVEL.name(), ColumnsDAO.CALL_LEVEL.ordinal());
        columnMappings.put(ColumnsDAO.EVENT_SUB_TYPE.name(), ColumnsDAO.EVENT_SUB_TYPE.ordinal());
        columnMappings.put(ColumnsDAO.CALLER_HLL_STMT_NBR.name(), ColumnsDAO.CALLER_HLL_STMT_NBR.ordinal());
        columnMappings.put(ColumnsDAO.CALLER_PROC_NAME.name(), ColumnsDAO.CALLER_PROC_NAME.ordinal());
        columnMappings.put(ColumnsDAO.CALLER_CALL_LEVEL.name(), ColumnsDAO.CALLER_CALL_LEVEL.ordinal());
    }

    private static List<ContentAssistProposal> proposals;
    static {
        // @formatter:off
        proposals = new LinkedList<ContentAssistProposal>();
        proposals.add(new ContentAssistProposal(ColumnsDAO.ID.systemColumnName(), ColumnsDAO.ID.type() + " - " + ColumnsDAO.ID.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.NANOS_SINE_STARTED.name(), ColumnsDAO.NANOS_SINE_STARTED.type() + " - " + ColumnsDAO.NANOS_SINE_STARTED.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.TIMESTAMP.name(), ColumnsDAO.TIMESTAMP.type() + " - " + ColumnsDAO.TIMESTAMP.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.PGM_NAME.name(), ColumnsDAO.PGM_NAME.type() + " - " + ColumnsDAO.PGM_NAME.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.PGM_LIB.name(), ColumnsDAO.PGM_LIB.type() + " - " + ColumnsDAO.PGM_LIB.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.MOD_NAME.name(), ColumnsDAO.MOD_NAME.type() + " - " + ColumnsDAO.MOD_NAME.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.MOD_LIB.name(), ColumnsDAO.MOD_LIB.type() + " - " + ColumnsDAO.MOD_LIB.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.HLL_STMT_NBR.name(), ColumnsDAO.HLL_STMT_NBR.type() + " - " + ColumnsDAO.HLL_STMT_NBR.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.PROC_NAME.name(), ColumnsDAO.PROC_NAME.type() + " - " + ColumnsDAO.PROC_NAME.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.CALL_LEVEL.name(), ColumnsDAO.CALL_LEVEL.type() + " - " + ColumnsDAO.CALL_LEVEL.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.EVENT_SUB_TYPE.name(), ColumnsDAO.EVENT_SUB_TYPE.type() + " - " + ColumnsDAO.EVENT_SUB_TYPE.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.CALLER_HLL_STMT_NBR.name(), ColumnsDAO.CALLER_HLL_STMT_NBR.type() + " - " + ColumnsDAO.CALLER_HLL_STMT_NBR.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.CALLER_PROC_NAME.name(), ColumnsDAO.CALLER_PROC_NAME.type() + " - " + ColumnsDAO.CALLER_PROC_NAME.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.CALLER_CALL_LEVEL.name(), ColumnsDAO.CALLER_CALL_LEVEL.type() + " - " + ColumnsDAO.CALLER_CALL_LEVEL.description()));
        // @formatter:on
    }

    @Expose(serialize = true, deserialize = true)
    private int id;
    @Expose(serialize = true, deserialize = true)
    private BigInteger nanosSinceStarted;
    @Expose(serialize = true, deserialize = true)
    private Timestamp timestamp;
    @Expose(serialize = true, deserialize = true)
    private String programName;
    @Expose(serialize = true, deserialize = true)
    private String programLibrary;
    @Expose(serialize = true, deserialize = true)
    private String moduleName;
    @Expose(serialize = true, deserialize = true)
    private String moduleLibrary;
    @Expose(serialize = true, deserialize = true)
    private int hllStmtNbr;
    @Expose(serialize = true, deserialize = true)
    private String procedureName;
    @Expose(serialize = true, deserialize = true)
    private int callLevel;
    @Expose(serialize = true, deserialize = true)
    private String eventSubType;
    @Expose(serialize = true, deserialize = true)
    private int callerHLLStmtNbr;
    @Expose(serialize = true, deserialize = true)
    private String callerProcedureName;
    @Expose(serialize = true, deserialize = true)
    private int callerCallLevel;

    @Expose(serialize = true, deserialize = true)
    private boolean isHighlighted;
    @Expose(serialize = true, deserialize = true)
    private BigInteger excludedEntriesKey;

    // Transient values, set on demand

    private transient JobTraceEntries parent;
    private transient DecimalFormat bin8Formatter;
    private transient SimpleDateFormat timestampFormatter;

    /**
     * Produces a new TraceEntry. This constructor is used when loading job
     * trace entries from a Job Trace session.
     * 
     * @param outputFile
     */
    public JobTraceEntry() {

        this.bin8Formatter = new DecimalFormat("00000000000000000000");
        this.timestampFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS"); //$NON-NLS-1$
    }

    public static HashMap<String, Integer> getColumnMapping() {
        return columnMappings;
    }

    public static String getColumnName(int index) {

        for (Entry<String, Integer> mapping : columnMappings.entrySet()) {
            if (mapping.getValue().intValue() == index) {
                return mapping.getKey();
            }
        }

        return null;
    }

    public static List<ContentAssistProposal> getContentAssistProposals() {
        return proposals;
    }

    // //////////////////////////////////////////////////////////
    // / SQLJep Getters
    // //////////////////////////////////////////////////////////

    public static Comparable<?>[] getSampleRow() {

        long now = new java.util.Date().getTime();

        JobTraceEntry jobTraceEntry = new JobTraceEntry();
        jobTraceEntry.setId(1);
        jobTraceEntry.setNanosSinceStarted(new BigInteger("9207031548"));
        jobTraceEntry.setTimestamp(new java.sql.Timestamp(now));
        jobTraceEntry.setProgramName("SPLF");
        jobTraceEntry.setProgramLibrary("ISPHEREDVP");
        jobTraceEntry.setModuleName("SPLF");
        jobTraceEntry.setModuleLibrary("ISPHEREDVP");
        jobTraceEntry.setHLLStmtNbr(1256);
        jobTraceEntry.setProcedureName("SPLF_CLEAR");
        jobTraceEntry.setCallLevel(8);
        jobTraceEntry.setEventSubType(ColumnsDAO.EVENT_SUB_TYPE_PRCEXIT);
        jobTraceEntry.setCallerHLLStmtNbr(177);
        jobTraceEntry.setCallerProcedureName("*ccsidConvProc");
        jobTraceEntry.setCallerCallLevel(7);

        return jobTraceEntry.getRow();
    }

    public Comparable<?>[] getRow() {

        Comparable<?>[] row = new Comparable[columnMappings.size()];

        row[ColumnsDAO.ID.ordinal()] = getId();
        row[ColumnsDAO.NANOS_SINE_STARTED.ordinal()] = getNanosSinceStarted();
        row[ColumnsDAO.TIMESTAMP.ordinal()] = getTimestamp();
        row[ColumnsDAO.PGM_NAME.ordinal()] = getProgramName();
        row[ColumnsDAO.PGM_LIB.ordinal()] = getProgramLibrary();
        row[ColumnsDAO.MOD_NAME.ordinal()] = getModuleName();
        row[ColumnsDAO.MOD_LIB.ordinal()] = getModuleLibrary();
        row[ColumnsDAO.HLL_STMT_NBR.ordinal()] = getHLLStmtNbr();
        row[ColumnsDAO.PROC_NAME.ordinal()] = getProcedureName();
        row[ColumnsDAO.CALL_LEVEL.ordinal()] = getCallLevel();
        row[ColumnsDAO.EVENT_SUB_TYPE.ordinal()] = getEventSubType();
        row[ColumnsDAO.CALLER_HLL_STMT_NBR.ordinal()] = getCallerHLLStmtNbr();
        row[ColumnsDAO.CALLER_PROC_NAME.ordinal()] = getCallerProcedureName();
        row[ColumnsDAO.CALLER_CALL_LEVEL.ordinal()] = getCallerCallLevel();

        return row;
    }

    // //////////////////////////////////////////////////////////
    // / Getters / Setters
    // //////////////////////////////////////////////////////////

    public JobTraceEntries getParent() {
        return parent;
    }

    public void setParent(JobTraceEntries parent) {
        this.parent = parent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // //////////////////////////////////////////////////////////
    // / Getters / Setters of job trace entry
    // //////////////////////////////////////////////////////////

    public BigInteger getNanosSinceStarted() {
        return nanosSinceStarted;
    }

    public void setNanosSinceStarted(BigInteger nanosSinceStarted) {
        this.nanosSinceStarted = nanosSinceStarted;
    }

    private java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = nullSave(programName);
    }

    public String getProgramLibrary() {
        return programLibrary;
    }

    public void setProgramLibrary(String programLibrary) {
        this.programLibrary = nullSave(programLibrary);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = nullSave(moduleName);
    }

    public String getModuleLibrary() {
        return moduleLibrary;
    }

    public void setModuleLibrary(String moduleLibrary) {
        this.moduleLibrary = nullSave(moduleLibrary);
    }

    public int getHLLStmtNbr() {
        return hllStmtNbr;
    }

    public void setHLLStmtNbr(int hllStmtNbr) {
        this.hllStmtNbr = hllStmtNbr;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = nullSave(procedureName);
    }

    public int getCallLevel() {
        return callLevel;
    }

    public void setCallLevel(int callLevel) {
        this.callLevel = callLevel;
    }

    public String getEventSubType() {
        return eventSubType;
    }

    public void setEventSubType(String eventSubType) {
        this.eventSubType = nullSave(eventSubType);
    }

    public int getCallerHLLStmtNbr() {
        return callerHLLStmtNbr;
    }

    public void setCallerHLLStmtNbr(int hllStmtNbr) {
        this.callerHLLStmtNbr = hllStmtNbr;
    }

    public String getCallerProcedureName() {
        return callerProcedureName.trim();
    }

    public void setCallerProcedureName(String procedureName) {
        this.callerProcedureName = nullSave(procedureName);
    }

    public int getCallerCallLevel() {
        return callerCallLevel;
    }

    public void setCallerCallLevel(int callerCallLevel) {
        this.callerCallLevel = callerCallLevel;
    }

    // //////////////////////////////////////////////////////////
    // / UI specific methods
    // //////////////////////////////////////////////////////////

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
        if (isExcluded()) {
            getParent().setHighlightedExcludedEntries(nanosSinceStarted, this.isHighlighted);
        }
    }

    public boolean isExcluded() {

        if (excludedEntriesKey != null) {
            return true;
        }

        return false;
    }

    public void setExcludedEntriesKey(BigInteger excludedEntriesKey) {
        this.excludedEntriesKey = excludedEntriesKey;
    }

    public String getValueForUi(int index) {
        return getValueForUi(ColumnsDAO.values()[index]);
    }

    public String getValueForUi(String columnName) {
        return getValueForUi(ColumnsDAO.valueOf(columnName));
    }

    public String getValueForUi(ColumnsDAO columnsDAO) {

        if (ColumnsDAO.ID.equals(columnsDAO)) {
            return toString(getId());
        } else if (ColumnsDAO.NANOS_SINE_STARTED.equals(columnsDAO)) {
            return toString(getNanosSinceStarted());
        } else if (ColumnsDAO.TIMESTAMP.equals(columnsDAO)) {
            return toString(getTimestamp());
        } else if (ColumnsDAO.PGM_NAME.equals(columnsDAO)) {
            return toString(getProgramName());
        } else if (ColumnsDAO.PGM_LIB.equals(columnsDAO)) {
            return toString(getProgramLibrary());
        } else if (ColumnsDAO.MOD_NAME.equals(columnsDAO)) {
            return toString(getModuleName());
        } else if (ColumnsDAO.MOD_LIB.equals(columnsDAO)) {
            return toString(getModuleLibrary());
        } else if (ColumnsDAO.HLL_STMT_NBR.equals(columnsDAO)) {
            return toString(getHLLStmtNbr());
        } else if (ColumnsDAO.PROC_NAME.equals(columnsDAO)) {
            return toString(getProcedureName());
        } else if (ColumnsDAO.CALL_LEVEL.equals(columnsDAO)) {
            return toString(getCallLevel());
        } else if (ColumnsDAO.EVENT_SUB_TYPE.equals(columnsDAO)) {
            return eventSubTypeToExt(getEventSubType());
        } else if (ColumnsDAO.CALLER_HLL_STMT_NBR.equals(columnsDAO)) {
            return toString(getCallerHLLStmtNbr());
        } else if (ColumnsDAO.CALLER_PROC_NAME.equals(columnsDAO)) {
            return toString(getCallerProcedureName());
        } else if (ColumnsDAO.CALLER_CALL_LEVEL.equals(columnsDAO)) {
            return toString(getCallerCallLevel());
        }

        return "?"; //$NON-NLS-1$
    }

    public boolean isProcEntry() {
        return ColumnsDAO.EVENT_SUB_TYPE_PRCENTRY.equals(eventSubType);
    }

    public boolean isProcExit() {
        return ColumnsDAO.EVENT_SUB_TYPE_PRCEXIT.equals(eventSubType);
    }

    private String eventSubTypeToExt(String eventSubType) {
        if (isProcExit()) {
            return Messages.Returned_to;
        } else {
            return Messages.Called_by;
        }
    }

    private String nullSave(String value) {

        if (value == null) {
            return ""; //$NON-NLS-1$
        }

        return value.trim();
    }

    private String toString(BigInteger unsignedBin8Value) {
        return bin8Formatter.format(unsignedBin8Value);
    }

    private String toString(int intValue) {
        return Integer.toString(intValue);
    }

    private String toString(String stringValue) {
        return stringValue;
    }

    private String toString(java.sql.Timestamp timestampValue) {

        if (timestampValue == null) {
            return ""; //$NON-NLS-1$
        }

        return timestampFormatter.format(timestampValue);
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 1;
        result = prime * result + callLevel;
        result = prime * result + callerCallLevel;
        result = prime * result + callerHLLStmtNbr;
        result = prime * result + ((callerProcedureName == null) ? 0 : callerProcedureName.hashCode());
        result = prime * result + ((eventSubType == null) ? 0 : eventSubType.hashCode());
        result = prime * result + hllStmtNbr;
        result = prime * result + id;
        result = prime * result + ((moduleLibrary == null) ? 0 : moduleLibrary.hashCode());
        result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
        result = prime * result + ((nanosSinceStarted == null) ? 0 : nanosSinceStarted.hashCode());
        result = prime * result + ((procedureName == null) ? 0 : procedureName.hashCode());
        result = prime * result + ((programLibrary == null) ? 0 : programLibrary.hashCode());
        result = prime * result + ((programName == null) ? 0 : programName.hashCode());
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JobTraceEntry other = (JobTraceEntry)obj;
        if (callLevel != other.callLevel) return false;
        if (callerCallLevel != other.callerCallLevel) return false;
        if (callerHLLStmtNbr != other.callerHLLStmtNbr) return false;
        if (callerProcedureName == null) {
            if (other.callerProcedureName != null) return false;
        } else if (!callerProcedureName.equals(other.callerProcedureName)) return false;
        if (eventSubType == null) {
            if (other.eventSubType != null) return false;
        } else if (!eventSubType.equals(other.eventSubType)) return false;
        if (hllStmtNbr != other.hllStmtNbr) return false;
        if (id != other.id) return false;
        if (moduleLibrary == null) {
            if (other.moduleLibrary != null) return false;
        } else if (!moduleLibrary.equals(other.moduleLibrary)) return false;
        if (moduleName == null) {
            if (other.moduleName != null) return false;
        } else if (!moduleName.equals(other.moduleName)) return false;
        if (nanosSinceStarted == null) {
            if (other.nanosSinceStarted != null) return false;
        } else if (!nanosSinceStarted.equals(other.nanosSinceStarted)) return false;
        if (procedureName == null) {
            if (other.procedureName != null) return false;
        } else if (!procedureName.equals(other.procedureName)) return false;
        if (programLibrary == null) {
            if (other.programLibrary != null) return false;
        } else if (!programLibrary.equals(other.programLibrary)) return false;
        if (programName == null) {
            if (other.programName != null) return false;
        } else if (!programName.equals(other.programName)) return false;
        if (timestamp == null) {
            if (other.timestamp != null) return false;
        } else if (!timestamp.equals(other.timestamp)) return false;
        return true;
    }
}
