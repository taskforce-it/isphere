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

public class Version implements Comparable<Version> {

    private static final String BETA_PREFIX = "b";
    private String version;
    private boolean isChecked;
    private boolean isBeta;

    public Version(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Version can not be null");
        }
        if (!version.matches("[0-9]+(\\.[0-9]+)*" + "(\\.b[0-9]+)?")) {
            throw new IllegalArgumentException("Invalid version format");
        }
        this.version = version;
        this.isChecked = false;
        this.isBeta = false;
    }

    public final String get() {
        if (!isChecked) {
            this.version = checkAndFixParts(this.version);
        }
        return this.version;
    }

    public final String toString() {
        return get();
    }

    public boolean isBeta() {
        if (!isChecked) {
            this.version = checkAndFixParts(this.version);
        }
        return isBeta;
    }

    private String checkAndFixParts(String version) {
        isBeta = false;
        String[] parts = splitt(version);
        StringBuilder fixed = new StringBuilder();
        for (String part : parts) {
            if (fixed.length() > 0) {
                fixed.append(".");
            }
            String fixedPart = removeLeadingZeros(part);
            if (!isBeta) {
                isBeta = checkForBeta(fixedPart);
            }
            fixed.append(fixedPart);
        }
        return fixed.toString();
    }

    private boolean checkForBeta(String part) {
        if (part.toLowerCase().startsWith(BETA_PREFIX)) {
            return true;
        }
        return false;
    }

    private String removeLeadingZeros(String part) {
        if (part.startsWith(BETA_PREFIX)) {
            return BETA_PREFIX + removeLeadingZeros(part.substring(1));
        }
        String fixed = part.replaceAll("^0+", "");
        if (StringHelper.isNullOrEmpty(fixed)) {
            return "0";
        }
        return fixed;
    }

    public int compareTo(Version that) {
        if (that == null) return 1;
        String[] thisParts = splitt(this.get());
        String[] thatParts = splitt(that.get());
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++) {
            int thisPart = getPart(thisParts, thatParts, i);
            int thatPart = getPart(thatParts, thisParts, i);

            if (thisPart < thatPart) return -1;
            if (thisPart > thatPart) return 1;
        }
        return 0;
    }

    private String[] splitt(String version) {
        return version.split("\\.");
    }

    private int getPart(String[] thisParts, String[] thatParts, int i) {
        if (thisParts.length < thatParts.length && i >= thisParts.length) {
            if (isBetaPart(thatParts[i])) {
                return Integer.MAX_VALUE;
            } else {
                return 0;
            }
        }
        int part = parseInteger(thisParts[i]);
        if (part == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Illegal release number. Release number must not contain: " + Integer.MAX_VALUE);
        }
        return part;
    }

    private int parseInteger(String part) {
        if (isBetaPart(part)) {
            return Integer.parseInt(part.substring(1));
        }
        return Integer.parseInt(part);
    }

    private boolean isBetaPart(String part) {
        if (StringHelper.isNullOrEmpty(part)) {
            return false;
        }
        return part.startsWith(BETA_PREFIX);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (this.getClass() != that.getClass()) return false;
        return this.compareTo((Version)that) == 0;
    }

}