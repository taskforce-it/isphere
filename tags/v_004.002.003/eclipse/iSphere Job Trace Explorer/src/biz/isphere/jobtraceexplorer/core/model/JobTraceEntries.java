/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.model.api.IBMiMessage;

import com.google.gson.annotations.Expose;

public class JobTraceEntries {

    @Expose(serialize = true, deserialize = true)
    private List<JobTraceEntry> jobTraceEntries;
    @Expose(serialize = true, deserialize = true)
    private String filterWhereClause;
    @Expose(serialize = true, deserialize = true)
    private boolean isOverflow;
    @Expose(serialize = true, deserialize = true)
    private int numAvailableRows;
    @Expose(serialize = true, deserialize = true)
    private List<IBMiMessage> messages;
    @Expose(serialize = true, deserialize = true)
    private boolean isCanceled;
    @Expose(serialize = true, deserialize = true)
    private HighlightedAttributes highlightedAttributes;
    @Expose(serialize = true, deserialize = true)
    private ExcludedEntries excludedEntries;

    private transient List<JobTraceEntry> filteredJobTraceEntries;

    public JobTraceEntries() {
        initialize();
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

    public int excludeJobTraceEntries(BigInteger nanosSinceStarted, List<JobTraceEntry> excludedJobTraceEntries) {

        int count = jobTraceEntries.size();

        this.excludedEntries.addAll(nanosSinceStarted, excludedJobTraceEntries);
        this.jobTraceEntries.removeAll(excludedJobTraceEntries);

        count = count - jobTraceEntries.size();

        return count;
    }

    public int includeJobTraceEntries(int index, BigInteger nanonsSinceStarted) {

        int count = jobTraceEntries.size();

        JobTraceEntry[] excludedJobTraceEntries = excludedEntries.getAll(nanonsSinceStarted);
        jobTraceEntries.addAll(index, Arrays.asList(excludedJobTraceEntries));
        excludedEntries.removeAll(nanonsSinceStarted);

        count = jobTraceEntries.size() - count;

        return count;
    }

    public boolean isExcluded(BigInteger nanosSinceStarted) {
        if (excludedEntries == null) {
            return false;
        }
        return excludedEntries.isExcluded(nanosSinceStarted);
    }

    public void setHighlightedExcludedEntries(BigInteger nanosSinceStarted, boolean isHighlighted) {
        excludedEntries.setHighlighted(nanosSinceStarted, isHighlighted);
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

    public void reset() {
        reset(false);
    }

    public void fullReset() {
        reset(true);
    }

    private void reset(boolean fullReset) {

        this.jobTraceEntries = new LinkedList<JobTraceEntry>();
        this.filteredJobTraceEntries = null;
        this.isOverflow = false;
        this.numAvailableRows = -1;
        this.messages = null;
        this.isCanceled = false;

        if (fullReset) {
            this.highlightedAttributes = new HighlightedAttributes();
            this.excludedEntries = new ExcludedEntries();
        }
    }

    private void initialize() {

        reset(true);

        this.filterWhereClause = null;
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
