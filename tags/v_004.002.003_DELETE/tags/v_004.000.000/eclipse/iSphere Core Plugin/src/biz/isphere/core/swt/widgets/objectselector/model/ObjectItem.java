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
import com.ibm.as400.access.ObjectDescription;

public abstract class ObjectItem extends AbstractListItem {

    private static final String ASTERISK = "*";
    private String libraryName;
    private String objectType;
    private String description;

    public ObjectItem(AS400 system, String libraryName, String objectName, String objectType, Image image, String objectTypeFilter) {
        super(system, image, objectName, objectTypeFilter);

        this.libraryName = libraryName;
        this.objectType = objectType;
        this.description = null;
    }

    public String getLibrary() {
        return libraryName;
    }

    @Override
    public String getLabel() {
        return String.format("%s/%s (%s)", getLibrary(), getName(), getObjectType()); //$NON-NLS-1$
    }

    @Override
    public String getObjectType() {
        return objectType;
    }

    public String getDescription() {
        if (description == null) {
            description = resolveObjectDescription(getSystem(), libraryName, getName(), objectType);
        }
        return description;
    }

    protected String resolveObjectDescription(AS400 system, String library, String name, String type) {
        try {
            if (type.startsWith(ASTERISK)) {
                type = type.substring(1);
            }
            ObjectDescription objectDescription = new ObjectDescription(system, library, name, type);
            return objectDescription.getValueAsString(ObjectDescription.TEXT_DESCRIPTION);
        } catch (Exception e) {
            return ""; //$NON-NLS-1$
        }
    }

    void setDescription(String description) {
        this.description = description;
    }

    public boolean hasChildren() {
        return false;
    }
}
