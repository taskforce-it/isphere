/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class defines the IBM i journal codes.
 * <p>
 * This class has been inspired by the RJNE0100 example written by Stanley Vong.
 * See <a href="http://stanleyvong.blogspot.de/">RJNE0100</a> example from
 * February 19, 2013.
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

    private static Map<String, JournalCode> values;

    private String label;
    private String description;
    private Set<JournalEntryType> journalEntryTypes;

    static {
        values = new HashMap<String, JournalCode>();
        for (JournalCode journalCode : JournalCode.values()) {
            values.put(journalCode.name(), journalCode);
        }
    }

    public static JournalCode find(String value) {
        return values.get(value);
    }

    private JournalCode(String description) {
        this.label = this.name();
        this.description = description;
        this.journalEntryTypes = new HashSet<JournalEntryType>();
    }

    // intentionally package visibility
    void addJournalEntryType(JournalEntryType journalEntryType) {
        journalEntryTypes.add(journalEntryType);
    }

    public boolean isParentOf(JournalEntryType journalEntryType) {
        return journalEntryTypes.contains(journalEntryType);
    }

    public JournalEntryType[] getJournalEntryTypes() {
        return journalEntryTypes.toArray(new JournalEntryType[journalEntryTypes.size()]);
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
