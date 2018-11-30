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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.exceptions.BufferTooSmallException;
import biz.isphere.journalexplorer.core.exceptions.NoJournalEntriesLoadedException;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.OutputFile;
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
public class JournalEntriesViewerForRetrievedJournalEntries extends AbstractJournalEntriesViewer implements ISelectionChangedListener,
    ISelectionProvider, IPropertyChangeListener {

    private JrneToRtv jrneToRtv;
    private OutputFile outputFile;

    private TableViewer tableViewer;

    public JournalEntriesViewerForRetrievedJournalEntries(CTabFolder parent, JrneToRtv jrneToRtv,
        SelectionListener loadJournalEntriesSelectionListener) {
        super(parent, loadJournalEntriesSelectionListener);

        this.jrneToRtv = jrneToRtv;
        this.outputFile = JournalDAO.getOutputFile(this.jrneToRtv.getConnectionName());

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

    public void openJournal(final JournalExplorerView view) throws Exception {

        setSqlEditorEnabled(false);

        Job loadJournalDataJob = new Job(Messages.Status_Loading_journal_entries) {

            public IStatus run(IProgressMonitor monitor) {

                try {

                    // Clone the selection arguments to start with the original
                    // values when the view is refreshed.
                    JrneToRtv tJrneToRtv = jrneToRtv.clone();

                    JournalDAO journalDAO = new JournalDAO(tJrneToRtv);
                    final JournalEntries data = journalDAO.getJournalData(getWhereClause());

                    if (!isDisposed()) {
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                setInputData(data);
                                setSqlEditorEnabled(true);
                                setFocusOnSqlEditor();
                                view.finishDataLoading(JournalEntriesViewerForRetrievedJournalEntries.this);
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

        loadJournalDataJob.schedule();
    }

    public boolean hasSqlEditor() {
        return true;
    }

    protected ContentAssistProposal[] getContentAssistProposals() {

        HashMap<String, Integer> columnMapping = JournalEntry.getColumnMapping();

        List<ContentAssistProposal> proposals = new LinkedList<ContentAssistProposal>();

        MetaTable metaData = getMetaData();
        if (metaData != null) {
            for (MetaColumn column : metaData.getColumns()) {
                if (columnMapping.containsKey(column.getName())) {
                    proposals.add(new ContentAssistProposal(column.getName(), column.getFormattedType() + " - " + column.getText()));
                }
            }
        }

        return proposals.toArray(new ContentAssistProposal[proposals.size()]);
    }

    private MetaTable getMetaData() {

        try {
            return MetaDataCache.getInstance().retrieveMetaData(outputFile);
        } catch (Exception e) {
            String fileName;
            if (outputFile == null) {
                fileName = "null"; //$NON-NLS-1$
            } else {
                fileName = outputFile.toString();
            }
            ISpherePlugin.logError("*** Could not load meta data of file '" + fileName + "' ***", e); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
    }
}
