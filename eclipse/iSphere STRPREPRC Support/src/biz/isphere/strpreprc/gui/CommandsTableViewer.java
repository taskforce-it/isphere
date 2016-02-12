/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.gui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class CommandsTableViewer extends AbstractTableViewer<String> {

    public CommandsTableViewer(Composite parent) {
        super(parent);
    }

    private class LabelProviderCLCommandsViewer extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return (String)element;
            }
            return "*UNKNOWN";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private class ContentProviderCLCommandsViewer implements IStructuredContentProvider {

        String[] preCommands;

        public Object[] getElements(Object inputElement) {
            return preCommands;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            preCommands = (String[])newInput;
        }
    }

    @Override
    protected TableViewer createViewer(Composite parent) {

        TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.setLabelProvider(new LabelProviderCLCommandsViewer());
        tableViewer.setContentProvider(new ContentProviderCLCommandsViewer());

        Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(false);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        TableColumn columnPreCommand = new TableColumn(table, SWT.NONE);
        columnPreCommand.setText("Command");

        columnPreCommand.setWidth(1000);

        return tableViewer;
    }

    @Override
    protected String[] createArray(int size) {
        return new String[size];
    }

}
