/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

public class DTemplateEditor {

    private String name;
    private String description;
    private int columns;

    public DTemplateEditor(String label, int columns) {
        this(label, "", columns);
    }

    public DTemplateEditor(String label, String description, int columns) {
        this.name = label;
        this.description = description;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public String toString() {
        return name + "(" + columns + ")";
    }
}
