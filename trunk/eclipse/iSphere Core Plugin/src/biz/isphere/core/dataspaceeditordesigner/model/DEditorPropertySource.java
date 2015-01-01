/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.core.Messages;

public class DEditorPropertySource implements IPropertySource {

    private DEditor dEditor;
    private IPropertyDescriptor[] propertyDescriptors;

    private static final String PROPERTY_NAME = "biz.isphere.core.dataspaceeditordesigner.model.DEditor.name";
    private static final String PROPERTY_DESCRIPTION = "biz.isphere.core.dataspaceeditordesigner.model.DEditor.description";
    private static final String PROPERTY_COLUMNS = "biz.isphere.core.dataspaceeditordesigner.model.DEditor.columns";

    public DEditorPropertySource(DEditor dEditor) {
        this.dEditor = dEditor;
    }

    public Object getEditableValue() {
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (propertyDescriptors == null) {

            PropertyDescriptor sizeDescriptor = new PropertyDescriptor(PROPERTY_NAME, Messages.Name);
            PropertyDescriptor textDescriptor = new PropertyDescriptor(PROPERTY_DESCRIPTION, Messages.Description);
            PropertyDescriptor columnsDescriptor = new PropertyDescriptor(PROPERTY_COLUMNS, Messages.Columns);

            propertyDescriptors = new IPropertyDescriptor[] { sizeDescriptor, textDescriptor, columnsDescriptor };
        }
        return propertyDescriptors;
    }

    public Object getPropertyValue(Object name) {
        if (name.equals(PROPERTY_NAME)) {
            return dEditor.getName();
        } else if (name.equals(PROPERTY_DESCRIPTION)) {
            return dEditor.getDescription();
        } else if (name.equals(PROPERTY_COLUMNS)) {
            return dEditor.getColumns();
        }
        return null;
    }

    public boolean isPropertySet(Object name) {
        return getPropertyValue(name) != null;
    }

    public void resetPropertyValue(Object arg0) {

    }

    public void setPropertyValue(Object arg0, Object arg1) {

    }
}
