/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.rse;


public class RemoteObject {

    private String connectionName;
    private String name;
    private String library;
    private String objectType;

    public RemoteObject(String connectionName, String name, String library, String objectType) {
        this.connectionName = connectionName;
        this.name = name;
        this.library = library;
        this.objectType = objectType;
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

    public String getObjectType() {
        return objectType;
    }
    
    @Override
    public String toString() {
        return connectionName + "(" + library + "/" + name + "(" + objectType + "))";
    }
}
