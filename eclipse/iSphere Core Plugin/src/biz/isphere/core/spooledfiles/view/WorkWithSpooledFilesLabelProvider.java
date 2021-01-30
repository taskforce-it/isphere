/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileBaseResourceAdapter;

public class WorkWithSpooledFilesLabelProvider extends LabelProvider implements ITableLabelProvider {

    private SpooledFileBaseResourceAdapter baseLabelProvider;

    public WorkWithSpooledFilesLabelProvider() {
        this.baseLabelProvider = new SpooledFileBaseResourceAdapter();
    }

    public String getColumnText(Object element, int columnIndex) {

        SpooledFile spooledFile = (SpooledFile)element;
        if (columnIndex == WorkWithSpooledFilesTableColumns.STATUS.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.STATUS);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.FILE.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.FILE);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.FILE_NUMBER.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.FILE_NUMBER);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.JOB_NAME.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_NAME);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.JOB_USER.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_USER);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.JOB_NUMBER.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_NUMBER);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.JOB_SYSTEM.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_SYSTEM);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.CREATION_DATE.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.CREATION_DATE);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.CREATION_TIME.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.CREATION_TIME);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.OUTPUT_QUEUE.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.OUTPUT_QUEUE);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.OUTPUT_PRIORITY.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.OUTPUT_PRIORITY);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.USER_DATA.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.USER_DATA);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.FORM_TYPE.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.FORM_TYPE);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.COPIES.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.COPIES);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.PAGES.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.PAGES);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.CURRENT_PAGE.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.CURRENT_PAGE);
        } else if (columnIndex == WorkWithSpooledFilesTableColumns.CREATION_TIMESTAMP.index) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.CREATION_TIMESTAMP);
        }

        return null;
    }

    private String getColumnText(SpooledFile spooledFile, String property) {
        return baseLabelProvider.internalGetPropertyValue(spooledFile, property).toString();
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
}
