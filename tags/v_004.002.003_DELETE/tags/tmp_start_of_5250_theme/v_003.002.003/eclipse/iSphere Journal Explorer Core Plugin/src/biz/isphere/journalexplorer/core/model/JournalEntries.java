/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.util.ArrayList;
import java.util.List;

import biz.isphere.journalexplorer.core.model.api.IBMiMessage;

import com.ibm.as400.access.AS400Message;

public class JournalEntries {

    private List<JournalEntry> journalEntries;
    private boolean isOverflow;
    private int numAvailableRows;
    private List<IBMiMessage> messages;

    public JournalEntries() {

        this.journalEntries = new ArrayList<JournalEntry>();
        this.isOverflow = false;
        this.numAvailableRows = -1;
    }

    public void add(JournalEntry journalEntry) {

        journalEntries.add(journalEntry);
    }

    public List<JournalEntry> getItems() {

        return journalEntries;
    }

    public JournalEntry getItem(int index) {

        return journalEntries.get(index);
    }

    public boolean isOverflow() {
        return isOverflow;
    }

    public void setOverflow(boolean isOverflow, int numAvailableRows) {

        this.isOverflow = isOverflow;
        this.numAvailableRows = numAvailableRows;
    }

    public int size() {

        return journalEntries.size();
    }

    public int getNumberOfRowsAvailable() {

        if (isOverflow()) {
            return numAvailableRows;
        } else {
            return size();
        }
    }

    public void clear() {

        journalEntries.clear();
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
