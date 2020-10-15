/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;

import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.dao.ColumnsDAO;

/**
 * This class defines the UI names for the columns of a journal entry record.
 */
public enum JobTraceEntryColumnUI {
    ID (ColumnsDAO.ID, Messages.Tooltip_ID, 50),
    NANOS_SINE_STARTED (ColumnsDAO.NANOS_SINE_STARTED, Messages.Tooltip_Nanoseconds_since_collection_started, 160),
    TIMESTAMP (ColumnsDAO.TIMESTAMP, Messages.Tooltip_Timestamp, 160),
    PGM_NAME (ColumnsDAO.PGM_NAME, Messages.Tooltip_Program_name, 80),
    PGM_LIB (ColumnsDAO.PGM_LIB, Messages.Tooltip_Program_library, 80),
    MOD_NAME (ColumnsDAO.MOD_NAME, Messages.Tooltip_Module_name, 80),
    MOD_LIB (ColumnsDAO.MOD_LIB, Messages.Tooltip_Module_library, 80),
    HLL_STMT_NBR (ColumnsDAO.HLL_STMT_NBR, Messages.Tooltip_HLL_statement_number, 60),
    PROC_NAME (ColumnsDAO.PROC_NAME, Messages.Tooltip_Procedure_name, 160),
    CALL_LEVEL (ColumnsDAO.CALL_LEVEL, Messages.Tooltip_Invocation_call_level, 80),
    EVENT_SUB_TYPE (ColumnsDAO.EVENT_SUB_TYPE, Messages.Tooltip_Event_subtype_description, 80),
    CALLER_HLL_STMT_NBR (ColumnsDAO.CALLER_HLL_STMT_NBR, Messages.Tooltip_Caller_HLL_statement_number, 60),
    CALLER_PROC_NAME (ColumnsDAO.CALLER_PROC_NAME, Messages.Tooltip_Caller_procedure_name, 160),
    CALLER_CALL_LEVEL (ColumnsDAO.CALLER_CALL_LEVEL, Messages.Tooltip_Caller_Invocation_call_level, 80);

    private static Map<String, JobTraceEntryColumnUI> values;

    private String columnName;
    private int columnIndex;
    private String columnText;
    private String columnTooltip;
    private int width;
    private int style;

    static {
        values = new HashMap<String, JobTraceEntryColumnUI>();
        for (JobTraceEntryColumnUI JobTraceEntryType : JobTraceEntryColumnUI.values()) {
            values.put(JobTraceEntryType.columnName(), JobTraceEntryType);
        }
    }

    public static JobTraceEntryColumnUI find(String columnName) {
        return values.get(columnName);
    }

    private JobTraceEntryColumnUI(ColumnsDAO columnsDAO, String columnTooltip, int width) {
        this(columnsDAO.name(), columnsDAO.ordinal(), columnsDAO.description(), columnTooltip, width);
    }

    private JobTraceEntryColumnUI(String fieldName, int columnIndex, String columnText, String columnTooltip, int width) {
        this.columnName = fieldName;
        this.columnIndex = columnIndex;
        this.columnText = columnText;
        this.columnTooltip = columnTooltip;
        this.width = width;
        this.style = SWT.NONE;
    }

    public int columnIndex() {
        return columnIndex;
    }

    public String columnName() {
        return columnName;
    }

    public String columnText() {
        return columnText;
    }

    public String columnTooltip() {
        return columnTooltip;
    }

    public int width() {
        return width;
    }

    public int style() {
        return style;
    }
}
