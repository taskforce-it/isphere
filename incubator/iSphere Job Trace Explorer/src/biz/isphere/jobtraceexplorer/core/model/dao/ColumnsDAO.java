/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model.dao;

import biz.isphere.jobtraceexplorer.core.Messages;

public enum ColumnsDAO {
    ID ("ID", "INTEGER", Messages.LongFieldName_ID),
    NANOS_SINE_STARTED ("QTITIMN", "BIGINT", Messages.LongFieldName_Nanoseconds_since_collection_started),
    TIMESTAMP ("QTITSP", "TIMESTAMP", Messages.LongFieldName_Timestamp),
    PGM_NAME ("QPRPGN", "CHAR(30)", Messages.LongFieldName_Program_name),
    PGM_LIB ("QPRPQL", "CHAR(10)", Messages.LongFieldName_Program_library),
    MOD_NAME ("QPRMNM", "VARCHAR(256", Messages.LongFieldName_Module_name),
    MOD_LIB ("QPRMQL", "CHAR(10", Messages.LongFieldName_Module_library),
    HLL_STMT_NBR ("QTBHLL", "INTEGER", Messages.LongFieldName_HLL_statement_number),
    PROC_NAME ("QPRPNM", "VARCHAR(256)", Messages.LongFieldName_Procedure_name),
    CALL_LEVEL ("QTBCLL", "INTEGER", Messages.LongFieldName_Invocation_call_level),
    EVENT_SUB_TYPE ("QEVSSN", "VARCHAR(15)", Messages.LongFieldName_Event_subtype_description),
    CALLER_HLL_STMT_NBR ("QTBCHL", "INTEGER", Messages.LongFieldName_Caller_HLL_statement_number),
    CALLER_PROC_NAME ("QPRPNM", "VARCHAR(256)", Messages.LongFieldName_Caller_procedure_name),
    CALLER_CALL_LEVEL ("QTBCLL", "INTEGER", Messages.LongFieldName_Caller_Invocation_call_level);

    public static final String EVENT_SUB_TYPE_PRCENTRY = "*PRCENTRY"; //$NON-NLS-1$
    public static final String EVENT_SUB_TYPE_PRCEXIT = "*PRCEXIT"; //$NON-NLS-1$

    private String systemColumnName;
    private String type;
    private String description;

    private ColumnsDAO(String systemColumnName, String type, String description) {
        this.systemColumnName = systemColumnName;
        this.type = type;
        this.description = description;
    }

    public String systemColumnName() {
        return systemColumnName;
    }

    public String type() {
        return type;
    }

    public String description() {
        return description;
    }

    public int index() {
        return ordinal() + 1;
    }

    public static final String[] ALL = new String[] { NANOS_SINE_STARTED.name(), TIMESTAMP.name(), PGM_NAME.name(), PGM_LIB.name(), MOD_NAME.name(),
        MOD_LIB.name(), HLL_STMT_NBR.name(), PROC_NAME.name(), CALL_LEVEL.name(), EVENT_SUB_TYPE.name(), CALLER_HLL_STMT_NBR.name(),
        CALLER_PROC_NAME.name() };
}
