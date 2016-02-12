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
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import biz.isphere.strpreprc.model.StrPrePrcParameter;

public class ParametersTableViewer extends AbstractTableViewer<StrPrePrcParameter> {

    public ParametersTableViewer(Composite parent) {
        super(parent);
    }

    private class SorterCLCommandViewer extends ViewerSorter {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {

            if (e1 == null && e2 == null) {
                return 0;
            } else if (e1 == null) {
                return 1;
            } else if (e2 == null) {
                return -1;
            }

            StrPrePrcParameter p1 = (StrPrePrcParameter)e1;
            StrPrePrcParameter p2 = (StrPrePrcParameter)e2;

            if (p1.getType() != p2.getType()) {
                return p1.getType().compareTo(p2.getType());
            } else {
                return p1.getKeyword().compareTo(p2.getKeyword());
            }
        }
    }

    private class LabelProviderCLCommandsViewer extends LabelProvider implements ITableLabelProvider {

        public String getColumnText(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return ((StrPrePrcParameter)element).getKeyword();
            } else if (columnIndex == 1) {
                return ((StrPrePrcParameter)element).getValue();
            } else if (columnIndex == 2) {
                return ((StrPrePrcParameter)element).getType().getType();
            }
            return "*UNKNOWN";
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }

    private class ContentProviderCLCommandsViewer implements IStructuredContentProvider {

        StrPrePrcParameter[] preCommands;

        public Object[] getElements(Object inputElement) {
            return preCommands;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            preCommands = (StrPrePrcParameter[])newInput;
        }
    }

    @Override
    protected TableViewer createViewer(Composite parent) {

        TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.setLabelProvider(new LabelProviderCLCommandsViewer());
        tableViewer.setContentProvider(new ContentProviderCLCommandsViewer());
        tableViewer.setSorter(new SorterCLCommandViewer());

        Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        TableColumn columnKeyword = new TableColumn(table, SWT.NONE);
        columnKeyword.setText("Keyword");

        TableColumn columnValue = new TableColumn(table, SWT.NONE);
        columnValue.setText("Value");

        TableColumn columnType = new TableColumn(table, SWT.NONE);
        columnType.setText("Section");

        columnKeyword.setWidth(100);
        columnValue.setWidth(200);
        columnType.setWidth(100);

        return tableViewer;
    }

    @Override
    protected StrPrePrcParameter[] createArray(int size) {
        return new StrPrePrcParameter[size];
    }
}
