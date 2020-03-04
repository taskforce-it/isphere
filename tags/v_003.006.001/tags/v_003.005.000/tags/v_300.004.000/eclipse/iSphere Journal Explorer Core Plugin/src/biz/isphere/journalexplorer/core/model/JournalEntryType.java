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
    BR ("Before-image of record updated for rollback operation"),
    DL ("Record deleted in the physical file member"),
    DR ("Record deleted for rollback operation"),
    IL ("Increment record limit"),
    PT ("Record added to a physical file member"),
    PX ("Record added directly by RRN (relative record number) to a physical file member"),
    UB ("Before-image of a record that is updated in the physical file member"),
    UP ("After-image of a record that is updated in the physical file member"),
    UR ("After-image of a record that is updated for rollback information");

    private static Map<String, JournalEntryType> values;

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

    private JournalEntryType(String description) {
        this.label = this.name();
        this.description = description;
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
