/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
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
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.dao.OutputFileDAO;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type1ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type2ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type3ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type4ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type5ViewerFactory;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

/**
 * This widget is a viewer for the journal entries of an output file of the
 * DSPJRN command. It is created by a sub-class of the
 * {@link AbstractTypeViewerFactory}. It is used by the "Journal Explorer" view
 * to create the tabs for the opened output files of the DSPJRN command.
 * 
 * @see JournalEntry
 * @see JournalEntryViewerView
 */
public class JournalEntriesViewerForOutputFilesTab extends AbstractJournalEntriesViewerTab {

    private TableViewer tableViewer;

    public JournalEntriesViewerForOutputFilesTab(CTabFolder parent, OutputFile outputFile, String whereClause,
        SelectionListener loadJournalEntriesSelectionListener) {
        super(parent, outputFile, loadJournalEntriesSelectionListener);

        setSelectClause(whereClause);
        setSqlEditorVisibility(false);

        Preferences.getInstance().addPropertyChangeListener(this);

        initializeComponents();
    }

    protected String getLabel() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getOutputFile().getConnectionName());
        buffer.append(": ");
        buffer.append(getOutputFile().getQualifiedName());

        return buffer.toString();
    }

    protected String getTooltip() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(Messages.bind(Messages.Title_Connection_A, getOutputFile().getConnectionName()));
        buffer.append("\n");
        buffer.append(Messages.bind(Messages.Title_File_A, getOutputFile().getQualifiedName()));

        return buffer.toString();
    }

    protected TableViewer createTableViewer(Composite container) {

        try {

            AbstractTypeViewerFactory factory = null;
            switch (getOutputFile().getType()) {
            case TYPE5:
                factory = new Type5ViewerFactory();
                break;
            case TYPE4:
                factory = new Type4ViewerFactory();
                break;
            case TYPE3:
                factory = new Type3ViewerFactory();
                break;
            case TYPE2:
                factory = new Type2ViewerFactory();
                break;
            default:
                factory = new Type1ViewerFactory();
                break;
            }

            tableViewer = factory.createTableViewer(container, getDialogSettingsManager());
            tableViewer.addSelectionChangedListener(this);
            tableViewer.getTable().setEnabled(false);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalEntriesViewerForOutputFilesTab.createTableViewer() ***", e);
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

        setFocusOnSqlEditor();

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

                monitor.beginTask(Messages.Status_Loading_journal_entries, IProgressMonitor.UNKNOWN);

                OutputFileDAO journalDAO = new OutputFileDAO(getOutputFile());

                final JournalEntries data = journalDAO.getJournalData(whereClause, monitor);
                data.applyFilter(filterWhereClause);

                if (!isDisposed()) {
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            setInputData(data);
                            view.finishDataLoading(JournalEntriesViewerForOutputFilesTab.this, false);
                        }
                    });
                }

            } catch (Throwable e) {

                if (!isDisposed()) {
                    final Throwable e1 = e;
                    getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            view.handleDataLoadException(JournalEntriesViewerForOutputFilesTab.this, e1);
                        }
                    });
                }

            } finally {
                monitor.done();
            }

            return Status.OK_STATUS;
        }

    }

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
                            view.finishDataLoading(JournalEntriesViewerForOutputFilesTab.this, true);
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
                            view.handleDataLoadException(JournalEntriesViewerForOutputFilesTab.this, e1);
                        }
                    });
                }

            }

            return Status.OK_STATUS;
        }

    };
}
