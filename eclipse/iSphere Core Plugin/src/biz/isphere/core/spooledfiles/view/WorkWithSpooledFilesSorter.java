package biz.isphere.core.spooledfiles.view;

import java.util.Date;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.spooledfiles.SpooledFile;

/**
 * This table sorter automatically stores the sort properties in
 * "dialog_settings.xml". Previously stored properties can be re-applied by
 * calling {@link #setPreviousSortOrder()}.
 */
public class WorkWithSpooledFilesSorter extends ViewerSorter {

    private static final String SORT_COLUMN_INDEX = "sortColumnIndex"; //$NON-NLS-1$
    private static final String SORT_DIRECTION = "sortDirection"; //$NON-NLS-1$

    private static final String SORT_UP = "Up"; //$NON-NLS-1$
    private static final String SORT_DOWN = "Down"; //$NON-NLS-1$
    private static final String SORT_NONE = "None"; //$NON-NLS-1$

    private TableViewer tableViewer;
    private Table table;
    private DialogSettingsManager dialogSettingsManager;

    private int columnIndex;
    private boolean isReverseOrder;

    public WorkWithSpooledFilesSorter(TableViewer tableViewer, DialogSettingsManager dialogSettingsManager) {
        this.tableViewer = tableViewer;
        this.table = tableViewer.getTable();
        this.dialogSettingsManager = dialogSettingsManager;

        setSortColumn(null, SORT_NONE);
    }

    public int getSortColumnIndex() {
        return columnIndex;
    }

    public String getSortDirection() {

        int sortDirection = table.getSortDirection();
        if (sortDirection == SWT.UP) {
            return SORT_UP;
        } else if (sortDirection == SWT.DOWN) {
            return SORT_DOWN;
        } else {
            return SORT_NONE;
        }
    }

    public void setSortColumn(TableColumn column) {
        setSortProperties(column, changeSortDirection(column));
    }

    public void setSortColumn(TableColumn column, String direction) {

        if (column == null) {
            setSortProperties(null, SWT.NONE);
        } else {
            if (SORT_UP.equals(direction)) {
                setSortProperties(column, SWT.UP);
            } else if (SORT_DOWN.equals(direction)) {
                setSortProperties(column, SWT.DOWN);
            } else {
                setSortProperties(column, SWT.NONE);
            }
        }
    }

    public void setPreviousSortOrder() {

        if (dialogSettingsManager == null) {
            return;
        }

        String sortColumnIndex = dialogSettingsManager.loadValue(SORT_COLUMN_INDEX, null);
        if (sortColumnIndex == null) {
            return;
        }

        String sortOrder = dialogSettingsManager.loadValue(SORT_DIRECTION, null);
        if (sortOrder == null) {
            return;
        }

        int columnIndex = IntHelper.tryParseInt(sortColumnIndex, -1);
        if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
            setSortColumn(table.getColumn(columnIndex), sortOrder);
        }
    }

    public void refresh() {
        tableViewer.refresh();
    }

    private int changeSortDirection(TableColumn column) {

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

        if (dialogSettingsManager != null) {
            dialogSettingsManager.storeValue(SORT_COLUMN_INDEX, Integer.toString(getSortColumnIndex()));
            dialogSettingsManager.storeValue(SORT_DIRECTION, getSortDirection());
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

        if (columnIndex == Columns.STATUS.ordinal()) {
            return spooledFile.getStatus();
        } else if (columnIndex == Columns.FILE.ordinal()) {
            return spooledFile.getFile();
        } else if (columnIndex == Columns.FILE_NUMBER.ordinal()) {
            return spooledFile.getFileNumber();
        } else if (columnIndex == Columns.JOB_NAME.ordinal()) {
            return spooledFile.getJobName();
        } else if (columnIndex == Columns.JOB_USER.ordinal()) {
            return spooledFile.getJobUser();
        } else if (columnIndex == Columns.JOB_NUMBER.ordinal()) {
            return spooledFile.getJobNumber();
        } else if (columnIndex == Columns.JOB_SYSTEM.ordinal()) {
            return spooledFile.getJobSystem();
        } else if (columnIndex == Columns.CREATION_DATE.ordinal()) {
            return spooledFile.getCreationDateAsDate();
        } else if (columnIndex == Columns.CREATION_TIME.ordinal()) {
            return spooledFile.getCreationTimeAsDate();
        } else if (columnIndex == Columns.OUTPUT_QUEUE.ordinal()) {
            return spooledFile.getOutputQueue();
        } else if (columnIndex == Columns.OUTPUT_PRIORITY.ordinal()) {
            return spooledFile.getOutputPriority();
        } else if (columnIndex == Columns.USER_DATA.ordinal()) {
            return spooledFile.getUserData();
        } else if (columnIndex == Columns.FORM_TYPE.ordinal()) {
            return spooledFile.getFormType();
        } else if (columnIndex == Columns.COPIES.ordinal()) {
            return spooledFile.getCopies();
        } else if (columnIndex == Columns.PAGES.ordinal()) {
            return spooledFile.getPages();
        } else if (columnIndex == Columns.CURRENT_PAGE.ordinal()) {
            return spooledFile.getCurrentPage();
        } else if (columnIndex == Columns.CREATION_TIMESTAMP.ordinal()) {
            return spooledFile.getCreationTimestamp();
        }

        return null;
    }
}
