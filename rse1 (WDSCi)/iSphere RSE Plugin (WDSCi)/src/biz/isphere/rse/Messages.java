/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.rse.messages";

    public static String iSphere_Retrieve_Binder_Source;

    public static String iSphere_Message_File_Editor;

    public static String iSphere_Compare_Editor;

    public static String iSphere_Data_Area_Editor;

    public static String iSphere_User_Space_Editor;

    public static String iSphere_Data_Area_Monitor;

    public static String iSphere_User_Space_Monitor;
    
    public static String iSphere_Data_Queue_Monitor;
    
    public static String iSphere_Compare_Message_File_Editor;
    
    public static String iSphere_CopyMembersTo;

    public static String Right;

    public static String Left;

    public static String Ancestor;

    public static String iSphere_Binding_Directory_Editor;

    public static String E_R_R_O_R;

    public static String Resources_with_different_connections_have_been_selected;

    public static String iSphere_Message_File_Search;

    public static String iSphere_Source_File_Search;

    public static String Deleting_spooled_files;

    public static String Deleting;

    public static String Enter_or_select_search_string;
    
    public static String Specify_whether_all_matching_records_are_returned;
    
    public static String Connection;
    
    public static String Target;
    
    public static String Enter_or_select_a_library_name;
    
    public static String Enter_or_select_a_simple_or_generic_message_file_name;

    public static String Enter_or_select_a_simple_or_generic_file_name;

    public static String Enter_or_select_a_simple_or_generic_member_name;

    public static String Library;
    
    public static String Message_file;
    
    public static String Columns;
    
    public static String All_columns;
    
    public static String Search_all_columns;
    
    public static String Between;
    
    public static String Search_between_specified_columns;
    
    public static String Specify_start_column;
    
    public static String and;
    
    public static String Specify_end_column_max_132;
    
    public static String Specify_end_column_max_228;
    
    public static String Source_File;

    public static String Source_Member; 

    public static String Options;
    
    public static String ShowAllRecords;

    public static String IncludeFirstLevelText;
    
    public static String Specify_whether_or_not_to_include_the_first_level_message_text;
    
    public static String IncludeSecondLevelText;
    
    public static String Specify_whether_or_not_to_include_the_second_level_message_text;

    public static String No_objects_found_that_match_the_selection_criteria;

    public static String Loading_remote_objects;

    public static String Library_A_not_found;
    
    public static String File_A_in_library_B_not_found;

    public static String Select_Message_File;
    
    public static String Select_Object;

    public static String Object_A_in_library_B_not_found;
    
    public static String No_filter_pool_available;
    
    public static String A_filter_with_name_A_already_exists_Do_you_want_to_extend_the_filter;

    public static String Failed_to_save_data_to_file_colon_A;

    public static String Failed_to_load_data_from_file_colon_A;

    public static String Connection_A_not_found;

    public static String Failed_to_execute_command_A;

    public static String Cannot_copy_source_members_from_different_connections;
    
    public static String Member_C_of_file_A_slash_B_is_locked_by_job_F_slash_E_slash_D;

    public static String Failed_to_connect_to_system_A;

    public static String SplfDecoration_Menu;
    
    public static String SplfDecoration_Status;
    
    public static String SplfDecoration_CreationTime;
    
    public static String SplfDecoration_UserData;
    
    public static String SplfDecoration_Job;
    
    public static String SplfDecoration_UserDefined;

    public static String IncludeMessageId;

    public static String Specify_whether_or_not_to_include_the_message_id;
    
    public static String Refer_to_help_for_details;
    
    public static String Filter_pool_colon;
    
    public static String Filter_colon;

    public static String Information;

    public static String Description;

    public static String Copy_to_clipboard;
    
    public static String No_user_action_manager_available;
    
    public static String No_compile_command_manager_available;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
