/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import biz.isphere.core.swt.widgets.objectselector.ISelectedObject;

public class SelectedObject implements ISelectedObject {

    private String connectionName;
    private String libraryName;
    private String objectName;
    private String objectType;
    private String description;

    public SelectedObject(String connectionName, String libraryName, String objectName, String objectType) {
        this(connectionName, libraryName, objectName, objectType, ""); //$NON-NLS-1$
    }

    public SelectedObject(String connectionName, String libraryName, String objectName, String objectType, String description) {
        this.connectionName = connectionName;
        this.libraryName = libraryName;
        this.objectName = objectName;
        this.objectType = objectType;
        this.description = description;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getLibrary() {
        return libraryName;
    }

    public String getName() {
        return objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getDescription() {
        return description;
    }
}
