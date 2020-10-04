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

public class QualifiedJobName {

    private static final String PATTERN = "((\\d{6})/(.{1,10})/(.{1,10}))";
    private static final String DELIMITER = "/";

    private String jobName;
    private String userName;
    private String jobNumber;

    private StringBuilder qualifiedJobName;
    private static final Pattern pattern = Pattern.compile(PATTERN);

    public QualifiedJobName(String jobName, String userName, String jobNumber) {

        this.jobName = jobName;
        this.userName = userName;
        this.jobNumber = jobNumber;

        this.qualifiedJobName = null;
    }

    public QualifiedJobName(String qualifiedJobName) {

        if (!isValid(qualifiedJobName)) {
            throw new IllegalArgumentException("Invalid qualified job name: " + qualifiedJobName); //$NON-NLS-1$
        }

        // Retrieve job, user and number from a qualified job name of
        // format '123456/USER/JOB'.
        Matcher matcher = pattern.matcher(qualifiedJobName.trim().toUpperCase());
        matcher.find();

        this.jobNumber = matcher.group(2);
        this.userName = matcher.group(3);
        this.jobName = matcher.group(4);

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

        if (qualifiedJobName == null) {
            StringBuilder qualifiedJobName = new StringBuilder();
            qualifiedJobName.append(jobNumber);
            qualifiedJobName.append(DELIMITER);
            qualifiedJobName.append(userName);
            qualifiedJobName.append(DELIMITER);
            qualifiedJobName.append(jobName);
        }

        return qualifiedJobName.toString();
    }

    public static boolean isValid(String qualifiedJobName) {

        Matcher matcher = pattern.matcher(qualifiedJobName.trim().toUpperCase());
        if (matcher.find()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return getQualifiedJobName();
    }
}
