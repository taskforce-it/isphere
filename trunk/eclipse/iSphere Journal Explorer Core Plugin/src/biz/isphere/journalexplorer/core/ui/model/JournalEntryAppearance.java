/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import org.eclipse.swt.graphics.Color;

public class JournalEntryAppearance {

    private String columnName;
    private Color color;

    public JournalEntryAppearance(String columnName, Color color) {
        this.columnName = columnName;
        this.color = color;
    }

    public String getColumnName() {
        return columnName;
    }

    public Color getColor() {
        return color;
    }

}
