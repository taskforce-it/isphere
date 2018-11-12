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
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
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
public class JournalEntriesViewerForOutputFiles extends AbstractJournalEntriesViewer implements ISelectionChangedListener, ISelectionProvider,
    IPropertyChangeListener {

    private OutputFile outputFile;

    private TableViewer tableViewer;

    public JournalEntriesViewerForOutputFiles(CTabFolder parent, OutputFile outputFile, SelectionListener loadJournalEntriesSelectionListener) {
        super(parent, loadJournalEntriesSelectionListener);

        this.outputFile = outputFile;

        setSqlEditorVisibility(false);

        Preferences.getInstance().addPropertyChangeListener(this);

        initializeComponents();
    }

    protected String getLabel() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(outputFile.getConnectionName());
        buffer.append(": ");
        buffer.append(outputFile.getQualifiedName());

        return buffer.toString();
    }

    protected String getTooltip() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(Messages.bind(Messages.Title_Connection_A, outputFile.getConnectionName()));
        buffer.append("\n");
        buffer.append(Messages.bind(Messages.Title_File_A, outputFile.getQualifiedName()));

        return buffer.toString();
    }

    protected TableViewer createTableViewer(Composite container) {

        try {

            AbstractTypeViewerFactory factory = null;
            switch (outputFile.getType()) {
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

            tableViewer = factory.createTableViewer(container);
            tableViewer.addSelectionChangedListener(this);
            tableViewer.getTable().setEnabled(false);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalEntriesViewerForOutputFiles.createTableViewer() ***", e);
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

        setFocusOnSqlEditor();

        Job loadJournalDataJob = new Job(Messages.Status_Loading_journal_entries) {

            public IStatus run(IProgressMonitor monitor) {

                try {

                    OutputFileDAO journalDAO = new OutputFileDAO(outputFile);
                    final JournalEntries data = journalDAO.getJournalData(getWhereClause());

                    if (!isDisposed()) {
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                setInputData(data);
                                view.finishDataLoading(JournalEntriesViewerForOutputFiles.this);
                            }
                        });
                    }

                } catch (Throwable e) {

                    if (!isDisposed()) {
                        final Throwable e1 = e;
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                view.handleDataLoadException(JournalEntriesViewerForOutputFiles.this, e1);
                            }
                        });
                    }

                }

                return Status.OK_STATUS;
            }

        };

        loadJournalDataJob.schedule();
    }

    public boolean hasSqlEditor() {
        return true;
    }

    protected ContentAssistProposal[] getContentAssistProposals() {

        List<ContentAssistProposal> proposals = new LinkedList<ContentAssistProposal>();

        MetaTable metaData = getMetaData();
        if (metaData != null) {
            for (MetaColumn column : metaData.getColumns()) {
                proposals.add(new ContentAssistProposal(column.getName(), column.getFormattedType() + " - " + column.getText()));
            }
        }

        return proposals.toArray(new ContentAssistProposal[proposals.size()]);
    }

    private MetaTable getMetaData() {

        try {
            return MetaDataCache.INSTANCE.retrieveMetaData(outputFile);
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
