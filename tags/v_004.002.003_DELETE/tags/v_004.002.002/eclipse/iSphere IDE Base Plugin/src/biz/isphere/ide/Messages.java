/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.ide;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.ide.messages"; //$NON-NLS-1$

    public static String E_R_R_O_R;

    public static String Could_not_get_RSE_connection_A;

    public static String Question;

    public static String Source_member_contains_unsaved_changes_Save_member_A;

    public static String Could_not_download_member_2_of_file_1_of_library_0;

    public static String Menu_Compare;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
