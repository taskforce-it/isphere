/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.joblogexplorer.messages"; //$NON-NLS-1$

    public static String Dropped_object_does_not_match_expected_type;

    public static String Job_Parsing_job_log;

    public static String Job_Log_Explorer;

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

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
