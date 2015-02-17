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

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataqueue.retrieve.description.RDQD0100;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200MessageEntry;

public class LabelProviderTableViewer extends LabelProvider implements ITableLabelProvider {

    private static final int COLUMN_ENTRY_TYPE = 0;
    private static final int COLUMN_KEY = 1;
    private static final int COLUMN_SENDER_ID = 2;
    private static final int COLUMN_MESSAGE_TEXT_LENGTH = 3;
    private static final int COLUMN_MESSAGE_TEXT = 4;

    private static final int NUM_COLUMNS = 5;
    
    private int visibleColumns[] = new int[NUM_COLUMNS];
    private DateFormat dateFormatter;

    private RDQD0100 rdqd0100;

    public LabelProviderTableViewer(RDQD0100 rdqd0100) {

        this.rdqd0100 = rdqd0100;

        visibleColumns = new int[NUM_COLUMNS];
        for (int i = 0; i < NUM_COLUMNS; i++) {
            visibleColumns[i] = -1;
        }
        
        dateFormatter = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
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
                return StringHelper.trimR(messageDescription.getKeyText());
            } else if (columnIndex == visibleColumns[COLUMN_SENDER_ID]) {
                return StringHelper.trimR(messageDescription.getSenderID().toString());
            } else if (columnIndex == visibleColumns[COLUMN_MESSAGE_TEXT_LENGTH]) {
                return Integer.toString(messageDescription.getEnqueuedMesageEntryLength()).trim();
            } else if (columnIndex == visibleColumns[COLUMN_MESSAGE_TEXT]) {
                return StringHelper.trimR(messageDescription.getMessageText(!rdqd0100.isSenderIDIncludedInMessageText()));
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
}
