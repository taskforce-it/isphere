/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.tn5250j.settings;

public enum ColorProperty {
    BACKGROUND ("colorBg"),
    BLUE ("colorBlue"),
    RED ("colorRed"),
    PINK ("colorPink"),
    GREEN ("colorGreen"),
    TURQUISE ("colorTurq"),
    YELLOW ("colorYellow"),
    WHITE ("colorWhite"),
    GUI_FIELD ("colorGUIField"),
    CURSOR ("colorCursor"),
    SEPARATOR ("colorSep"),
    HEX_ATTR ("colorHexAttr");

    private String key;

    private ColorProperty(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
