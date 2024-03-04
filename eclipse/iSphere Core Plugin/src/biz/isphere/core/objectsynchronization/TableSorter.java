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
        if (item1 == null && item2 == null) {
            rc = 0;
        } else if (item1 == null) {
            rc = -1;
        } else if (item2 == null) {
            rc = 1;
        } else {

            MemberDescription description1;
            MemberDescription description2;
            if (mode == SyncMbrMode.LEFT_SYSTEM) {
                description1 = getSortMemberDescription(item1.getLeftMemberDescription(), item1.getRightMemberDescription());
                description2 = getSortMemberDescription(item2.getLeftMemberDescription(), item2.getRightMemberDescription());
            } else {
                description1 = getSortMemberDescription(item1.getRightMemberDescription(), item1.getLeftMemberDescription());
                description2 = getSortMemberDescription(item2.getRightMemberDescription(), item2.getLeftMemberDescription());
            }

            // Library name must be ignored
            rc = description1.getFileName().compareTo(description2.getFileName());
            if (rc == 0) {
                rc = description1.getMemberName().compareTo(description2.getMemberName());
                if (rc == 0) {
                    rc = description1.getSourceType().compareTo(description2.getSourceType());
                }
            }
            // }
            // }
        }

        return rc;
    }

    private MemberDescription getSortMemberDescription(MemberDescription value, MemberDescription defaultValue) {
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }
}
