/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import com.ibm.as400.access.QSYSObjectPathName;

/**
 * The RemoteObject class is used by the RDI and WDSCi plug-in as an adapter to
 * pass QSYSRemoteObjects (RDi) and ISeriesObjects (WDSCi) to the core plug-in.
 */
public class RemoteObject {

    // private static final String PATTERN =
    // "^([^:]+):([^/]+)/([^(]+)\\(([^)]+)\\)(?::(.+))?$";
    // private static final int GROUP_CONNECTION = 1;
    // private static final int GROUP_LIBRARY = 2;
    // private static final int GROUP_NAME = 3;
    // private static final int GROUP_OBJECT_TYPE = 4;
    // private static final int GROUP_DESCRIPTION = 5;

    private String connectionName;
    private String name;
    private String library;
    private String objectType;
    private String description;

    // private Pattern pattern;

    public RemoteObject(String connectionName, String name, String library, String objectType, String description) {
        this.connectionName = connectionName;
        this.name = name;
        this.library = library;
        this.objectType = objectType;
        this.description = description;

        if (!objectType.startsWith("*")) {
            throw new IllegalArgumentException("Invalid object type. Object type must start with an asterisk: " + objectType);
        }
    }

    /*
     * Produces a RemoteObject from a given absolute name. The format of an
     * absolute name is: <p> connection:library/object(objType)[:description <p>
     * The 'description' portion is optionally.
     * @param absoluteName - absolute name of the remote object
     */
    /*
     * Not yet or no longer used?
     */
    // private RemoteObject(String absoluteName) {
    // Pattern pattern = getPattern();
    // Matcher matcher = pattern.matcher(absoluteName);
    // if (matcher.find()) {
    // connectionName = matcher.group(GROUP_CONNECTION);
    // name = matcher.group(GROUP_LIBRARY);
    // library = matcher.group(GROUP_NAME);
    // objectType = matcher.group(GROUP_OBJECT_TYPE);
    // description = matcher.group(GROUP_DESCRIPTION);
    // }
    // }

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

    public String getQualifiedObject() {
        return library + "/" + name + " (" + objectType + ")";
    }

    public String getAbsoluteName() {
        return connectionName + ":" + library + "/" + name + "(" + objectType + ")";
    }

    public QSYSObjectPathName getObjectPathName() {
        return new QSYSObjectPathName(library, name, objectType.substring(1));
    }

    public String getToolTipText() {
        return "\\\\" + connectionName + "\\QSYS.LIB\\" + library + ".LIB\\" + name + "." + objectType;
    }

    // private Pattern getPattern() {
    // if (pattern == null) {
    // pattern = Pattern.compile(PATTERN);
    // }
    // return pattern;
    // }

    @Override
    public String toString() {
        return getAbsoluteName();
    }
}
