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

import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;

import com.ibm.as400.access.AS400Date;

/**
 * Journal entry property, which contains the name and the value of a field of a
 * {@link JournalEntry}. That field could be a JO* field or an entry specific
 * field. Entry specific fields are grouped by a {@link JOESDProperty}.
 */
public class JournalProperty implements Comparable<JournalProperty> {

    public String name;
    public String label;
    public Object value;
    public int dataType;
    public Object parent;
    public boolean highlighted;

    private boolean errorParsing;
    private boolean nullValue;

    public JournalProperty(JournalEntryColumnUI columnDef, Object value, Object parent) {
        this(columnDef.columnName(), columnDef.columnNameLong(), value, -1, parent);
    }

    public JournalProperty(String name, String label, Object value, Object parent) {
        this(name, label, value, -1, parent);
    }

    public JournalProperty(String name, String label, Object value, int dataType, Object parent) {

        this.name = name;
        this.label = name + " (" + label + ")";
        this.value = value;
        this.dataType = dataType;
        this.parent = parent;

        setErrorParsing(false);
        setNullValue(false);
    }

    public int compareTo(JournalProperty comparable) {

        if (name.equals(comparable.name) && value.equals(comparable.value)) {
            highlighted = comparable.highlighted = false;
            return 0;
        } else {
            highlighted = comparable.highlighted = true;
            return -1;
        }
    }

    public void setErrorParsing(boolean error) {
        this.errorParsing = error;
    }

    public boolean isErrorParsing() {
        return errorParsing;
    }

    public void setNullValue(boolean nullValue) {
        this.nullValue = nullValue;
    }

    public boolean isNullValue() {
        return nullValue;
    }

    public boolean isNumeric() {
        switch (dataType) {
        case AS400Date.TYPE_BIN1:
        case AS400Date.TYPE_BIN2:
        case AS400Date.TYPE_BIN4:
        case AS400Date.TYPE_BIN8:
        case AS400Date.TYPE_UBIN1:
        case AS400Date.TYPE_UBIN2:
        case AS400Date.TYPE_UBIN4:
        case AS400Date.TYPE_UBIN8:
        case AS400Date.TYPE_FLOAT4:
        case AS400Date.TYPE_FLOAT8:
        case AS400Date.TYPE_DECFLOAT:
        case AS400Date.TYPE_PACKED:
        case AS400Date.TYPE_ZONED:
            return true;

        default:
            return false;
        }
    }

    @Override
    public String toString() {
        return label + "=" + value;
    }
}
