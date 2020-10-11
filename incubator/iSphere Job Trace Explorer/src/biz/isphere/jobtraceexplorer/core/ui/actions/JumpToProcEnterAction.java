/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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

import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;

public class JumpToProcEnterAction extends Action {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_JUMP_PROC_ENTER;

    private Shell shell;
    private TableViewer tableViewer;
    private JobTraceEntry selectedItem;

    public JumpToProcEnterAction(Shell shell) {
        super(Messages.MenuItem_Jump_to_proc_enter);

        this.shell = shell;

        setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    public void setTableViewer(TableViewer tableViewer) {
        this.tableViewer = tableViewer;
    }

    public void setSelectedItems(StructuredSelection selection) {
        this.selectedItem = (JobTraceEntry)selection.getFirstElement();
    }

    @Override
    public void run() {
        performJumpToProcEnter();
    }

    private void performJumpToProcEnter() {

        int index = tableViewer.getTable().getSelectionIndex();
        JobTraceEntry selectedItem = getElementAt(index);
        int callLevel = selectedItem.getCallLevel();

        index--;

        while (index >= 0 && callLevel != getElementAt(index).getCallLevel()) {
            index--;
        }

        if (index >= 0 && index < tableViewer.getTable().getItemCount()) {
            tableViewer.setSelection(new StructuredSelection(getElementAt(index)));
            tableViewer.getTable().setTopIndex(index);
        }
    }

    private JobTraceEntry getElementAt(int index) {

        JobTraceEntry jobTraceEntry = (JobTraceEntry)tableViewer.getElementAt(index);

        return jobTraceEntry;
    }
}
