/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import biz.isphere.base.internal.ByteHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataqueue.action.DisplayEndOfDataAction;
import biz.isphere.core.dataqueue.action.ViewInHexAction;
import biz.isphere.core.dataqueue.retrieve.description.RDQD0100;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200MessageEntry;

public class LabelProviderTableViewer extends LabelProvider implements ITableLabelProvider, IPropertyChangeListener {

    private static final String END_OF_DATA = "«"; //$NON-NLS-1$
    private static final String CONTINUE = " ..."; //$NON-NLS-1$

    private static final int COLUMN_ENTRY_TYPE = 0;
    private static final int COLUMN_KEY = 1;
    private static final int COLUMN_SENDER_ID = 2;
    private static final int COLUMN_MESSAGE_TEXT_LENGTH = 3;
    private static final int COLUMN_MESSAGE_TEXT = 4;

    private static final int NUM_COLUMNS = 5;

    private int visibleColumns[] = new int[NUM_COLUMNS];
    private DateFormat dateFormatter;
    private boolean isHexView;
    private boolean isDisplayEndOfData;

    private RDQD0100 rdqd0100;

    public LabelProviderTableViewer(RDQD0100 rdqd0100) {

        this.rdqd0100 = rdqd0100;

        this.visibleColumns = new int[NUM_COLUMNS];
        for (int i = 0; i < NUM_COLUMNS; i++) {
            this.visibleColumns[i] = -1;
        }

        this.dateFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        setHexMode(true);
        setDisplayEndOfData(false);
    }

    public static int getNumColumns() {
        return NUM_COLUMNS;
    }

    public void setEntryTypeColumnIndex(int index) {
        visibleColumns[COLUMN_ENTRY_TYPE] = index;
    }

    public void setKeyColumnIndex(int index) {
        visibleColumns[COLUMN_KEY] = index;
    }

    public void setSenderIdColumnIndex(int index) {
        visibleColumns[COLUMN_SENDER_ID] = index;
    }

    public void setMessageTextLengthColumnIndex(int index) {
        visibleColumns[COLUMN_MESSAGE_TEXT_LENGTH] = index;
    }

    public void setMessageTextColumnIndex(int index) {
        visibleColumns[COLUMN_MESSAGE_TEXT] = index;
    }

    public boolean isLastColumn(int columnIndex) {

        int maxColumnIndex = -1;
        for (int i = 0; i < NUM_COLUMNS; i++) {
            if (visibleColumns[i] != -1) {
                maxColumnIndex++;
            }
        }

        if (maxColumnIndex == columnIndex) {
            return true;
        }

        return false;
    }

    public String getColumnText(Object element, int columnIndex) {

        try {

            RDQM0200MessageEntry messageDescription = (RDQM0200MessageEntry)element;

            if (columnIndex == visibleColumns[COLUMN_ENTRY_TYPE]) {
                Date date = messageDescription.getMessageEnqueueDateAndTime();
                if (date == null) {
                    return ""; //$NON-NLS-1$
                } else {
                    return dateFormatter.format(date);
                }
            } else if (columnIndex == visibleColumns[COLUMN_KEY]) {
                if (isHexView) {
                    return ByteHelper.getHexString(messageDescription.getKeyBytes());
                } else {
                    return StringHelper.trimR(messageDescription.getKeyText());
                }
            } else if (columnIndex == visibleColumns[COLUMN_SENDER_ID]) {
                return StringHelper.trimR(messageDescription.getSenderID().toString());
            } else if (columnIndex == visibleColumns[COLUMN_MESSAGE_TEXT_LENGTH]) {
                return Integer.toString(messageDescription.getEnqueuedMesageEntryLength());
            } else if (columnIndex == visibleColumns[COLUMN_MESSAGE_TEXT]) {

                String label;
                if (isHexView) {
                    label = ByteHelper.getHexString(messageDescription.getMessageBytes(!rdqd0100.isSenderIDIncludedInMessageText()));
                } else {
                    label = messageDescription.getMessageText(!rdqd0100.isSenderIDIncludedInMessageText());

                    if (messageDescription.getEnqueuedMesageEntryLength() > messageDescription.getRDQM0200().getMaximumMessageTextLengthRequested()) {
                        label = label + CONTINUE;
                    } else {
                        if (isDisplayEndOfData) {
                            label = label + END_OF_DATA;
                        } else {
                            label = StringHelper.trimR(label);
                        }
                    }
                }

                return label;
            }

        } catch (Exception e) {
            return e.getLocalizedMessage();
        }

        return "*UNKNOWN"; //$NON-NLS-1$
    }

    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == COLUMN_ENTRY_TYPE) {
            if (rdqd0100.isKeyed()) {
                return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_KEY);
            } else {
                return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_MESSAGE);
            }
        } else {
            return null;
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() instanceof ViewInHexAction) {
            Action action = (Action)event.getSource();
            setHexMode(action.isChecked());
        } else if (event.getSource() instanceof DisplayEndOfDataAction) {
            Action action = (Action)event.getSource();
            setDisplayEndOfData(action.isChecked());
        }
    }

    public void setHexMode(boolean isHexMode) {
        this.isHexView = isHexMode;
    }

    public void setDisplayEndOfData(boolean isDisplayEndOfData) {
        this.isDisplayEndOfData = isDisplayEndOfData;
    }
}
