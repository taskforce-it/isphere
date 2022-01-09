/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.journalexplorer.core.model.api.IBMiMessage;

public class JournalEntries {

    private List<JournalEntry> journalEntries;
    private List<JournalEntry> filteredJournalEntries;
    private boolean isOverflow;
    private int numAvailableRows;
    private List<IBMiMessage> messages;
    private boolean isCanceled;

    public JournalEntries(int initialCapacity) {

        this.journalEntries = new ArrayList<JournalEntry>(initialCapacity);
        this.filteredJournalEntries = null;
        this.isOverflow = false;
        this.numAvailableRows = -1;
    }

    public void applyFilter(String whereClause) throws ParseException {

        Date startTime = new Date();

        if (StringHelper.isNullOrEmpty(whereClause)) {
            removeFilter();
            return;
        }

        HashMap<String, Integer> columnMapping = JournalEntry.getColumnMapping();
        RowJEP sqljep = new RowJEP(whereClause);
        sqljep.parseExpression(columnMapping);

        filteredJournalEntries = new ArrayList<JournalEntry>(journalEntries.size());

        for (JournalEntry journalEntry : journalEntries) {
            Comparable<?>[] row = journalEntry.getRow();
            if ((Boolean)sqljep.getValue(row)) {
                filteredJournalEntries.add(journalEntry);
            }
        }

        // System.out.println("mSecs total: " + timeElapsed(startTime) +
        // ", FILTER-CLAUSE: " + whereClause);
    }

    private long timeElapsed(Date startTime) {
        return (new Date().getTime() - startTime.getTime());
    }

    public void removeFilter() {
        this.filteredJournalEntries = null;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public void add(JournalEntry journalEntry) {

        if (filteredJournalEntries != null) {
            throw new IllegalAccessError("Cannot add entry when filter is active.");
        }

        getItems().add(journalEntry);
    }

    public List<JournalEntry> getItems() {

        if (filteredJournalEntries != null) {
            return filteredJournalEntries;
        } else {
            return journalEntries;
        }
    }

    public JournalEntry getItem(int index) {

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
        return journalEntries.size();
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
