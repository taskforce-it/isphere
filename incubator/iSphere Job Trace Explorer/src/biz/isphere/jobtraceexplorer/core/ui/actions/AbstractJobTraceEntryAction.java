/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;

public abstract class AbstractJobTraceEntryAction extends Action {

    private Shell shell;
    private TableViewer tableViewer;

    public AbstractJobTraceEntryAction(Shell shell, TableViewer tableViewer) {

        this.shell = shell;
        this.tableViewer = tableViewer;
    }

    public abstract Image getImage();

    protected JobTraceEntry getElementAt(int index) {

        if (isValidIndex(index)) {
            return (JobTraceEntry)getTabelViewer().getElementAt(index);
        }

        return null;
    }

    protected int getSelectionIndex() {
        return getTable().getSelectionIndex();
    }

    protected void setPositionTo(int index) {

        if (isValidIndex(index)) {
            getTabelViewer().setSelection(new StructuredSelection(getElementAt(index)));
            getTabelViewer().getTable().setTopIndex(index);
        }
    }

    protected boolean isValidIndex(int index) {

        if (index >= 0 && index < getItemCount()) {
            return true;
        }

        return false;
    }

    protected int getItemCount() {
        return getTable().getItemCount();
    }

    private Table getTable() {
        return tableViewer.getTable();
    }

    private TableViewer getTabelViewer() {
        return tableViewer;
    }

    protected Shell getShell() {
        return shell;
    }
}
