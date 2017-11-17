/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import org.eclipse.swt.graphics.Color;

import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.labelproviders.JournalEntryAppearanceAttributesLabelProvider;
import biz.isphere.journalexplorer.core.ui.widgets.JournalEntryAppearanceAttributesEditor;

/**
 * This class holds the persisted appearance attributes of a journal entry
 * column.
 * 
 * @see Preferences
 * @see JournalEntryAppearanceAttributesLabelProvider
 * @see JournalEntryAppearanceAttributesEditor
 */
public class JournalEntryAppearanceAttributes {

    private String columnName;
    private Color color;

    public JournalEntryAppearanceAttributes(String columnName, Color color) {
        this.columnName = columnName;
        this.color = color;
    }

    public String getColumnName() {
        return columnName;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
