/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.comparators;

import java.util.Comparator;

public class IgnoreCaseComparator implements Comparator<String> {

    public int compare(String b1, String b2) {
        
        if (b1 == null && b2 == null) {
            return 0;
        } else if (b1 == null) {
            return 1;
        } else if (b2 == null) {
            return -1;
        } else {
            return b1.toUpperCase().compareTo(b2.toUpperCase());
        }
    }

}
