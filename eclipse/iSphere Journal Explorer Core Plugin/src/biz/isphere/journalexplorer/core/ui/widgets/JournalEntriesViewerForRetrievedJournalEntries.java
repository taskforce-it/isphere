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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntries;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.dao.JournalDAO;
import biz.isphere.journalexplorer.core.model.shared.JournaledObject;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.AbstractTypeViewerFactory;
import biz.isphere.journalexplorer.core.ui.model.Type2ViewerFactory;
import biz.isphere.journalexplorer.core.ui.views.JournalEntryViewerView;

/**
 * This widget is a viewer for the journal entries retrieved by the
 * <i>QjoRetrieveJournalEntries</i> API. It is used by the "Journal Explorer"
 * view when creating a tab for retrieved journal entries.
 * 
 * @see JournalEntry
 * @see JournalEntryViewerView
 */
public class JournalEntriesViewerForRetrievedJournalEntries extends AbstractJournalEntriesViewer
    implements ISelectionChangedListener, ISelectionProvider, IPropertyChangeListener {

    private String connectionName;
    private JournaledObject journaledObject;
    private Exception dataLoadException;

    public JournalEntriesViewerForRetrievedJournalEntries(CTabFolder parent, JournaledObject journaledObject) {
        super(parent, journaledObject.getQualifiedName());

        this.journaledObject = journaledObject;
        this.connectionName = journaledObject.getConnectionName();

        Preferences.getInstance().addPropertyChangeListener(this);

        initializeComponents();

        super.setText(journaledObject.getQualifiedName());
    }

    protected TableViewer createTableViewer(Composite container) {

        try {

            AbstractTypeViewerFactory factory = new Type2ViewerFactory();

            TableViewer tableViewer = factory.createTableViewer(container);
            tableViewer.addSelectionChangedListener(this);

            return tableViewer;

        } catch (Exception e) {
            MessageDialog.openError(getParent().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return null;
        }
    }

    public void openJournal() throws Exception {

        dataLoadException = null;

        Runnable loadJournalDataJob = new Runnable() {

            public void run() {

                try {

                    JournalDAO journalDAO = new JournalDAO(journaledObject);
                    JournalEntries data = journalDAO.getJournalData();

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
}
