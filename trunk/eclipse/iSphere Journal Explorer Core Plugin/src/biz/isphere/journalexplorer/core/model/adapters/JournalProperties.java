/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.adapters;

import java.util.ArrayList;

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;

public class JournalProperties {

    private final JournalEntry journal;

    private ArrayList<JournalProperty> properties;

    public JournalProperties(JournalEntry journal) {
        this.journal = journal;
        this.properties = new ArrayList<JournalProperty>();
        initialize();
    }

    private void initialize() {

        properties.add(new JournalProperty(JournalEntryColumnUI.ID, journal.getId(), journal));
        properties.add(new JournalProperty(JournalEntryColumnUI.JOENTL, journal.getEntryLength(), journal));
        properties.add(new JournalProperty(JournalEntryColumnUI.JOSEQN, journal.getSequenceNumber(), journal));
        properties.add(new JournalProperty(JournalEntryColumnUI.JOCODE, journal.getJournalCode(), journal));
        properties.add(new JournalProperty(JournalEntryColumnUI.JOENTT, journal.getEntryType(), journal));
        properties.add(new JournalProperty(JournalEntryColumnUI.JOCTRR, journal.getCountRrn(), journal));
        properties.add(new JOESDProperty(JournalEntryColumnUI.JOESD, journal, journal)); //$NON-NLS-1$
    }

    public JOESDProperty getJOESDProperty() {

        for (JournalProperty property : properties) {
            if (property.name == "JOESD" && property instanceof JOESDProperty) {
                return (JOESDProperty)property;
            }
        }
        return null;
    }

    public Object[] toArray() {
        return properties.toArray();
    }

    public JournalEntry getJournalEntry() {
        return journal;
    }

    @Override
    public int hashCode() {
        return journal.hashCode();
    }

    @Override
    public boolean equals(Object comparedObject) {
        if (comparedObject instanceof JournalProperties) {
            return journal.equals(((JournalProperties)comparedObject).journal);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return journal.getKey() + " " + journal.getQualifiedObjectName(); //$NON-NLS-1$
    }
}
