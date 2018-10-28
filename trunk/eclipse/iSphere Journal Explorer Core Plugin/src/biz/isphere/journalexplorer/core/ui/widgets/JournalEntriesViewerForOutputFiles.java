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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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
import biz.isphere.journalexplorer.core.swt.widgets.SqlEditor;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type1ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type2ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type3ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type4ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type5ViewerFactory;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;

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

    private SelectionListener loadJournalEntriesSelectionListener;
    private OutputFile outputFile;
    private Exception dataLoadException;
    private String whereClause;

    private boolean showSqlEditor;
    private SqlEditor sqlEditor;

    public JournalEntriesViewerForOutputFiles(CTabFolder parent, OutputFile outputFile, SelectionListener loadJournalEntriesSelectionListener) {
        super(parent);

        this.outputFile = outputFile;
        this.loadJournalEntriesSelectionListener = loadJournalEntriesSelectionListener;

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

            TableViewer tableViewer = factory.createTableViewer(container);
            tableViewer.addSelectionChangedListener(this);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalEntriesViewerForOutputFiles.createTableViewer() ***", e);
            MessageDialog.openError(getParent().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return null;
        }
    }

    public void openJournal() throws Exception {

        dataLoadException = null;
        if (isAvailable(sqlEditor)) {
            whereClause = sqlEditor.getWhereClause().trim();
            sqlEditor.setFocus();
        }

        Runnable loadJournalDataJob = new Runnable() {

            public void run() {

                try {

                    OutputFileDAO journalDAO = new OutputFileDAO(outputFile);
                    JournalEntries data = journalDAO.getJournalData(whereClause);

                    setInputData(data);

                } catch (Exception e) {
                    dataLoadException = e;
                }
            }

        };

        BusyIndicator.showWhile(getDisplay(), loadJournalDataJob);

        if (dataLoadException != null) {
            throw dataLoadException;
        }
    }

    public boolean hasSqlEditor() {
        return true;
    }

    public boolean isSqlEditorVisible() {
        return showSqlEditor;
    }

    public void setSqlEditorVisibility(boolean isVisible) {
        showSqlEditor = isVisible;
        setSqlEditorEnablement();
    }

    private void setSqlEditorEnablement() {

        if (showSqlEditor) {
            createSqlEditor();
        } else {
            destroySqlEditor();
        }
    }

    private void createSqlEditor() {

        if (!isAvailable(sqlEditor)) {
            sqlEditor = new SqlEditor(getContainer(), SWT.NONE);
            sqlEditor.setContentAssistProposals(getContentAssistProposals());
            sqlEditor.addSelectionListener(loadJournalEntriesSelectionListener);
            sqlEditor.setWhereClause(whereClause);
            GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.heightHint = 80;
            sqlEditor.setLayoutData(gd);
            getContainer().layout();
            sqlEditor.setFocus();
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

    private boolean isAvailable(Control control) {

        if (control != null && !control.isDisposed()) {
            return true;
        }

        return false;
    }

    private ContentAssistProposal[] getContentAssistProposals() {

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
