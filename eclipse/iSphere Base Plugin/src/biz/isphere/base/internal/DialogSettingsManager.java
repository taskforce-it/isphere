/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class DialogSettingsManager {

    public static final String COLUMN_WIDTH = "COLUMN_WIDTH_";
    public static final String COLUMN_NAME = "COLUMN_NAME";

    private IDialogSettings dialogSettings;
    private Class<?> section;

    private SimpleDateFormat dateFormatter;
    private ControlAdapter columnResizeListener;

    public DialogSettingsManager(IDialogSettings aDialogSettings) {
        this(aDialogSettings, null);
    }

    public DialogSettingsManager(IDialogSettings aDialogSettings, Class<?> section) {
        this.dialogSettings = aDialogSettings;
        this.section = section;
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
    }

    /**
     * Retrieves the screen value that was last displayed on the dialog.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the screen value that was last shown
     */
    public String loadValue(String aKey, String aDefault) {
        String tValue = getDialogSettings().get(aKey);
        if (StringHelper.isNullOrEmpty(tValue)) {
            tValue = aDefault;
        }
        return tValue;
    }

    /**
     * Stores a given string value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, String aValue) {
        getDialogSettings().put(aKey, aValue);
    }

    /**
     * Retrieves the screen value that was last displayed on the dialog.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the screen value that was last shown
     */
    public boolean loadBooleanValue(String aKey, boolean aDefault) {
        String tValue = getDialogSettings().get(aKey);
        return BooleanHelper.tryParseBoolean(tValue, aDefault);
    }

    /**
     * Stores a given boolean value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, boolean aValue) {
        getDialogSettings().put(aKey, aValue);
    }

    /**
     * Retrieves the the value that is assigned to a given key.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the value that is assigned to the key
     */
    public int loadIntValue(String aKey, int aDefault) {
        return IntHelper.tryParseInt(getDialogSettings().get(aKey), aDefault);
    }

    /**
     * Stores a given integer value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, int aValue) {
        getDialogSettings().put(aKey, aValue);
        IntHelper.tryParseInt(getDialogSettings().get(aKey), -1);
    }

    /**
     * Retrieves the the value that is assigned to a given key.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the value that is assigned to the key
     */
    public Date loadDateValue(String aKey, Date aDefault) {

        String tValue = getDialogSettings().get(aKey);
        if (StringHelper.isNullOrEmpty(tValue)) {
            return aDefault;
        }

        try {
            Date tDate = dateFormatter.parse(tValue);
            return tDate;
        } catch (Exception e) {
            return aDefault;
        }
    }

    /**
     * Stores a given java.util.Date value to preserve it for the next time the
     * dialog is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, Date aValue) {
        String value = dateFormatter.format(aValue);
        getDialogSettings().put(aKey, value);
    }

    /**
     * Produces a resizable table given for a given table.
     * 
     * @param table - Table, the column is added to.
     * @param style - The style of column to construct.
     * @param index - The zero-relative index to store the column in the table.
     * @param name - The name of the table column.
     * @param width - The initial width of the table column.
     * @return
     */
    public TableColumn createResizableTableColumn(Table table, int style, int index, String name, int width) {

        TableColumn tableColumn = new TableColumn(table, style, index);
        tableColumn.setResizable(true);
        tableColumn.setData(COLUMN_NAME, name);
        tableColumn.setData(COLUMN_WIDTH, new Integer(width));

        int effectiveWidth = loadIntValue(produceColumnWidthKey(tableColumn), width);
        tableColumn.setWidth(effectiveWidth);

        tableColumn.addControlListener(getColumnResizeListener());

        return tableColumn;
    }

    public void resetColumnWidths(Table table) {

        table.setVisible(false);

        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumn(i);
            column.setWidth(getDefaultColumnWidth(column));
        }

        table.setVisible(true);
    }

    private ControlAdapter getColumnResizeListener() {

        if (columnResizeListener == null) {
            columnResizeListener = new ControlAdapter() {
                @Override
                public void controlResized(ControlEvent event) {
                    TableColumn column = (TableColumn)event.getSource();
                    String key = produceColumnWidthKey(column);
                    if (key != null) {
                        storeValue(key, column.getWidth());
                    }
                }
            };
        }

        return columnResizeListener;
    }

    private String produceColumnWidthKey(TableColumn column) {

        if (column == null) {
            return null;
        }

        String columnName = getColumnName(column);
        if (columnName == null) {
            return null;
        }

        return COLUMN_WIDTH + columnName;
    }

    private String getColumnName(TableColumn column) {

        if (column == null) {
            return null;
        }

        Object objectName = column.getData(COLUMN_NAME);
        if (objectName instanceof String) {
            return (String)objectName;
        }

        return null;
    }

    private int getDefaultColumnWidth(TableColumn column) {

        if (column == null) {
            return -1;
        }

        Object data = column.getData(COLUMN_WIDTH);
        if (data == null) {
            return -1;
        }

        int width = IntHelper.tryParseInt(data.toString(), -1);

        return width;
    }

    /**
     * Returns the dialog settings store.
     * 
     * @return dialog settings
     */
    private IDialogSettings getDialogSettings() {

        if (section == null) {
            return dialogSettings;
        }

        String sectionName = section.getName();
        IDialogSettings dialogSectionSettings = dialogSettings.getSection(sectionName);
        if (dialogSectionSettings == null) {
            dialogSectionSettings = dialogSettings.addNewSection(sectionName);
        }

        return dialogSectionSettings;
    }

}
