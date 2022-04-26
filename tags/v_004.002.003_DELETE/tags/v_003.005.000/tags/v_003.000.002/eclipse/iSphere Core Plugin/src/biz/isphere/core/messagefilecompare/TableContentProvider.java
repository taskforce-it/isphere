/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.core.messagefilecompare.rse.MessageFileCompareItem;
import biz.isphere.core.messagefileeditor.MessageDescription;

/**
 * Class to provide the content of the table viewer.
 */
public class TableContentProvider implements IStructuredContentProvider {

    private TableStatistics tableStatistics;
    private MessageFileCompareEditorInput editorInput;
    private Map<String, MessageFileCompareItem> compareItems;

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
            compareItems = new LinkedHashMap<String, MessageFileCompareItem>();
            
            if (editorInput != null) {

                for (MessageDescription leftMessageDescription : editorInput.getLeftMessageDescriptions()) {
                    compareItems.put(leftMessageDescription.getMessageId(), new MessageFileCompareItem(leftMessageDescription, null));
                }

                for (MessageDescription rightMessageDescription : editorInput.getRightMessageDescriptions()) {
                    MessageFileCompareItem item = compareItems.get(rightMessageDescription.getMessageId());
                    if (item == null) {
                        compareItems.put(rightMessageDescription.getMessageId(), new MessageFileCompareItem(null, rightMessageDescription));
                    } else {
                        item.setRightMessageDescription(rightMessageDescription);
                    }
                }
            }
        }

        MessageFileCompareItem[] compareItemsArray = compareItems.values().toArray(new MessageFileCompareItem[compareItems.size()]);
        Arrays.sort(compareItemsArray);

        return compareItemsArray;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        editorInput = (MessageFileCompareEditorInput)newInput;
        compareItems = null;
    }
}
