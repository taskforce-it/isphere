/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.exceptions.BufferTooSmallException;
import biz.isphere.journalexplorer.core.exceptions.NoJournalEntriesLoadedException;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.api.IBMiMessage;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.model.dao.JournalDAO;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type5ViewerFactory;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

/**
 * This widget is a viewer for the journal entries retrieved by the
 * <i>QjoRetrieveJournalEntries</i> API. It is used by the "Journal Explorer"
 * view when creating a tab for retrieved journal entries.
 * 
 * @see JournalEntry
 * @see JournalEntryViewerView
 */
public class JournalEntriesViewerForRetrievedJournalEntries extends AbstractJournalEntriesViewer {

    private JrneToRtv jrneToRtv;
    private TableViewer tableViewer;

    public JournalEntriesViewerForRetrievedJournalEntries(CTabFolder parent, JrneToRtv jrneToRtv,
        SelectionListener loadJournalEntriesSelectionListener) {
        super(parent, JournalDAO.getOutputFile(jrneToRtv.getConnectionName()), loadJournalEntriesSelectionListener);

        this.jrneToRtv = jrneToRtv;

        setSelectClause(null);
        setSqlEditorVisibility(false);

        Preferences.getInstance().addPropertyChangeListener(this);

        initializeComponents();
    }

    protected String getLabel() {

        String[] files = jrneToRtv.getFiles();
        if (files.length == 1) {
            return jrneToRtv.getConnectionName() + ": " + files[0];
        }

        return jrneToRtv.getConnectionName() + ": " + jrneToRtv.getQualifiedJournalName();
    }

    protected String getTooltip() {

        String[] files = jrneToRtv.getFiles();

        StringBuilder buffer = new StringBuilder();

        buffer.append(Messages.bind(Messages.Title_Connection_A, jrneToRtv.getConnectionName()));
        buffer.append("\n");

        buffer.append(Messages.bind(Messages.Title_Journal_A, jrneToRtv.getQualifiedJournalName()));
        buffer.append("\n");

        if (files.length == 1) {
            buffer.append(Messages.bind(Messages.Title_File_A, files[0]));
        } else {
            buffer.append(Messages.bind(Messages.Title_Files_A, "*SELECTION"));
        }

        return buffer.toString();
    }

    protected TableViewer createTableViewer(Composite container) {

        try {

            AbstractTypeViewerFactory factory = new Type5ViewerFactory();

            tableViewer = factory.createTableViewer(container);
            tableViewer.addSelectionChangedListener(this);
            tableViewer.getTable().setEnabled(false);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalEntriesViewerForRetrievedJournalEntries.createTableViewer() ***", e);
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

    public void openJournal(final JournalExplorerView view, String whereClause, String filterWhereClause) throws Exception {

        setSqlEditorEnabled(false);

        Job loadJournalDataJob = new OpenJournalJob(view, whereClause, filterWhereClause);
        loadJournalDataJob.schedule();
    }

    public void filterJournal(final JournalExplorerView view, String whereClause) throws Exception {

        setSqlEditorEnabled(false);

        Job filterJournalDataJob = new FilterJournalJob(view, whereClause);
        filterJournalDataJob.schedule();
    }

    public boolean hasSqlEditor() {
        return true;
    }

    private class OpenJournalJob extends Job {

        private JournalExplorerView view;
        private String whereClause;
        private String filterWhereClause;

        public OpenJournalJob(JournalExplorerView view, String whereClause, String filterWhereClause) {
            super(Messages.Status_Loading_journal_entries);

            this.view = view;
            this.whereClause = whereClause;
            this.filterWhereClause = filterWhereClause;
        }

        public IStatus run(IProgressMonitor monitor) {

            try {

                // Clone the selection arguments to start with the original
                // values when the view is refreshed.
                JrneToRtv tJrneToRtv = jrneToRtv.clone();

                JournalDAO journalDAO = new JournalDAO(tJrneToRtv);
                final JournalEntries data = journalDAO.getJournalData(whereClause);
                data.applyFilter(filterWhereClause);

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.finishDataLoading(JournalEntriesViewerForRetrievedJournalEntries.this, false);
                        }
                    });
                }

                IBMiMessage[] messages = data.getMessages();
                if (messages.length != 0) {
                    if (isBufferTooSmallException(messages)) {
                        throw new BufferTooSmallException();
                    } else if (isNoDataLoadedException(messages)) {
                        throw new NoJournalEntriesLoadedException(tJrneToRtv.getJournalLibraryName(), tJrneToRtv.getJournalName());
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
                            view.handleDataLoadException(JournalEntriesViewerForRetrievedJournalEntries.this, e1);
                        }
                    });
                }

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
        private String whereClause;

        public FilterJournalJob(JournalExplorerView view, String whereClause) {
            super(Messages.Status_Loading_journal_entries);

            this.view = view;
            this.whereClause = whereClause;
        }

        public IStatus run(IProgressMonitor monitor) {

            final JournalEntries data = getInput();

            try {

                data.applyFilter(whereClause);

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            setSqlEditorEnabled(true);
                            setFocusOnSqlEditor();
                            view.finishDataLoading(JournalEntriesViewerForRetrievedJournalEntries.this, true);
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
                            view.handleDataLoadException(JournalEntriesViewerForRetrievedJournalEntries.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };
}
