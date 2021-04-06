/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.json.JsonImporter;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.exceptions.BufferTooSmallException;
import biz.isphere.journalexplorer.core.exceptions.NoJournalEntriesLoadedException;
import biz.isphere.journalexplorer.core.helpers.TimeTaken;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.JsonFile;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.model.api.IBMiMessage;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type5ViewerFactory;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

/**
 * This widget is a viewer for the journal entries loaded from a Json export
 * file. It is used by the "Journal Explorer" view when creating a tab for
 * retrieved journal entries.
 * 
 * @see JournalEntry
 * @see JournalEntryViewerView
 */
public class JournalEntriesViewerForLoadedJournalEntriesTab extends AbstractJournalEntriesViewerTab {

    private JsonFile jsonFile;

    private TableViewer tableViewer;

    public JournalEntriesViewerForLoadedJournalEntriesTab(Shell shell, CTabFolder parent, JsonFile jsonFile, SQLWhereClause whereClause,
        SelectionListener loadJournalEntriesSelectionListener) {
        super(shell, parent, loadJournalEntriesSelectionListener);

        this.jsonFile = jsonFile;

        setSelectClause(whereClause);
        setSqlEditorVisibility(false);

        Preferences.getInstance().addPropertyChangeListener(this);

        initializeComponents();
    }

    protected String getLabel() {
        return FileHelper.getFileName(jsonFile.getPath());
    }

    protected String getTooltip() {
        return jsonFile.getPath();
    }

    protected TableViewer createTableViewer(Composite container) {

        try {

            AbstractTypeViewerFactory factory = new Type5ViewerFactory();

            tableViewer = factory.createTableViewer(container, getDialogSettingsManager());
            tableViewer.addSelectionChangedListener(this);
            tableViewer.getTable().setEnabled(false);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalEntriesViewerForRetrievedJournalEntriesTab.createTableViewer() ***", e);
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

    public void closeJournal() {
        setInputData(null);
    }

    public void openJournal(final JournalExplorerView view, SQLWhereClause whereClause, SQLWhereClause filterWhereClause) throws Exception {

        tableViewer.getTable().setEnabled(false);

        setSqlEditorEnabled(false);

        Job loadJournalDataJob = new OpenJournalJob(view, jsonFile, whereClause, filterWhereClause);
        loadJournalDataJob.schedule();
    }

    public void filterJournal(final JournalExplorerView view, SQLWhereClause whereClause) throws Exception {

        setSqlEditorEnabled(false);

        Job filterJournalDataJob = new FilterJournalJob(view, whereClause);
        filterJournalDataJob.schedule();
    }

    public boolean hasSqlEditor() {
        return true;
    }

    private class OpenJournalJob extends Job {

        private JournalExplorerView view;
        private String connectionName;
        private File jsonFile;
        private SQLWhereClause whereClause;
        private SQLWhereClause filterWhereClause;

        public OpenJournalJob(JournalExplorerView view, JsonFile jsonFile, SQLWhereClause whereClause, SQLWhereClause filterWhereClause) {
            super(Messages.Status_Loading_journal_entries);

            this.view = view;
            this.connectionName = jsonFile.getConnectionName();
            this.jsonFile = new File(jsonFile.getPath());
            this.whereClause = whereClause;
            this.filterWhereClause = filterWhereClause;
        }

        public IStatus run(IProgressMonitor monitor) {

            try {

                TimeTaken timeTaken = TimeTaken.start("Loading journal entries"); // //$NON-NLS-1$

                monitor.beginTask(Messages.Status_Loading_journal_entries, IProgressMonitor.UNKNOWN);

                JsonImporter<JournalEntries> importer = new JsonImporter<JournalEntries>(JournalEntries.class);

                final JournalEntries data = importer.execute(view.getViewSite().getShell(), jsonFile);

                // Overwrite connection name, if passed in
                data.finalizeJsonLoading(this.connectionName, whereClause);

                timeTaken.stop(data.size());

                data.applyFilter(filterWhereClause, monitor);

                if (ISphereHelper.checkISphereLibrary(getShell(), data.getConnectionName())) {
                    MetaDataCache.getInstance().preloadTables(getShell(), data.getJournaledFiles());
                }

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.finishDataLoading(JournalEntriesViewerForLoadedJournalEntriesTab.this, false);
                        }
                    });
                }

                IBMiMessage[] messages = data.getMessages();
                if (messages.length != 0) {
                    if (isBufferTooSmallException(messages)) {
                        throw new BufferTooSmallException();
                    } else if (isNoDataLoadedException(messages)) {
                        throw new NoJournalEntriesLoadedException(jsonFile.getPath());
                    } else {
                        throw new Exception("Error loading journal entries. \n" + messages[0].getID() + ": " + messages[0].getText());
                    }
                }

            } catch (Throwable e) {

                if (!isDisposed()) {
                    final Throwable e1 = e;
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.handleDataLoadException(JournalEntriesViewerForLoadedJournalEntriesTab.this, e1);
                        }
                    });
                }

            } finally {
                monitor.done();
            }

            return Status.OK_STATUS;
        }

        private boolean isBufferTooSmallException(IBMiMessage[] messages) {

            for (IBMiMessage ibmiMessage : messages) {
                if (BufferTooSmallException.ID.equals(ibmiMessage.getID())) {
                    return true;
                }
            }

            return false;
        }

        private boolean isNoDataLoadedException(IBMiMessage[] messages) {

            for (IBMiMessage ibmiMessage : messages) {
                if (NoJournalEntriesLoadedException.ID.equals(ibmiMessage.getID())) {
                    return true;
                }
            }

            return false;
        }

    };

    private class FilterJournalJob extends Job {

        private JournalExplorerView view;
        private SQLWhereClause whereClause;

        public FilterJournalJob(JournalExplorerView view, SQLWhereClause whereClause) {
            super(Messages.Status_Filtering_journal_entries);

            this.view = view;
            this.whereClause = whereClause;
        }

        public IStatus run(IProgressMonitor monitor) {

            final JournalEntries data = getInput();

            try {

                data.applyFilter(whereClause, monitor);

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.finishDataLoading(JournalEntriesViewerForLoadedJournalEntriesTab.this, true);
                        }
                    });
                }

            } catch (Throwable e) {

                if (!isDisposed()) {
                    final Throwable e1 = e;
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.handleDataLoadException(JournalEntriesViewerForLoadedJournalEntriesTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };
}
