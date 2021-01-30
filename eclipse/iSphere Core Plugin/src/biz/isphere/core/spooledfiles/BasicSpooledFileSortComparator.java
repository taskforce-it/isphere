/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.util.Comparator;
import java.util.Date;

import biz.isphere.core.spooledfiles.view.rse.Columns;

/**
 * Basic class for comparing spooled files when sorting them.
 */
public class BasicSpooledFileSortComparator implements Comparator<SpooledFile> {

    private Columns sortColumn;
    private boolean isReverseOrder;

    public BasicSpooledFileSortComparator() {
        this(null, false);
    }

    public BasicSpooledFileSortComparator(Columns columnIndex) {
        this(columnIndex, false);
    }

    public BasicSpooledFileSortComparator(Columns columnIndex, boolean reverseOrder) {
        this.sortColumn = columnIndex;
        this.isReverseOrder = reverseOrder;
    }

    public Columns getSortColumn() {
        return sortColumn;
    }

    public void setColumnIndex(Columns columnIndex) {
        this.sortColumn = columnIndex;
    }

    public boolean isReverseOrder() {
        return isReverseOrder;
    }

    public void setReverseOrder(boolean isReverseOrder) {
        this.isReverseOrder = isReverseOrder;
    }

    public int compare(SpooledFile o1, SpooledFile o2) {

        if (sortColumn == null) {
            return 0;
        }

        Object value1;
        Object value2;

        if (isReverseOrder) {
            value1 = getColumnValue(o1);
            value2 = getColumnValue(o2);
        } else {
            value1 = getColumnValue(o2);
            value2 = getColumnValue(o1);
        }

        if (value1 == null) {
            return -1;
        } else if (value2 == null) {
            return 1;
        } else if ((value1 instanceof String)) {
            return ((String)value1).compareTo((String)value2);
        } else if ((value1 instanceof Date)) {
            return ((Date)value1).compareTo((Date)value2);
        } else if ((value1 instanceof Long)) {
            return ((Long)value1).compareTo((Long)value2);
        } else if ((value1 instanceof Integer)) {
            return ((Integer)value1).compareTo((Integer)value2);
        } else {
            throw new RuntimeException("Unsupported object type: " + value1.getClass().getName());
        }
    }

    public Object getColumnValue(SpooledFile spooledFile) {

        if (spooledFile == null) {
            return null;
        }

        if (sortColumn == Columns.STATUS) {
            return spooledFile.getStatus();
        } else if (sortColumn == Columns.FILE) {
            return spooledFile.getFile();
        } else if (sortColumn == Columns.FILE_NUMBER) {
            return spooledFile.getFileNumber();
        } else if (sortColumn == Columns.JOB_NAME) {
            return spooledFile.getJobName();
        } else if (sortColumn == Columns.JOB_USER) {
            return spooledFile.getJobUser();
        } else if (sortColumn == Columns.JOB_NUMBER) {
            return spooledFile.getJobNumber();
        } else if (sortColumn == Columns.JOB_SYSTEM) {
            return spooledFile.getJobSystem();
        } else if (sortColumn == Columns.CREATION_DATE) {
            return spooledFile.getCreationDateAsDate();
        } else if (sortColumn == Columns.CREATION_TIME) {
            return spooledFile.getCreationTimeAsDate();
        } else if (sortColumn == Columns.OUTPUT_QUEUE) {
            return spooledFile.getOutputQueue();
        } else if (sortColumn == Columns.OUTPUT_PRIORITY) {
            return spooledFile.getOutputPriority();
        } else if (sortColumn == Columns.USER_DATA) {
            return spooledFile.getUserData();
        } else if (sortColumn == Columns.FORM_TYPE) {
            return spooledFile.getFormType();
        } else if (sortColumn == Columns.COPIES) {
            return spooledFile.getCopies();
        } else if (sortColumn == Columns.PAGES) {
            return spooledFile.getPages();
        } else if (sortColumn == Columns.CURRENT_PAGE) {
            return spooledFile.getCurrentPage();
        } else if (sortColumn == Columns.CREATION_TIMESTAMP) {
            return spooledFile.getCreationTimestamp();
        }

        return null;
    }

}
