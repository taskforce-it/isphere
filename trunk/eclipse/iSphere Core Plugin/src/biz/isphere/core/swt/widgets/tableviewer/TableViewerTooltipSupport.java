/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.tableviewer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * This class provides tooltip support for TableViewers prior/equal to Eclipse
 * 3.2.
 * <p>
 * This code is for users pre 3.3 others could use newly added tooltip support
 * in {@link CellLabelProvider}
 */
public class TableViewerTooltipSupport {

    protected TableViewer tableViewer;
    private Listener tableListener;

    public TableViewerTooltipSupport(TableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    public void startSupport() {

        Table table = tableViewer.getTable();

        tableListener = new TooltipListener(tableViewer);
        table.addListener(SWT.Dispose, tableListener);
        table.addListener(SWT.KeyDown, tableListener);
        table.addListener(SWT.MouseMove, tableListener);
        table.addListener(SWT.MouseHover, tableListener);
    }
}
