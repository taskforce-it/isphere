/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 ******************************************************************************* 
 * See also: 
 * http://stackoverflow.com/questions/198431/how-do-you-compare-two-version-strings-in-java
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.SearchForUpdates;

/**
 * This class represents a version number. It is mainly used for comparing
 * versions, e.g. by the {@link SearchForUpdates} task.
 * <p>
 * A version consists of 3 parts, delimited by dots:
 * <ul>
 * <li>major - version number (integer value)</li>
 * <li>minor - version number (integer value)</li>
 * <li>micro - version number (integer value)</li>
 * </ul>
 * 
 * @author Thomas Raddatz
 */
public class XmlVersion implements Comparable<XmlVersion> {

    private static final String DELIMITER = "."; //$NON-NLS-1$
    public static final String VERSION_NUMBER_PATTERN = "[0-9]+(\\.[0-9]+)*"; //$NON-NLS-1$

    private String originalVersion;
    private String parsedVersion;

    private int major;
    private int minor;
    private int micro;

    public XmlVersion(String version) throws InvalidVersionNumberException {
        this.originalVersion = version;
        this.parsedVersion = parseVersion(originalVersion);
    }

    public final String get() {
        return parsedVersion;
    }

    public final String toString() {
        return parsedVersion;
    }

    private String parseVersion(String version) throws InvalidVersionNumberException {

        validate(version);

        StringBuilder parsedVersion = new StringBuilder();
        String[] parts = splitt(version);
        int count = 0;
        for (String part : parts) {

            try {

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
                default:
                    throw new InvalidVersionNumberException();
                }

            } catch (NumberFormatException e) {
                throw new InvalidVersionNumberException();
            }

            count++;
        }

        return parsedVersion.toString();
    }

    public static void validate(String version) throws InvalidVersionNumberException {

        if (version == null) {
            throw new InvalidVersionNumberException();
        }

        if (!version.matches(VERSION_NUMBER_PATTERN)) {
            throw new InvalidVersionNumberException();
        }
    }

    private void addSegment(StringBuilder parsedVersion, String part) {

        if (parsedVersion.length() > 0) {
            parsedVersion.append(DELIMITER);
        }

        parsedVersion.append(removeLeadingZeros(part));
    }

    private String removeLeadingZeros(String part) {

        String fixed = part.replaceAll("^0+", ""); //$NON-NLS-1$ //$NON-NLS-2$
        if (StringHelper.isNullOrEmpty(fixed)) {
            return "0"; //$NON-NLS-1$
        }
        return fixed;
    }

    public int compareTo(String that) throws InvalidVersionNumberException {

        if (that == null) {
            return 1;
        }

        return compareTo(new XmlVersion(that));
    }

    public int compareTo(XmlVersion that) {

        if (that == null) {
            return 1;
        }

        int result = 0;

        result = this.major - that.major;
        if (result == 0) {
            result = this.minor - that.minor;
            if (result == 0) {
                result = this.micro - that.micro;
            }
        }

        if (result < 0) {
            result = -1;
        } else if (result > 0) {
            result = 1;
        }

        return result;
    }

    private String[] splitt(String version) {
        return version.split("\\."); //$NON-NLS-1$
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (this.getClass() != that.getClass()) return false;
        return this.compareTo((XmlVersion)that) == 0;
    }

}