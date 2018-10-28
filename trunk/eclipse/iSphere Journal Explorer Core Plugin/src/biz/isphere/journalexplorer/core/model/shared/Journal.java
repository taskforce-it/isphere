/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.shared;

public class Journal {

    private String connectionName;
    private String name;
    private String library;

    public Journal(String connectionName, String libraryName, String journalName) {
        this.connectionName = connectionName;
        this.library = libraryName;
        this.name = journalName;
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

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;

        result = prime * result + ((connectionName == null) ? 0 : connectionName.hashCode());
        result = prime * result + ((library == null) ? 0 : library.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Journal other = (Journal)obj;
        if (!equals(connectionName, other.connectionName)) {
            return false;
        }

        if (!equals(library, other.library)) {
            return false;
        }

        if (!equals(name, other.name)) {
            return false;
        }

        return true;
    }

    private boolean equals(String thisValue, String otherValue) {

        if (thisValue == null) {
            if (otherValue != null) {
                return false;
            }
        } else if (!thisValue.equals(otherValue)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(connectionName);
        buffer.append(": ");
        buffer.append(library);
        buffer.append("/");
        buffer.append(name);

        return buffer.toString();
    }
}
