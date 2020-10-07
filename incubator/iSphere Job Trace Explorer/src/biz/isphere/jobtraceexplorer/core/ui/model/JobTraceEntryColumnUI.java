/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the UI names for the columns of a job trace entry record.
 */
public enum JobTraceEntryColumnUI {
    ;

    private static Map<String, JobTraceEntryColumnUI> values;

    private String columnName;
    private String columnNameLong;
    private String columnDescription;

    static {
        values = new HashMap<String, JobTraceEntryColumnUI>();
        for (JobTraceEntryColumnUI jobTraceEntryType : JobTraceEntryColumnUI.values()) {
            values.put(jobTraceEntryType.columnName(), jobTraceEntryType);
        }
    }

    public static JobTraceEntryColumnUI find(String columnName) {
        return values.get(columnName);
    }

    private JobTraceEntryColumnUI(String fieldName, String longFieldName, String columnDescription) {
        this.columnName = fieldName;
        this.columnNameLong = longFieldName;
        this.columnDescription = columnDescription;
    }

    public String columnName() {
        return columnName;
    }

    public String columnNameLong() {
        return columnNameLong;
    }

    public String description() {
        return columnDescription;
    }
}
