/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IResizableTableColumnsViewer;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dialog.ConfirmErrorsDialog;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.sqleditor.SQLSyntaxErrorException;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.AbstractJobLogExplorerInput;
import biz.isphere.joblogexplorer.editor.IJobLogExplorerStatusChangedListener;
import biz.isphere.joblogexplorer.editor.JobLogExplorerStatusChangedEvent;
import biz.isphere.joblogexplorer.editor.detailsviewer.JobLogExplorerDetailsViewer;
import biz.isphere.joblogexplorer.editor.filter.FilterData;
import biz.isphere.joblogexplorer.editor.filter.JobLogExplorerFilterPanel;
import biz.isphere.joblogexplorer.editor.filter.JobLogExplorerFilterPanelEvents;
import biz.isphere.joblogexplorer.editor.tableviewer.JobLogExplorerTableViewer;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.AbstractStringFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromLibraryFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromProgramFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.FromStatementFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.IdFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.SeverityFilter;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.TypeFilter;
import biz.isphere.joblogexplorer.exceptions.DownloadSpooledFileException;
import biz.isphere.joblogexplorer.exceptions.InvalidJobLogFormatException;
import biz.isphere.joblogexplorer.exceptions.JobLogNotLoadedException;
import biz.isphere.joblogexplorer.exceptions.JobNotFoundException;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogMessage;

public class JobLogExplorerTab extends CTabItem implements IResizableTableColumnsViewer, IJobLogExplorerStatusChangedListener {

    private static final String EMPTY = ""; //$NON-NLS-1$

    private final static String SASH_WEIGHTS = "SASH_WEIGHTS_"; //$NON-NLS-1$

    private CTabFolder tabFolder;
    private SelectionListener sqlEditorSelectionListener;

    private Composite container;
    private JobLogExplorerTableViewer tableViewerPanel;
    private JobLogExplorerFilterPanel filterPanel;
    private SashForm sashForm;
    private JobLogExplorerDetailsViewer detailsPanel;

    private List<IJobLogExplorerStatusChangedListener> statusChangedListeners;

    private AbstractJobLogExplorerInput jobLogExplorerInput;

    private DialogSettingsManager dialogSettingsManager = null;
    private boolean isSqlEditorVisible;
    private SqlEditor sqlEditor;
    private String filterClause;

    public JobLogExplorerTab(CTabFolder parent, SelectionListener sqlEditorSelectionListener) {
        super(parent, SWT.NONE);
        initializeComponents(parent, sqlEditorSelectionListener);
    }

    public JobLogExplorerTab(CTabFolder parent, SelectionListener sqlEditorSelectionListener, int index) {
        super(parent, SWT.NONE, index);
        initializeComponents(parent, sqlEditorSelectionListener);
    }

    public void setEnabled(boolean enabled) {
        tableViewerPanel.setEnabled(enabled);
        if (isSqlEditorVisible()) {
            filterPanel.setEnabled(false);
            sqlEditor.setEnabled(enabled);
        } else {
            filterPanel.setEnabled(enabled);
        }
    }

    public void setInput(AbstractJobLogExplorerInput input, boolean isReload) {

        jobLogExplorerInput = input;

        prepareLoadingJobLog(jobLogExplorerInput);

        if (jobLogExplorerInput instanceof AbstractJobLogExplorerInput) {
            LoadJobLogJob job = new LoadJobLogJob(input, tableViewerPanel, filterPanel, isReload);
            job.schedule();
        } else {
            jobLogExplorerInput = null;
            SetEditorInputJob job = new SetEditorInputJob(new JobLog(), tableViewerPanel, filterPanel, isReload);
            job.schedule();
        }

    }

    public void refresh() {
        setInput(getInput(), true);
    }

    public AbstractJobLogExplorerInput getInput() {
        return jobLogExplorerInput;
    }

    public JobLog getJobLog() {
        return tableViewerPanel.getInputData();
    }

    public boolean isSqlEditorVisible() {
        return isSqlEditorVisible;
    }

    public void setSqlEditorVisibility(boolean visible) {
        this.isSqlEditorVisible = visible;
        setSqlEditorEnablement();
    }

    private void setSqlEditorEnablement() {

        if (isSqlEditorVisible()) {
            createSqlEditor();
            filterPanel.setEnabled(false);
        } else {
            destroySqlEditor();
            filterPanel.setEnabled(true);
        }
    }

    private boolean isAvailable(Control control) {

        if (control != null && !control.isDisposed()) {
            return true;
        }

        return false;
    }

    private void prepareLoadingJobLog(AbstractJobLogExplorerInput editorInput) {

        if (editorInput == null) {
            setText(EMPTY);
            setToolTipText(EMPTY);
            return;
        } else {
            setText(editorInput.getName());
            setToolTipText(editorInput.getToolTipText());
        }

        JobLogExplorerStatusChangedEvent status = new JobLogExplorerStatusChangedEvent(
            JobLogExplorerStatusChangedEvent.EventType.STARTED_DATA_LOADING, this);
        notifyStatusChangedListeners(status);
    }

    public JobLogMessage[] getItems() {
        return tableViewerPanel.getItems();
    }

    public int getItemsCount() {
        return tableViewerPanel.getNumberOfDisplayedMessages();
    }

    public int getTotalItemsCount() {
        return tableViewerPanel.getTotalNumberOfMessages();
    }

    public void resetColumnWidths() {
        tableViewerPanel.resetColumnSize();
    }

    private void initializeComponents(CTabFolder parent, SelectionListener sqlEditorSelectionListener) {

        this.tabFolder = parent;
        this.sqlEditorSelectionListener = sqlEditorSelectionListener;
        this.statusChangedListeners = new ArrayList<IJobLogExplorerStatusChangedListener>();

        this.container = new Composite(parent, SWT.NONE);
        container.setLayout(createGridLayoutNoMargin());
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createFilterPanel(container);
        createMainPanel(container);

        filterPanel.addFilterChangedListener(tableViewerPanel);

        container.layout(true);
        setControl(container);

    }

    private void createFilterPanel(Composite parent) {

        if (!isAvailable(filterPanel)) {
            filterPanel = new JobLogExplorerFilterPanel(parent, SWT.NONE);
            filterPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            setFilterPanelOptions();
            parent.layout(true);
        }
    }

    private void destroyFilterPanel() {

        if (isAvailable(filterPanel)) {
            filterPanel.dispose();
            container.layout(true);
        }
    }

    private void createMainPanel(Composite parent) {

        sashForm = new SashForm(container, SWT.NONE);
        GridData sashFormLayoutData = new GridData(GridData.FILL_BOTH);
        sashForm.setLayoutData(sashFormLayoutData);

        tableViewerPanel = createLeftPanel(sashForm);
        detailsPanel = createRightPanel(sashForm);
        sashForm.setWeights(loadWeights());

        tableViewerPanel.addMessageSelectionChangedListener(detailsPanel);
        tableViewerPanel.addStatusChangedListener(this);
    }

    private void createSqlEditor() {

        if (!isAvailable(sqlEditor)) {
            sqlEditor = WidgetFactory.createSqlEditor(container, getClass().getSimpleName(), getDialogSettingsManager());
            sqlEditor.setContentAssistProposals(getContentAssistProposals());
            sqlEditor.setWhereClause(getFilterClause());
            GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.heightHint = 120;
            sqlEditor.setLayoutData(gd);
            sqlEditor.setFocus();
            sqlEditor.setBtnExecuteLabel(Messages.Label_Filter);
            sqlEditor.setBtnExecuteToolTipText(Messages.Label_Filter_tooltip);
            sqlEditor.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    setFilterClause(sqlEditor.getWhereClause().trim());
                }
            });
            sqlEditor.addSelectionListener(sqlEditorSelectionListener);
        }

        container.layout(true);
    }

    private void destroySqlEditor() {

        if (isAvailable(sqlEditor)) {
            // Important, must be called to ensure the SqlEditor is removed from
            // the list of preferences listeners.
            sqlEditor.removeSelectionListener(tableViewerPanel);
            sqlEditor.dispose();
            container.layout(true);
        }
    }

    private ContentAssistProposal[] getContentAssistProposals() {

        List<ContentAssistProposal> proposals = JobLogMessage.getContentAssistProposals();

        return proposals.toArray(new ContentAssistProposal[proposals.size()]);
    }

    private void setFilterClause(String whereClause) {
        this.filterClause = whereClause;
    }

    public String getFilterClause() {
        return filterClause;
    }

    @Override
    public void dispose() {

        int[] weights = sashForm.getWeights();
        storeWeights(weights);

        detailsPanel.dispose();

        super.dispose();
    }

    private JobLogExplorerTableViewer createLeftPanel(SashForm sashForm) {

        Composite leftMainPanel = new Composite(sashForm, SWT.NONE);
        leftMainPanel.setLayout(createGridLayoutWithMargin());
        leftMainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        JobLogExplorerTableViewer tableViewer = new JobLogExplorerTableViewer(this, getDialogSettingsManager());
        tableViewer.createViewer(leftMainPanel);

        return tableViewer;
    }

    private JobLogExplorerDetailsViewer createRightPanel(SashForm sashForm) {

        Composite rightMainPanel = new Composite(sashForm, SWT.NONE);
        rightMainPanel.setLayout(createGridLayoutWithMargin());
        rightMainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite alignmentHelperPanel = new Composite(rightMainPanel, SWT.NONE);
        alignmentHelperPanel.setLayout(createGridLayoutNoMargin());
        alignmentHelperPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        JobLogExplorerDetailsViewer details = new JobLogExplorerDetailsViewer();
        details.createViewer(alignmentHelperPanel);

        return details;
    }

    private GridLayout createGridLayoutNoMargin() {
        GridLayout editorLayout = new GridLayout(1, false);
        editorLayout.marginHeight = 0;
        editorLayout.marginWidth = 0;
        return editorLayout;
    }

    private GridLayout createGridLayoutWithMargin() {
        GridLayout treeAreaLayout = new GridLayout(1, false);
        treeAreaLayout.marginHeight = 2;
        treeAreaLayout.marginWidth = 2;
        return treeAreaLayout;
    }

    private int[] loadWeights() {

        int[] weights = new int[] { 8, 4 };
        for (int i = 0; i < weights.length; i++) {
            weights[i] = getDialogSettingsManager().loadIntValue(SASH_WEIGHTS + i, weights[i]);
        }

        return weights;
    }

    private void storeWeights(int[] weights) {

        for (int i = 0; i < weights.length; i++) {
            getDialogSettingsManager().storeValue(SASH_WEIGHTS + i, weights[i]);
        }
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISphereJobLogExplorerPlugin.getDefault().getDialogSettings(), getClass());
        }
        return dialogSettingsManager;
    }

    public void statusChanged(JobLogExplorerStatusChangedEvent data) {
        JobLogExplorerStatusChangedEvent newData = new JobLogExplorerStatusChangedEvent(data, this);
        notifyStatusChangedListeners(newData);
    }

    public void addStatusChangedListener(IJobLogExplorerStatusChangedListener listener) {

        statusChangedListeners.add(listener);
    }

    public void removeStatusChangedListener(IJobLogExplorerStatusChangedListener listener) {

        statusChangedListeners.remove(listener);
    }

    private void notifyStatusChangedListeners(JobLogExplorerStatusChangedEvent status) {

        for (IJobLogExplorerStatusChangedListener listener : statusChangedListeners) {
            listener.statusChanged(status);
        }
    }

    private void handleDataLoadException(String message, Throwable e) {
        handleDataLoadException(message, e, false, false);
    }

    private void handleDataLoadException(String message, Throwable e, boolean addToErrorLog, boolean isReload) {

        if (message != null) {
            if (addToErrorLog) {
                ISpherePlugin.logError(message, e);
            }
        }

        final JobLogExplorerStatusChangedEvent status = new JobLogExplorerStatusChangedEvent(
            JobLogExplorerStatusChangedEvent.EventType.DATA_LOAD_ERROR, this);
        status.setException(message, e);
        status.setReload(isReload);

        if (Display.getCurrent() == null) {
            UIJob job = new UIJob(EMPTY) {
                @Override
                public IStatus runInUIThread(IProgressMonitor arg0) {
                    notifyStatusChangedListeners(status);
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        } else {
            notifyStatusChangedListeners(status);
        }
    }

    public void storeSqlEditorHistory() {
        sqlEditor.storeHistory();
    }

    public void refreshSqlEditorHistory() {
        if (isSqlEditorVisible) {
            sqlEditor.refreshHistory();
        }
    }

    public void validateWhereClause(Shell shell) throws SQLSyntaxErrorException {

        String whereClause = sqlEditor.getWhereClause();

        if (StringHelper.isNullOrEmpty(whereClause)) {
            return;
        }

        try {

            HashMap<String, Integer> columnMapping = JobLogMessage.getColumnMapping();
            RowJEP sqljep = new RowJEP(whereClause);
            sqljep.parseExpression(columnMapping);
            sqljep.getValue(JobLogMessage.getSampleRow());

        } catch (ParseException e) {
            throw new SQLSyntaxErrorException(e);
        }

    }

    public void setFocusOnSqlEditor() {

        if (isSqlEditorVisible()) {
            sqlEditor.setFocus();
        }
    }

    public void filterJobLogMessages() {

        boolean enabled = sqlEditor.getEnabled();

        try {

            sqlEditor.setEnabled(true);

            String whereClause = sqlEditor.getWhereClause();

            FilterData filterData = new FilterData();
            filterData.whereClause = whereClause;
            Event event = new Event();
            event.widget = this;
            event.detail = JobLogExplorerFilterPanelEvents.APPLY_FILTERS;
            event.data = filterData;

            SelectionEvent selectionEvent = new SelectionEvent(event);
            tableViewerPanel.widgetSelected(selectionEvent);

        } finally {
            sqlEditor.setEnabled(enabled);
        }
    }

    private void setFilterPanelOptions() {

        if (tableViewerPanel == null) {
            return;
        }

        JobLog jobLog = tableViewerPanel.getInputData();
        if (jobLog == null) {
            return;
        }

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

    private class LoadJobLogJob extends Job {

        private AbstractJobLogExplorerInput input;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;
        private boolean isReload;

        public LoadJobLogJob(AbstractJobLogExplorerInput jobInput, JobLogExplorerTableViewer viewer, JobLogExplorerFilterPanel filterPanel,
            boolean isReload) {
            super(Messages.Job_Loading_job_log);

            this.input = jobInput;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
            this.isReload = isReload;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                JobLog jobLog = input.load(monitor);
                new SetEditorInputJob(jobLog, viewer, filterPanel, isReload).schedule();

            } catch (DownloadSpooledFileException e) {
                handleDataLoadException(e.getLocalizedMessage(), e, false, isReload);
            } catch (InvalidJobLogFormatException e) {
                handleDataLoadException(e.getLocalizedMessage(), e, false, isReload);
            } catch (JobNotFoundException e) {
                handleDataLoadException(e.getLocalizedMessage(), e, false, isReload);
            } catch (JobLogNotLoadedException e) {
                handleDataLoadException(e.getLocalizedMessage(), e, false, isReload);
            } catch (Throwable e) {
                handleDataLoadException("*** Failed to retrieve job log ***", e, true, isReload); //$NON-NLS-1$
            }

            return Status.OK_STATUS;
        }

    }

    private class SetEditorInputJob extends UIJob {

        private JobLog jobLog;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;
        private boolean isReload;

        public SetEditorInputJob(JobLog jobLog, JobLogExplorerTableViewer viewer, JobLogExplorerFilterPanel filterPanel, boolean isReload) {
            super(EMPTY);

            this.jobLog = jobLog;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
            this.isReload = isReload;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {

            try {

                if (JobLogExplorerTab.this.isDisposed()) {
                    handleDataLoadException(null, null);
                    return Status.OK_STATUS;
                }

                viewer.setInputData(jobLog);

                if (isAvailable(filterPanel)) {
                    setFilterPanelOptions();
                }

                if (viewer.getItemCount() != 0) {
                    viewer.setSelection(0);
                } else {
                    viewer.setSelection(-1);
                }
                viewer.setFocus();

                if (jobLog.getErrorCount() > 0) {
                    ConfirmErrorsDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.E_R_R_O_R,
                        Messages.Job_Log_Explorer, jobLog.getErrors());
                }

            } catch (Throwable e) {
                handleDataLoadException("*** Failed to set job log data ***", e, true, isReload); //$NON-NLS-1$
            }

            JobLogExplorerStatusChangedEvent status = new JobLogExplorerStatusChangedEvent(
                JobLogExplorerStatusChangedEvent.EventType.FINISHED_DATA_LOADING, JobLogExplorerTab.this);
            notifyStatusChangedListeners(status);

            return Status.OK_STATUS;
        }

    }

}
