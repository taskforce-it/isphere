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
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.jface.dialogs.XEditorPart;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.action.rse.ExportToExcelAction;
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
import biz.isphere.joblogexplorer.jobs.IDropFileListener;
import biz.isphere.joblogexplorer.model.JobLog;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.model.JobLogParser;
import biz.isphere.joblogexplorer.model.JobLogReader;

import com.ibm.as400.access.AS400;

public class JobLogExplorerEditor extends XEditorPart implements IDropFileListener, IJobLogExplorerStatusChangedListener {

    public static final String ID = "biz.isphere.joblogexplorer.editor.JobLogExplorerEditor"; //$NON-NLS-1$

    private final static String SASH_WEIGHTS = "SASH_WEIGHTS_"; //$NON-NLS-1$

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

        JobLogExplorerTableViewer tableViewer = createMainPanel(mainArea, filterPanel);

        getSite().setSelectionProvider(tableViewer);

        new UIJob("") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                DoNotAskMeAgainDialog.openInformation(getShell(), DoNotAskMeAgain.INFORMATION_USAGE_JOB_LOG_EXPLORER,
                    Messages.Use_the_exclamation_mark_to_negate_a_search_argument_eg_Completion);
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    @Override
    public void setFocus() {
    }

    /*
     * Drag & drop support
     */

    public void dropJobLog(String connectionName, String jobName, String userName, String jobNumber, Object target) {

        if (connectionName == null || jobName == null || userName == null || jobNumber == null) {
            return;
        }

        if (!tableViewerPanel.isDisposed()) {
            tableViewerPanel.setInputData(null);
        }

        tableViewerPanel.setEnabled(false);
        JobLogExplorerEditor.this.showBusy(true);

        LoadJobLogJob loaderJob = new LoadJobLogJob(connectionName, jobName, userName, jobNumber, tableViewerPanel, filterPanel);
        loaderJob.schedule();
    }

    public void dropJobLog(String pathName, String originalFileName, Object target) {

        if (pathName == null) {
            return;
        }

        if (!tableViewerPanel.isDisposed()) {
            tableViewerPanel.setInputData(null);
        }

        tableViewerPanel.setEnabled(false);
        JobLogExplorerEditor.this.showBusy(true);

        ParseSpooledFileJob parserJob = new ParseSpooledFileJob(pathName, originalFileName, tableViewerPanel, filterPanel);
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

    private JobLogExplorerTableViewer createMainPanel(Composite parent, JobLogExplorerFilterPanel filterPanel) {

        SashForm sashForm = new SashForm(parent, SWT.NONE);
        GridData sashFormLayoutData = new GridData(GridData.FILL_BOTH);
        sashForm.setLayoutData(sashFormLayoutData);
        sashForm.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent event) {
                SashForm sashForm = (SashForm)event.getSource();
                int[] weights = sashForm.getWeights();
                storeWeights(weights);
            }
        });

        tableViewerPanel = createLeftPanel(sashForm);
        JobLogExplorerDetailsViewer detailsPanel = createRightPanel(sashForm);
        sashForm.setWeights(loadWeights());

        tableViewerPanel.addMessageSelectionChangedListener(detailsPanel);
        tableViewerPanel.addStatusChangedListener(this);
        filterPanel.addFilterChangedListener(tableViewerPanel);

        Object input = getEditorInput();

        if (input instanceof JobLogExplorerEditorFileInput) {
            JobLogExplorerEditorFileInput fileInput = (JobLogExplorerEditorFileInput)input;
            if (fileInput.getPath() != null) {
                dropJobLog(fileInput.getPath(), fileInput.getOriginalFileName(), null);
            } else {
                // Open empty editor to drag & drop files.
            }
        } else if (input instanceof JobLogExplorerEditorJobInput) {
            JobLogExplorerEditorJobInput jobInput = (JobLogExplorerEditorJobInput)input;
            if (jobInput.getName() != null) {
                dropJobLog(jobInput.getConnectionName(), jobInput.getJobName(), jobInput.getUserName(), jobInput.getJobNumber(), null);
            }
        }

        return tableViewerPanel;
    }

    private JobLogExplorerTableViewer createLeftPanel(SashForm sashForm) {

        Composite leftMainPanel = new Composite(sashForm, SWT.NONE);
        leftMainPanel.setLayout(createGridLayoutWithMargin());
        leftMainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        JobLogExplorerTableViewer tableViewer = new JobLogExplorerTableViewer();
        tableViewer.createViewer(leftMainPanel);

        addDropSupportOnComposite(leftMainPanel);

        return tableViewer;
    }

    private JobLogExplorerDetailsViewer createRightPanel(SashForm sashForm) {

        Composite rightMainPanel = new Composite(sashForm, SWT.NONE);
        rightMainPanel.setLayout(createGridLayoutWithMargin());
        rightMainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite alignmentHelperPanel = new Composite(rightMainPanel, SWT.NONE);
        alignmentHelperPanel.setLayout(createGridLayoutNoMargin());
        alignmentHelperPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

        JobLogExplorerDetailsViewer details = new JobLogExplorerDetailsViewer(getDialogBoundsSettings());
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
        treeAreaLayout.marginHeight = 2;
        treeAreaLayout.marginWidth = 2;
        return treeAreaLayout;
    }

    private class LoadJobLogJob extends Job {

        private String connectionName;
        private String jobName;
        private String userName;
        private String jobNumber;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;

        public LoadJobLogJob(String connectionName, String jobName, String userName, String jobNumber, JobLogExplorerTableViewer viewer,
            JobLogExplorerFilterPanel filterPanel) {
            super(Messages.Job_Loading_job_log);

            this.connectionName = connectionName;
            this.jobName = jobName;
            this.userName = userName;
            this.jobNumber = jobNumber;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
        }

        @Override
        protected IStatus run(IProgressMonitor arg0) {

            try {

                AS400 as400 = IBMiHostContributionsHandler.getSystem(connectionName);

                JobLogReader reader = new JobLogReader();
                final JobLog jobLog = reader.loadFromJob(as400, jobName, userName, jobNumber);

                StringBuilder qualifiedJobName = new StringBuilder();
                qualifiedJobName.append(connectionName);
                qualifiedJobName.append(":"); //$NON-NLS-1$
                qualifiedJobName.append(jobNumber);
                qualifiedJobName.append("/"); //$NON-NLS-1$
                qualifiedJobName.append(userName);
                qualifiedJobName.append("/"); //$NON-NLS-1$
                qualifiedJobName.append(jobName);

                new SetEditorInputJob(qualifiedJobName.toString(), jobLog, viewer, filterPanel).schedule();

            } catch (JobNotFoundException e) {
                MessageDialogAsync.displayError(Messages.E_R_R_O_R,
                    Messages.bind(Messages.Job_C_B_A_not_found, new Object[] { jobName, userName, jobNumber }));
            } catch (JobLogNotLoadedException e) {
                MessageDialogAsync.displayError(
                    Messages.E_R_R_O_R,
                    Messages.bind(Messages.Could_not_load_job_log_of_job_C_B_A_Reason_D,
                        new Object[] { jobName, userName, jobNumber, ExceptionHelper.getLocalizedMessage(e) }));
            } catch (Throwable e) {
                ISpherePlugin.logError("*** Failed to retrieve job log ***", e); //$NON-NLS-1$
                MessageDialogAsync.displayError(Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }

            return Status.OK_STATUS;
        }

    }

    private class ParseSpooledFileJob extends Job {

        private String pathName;
        private String originalFileName;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;

        public ParseSpooledFileJob(String pathName, String originalFileName, JobLogExplorerTableViewer viewer, JobLogExplorerFilterPanel filterPanel) {
            super(Messages.Job_Parsing_job_log);

            this.pathName = pathName;
            this.originalFileName = originalFileName;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
        }

        @Override
        protected IStatus run(IProgressMonitor arg0) {

            try {

                JobLogParser reader = new JobLogParser();
                final JobLog jobLog = reader.loadFromStmf(pathName);

                new SetEditorInputJob(originalFileName, jobLog, viewer, filterPanel).schedule();

            } catch (InvalidJobLogFormatException e) {
                MessageDialogAsync.displayError(getShell(), Messages.Invalid_job_log_Format_Could_not_find_first_line_of_job_log);
            }

            return Status.OK_STATUS;
        }
    }

    private class SetEditorInputJob extends UIJob {

        private String partName;
        private JobLog jobLog;
        private JobLogExplorerTableViewer viewer;
        private JobLogExplorerFilterPanel filterPanel;

        public SetEditorInputJob(String partName, JobLog jobLog, JobLogExplorerTableViewer viewer, JobLogExplorerFilterPanel filterPanel) {
            super(""); //$NON-NLS-1$

            this.partName = partName;
            this.jobLog = jobLog;
            this.viewer = viewer;
            this.filterPanel = filterPanel;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {

            setPartName(partName);
            viewer.setInputData(jobLog);

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

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    /**
     * Used by the JobLogExplorerEditorActionBarContributor to update the status
     * line.
     */
    public void updateActionsStatusAndStatusLine() {

        statusLine.setData(statusLineData);

        updateActionStatus();
    }

    public JobLogMessage[] getItems() {
        return tableViewerPanel.getItems();
    }

    public int getItemCount() {
        return tableViewerPanel.getItemCount();
    }

    public void statusChanged(StatusLineData status) {

        statusLineData = status;

        if (statusLine != null) {
            statusLine.setData(statusLineData);
        }

        updateActionStatus();
    }

    private void updateActionStatus() {

        IActionBars actionBars = getEditorSite().getActionBars();
        IContributionItem item = actionBars.getToolBarManager().find(ExportToExcelAction.ID);
        if (item instanceof ActionContributionItem) {
            ActionContributionItem actionItem = (ActionContributionItem)item;
            IAction action = actionItem.getAction();
            if (tableViewerPanel.getItemCount() > 0) {
                action.setEnabled(true);
            } else {
                action.setEnabled(false);
            }
        }
    }

    private int[] loadWeights() {

        int[] weights = new int[] { 8, 4 };
        for (int i = 0; i < weights.length; i++) {
            if (getDialogBoundsSettings().get(SASH_WEIGHTS + i) == null) {
                break;
            }
            weights[i] = getDialogBoundsSettings().getInt(SASH_WEIGHTS + i);
        }

        return weights;
    }

    private void storeWeights(int[] weights) {

        int count = 0;
        for (int weight : weights) {
            getDialogBoundsSettings().put(SASH_WEIGHTS + count, weight);
            count++;
        }
    }

    /**
     * Overridden to let {@link XEditorPart} store the state of this editor in a
     * separate section of the dialog settings file.
     */
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISphereJobLogExplorerPlugin.getDefault().getDialogSettings());
    }
}
