/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.internals;

public abstract class AbstractComparable<T> implements Comparable<T> {

    protected int compareToChecked(String o1, String o2) {

        if (o1 == null && o2 == null) {
            return 0;
        } else {
            if (o1 != null && o2 == null) {
                return 1;
            } else if (o1 == null && o2 != null) {
                return -1;
            } else {
                return o1.compareTo(o2);
            }
        }
    }
}
