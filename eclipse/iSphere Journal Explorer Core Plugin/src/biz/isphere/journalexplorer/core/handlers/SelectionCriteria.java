/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biz.isphere.journalexplorer.core.externalapi.ISelectionCriteria;
import biz.isphere.journalexplorer.core.model.JournalEntryType;

public class SelectionCriteria implements ISelectionCriteria {

    private java.sql.Timestamp startDate;
    private java.sql.Timestamp endDate;
    private boolean isRecordsOnly;
    private Set<JournalEntryType> journalEntryTypes;
    int maxItemsToRetrieve;

    public SelectionCriteria() {
        this(null, null, false, -1);
    }

    public SelectionCriteria(java.sql.Timestamp startDate, java.sql.Timestamp endDate, boolean recordsOnly, int maxItemsToRetrieve) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isRecordsOnly = recordsOnly;
        this.maxItemsToRetrieve = maxItemsToRetrieve;
        this.journalEntryTypes = new HashSet<JournalEntryType>();
    }

    public java.sql.Timestamp getFromTime() {
        return startDate;
    }

    public void setStartDate(java.sql.Timestamp startDate) {
        this.startDate = startDate;
    }

    public java.sql.Timestamp getToTime() {
        return endDate;
    }

    public void setEndDate(java.sql.Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isRecordsOnly() {
        return isRecordsOnly;
    }

    public void setRecordsOnly(boolean recordsOnly) {
        this.isRecordsOnly = recordsOnly;
    }

    public String[] getEntryTypes() {

        List<String> entryTypes = new ArrayList<String>();
        for (JournalEntryType entryType : getJournalEntryTypes()) {
            entryTypes.add(entryType.name());
        }

        return entryTypes.toArray(new String[entryTypes.size()]);
    }

    public JournalEntryType[] getJournalEntryTypes() {
        return journalEntryTypes.toArray(new JournalEntryType[journalEntryTypes.size()]);
    }

    public void addJournalEntryType(JournalEntryType journalEntryType) {
        journalEntryTypes.add(journalEntryType);
    }

    public int getMaxEntries() {
        return maxItemsToRetrieve;
    }

    public void setMaxItemsToRetrieve(int maxItemsToRetrieve) {
        this.maxItemsToRetrieve = maxItemsToRetrieve;
    }
}
