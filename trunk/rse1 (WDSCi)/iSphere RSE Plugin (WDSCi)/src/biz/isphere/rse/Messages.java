/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.rse.messages";

    public static String iSphere_Message_File_Editor;

    public static String iSphere_Compare_Editor;

    public static String Right;

    public static String Ancestor;

    public static String iSphere_Binding_Directory_Editor;

    public static String E_R_R_O_R;

    public static String Resources_with_different_connections_have_been_selected;

    public static String iSphere_Message_File_Search;

    public static String iSphere_Source_File_Search;

    public static String Deleting_spooled_files;

    public static String Deleting;

    public static String Search_string;
    
    public static String String_to_be_searched;
    
    public static String Enter_or_select_search_string;
    
    public static String Case_sensitive;
    
    public static String Specify_whether_case_should_be_considered_during_search;
    
    public static String Connection;
    
    public static String Target;
    
    public static String Enter_or_select_a_library_name;
    
    public static String Enter_or_select_a_simple_or_generic_message_file_name;
    
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

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
