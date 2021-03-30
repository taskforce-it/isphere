/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.externalapi;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.journalexplorer.core.internals.JournalExplorerHelper;
import biz.isphere.journalexplorer.core.model.JsonFile;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.model.api.JrneToRtv;
import biz.isphere.journalexplorer.core.model.shared.Journal;
import biz.isphere.journalexplorer.core.ui.dialogs.OpenJournalJsonFileDialog;
import biz.isphere.journalexplorer.core.ui.dialogs.OpenJournalOutputFileDialog;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

/**
 * This class is the public API of the iSphere Journal Explorer. <br>
 * The following interfaces are part of it:
 * <ul>
 * <li>{@link IJournaledObject}</li>
 * <li>{@link ISelectionCriteria}</li>
 * </ul>
 */
public class Access extends AbstractAccess {

    /**
     * Opens the iSphere Journal Explorer for a given list of journals.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name that overwrites the connection
     *        name stored in the import file.
     * @param libraryName - name of the library that contains the journal.
     * @param journalName - name of the journal that contains the journal
     *        entries that are displayed.
     * @param selectionCriteria - selection criteria used for selecting journal
     *        entries.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String journalName,
        ISelectionCriteria selectionCriteria, boolean newTab) throws Exception {
        openJournalExplorerView(shell, connectionName, libraryName, journalName, null, selectionCriteria, newTab);
    }

    /**
     * Opens the iSphere Journal Explorer for a given list of journals.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name that overwrites the connection
     *        name stored in the import file.
     * @param libraryName - name of the library that contains the journal.
     * @param journalName - name of the journal that contains the journal
     *        entries that are displayed.
     * @param selectedObjects - list of objects for which journal entries are
     *        retrieved. Must be one of *FILE, *DTAARA or *DTAQ.
     * @param selectionCriteria - selection criteria used for selecting journal
     *        entries.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String journalName,
        IJournaledObject[] selectedObjects, ISelectionCriteria selectionCriteria, boolean newTab) throws Exception {

        JrneToRtv jrneToRtv = new JrneToRtv(new Journal(connectionName, libraryName, journalName));

        if (selectionCriteria == null) {
            throw new IllegalArgumentException("Parameter 'selectionCriteria' must not be [null]"); //$NON-NLS-1$
        }

        if (selectedObjects != null) {
            for (IJournaledObject selectedObject : selectedObjects) {
                if (!JournalExplorerHelper.isValidObjectType(selectedObject.getObjectType())) {
                    throw new IllegalArgumentException("Object type not supported: " + selectedObject.getObjectType()); //$NON-NLS-1$
                }
            }
        }

        jrneToRtv.setSelectionCriteria(selectionCriteria);
        jrneToRtv.setSelectedObjects(selectedObjects);

        JournalExplorerView.openJournal(ensureShell(shell), jrneToRtv, newTab);
    }

    /**
     * Open the iSphere Journal Explorer and presents a dialog for selecting a
     * Json file that contains the journal entries. The Json file must have been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void loadJournalEntriesFromJsonFile(Shell shell, boolean newTab) throws Exception {

        OpenJournalJsonFileDialog dialog = new OpenJournalJsonFileDialog(shell);
        if (dialog.open() == Dialog.OK) {
            String connectionName = dialog.getConnectionName();
            String jsonFile = dialog.getJsonFileName();
            String whereClause = dialog.getSqlWhere();
            openJournalExplorerView(ensureShell(shell), connectionName, jsonFile, whereClause, newTab);
        }
    }

    /**
     * Opens the iSphere Journal Explorer for a given Json file, which has been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell.
     * @param jsonFile - path of the Json file.
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void openJournalExplorerView(Shell shell, String jsonFile, String whereClause, boolean newTab) throws Exception {

        openJournalExplorerView(ensureShell(shell), null, jsonFile, whereClause, newTab);
    }

    /**
     * Opens the iSphere Journal Explorer for a given Json file, which has been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name that overwrites the connection
     *        name stored in the import file.
     * @param jsonFile - path of the Json file.
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries using native SQL on the server.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String path, String whereClause, boolean newTab) throws Exception {

        JournalExplorerView.openJournalJsonFile(ensureShell(shell), new JsonFile(connectionName, path), new SQLWhereClause(whereClause), newTab);
    }

    /**
     * Opens the iSphere Journal Explorer and presents a dialog for selecting
     * the output file that contains the journal entries. The file must have
     * been created with the <code>DSPJRN</code> command and and output format
     * one of *TYPE1, *TYPE2, *TYPE3, *TYPE4 or *TYPE5.
     * 
     * @param shell - the parent shell.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void loadJournalEntriesFromOutputFile(Shell shell, boolean newTab) throws Exception {

        OpenJournalOutputFileDialog openJournalOutputFileDialog = new OpenJournalOutputFileDialog(shell);
        openJournalOutputFileDialog.create();

        if (openJournalOutputFileDialog.open() == Window.OK) {
            String connectionName = openJournalOutputFileDialog.getConnectionName();
            String libraryName = openJournalOutputFileDialog.getLibrary();
            String fileName = openJournalOutputFileDialog.getFileName();
            String memberName = openJournalOutputFileDialog.getMemberName();
            String sqlWhereClause = openJournalOutputFileDialog.getSqlWhere();
            openJournalExplorerView(ensureShell(shell), connectionName, libraryName, fileName, memberName, sqlWhereClause, newTab);
        }
    }

    /**
     * Opens the iSphere Journal Explorer for a given output file of journal
     * entries. The file must have been created with the <code>DSPJRN</code>Ta
     * command and and output format one of *TYPE1, *TYPE2, *TYPE3, *TYPE4 or
     * *TYPE5.
     * 
     * @param shell - the parent shell.
     * @param connectioName - name of the host connection.
     * @param libraryName - name of the library that contains the file.
     * @param fileName - name of the file that stores the member that contains
     *        the exported journal entries.
     * @param memberName - name of the member that contains the exported records
     *        of journal entries.
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries using native SQL on the server.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String fileName, String memberName,
        String whereClause, boolean newTab) throws Exception {

        OutputFile outputFile = new OutputFile(connectionName, libraryName, fileName, memberName);
        SQLWhereClause sqlWhereClause = new SQLWhereClause(whereClause);

        JournalExplorerView.openJournalOutputFile(ensureShell(shell), outputFile, sqlWhereClause, newTab);
    }
}
