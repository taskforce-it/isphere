/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.externalapi.IJournaledObject;
import biz.isphere.journalexplorer.core.externalapi.ISelectionCriteria;
import biz.isphere.journalexplorer.core.internals.JournalExplorerHelper;
import biz.isphere.journalexplorer.core.internals.QualifiedName;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.model.dao.JournalDAO;
import biz.isphere.journalexplorer.core.model.shared.Journal;

public class JournalExplorerJournalInput extends AbstractJournalExplorerInput {

    private static final String INPUT_TYPE = "journal://"; //$NON-NLS-1$

    private QualifiedName journal;
    private JrneToRtv jrneToRtv;

    public JournalExplorerJournalInput(String connectionName, String libraryName, String journalName) {
        this(connectionName, libraryName, journalName, new SQLWhereClause());
    }

    public JournalExplorerJournalInput(String connectionName, String libraryName, String journalName, SQLWhereClause whereClause) {
        super(whereClause);

        journal = new QualifiedName(connectionName, libraryName, journalName);
        Journal aJournal = new Journal(journal);
        jrneToRtv = new JrneToRtv(aJournal);
    }

    public void setSelectionCriteria(ISelectionCriteria selectionCriteria) {

        if (selectionCriteria == null) {
            return;
        }

        jrneToRtv.setSelectionCriteria(selectionCriteria);
    }

    public void addObjects(IJournaledObject[] selectedObjects) {

        if (selectedObjects == null) {
            return;
        }

        if (selectedObjects != null) {
            for (IJournaledObject selectedObject : selectedObjects) {
                if (!JournalExplorerHelper.isValidObjectType(selectedObject.getObjectType())) {
                    throw new IllegalArgumentException("Object type not supported: " + selectedObject.getObjectType()); //$NON-NLS-1$
                }
            }
        }

        jrneToRtv.setSelectedObjects(selectedObjects);
    }

    @Override
    public String getName() {
        return QualifiedName.getName(journal.getLibraryName(), journal.getObjectName());
    }

    @Override
    public String getToolTipText() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(Messages.bind(Messages.Title_Connection_A, journal.getConnectionName()));
        buffer.append("\n");
        buffer.append(Messages.bind(Messages.Title_Journal_A, getName()));

        return buffer.toString();
    }

    @Override
    public String getContentId() {
        return INPUT_TYPE + journal.getQualifiedName();
    }

    @Override
    public JournalEntries load(IProgressMonitor monitor) throws Exception {

        JournalDAO journalDAO = new JournalDAO(jrneToRtv);
        JournalEntries data = journalDAO.load(getWhereClause(), monitor);

        return data;
    }

}
