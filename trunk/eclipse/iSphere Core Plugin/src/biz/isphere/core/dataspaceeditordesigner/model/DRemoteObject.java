/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

public class DRemoteObject {

    private String name;
    private String library;
    private String type;
    private String connectionName;

    public DRemoteObject(String connectionName, String name, String library, String type) {
        this.connectionName = connectionName;
        this.name = name;
        this.library = library;
        this.type = type;
    }

    public String getConnectionName() {
        return connectionName;
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

    @Override
    public String toString() {
        return connectionName + "(" + library + "/" + name + "(" + type + "))";
    }
}
