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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.IResizableTableColumnsViewer;
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
    ISelectionProvider, IPropertyChangeListener {

    private DialogSettingsManager dialogSettingsManager = null;

    private JobTraceSession jobTraceSession;
    private Composite container;
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
        this.container = new Composite(parent, SWT.NONE);
        this.selectionChangedListeners = new HashSet<ISelectionChangedListener>();
        this.isSqlEditorVisible = false;
        this.loadJobTraceEntriesSelectionListener = loadJobTraceEntriesSelectionListener;

        // Preferences.getInstance().addPropertyChangeListener(this);
    }

    protected String getLabel() {
        return jobTraceSession.toString();
    }

    protected String getTooltip() {
        return jobTraceSession.toString();
    }

    protected JobTraceSession getJobTraceSession() {
        return jobTraceSession;
    }

    public boolean isSameSession(JobTraceSession otherJobTraceSession) {

        if (this.jobTraceSession == null && otherJobTraceSession == null) {
            return true;
        } else if (this.jobTraceSession != null) {
            return this.jobTraceSession.equals(otherJobTraceSession);
        } else {
            return false;
        }
    }

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

    protected void setSqlEditorEnabled(boolean enabled) {

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

    private boolean isAvailable(Control control) {

        if (control != null && !control.isDisposed()) {
            return true;
        }

        return false;
    }

    protected void initializeComponents() {

        setText(getLabel());
        setToolTipText(getTooltip());

        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer = createTableViewer(container);
        container.layout(true);
        setControl(container);
    }

    protected DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISphereJobTraceExplorerCorePlugin.getDefault().getDialogSettings(),
                AbstractJobTraceEntriesViewerTab.class);
        }
        return dialogSettingsManager;
    }

    protected Composite getContainer() {
        return container;
    }

    public void resetColumnSizes() {
        dialogSettingsManager.resetColumnWidths(tableViewer.getTable());
    }

    public boolean hasSqlEditor() {
        return false;
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

    private void setSqlEditorEnablement() {

        if (hasSqlEditor()) {
            if (isSqlEditorVisible()) {
                createSqlEditor();
            } else {
                destroySqlEditor();
            }
        }
    }

    public JobTraceEntryColumn[] getColumns() {
        return getLabelProvider().getColumns();
    }

    protected abstract TableViewer createTableViewer(Composite container);

    public abstract void openJobTraceSession(IDataLoadPostRun postRun) throws Exception;

    public abstract void filterJobTraceSession(IDataLoadPostRun postRun) throws Exception;

    public abstract void closeJobTraceSession();

    public abstract boolean isLoading();

    public abstract JobTraceEntry getSelectedItem();

    public abstract void setSelectedItem(JobTraceEntry jobTraceEntry);

    protected void setInputData(JobTraceEntries data) {

        this.data = data;

        container.layout(true);
        tableViewer.setInput(null);
        tableViewer.setUseHashlookup(true);

        if (data != null) {
            tableViewer.setItemCount(data.size());
            tableViewer.setInput(data);
            tableViewer.getTable().setEnabled(true);
        } else {
            tableViewer.setItemCount(0);
            tableViewer.getTable().setEnabled(false);
        }

        tableViewer.setSelection(null);
    }

    @Override
    public void dispose() {

        if (data != null) {

            data.clear();
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
}
