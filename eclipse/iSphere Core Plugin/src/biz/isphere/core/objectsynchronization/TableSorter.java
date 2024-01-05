/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import biz.isphere.core.objectsynchronization.jobs.SyncMbrMode;
import biz.isphere.core.objectsynchronization.rse.MemberCompareItem;

public class TableSorter extends ViewerSorter {

    private SyncMbrMode mode;

    public TableSorter(SyncMbrMode mode) {
        this.mode = mode;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {

        MemberCompareItem item1 = (MemberCompareItem)e1;
        MemberCompareItem item2 = (MemberCompareItem)e2;

        int rc;

        if (item1 == null) {
            rc = -1;
        } else if (item2 == null) {
            rc = 1;
        } else {
            MemberDescription description1;
            MemberDescription description2;
            if (mode == SyncMbrMode.LEFT_SYSTEM) {
                description1 = item1.getLeftMemberDescription();
                description2 = item2.getLeftMemberDescription();
            } else {
                description1 = item1.getRightMemberDescription();
                description2 = item2.getRightMemberDescription();
            }

            if (description1 == null) {
                rc = -1;
            } else if (description2 == null) {
                rc = 1;
            } else {
                rc = description1.getLibraryName().compareTo(description2.getLibraryName());
                if (rc == 0) {
                    rc = description1.getFileName().compareTo(description2.getFileName());
                    if (rc == 0) {
                        rc = description1.getMemberName().compareTo(description2.getMemberName());
                        if (rc == 0) {
                            rc = description1.getSourceType().compareTo(description2.getSourceType());
                        }
                    }
                }
            }
        }

        return rc;
    }
}
