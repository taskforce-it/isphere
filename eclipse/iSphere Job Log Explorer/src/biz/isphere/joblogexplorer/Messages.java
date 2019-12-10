/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.joblogexplorer.messages"; //$NON-NLS-1$

    public static String Job_Parsing_job_log;
    public static String Job_Loading_job_log;
    public static String Job_Log_Explorer;
    public static String Job_Log_Explorer_Tooltip;

    public static String Column_Date_sent;
    public static String Column_Time_sent;
    public static String Column_ID;
    public static String Column_Type;
    public static String Column_Severity;
    public static String Column_Text;
    public static String Column_From_Library;
    public static String Column_From_Program;
    public static String Column_From_Stmt;
    public static String Column_To_Library;
    public static String Column_To_Program;
    public static String Column_To_Stmt;
    public static String Column_From_Module;
    public static String Column_To_Module;
    public static String Column_From_Procedure;
    public static String Column_To_Procedure;

    public static String Label_ID;
    public static String Label_Type;
    public static String Label_Severity;
    public static String Label_Date_sent;
    public static String Label_Time_sent;
    public static String Label_From_Library;
    public static String Label_From_Program;
    public static String Label_From_Module;
    public static String Label_From_Procedure;
    public static String Label_From_Stmt;
    public static String Label_To_Library;
    public static String Label_To_Program;
    public static String Label_To_Module;
    public static String Label_To_Procedure;
    public static String Label_To_Stmt;
    public static String Label_Text;
    public static String Label_Text_tooltip;

    public static String Label_Filter;
    public static String Label_Filter_tooltip;

    public static String LongFieldName_ID;
    public static String LongFieldName_TYPE;
    public static String LongFieldName_SEVERITY;
    public static String LongFieldName_FROM_LIBRARY;
    public static String LongFieldName_FROM_PROGRAM;
    public static String LongFieldName_FROM_MODULE;
    public static String LongFieldName_FROM_PROCEDURE;
    public static String LongFieldName_FROM_STATEMENT;
    public static String LongFieldName_TO_LIBRARY;
    public static String LongFieldName_TO_PROGRAM;
    public static String LongFieldName_TO_MODULE;
    public static String LongFieldName_TO_PROCEDURE;
    public static String LongFieldName_TO_STATEMENT;
    public static String LongFieldName_TEXT;
    public static String LongFieldName_HELP;
    public static String LongFieldName_TIMESTAMP;

    public static String Message_details;
    public static String Number_of_messages_A;
    public static String Number_of_messages_B_slash_A;
    public static String Loading_remote_job_log_dots;
    public static String Invalid_job_log_Format_Could_not_find_first_line_of_job_log;

    public static String Enable_coloring;
    public static String Enable_coloring_tooltip;
    public static String Colors;
    public static String Severity_BL_colon;
    public static String Severity_00_colon;
    public static String Severity_10_colon;
    public static String Severity_20_colon;
    public static String Severity_30_colon;
    public static String Severity_40_colon;
    public static String Severity_BLANK_tooltip;
    public static String Severity_A_to_B_tooltip;

    public static String Apply_filters;
    public static String Clear_filters;
    public static String Select_all;
    public static String Deselect_all;
    public static String Search_up;
    public static String Search_down;

    public static String Property_Category_system;
    public static String Property_Category_job;
    public static String Property_Category_statistics;
    public static String Property_system_name;
    public static String Property_job_name;
    public static String Property_job_user;
    public static String Property_job_number;
    public static String Property_job_description;
    public static String Property_start_date;
    public static String Property_last_date;
    public static String Property_pages;
    public static String Property_number_of_messages;

    public static String E_R_R_O_R;
    public static String Job_C_B_A_not_found;
    public static String Could_not_load_job_log_of_job_C_B_A_Reason_D;
    public static String Could_not_load_job_log_parser_configuration;

    public static String MsgType_Completion;
    public static String MsgType_Diagnostic;
    public static String MsgType_Informational;
    public static String MsgType_Inquery;
    public static String MsgType_Copy;
    public static String MsgType_Request;
    public static String MsgType_Notify;
    public static String MsgType_Escape;
    public static String MsgType_Reply;

    public static String Use_the_exclamation_mark_to_negate_a_search_argument_eg_Completion;

    public static String Refresh;
    public static String Edit_SQL;
    public static String Exort_to_Excel;
    public static String Reset_Column_Size;
    public static String SaveDialog_Excel_Workbook;
    public static String SaveDialog_All_Files;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
