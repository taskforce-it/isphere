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

import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryAppearanceAttributes;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;

/**
 * Properties (= fields) of a journal entry, which can be displayed in a journal
 * entry details (tree) viewer. Field are stored in a {@link JournalProperty}.
 * Entry specific fields are grouped in a {@link JOESDProperty}.
 * 
 * <pre>
 * JournalProperties
 *    |
 *    +-- JournalPropery[]
 *    |
 *    +-- JOESDProperty 
 *           |
 *           +-- JournalPropery[]
 * </pre>
 * 
 * <i>Journal properties is</i> is a transient attribute of {@link JournalEntry}
 * .<br>
 * Classes {@link JournalProperties}, {@link JournalProperty} and
 * {@link JOESDProperty} are the GUI representation of {@link JournalEntries}
 * and {@link JournalEntry}, which build the data model.
 */
public class JournalProperties {

    private final JournalEntry journalEntry;
    private final ArrayList<JournalProperty> properties;
    private JournalEntryAppearanceAttributes[] sortedAttributes;
    private JOESDProperty joesdProperty;

    public JournalProperties(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
        this.properties = new ArrayList<JournalProperty>();
        this.sortedAttributes = Preferences.getInstance().getSortedJournalEntryAppearancesAttributes();
        this.joesdProperty = null;

        initialize();
    }

    private void initialize() {

        Map<String, JournalProperty> journalProperties = new HashMap<String, JournalProperty>();

        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.ID, journalEntry.getId(), journalEntry));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOENTL, journalEntry.getEntryLength(), journalEntry));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOSEQN, journalEntry.getSequenceNumber(), journalEntry));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOCODE, journalEntry.getJournalCode(), journalEntry));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOENTT, journalEntry.getEntryType(), journalEntry));
        addProperty(journalProperties, new JournalProperty(JournalEntryColumnUI.JOCTRR, journalEntry.getCountRrn(), journalEntry));

        // Properties of the entry specific data.
        this.joesdProperty = new JOESDProperty(JournalEntryColumnUI.JOESD, journalEntry, journalEntry);
        addProperty(journalProperties, joesdProperty);

        for (JournalEntryAppearanceAttributes attribute : sortedAttributes) {
            if (journalProperties.containsKey(attribute.getColumnName())) {
                properties.add(journalProperties.get(attribute.getColumnName()));
            }
        }

        journalEntry.setJournalProperties(this);
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
        return journalEntry;
    }

    @Override
    public int hashCode() {
        return journalEntry.hashCode();
    }

    @Override
    public boolean equals(Object comparedObject) {
        if (comparedObject instanceof JournalProperties) {
            return journalEntry.equals(((JournalProperties)comparedObject).journalEntry);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return journalEntry.getKey() + " " + journalEntry.getQualifiedObjectName(); //$NON-NLS-1$
    }
}
