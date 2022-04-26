/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.internal.ISeries;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.ObjectList;

public class LibraryItem extends ObjectItem {

    private String description;

    public LibraryItem(AS400 system, String libraryName, Image image, String objectTypeFilter) {
        super(system, ISeries.QSYS_LIBRARY, libraryName, ISeries.LIB, image, objectTypeFilter);

        this.description = null;
    }

    @Override
    public String getLabel() {
        return super.getName();
    }

    public boolean hasChildren() {
        return true;
    }

    public String getDescription() {
        if (description == null) {
            description = resolveLibraryDescription(getSystem(), getName());
        }
        return description;
    }

    protected String resolveLibraryDescription(AS400 system, String library) {
        return resolveObjectDescription(system, ISeries.QSYS_LIBRARY, getName(), ISeries.LIB);
    }

    public AbstractListItem[] resolveChildren() {

        List<AbstractListItem> children = new LinkedList<AbstractListItem>();

        try {

            ObjectList listObjects = new ObjectList(getSystem(), getName(), ObjectList.ALL, getObjectTypeFilter());
            Enumeration<?> objects = listObjects.getObjects();
            while (objects.hasMoreElements()) {
                Object object = (Object)objects.nextElement();
                if (object instanceof ObjectDescription) {
                    ObjectDescription objectDescription = (ObjectDescription)object;

                    String library = objectDescription.getValueAsString(ObjectDescription.LIBRARY);
                    String name = objectDescription.getValueAsString(ObjectDescription.NAME);
                    String type = objectDescription.getValueAsString(ObjectDescription.TYPE);
                    String description = objectDescription.getValueAsString(ObjectDescription.TEXT_DESCRIPTION);

                    ObjectItem objectItem = ListItemsFactory.createObject(getSystem(), library, name, type, getObjectTypeFilter());
                    objectItem.setDescription(description);
                    children.add(objectItem);
                }
            }

        } catch (Exception e) {
            children.add(new ErrorItem(ExceptionHelper.getLocalizedMessage(e)));
        }

        return children.toArray(new AbstractListItem[children.size()]);
    }
}
