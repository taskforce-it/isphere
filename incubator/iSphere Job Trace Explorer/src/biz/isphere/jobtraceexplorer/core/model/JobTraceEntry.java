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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.jobtraceexplorer.core.Messages;

public class JobTraceEntry {

    private static final int QTITIMN = 0;

    private static HashMap<String, Integer> columnMappings;
    static {
        columnMappings = new HashMap<String, Integer>();
        columnMappings.put("QTITIMN", QTITIMN);
    }

    private static List<ContentAssistProposal> proposals;
    static {
        proposals = new LinkedList<ContentAssistProposal>();
        proposals.add(new ContentAssistProposal("QTITIMN", "BIGINT" + " - " + Messages.ColDesc_LongFieldName_QTITIMN));
    }

    private String connectionName;
    private String libraryName;
    private String sessionID;

    private BigInteger nanosSinceStarted;

    // Transient values, set on demand
    private JobTraceSession jobTraceSession;

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

    // //////////////////////////////////////////////////////////
    // / UI specific methods
    // //////////////////////////////////////////////////////////

    public String getValueForUi(String name) {

        return "";
    }
}
