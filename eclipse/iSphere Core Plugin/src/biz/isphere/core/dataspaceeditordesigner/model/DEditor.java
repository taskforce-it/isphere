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

@SuppressWarnings("serial")
public class DEditor implements Comparable<DEditor>, Serializable {

    private String name;
    private String description;
    private Map<String, AbstractDWidget> widgets;
    private Map<String, DReferencedObject> referencedBy;
    private int columns;
    private String key;

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

    public AbstractDWidget[] getWidgets() {
        AbstractDWidget[] sortedWidgets = new AbstractDWidget[widgets.size()];
        widgets.values().toArray(sortedWidgets);
        Arrays.sort(sortedWidgets);
        return sortedWidgets;
    }

    public DReferencedObject[] getReferencedObjects() {
        DReferencedObject[] objects = new DReferencedObject[referencedBy.size()];
        referencedBy.values().toArray(objects);
        return objects;
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
        }
    }

    void removeWidget(AbstractDWidget widget) {
        if (widgets.containsKey(widget.getKey())) {
            widgets.remove(widget.getKey());
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
