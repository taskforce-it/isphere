/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import org.eclipse.swt.graphics.Image;

import com.ibm.as400.access.AS400;

public abstract class AbstractListItem {

    private static final String OBJECT_TYPE_VIRTUELL = "*VIRTUELL"; //$NON-NLS-1$
    private AS400 system;
    private Image image;
    private String name;
    private String objectTypeFilter;

    private AbstractListItem[] children;

    public AbstractListItem(AS400 system, Image image, String name, String objectTypeFilter) {
        this.system = system;
        this.image = image;
        this.name = name;
        this.objectTypeFilter = objectTypeFilter;
    }

    protected AS400 getSystem() {
        return system;
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    protected String getObjectTypeFilter() {
        return objectTypeFilter;
    }

    public String getObjectType() {
        return OBJECT_TYPE_VIRTUELL;
    }

    public AbstractListItem[] getChildren() {

        if (children == null) {
            children = resolveChildren();
        }

        return children;
    }

    public abstract String getLabel();

    public abstract boolean hasChildren();

    public abstract AbstractListItem[] resolveChildren();
}
