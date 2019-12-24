package biz.isphere.core.spooledfiles.view;

import java.util.Date;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.core.spooledfiles.SpooledFile;

public class WorkWithSpooledFilesSorter extends ViewerSorter {

    private TableViewer tableViewer;
    private Table table;

    private int columnIndex;
    private boolean isReverseOrder;

    public WorkWithSpooledFilesSorter(TableViewer tableViewer) {
        this.tableViewer = tableViewer;
        this.table = tableViewer.getTable();

        setSortColumn(null);
    }

    public void refresh() {
        tableViewer.refresh();
    }

    public int getSortColumnIndex() {
        return columnIndex;
    }

    public int getSortDirection() {
        return table.getSortDirection();
    }

    public void setSortColumn(TableColumn column) {
        setSortProperties(column, getSortDirection(column));
    }

    public void setSortColumn(TableColumn column, int direction) {

        if (column == null || direction == SWT.NONE) {
            setSortProperties(null, SWT.NONE);
        } else {
            if (direction == SWT.UP || direction == SWT.DOWN) {
                setSortProperties(column, direction);
            }
        }
    }

    private int getSortDirection(TableColumn column) {

        if (column == table.getSortColumn()) {
            if (table.getSortDirection() == SWT.NONE) {
                return SWT.UP;
            } else if (table.getSortDirection() == SWT.UP) {
                return SWT.DOWN;
            } else {
                return SWT.NONE;
            }
        } else {
            return SWT.UP;
        }
    }

    private void setSortProperties(TableColumn column, int direction) {

        table.setSortDirection(direction);

        if (direction == SWT.NONE) {
            this.table.setSortColumn(null);
            this.columnIndex = -1;
        } else {
            this.table.setSortColumn(column);
            this.columnIndex = getColumnIndex(column);
        }

        if (table.getSortDirection() == SWT.DOWN) {
            isReverseOrder = true;
        } else {
            isReverseOrder = false;
        }
    }

    private int getColumnIndex(TableColumn column) {

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (column.equals(table.getColumn(i))) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {

        if (columnIndex == -1) {
            return 0;
        }

        Object value1;
        Object value2;
        if (isReverseOrder) {
            value1 = getColumnValue((SpooledFile)e2, columnIndex);
            value2 = getColumnValue((SpooledFile)e1, columnIndex);
        } else {
            value1 = getColumnValue((SpooledFile)e1, columnIndex);
            value2 = getColumnValue((SpooledFile)e2, columnIndex);
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
        }

        return super.compare(viewer, value1, value2);
    }

    public Object getColumnValue(SpooledFile spooledFile, int columnIndex) {

        if (columnIndex == Columns.STATUS.index) {
            return spooledFile.getStatus();
        } else if (columnIndex == Columns.FILE.index) {
            return spooledFile.getFile();
        } else if (columnIndex == Columns.FILE_NUMBER.index) {
            return spooledFile.getFileNumber();
        } else if (columnIndex == Columns.JOB_NAME.index) {
            return spooledFile.getJobName();
        } else if (columnIndex == Columns.JOB_USER.index) {
            return spooledFile.getJobUser();
        } else if (columnIndex == Columns.JOB_NUMBER.index) {
            return spooledFile.getJobNumber();
        } else if (columnIndex == Columns.JOB_SYSTEM.index) {
            return spooledFile.getJobSystem();
        } else if (columnIndex == Columns.CREATION_DATE.index) {
            return spooledFile.getCreationDateAsDate();
        } else if (columnIndex == Columns.CREATION_TIME.index) {
            return spooledFile.getCreationTimeAsDate();
        } else if (columnIndex == Columns.OUTPUT_QUEUE.index) {
            return spooledFile.getOutputQueue();
        } else if (columnIndex == Columns.OUTPUT_PRIORITY.index) {
            return spooledFile.getOutputPriority();
        } else if (columnIndex == Columns.USER_DATE.index) {
            return spooledFile.getUserData();
        } else if (columnIndex == Columns.FORM_TYPE.index) {
            return spooledFile.getFormType();
        } else if (columnIndex == Columns.COPIES.index) {
            return spooledFile.getCopies();
        } else if (columnIndex == Columns.PAGES.index) {
            return spooledFile.getPages();
        } else if (columnIndex == Columns.CURRENT_PAGE.index) {
            return spooledFile.getCurrentPage();
        } else if (columnIndex == Columns.CREATION_TIMESTAMP.index) {
            return spooledFile.getCreationTimestamp();
        }

        return null;
    }
}
