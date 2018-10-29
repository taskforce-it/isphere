/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.rse;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import biz.isphere.core.ISpherePlugin;

/**
 * Class the provides the content for the cells of the table.
 */
public abstract class AbstractTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    protected static final int COLUMN_DUMMY = 0;
    protected static final int COLUMN_LEFT_MESSAGE_ID = 1;
    protected static final int COLUMN_LEFT_MESSAGE_TEXT = 2;
    protected static final int COLUMN_COMPARE_RESULT = 3;
    protected static final int COLUMN_RIGHT_MESSAGE_ID = 4;
    protected static final int COLUMN_RIGHT_MESSAGE_TEXT = 5;

    protected Image copyToLeft;
    protected Image copyToRight;
    protected Image copyNotEqual;
    protected Image copyEqual;

    public AbstractTableLabelProvider(TableViewer tableViewer, int columnIndex) {

        this.copyToLeft = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_LEFT).createImage();
        this.copyToRight = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_RIGHT).createImage();
        this.copyNotEqual = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_NOT_EQUAL).createImage();
        this.copyEqual = ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_COPY_EQUAL).createImage();

        if (useCompareStatusImagePainter()) {
            tableViewer.getTable().addListener(SWT.PaintItem, new CompareStatusImagePainter(columnIndex));
        }
    }

    protected boolean useCompareStatusImagePainter() {
        return true;
    }

    public Image getColumnImage(Object element, int columnIndex) {

        if (columnIndex != COLUMN_COMPARE_RESULT) {
            return null;
        }

        if (useCompareStatusImagePainter()) {
            return null;
        }

        MessageFileCompareItem compareItem = (MessageFileCompareItem)element;
        if (compareItem == null) {
            return null;
        }

        int compareStatus = compareItem.getCompareStatus();
        if (compareStatus == MessageFileCompareItem.RIGHT_MISSING) {
            return copyToRight;
        } else if (compareStatus == MessageFileCompareItem.LEFT_MISSING) {
            return copyToLeft;
        } else if (compareStatus == MessageFileCompareItem.LEFT_EQUALS_RIGHT) {
            return copyEqual;
        } else if (compareStatus == MessageFileCompareItem.NOT_EQUAL) {
            return copyNotEqual;
        }

        return null;
    }

    public String getColumnText(Object element, int columnIndex) {

        if (columnIndex == COLUMN_COMPARE_RESULT) {
            return null;
        }

        if (!(element instanceof MessageFileCompareItem)) {
            return ""; //$NON-NLS-1$
        }

        MessageFileCompareItem compareItem = (MessageFileCompareItem)element;

        switch (columnIndex) {
        case COLUMN_DUMMY:
            return ""; //$NON-NLS-1$

        case COLUMN_LEFT_MESSAGE_ID:
            if (compareItem.getLeftMessageDescription() != null) {
                return compareItem.getLeftMessageDescription().getMessageId();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_LEFT_MESSAGE_TEXT:
            if (compareItem.getLeftMessageDescription() != null) {
                return compareItem.getLeftMessageDescription().getMessage();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_MESSAGE_ID:
            if (compareItem.getRightMessageDescription() != null) {
                return compareItem.getRightMessageDescription().getMessageId();
            } else {
                return ""; //$NON-NLS-1$
            }

        case COLUMN_RIGHT_MESSAGE_TEXT:
            if (compareItem.getRightMessageDescription() != null) {
                return compareItem.getRightMessageDescription().getMessage();
            } else {
                return ""; //$NON-NLS-1$
            }

        default:
            return ""; //$NON-NLS-1$
        }
    }

    @Override
    public void dispose() {

        copyToLeft.dispose();
        copyToRight.dispose();
        copyNotEqual.dispose();
        copyEqual.dispose();

        super.dispose();
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

            MessageFileCompareItem compareItem = (MessageFileCompareItem)tableItem.getData();
            if (compareItem == null) {
                return null;
            }

            int compareStatus = compareItem.getCompareStatus();
            if (compareStatus == MessageFileCompareItem.RIGHT_MISSING) {
                return copyToRight;
            } else if (compareStatus == MessageFileCompareItem.LEFT_MISSING) {
                return copyToLeft;
            } else if (compareStatus == MessageFileCompareItem.LEFT_EQUALS_RIGHT) {
                return copyEqual;
            } else if (compareStatus == MessageFileCompareItem.NOT_EQUAL) {
                return copyNotEqual;
            }
            return null;
        }
    }
}
