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
        if (columnIndex == Columns.STATUS.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.STATUS);
        } else if (columnIndex == Columns.FILE.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.FILE);
        } else if (columnIndex == Columns.FILE_NUMBER.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.FILE_NUMBER);
        } else if (columnIndex == Columns.JOB_NAME.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_NAME);
        } else if (columnIndex == Columns.JOB_USER.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_USER);
        } else if (columnIndex == Columns.JOB_NUMBER.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_NUMBER);
        } else if (columnIndex == Columns.JOB_SYSTEM.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.JOB_SYSTEM);
        } else if (columnIndex == Columns.CREATION_DATE.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.CREATION_DATE);
        } else if (columnIndex == Columns.CREATION_TIME.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.CREATION_TIME);
        } else if (columnIndex == Columns.OUTPUT_QUEUE.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.OUTPUT_QUEUE);
        } else if (columnIndex == Columns.OUTPUT_PRIORITY.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.OUTPUT_PRIORITY);
        } else if (columnIndex == Columns.USER_DATA.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.USER_DATA);
        } else if (columnIndex == Columns.FORM_TYPE.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.FORM_TYPE);
        } else if (columnIndex == Columns.COPIES.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.COPIES);
        } else if (columnIndex == Columns.PAGES.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.PAGES);
        } else if (columnIndex == Columns.CURRENT_PAGE.ordinal()) {
            return getColumnText(spooledFile, SpooledFileBaseResourceAdapter.CURRENT_PAGE);
        } else if (columnIndex == Columns.CREATION_TIMESTAMP.ordinal()) {
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
