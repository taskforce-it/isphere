/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.rse;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.DateTimeHelper;
import biz.isphere.core.objectsynchronization.CompareOptions;

/**
 * Class the provides the content for the cells of the table.
 */
public abstract class AbstractTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    protected static final int COLUMN_DUMMY = 0;
    protected static final int COLUMN_LEFT_LIBRARY = 1;
    protected static final int COLUMN_LEFT_FILE = 2;
    protected static final int COLUMN_LEFT_MEMBER = 3;
    protected static final int COLUMN_LEFT_SOURCE_TYPE = 4;
    protected static final int COLUMN_LEFT_LAST_CHANGES = 5;
    protected static final int COLUMN_LEFT_DESCRIPTION = 6;
    protected static final int COLUMN_COMPARE_RESULT = 7;
    protected static final int COLUMN_RIGHT_LIBRARY = 8;
    protected static final int COLUMN_RIGHT_FILE = 9;
    protected static final int COLUMN_RIGHT_MEMBER = 10;
    protected static final int COLUMN_RIGHT_SOURCE_TYPE = 11;
    protected static final int COLUMN_RIGHT_LAST_CHANGES = 12;
    protected static final int COLUMN_RIGHT_DESCRIPTION = 13;

    protected Image copyToLeft;
    protected Image copyToRight;
    protected Image copyNotEqual;
    protected Image copyEqual;
    protected Image error;

    private CompareOptions compareOptions;

    public AbstractTableLabelProvider(TableViewer tableViewer, int columnIndex) {

        this.compareOptions = null;

        this.copyToLeft = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_LEFT).createImage();
        this.copyToRight = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_RIGHT).createImage();
        this.copyNotEqual = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_NOT_EQUAL).createImage();
        this.error = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_ERROR).createImage();

        if (useCompareStatusImagePainter()) {
            tableViewer.getTable().addListener(SWT.PaintItem, new CompareStatusImagePainter(columnIndex));
        }
    }

    protected boolean useCompareStatusImagePainter() {
        return true;
    }

    public void setCompareOptions(CompareOptions compareOptions) {
        this.compareOptions = compareOptions;
    }

    public Image getColumnImage(Object element, int columnIndex) {

        if (columnIndex != COLUMN_COMPARE_RESULT) {
            return null;
        }

        if (useCompareStatusImagePainter()) {
            return null;
        }

        MemberCompareItem compareItem = (MemberCompareItem)element;
        if (compareItem == null) {
            return null;
        }

        int compareStatus = compareItem.getCompareStatus(compareOptions);
        if (compareStatus == MemberCompareItem.RIGHT_MISSING) {
            return copyToRight;
        } else if (compareStatus == MemberCompareItem.LEFT_MISSING) {
            return copyToLeft;
        } else if (compareStatus == MemberCompareItem.LEFT_EQUALS_RIGHT) {
            return copyEqual;
        } else if (compareStatus == MemberCompareItem.NOT_EQUAL) {
            return copyNotEqual;
        } else if (compareStatus == MemberCompareItem.ERROR) {
            return error;
        }

        return null;
    }

    public String getColumnText(Object element, int columnIndex) {

        if (columnIndex == COLUMN_COMPARE_RESULT) {
            return null;
        }

        if (!(element instanceof MemberCompareItem)) {
            return ""; //$NON-NLS-1$
        }

        MemberCompareItem compareItem = (MemberCompareItem)element;

        switch (columnIndex) {
        case COLUMN_DUMMY:
            return ""; //$NON-NLS-1$

        case COLUMN_LEFT_LIBRARY:
            if (compareItem.getLeftMemberDescription() != null) {
                return compareItem.getLeftMemberDescription().getLibraryName();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_LEFT_FILE:
            if (compareItem.getLeftMemberDescription() != null) {
                return compareItem.getLeftMemberDescription().getFileName();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_LEFT_MEMBER:
            if (compareItem.getLeftMemberDescription() != null) {
                return compareItem.getLeftMemberDescription().getMemberName();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_LEFT_SOURCE_TYPE:
            if (compareItem.getLeftMemberDescription() != null) {
                return compareItem.getLeftMemberDescription().getSourceType();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_LEFT_LAST_CHANGES:
            if (compareItem.getLeftMemberDescription() != null) {
                return DateTimeHelper.getTimestampFormatted(compareItem.getLeftMemberDescription().getLastChangedDate());
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_LEFT_DESCRIPTION:
            if (compareItem.getLeftMemberDescription() != null) {
                return compareItem.getLeftMemberDescription().getText();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_LIBRARY:
            if (compareItem.getRightMemberDescription() != null) {
                return compareItem.getRightMemberDescription().getLibraryName();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_FILE:
            if (compareItem.getRightMemberDescription() != null) {
                return compareItem.getRightMemberDescription().getFileName();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_MEMBER:
            if (compareItem.getRightMemberDescription() != null) {
                return compareItem.getRightMemberDescription().getMemberName();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_SOURCE_TYPE:
            if (compareItem.getRightMemberDescription() != null) {
                return compareItem.getRightMemberDescription().getSourceType();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_LAST_CHANGES:
            if (compareItem.getRightMemberDescription() != null) {
                return DateTimeHelper.getTimestampFormatted(compareItem.getRightMemberDescription().getLastChangedDate());
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_DESCRIPTION:
            if (compareItem.getRightMemberDescription() != null) {
                return compareItem.getRightMemberDescription().getText();
            } else {
                return ""; //$NON-NLS-1$
            }

        default:
            return ""; //$NON-NLS-1$
        }
    }

    @Override
    public void dispose() {

        dispose(copyToLeft);
        dispose(copyToRight);
        dispose(copyNotEqual);
        dispose(copyEqual);
        dispose(error);

        super.dispose();
    }

    private void dispose(Image image) {
        if (!image.isDisposed()) {
            image.dispose();
        }
    }

    protected class CompareStatusImagePainter implements Listener {

        private int columnIndex;

        public CompareStatusImagePainter(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public void handleEvent(Event event) {
            TableItem tableItem = (TableItem)event.item;
            if (event.index == columnIndex) {
                Image tmpImage = getImage(tableItem);
                if (tmpImage == null) {
                    return;
                }
                int tmpWidth = tableItem.getParent().getColumn(event.index).getWidth();
                int tmpHeight = ((TableItem)event.item).getBounds().height;
                int tmpX = tmpImage.getBounds().width;
                tmpX = (tmpWidth / 2 - tmpX / 2);
                int tmpY = tmpImage.getBounds().height;
                tmpY = (tmpHeight / 2 - tmpY / 2);
                if (tmpX <= 0)
                    tmpX = event.x;
                else
                    tmpX += event.x;
                if (tmpY <= 0)
                    tmpY = event.y;
                else
                    tmpY += event.y;
                event.gc.drawImage(tmpImage, tmpX, tmpY);
            }
        }

        private Image getImage(TableItem tableItem) {

            MemberCompareItem compareItem = (MemberCompareItem)tableItem.getData();
            if (compareItem == null) {
                return null;
            }

            int compareStatus = compareItem.getCompareStatus(compareOptions);
            if (compareStatus == MemberCompareItem.RIGHT_MISSING) {
                return copyToRight;
            } else if (compareStatus == MemberCompareItem.LEFT_MISSING) {
                return copyToLeft;
            } else if (compareStatus == MemberCompareItem.LEFT_EQUALS_RIGHT) {
                return copyEqual;
            } else if (compareStatus == MemberCompareItem.NOT_EQUAL) {
                return copyNotEqual;
            } else if (compareStatus == MemberCompareItem.ERROR) {
                return error;
            }
            return null;
        }
    }
}
