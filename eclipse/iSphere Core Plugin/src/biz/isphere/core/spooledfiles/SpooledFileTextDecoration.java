/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.util.HashMap;
import java.util.Map;

public enum SpooledFileTextDecoration {
    STATUS ("1", "Status", "&SPLF - &STATUS"),
    CREATION_TIME ("2", "Creation time", "&SPLF - &STATUS - &CDATE - &CTIME"),
    USER_DATA ("3", "User data", "&SPLF - &STATUS : '&USRDTA'");

    private static final String T_SPLF = "&SPLF";
    private static final String T_STATUS = "&STATUS";
    private static final String T_CDATE = "&CDATE";
    private static final String T_CTIME = "&CTIME";
    private static final String T_CTIME_STAMP = "&CTIMESTAMP";
    private static final String T_USRDTA = "&USRDTA";

    private String key;
    private String label;
    private String mask;

    private static HashMap<String, SpooledFileTextDecoration> listByKeys;
    private static Map<String, SpooledFileTextDecoration> listByLabels;
    private static final SpooledFileTextDecoration DEFAULT_DECORATION_STYLE = STATUS;

    static {
        listByKeys = new HashMap<String, SpooledFileTextDecoration>();
        listByKeys.put(STATUS.getKey(), STATUS);
        listByKeys.put(CREATION_TIME.getKey(), CREATION_TIME);
        listByKeys.put(USER_DATA.getKey(), USER_DATA);

        listByLabels = new HashMap<String, SpooledFileTextDecoration>();
        listByLabels.put(STATUS.getLabel(), STATUS);
        listByLabels.put(CREATION_TIME.getLabel(), CREATION_TIME);
        listByLabels.put(USER_DATA.getLabel(), USER_DATA);
    }

    private SpooledFileTextDecoration(String key, String label, String mask) {
        this.key = key;
        this.label = label;
        this.mask = mask;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String createDecoration(SpooledFile splf) {

        String decoration = replaceReplacementVariables(splf);

        return decoration;
    }

    public static boolean isValidKey(String key) {

        if (STATUS.getKey().equals(key) || CREATION_TIME.getKey().equals(key) || USER_DATA.getKey().equals(key)) {
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

    public static SpooledFileTextDecoration getDecorationStyleByLabel(String label) {

        SpooledFileTextDecoration decorationStyle = listByLabels.get(label);
        if (decorationStyle == null) {
            decorationStyle = DEFAULT_DECORATION_STYLE;
        }

        return decorationStyle;
    }

    private String replaceReplacementVariables(SpooledFile splf) {

        String decorationText = mask;
        decorationText = decorationText.replaceAll(T_SPLF, splf.getFile());
        decorationText = decorationText.replaceAll(T_STATUS, splf.getStatus());
        decorationText = decorationText.replaceAll(T_CTIME_STAMP, splf.getCreationTimestampFormatted());
        decorationText = decorationText.replaceAll(T_CDATE, splf.getCreationDateFormatted());
        decorationText = decorationText.replaceAll(T_CTIME, splf.getCreationTimeFormatted());
        decorationText = replaceOptionally(decorationText, T_USRDTA, splf.getUserData());

        return decorationText;
    }

    private String replaceOptionally(String decorationText, String variableName, String replacementData) {

        // if (StringHelper.isNullOrEmpty(replacementData)) {
        // return decorationText;
        // }

        return decorationText.replaceAll(variableName, replacementData);
    }
}
