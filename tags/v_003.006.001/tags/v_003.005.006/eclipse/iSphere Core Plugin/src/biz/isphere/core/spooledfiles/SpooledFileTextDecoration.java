/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.util.HashMap;

import biz.isphere.core.preferences.Preferences;

public enum SpooledFileTextDecoration {
    STATUS ("1", "&SPLF - &STATUS"),
    CREATION_TIME ("2", "&SPLF - &STATUS - &CDATE-&CTIME"),
    USER_DATA ("3", "&SPLF - &STATUS - &USRDTA"),
    JOB ("4", "&SPLF - &STATUS - &JOBNAME/&JOBUSR/&JOBNBR"),
    USER_DEFINED ("5", null);

    private String key;
    private String mask;

    private static HashMap<String, SpooledFileTextDecoration> listByKeys;
    private static final SpooledFileTextDecoration DEFAULT_DECORATION_STYLE = STATUS;

    static {
        listByKeys = new HashMap<String, SpooledFileTextDecoration>();
        listByKeys.put(STATUS.getKey(), STATUS);
        listByKeys.put(CREATION_TIME.getKey(), CREATION_TIME);
        listByKeys.put(USER_DATA.getKey(), USER_DATA);
        listByKeys.put(JOB.getKey(), JOB);
        listByKeys.put(USER_DEFINED.getKey(), USER_DEFINED);
    }

    private SpooledFileTextDecoration(String key, String mask) {
        this.key = key;
        this.mask = mask;
    }

    public String getKey() {
        return key;
    }

    public String createDecoration(SpooledFile splf) {

        String tMask;
        if (mask == null) {
            tMask = Preferences.getInstance().getSpooledFileRSEDescription();
        } else {
            tMask = mask;
        }

        String decoration = splf.replaceVariables(tMask);

        return decoration;
    }

    public static boolean isValidKey(String key) {

        if (STATUS.getKey().equals(key) || CREATION_TIME.getKey().equals(key) || USER_DATA.getKey().equals(key) || JOB.getKey().equals(key) || USER_DEFINED.getKey().equals(key)) {
            return true;
        }

        return false;
    }

    public static SpooledFileTextDecoration getDefaultDecorationStyle() {
        return DEFAULT_DECORATION_STYLE;
    }

    public static SpooledFileTextDecoration getDecorationStyleByKey(String key) {

        SpooledFileTextDecoration decorationStyle = listByKeys.get(key);
        if (decorationStyle == null) {
            decorationStyle = DEFAULT_DECORATION_STYLE;
        }
        return decorationStyle;
    }
}
