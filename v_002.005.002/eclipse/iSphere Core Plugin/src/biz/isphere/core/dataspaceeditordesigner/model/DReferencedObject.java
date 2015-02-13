/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

@SuppressWarnings("serial")
public class DReferencedObject implements Comparable<DReferencedObject>, Serializable {

    private String name;
    private String library;
    private String type;
    private boolean isDefault;

    @XStreamOmitField
    private DEditor parent;

    DReferencedObject(String name, String library, String type) {
        this.name = name;
        this.library = library;
        this.type = type;
        this.isDefault = false;
    }

    public String getName() {
        return name;
    }

    public String getLibrary() {
        return library;
    }

    public String getType() {
        return type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getKey() {
        return library + "/" + name + "(" + type + ")";
    }

    public DEditor getParent() {
        return parent;
    }

    void setParent(DEditor parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return library + "/" + name + "(" + type + ")";
    }

    public int compareTo(DReferencedObject object) {
        if (object == null) {
            return 1;
        }

        int result = library.compareTo(object.getLibrary());
        if (result != 0) {
            return result;
        }

        result = name.compareTo(object.getName());
        if (result != 0) {
            return result;
        }

        result = type.compareTo(object.getType());
        if (result != 0) {
            return result;
        }

        return 0;
    }
}
