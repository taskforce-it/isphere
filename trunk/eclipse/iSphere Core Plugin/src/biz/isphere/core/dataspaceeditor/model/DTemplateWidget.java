/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.model;


public class DTemplateWidget {

    private Class<AbstractDWidget> widgetClass;
    private String label;
    private int offset;
    private int length;
    private int fraction;

    public DTemplateWidget(Class<AbstractDWidget> widgetClass, String label, int offset) {
        this(widgetClass, label, offset, -1, -1);
    }

    public DTemplateWidget(Class<AbstractDWidget> widgetClass, String label, int offset, int length) {
        this(widgetClass, label, offset, length, -1);
    }

    public DTemplateWidget(Class<AbstractDWidget> widgetClass, String label, int offset, int length, int fraction) {
        this.widgetClass = widgetClass;
        this.label = label;
        this.offset = offset;
        this.length = length;
        this.fraction = fraction;
    }

    public Class<AbstractDWidget> getWidgetClass() {
        return widgetClass;
    }

    public String getLabel() {
        return label;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getFraction() {
        return fraction;
    }

    @Override
    public String toString() {
        return widgetClass.getSimpleName() + "(" + label + ", " + offset + ", " + length + ", " + fraction + ")";
    }
}
