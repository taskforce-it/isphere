/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journaling.retrievejournalentries.internal;

import java.util.HashMap;
import java.util.Map;

public enum JournalCode {
    A ("System accounting entry"),
    B ("Integrated file system operation"),
    C ("Commitment control operation"),
    D ("Database file operation"),
    E ("Data area operation"),
    F ("Database file member operation"),
    I ("Internal operation"),
    J ("Journal or journal receiver operation"),
    L ("License management"),
    M ("Network management data"),
    P ("Performance tuning entry"),
    Q ("Data queue operation"),
    R ("Record level operation"),
    S ("Distributed mail service for SNA distribution services (SNADS), network alerts, or mail server framework"),
    T ("Audit trail entry"),
    U ("User generated");

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

    private JournalCode(String description) {
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
