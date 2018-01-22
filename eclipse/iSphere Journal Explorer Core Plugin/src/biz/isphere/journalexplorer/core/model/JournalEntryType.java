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

public enum JournalEntryType {
    BR ("Before-image of record updated for rollback"),
    DL ("Record deleted from physical file member"),
    DR ("Record deleted for rollback"),
    IL ("Increment record limit"),
    PT ("Record added to physical file member"),
    PX ("Record added directly to physical file member"),
    UB ("Before-image of record updated in physical file member"),
    UP ("After-image of record updated in physical file member"),
    UR ("After-image of record updated for rollback");

    private static Map<String, JournalEntryType> values;

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
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("%s, (%s)", getDescription(), this.name());
    }
}
