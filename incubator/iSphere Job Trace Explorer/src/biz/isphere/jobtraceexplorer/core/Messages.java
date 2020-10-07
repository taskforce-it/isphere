/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
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

    public static String ButtonLabel_Filter;
    public static String ButtonTooltip_Filter;

    public static String ColDesc_LongFieldName_QTITIMN;

    public static String Status_Loading_job_trace_entries;

    public static String Action_ReloadEntries;

    public static String DisplayJobTraceEntriesDialog_Title;
    public static String MessageDialog_Load_Job_Trace_Entries_Title;

    public static String Number_of_job_trace_entries_A_more_items_available;
    public static String Number_of_job_trace_entries_A_of_B;
    public static String Number_of_job_trace_entries_A;
    public static String subsetted_list;

    public static String Warning_Not_all_job_trace_entries_loaded;
    public static String Warning_Not_all_job_trace_entries_loaded_unknown_size;
    public static String Warning_Loading_job_trace_entries_has_been_canceled_by_the_user;

    public static String Exception_No_job_trace_entries_loaded_from_library_A_and_session_id_B;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
