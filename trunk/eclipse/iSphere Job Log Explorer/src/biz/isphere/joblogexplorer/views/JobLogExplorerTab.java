/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
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

import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.progress.UIJob;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.sqleditor.SQLSyntaxErrorException;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.AbstractJobLogExplorerInput;
import biz.isphere.joblogexplorer.editor.IJobLogExplorerStatusChangedListener;
import biz.isphere.joblogexplorer.editor.JobLogExplorerFileInput;
import biz.isphere.joblogexplorer.editor.JobLogExplorerJobInput;
import biz.isphere.joblogexplorer.editor.JobLogExplorerSpooledFileInput;
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
import biz.isphere.joblogexplorer.exceptions.InvalidJobLogFormatException;
import biz.isphere.joblogexplorer.exceptions.JobLogNotLoadedException;
import biz.isphere.joblogexplorer.exceptions.JobNotFoundException;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.model.JobLogParser;
import biz.isphere.joblogexplorer.model.JobLogReader;

import com.ibm.as400.access.AS400;

public class JobLogExplorerTab extends CTabItem implements IJobLogExplorerStatusChangedListener {

    private static final String EMPTY = ""; //$NON-NLS-1$

    private final static String SASH_WEIGHTS = "SASH_WEIGHTS_"; //$NON-NLS-1$

    private Composite container;
    private JobLogExplorerTableViewer tableViewerPanel;
    private JobLogExplorerFilterPanel filterPanel;
    private SashForm sashForm;
    private JobLogExplorerDetailsViewer detailsPanel;

    SelectionListener sqlEditorSelectionListener;

    private List<IJobLogExplorerStatusChangedListener> statusChangedListeners;

    private AbstractJobLogExplorerInput jobLogExplorerInput;

    private DialogSettingsManager dialogSettingsManager = null;
    private boolean isSqlEditorVisible;
    private SqlEditor sqlEditor;
    private String filterClause;

    public JobLogExplorerTab(CTabFolder parent, SelectionListener sqlEditorSelectionListener) {
        super(parent, SWT.NONE);

        this.sqlEditorSelectionListener = sqlEditorSelectionListener;

        this.statusChangedListeners = new ArrayList<IJobLogExplorerStatusChangedListener>();

        initializeComponents(parent);
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

    public void setInput(AbstractJobLogExplorerInput input) {

        jobLogExplorerInput = input;

        prepareLoadingJobLog(jobLogExplorerInput);

        if (jobLogExplorerInput instanceof JobLogExplorerFileInput) {
            JobLogExplorerFileInput fileInput = (JobLogExplorerFileInput)jobLogExplorerInput;
            ParseSpooledFileJob job = new ParseSpooledFileJob(fileInput, tableViewerPanel, filterPanel);
            job.schedule();
        } else if (jobLogExplorerInput instanceof JobLogExplorerJobInput) {
            JobLogExplorerJobInput jobInput = (JobLogExplorerJobInput)jobLogExplorerInput;
            LoadJobLogJob job = new LoadJobLogJob(jobInput, tableViewerPanel, filterPanel);
            job.schedule();
        } else if (jobLogExplorerInput instanceof JobLogExplorerSpooledFileInput) {
            JobLogExplorerSpooledFileInput spooledFileInput = (JobLogExplorerSpooledFileInput)jobLogExplorerInput;
            LoadSpooledFileJob job = new LoadSpooledFileJob(spooledFileInput, tableViewerPanel, filterPanel);
            job.schedule();
        } else {
            jobLogExplorerInput = null;
            SetEditorInputJob job = new SetEditorInputJob(new JobLog(), tableViewerPanel, filterPanel);
            job.schedule();
        }

    }

    public AbstractJobLogExplorerInput getInput() {
        return jobLogExplorerInput;
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

    public void resetColumnSize() {
        tableViewerPanel.resetColumnSize();
    }

    private void initializeComponents(CTabFolder parent) {

        this.container = new Composite(parent, SWT.NONE);
        container.setLayout(createGridLayoutNoMargin());
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createFilterPanel();
        createMainPanel(filterPanel);

        filterPanel.addFilterChangedListener(tableViewerPanel);

        container.layout(true);
        setControl(container);

    }

    private void createFilterPanel() {

        if (!isAvailable(filterPanel)) {
            filterPanel = new JobLogExplorerFilterPanel(container, SWT.NONE);
            filterPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            setFilterPanelOptions();
            container.layout(true);
        }
    }

    private void destroyFilterPanel() {

        if (isAvailable(filterPanel)) {
            filterPanel.dispose();
            container.layout(true);
        }
    }

    private void createMainPanel(JobLogExplorerFilterPanel filterPanel) {

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

        JobLogExplorerTableViewer tableViewer = new JobLogExplorerTableViewer(getDialogSettingsManager());
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
        handleDataLoadException(message, e, false);
    }

    private void handleDataLoadException(String message, Throwable e, boolean addToErrorLog) {

        if (message != null) {
            if (addToErrorLog) {
                ISpherePlugin.logError(message, e);
            }
        }

        final JobLogExplorerStatusChangedEvent status = new JobLogExplorerStatusChangedEvent(
            JobLogExplorerStatusChangedEvent.EventType.DATA_LOAD_ERROR, this);
        status.setException(message, e);

        if (Display.getCurrent() == null) {
            UIJob job = new UIJob("") {
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
        sqlEditor.refreshHistory();
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

        String whereClause = sqlEditor.getWhereClause();

        FilterData filterData = new FilterData();
        filterData.whereClause = whereClause;
        Event event = new Event();
        event.widget = this;
        event.detail = JobLogExplorerFilterPanelEvents.APPLY_FILTERS;
        event.data = filterData;
        SelectionEvent selectionEvent = new SelectionEvent(event);
        tableViewerPanel.widgetSelected(selectionEvent);
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

    private class ParseSpooledFileJob extends Job {

        private JobLogExplorerFileInput fileInput;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;

        public ParseSpooledFileJob(JobLogExplorerFileInput fileInput, JobLogExplorerTableViewer viewer, JobLogExplorerFilterPanel filterPanel) {
            super(Messages.Job_Parsing_job_log);

            this.fileInput = fileInput;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
        }

        @Override
        protected IStatus run(IProgressMonitor arg0) {

            try {

                JobLogParser reader = new JobLogParser();
                final JobLog jobLog = reader.loadFromStmf(fileInput.getPath());

                new SetEditorInputJob(jobLog, viewer, filterPanel).schedule();

            } catch (InvalidJobLogFormatException e) {
                handleDataLoadException(Messages.Invalid_job_log_Format_Could_not_find_first_line_of_job_log, e);
            } catch (Throwable e) {
                handleDataLoadException("*** Failed to parse spooled file ***", e, true);
            }

            return Status.OK_STATUS;
        }
    }

    private class LoadJobLogJob extends Job {

        private JobLogExplorerJobInput jobInput;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;

        public LoadJobLogJob(JobLogExplorerJobInput jobInput, JobLogExplorerTableViewer viewer, JobLogExplorerFilterPanel filterPanel) {
            super(Messages.Job_Loading_job_log);

            this.jobInput = jobInput;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
        }

        @Override
        protected IStatus run(IProgressMonitor arg0) {

            try {

                AS400 as400 = IBMiHostContributionsHandler.getSystem(jobInput.getConnectionName());

                JobLogReader reader = new JobLogReader();
                final JobLog jobLog = reader.loadFromJob(as400, jobInput.getJobName(), jobInput.getUserName(), jobInput.getJobNumber());

                new SetEditorInputJob(jobLog, viewer, filterPanel).schedule();

            } catch (JobNotFoundException e) {
                handleDataLoadException(Messages.bind(Messages.Job_C_B_A_not_found, new Object[] { jobInput.getJobName(), jobInput.getUserName(),
                    jobInput.getJobNumber() }), e);
            } catch (JobLogNotLoadedException e) {
                handleDataLoadException(
                    Messages.bind(Messages.Could_not_load_job_log_of_job_C_B_A_Reason_D, new Object[] { jobInput.getJobName(),
                        jobInput.getUserName(), jobInput.getJobNumber(), ExceptionHelper.getLocalizedMessage(e) }), e);
            } catch (Throwable e) {
                handleDataLoadException("*** Failed to retrieve job log ***", e, true);
            }

            return Status.OK_STATUS;
        }

    }

    private class LoadSpooledFileJob extends Job {

        private JobLogExplorerSpooledFileInput spooledFileInput;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;

        public LoadSpooledFileJob(JobLogExplorerSpooledFileInput spooledFileInput, JobLogExplorerTableViewer viewer,
            JobLogExplorerFilterPanel filterPanel) {
            super(Messages.Job_Parsing_job_log);

            this.spooledFileInput = spooledFileInput;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
        }

        @Override
        protected IStatus run(IProgressMonitor arg0) {

            try {

                SpooledFile spooledFile = spooledFileInput.getSpooledFile();

                String format = IPreferences.OUTPUT_FORMAT_TEXT;
                IFile target = ISpherePlugin.getDefault().getSpooledFilesProject().getFile(spooledFile.getTemporaryName(format));

                IFile localSpooledFilePath = spooledFile.downloadSpooledFile(format, target);
                final String filePath = localSpooledFilePath.getLocation().toOSString();
                final String originalFileName = spooledFile.getQualifiedName();

                JobLogExplorerFileInput editorInput = new JobLogExplorerFileInput(filePath, originalFileName);

                JobLogParser reader = new JobLogParser();
                final JobLog jobLog = reader.loadFromStmf(editorInput.getPath());

                new SetEditorInputJob(jobLog, viewer, filterPanel).schedule();

            } catch (InvalidJobLogFormatException e) {
                handleDataLoadException(Messages.Invalid_job_log_Format_Could_not_find_first_line_of_job_log, e);
            } catch (Throwable e) {
                handleDataLoadException("*** Failed to parse job log from spooled file ***", e, true);
            }

            return Status.OK_STATUS;
        }
    }

    private class SetEditorInputJob extends UIJob {

        private JobLog jobLog;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;

        public SetEditorInputJob(JobLog jobLog, JobLogExplorerTableViewer viewer, JobLogExplorerFilterPanel filterPanel) {
            super(""); //$NON-NLS-1$

            this.jobLog = jobLog;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {

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

            } catch (Throwable e) {
                handleDataLoadException("*** Failed to set job log data ***", e, true);
            }

            JobLogExplorerStatusChangedEvent status = new JobLogExplorerStatusChangedEvent(
                JobLogExplorerStatusChangedEvent.EventType.FINISHED_DATA_LOADING, JobLogExplorerTab.this);
            notifyStatusChangedListeners(status);

            return Status.OK_STATUS;
        }

    }

}
