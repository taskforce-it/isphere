/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.labelproviders;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;

public class JournalEntryAppearanceLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

    public Image getColumnImage(Object object, int index) {
        return null;
    }

    public String getColumnText(Object object, int index) {

        JournalEntryColumn columnColorEntry = (JournalEntryColumn)object;

        switch (index) {
        case 0: // Name
            return columnColorEntry.getName();
        case 1: // Description
            return columnColorEntry.getTooltipText();
        case 2: // Color
            return ""; // columnColorEntry.getColor().toString();
        default:
            break;
        }

        return null;
    }

    public Color getBackground(Object object, int index) {

        if (index != 2) {
            return null;
        }

        JournalEntryColumn columnColorEntry = (JournalEntryColumn)object;

        return columnColorEntry.getColor();
    }

    public Color getForeground(Object object, int index) {
        // TODO Auto-generated method stub
        return null;
    }

}
