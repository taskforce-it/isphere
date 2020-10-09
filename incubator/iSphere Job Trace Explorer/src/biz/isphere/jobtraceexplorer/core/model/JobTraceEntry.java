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

package biz.isphere.jobtraceexplorer.core.model;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.jobtraceexplorer.core.model.dao.ColumnsDAO;

public class JobTraceEntry {

    private static final int QTITIMN = 0;

    private static HashMap<String, Integer> columnMappings;
    static {
        columnMappings = new HashMap<String, Integer>();
        columnMappings.put(ColumnsDAO.NANOS_SINE_STARTED.name(), ColumnsDAO.NANOS_SINE_STARTED.ordinal());
        columnMappings.put(ColumnsDAO.TIMESTAMP.name(), ColumnsDAO.TIMESTAMP.ordinal());
        columnMappings.put(ColumnsDAO.PGM_NAME.name(), ColumnsDAO.PGM_NAME.ordinal());
        columnMappings.put(ColumnsDAO.PGM_LIB.name(), ColumnsDAO.PGM_LIB.ordinal());
        columnMappings.put(ColumnsDAO.MODULE_NAME.name(), ColumnsDAO.MODULE_NAME.ordinal());
        columnMappings.put(ColumnsDAO.MODULE_LIBRARY.name(), ColumnsDAO.MODULE_LIBRARY.ordinal());
        columnMappings.put(ColumnsDAO.HLL_STMT_NBR.name(), ColumnsDAO.HLL_STMT_NBR.ordinal());
        columnMappings.put(ColumnsDAO.PROC_NAME.name(), ColumnsDAO.PROC_NAME.ordinal());
        columnMappings.put(ColumnsDAO.CALL_LEVEL.name(), ColumnsDAO.CALL_LEVEL.ordinal());
        columnMappings.put(ColumnsDAO.EVENT_SUB_TYPE.name(), ColumnsDAO.EVENT_SUB_TYPE.ordinal());
        columnMappings.put(ColumnsDAO.CALLER_HLL_STMT_NBR.name(), ColumnsDAO.CALLER_HLL_STMT_NBR.ordinal());
        columnMappings.put(ColumnsDAO.CALLER_PROC_NAME.name(), ColumnsDAO.CALLER_PROC_NAME.ordinal());
    }

    private static List<ContentAssistProposal> proposals;
    static {
        proposals = new LinkedList<ContentAssistProposal>();
        proposals.add(new ContentAssistProposal(ColumnsDAO.NANOS_SINE_STARTED.systemColumnName(), ColumnsDAO.NANOS_SINE_STARTED.type() + " - "
            + ColumnsDAO.NANOS_SINE_STARTED.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.TIMESTAMP.systemColumnName(), ColumnsDAO.TIMESTAMP.type() + " - "
            + ColumnsDAO.TIMESTAMP.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.PGM_NAME.systemColumnName(), ColumnsDAO.PGM_NAME.type() + " - "
            + ColumnsDAO.PGM_NAME.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.PGM_LIB.systemColumnName(), ColumnsDAO.PGM_LIB.type() + " - "
            + ColumnsDAO.PGM_LIB.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.MODULE_NAME.systemColumnName(), ColumnsDAO.MODULE_NAME.type() + " - "
            + ColumnsDAO.MODULE_NAME.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.MODULE_LIBRARY.systemColumnName(), ColumnsDAO.MODULE_LIBRARY.type() + " - "
            + ColumnsDAO.MODULE_LIBRARY.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.HLL_STMT_NBR.systemColumnName(), ColumnsDAO.HLL_STMT_NBR.type() + " - "
            + ColumnsDAO.HLL_STMT_NBR.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.PROC_NAME.systemColumnName(), ColumnsDAO.PROC_NAME.type() + " - "
            + ColumnsDAO.PROC_NAME.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.CALL_LEVEL.systemColumnName(), ColumnsDAO.CALL_LEVEL.type() + " - "
            + ColumnsDAO.CALL_LEVEL.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.EVENT_SUB_TYPE.systemColumnName(), ColumnsDAO.EVENT_SUB_TYPE.type() + " - "
            + ColumnsDAO.EVENT_SUB_TYPE.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.CALLER_HLL_STMT_NBR.systemColumnName(), ColumnsDAO.CALLER_HLL_STMT_NBR.type() + " - "
            + ColumnsDAO.CALLER_HLL_STMT_NBR.description()));
        proposals.add(new ContentAssistProposal(ColumnsDAO.CALLER_PROC_NAME.systemColumnName(), ColumnsDAO.CALLER_PROC_NAME.type() + " - "
            + ColumnsDAO.CALLER_PROC_NAME.description()));
    }

    private String connectionName;
    private String libraryName;
    private String sessionID;

    private BigInteger nanosSinceStarted;
    private Timestamp timestamp;
    private String programName;
    private String programLibrary;
    private String moduleName;
    private String moduleLibrary;
    private int hllStmtNbr;
    private String procedureName;
    private int callLevel;
    private String eventSubType;
    private int callerHLLStmtNbr;
    private String callerProcedureName;

    // Transient values, set on demand
    private JobTraceSession jobTraceSession;
    private transient DecimalFormat bin8Formatter;
    private transient SimpleDateFormat timestampFormatter;

    /**
     * Produces a new TraceEntry. This constructor is used when loading job
     * trace entries from a Job Trace session.
     * 
     * @param outputFile
     */
    public JobTraceEntry(JobTraceSession jobTraceSession) {

        if (jobTraceSession != null) {
            this.connectionName = jobTraceSession.getConnectionName();
            this.libraryName = jobTraceSession.getLibraryName();
            this.sessionID = jobTraceSession.getSessionID();
        }

        this.bin8Formatter = new DecimalFormat("00000000000000000000");
        this.timestampFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS"); //$NON-NLS-1$
    }

    public JobTraceSession getJobTraceSession() {
        if (jobTraceSession == null) {
            jobTraceSession = new JobTraceSession(this.connectionName, this.libraryName, this.sessionID);
        }
        return jobTraceSession;
    }

    public static HashMap<String, Integer> getColumnMapping() {
        return columnMappings;
    }

    public static List<ContentAssistProposal> getContentAssistProposals() {
        return proposals;
    }

    public static Comparable[] getSampleRow() {

        long now = new java.util.Date().getTime();

        JobTraceEntry jobTraceEntry = new JobTraceEntry(null);
        jobTraceEntry.setNanosSinceStarted(new BigInteger("9207031548"));

        return jobTraceEntry.getRow();
    }

    public Comparable[] getRow() {

        Comparable[] row = new Comparable[columnMappings.size()];

        row[QTITIMN] = getNanosSinceStarted();

        return row;
    }

    // //////////////////////////////////////////////////////////
    // / Getters / Setters
    // //////////////////////////////////////////////////////////

    public String getConnectionName() {
        return connectionName;
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
        this.programName = programName.trim();
    }

    public String getProgramLibrary() {
        return programLibrary;
    }

    public void setProgramLibrary(String programLibrary) {
        this.programLibrary = programLibrary.trim();
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName.trim();
    }

    public String getModuleLibrary() {
        return moduleLibrary;
    }

    public void setModuleLibrary(String moduleLibrary) {
        this.moduleLibrary = moduleLibrary.trim();
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
        this.procedureName = procedureName.trim();
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
        this.eventSubType = eventSubType.trim();
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
        this.callerProcedureName = procedureName;
    }

    // //////////////////////////////////////////////////////////
    // / UI specific methods
    // //////////////////////////////////////////////////////////

    public String getValueForUi(ColumnsDAO columnsDAO) {

        boolean isSwitchProcExit = true;

        if (ColumnsDAO.NANOS_SINE_STARTED.equals(columnsDAO)) {
            return toString(getNanosSinceStarted());
        } else if (ColumnsDAO.TIMESTAMP.equals(columnsDAO)) {
            return toString(getTimestamp());
        } else if (ColumnsDAO.PGM_NAME.equals(columnsDAO)) {
            return toString(getProgramName());
        } else if (ColumnsDAO.PGM_LIB.equals(columnsDAO)) {
            return toString(getProgramLibrary());
        } else if (ColumnsDAO.MODULE_NAME.equals(columnsDAO)) {
            return toString(getModuleName());
        } else if (ColumnsDAO.MODULE_LIBRARY.equals(columnsDAO)) {
            return toString(getModuleLibrary());
        } else if (ColumnsDAO.HLL_STMT_NBR.equals(columnsDAO)) {
            if (isProcedureExit(isSwitchProcExit)) {
                return toString(getCallerHLLStmtNbr());
            } else {
                return toString(getHLLStmtNbr());
            }
        } else if (ColumnsDAO.PROC_NAME.equals(columnsDAO)) {
            if (isProcedureExit(isSwitchProcExit)) {
                return toString(getCallerProcedureName());
            } else {
                return toString(getProcedureName());
            }
        } else if (ColumnsDAO.CALL_LEVEL.equals(columnsDAO)) {
            return toString(getCallLevel());
        } else if (ColumnsDAO.EVENT_SUB_TYPE.equals(columnsDAO)) {
            return eventSubTypeToExt(isSwitchProcExit, getEventSubType());
        } else if (ColumnsDAO.MODULE_LIBRARY.equals(columnsDAO)) {
            return toString(getCallerHLLStmtNbr());
        } else if (ColumnsDAO.CALLER_HLL_STMT_NBR.equals(columnsDAO)) {
            if (isProcedureExit(isSwitchProcExit)) {
                return toString(getHLLStmtNbr());
            } else {
                return toString(getCallerHLLStmtNbr());
            }
        } else if (ColumnsDAO.CALLER_PROC_NAME.equals(columnsDAO)) {
            if (isProcedureExit(isSwitchProcExit)) {
                return toString(getProcedureName());
            } else {
                return toString(getCallerProcedureName());
            }
        }

        return "?"; //$NON-NLS-1$
    }

    private String eventSubTypeToExt(boolean isSwitchProcExit, String eventSubType) {
        if (ColumnsDAO.EVENT_SUB_TYPE_PRCEXIT.equals(eventSubType)) {
            if (isSwitchProcExit) {
                return "returned from";
            } else {
                return "returns to";
            }
        } else {
            return "called by";
        }
    }

    private boolean isProcedureExit(boolean enabled) {
        return enabled && ColumnsDAO.EVENT_SUB_TYPE_PRCEXIT.equals(getEventSubType());
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
}
