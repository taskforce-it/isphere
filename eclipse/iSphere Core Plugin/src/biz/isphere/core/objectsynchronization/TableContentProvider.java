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
    private MemberCompareItem[] compareItemsArray;

    public TableContentProvider(TableStatistics tableStatistics) {

        this.tableStatistics = tableStatistics;
        this.editorInput = null;
        this.compareItemsArray = null;
    }

    public TableStatistics getTableStatistics() {
        return tableStatistics;
    }

    public Object[] getElements(Object inputElement) {

        if (compareItemsArray != null) {
            return compareItemsArray;
        }

        tableStatistics.clearStatistics();

        Map<String, MemberCompareItem> compareItems = new LinkedHashMap<String, MemberCompareItem>();

        if (editorInput != null) {

            boolean isFileSynchronization = editorInput.isFileSynchronization();

            for (MemberDescription leftMemberDescription : editorInput.getLeftMemberDescriptions()) {
                String key = produceKey(isFileSynchronization, leftMemberDescription);
                compareItems.put(key, new MemberCompareItem(leftMemberDescription, null));
            }

            for (MemberDescription rightMemberDescription : editorInput.getRightMemberDescriptions()) {
                String key = produceKey(isFileSynchronization, rightMemberDescription);
                MemberCompareItem compareItem = compareItems.get(key);
                if (compareItem != null) {
                    compareItem.setRightMemberDescription(rightMemberDescription);
                } else {
                    compareItems.put(key, new MemberCompareItem(null, rightMemberDescription));
                }
            }
        }

        compareItemsArray = compareItems.values().toArray(new MemberCompareItem[compareItems.size()]);
        Arrays.sort(compareItemsArray);

        return compareItemsArray;
    }

    private String produceKey(boolean isFileSynchronization, MemberDescription memberDescription) {
        if (isFileSynchronization) {
            return memberDescription.getMemberName();
        } else {
            return String.format("%s.%s", memberDescription.getFileName(), memberDescription.getMemberName());
        }
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        editorInput = (SynchronizeMembersEditorInput)newInput;
        compareItemsArray = null;
    }
}
