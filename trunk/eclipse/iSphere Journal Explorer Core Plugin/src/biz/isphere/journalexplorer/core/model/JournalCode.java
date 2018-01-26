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
 * This class defines the IBM i journal codes.
 * <p>
 * This class has been inspired by the RJNE0100 example written by Stanley Vong.
 */
public enum JournalCode {
    A ("System Accounting Entry"),
    B ("Integrated File System"),
    C ("Commitment Control Operation"),
    D ("Database File Operation"),
    E ("Data Area Operation"),
    F ("Database File Member Operation"),
    I ("Internal Operation"),
    J ("Journal or Receiver Operation"),
    L ("License Management"),
    M ("Network Management Data"),
    P ("Performance Tuning Entry"),
    Q ("Data Queue Operation"),
    R ("Operation on Specific Record"),
    S ("Distributed Mail Services"),
    T ("Audit Trail Entry"),
    U ("User-Generated Entry"),
    Y ("Library Entry");

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
