/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 ******************************************************************************* 
 * See also: 
 * http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
 *******************************************************************************/

package biz.isphere.core.internal;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.exception.InvalidVersionNumberException;

/**
 * This class represents a version number. It is mainly used for comparing
 * versions, e.g. by the {@link SearchForUpdates} task.
 * <p>
 * A version consists of 4 parts, delimited by dots:
 * <ul>
 * <li>major - version number (integer value)</li>
 * <li>minor - version number (integer value)</li>
 * <li>micro - version number (integer value)</li>
 * <li>qualifier - qualifier of the version number. Often the qualifier is the
 * timestamp of the build.</li>
 * </ul>
 * The iSphere version enforces the following rules for the <i>qualifier</i> of
 * the version number, in order to ensure, that we can distinguish between beta
 * and release versions numbers.
 * <p>
 * <u>Beta Version Qualifier</u>
 * <p>
 * The qualifier of a beta version starts with the letter 'b', followed by a 3
 * digit integer value. For example: 1.3.1.b004
 * 
 * @author Thomas Raddatz
 */
public class Version implements Comparable<Version> {

    private static final String BETA_PREFIX = "b";
    private static final String RELEASE_INDICATOR = "r";
    private static final String DELIMITER = ".";
    private static final int LENGTH_OF_QUALIFIER = 3;

    public static final String VERSION_NUMBER_PATTERN = "[0-9]+(\\.[0-9]+)*" + "(\\.(b[0-9]{1," + LENGTH_OF_QUALIFIER + "}|r)){1}";

    private String originalVersion;
    private String parsedVersion;
    private boolean isBeta;

    private int major;
    private int minor;
    private int micro;
    private String qualifier;

    public Version(String version) throws InvalidVersionNumberException {

        if (version == null) {
            throw new InvalidVersionNumberException(version);
        }

        if ("2.4.0".equals(version) || "2.5.0".equals(version) || "2.5.1".equals(version) || "2.5.2".equals(version)) {
            version = version + ".r";
        }

        if (!version.matches(VERSION_NUMBER_PATTERN)) {
            throw new InvalidVersionNumberException(version);
        }

        this.originalVersion = version;
        parsedVersion = parseVersion(this.originalVersion);
    }

    public final String get() {
        return parsedVersion;
    }

    public final String toString() {
        return parsedVersion;
    }

    public boolean isBeta() {
        return isBeta;
    }

    private String parseVersion(String version) {

        isBeta = false;
        StringBuilder parsedVersion = new StringBuilder();
        String[] parts = splitt(version);
        int count = 0;
        for (String part : parts) {
            if (isQualifier(part)) {
                isBeta = isBetaPart(part);
                qualifier = fixQualifierSegment(part);
                addSegment(parsedVersion, part);
                break;
            } else {
                switch (count) {
                case 0:
                    major = Integer.parseInt(part);
                    addSegment(parsedVersion, part);
                    break;
                case 1:
                    minor = Integer.parseInt(part);
                    addSegment(parsedVersion, part);
                    break;
                case 2:
                    micro = Integer.parseInt(part);
                    addSegment(parsedVersion, part);
                    break;
                }
                count++;
            }
        }

        return parsedVersion.toString();
    }

    private void addSegment(StringBuilder parsedVersion, String part) {

        if (parsedVersion.length() > 0) {
            parsedVersion.append(DELIMITER);
        }

        parsedVersion.append(removeLeadingZeros(part));
    }

    private String removeLeadingZeros(String part) {

        if (isReleasePart(part)) {
            return part;
        }

        if (isBetaPart(part)) {
            return part;
        }

        String fixed = part.replaceAll("^0+", "");
        if (StringHelper.isNullOrEmpty(fixed)) {
            return "0";
        }
        return fixed;
    }

    private String fixQualifierSegment(String part) {

        if (isReleasePart(part)) {
            return part;
        }

        String prefix = part.substring(0, 0);
        part = part.substring(1);

        while (part.length() < LENGTH_OF_QUALIFIER) {
            part = "0" + part;
        }

        return prefix + part;
    }

    public int compareTo(Version that) {

        int result = 0;

        if (that == null) {
            return 1;
        }

        result = this.major - that.major;
        if (result == 0) {
            result = this.minor - that.minor;
            if (result == 0) {
                result = this.micro - that.micro;
                if (result == 0) {
                    result = this.qualifier.compareTo(that.qualifier);
                }
            }
        }

        return result;
    }

    private boolean isQualifier(String part) {

        if (isBetaPart(part) || isReleasePart(part)) {
            return true;
        }

        return false;
    }

    private String[] splitt(String version) {
        return version.split("\\.");
    }

    private boolean isBetaPart(String part) {
        if (StringHelper.isNullOrEmpty(part)) {
            return false;
        }
        return part.startsWith(BETA_PREFIX);
    }

    private boolean isReleasePart(String part) {
        if (StringHelper.isNullOrEmpty(part)) {
            return false;
        }
        return part.startsWith(RELEASE_INDICATOR);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (this.getClass() != that.getClass()) return false;
        return this.compareTo((Version)that) == 0;
    }

}