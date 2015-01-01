/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

import java.io.Serializable;
import java.util.Vector;

import biz.isphere.core.dataspaceeditordesigner.listener.DataModifiedEvent;
import biz.isphere.core.dataspaceeditordesigner.listener.IWidgetModifyListener;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

@SuppressWarnings("serial")
public abstract class AbstractDWidget implements Comparable<AbstractDWidget>, Serializable {

    private String label;
    private int offset;
    private int length;
    private int sequence;
    private int horizontalSpan;

    @XStreamOmitField
    private String key;
    @XStreamOmitField
    private Vector<IWidgetModifyListener> modifyListener;
    @XStreamOmitField
    private DEditor dEditor;

    AbstractDWidget() {
        this("", 0, 0);
    }

    AbstractDWidget(String label, int offset, int maxLength) {
        this.label = label;
        this.offset = offset;
        this.length = maxLength;
        this.sequence = -1;
        this.horizontalSpan = 1;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
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

    public int getHorizontalSpan() {
        if (horizontalSpan <= 0) {
            return horizontalSpan = 1;
        }
        return horizontalSpan;
    }

    public DEditor getParent() {
        return dEditor;
    }

    public boolean isFirst() {
        if (getParent().getPreviousSibling(this) == null) {
            return true;
        }
        return false;
    }

    public boolean isLast() {
        if (getParent().getNextSibling(this) == null) {
            return true;
        }
        return false;
    }

    void setLabel(String label) {
        this.label = label;
    }

    void setOffset(int offset) {
        this.offset = offset;
    }

    void setLength(int length) {
        this.length = length;
    }

    void setHorizontalSpan(int horizontalSpan) {
        this.horizontalSpan = horizontalSpan;
    }

    void setParent(DEditor dEditor) {
        this.dEditor = dEditor;
    }

    public void addModifyListener(IWidgetModifyListener listener) {
        if (modifyListener == null) {
            modifyListener = new Vector<IWidgetModifyListener>();
        }
        modifyListener.add(listener);
    }

    protected void fireDataModifiedEvent(DataModifiedEvent event) {
        for (IWidgetModifyListener listener : modifyListener) {
            listener.dataModified(event);
        }
    }

    public String getKey() {
        if (sequence < 1) {
            return null;
        }

        if (key == null) {
            key = "" + getSequence();
        }
        return key;
    }

    public int compareTo(AbstractDWidget widget) {
        if (widget == null || getSequence() > widget.getSequence()) {
            return 1;
        } else if (getSequence() < widget.getSequence()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "('" + label + "', length=" + length + ")";
    }
}
