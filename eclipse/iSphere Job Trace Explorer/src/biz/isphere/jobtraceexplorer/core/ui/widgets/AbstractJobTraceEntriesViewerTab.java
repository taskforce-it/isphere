/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.widgets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.RowJEP;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.IResizableTableColumnsViewer;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.sqleditor.SqlEditor;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntries;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;
import biz.isphere.jobtraceexplorer.core.model.JobTraceSession;
import biz.isphere.jobtraceexplorer.core.ui.contentproviders.JobTraceViewerContentProvider;
import biz.isphere.jobtraceexplorer.core.ui.labelproviders.JobTraceEntryLabelProvider;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceEntryColumn;
import biz.isphere.jobtraceexplorer.core.ui.model.JobTraceViewerFactory;
import biz.isphere.jobtraceexplorer.core.ui.views.IDataLoadPostRun;
import biz.isphere.jobtraceexplorer.core.ui.widgets.internals.ISearchComparer;
import biz.isphere.jobtraceexplorer.core.ui.widgets.internals.SearchComparerSQL;
import biz.isphere.jobtraceexplorer.core.ui.widgets.internals.SearchComparerText;

/**
 * This widget is a viewer for the job trace entries of an output file of the
 * DSPJRN command. It is created by a sub-class of the
 * {@link JobTraceViewerFactory}. It is used by the "Job Trace Explorer" view to
 * create the tabs for the opened output files of the DSPJRN command.
 * 
 * @see JobTraceEntry
 * @see JobTraceEntryViewerView
 */
public abstract class AbstractJobTraceEntriesViewerTab extends CTabItem implements IResizableTableColumnsViewer, ISelectionChangedListener,
    ISelectionProvider, IPropertyChangeListener, SelectionListener {

    private DialogSettingsManager dialogSettingsManager = null;

    private JobTraceSession jobTraceSession;
    private Composite container;
    private JobTraceExplorerSearchPanel filterPanel;
    private Set<ISelectionChangedListener> selectionChangedListeners;
    private boolean isSqlEditorVisible;
    private SelectionListener loadJobTraceEntriesSelectionListener;

    private TableViewer tableViewer;
    private JobTraceEntries data;
    private SqlEditor sqlEditor;

    public AbstractJobTraceEntriesViewerTab(CTabFolder parent, JobTraceSession jobTraceSession, SelectionListener loadJobTraceEntriesSelectionListener) {
        super(parent, SWT.NONE);

        setSqlEditorVisibility(false);

        this.jobTraceSession = jobTraceSession;
        this.selectionChangedListeners = new HashSet<ISelectionChangedListener>();
        this.isSqlEditorVisible = false;
        this.loadJobTraceEntriesSelectionListener = loadJobTraceEntriesSelectionListener;

        setSqlEditorVisibility(false);

        initializeComponents(parent);
    }

    /*
     * View
     */

    private String getLabel() {
        return jobTraceSession.toString();
    }

    private String getTooltip() {
        return jobTraceSession.toString();
    }

    public JobTraceSession getJobTraceSession() {
        return jobTraceSession;
    }

    protected void setEnabled(boolean enabled) {
        tableViewer.getTable().setEnabled(enabled);
        if (isSqlEditorVisible()) {
            filterPanel.setEnabled(false);
            setSqlEditorEnabled(enabled);
        } else {
            filterPanel.setEnabled(enabled);
        }
    }

    /*
     * SQL Editor
     */

    private ContentAssistProposal[] getContentAssistProposals() {

        List<ContentAssistProposal> proposals = JobTraceEntry.getContentAssistProposals();

        return proposals.toArray(new ContentAssistProposal[proposals.size()]);
    }

    private void createSqlEditor() {

        if (!isAvailable(sqlEditor)) {
            sqlEditor = WidgetFactory.createSqlEditor(getContainer(), getClass().getSimpleName(), getDialogSettingsManager());
            sqlEditor.setContentAssistProposals(getContentAssistProposals());
            sqlEditor.addSelectionListener(loadJobTraceEntriesSelectionListener);
            sqlEditor.setWhereClause(getFilterWhereClause());
            GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.heightHint = 120;
            sqlEditor.setLayoutData(gd);
            getContainer().layout();
            sqlEditor.setFocus();
            sqlEditor.setBtnExecuteLabel(Messages.ButtonLabel_Filter);
            sqlEditor.setBtnExecuteToolTipText(Messages.ButtonTooltip_Filter);
            sqlEditor.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    setFilterWhereClause(sqlEditor.getWhereClause());
                }
            });
        }
    }

    private void destroySqlEditor() {

        if (sqlEditor != null) {
            // Important, must be called to ensure the SqlEditor is removed from
            // the list of preferences listeners.
            sqlEditor.dispose();
            getContainer().layout();
        }
    }

    public boolean hasSqlEditor() {
        return true;
    }

    public void setSqlEditorEnabled(boolean enabled) {

        if (isSqlEditorVisible()) {
            sqlEditor.setEnabled(enabled);
        }
    }

    public void setFocusOnSqlEditor() {

        if (isSqlEditorVisible()) {
            sqlEditor.setFocus();
        }
    }

    public void storeSqlEditorHistory() {
        sqlEditor.storeHistory();
    }

    public void refreshSqlEditorHistory() {
        if (isAvailable(sqlEditor)) {
            sqlEditor.refreshHistory();
        }
    }

    private void setFilterWhereClause(String filterWhereClause) {
        jobTraceSession.getJobTraceEntries().setFilterWhereClause(filterWhereClause);
    }

    public String getFilterWhereClause() {
        return jobTraceSession.getJobTraceEntries().getFilterWhereClause();
    }

    public boolean isFiltered() {
        return hasWhereClause();
    }

    private boolean hasWhereClause() {
        return jobTraceSession.getJobTraceEntries().hasFilterWhereClause();
    }

    private void initializeComponents(CTabFolder parent) {

        setText(getLabel());
        setToolTipText(getTooltip());

        this.container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createFilterPanel(container);
        createMainPanel(container);

        container.layout(true);
        setControl(container);
    }

    private void createFilterPanel(Composite parent) {

        filterPanel = new JobTraceExplorerSearchPanel(parent, SWT.NONE);
        filterPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        filterPanel.addFilterChangedListener(this);
    }

    private void createMainPanel(Composite parent) {

        tableViewer = createTableViewer(parent);
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISphereJobTraceExplorerCorePlugin.getDefault().getDialogSettings(),
                AbstractJobTraceEntriesViewerTab.class);
        }
        return dialogSettingsManager;
    }

    private Composite getContainer() {
        return container;
    }

    public void resetColumnSizes() {
        dialogSettingsManager.resetColumnWidths(tableViewer.getTable());
    }

    public boolean isSqlEditorVisible() {
        return isSqlEditorVisible;
    }

    public void setSqlEditorVisibility(boolean visible) {

        if (!hasSqlEditor()) {
            this.isSqlEditorVisible = false;
        } else {
            this.isSqlEditorVisible = visible;
        }

        setSqlEditorEnablement();
    }

    /*
     * Job Trace Session (input data)
     */

    public abstract void openJobTraceSession(IDataLoadPostRun postRun) throws Exception;

    public abstract void reloadJobTraceSession(IDataLoadPostRun postRun) throws Exception;

    public boolean isSameSession(JobTraceSession otherJobTraceSession) {

        if (this.jobTraceSession == null && otherJobTraceSession == null) {
            return true;
        } else if (this.jobTraceSession != null) {
            return this.jobTraceSession.equals(otherJobTraceSession);
        } else {
            return false;
        }
    }

    public void setJobTraceSession(JobTraceSession jobTraceSession) {
        this.jobTraceSession = jobTraceSession;
        setInputData(jobTraceSession.getJobTraceEntries());
    }

    protected void setInputData(JobTraceEntries data) {

        this.data = data;

        container.layout(true);
        tableViewer.setInput(null);
        tableViewer.setUseHashlookup(true);

        if (data != null) {
            tableViewer.setItemCount(data.size());
            tableViewer.setInput(data);
            setEnabled(true);
        } else {
            tableViewer.setItemCount(0);
            setEnabled(false);
        }

        tableViewer.setSelection(null);
    }

    public JobTraceEntry getSelectedItem() {

        int index = tableViewer.getTable().getSelectionIndex();
        if (index < 0) {
            return null;
        }

        JobTraceEntry jobTraceEntry = getElementAt(index);

        return jobTraceEntry;
    }

    public void setSelectedItem(JobTraceEntry jobTraceEntry) {

        if (jobTraceEntry == null) {
            return;
        }

        tableViewer.setSelection(new StructuredSelection(jobTraceEntry));
        int index = tableViewer.getTable().getSelectionIndex();
        if (index >= 0) {
            tableViewer.getTable().setTopIndex(index);
        }
    }

    public void filterJobTraceSession(final IDataLoadPostRun postRun) throws Exception {

        setEnabled(false);

        Job filterJobTraceDataJob = new FilterJobTraceSessionDataJob(postRun);
        filterJobTraceDataJob.schedule();
    }

    public JobTraceEntryColumn[] getColumns() {
        return getLabelProvider().getColumns();
    }

    @Override
    public void dispose() {

        if (data != null) {
            data = null;
        }

        if (tableViewer != null) {
            tableViewer.getTable().dispose();
            tableViewer = null;
        }

        super.dispose();
    }

    public StructuredSelection getSelection() {

        ISelection selection = tableViewer.getSelection();
        if (selection instanceof StructuredSelection) {
            return (StructuredSelection)selection;
        }

        return new StructuredSelection(new JobTraceEntry[0]);
    }

    public void setSelection(ISelection selection) {
        // satisfy the ISelectionProvider interface
        tableViewer.setSelection(selection);
    }

    public JobTraceEntry[] getSelectedItems() {

        List<JobTraceEntry> selectedItems = new LinkedList<JobTraceEntry>();

        StructuredSelection selection = getSelection();
        Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof JobTraceEntry) {
                selectedItems.add((JobTraceEntry)object);
            }
        }

        return selectedItems.toArray(new JobTraceEntry[selectedItems.size()]);
    }

    public JobTraceEntries getInput() {

        JobTraceViewerContentProvider contentProvider = getContentProvider();
        return contentProvider.getInput();
    }

    private JobTraceViewerContentProvider getContentProvider() {
        return (JobTraceViewerContentProvider)tableViewer.getContentProvider();
    }

    private JobTraceEntry getElementAt(int index) {
        return getContentProvider().getElementAt(index);
    }

    private JobTraceEntryLabelProvider getLabelProvider() {
        return (JobTraceEntryLabelProvider)tableViewer.getLabelProvider();
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        selectionChangedListeners.remove(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {

        SelectionChangedEvent newEvent = new SelectionChangedEvent(this, event.getSelection());

        for (ISelectionChangedListener selectionChangedListener : selectionChangedListeners) {
            selectionChangedListener.selectionChanged(newEvent);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {

        if (event.getProperty() == null) {
            return;
        }
    }

    private TableViewer createTableViewer(Composite container) {

        try {

            tableViewer = new JobTraceViewerFactory().createTableViewer(container, getDialogSettingsManager());
            tableViewer.addSelectionChangedListener(this);
            setEnabled(false);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JobTraceEntriesSQLViewerTab.createTableViewer() ***", e);
            MessageDialog.openError(getParent().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return null;
        }
    }

    public boolean isLoading() {

        if (tableViewer.getTable().isEnabled()) {
            return false;
        }

        return true;
    }

    private void setSqlEditorEnablement() {

        if (hasSqlEditor()) {
            if (isSqlEditorVisible()) {
                createSqlEditor();
            } else {
                destroySqlEditor();
            }
        }
    }

    private boolean isAvailable(Control control) {

        if (control != null && !control.isDisposed()) {
            return true;
        }

        return false;
    }

    public void widgetDefaultSelected(SelectionEvent event) {
        widgetSelected(event);
    }

    /*
     * Filter Panel Listener
     */

    public void widgetSelected(SelectionEvent event) {

        switch (event.detail) {
        case JobTraceExplorerFilterPanelEvents.SEARCH_UP:
            doSearchUp(event.text);
            break;

        case JobTraceExplorerFilterPanelEvents.SEARCH_DOWN:
            doSearchDown(event.text);
            break;

        default:
            break;
        }
    }

    private void doSearchUp(String text) {

        if (StringHelper.isNullOrEmpty(text)) {
            return;
        }

        boolean isSQLExpression = isSQLExpression(text);
        doSearchUp(text, isSQLExpression, tableViewer.getTable().getSelectionIndex(), 0);
    }

    private void doSearchUp(String text, boolean isSQLExpression, int startIndex, int minIndex) {

        Table table = tableViewer.getTable();
        if (table == null || table.getItemCount() <= 0) {
            return;
        }

        ISearchComparer searchConfig = null;
        if (!isSQLExpression) {
            searchConfig = new SearchComparerText(tableViewer.getTable().getColumns());
            searchConfig.setWhereClause(text);
        } else {
            searchConfig = new SearchComparerSQL(JobTraceEntry.getColumnMapping());
            searchConfig.setWhereClause(text);
        }

        int currentIndex = startIndex;
        if (currentIndex < 0) {
            currentIndex = table.getItemCount();
        }

        currentIndex--;

        while (currentIndex >= minIndex) {
            JobTraceEntry jobTraceEntry = getElementAt(currentIndex);
            if (searchConfig.isMatch(jobTraceEntry)) {
                table.setSelection(currentIndex);
                return; // Found!
            }
            currentIndex--;
        }

        Display.getCurrent().beep();

        if (startIndex < table.getItemCount() - 1) {
            doSearchUp(text, isSQLExpression, table.getItemCount(), startIndex);
        }
    }

    private void doSearchDown(String text) {

        if (StringHelper.isNullOrEmpty(text)) {
            return;
        }

        boolean isSQLExpression = isSQLExpression(text);
        doSearchDown(text, isSQLExpression, tableViewer.getTable().getSelectionIndex(), tableViewer.getTable().getItemCount() - 1);
    }

    private void doSearchDown(String text, boolean isSQLExpression, int startIndex, int maxIndex) {

        Table table = tableViewer.getTable();
        if (table == null || table.getItemCount() <= 0) {
            return;
        }

        ISearchComparer searchConfig = null;
        if (!isSQLExpression) {
            searchConfig = new SearchComparerText(tableViewer.getTable().getColumns());
            searchConfig.setWhereClause(text);
        } else {
            searchConfig = new SearchComparerSQL(JobTraceEntry.getColumnMapping());
            searchConfig.setWhereClause(text);
        }

        int currentIndex = startIndex;
        if (currentIndex > maxIndex) {
            currentIndex = -1;
        }

        currentIndex++;

        while (currentIndex <= maxIndex) {
            JobTraceEntry jobTraceEntry = getElementAt(currentIndex);
            if (searchConfig.isMatch(jobTraceEntry)) {
                table.setSelection(currentIndex);
                return; // Found!
            }
            currentIndex++;
        }

        Display.getCurrent().beep();

        if (startIndex > 0) {
            doSearchDown(text, isSQLExpression, -1, startIndex);
        }
    }

    private boolean isSQLExpression(String searchArgument) {

        try {

            RowJEP sqljep = new RowJEP(searchArgument);
            sqljep.parseExpression(JobTraceEntry.getColumnMapping());

            return true;

        } catch (ParseException e) {
            return false;
        }
    }

    private class FilterJobTraceSessionDataJob extends Job {

        private IDataLoadPostRun postRun;

        public FilterJobTraceSessionDataJob(IDataLoadPostRun postRun) {
            super(Messages.Status_Loading_job_trace_entries_of_session_A);

            this.postRun = postRun;
        }

        public IStatus run(IProgressMonitor monitor) {

            final JobTraceEntries data = getInput();

            try {

                data.applyFilter();

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            setEnabled(true);
                            setFocusOnSqlEditor();
                            postRun.finishDataLoading(AbstractJobTraceEntriesViewerTab.this, true);
                        }
                    });
                }

            } catch (Throwable e) {

                if (!isDisposed()) {
                    final Throwable e1 = e;
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setEnabled(true);
                            setFocusOnSqlEditor();
                            postRun.handleDataLoadException(AbstractJobTraceEntriesViewerTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };
}
