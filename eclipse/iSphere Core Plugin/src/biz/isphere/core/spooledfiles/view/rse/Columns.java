/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.rse;

import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.Messages;

public enum Columns {

    STATUS ("status", ColumnIndex.STATUS, 70, Messages.Status), //$NON-NLS-1$
    FILE ("file", ColumnIndex.FILE, 100, Messages.File), //$NON-NLS-1$
    FILE_NUMBER ("fileNumber", ColumnIndex.FILE_NUMBER, 80, Messages.File_number), //$NON-NLS-1$
    JOB_NAME ("jobName", ColumnIndex.JOB_NAME, 100, Messages.Job_name), //$NON-NLS-1$
    JOB_USER ("jobUser", ColumnIndex.JOB_USER, 100, Messages.Job_user), //$NON-NLS-1$
    JOB_NUMBER ("jobNumber", ColumnIndex.JOB_NUMBER, 100, Messages.Job_number), //$NON-NLS-1$
    JOB_SYSTEM ("jobSystem", ColumnIndex.JOB_SYSTEM, 80, Messages.Job_system), //$NON-NLS-1$
    CREATION_TIMESTAMP ("creationTimestamp", ColumnIndex.CREATION_TIMESTAMP, 150, Messages.Creation_timestamp), //$NON-NLS-1$
    OUTPUT_QUEUE ("outputQueue", ColumnIndex.OUTPUT_QUEUE, 160, Messages.Output_queue), //$NON-NLS-1$
    OUTPUT_PRIORITY ("outputPriority", ColumnIndex.OUTPUT_PRIORITY, 80, Messages.Output_priority), //$NON-NLS-1$
    USER_DATA ("userData", ColumnIndex.USER_DATE, 80, Messages.User_data), //$NON-NLS-1$
    FORM_TYPE ("formType", ColumnIndex.FORM_TYPE, 80, Messages.Form_type), //$NON-NLS-1$
    COPIES ("copies", ColumnIndex.COPIES, 50, Messages.Copies), //$NON-NLS-1$
    PAGES ("pages", ColumnIndex.PAGES, 50, Messages.Pages), //$NON-NLS-1$
    CURRENT_PAGE ("currentPage", ColumnIndex.CURRENT_PAGE, 50, Messages.Current_page), //$NON-NLS-1$
    CREATION_DATE ("creationDate", ColumnIndex.CREATION_DATE, 80, Messages.Creation_date), //$NON-NLS-1$
    CREATION_TIME ("creationTime", ColumnIndex.CREATION_TIME, 80, Messages.Creation_time); //$NON-NLS-1$

    public final String name;
    public final int index;
    public final int width;
    public final String label;

    private Columns(String name, int columnIndex, int width, String label) {
        this.name = name;
        this.index = columnIndex;
        this.width = width;
        this.label = label;
    }

    public static String[] names() {

        List<String> names = new ArrayList<String>();
        for (Columns column : Columns.values()) {
            names.add(column.name);
        }

        return names.toArray(new String[names.size()]);
    }

    public static Columns getByName(String columnName) {

        for (Columns column : Columns.values()) {
            if (column.name.equals(columnName)) {
                return column;
            }
        }

        throw new RuntimeException("Column [" + columnName + "] not found.");
    }

    /**
     * Specifies the order of the table columns from left to right.
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
