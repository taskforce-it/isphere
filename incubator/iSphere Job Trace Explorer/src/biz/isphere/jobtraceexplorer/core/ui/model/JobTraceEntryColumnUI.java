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
    // @formatter:off
    ID (ColumnsDAO.ID.name(), ColumnsDAO.ID.description(), Messages.Tooltip_ID, 50),
    NANOS_SINE_STARTED (ColumnsDAO.NANOS_SINE_STARTED.name(), ColumnsDAO.NANOS_SINE_STARTED.description(), Messages.Tooltip_Nanoseconds_since_collection_started, 160),
    TIMESTAMP (ColumnsDAO.TIMESTAMP.name(), ColumnsDAO.TIMESTAMP.description(), Messages.Tooltip_Timestamp, 160),
    PGM_NAME (ColumnsDAO.PGM_NAME.name(), ColumnsDAO.PGM_NAME.description(), Messages.Tooltip_Program_name, 80),
    PGM_LIB (ColumnsDAO.PGM_LIB.name(), ColumnsDAO.PGM_LIB.description(), Messages.Tooltip_Program_library, 80),
    MODULE_NAME (ColumnsDAO.MODULE_NAME.name(), ColumnsDAO.MODULE_NAME.description(), Messages.Tooltip_Module_name, 80),
    HLL_STMT_NBR (ColumnsDAO.HLL_STMT_NBR.name(), ColumnsDAO.HLL_STMT_NBR.description(), Messages.Tooltip_HLL_statement_number, 60),
    PROC_NAME (ColumnsDAO.PROC_NAME.name(), ColumnsDAO.PROC_NAME.description(), Messages.Tooltip_Procedure_name, 160),
    CALL_LEVEL (ColumnsDAO.CALL_LEVEL.name(), ColumnsDAO.CALL_LEVEL.description(), Messages.Tooltip_Invocation_call_level, 80),
    EVENT_SUB_TYPE (ColumnsDAO.EVENT_SUB_TYPE.name(), ColumnsDAO.EVENT_SUB_TYPE.description(), Messages.Tooltip_Event_subtype_description, 80),
    CALLER_HLL_STMT_NBR (ColumnsDAO.CALLER_HLL_STMT_NBR.name(), ColumnsDAO.CALLER_HLL_STMT_NBR.description(), Messages.Tooltip_Caller_HLL_statement_number, 60),
    CALLER_PROC_NAME (ColumnsDAO.CALLER_PROC_NAME.name(), ColumnsDAO.CALLER_PROC_NAME.description(), Messages.Tooltip_Caller_procedure_name, 160), 
    CALLER_CALL_LEVEL (ColumnsDAO.CALLER_CALL_LEVEL.name(), ColumnsDAO.CALLER_CALL_LEVEL.description(), Messages.Tooltip_Caller_Invocation_call_level, 80);
    // @formatter:on

    private static Map<String, JobTraceEntryColumnUI> values;

    private String columnName;
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

    private JobTraceEntryColumnUI(String fieldName, String columnText, String columnTooltip, int width) {
        this.columnName = fieldName;
        this.columnText = columnText;
        this.columnTooltip = columnTooltip;
        this.width = width;
        this.style = SWT.NONE;
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
