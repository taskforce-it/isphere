/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biz.isphere.base.internal.StringHelper;

public class QualifiedJobName {

    private static final String RETRIEVE_PATTERN = "((\\d{6})/\\b((?!/)\\S{1,10})\\b/\\b((?!/)\\S{1,10})\\b)";
    private static final String VALIDATE_PATTERN = "^" + RETRIEVE_PATTERN + "$";
    private static final String DELIMITER = "/";

    private String jobName;
    private String userName;
    private String jobNumber;

    private StringBuilder qualifiedJobName;
    private static final Pattern retrieve_pattern = Pattern.compile(RETRIEVE_PATTERN);
    private static final Pattern validate_pattern = Pattern.compile(VALIDATE_PATTERN);

    public QualifiedJobName(String jobName, String userName, String jobNumber) {

        this.jobName = jobName;
        this.userName = userName;
        this.jobNumber = jobNumber;

        this.qualifiedJobName = null;
    }

    public QualifiedJobName(String qualifiedJobName) {

        // Retrieve job, user and number from a qualified job name of
        // format '123456/USER/JOB'.
        Matcher matcher = validate_pattern.matcher(qualifiedJobName.trim().toUpperCase());
        if (matcher.find()) {
            this.jobNumber = matcher.group(2);
            this.userName = matcher.group(3);
            this.jobName = matcher.group(4);
        } else {
            throw new IllegalArgumentException("Invalid qualified job name: " + qualifiedJobName); //$NON-NLS-1$
        }

        this.qualifiedJobName = null;
    }

    public String getJob() {
        return jobName;
    }

    public String getUser() {
        return userName;
    }

    public String getNumber() {
        return jobNumber;
    }

    public String getQualifiedJobName() {

        if (StringHelper.isNullOrEmpty(jobName) || StringHelper.isNullOrEmpty(userName) || StringHelper.isNullOrEmpty(jobNumber)) {
            return null;
        }

        if (qualifiedJobName == null) {
            qualifiedJobName = new StringBuilder();
            qualifiedJobName.append(jobNumber);
            qualifiedJobName.append(DELIMITER);
            qualifiedJobName.append(userName);
            qualifiedJobName.append(DELIMITER);
            qualifiedJobName.append(jobName);
        }

        return qualifiedJobName.toString();
    }

    public static boolean isValid(String qualifiedJobName) {

        Matcher matcher = validate_pattern.matcher(qualifiedJobName.toUpperCase());
        if (matcher.find()) {
            return true;
        }

        return false;
    }

    public static QualifiedJobName parse(String string) {

        if (StringHelper.isNullOrEmpty(string)) {
            return null;
        }

        Matcher matcher = retrieve_pattern.matcher(string);
        if (matcher.find()) {
            return new QualifiedJobName(matcher.group(1).trim());
        }

        return null;
    }

    @Override
    public String toString() {
        return getQualifiedJobName();
    }
}
