/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;

public class JobTraceEntries {

    private List<JobTraceEntry> jobTraceEntries;

    private boolean isOverflow;

    private int numAvailableRows;

    private List<IBMiMessage> messages;

    private boolean isCanceled;

    private transient List<JobTraceEntry> filteredJobTraceEntries;

    public JobTraceEntries() {
        this(0);
    }

    public JobTraceEntries(int initialCapacity) {

        this.jobTraceEntries = new ArrayList<JobTraceEntry>(initialCapacity);
        this.filteredJobTraceEntries = null;
        this.isOverflow = false;
        this.numAvailableRows = -1;
    }

    public void applyFilter(String whereClause) throws ParseException {

        Date startTime = new Date();

        if (StringHelper.isNullOrEmpty(whereClause)) {
            removeFilter();
            return;
        }

        HashMap<String, Integer> columnMapping = JobTraceEntry.getColumnMapping();
        RowJEP sqljep = new RowJEP(whereClause);
        sqljep.parseExpression(columnMapping);

        filteredJobTraceEntries = new ArrayList<JobTraceEntry>(jobTraceEntries.size());

        for (JobTraceEntry jobTraceEntry : jobTraceEntries) {
            Comparable<?>[] row = jobTraceEntry.getRow();
            if ((Boolean)sqljep.getValue(row)) {
                filteredJobTraceEntries.add(jobTraceEntry);
            }
        }

        // System.out.println("mSecs total: " + timeElapsed(startTime) +
        // ", FILTER-CLAUSE: " + whereClause);
    }

    private long timeElapsed(Date startTime) {
        return (new Date().getTime() - startTime.getTime());
    }

    public void removeFilter() {
        this.filteredJobTraceEntries = null;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public void add(JobTraceEntry jobTraceEntry) {

        if (filteredJobTraceEntries != null) {
            throw new IllegalAccessError("Cannot add entry when filter is active."); //$NON-NLS-1$
        }

        getItems().add(jobTraceEntry);
        jobTraceEntry.setId(jobTraceEntries.size());
    }

    public List<JobTraceEntry> getItems() {

        if (filteredJobTraceEntries != null) {
            return filteredJobTraceEntries;
        } else {
            return jobTraceEntries;
        }
    }

    public JobTraceEntry getItem(int index) {

        return getItems().get(index);
    }

    public boolean isOverflow() {
        return isOverflow;
    }

    public void setOverflow(boolean isOverflow, int numAvailableRows) {

        this.isOverflow = isOverflow;
        this.numAvailableRows = numAvailableRows;
    }

    public int size() {

        return getItems().size();
    }

    public int getNumberOfRowsAvailable() {

        if (isOverflow()) {
            return numAvailableRows;
        } else {
            return getNumberOfRowsDownloaded();
        }
    }

    public int getNumberOfRowsDownloaded() {
        return jobTraceEntries.size();
    }

    public void clear() {

        removeFilter();
        getItems().clear();
    }

    public void setMessages(List<IBMiMessage> messages) {
        this.messages = messages;
    }

    public IBMiMessage[] getMessages() {

        if (messages == null) {
            return new IBMiMessage[0];
        }

        return messages.toArray(new IBMiMessage[messages.size()]);
    }
}
