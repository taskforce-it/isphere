/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

public class BindingDirectoryEntry {

    private String connectionName;
    private String library;
    private String object;
    private String objectType;
    private String activation;

    public BindingDirectoryEntry() {
        connectionName = "";
        library = "";
        object = "";
        objectType = "";
        activation = "";
    }

    public String getConnection() {
        return connectionName;
    }

    public void setConnection(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

}
