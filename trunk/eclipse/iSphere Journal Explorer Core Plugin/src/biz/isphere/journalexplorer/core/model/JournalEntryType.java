/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the IBM i journal entry types of journal code 'R'.
 * <p>
 * This class has been inspired by the RJNE0100 example written by Stanley Vong.
 * See <a href="http://stanleyvong.blogspot.de/">RJNE0100</a> example from
 * February 19, 2013.
 */
public enum JournalEntryType {
    BR (JournalCode.R, "Before-image of record updated for rollback operation"),
    DL (JournalCode.R, "Record deleted in the physical file member"),
    DR (JournalCode.R, "Record deleted for rollback operation"),
    IL (JournalCode.R, "Increment record limit"),
    PT (JournalCode.R, "Record added to a physical file member"),
    PX (JournalCode.R, "Record added directly by RRN (relative record number) to a physical file member"),
    UB (JournalCode.R, "Before-image of a record that is updated in the physical file member"),
    UP (JournalCode.R, "After-image of a record that is updated in the physical file member"),
    UR (JournalCode.R, "After-image of a record that is updated for rollback information");

    private static Map<String, JournalEntryType> values;

    private JournalCode journalCode;
    private String label;
    private String description;

    static {
        values = new HashMap<String, JournalEntryType>();
        for (JournalEntryType journalEntryType : JournalEntryType.values()) {
            values.put(journalEntryType.name(), journalEntryType);
        }
    }

    public static JournalEntryType find(String value) {
        return values.get(value);
    }

    private JournalEntryType(JournalCode journalCode, String description) {
        this.journalCode = journalCode;
        this.label = this.name();
        this.description = description;

        journalCode.addJournalEntryType(this);
    }

    public boolean isChildOf(JournalCode journalCode) {

        if (this.journalCode.equals(journalCode)) {
            return true;
        }

        return false;
    }

    public String label() {
        return label;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("%s, (%s)", getDescription(), this.name());
    }
}
