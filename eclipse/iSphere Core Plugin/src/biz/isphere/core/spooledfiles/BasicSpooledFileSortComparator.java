/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.util.Comparator;
import java.util.Date;

/**
 * Basic class for comparing spooled files when sorting them.
 */
public class BasicSpooledFileSortComparator implements Comparator<SpooledFile> {

    private SpooledFileAttributes sortAttribute;
    private boolean isReverseOrder;

    public BasicSpooledFileSortComparator() {
        this(null, false);
    }

    public BasicSpooledFileSortComparator(SpooledFileAttributes attribute) {
        this(attribute, false);
    }

    public BasicSpooledFileSortComparator(SpooledFileAttributes attribute, boolean reverseOrder) {
        this.sortAttribute = attribute;
        this.isReverseOrder = reverseOrder;
    }

    public SpooledFileAttributes getSortAttribute() {
        return sortAttribute;
    }

    public void setSortAttribute(SpooledFileAttributes attribute) {
        this.sortAttribute = attribute;
    }

    public boolean isReverseOrder() {
        return isReverseOrder;
    }

    public void setReverseOrder(boolean isReverseOrder) {
        this.isReverseOrder = isReverseOrder;
    }

    public int compare(SpooledFile o1, SpooledFile o2) {

        if (sortAttribute == null) {
            return 0;
        }

        Object value1;
        Object value2;

        if (isReverseOrder) {
            value1 = o1.getAttributeValue(sortAttribute);
            value2 = o2.getAttributeValue(sortAttribute);
        } else {
            value1 = o2.getAttributeValue(sortAttribute);
            value2 = o1.getAttributeValue(sortAttribute);
        }

        if (value1 == null) {
            return -1;
        } else if (value2 == null) {
            return 1;
        } else if ((value1 instanceof String)) {
            return ((String)value1).compareTo((String)value2);
        } else if ((value1 instanceof Date)) {
            return ((Date)value1).compareTo((Date)value2);
        } else if ((value1 instanceof Long)) {
            return ((Long)value1).compareTo((Long)value2);
        } else if ((value1 instanceof Integer)) {
            return ((Integer)value1).compareTo((Integer)value2);
        } else {
            throw new RuntimeException("Unsupported object type: " + value1.getClass().getName());
        }
    }
}
