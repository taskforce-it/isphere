/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.core.objectsynchronization.rse.MemberCompareItem;

/**
 * Class to provide the content of the table viewer.
 */
public class TableContentProvider implements IStructuredContentProvider {

    private TableStatistics tableStatistics;
    private SynchronizeMembersEditorInput editorInput;
    private Map<String, MemberCompareItem> compareItems;

    public TableContentProvider(TableStatistics tableStatistics) {

        this.tableStatistics = tableStatistics;
        this.editorInput = null;
        this.compareItems = null;
    }

    public TableStatistics getTableStatistics() {
        return tableStatistics;
    }

    public Object[] getElements(Object inputElement) {

        if (compareItems == null) {

            tableStatistics.clearStatistics();
            compareItems = new LinkedHashMap<String, MemberCompareItem>();

            if (editorInput != null) {

                for (MemberDescription leftMemberDescription : editorInput.getLeftMemberDescriptions()) {
                    compareItems.put(leftMemberDescription.getMemberName(), new MemberCompareItem(leftMemberDescription, null));
                }

                for (MemberDescription rightMemberDescription : editorInput.getRightMemberDescriptions()) {
                    MemberCompareItem item = compareItems.get(rightMemberDescription.getMemberName());
                    if (item == null) {
                        compareItems.put(rightMemberDescription.getMemberName(), new MemberCompareItem(null, rightMemberDescription));
                    } else {
                        item.setRightMemberDescription(rightMemberDescription);
                    }
                }
            }
        }

        MemberCompareItem[] compareItemsArray = compareItems.values().toArray(new MemberCompareItem[compareItems.size()]);
        Arrays.sort(compareItemsArray);

        return compareItemsArray;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        editorInput = (SynchronizeMembersEditorInput)newInput;
        compareItems = null;
    }
}
