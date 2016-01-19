/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.swt.widgets.UpperCaseOnlyVerifier;
import biz.isphere.core.internal.ColorHelper;

/**
 * This class implements an ICellModifier An ICellModifier is called when the
 * user modifies a cell in the tableViewer.
 */

public class CopyMemberItemTableCellModifier implements ICellModifier {

    private static final String COLUMN_FROM_LIB = "fromLib"; //$NON-NLS-1$
    private static final String COLUMN_FROM_FILE = "fromFile"; //$NON-NLS-1$
    private static final String COLUMN_FROM_MEMBER = "fromMbr"; //$NON-NLS-1$
    private static final String COLUMN_TO_MEMBER = "toMbr"; //$NON-NLS-1$
    private static final String COLUMN_ERROR_MESSAGE = "errorMessage"; //$NON-NLS-1$

    public static final String[] COLUMN_NAMES = new String[] { COLUMN_FROM_LIB, COLUMN_FROM_FILE, COLUMN_FROM_MEMBER, COLUMN_TO_MEMBER,
        COLUMN_ERROR_MESSAGE };

    private Set<String> editableColumns;

    /**
     * Constructor
     * 
     * @param TableViewerExample an instance of a TableViewerExample
     */
    public CopyMemberItemTableCellModifier(TableViewer tableViewer) {
        super();

        editableColumns = new HashSet<String>();
        editableColumns.add(COLUMN_TO_MEMBER);

        Table table = tableViewer.getTable();
        CellEditor[] editors = new CellEditor[COLUMN_NAMES.length];
        for (int i = 0; i < editors.length; i++) {
            if (hasEditor(COLUMN_NAMES[i])) {
                editors[i] = new TextCellEditor(table) {
                    private Text text;
                    private Color backgroundColor;

                    protected org.eclipse.swt.widgets.Control createControl(org.eclipse.swt.widgets.Composite parent) {
                        Control control = super.createControl(parent);
                        text = getTextControl(control);
                        if (text != null) {
                            text.addVerifyListener(new UpperCaseOnlyVerifier());
                        }
                        return control;
                    };

                    public void setFocus() {
                        super.setFocus();
                        if (isActivated()) {
                            Color color = ColorHelper.getBackgroundColorOfSelectedControls();
                            text.setBackground(color);
                        }
                    };

                    protected void focusLost() {
                        super.focusLost();
                        if (text != null) {
                            text.setBackground(backgroundColor);
                        }
                    };
                    
                    private Text getTextControl(Control control) {
                        if (control instanceof Text) {
                            return (Text)control;
                        }
                        return null;
                    }
                };
            } else {
                editors[i] = null;
            }
        }

        tableViewer.setCellEditors(editors);
        tableViewer.setColumnProperties(COLUMN_NAMES);
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
     *      java.lang.String)
     */
    public boolean canModify(Object element, String columnName) {
        return hasEditor(columnName);
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
     *      java.lang.String)
     */
    public Object getValue(Object element, String columnName) {

        CopyMemberItem copyMemberItem = (CopyMemberItem)element;

        if (COLUMN_FROM_LIB.equals(columnName)) {
            return copyMemberItem.getFromLibrary();
        } else if (COLUMN_FROM_FILE.equals(columnName)) {
            return copyMemberItem.getFromFile();
        } else if (COLUMN_FROM_MEMBER.equals(columnName)) {
            return copyMemberItem.getFromMember();
        } else if (COLUMN_TO_MEMBER.equals(columnName)) {
            return copyMemberItem.getToMember();
        } else if (COLUMN_ERROR_MESSAGE.equals(columnName)) {
            return copyMemberItem.getErrorMessage();
        }

        throw new IllegalArgumentException("Illegal argument 'columnName': " + columnName); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
     *      java.lang.String, java.lang.Object)
     */
    public void modify(Object element, String columnName, Object value) {

        TableItem item = (TableItem)element;
        CopyMemberItem copyMemberItem = (CopyMemberItem)item.getData();

        if (COLUMN_TO_MEMBER.equals(columnName)) {
            copyMemberItem.setToMember((String)value);
        } else {
            //            throw new IllegalArgumentException("Illegal argument 'columnName': " + columnName); //$NON-NLS-1$
        }
    }

    private boolean hasEditor(String columnName) {
        return editableColumns.contains(columnName);
    }
}
