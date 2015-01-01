/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

/**
 * The RemoteObject class is used by the RDI and WDSCi plug-in as an adapter to
 * pass QSYSRemoteObjects (RDi) and ISeriesObjects (WDSCi) to the core plug-in.
 */
public class RemoteObject {

    private String connectionName;
    private String name;
    private String library;
    private String objectType;
    private String description;

    public RemoteObject(String connectionName, String name, String library, String objectType, String description) {
        this.connectionName = connectionName;
        this.name = name;
        this.library = library;
        this.objectType = objectType;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public String getQuaifiedObject() {
        return library + "/" + name + " (" + objectType + ")";
    }

    @Override
    public String toString() {
        return connectionName + "(" + library + "/" + name + "(" + objectType + "))";
    }
}
