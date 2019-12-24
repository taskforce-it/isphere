/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import java.util.ArrayList;
import java.util.List;

public enum Columns {

    STATUS ("status", Index.STATUS, 70), //$NON-NLS-1$
    FILE ("file", Index.FILE, 100), //$NON-NLS-1$
    FILE_NUMBER ("fileNumber", Index.FILE_NUMBER, 80), //$NON-NLS-1$
    JOB_NAME ("jobName", Index.JOB_NAME, 100), //$NON-NLS-1$
    JOB_USER ("jobUser", Index.JOB_USER, 100), //$NON-NLS-1$
    JOB_NUMBER ("jobNumber", Index.JOB_NUMBER, 100), //$NON-NLS-1$
    JOB_SYSTEM ("jobSystem", Index.JOB_SYSTEM, 80), //$NON-NLS-1$
    CREATION_DATE ("creationDate", Index.CREATION_DATE, 80), //$NON-NLS-1$
    CREATION_TIME ("creationTime", Index.CREATION_TIME, 80), //$NON-NLS-1$
    OUTPUT_QUEUE ("outputQueue", Index.OUTPUT_QUEUE, 160), //$NON-NLS-1$
    OUTPUT_PRIORITY ("outputPriority", Index.OUTPUT_PRIORITY, 80), //$NON-NLS-1$
    USER_DATE ("userData", Index.USER_DATE, 80), //$NON-NLS-1$
    FORM_TYPE ("formType", Index.FORM_TYPE, 80), //$NON-NLS-1$
    COPIES ("copies", Index.COPIES, 50), //$NON-NLS-1$
    PAGES ("pages", Index.PAGES, 50), //$NON-NLS-1$
    CURRENT_PAGE ("currentPage", Index.CURRENT_PAGE, 50), //$NON-NLS-1$
    CREATION_TIMESTAMP ("creationTimestamp", Index.CREATION_TIMESTAMP, 150); //$NON-NLS-1$

    public final String name;
    public final int index;
    public final int width;

    private Columns(String name, int columnNumber, int width) {
        this.name = name;
        this.index = columnNumber;
        this.width = width;
    }

    public static String[] names() {

        List<String> names = new ArrayList<String>();
        for (Columns column : Columns.values()) {
            names.add(column.name);
        }

        return names.toArray(new String[names.size()]);
    }

    public interface Index {
        public static int STATUS = 0;
        public static int FILE = 1;
        public static int FILE_NUMBER = 2;
        public static int JOB_NAME = 3;
        public static int JOB_USER = 4;
        public static int JOB_NUMBER = 5;
        public static int JOB_SYSTEM = 6;
        public static int CREATION_DATE = 7;
        public static int CREATION_TIME = 8;
        public static int OUTPUT_QUEUE = 9;
        public static int OUTPUT_PRIORITY = 10;
        public static int USER_DATE = 11;
        public static int FORM_TYPE = 12;
        public static int COPIES = 13;
        public static int PAGES = 14;
        public static int CURRENT_PAGE = 15;
        public static int CREATION_TIMESTAMP = 16;
    }

}
