/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.jobtraceexplorer.core.messages"; //$NON-NLS-1$

    public static String E_R_R_O_R;

    // Labels and tooltips
    public static String ButtonLabel_Filter;
    public static String ButtonTooltip_Filter;

    public static String GroupLabel_Limitation_Properties;
    public static String ButtonLabel_Maximum_number_of_rows_to_fetch;
    public static String ButtonTooltip_Maximum_number_of_rows_to_fetch_tooltip;

    // Column descriptions and tooltips
    public static String LongFieldName_ID;
    public static String LongFieldName_Nanoseconds_since_collection_started;
    public static String LongFieldName_Timestamp;
    public static String LongFieldName_Program_name;
    public static String LongFieldName_Program_library;
    public static String LongFieldName_Module_name;
    public static String LongFieldName_Module_library;
    public static String LongFieldName_HLL_statement_number;
    public static String LongFieldName_Procedure_name;
    public static String LongFieldName_Invocation_call_level;
    public static String LongFieldName_Event_subtype_description;
    public static String LongFieldName_Caller_HLL_statement_number;
    public static String LongFieldName_Caller_procedure_name;
    public static String LongFieldName_Caller_Invocation_call_level;

    public static String Tooltip_ID;
    public static String Tooltip_Nanoseconds_since_collection_started;
    public static String Tooltip_Timestamp;
    public static String Tooltip_Program_name;
    public static String Tooltip_Program_library;
    public static String Tooltip_Module_name;
    public static String Tooltip_Module_library;
    public static String Tooltip_HLL_statement_number;
    public static String Tooltip_Procedure_name;
    public static String Tooltip_Invocation_call_level;
    public static String Tooltip_Event_subtype_description;
    public static String Tooltip_Caller_HLL_statement_number;
    public static String Tooltip_Caller_procedure_name;
    public static String Tooltip_Caller_Invocation_call_level;

    // Column values: Event sub type
    public static String Called_by;
    public static String Returned_to;

    // Job status
    public static String Status_Loading_job_trace_entries;

    // Actions
    public static String Action_ReloadEntries;
    public static String JobTraceExplorerView_OpenJobTraceSession;

    // Dialog titles
    public static String MessageDialog_Open_Job_Trace_Session_Title;
    public static String MessageDialog_Load_Job_Trace_Entries_Title;

    // Open Job Trace Session Dialog
    public static String OpenJobTraceSessionDialog_Connection;
    public static String OpenJobTraceSessionDialog_SessionID;
    public static String OpenJobTraceSessionDialog_Library;

    public static String Tooltip_OpenJobTraceSessionDialog_Connection;
    public static String Tooltip_OpenJobTraceSessionDialog_SessionID;
    public static String Tooltip_OpenJobTraceSessionDialog_Library;

    // Messages
    public static String Number_of_job_trace_entries_A_more_items_available;
    public static String Number_of_job_trace_entries_A_of_B;
    public static String Number_of_job_trace_entries_A;
    public static String subsetted_list;

    // Error Messages
    public static String Error_No_connections_available;
    public static String Error_AllDataRequired;
    public static String Error_Connection_A_not_found_or_not_available;

    // Warnings
    public static String Warning_Not_all_job_trace_entries_loaded;
    public static String Warning_Not_all_job_trace_entries_loaded_unknown_size;
    public static String Warning_Loading_job_trace_entries_has_been_canceled_by_the_user;

    // Exceptions
    public static String Exception_No_job_trace_entries_loaded_from_library_A_and_session_id_B;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
