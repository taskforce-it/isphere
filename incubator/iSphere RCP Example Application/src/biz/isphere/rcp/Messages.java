/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rcp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.rcp.messages"; //$NON-NLS-1$

    // Dialog Titles
    public static String Title_E_R_R_O_R;
    public static String Title_Select_Message_Files;

    // Button Labels
    public static String Button_Browse;

    // Input Field Labels
    public static String Label_Connection;
    public static String Label_Library;
    public static String Label_Message_File;

    // Error Messages
    public static String Error_Please_select_a_connection;
    public static String Error_Connection_A_not_available;
    public static String Error_Library_name_is_missing;
    public static String Error_Library_A_not_found;
    public static String Error_Message_file_name_is_missing;
    public static String Error_Message_file_B_in_library_A_not_found;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
