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

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.SelectionProviderIntermediate;
import biz.isphere.journalexplorer.core.model.File;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.dao.JournalDAO;
import biz.isphere.journalexplorer.core.model.dao.JournalOutputType;
import biz.isphere.journalexplorer.core.ui.contentproviders.JournalViewerContentProvider;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type1ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type2ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type3ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type4ViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type5ViewerFactory;

public class JournalEntriesViewer extends CTabItem {

    private Composite container;
    private TableViewer tableViewer;
    private String connectionName;
    private File outputFile;
    private List<JournalEntry> data;
    private Exception dataLoadException;

    public JournalEntriesViewer(CTabFolder parent, File outputFile) {
        super(parent, SWT.NONE);

        this.outputFile = outputFile;
        this.connectionName = outputFile.getConnectionName();
        this.container = new Composite(parent, SWT.NONE);

        this.initializeComponents();
    }

    private void initializeComponents() {

        container.setLayout(new FillLayout());
        setText(connectionName + ": " + outputFile.getQualifiedName());
        createTableViewer(container);
        container.layout(true);
        setControl(container);
    }

    private void createTableViewer(Composite container) {

        try {

            AbstractTypeViewerFactory factory = null;
            switch (getOutfileType(outputFile)) {
            case JournalOutputType.TYPE5:
                factory = new Type5ViewerFactory();
                break;
            case JournalOutputType.TYPE4:
                factory = new Type4ViewerFactory();
                break;
            case JournalOutputType.TYPE3:
                factory = new Type3ViewerFactory();
                break;
            case JournalOutputType.TYPE2:
                factory = new Type2ViewerFactory();
                break;
            default:
                factory = new Type1ViewerFactory();
                break;
            }

            tableViewer = factory.createTableViewer(container);

        } catch (Exception e) {
            MessageDialog.openError(getParent().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }

    private int getOutfileType(File outputFile) throws Exception {

        MetaTable metaTable = MetaDataCache.INSTANCE.retrieveMetaData(outputFile);

        return metaTable.getOutfileType();
    }

    public void openJournal() throws Exception {

        dataLoadException = null;

        Runnable loadJournalDataJob = new Runnable() {

            public void run() {

                try {

                    JournalDAO journalDAO = new JournalDAO(outputFile);
                    data = journalDAO.getJournalData();
                    container.layout(true);
                    tableViewer.setInput(null);
                    tableViewer.setUseHashlookup(true);
                    tableViewer.setItemCount(data.size());
                    tableViewer.setInput(data);

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

    @Override
    public void dispose() {

        super.dispose();

        if (data != null) {

            data.clear();
            data = null;
        }

        if (tableViewer != null) {

            tableViewer.getTable().dispose();
            tableViewer = null;
        }
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void setAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.setSelectionProviderDelegate(tableViewer);
    }

    public void removeAsSelectionProvider(SelectionProviderIntermediate selectionProvider) {
        selectionProvider.removeSelectionProviderDelegate(tableViewer);
    }

    public void refreshTable() {
        if (tableViewer != null) {
            tableViewer.refresh(true);
        }

    }

    public JournalEntry[] getInput() {

        JournalViewerContentProvider contentProvider = (JournalViewerContentProvider)tableViewer.getContentProvider();
        return contentProvider.getInput();
    }
}
