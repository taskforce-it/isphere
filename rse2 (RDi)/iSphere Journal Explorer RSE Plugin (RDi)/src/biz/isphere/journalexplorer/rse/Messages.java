/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.rse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.journalexplorer.rse.messages"; //$NON-NLS-1$

    public static String E_R_R_O_R;

    public static String DisplayJournalEntriesDialog_Title;
    public static String Label_Connection;
    public static String Label_Object_type;

    public static String Error_Connection_not_found_A;
    public static String Error_Invalid_object_type_A;
    public static String Error_Library_A_not_found;
    public static String Error_Object_A_of_type_C_in_library_B_not_found;
    public static String Error_Member_2_of_file_1_in_library_0_not_found;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
