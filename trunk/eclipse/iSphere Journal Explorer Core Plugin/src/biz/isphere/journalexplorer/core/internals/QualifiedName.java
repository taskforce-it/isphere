/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.internals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.QualifiedObjectName;

public class QualifiedName {

    protected static final String RETRIEVE_PATTERN = "((?:(\\S{1,})(?::))?(\\S{1,10})\\b/\\b((?!/)\\S{1,10})\\b)";
    protected static final String VALIDATE_PATTERN = "^" + RETRIEVE_PATTERN + "$";
    protected static final String NAME_DELIMITER_SYS = "/";
    protected static final String NAME_DELIMITER_SQL = ".";

    private static final Pattern retrieve_pattern = Pattern.compile(RETRIEVE_PATTERN);
    private static final Pattern validate_pattern = Pattern.compile(VALIDATE_PATTERN);

    protected static final int CONNECTION = 2;
    protected static final int LIBRARY = 3;
    protected static final int OBJECT = 4;

    private String connectionName;
    private String objectName;
    private String libraryName;

    public QualifiedName(String libraryName, String objectName) {
        init(null, libraryName, objectName);
    }

    public QualifiedName(String connectionName, String libraryName, String objectName) {
        init(connectionName, libraryName, objectName);
    }

    public QualifiedName(String qualifiedObjectName) {

        // Retrieve library and object from a qualified object name of
        // format 'LIBRARY/OBJECT'.
        qualifiedObjectName = qualifiedObjectName.trim().replaceFirst("\\.", "/").toUpperCase();
        Matcher matcher = validate_pattern.matcher(qualifiedObjectName);
        if (matcher.find()) {
            init(matcher.group(CONNECTION), matcher.group(LIBRARY), matcher.group(OBJECT));
        } else {
            throw new IllegalArgumentException("Invalid qualified object name: " + qualifiedObjectName); //$NON-NLS-1$
        }
    }

    private void init(String connectionName, String libraryName, String objectName) {
        this.connectionName = connectionName == null ? null : connectionName.trim();
        this.libraryName = libraryName.trim();
        this.objectName = objectName.trim();
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getQualifiedName() {
        return getQualifiedName(NAME_DELIMITER_SYS);
    }

    public String getQualifiedNameSQL() {
        return getQualifiedName(NAME_DELIMITER_SQL);
    }

    private String getQualifiedName(String nameDelimiter) {
        if (StringHelper.isNullOrEmpty(connectionName)) {
            return libraryName.trim() + nameDelimiter + objectName;
        } else {
            return connectionName + ":" + libraryName + NAME_DELIMITER_SYS + objectName;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionName == null) ? 0 : connectionName.hashCode());
        result = prime * result + ((libraryName == null) ? 0 : libraryName.hashCode());
        result = prime * result + ((objectName == null) ? 0 : objectName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        QualifiedName other = (QualifiedName)obj;
        if (connectionName == null) {
            if (other.connectionName != null) return false;
        } else if (!connectionName.equals(other.connectionName)) return false;
        if (libraryName == null) {
            if (other.libraryName != null) return false;
        } else if (!libraryName.equals(other.libraryName)) return false;
        if (objectName == null) {
            if (other.objectName != null) return false;
        } else if (!objectName.equals(other.objectName)) return false;
        return true;
    }

    public static QualifiedObjectName parse(String string) {

        if (StringHelper.isNullOrEmpty(string)) {
            return null;
        }

        Matcher matcher = retrieve_pattern.matcher(string);
        if (matcher.find()) {
            return new QualifiedObjectName(matcher.group(1).trim());
        }

        return null;
    }

    public static String getName(String connectionName, String libraryName, String objectName) {
        return new QualifiedName(connectionName, libraryName, objectName).getQualifiedName();
    }

    public static String getName(String libraryName, String objectName) {
        return new QualifiedName(libraryName, objectName).getQualifiedName();
    }

    public static String getMemberName(String connectionName, String libraryName, String objectName, String memberName) {
        return connectionName.trim() + ":" + getMemberName(libraryName, objectName, memberName);
    }

    public static String getMemberName(String libraryName, String objectName, String memberName) {
        return getName(libraryName, objectName) + " (" + memberName.trim() + ")";
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
