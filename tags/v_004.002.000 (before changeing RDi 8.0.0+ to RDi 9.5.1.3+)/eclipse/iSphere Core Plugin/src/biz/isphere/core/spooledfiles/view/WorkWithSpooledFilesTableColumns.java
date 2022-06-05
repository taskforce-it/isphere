/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.widgets.Table;

import biz.isphere.core.spooledfiles.SpooledFileAttributes;

/**
 * Defines the columns of the iSphere 'Spooled Files' panel (
 * {@link WorkWithSpooledFilesPanel}.
 */
public enum WorkWithSpooledFilesTableColumns {

    STATUS (SpooledFileAttributes.STATUS, ColumnIndex.STATUS),
    FILE (SpooledFileAttributes.FILE, ColumnIndex.FILE),
    FILE_NUMBER (SpooledFileAttributes.FILE_NUMBER, ColumnIndex.FILE_NUMBER),
    JOB_NAME (SpooledFileAttributes.JOB_NAME, ColumnIndex.JOB_NAME),
    JOB_USER (SpooledFileAttributes.JOB_USER, ColumnIndex.JOB_USER),
    JOB_NUMBER (SpooledFileAttributes.JOB_NUMBER, ColumnIndex.JOB_NUMBER),
    JOB_SYSTEM (SpooledFileAttributes.JOB_SYSTEM, ColumnIndex.JOB_SYSTEM),
    CREATION_TIMESTAMP (SpooledFileAttributes.CREATION_TIMESTAMP, ColumnIndex.CREATION_TIMESTAMP),
    OUTPUT_QUEUE (SpooledFileAttributes.OUTPUT_QUEUE, ColumnIndex.OUTPUT_QUEUE),
    OUTPUT_PRIORITY (SpooledFileAttributes.OUTPUT_PRIORITY, ColumnIndex.OUTPUT_PRIORITY),
    USER_DATA (SpooledFileAttributes.USER_DATA, ColumnIndex.USER_DATE),
    FORM_TYPE (SpooledFileAttributes.FORM_TYPE, ColumnIndex.FORM_TYPE),
    COPIES (SpooledFileAttributes.COPIES, ColumnIndex.COPIES),
    PAGES (SpooledFileAttributes.PAGES, ColumnIndex.PAGES),
    CURRENT_PAGE (SpooledFileAttributes.CURRENT_PAGE, ColumnIndex.CURRENT_PAGE),
    CREATION_DATE (SpooledFileAttributes.CREATION_DATE, ColumnIndex.CREATION_DATE),
    CREATION_TIME (SpooledFileAttributes.CREATION_TIME, ColumnIndex.CREATION_TIME);

    public final String name;
    public final Integer index;
    public final int width;
    public final String label;

    private WorkWithSpooledFilesTableColumns(SpooledFileAttributes attribute, int columnIndex) {
        this.name = attribute.attributeName;
        this.index = columnIndex;
        this.width = attribute.columnWidth;
        this.label = attribute.columnHeading;
    }

    public static WorkWithSpooledFilesTableColumns[] getDefaultColumns() {

        WorkWithSpooledFilesTableColumns[] columns = WorkWithSpooledFilesTableColumns.values();
        Arrays.sort(columns, new Comparator<WorkWithSpooledFilesTableColumns>() {

            public int compare(WorkWithSpooledFilesTableColumns o1, WorkWithSpooledFilesTableColumns o2) {

                if (o1 == null) {
                    return -1;
                } else {
                    int result = o1.index.compareTo(o2.index);
                    return result;
                }
            }
        });

        return columns;
    }

    /**
     * Specifies the default order of the table columns from left to right. The
     * values below are used for calling {@link Table#setColumnOrder(int[])}.
     */
    private interface ColumnIndex {
        public static int STATUS = 0;
        public static int FILE = 1;
        public static int FILE_NUMBER = 2;
        public static int JOB_NAME = 3;
        public static int JOB_USER = 4;
        public static int JOB_NUMBER = 5;
        public static int JOB_SYSTEM = 6;
        public static int CREATION_TIMESTAMP = 7;
        public static int OUTPUT_QUEUE = 8;
        public static int OUTPUT_PRIORITY = 9;
        public static int USER_DATE = 10;
        public static int FORM_TYPE = 11;
        public static int COPIES = 12;
        public static int PAGES = 13;
        public static int CURRENT_PAGE = 14;
        public static int CREATION_DATE = 15;
        public static int CREATION_TIME = 16;
    }

}
