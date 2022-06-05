/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

import biz.isphere.base.internal.StringHelper;

@SuppressWarnings("serial")
public class DEditor implements Comparable<DEditor>, Serializable, IAdaptable {

    private static final int STEP_WIDTH = 10;
    private static final int HALF_STEP_WIDTH = STEP_WIDTH / 2;

    private String name;
    private String description;
    private Map<String, AbstractDWidget> widgets;
    private Map<String, DReferencedObject> referencedBy;
    private int columns;
    private String key;
    private boolean columnsEqualWidth;

    private transient DEditorPropertySource propertySource;

    DEditor(String name, int columns) {
        this(name, "", columns);
    }

    DEditor(String name, String description, int columns) {
        this.name = name;
        this.description = description;
        this.columns = columns;
        this.widgets = new HashMap<String, AbstractDWidget>();
        this.referencedBy = new HashMap<String, DReferencedObject>();
        this.key = UUID.randomUUID().toString();
        this.columnsEqualWidth = false;
    }

    public String getKey() {
        return key;
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

    public boolean isColumnsEqualWidth() {
        return columnsEqualWidth;
    }

    public String getNameAndDescription() {

        if (StringHelper.isNullOrEmpty(description)) {
            return name;
        }

        return name + " - " + description; //$NON-NLS-1$
    }

    public boolean isGenerated() {

        if (DataSpaceEditorManager.GENERATED.equals(name)) {
            return true;
        }

        return false;
    }

    public AbstractDWidget[] getWidgets() {
        AbstractDWidget[] sortedWidgets = new AbstractDWidget[widgets.size()];
        widgets.values().toArray(sortedWidgets);
        Arrays.sort(sortedWidgets);
        return sortedWidgets;
    }

    public AbstractDWidget getPreviousSibling(AbstractDWidget widget) {
        AbstractDWidget[] widgets = getWidgets();
        AbstractDWidget previousSibling = null;
        for (AbstractDWidget tWidget : widgets) {
            if (tWidget.getSequence() < widget.getSequence()) {
                previousSibling = tWidget;
            } else {
                break;
            }
        }
        return previousSibling;
    }

    public AbstractDWidget getNextSibling(AbstractDWidget widget) {
        AbstractDWidget[] widgets = getWidgets();
        AbstractDWidget nextSibling = null;
        for (AbstractDWidget tWidget : widgets) {
            if (tWidget.getSequence() > widget.getSequence()) {
                nextSibling = tWidget;
                break;
            }
        }
        return nextSibling;
    }

    public DReferencedObject[] getReferencedObjects() {
        DReferencedObject[] objects = new DReferencedObject[referencedBy.size()];
        referencedBy.values().toArray(objects);
        return objects;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (propertySource == null) {
                propertySource = new DEditorPropertySource(this);
            }
            return propertySource;
        }
        return null;
    }

    void addReferencedByObject(DReferencedObject object) {
        if (!referencedBy.containsKey(object.getKey())) {
            referencedBy.put(object.toString(), object);
            object.setParent(this);
        }
    }

    void removeReferencedByObject(DReferencedObject object) {
        if (referencedBy.containsKey(object.getKey())) {
            referencedBy.remove(object.getKey());
            object.setParent(null);
        }
    }

    void addWidget(AbstractDWidget widget) {
        if (widget.getSequence() < 1) {
            widget.setSequence(widgets.size() + 1);
        }

        if (!widgets.containsKey(widget.getKey())) {
            widgets.put(widget.getKey(), widget);
            widget.setParent(this);
        }
    }

    void removeWidget(AbstractDWidget widget) {
        if (widgets.containsKey(widget.getKey())) {
            widgets.remove(widget.getKey());
            widget.setParent(null);
        }
    }

    void moveUpWidget(AbstractDWidget widget, int positions) {
        prepareToMoveWidget();

        int sequenceNumber = widget.getSequence();
        sequenceNumber = sequenceNumber - (STEP_WIDTH * positions) - HALF_STEP_WIDTH;
        if (sequenceNumber <= 0) {
            sequenceNumber = HALF_STEP_WIDTH;
        }
        widget.setSequence(sequenceNumber);

        finishMovingWidget();
    }

    void moveDownWidget(AbstractDWidget widget, int positions) {
        prepareToMoveWidget();

        int sequenceNumber = widget.getSequence();
        sequenceNumber = sequenceNumber + (STEP_WIDTH * positions) + HALF_STEP_WIDTH;
        widget.setSequence(sequenceNumber);

        finishMovingWidget();
    }

    void setName(String name) {
        this.name = name;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setColumns(int columns) {
        this.columns = columns;
    }

    private void prepareToMoveWidget() {

        int sequenceNumber = STEP_WIDTH;

        AbstractDWidget[] widgets = getWidgets();
        for (int i = 0; i < widgets.length; i++) {
            AbstractDWidget widget = widgets[i];
            widget.setSequence(sequenceNumber);
            sequenceNumber += STEP_WIDTH;
        }
    }

    private void finishMovingWidget() {

        AbstractDWidget[] widgets = getWidgets();
        for (int i = 0; i < widgets.length; i++) {
            AbstractDWidget widget = widgets[i];
            widget.setSequence(i);
        }
    }

    public int compareTo(DEditor widget) {
        if (widget == null) {
            return 1;
        }
        return getName().compareTo(widget.getName());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" (");
        for (int i = 0; i < widgets.size(); i++) {
            if (i == 0) {
                sb.append(" ");
            } else {
                sb.append(" , ");
            }
            sb.append(widgets.values().toArray()[i].toString());
        }
        sb.append(" )");
        return sb.toString();
    }
}
