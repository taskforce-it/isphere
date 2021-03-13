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

public class QualifiedMemberName {

    protected static final String RETRIEVE_PATTERN = "((?:(\\S{1,})(?::))?(\\S{1,10})\\b/\\b((?!/)\\S{1,10})\\b[ ]?\\((\\S{1,10})\\))";
    protected static final String VALIDATE_PATTERN = "^" + RETRIEVE_PATTERN + "$";
    protected static final String START_MEMBER = " (";
    protected static final String END_MEMBER = ")";

    private static final Pattern validate_pattern = Pattern.compile(VALIDATE_PATTERN);

    protected static final int CONNECTION = QualifiedName.CONNECTION;
    protected static final int LIBRARY = QualifiedName.LIBRARY;
    protected static final int FILE = QualifiedName.OBJECT;
    protected static final int MEMBER = 5;

    private QualifiedName qualifiedName;
    private String memberName;

    public QualifiedMemberName(String libraryName, String fileName, String memberName) {
        init(null, libraryName, fileName, memberName);

        this.memberName = memberName;
    }

    public QualifiedMemberName(String connectionName, String libraryName, String fileName, String memberName) {
        init(connectionName, libraryName, fileName, memberName);
    }

    public QualifiedMemberName(String qualifiedFileName) {

        // Retrieve library, file and member from a qualified file name of
        // format 'LIBRARY/FILE (MEMBER)'.
        qualifiedFileName = qualifiedFileName.trim().replaceFirst("\\.", QualifiedName.NAME_DELIMITER_SYS).toUpperCase();
        Matcher matcher = validate_pattern.matcher(qualifiedFileName.trim().toUpperCase());
        if (matcher.find()) {
            init(matcher.group(CONNECTION), matcher.group(LIBRARY), matcher.group(FILE), matcher.group(MEMBER));
        } else {
            throw new IllegalArgumentException("Invalid qualified object name: " + qualifiedFileName); //$NON-NLS-1$
        }
    }

    private void init(String connectionName, String libraryName, String fileName, String memberName) {
        this.qualifiedName = new QualifiedName(connectionName, libraryName, fileName);
        this.memberName = memberName.trim();
    }

    public String getConnectionName() {
        return qualifiedName.getConnectionName();
    }

    public String getFileName() {
        return qualifiedName.getObjectName();
    }

    public String getLibraryName() {
        return qualifiedName.getLibraryName();
    }

    public String getMemberName() {
        return memberName;
    }

    public String getQualifiedName() {
        return qualifiedName.getQualifiedName() + START_MEMBER + memberName.trim() + END_MEMBER;
    }

    public String getQualifiedNameSQL() {
        return qualifiedName.getQualifiedNameSQL() + START_MEMBER + memberName.trim() + END_MEMBER;
    }

    public static QualifiedMemberName parse(String string) {

        if (StringHelper.isNullOrEmpty(string)) {
            return null;
        }

        Matcher matcher = validate_pattern.matcher(string);
        if (matcher.find()) {
            return new QualifiedMemberName(matcher.group(1).trim());
        }

        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((memberName == null) ? 0 : memberName.hashCode());
        result = prime * result + ((qualifiedName == null) ? 0 : qualifiedName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        QualifiedMemberName other = (QualifiedMemberName)obj;
        if (memberName == null) {
            if (other.memberName != null) return false;
        } else if (!memberName.equals(other.memberName)) return false;
        if (qualifiedName == null) {
            if (other.qualifiedName != null) return false;
        } else if (!qualifiedName.equals(other.qualifiedName)) return false;
        return true;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }
}
