/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import java.util.Map;

import biz.isphere.core.Messages;

public class GenericSearchOption implements Map.Entry<String, Object> {

    // Source member search options
    public static final GenericSearchOption.Key SRCMBR_SRC_TYPE = GenericSearchOption.Key.SRCMBR_SRC_TYPE;

    // Message file search options
    public static final GenericSearchOption.Key MSGF_INCLUDE_MESSAGE_ID = GenericSearchOption.Key.MSGF_INCLUDE_MESSAGE_ID;
    public static final GenericSearchOption.Key MSGF_INCLUDE_SECOND_LEVEL_TEXT = GenericSearchOption.Key.MSGF_INCLUDE_SECOND_LEVEL_TEXT;
    public static final GenericSearchOption.Key MSGF_INCLUDE_FIRST_LEVEL_TEXT = GenericSearchOption.Key.MSGF_INCLUDE_FIRST_LEVEL_TEXT;

    private GenericSearchOption.Key key;
    private Object value;

    public GenericSearchOption(GenericSearchOption.Key key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key.getKeyValue();
    }

    public String getKeyAsText() {

        if (GenericSearchOption.MSGF_INCLUDE_FIRST_LEVEL_TEXT.equals(key)) {
            return Messages.GenericSearchOption_MsgF_IncludeFirstLevelText;
        } else if (GenericSearchOption.MSGF_INCLUDE_SECOND_LEVEL_TEXT.equals(key)) {
            return Messages.GenericSearchOption_MsgF_IncludeSecondLevelText;
        } else if (GenericSearchOption.MSGF_INCLUDE_MESSAGE_ID.equals(key)) {
            return Messages.GenericSearchOption_MsgF_IncludeMessageId;
        } else if (GenericSearchOption.SRCMBR_SRC_TYPE.equals(key)) {
            return Messages.GenericSearchOption_SrcMbr_SourceType;
        } else {
            return "*ERROR"; //$NON-NLS-1$
        }
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsText() {

        if (value instanceof Boolean) {
            return ((Boolean)value).toString();
        } else if (value instanceof String) {
            return (String)value;
        } else {
            return "*ERROR"; //$NON-NLS-1$
        }
    }

    public Object setValue(Object value) {
        Object oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    public String toText() {
        return getKeyAsText() + ": " + getValueAsText(); //$NON-NLS-1$        
    }

    public enum Key {
        SRCMBR_SRC_TYPE ("SRC_TYPE"),
        MSGF_INCLUDE_MESSAGE_ID ("INCLUDE_MESSAGE_ID"),
        MSGF_INCLUDE_SECOND_LEVEL_TEXT ("INCLUDE_SECOND_LEVEL_TEXT"),
        MSGF_INCLUDE_FIRST_LEVEL_TEXT ("INCLUDE_FIRST_LEVEL_TEXT");

        private String keyValue;

        private Key(String keyValue) {
            this.keyValue = keyValue;
        }

        public String getKeyValue() {
            return keyValue;
        }
    }
}
