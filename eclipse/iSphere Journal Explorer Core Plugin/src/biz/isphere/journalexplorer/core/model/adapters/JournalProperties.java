/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
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
import java.util.HashMap;
import java.util.Map;

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryAppearanceAttributes;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;

public class JournalProperties {

    private final JournalEntry journal;
    private final ArrayList<JournalProperty> properties;
    private JournalEntryAppearanceAttributes[] sortedAttributes;
    private JOESDProperty joesdProperty;

    public JournalProperties(JournalEntry journal) {
        this.journal = journal;
        this.properties = new ArrayList<JournalProperty>();
        this.sortedAttributes = Preferences.getInstance().getSortedJournalEntryAppearancesAttributes();
        this.joesdProperty = null;

        initialize();
    }

    private void initialize() {

        Map<String, JournalProperty> journalProperties = new HashMap<String, JournalProperty>();

        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.ID, journal.getId(), journal));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOENTL, journal.getEntryLength(), journal));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOSEQN, journal.getSequenceNumber(), journal));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOCODE, journal.getJournalCode(), journal));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOENTT, journal.getEntryType(), journal));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOCTRR, journal.getCountRrn(), journal));

        this.joesdProperty = new JOESDProperty(JournalEntryColumnUI.JOESD, journal, journal);
        addProperty(journalProperties, this.joesdProperty);

        for (JournalEntryAppearanceAttributes attribute : sortedAttributes) {
            if (journalProperties.containsKey(attribute.getColumnName())) {
                properties.add(journalProperties.get(attribute.getColumnName()));
            }
        }
    }

    private void addProperty(Map<String, JournalProperty> availableProperties, JournalProperty journalProperty) {

        availableProperties.put(journalProperty.name, journalProperty);
    }

    public JOESDProperty getJOESDProperty() {
        return joesdProperty;
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
