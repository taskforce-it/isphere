/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.model;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class JournalEntryColumn {

    private IJournalEntryColumn columnDef;
    private boolean resizable;
    private boolean moveable;

    private int style;
    private String text;
    private String tooltipText;
    private int width;
    private Color color;

    private JournalEntryColumn(IJournalEntryColumn columnDef) {
        this.columnDef = columnDef;
        this.resizable = true;
        this.moveable = true;

        this.style = SWT.NONE;
        this.text = ""; //$NON-NLS-1$
        this.tooltipText = ""; //$NON-NLS-1$
        this.width = 90;

        this.color = null;
    }

    public JournalEntryColumn(IJournalEntryColumn columnDef, String tooltipText, int width) {
        this(columnDef, columnDef.name(), tooltipText, width, SWT.NONE);
    }

    public JournalEntryColumn(IJournalEntryColumn columnDef, String tooltipText, int width, int style) {
        this(columnDef, columnDef.name(), tooltipText, width, style);
    }

    public JournalEntryColumn(IJournalEntryColumn columnDef, String text, String tooltipText, int width) {
        this(columnDef, text, tooltipText, width, SWT.NONE);
    }

    public JournalEntryColumn(IJournalEntryColumn columnDef, String text, String tooltipText, int width, int style) {
        this(columnDef);

        this.text = text;
        this.tooltipText = tooltipText;
        this.width = width;
        this.style = style;
    }

    public IJournalEntryColumn getColumnDef() {
        return columnDef;
    }

    public String getName() {
        return this.columnDef.columnName();
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getText() {
        return notNull(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTooltipText() {
        return notNull(tooltipText);
    }

    public void setTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public boolean isMovebale() {
        return moveable;
    }

    public void setMovebale(boolean moveable) {
        this.moveable = moveable;
    }

    private String notNull(String text) {

        if (text == null) {
            return ""; //$NON-NLS-1$
        }

        return text;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
