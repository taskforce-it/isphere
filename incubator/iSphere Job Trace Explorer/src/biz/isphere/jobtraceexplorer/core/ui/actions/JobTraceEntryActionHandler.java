/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.jobtraceexplorer.core.model.HighlightedAttribute;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntries;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceViewerFactory;
import biz.isphere.jobtraceexplorer.core.ui.model.MouseCursorLocation;

/**
 * This class is a handler for Job Trace Entry actions.
 */
public class JobTraceEntryActionHandler {

    private Shell shell;
    private TableViewer tableViewer;

    public JobTraceEntryActionHandler(Shell shell, TableViewer tableViewer) {

        this.shell = shell;
        this.tableViewer = tableViewer;
    }

    /**
     * Jumps from a selected procedure <b>EXIT</b> entry to the associated
     * procedure <b>ENTRY</b> entry.
     * <p>
     * 
     * <pre>
     * From *PRCENTRY -- jump to --> *PRCEXIT
     * </pre>
     * 
     * @see ColumnsDAO#EVENT_SUB_TYPE_PRCENTRY
     * @see ColumnsDAO#EVENT_SUB_TYPE_PRCEXIT
     */
    public void handleJumpToProcEntry() {

        final int index = getSelectionIndexUI();
        if (!isValidIndexUI(index)) {
            return;
        }

        final int itemCount = getItemCountUI();

        new Job("handleJumpToProcEntry") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                final int indexPosTo = findProcEntry(index, itemCount, monitor);

                if (isValidIndex(indexPosTo, itemCount)) {
                    new UIJob(getShell().getDisplay(), "handleJumpToProcEntryUI") {

                        @Override
                        public IStatus runInUIThread(IProgressMonitor arg0) {

                            setPositionToUI(indexPosTo);

                            return Status.OK_STATUS;
                        }
                    }.schedule();
                }

                return Status.OK_STATUS;
            }
        }.schedule();
    }

    /**
     * Jumps from a selected procedure <b>ENTRY</b> entry to the associated
     * procedure <b>EXIT</b> entry.
     * <p>
     * 
     * <pre>
     * From *PRCENTRY -- jump to --> *PRCEXIT
     * </pre>
     * 
     * @see ColumnsDAO#EVENT_SUB_TYPE_PRCENTRY
     * @see ColumnsDAO#EVENT_SUB_TYPE_PRCEXIT
     */
    public void handleJumpToProcExit() {

        final int index = getSelectionIndexUI();
        if (!isValidIndexUI(index)) {
            return;
        }

        final int itemCount = getItemCountUI();

        new Job("handleJumpToProcExit") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {

                final int indexPosTo = findProcExit(index, itemCount, monitor);

                if (isValidIndex(indexPosTo, itemCount)) {
                    new UIJob(getShell().getDisplay(), "handleJumpToProcExitUI") {

                        @Override
                        public IStatus runInUIThread(IProgressMonitor arg0) {

                            setPositionToUI(indexPosTo);

                            return Status.OK_STATUS;
                        }
                    }.schedule();
                }

                return Status.OK_STATUS;
            }
        }.schedule();
    }

    /**
     * Highlights the selected procedure, starting at the procedure <b>ENTRY</b>
     * entry until the procedure <b>EXIT</b> entry.
     */
    public void handleHighlightProc() {

        int startIndex = getSelectionIndexUI();
        if (!isValidIndexUI(startIndex)) {
            return;
        }

        int endIndex = -1;
        JobTraceEntry jobTraceEntry = getElementAtUI(startIndex);
        if (jobTraceEntry.isProcEntry()) {
            endIndex = findProcExit(startIndex, getItemCountUI(), null);
        } else if (jobTraceEntry.isProcExit()) {
            endIndex = findProcEntry(startIndex, getItemCountUI(), null);
        } else {
            return;
        }

        int index = Math.min(startIndex, endIndex);
        endIndex = Math.max(startIndex, endIndex);

        boolean highlighted = !jobTraceEntry.isHighlighted();

        List<JobTraceEntry> items = new ArrayList<JobTraceEntry>();
        if (isValidIndexUI(index) && isValidIndexUI(endIndex)) {
            while (index <= endIndex) {
                jobTraceEntry = getElementAtUI(index);
                jobTraceEntry.setHighlighted(highlighted);
                items.add(jobTraceEntry);
                index++;
            }
        }

        updateElementsUI(items.toArray(new JobTraceEntry[items.size()]));
    }

    /**
     * Highlights the selected row attribute.
     */
    public void handleHighlightRowAttribute() {

        int startIndex = getSelectionIndexUI();
        if (!isValidIndexUI(startIndex)) {
            return;
        }

        MouseCursorLocation mouseCursorLocation = JobTraceViewerFactory.getMouseCursorLocation(tableViewer);
        if (mouseCursorLocation != null) {
            JobTraceEntry jobTraceEntry = mouseCursorLocation.getJobTraceEntry();
            JobTraceEntries parent = jobTraceEntry.getParent();
            int index = mouseCursorLocation.getColumnIndexUI();
            String value = mouseCursorLocation.getJobTraceEntry().getValueForUi(index);
            if (!parent.isHighlighted(mouseCursorLocation.getColumnIndexUI(), value)) {
                parent.addHighlightedAttribute(new HighlightedAttribute(index, value));
            } else {
                parent.removeHighlightedAttribute(new HighlightedAttribute(index, value));
            }

            List<JobTraceEntry> items = jobTraceEntry.getParent().getItems();
            updateElementsUI(items.toArray(new JobTraceEntry[items.size()]));
        }
    }

    // //////////////////////////////////////////////////////////
    // / Private procedures
    // //////////////////////////////////////////////////////////

    private int findProcExit(int index, int itemCount, IProgressMonitor monitor) {

        try {

            beginTaskChecked(monitor, "findProcExit", itemCount);

            int callLevel = getElementAt(index, itemCount).getCallLevel();

            index++;

            while (isValidIndex(index, itemCount) && callLevel != getElementAt(index, itemCount).getCallLevel()) {
                monitorWorkedChecked(monitor, 1);
                index++;
            }

        } finally {
            monitorDoneChecked(monitor);
        }

        return index;
    }

    private int findProcEntry(int index, int itemCount, IProgressMonitor monitor) {

        try {

            beginTaskChecked(monitor, "findProcExit", itemCount);

            int callLevel = getElementAt(index, itemCount).getCallLevel();

            index--;

            while (isValidIndex(index, itemCount) && callLevel != getElementAt(index, itemCount).getCallLevel()) {
                monitorWorkedChecked(monitor, 1);
                index--;
            }

        } finally {
            monitorDoneChecked(monitor);
        }

        return index;
    }

    private void beginTaskChecked(IProgressMonitor monitor, String taskName, int maxValue) {
        if (monitor != null) {
            monitor.beginTask(taskName, maxValue);
        }
    }

    private void monitorWorkedChecked(IProgressMonitor monitor, int worked) {
        if (monitor != null) {
            monitor.worked(worked);
        }
    }

    private void monitorDoneChecked(IProgressMonitor monitor) {
        if (monitor != null) {
            monitor.done();
        }
    }

    private JobTraceEntry getElementAt(int index, int itemCount) {

        JobTraceEntries entries = (JobTraceEntries)tableViewer.getInput();

        if (isValidIndex(index, itemCount)) {
            return entries.getItem(index);
        }

        return null;
    }

    private boolean isValidIndex(int index, int itemCount) {

        if (index >= 0 && index < itemCount) {
            return true;
        }

        return false;
    }

    private int getItemCountUI() {
        return getTable().getItemCount();
    }

    private int getSelectionIndexUI() {
        return getTable().getSelectionIndex();
    }

    private JobTraceEntry getElementAtUI(int index) {
        return getElementAt(index, getItemCountUI());
    }

    private void updateElementsUI(JobTraceEntry... jobtraceEntry) {
        getTabelViewer().update(jobtraceEntry, null);
    }

    private void setPositionToUI(int index) {

        if (isValidIndexUI(index)) {
            getTabelViewer().setSelection(new StructuredSelection(getElementAtUI(index)));
            getTabelViewer().getTable().setTopIndex(index);
        }
    }

    private boolean isValidIndexUI(int index) {
        return isValidIndex(index, getItemCountUI());
    }

    private Table getTable() {
        return tableViewer.getTable();
    }

    private TableViewer getTabelViewer() {
        return tableViewer;
    }

    private Shell getShell() {
        return shell;
    }
}
