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

import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;

public class JournalProperties {

    private static final String RRN_OUTPUT_FILE = Messages.JournalProperties_RRN_OutputFile;
    private static final String JOENTL = Messages.JournalProperties_JOENTL;
    private static final String JOSEQN = Messages.JournalProperties_JOSEQN;
    private static final String JOCODE = Messages.JournalProperties_JOCODE;
    private static final String JOENTT = Messages.JournalProperties_JOENTT;
    private static final String JOCTRR = Messages.JournalProperties_JOCTRR;
    private static final String STRING_SPECIFIC_DATA = Messages.JournalProperties_JOESD;

    private final JournalEntry journal;

    private ArrayList<JournalProperty> properties;

    public JournalProperties(JournalEntry journal) {
        this.journal = journal;
        this.properties = new ArrayList<JournalProperty>();
        initialize();
    }

    private void initialize() {

        properties.add(new JournalProperty(RRN_OUTPUT_FILE, journal.getId(), journal));
        properties.add(new JournalProperty(JOENTL, journal.getEntryLength(), journal));
        properties.add(new JournalProperty(JOSEQN, journal.getSequenceNumber(), journal));
        properties.add(new JournalProperty(JOCODE, journal.getJournalCode(), journal));
        properties.add(new JournalProperty(JOENTT, journal.getEntryType(), journal));
        properties.add(new JournalProperty(JOCTRR, journal.getCountRrn(), journal));
        properties.add(new JOESDProperty(STRING_SPECIFIC_DATA, "", journal, journal)); //$NON-NLS-1$
    }

    public JOESDProperty getJOESDProperty() {

        for (JournalProperty property : properties) {
            if (property.name == STRING_SPECIFIC_DATA && property instanceof JOESDProperty) {
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
