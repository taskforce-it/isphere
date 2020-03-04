/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.util.LinkedList;
import java.util.List;

public enum IBMiDateFormat {
    YMD ("*YMD"),
    DMY ("*DMY"),
    MDY ("*MDY"),
    JUL ("*JUL");

    private String label;

    private static String[] formats;

    static {
        List<String> listFormats = new LinkedList<String>();
        for (IBMiDateFormat format : values()) {
            listFormats.add(format.label());
        }
        formats = listFormats.toArray(new String[listFormats.size()]);
    }

    private IBMiDateFormat(String label) {
        this.label = label;
    }

    public String label() {
        return this.label;
    }

    public static String[] getFormats() {
        return formats;
    }
}
