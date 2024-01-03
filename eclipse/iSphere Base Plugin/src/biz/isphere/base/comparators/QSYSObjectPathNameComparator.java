/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.comparators;

import java.util.Comparator;

import com.ibm.as400.access.QSYSObjectPathName;

public class QSYSObjectPathNameComparator implements Comparator<QSYSObjectPathName> {

    public int compare(QSYSObjectPathName path1, QSYSObjectPathName path2) {

        if (!path1.getLibraryName().equals(path2.getLibraryName())) {
            return path1.getLibraryName().compareTo(path2.getLibraryName());
        } else if (!path1.getObjectName().equals(path2.getObjectName())) {
            return path1.getObjectName().compareTo(path2.getObjectName());
        } else if (!path1.getMemberName().equals(path2.getMemberName())) {
            return path1.getMemberName().compareTo(path2.getMemberName());
        } else {
            return 0;
        }
    }
}
