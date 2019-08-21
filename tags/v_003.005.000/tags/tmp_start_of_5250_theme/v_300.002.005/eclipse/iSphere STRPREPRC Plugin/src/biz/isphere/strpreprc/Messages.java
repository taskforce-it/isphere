/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.strpreprc.messages"; //$NON-NLS-1$

    public static final String EMPTY = ""; //$NON-NLS-1$

    public static String E_R_R_O_R;

    public static String Command_A_is_invalid_or_could_not_be_found_The_original_error_message_is_B;

    public static String Could_not_get_the_active_editor;

    public static String Could_not_get_AS400_object_for_connection_A;

    public static String Menu_Edit_header;

    public static String Menu_Add_pre_compile_command;

    public static String Menu_Add_post_compile_command;

    public static String Menu_Edit_Pre_Post_Command;

    public static String Menu_Remove_header;

    public static String Button_Prompt;

    public static String Browse;

    public static String Directory_must_not_be_empty;

    public static String The_specified_directory_does_not_exist;

    public static String Could_not_save_A_files_to_directory_B;

    public static String Create_STRPREPRC_Header;

    public static String Sections_IMPORTANT_COMPILE_and_LINK_are_removed_when_updating_the_STRPREPRC_header_Change_preferences;

    public static String STRPREPRC_header_not_found_or_incomplete;

    public static String Remove_SPRPREPRC_header_from_source_member;

    public static String Missing_connection_name;

    public static String Could_not_create_AS400_object;

    public static String Missing_object_creation_command;

    public static String Invalid_replacement_variable_A_found_at_position_B_command_C;

    public static String Use_parameter_sections_colon;

    public static String Tooltip_Use_parameter_sections;

    public static String Add_header;

    public static String Default_section_colon;

    public static String Tooltip_Default_section;

    public static String Use_template_directory_colon;

    public static String Tooltip_Use_template_directory_colon;

    public static String Templates_directory_colon;

    public static String Tooltip_Templates_directory_colon;

    public static String Edit_header;

    public static String Skip_edit_dialog_colon;

    public static String Tooltip_Skip_edit_dialog_colon;

    public static String Export;

    public static String Templates_successfully_exported_to_folder_colon;

    public static String Tooltip_Export;

    public static String Clear_Cache;

    public static String Tooltip_Clear_Cache;

    public static String Cached_templates_A;

    public static String Pre_or_post_command_not_found_at_line_A;

    public static String RPLVAR_LI;

    public static String RPLVAR_OB;

    public static String RPLVAR_TY;

    public static String RPLVAR_SL;

    public static String RPLVAR_SF;

    public static String RPLVAR_SM;

    public static String RPLVAR_TL;

    public static String RPLVAR_TO;

    public static String RPLVAR_TR;

    public static String RPLVAR_FL;

    public static String RPLVAR_FF;

    public static String RPLVAR_FM;

    public static String RPLVAR_U0;

    public static String RPLVAR_U1;

    public static String RPLVAR_U2;

    public static String RPLVAR_U3;

    public static String RPLVAR_U4;

    public static String RPLVAR_U5;

    public static String RPLVAR_U6;

    public static String RPLVAR_U7;

    public static String RPLVAR_U8;

    public static String RPLVAR_U9;

    public static String Connection_colon;

    public static String Command_colon;

    public static String Parameters_colon;

    public static String Tooltip_Header_Text;

    public static String Label_Insert_Variable;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
