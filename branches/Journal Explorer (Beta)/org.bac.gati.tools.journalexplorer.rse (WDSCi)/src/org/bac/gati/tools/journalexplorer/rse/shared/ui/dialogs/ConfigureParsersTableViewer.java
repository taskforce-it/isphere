/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.bac.gati.tools.journalexplorer.rse.shared.ui.dialogs;

import org.bac.gati.tools.journalexplorer.rse.base.interfaces.IMetaTable;
import org.bac.gati.tools.journalexplorer.rse.shared.ui.labelprovider.ParserColumnLabel;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public final class ConfigureParsersTableViewer {

    public static void configureTableViewer(final TableViewer tableViewer, final String[] columnNames) {

        Table table = tableViewer.getTable();
        TextCellEditor textEditor;

        tableViewer.setLabelProvider(new ParserColumnLabel());

        // Create the cell editors
        final CellEditor[] editors = new CellEditor[columnNames.length];

        // Column 1 : Journal object
        editors[0] = null;

        // Column 2 : Parser library
        textEditor = new TextCellEditor(table);
        ((Text)textEditor.getControl()).setTextLimit(10);
        editors[1] = textEditor;

        // Column 3 : Parser name
        textEditor = new TextCellEditor(table);
        ((Text)textEditor.getControl()).setTextLimit(10);
        editors[2] = textEditor;

        // Column 4 : Parsing offset
        textEditor = new TextCellEditor(table);
        ((Text)textEditor.getControl()).addVerifyListener(

        new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                e.doit = "0123456789".indexOf(e.text) >= 0;
            }
        });
        editors[3] = textEditor;

        // Assign the cell editors to the viewer
        tableViewer.setCellEditors(editors);

        // Set the cell modifier for the viewer
        tableViewer.setCellModifier(new ICellModifier() {

            public void modify(Object element, String property, Object value) {

                TableItem tableItem = (TableItem)element;
                IMetaTable metaTable = (IMetaTable)tableItem.getData();

                int index = getColumnIndex(property);
                switch (index) {
                case 1: // Parser library
                    metaTable.setDefinitionLibrary((String)value);
                    tableViewer.update(metaTable, null);
                case 2: // Parser name
                    metaTable.setDefinitionName((String)value);
                    tableViewer.update(metaTable, null);
                case 3: // Parsing offset
                    try {
                        metaTable.setParsingOffset(Integer.parseInt((String)value));
                        tableViewer.update(metaTable, null);
                    } catch (Exception e) {
                    }
                default:
                }
            }

            public Object getValue(Object element, String property) {

                IMetaTable metaTable = (IMetaTable)element;

                int index = getColumnIndex(property);
                switch (index) {
                case 0: // Journaled object
                    return metaTable.getQualifiedName();
                case 1: // Parser library
                    return metaTable.getDefinitionLibrary();
                case 2: // Parser name
                    return metaTable.getDefinitionName();
                case 3: // Parsing offset
                    return Integer.toString(metaTable.getParsingOffset());
                default:
                    return "";
                }
            }

            public boolean canModify(Object element, String property) {

                int index = getColumnIndex(property);
                if (index < 0 || index > editors.length - 1) {
                    return false;
                }

                return editors[index] != null;
            }

            private int getColumnIndex(String property) {

                for (int i = 0; i < columnNames.length; i++) {
                    if (property.equals(columnNames[i])) {
                        return i;
                    }

                }

                return -1;
            }
        });
    }
}
