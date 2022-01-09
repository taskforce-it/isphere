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

    public static String GroupLabel_Colors;
    public static String ColorLabel_HighlighValues;
    public static String ColorTooltip_HighlighValues;
    public static String ColorLabel_HighlighProcedures;
    public static String ColorTooltip_HighlighProcedures;
    public static String ColorLabel_HighlighHiddenProcedures;
    public static String ColorTooltip_HighlighHiddenProcedures;
    public static String GroupLabel_Limitation_Properties;
    public static String ButtonLabel_Maximum_number_of_rows_to_fetch;
    public static String ButtonTooltip_Maximum_number_of_rows_to_fetch_tooltip;
    public static String ButtonLabel_Exclude_IBM_data_SQL_WHERE_clause;
    public static String ButtonTooltip_Exclude_IBM_data_SQL_WHERE_clause;

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

    // Properties
    public static String Property_connection_name;
    public static String Property_library_name;
    public static String Property_session_ID;
    public static String Property_where_clause;
    public static String Property_IBM_data_excluded;
    public static String Property_file_name;

    // Job status
    public static String Status_Loading_job_trace_entries_of_session_A;
    public static String Status_Preparing_to_load_job_trace_entries;
    public static String Status_Executing_query;
    public static String Status_Receiving_job_trace_entries;
    public static String Status_Searching_for_procedure_exit;
    public static String Status_Searching_for_procedure_entry;
    public static String Status_Exporting_to_Json;
    public static String Status_Importing_from_Json;

    // Actions
    public static String Action_ReloadEntries;
    public static String JobTraceExplorerView_OpenJobTraceSession;
    public static String JobTraceExplorerView_Export_to_Json;
    public static String JobTraceExplorerView_Import_from_Json;
    public static String MenuItem_Jump_to_procedure_entry;
    public static String MenuItem_Jump_to_procedure_exit;
    public static String MenuItem_Highlight_procedure;
    public static String MenuItem_Highlight_value;
    public static String MenuItem_Edit_sql;
    public static String MenuItem_Exclude_procedure;
    public static String MenuItem_Include_procedure;

    // Dialog titles
    public static String MessageDialog_Open_Job_Trace_Session_Title;
    public static String MessageDialog_Load_Job_Trace_Entries_Title;
    public static String MessageDialog_Information;

    // Open Job Trace Session Dialog
    public static String OpenJobTraceSessionDialog_Connection;
    public static String OpenJobTraceSessionDialog_SessionID;
    public static String OpenJobTraceSessionDialog_Library;
    public static String OpenJobTraceSessionDialog_Exclude_IBM_Data;

    // Filter panel
    public static String Label_Text;
    public static String ButtonTooltip_Text_tooltip;
    public static String ButtonTooltip_Search_up;
    public static String ButtonTooltip_Search_down;

    public static String Tooltip_OpenJobTraceSessionDialog_Connection;
    public static String Tooltip_OpenJobTraceSessionDialog_SessionID;
    public static String Tooltip_OpenJobTraceSessionDialog_Library;
    public static String Tooltip_OpenJobTraceSessionDialog_Exclude_IBM_data;

    // Messages
    public static String Number_of_job_trace_entries_A_more_items_available;
    public static String Number_of_job_trace_entries_A_of_B;
    public static String Number_of_job_trace_entries_A;
    public static String subsetted_list;
    public static String Display_Export_Result;
    public static String Finished_exporting_data_to_file_A;
    public static String There_is_nothing_to_hide;

    // Error Messages
    public static String Error_No_connections_available;
    public static String Error_AllDataRequired;
    public static String Error_Connection_A_not_found_or_not_available;
    public static String Error_Job_trace_session_B_not_found_in_library_A;

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
