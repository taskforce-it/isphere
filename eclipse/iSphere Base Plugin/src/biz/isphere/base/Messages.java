/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.base.messages";

    public static String E_Mail;

    public static String Internet;

    public static String Telefax;

    public static String Telefon;

    public static String Obsolete_Bundles_Warning_Message_Text;

    public static String Obsolete_Bundles_Warning_Message_Message;

    public static String Reset_Column_Size;

    public static String Tooltip_Reset_Column_Size;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

}
