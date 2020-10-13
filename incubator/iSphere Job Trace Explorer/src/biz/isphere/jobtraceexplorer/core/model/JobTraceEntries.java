/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;

public class JobTraceEntries {

    private List<JobTraceEntry> jobTraceEntries;
    private transient List<JobTraceEntry> filteredJobTraceEntries;
    private String filterWhereClause;
    private boolean isOverflow;
    private int numAvailableRows;
    private List<IBMiMessage> messages;
    private boolean isCanceled;
    private HighlightedAttributes highlightedAttributes;

    public JobTraceEntries() {

        this.jobTraceEntries = new LinkedList<JobTraceEntry>();
        this.filteredJobTraceEntries = null;
        this.filterWhereClause = null;
        this.isOverflow = false;
        this.numAvailableRows = -1;
        this.messages = null;
        this.isCanceled = false;
        this.highlightedAttributes = new HighlightedAttributes();
    }

    public void addHighlightedAttribute(HighlightedAttribute attribute) {
        highlightedAttributes.add(attribute);
    }

    public void removeHighlightedAttribute(HighlightedAttribute attribute) {
        highlightedAttributes.remove(attribute);
    }

    public boolean isHighlighted(int index, String value) {
        return highlightedAttributes.isHighlighted(index, value);
    }

    public String getFilterWhereClause() {
        return StringHelper.notNull(filterWhereClause);
    }

    public void setFilterWhereClause(String filterWhereClause) {

        if (this.filterWhereClause == filterWhereClause) {
            return;
        }

        this.filterWhereClause = filterWhereClause;

        if (StringHelper.isNullOrEmpty(this.filterWhereClause)) {
            removeFilter();
        }
    }

    public boolean isFiltered() {

        if (filteredJobTraceEntries != null) {
            return true;
        }

        return false;
    }

    public boolean hasFilterWhereClause() {

        if (!StringHelper.isNullOrEmpty(filterWhereClause)) {
            return true;
        }

        return false;
    }

    public void applyFilter() throws ParseException {

        Date startTime = new Date();

        if (!hasFilterWhereClause()) {
            return;
        }

        HashMap<String, Integer> columnMapping = JobTraceEntry.getColumnMapping();
        RowJEP sqljep = new RowJEP(filterWhereClause);
        sqljep.parseExpression(columnMapping);

        filteredJobTraceEntries = new LinkedList<JobTraceEntry>();

        for (JobTraceEntry jobTraceEntry : jobTraceEntries) {
            Comparable<?>[] row = jobTraceEntry.getRow();
            if ((Boolean)sqljep.getValue(row)) {
                filteredJobTraceEntries.add(jobTraceEntry);
            }
        }

        ISphereJobTraceExplorerCorePlugin.debug("mSecs total: " + timeElapsed(startTime) + ", FILTER-CLAUSE: " + filterWhereClause);
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

        if (isFiltered()) {
            throw new IllegalAccessError("Cannot add entry when filter is active."); //$NON-NLS-1$
        }

        getItems().add(jobTraceEntry);
        jobTraceEntry.setParent(this);
        jobTraceEntry.setId(jobTraceEntries.size());
    }

    public List<JobTraceEntry> getItems() {

        if (isFiltered()) {
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
        jobTraceEntries.clear();
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
