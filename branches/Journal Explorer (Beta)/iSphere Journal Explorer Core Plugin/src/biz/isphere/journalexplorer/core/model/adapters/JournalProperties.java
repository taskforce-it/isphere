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

    private static final String RRN = Messages.JournalProperties_RRN;
    private static final String JOENTL = Messages.JournalProperties_JOENTL;
    private static final String JOSEQN = Messages.JournalProperties_JOSEQN;
    private static final String JOCODE = Messages.JournalProperties_JOCODE;
    private static final String JOENTT = Messages.JournalProperties_JOENTT;
    private static final String STRING_SPECIFIC_DATA = Messages.JournalProperties_JOESD;

    private final JournalEntry journal;

    private ArrayList<JournalProperty> properties;

    public JournalProperties(JournalEntry journal) {
        this.journal = journal;
        this.properties = new ArrayList<JournalProperty>();
        initialize();
    }

    private void initialize() {

        this.properties.add(new JournalProperty(RRN, this.journal.getRrn(), this.journal));
        this.properties.add(new JournalProperty(JOENTL, this.journal.getEntryLength(), this.journal));
        this.properties.add(new JournalProperty(JOSEQN, this.journal.getSequenceNumber(), this.journal));
        this.properties.add(new JournalProperty(JOCODE, this.journal.getJournalCode(), this.journal));
        this.properties.add(new JournalProperty(JOENTT, this.journal.getEntryType(), this.journal));
        this.properties.add(new JOESDProperty(STRING_SPECIFIC_DATA, "", this.journal, this.journal)); //$NON-NLS-1$
    }

    public JournalProperty getJOESDProperty() {

        for (JournalProperty property : this.properties) {
            if (property.name == STRING_SPECIFIC_DATA) {
                return property;
            }
        }
        return null;
    }

    public Object[] toArray() {
        return properties.toArray();
    }

    public JournalEntry getJournal() {
        return this.journal;
    }

    @Override
    public int hashCode() {
        return this.journal.hashCode();
    }

    @Override
    public boolean equals(Object comparedObject) {
        if (comparedObject instanceof JournalProperties) {
            return this.journal.equals(((JournalProperties)comparedObject).journal);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.journal.getKey() + " " + this.journal.getQualifiedObjectName(); //$NON-NLS-1$
    }
}
