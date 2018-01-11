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
    BR, // Before rollback
    DL, // Delete
    DR, // Delete rollback
    IL, // Increment record limit
    PT, // Put
    PX, // Put (reuse deleted record)
    UB, // Update before
    UP, // Update past
    UR; // Update rollback

    private static Map<String, JournalEntryType> values;

    static {
        values = new HashMap<String, JournalEntryType>();
        for (JournalEntryType journalEntryType : JournalEntryType.values()) {
            values.put(journalEntryType.name(), journalEntryType);
        }
    }

    public static JournalEntryType find(String value) {
        return values.get(value);
    }
}
