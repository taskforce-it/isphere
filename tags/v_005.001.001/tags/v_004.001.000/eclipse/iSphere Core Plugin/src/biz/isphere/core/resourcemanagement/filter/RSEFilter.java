/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

import java.util.Arrays;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.ObjectHelper;
import biz.isphere.core.resourcemanagement.AbstractResource;

public class RSEFilter extends AbstractResource implements Comparable<RSEFilter> {

    public static final String TYPE_LIBRARY = "LIBRARY";
    public static final String TYPE_OBJECT = "OBJECT";
    public static final String TYPE_MEMBER = "MEMBER";
    public static final String TYPE_UNKNOWN = "UNKNOWN";

    private RSEFilterPool filterPool;
    private String name;
    private String type;
    private String[] filterStrings;
    private Object origin;

    public RSEFilter(boolean editable) {
        super(editable);
        this.filterPool = null;
        this.name = null;
        this.type = null;
        this.filterStrings = null;
        this.origin = null;
    }

    public RSEFilter(RSEFilterPool filterPool, String name, String type, String[] filterStrings, boolean editable, Object origin) {
        super(editable);
        this.filterPool = filterPool;
        this.name = name;
        this.type = type;
        this.filterStrings = filterStrings;
        this.origin = origin;
        // Arrays.sort(this.filterStrings);
    }

    public RSEFilterPool getFilterPool() {
        return filterPool;
    }

    public void setFilterPool(RSEFilterPool filterPool) {
        this.filterPool = filterPool;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getFilterStrings() {
        return filterStrings;
    }

    public void setFilterStrings(String[] filterStrings) {
        this.filterStrings = filterStrings;
        // Arrays.sort(this.filterStrings);
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    public String getDisplayFilterString() {
        String[] sortedFilterString = ObjectHelper.cloneVO(filterStrings);
        Arrays.sort(sortedFilterString);

        StringBuffer buffer = new StringBuffer("");
        for (int idx = 0; idx < sortedFilterString.length; idx++) {
            if (idx > 0) {
                buffer.append("     ");
            }
            buffer.append(sortedFilterString[idx]);
        }
        return buffer.toString();
    }

    @Override
    public String getKey() {
        return filterPool.getName() + ":" + name;
    }

    @Override
    public String getValue() {
        return type + ":" + getDisplayFilterString();
    }

    public static String getTypeText(String type) {
        if (type.equals(TYPE_LIBRARY)) {
            return Messages.Library;
        } else if (type.equals(TYPE_OBJECT)) {
            return Messages.Object;
        } else if (type.equals(TYPE_MEMBER)) {
            return Messages.Member;
        } else if (type.equals(TYPE_UNKNOWN)) {
            return Messages.Unknown;
        } else {
            return "*UNKNOWN";
        }
    }

    public int compareTo(RSEFilter other) {

        if (other == null || other.getKey() == null) {
            return 1;
        } else {
            return getKey().compareTo(other.getKey());
        }
    }

}
