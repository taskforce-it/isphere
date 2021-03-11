/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biz.isphere.base.internal.StringHelper;

public class QualifiedObjectName {

    private static final String RETRIEVE_PATTERN = "((\\S{1,10})\\b/\\b((?!/)\\S{1,10})\\b)";
    private static final String VALIDATE_PATTERN = "^" + RETRIEVE_PATTERN + "$";
    private static final String DELIMITER = "/";

    private String objectName;
    private String libraryName;

    private StringBuilder qualifiedObjectName;
    private static final Pattern retrieve_pattern = Pattern.compile(RETRIEVE_PATTERN);
    private static final Pattern validate_pattern = Pattern.compile(VALIDATE_PATTERN);

    public QualifiedObjectName(String objectName, String libraryName) {

        this.objectName = objectName;
        this.libraryName = libraryName;

        this.qualifiedObjectName = null;
    }

    public QualifiedObjectName(String qualifiedObjectName) {

        // Retrieve library and object from a qualified object name of
        // format 'LIBRARY/OBJECT'.
        Matcher matcher = validate_pattern.matcher(qualifiedObjectName.trim().toUpperCase());
        if (matcher.find()) {
            this.libraryName = matcher.group(2);
            this.objectName = matcher.group(3);
        } else {
            throw new IllegalArgumentException("Invalid qualified object name: " + qualifiedObjectName); //$NON-NLS-1$
        }

        this.qualifiedObjectName = null;
    }

    public String getObject() {
        return objectName;
    }

    public String getLibrary() {
        return libraryName;
    }

    public String getQualifiedObjectName() {
        return toString(objectName, libraryName);
    }

    public static boolean isValid(String qualifiedObjectName) {

        Matcher matcher = validate_pattern.matcher(qualifiedObjectName.toUpperCase());
        if (matcher.find()) {
            return true;
        }

        return false;
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

    public static String toString(String objectName, String libraryName) {

        if (StringHelper.isNullOrEmpty(objectName) || StringHelper.isNullOrEmpty(libraryName)) {
            return null;
        }

        StringBuilder qualifiedObjectName = new StringBuilder();

        qualifiedObjectName = new StringBuilder();
        qualifiedObjectName.append(libraryName);
        qualifiedObjectName.append(DELIMITER);
        qualifiedObjectName.append(objectName);

        return qualifiedObjectName.toString();
    }

    @Override
    public String toString() {
        return getQualifiedObjectName();
    }
}
