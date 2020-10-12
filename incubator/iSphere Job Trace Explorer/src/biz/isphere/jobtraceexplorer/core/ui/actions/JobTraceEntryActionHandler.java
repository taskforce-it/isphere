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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.dao.ColumnsDAO;

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
     * procedure <b>ENTER</b> entry.
     * <p>
     * 
     * <pre>
     * From *PRCENTRY -- jump to --> *PRCEXIT
     * </pre>
     * 
     * @see ColumnsDAO#EVENT_SUB_TYPE_PRCENTRY
     * @see ColumnsDAO#EVENT_SUB_TYPE_PRCEXIT
     */
    public void handleJumpToProcEnter() {

        int index = getSelectionIndex();
        if (!isValidIndex(index)) {
            return;
        }

        index = findProcEnterEntry(index);

        if (isValidIndex(index)) {
            setPositionTo(index);
        }
    }

    /**
     * Jumps from a selected procedure <b>ENTER</b> entry to the associated
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

        int index = getSelectionIndex();
        if (!isValidIndex(index)) {
            return;
        }

        index = findProcExitEntry(index);

        if (isValidIndex(index)) {
            setPositionTo(index);
        }
    }

    public void handleHighlightProc() {

        int startIndex = getSelectionIndex();
        if (!isValidIndex(startIndex)) {
            return;
        }

        int endIndex = -1;
        JobTraceEntry jobTraceEntry = getElementAt(startIndex);
        if (jobTraceEntry.isProcEnter()) {
            endIndex = findProcExitEntry(startIndex);
        } else if (jobTraceEntry.isProcExit()) {
            endIndex = findProcEnterEntry(startIndex);
        } else {
            return;
        }

        int index = Math.min(startIndex, endIndex);
        endIndex = Math.max(startIndex, endIndex);

        boolean highlighted = !jobTraceEntry.isHighlighted();

        List<JobTraceEntry> items = new ArrayList<JobTraceEntry>();
        if (isValidIndex(index) && isValidIndex(endIndex)) {
            while (index <= endIndex) {
                jobTraceEntry = getElementAt(index);
                jobTraceEntry.setHighlighted(highlighted);
                items.add(jobTraceEntry);
                index++;
            }
        }

        updateElements(items.toArray(new JobTraceEntry[items.size()]));

    }

    // //////////////////////////////////////////////////////////
    // / Private procedures
    // //////////////////////////////////////////////////////////

    private int findProcEnterEntry(int index) {

        int callLevel = getElementAt(index).getCallLevel();

        index--;

        while (isValidIndex(index) && callLevel != getElementAt(index).getCallLevel()) {
            index--;
        }

        return index;
    }

    private int findProcExitEntry(int index) {

        int callLevel = getElementAt(index).getCallLevel();

        index++;

        while (isValidIndex(index) && callLevel != getElementAt(index).getCallLevel()) {
            index++;
        }

        return index;
    }

    private int getItemCount() {
        return getTable().getItemCount();
    }

    private int getSelectionIndex() {
        return getTable().getSelectionIndex();
    }

    private JobTraceEntry getElementAt(int index) {

        if (isValidIndex(index)) {
            return (JobTraceEntry)getTabelViewer().getElementAt(index);
        }

        return null;
    }

    private void updateElements(JobTraceEntry... jobtraceEntry) {
        getTabelViewer().update(jobtraceEntry, null);
    }

    private void setPositionTo(int index) {

        if (isValidIndex(index)) {
            getTabelViewer().setSelection(new StructuredSelection(getElementAt(index)));
            getTabelViewer().getTable().setTopIndex(index);
        }
    }

    private boolean isValidIndex(int index) {

        if (index >= 0 && index < getItemCount()) {
            return true;
        }

        return false;
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
