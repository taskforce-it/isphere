/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.rse.messages";

    public static String Right;

    public static String Left;

    public static String Ancestor;

    public static String E_R_R_O_R;

    public static String Resources_with_different_connections_have_been_selected;

    public static String Choose_Editor;

    public static String Please_choose_the_editor_for_the_source_member;

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

    public static String Enter_or_select_a_simple_or_generic_member_type;

    public static String Enter_the_maximum_depth_of_subdirectories_to_be_searched;

    public static String Enter_or_select_an_IFS_directory;

    public static String Enter_or_select_a_simple_or_generic_stream_file_name;

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

    public static String Member_type_colon;

    public static String Specifies_the_generic_source_type_of_the_members_that_are_included_in_the_search;

    public static String Stream_file_type_colon;

    public static String Specifies_the_generic_type_of_the_stream_files_that_are_included_in_the_search;

    public static String Max_depth_colon;

    public static String Specifies_the_maximum_depth_of_sub_directories_included_in_the_search;

    public static String Warning_Maximum_depth_set_to_more_than_one_level;

    public static String ShowAllRecords;

    public static String IncludeFirstLevelText;

    public static String Specify_whether_or_not_to_include_the_first_level_message_text;

    public static String IncludeSecondLevelText;

    public static String Specify_whether_or_not_to_include_the_second_level_message_text;

    public static String No_objects_found_that_match_the_selection_criteria;

    public static String Library_A_not_found;

    public static String File_A_in_library_B_not_found;

    public static String Directory_not_found_A;

    public static String Stream_file_B_not_found_in_directory_A;

    public static String Select_Message_File;

    public static String Select_Object;

    public static String Object_A_in_library_B_not_found;

    public static String No_filter_pool_available;

    public static String Failed_to_save_data_to_file_colon_A;

    public static String Failed_to_load_data_from_file_colon_A;

    public static String Connection_A_not_found;

    public static String Cannot_copy_source_members_from_different_connections;

    public static String Selection_does_not_include_any_source_members;

    public static String Member_C_of_file_A_slash_B_is_locked_by_job_F_slash_E_slash_D;

    public static String Failed_to_connect_to_system_A;

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

    public static String Select_directory;

    public static String Saving_spooled_files;

    public static String Invalid_job_name_A;

    public static String Connection_not_found_A;

    public static String Connection_is_offline;

    public static String Label_Decorations_RSE_host_objects;

    public static String Label_Decorations_RSE_host_objects_Description;

    public static String Add_library_and_file_name_to_source_members;

    public static String Add_library_and_file_name_to_data_members;

    public static String Add_library_name_to_objects;

    public static String Title_Browse_For_Stream_File;

    public static String Label_Browse;

    public static String Label_Stream_file_colon;

    public static String Label_Directory_colon;

    public static String Title_Browse_For_Directory;

    public static String Search_argument_to_long_The_maximum_length_of_the_search_argument_is_A_characters;

    public static String Invalid_selection_Objects_must_be_of_the_same_type;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
