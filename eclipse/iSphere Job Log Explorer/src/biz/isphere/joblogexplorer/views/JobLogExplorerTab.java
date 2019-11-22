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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.AbstractJobLogExplorerInput;
import biz.isphere.joblogexplorer.editor.IJobLogExplorerStatusChangedListener;
import biz.isphere.joblogexplorer.editor.JobLogExplorerFileInput;
import biz.isphere.joblogexplorer.editor.JobLogExplorerJobInput;
import biz.isphere.joblogexplorer.editor.JobLogExplorerSpooledFileInput;
import biz.isphere.joblogexplorer.editor.JobLogExplorerStatusChangedEvent;
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

    private List<IJobLogExplorerStatusChangedListener> statusChangedListeners;

    private AbstractJobLogExplorerInput jobLogExplorerInput;

    private DialogSettingsManager dialogSettingsManager = null;

    public JobLogExplorerTab(CTabFolder parent) {
        super(parent, SWT.NONE);

        this.statusChangedListeners = new ArrayList<IJobLogExplorerStatusChangedListener>();

        initializeComponents(parent);
    }

    public void setEnabled(boolean enabled) {
        tableViewerPanel.setEnabled(enabled);
        filterPanel.setEnabled(enabled);
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

        this.container = new Composite(parent, SWT.BORDER);
        container.setLayout(createGridLayoutNoMargin());
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        JobLogExplorerFilterPanel filterPanel = createTopPanel(container);
        JobLogExplorerTableViewer tableViewer = createMainPanel(container, filterPanel);

        container.layout(true);
        setControl(container);

        tableViewer.addStatusChangedListener(this);
    }

    private JobLogExplorerFilterPanel createTopPanel(Composite parent) {

        filterPanel = new JobLogExplorerFilterPanel();
        filterPanel.createViewer(parent);

        return filterPanel;
    }

    private JobLogExplorerTableViewer createMainPanel(Composite parent, JobLogExplorerFilterPanel filterPanel) {

        sashForm = new SashForm(parent, SWT.NONE);
        GridData sashFormLayoutData = new GridData(GridData.FILL_BOTH);
        sashForm.setLayoutData(sashFormLayoutData);

        tableViewerPanel = createLeftPanel(sashForm);
        detailsPanel = createRightPanel(sashForm);
        sashForm.setWeights(loadWeights());

        tableViewerPanel.addMessageSelectionChangedListener(detailsPanel);
        filterPanel.addFilterChangedListener(tableViewerPanel);

        return tableViewerPanel;
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

        JobLogExplorerStatusChangedEvent status = new JobLogExplorerStatusChangedEvent(JobLogExplorerStatusChangedEvent.EventType.DATA_LOAD_ERROR,
            this);
        status.setException(message, e);
        notifyStatusChangedListeners(status);
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

}
