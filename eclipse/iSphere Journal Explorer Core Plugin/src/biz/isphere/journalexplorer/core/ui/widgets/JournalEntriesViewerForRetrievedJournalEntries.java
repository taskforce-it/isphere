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
import biz.isphere.core.ISpherePlugin;
import biz.isphere.journalexplorer.core.Messages;
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

    private Exception dataLoadException;

    public JournalEntriesViewerForRetrievedJournalEntries(CTabFolder parent, JrneToRtv jrneToRtv) {
        super(parent);

        this.jrneToRtv = jrneToRtv;

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

            TableViewer tableViewer = factory.createTableViewer(container);
            tableViewer.addSelectionChangedListener(this);

            return tableViewer;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error in method JournalEntriesViewerForRetrievedJournalEntries.createTableViewer() ***", e);
            MessageDialog.openError(getParent().getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return null;
        }
    }

    public void openJournal() throws Exception {

        dataLoadException = null;

        Runnable loadJournalDataJob = new Runnable() {

            public void run() {

                try {

                    JournalDAO journalDAO = new JournalDAO(jrneToRtv);
                    JournalEntries data = journalDAO.load();

                    IBMiMessage[] messages = data.getMessages();
                    if (messages.length != 0) {
                        if (isNoDataLoadedException(messages)) {
                            throw new NoJournalEntriesLoadedException(jrneToRtv.getJournalLibraryName(), jrneToRtv.getJournalName());
                        } else {
                            throw new Exception("Error loading journal entries. \n" + messages[0].getID() + ": " + messages[0].getText());
                        }
                    }

                    setInputData(data);

                } catch (Exception e) {
                    dataLoadException = e;
                }
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

        BusyIndicator.showWhile(getDisplay(), loadJournalDataJob);

        if (dataLoadException != null) {
            throw dataLoadException;
        }
    }
}
