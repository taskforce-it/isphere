/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.joblogexplorer.InvalidJobLogFormatException;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.detailsviewer.JobLogExplorerDetailsViewer;
import biz.isphere.joblogexplorer.editor.filter.JobLogExplorerFilterPanel;
import biz.isphere.joblogexplorer.editor.tableviewer.JobLogExplorerTableViewer;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.AbstractStringFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromLibraryFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromProgramFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromStatementFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.IdFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.SeverityFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.TypeFilter;
import biz.isphere.joblogexplorer.jobs.IDropFileListener;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogReader;

public class JobLogExplorerEditor extends EditorPart implements IDropFileListener, IJobLogExplorerStatusChangedListener {

    public static final String ID = "biz.isphere.joblogexplorer.editor.JobLogExplorerEditor"; //$NON-NLS-1$

    private StatusLine statusLine;
    private StatusLineData statusLineData;

    private JobLogExplorerTableViewer tableViewerPanel;
    private JobLogExplorerFilterPanel filterPanel;

    public JobLogExplorerEditor() {
    }

    @Override
    public void doSave(IProgressMonitor monitor) {

        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(Messages.Job_Log_Explorer);
        setTitleToolTip(Messages.Job_Log_Explorer_Tooltip);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(createGridLayoutNoMargin());
        disableDropSupportOnComposite(mainArea);

        JobLogExplorerFilterPanel filterPanel = createTopPanel(mainArea);

        SashForm sashForm = new SashForm(mainArea, SWT.NONE);
        GridData sashFormLayoutData = new GridData(GridData.FILL_BOTH);
        sashForm.setLayoutData(sashFormLayoutData);

        tableViewerPanel = createLeftPanel(sashForm);
        JobLogExplorerDetailsViewer detailsPanel = createRightPanel(sashForm);
        sashForm.setWeights(new int[] { 8, 4 });

        tableViewerPanel.addMessageSelectionChangedListener(detailsPanel);
        tableViewerPanel.addStatusChangedListener(this);
        filterPanel.addFilterChangedListener(tableViewerPanel);

        JobLogExplorerEditorInput input = (JobLogExplorerEditorInput)getEditorInput();

        if (input.getPath() != null) {
            dropJobLog(input.getPath(), input.getOriginalFileName(), null);
        } else {
            // Open empty editor to drag & drop files.
        }

        getSite().setSelectionProvider(tableViewerPanel);
    }

    @Override
    public void setFocus() {
    }

    /*
     * Drag & drop support
     */

    public void dropJobLog(String pathName, String originalFileName, Object target) {

        if (pathName == null) {
            return;
        }

        if (!tableViewerPanel.isDisposed()) {
            tableViewerPanel.setInputData(null);
        }

        tableViewerPanel.setEnabled(false);
        JobLogExplorerEditor.this.showBusy(true);

        ParseSpooledFileJob parserJob = new ParseSpooledFileJob(pathName, originalFileName, tableViewerPanel);
        parserJob.schedule();
    }

    private void addDropSupportOnComposite(Composite dialogEditorComposite) {

        Transfer[] transferTypes = new Transfer[] { PluginTransfer.getInstance() };
        int operations = DND.DROP_MOVE | DND.DROP_COPY;
        DropTargetListener listener = createEditorDropListener(this);

        DropTarget dropTarget = new DropTarget(dialogEditorComposite, operations);
        dropTarget.setTransfer(transferTypes);
        dropTarget.addDropListener(listener);
        dropTarget.setData(this);
    }

    private void disableDropSupportOnComposite(Composite dialogEditorComposite) {

        Transfer[] transferTypes = new Transfer[] {};
        int operations = DND.DROP_NONE;
        DropTargetListener listener = new DropVetoListerner();

        DropTarget dropTarget = new DropTarget(dialogEditorComposite, operations);
        dropTarget.setTransfer(transferTypes);
        dropTarget.addDropListener(listener);
    }

    protected DropTargetAdapter createEditorDropListener(IDropFileListener editor) {
        return new DropFileListener(editor);
    }

    private JobLogExplorerFilterPanel createTopPanel(Composite parent) {

        filterPanel = new JobLogExplorerFilterPanel();
        filterPanel.createViewer(parent);

        return filterPanel;
    }

    private JobLogExplorerTableViewer createLeftPanel(SashForm sashForm) {

        Composite leftMainPanel = new Composite(sashForm, SWT.NONE);
        leftMainPanel.setLayout(createGridLayoutWithMargin());
        leftMainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        //        new Label(leftMainPanel, SWT.NONE).setText("Messages"); //$NON-NLS-1$

        // Label separator = new Label(leftMainPanel, SWT.SEPARATOR |
        // SWT.HORIZONTAL);
        // separator.setLayoutData(createGridDataFillAndGrab(1));

        JobLogExplorerTableViewer tableViewer = new JobLogExplorerTableViewer();
        tableViewer.createViewer(leftMainPanel);

        addDropSupportOnComposite(leftMainPanel);

        return tableViewer;
    }

    private JobLogExplorerDetailsViewer createRightPanel(SashForm sashForm) {

        Composite rightMainPanel = new Composite(sashForm, SWT.NONE);
        rightMainPanel.setLayout(createGridLayoutWithMargin());
        rightMainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        //        new Label(rightMainPanel, SWT.NONE).setText("Message details"); //$NON-NLS-1$

        // Label separator = new Label(rightMainPanel, SWT.SEPARATOR |
        // SWT.HORIZONTAL);
        // separator.setLayoutData(createGridDataFillAndGrab(1));

        Composite alignmentHelperPanel = new Composite(rightMainPanel, SWT.NONE);
        alignmentHelperPanel.setLayout(new GridLayout(1, false));
        alignmentHelperPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        JobLogExplorerDetailsViewer details = new JobLogExplorerDetailsViewer();
        details.createViewer(alignmentHelperPanel);

        return details;
    }

    protected Shell getShell() {
        return this.getSite().getShell();
    }

    private GridLayout createGridLayoutNoMargin() {
        GridLayout editorLayout = new GridLayout(1, false);
        editorLayout.marginHeight = 0;
        editorLayout.marginWidth = 0;
        return editorLayout;
    }

    private GridLayout createGridLayoutWithMargin() {
        GridLayout treeAreaLayout = new GridLayout(1, false);
        return treeAreaLayout;
    }

    private class ParseSpooledFileJob extends Job {

        private String pathName;
        private String originalFileName;
        private JobLogExplorerTableViewer viewer;

        public ParseSpooledFileJob(String pathName, String originalFileName, JobLogExplorerTableViewer viewer) {
            super(Messages.Job_Parsing_job_log);

            this.pathName = pathName;
            this.originalFileName = originalFileName;
            this.viewer = viewer;
        }

        @Override
        protected IStatus run(IProgressMonitor arg0) {

            try {

                JobLogReader reader = new JobLogReader();
                final JobLog jobLog = reader.loadFromStmf(pathName);

                new UIJob("") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor arg0) {

                        setPartName(originalFileName);
                        viewer.setInputData(jobLog);

                        int count;
                        if (jobLog != null) {
                            count = jobLog.getMessages().size();
                        } else {
                            count = 0;
                        }

                        // statusChanged(new StatusLineData(count));

                        viewer.setEnabled(true);
                        JobLogExplorerEditor.this.showBusy(false);

                        if (!filterPanel.isDisposed()) {

                            filterPanel.setIdFilterItems(addSpecialTypes(jobLog.getMessageIds(), IdFilter.UI_SPCVAL_ALL));
                            filterPanel.setTypeFilterItems(addSpecialTypes(jobLog.getMessageTypes(), TypeFilter.UI_SPCVAL_ALL));
                            filterPanel.setSeverityFilterItems(addSpecialTypes(jobLog.getMessageSeverities(), SeverityFilter.UI_SPCVAL_ALL,
                                AbstractStringFilter.UI_SPCVAL_BLANK));

                            filterPanel.setFromLibraryFilterItems(addSpecialTypes(jobLog.getMessageFromLibraries(), FromLibraryFilter.UI_SPCVAL_ALL));
                            filterPanel.setFromProgramFilterItems(addSpecialTypes(jobLog.getMessageFromPrograms(), FromProgramFilter.UI_SPCVAL_ALL));
                            filterPanel.setFromStmtFilterItems(addSpecialTypes(jobLog.getMessageFromStatements(), FromStatementFilter.UI_SPCVAL_ALL));

                            filterPanel.setToLibraryFilterItems(addSpecialTypes(jobLog.getMessageToLibraries(), TypeFilter.UI_SPCVAL_ALL));
                            filterPanel.setToProgramFilterItems(addSpecialTypes(jobLog.getMessageToPrograms(), TypeFilter.UI_SPCVAL_ALL));
                            filterPanel.setToStmtFilterItems(addSpecialTypes(jobLog.getMessageToStatements(), TypeFilter.UI_SPCVAL_ALL));

                            filterPanel.clearFilters();
                        }

                        viewer.setSelection(0);
                        viewer.setFocus();

                        return Status.OK_STATUS;
                    }

                }.schedule();

            } catch (InvalidJobLogFormatException e) {
                MessageDialogAsync.displayError(getShell(), Messages.Invalid_job_log_Format_Could_not_find_first_line_of_job_log);
            }

            return Status.OK_STATUS;
        }

        protected String[] addSpecialTypes(String[] messageTypes, String... spcval) {

            if (spcval == null || spcval.length == 0) {
                return messageTypes;
            }

            List<String> items = new ArrayList<String>();

            for (String value : spcval) {
                items.add(value);
            }

            Arrays.sort(messageTypes);
            for (String value : messageTypes) {
                items.add(value);
            }

            return items.toArray(new String[items.size()]);
        }
    }

    // private void showStatusMessage(String message) {
    // statusLineData.setMessage(message);
    // updateActionsStatusAndStatusLine();
    // }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    /**
     * Used by the JobLogExplorerEditorActionBarContributor to update the status
     * line.
     */
    public void updateActionsStatusAndStatusLine() {

        statusLine.setData(statusLineData);
    }

    public void statusChanged(StatusLineData status) {

        statusLineData = status;

        if (statusLine != null) {
            statusLine.setData(statusLineData);
        }
    }
}
