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
import biz.isphere.journalexplorer.core.model.JournalExplorerJournalInput;
import biz.isphere.journalexplorer.core.model.JournalExplorerJsonFileInput;
import biz.isphere.journalexplorer.core.model.JournalExplorerOutputFileInput;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.ui.dialogs.OpenJournalJsonFileDialog;
import biz.isphere.journalexplorer.core.ui.dialogs.OpenJournalOutputFileDialog;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

/**
 * This class and the interfaces shown below build the public API of the iSphere
 * Journal Explorer. <br>
 * <ul>
 * <li>{@link IJournaledObject}</li>
 * <li>{@link ISelectionCriteria}</li>
 * </ul>
 */
public class Access extends AbstractAccess {

    /**
     * Opens the journal explorer for exploring journal entries directly loaded
     * from a given journal.
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
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String journalName,
        ISelectionCriteria selectionCriteria, boolean newTab) throws Exception {
        openJournalExplorerView(shell, connectionName, libraryName, journalName, null, selectionCriteria, newTab);
    }

    /**
     * Opens the journal explorer for exploring journal entries of a list of
     * journaled objects, which are attached to the same journal.
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
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String journalName,
        IJournaledObject[] selectedObjects, ISelectionCriteria selectionCriteria, boolean newTab) throws Exception {

        JournalExplorerJournalInput input = new JournalExplorerJournalInput(connectionName, libraryName, journalName);
        input.setSelectionCriteria(selectionCriteria);
        input.addObjects(selectedObjects);

        JournalExplorerView.openJournal(ensureShell(shell), input, newTab);
    }

    /**
     * Opens a dialog for selecting a Json file with journal entries that are
     * loaded into the journal explorer.<br>
     * The Json file must have been saved from the iSphere Journal Explorer
     * view.
     * 
     * @param shell - the parent shell.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     */
    public static void exploreJournalEntriesFromJsonFile(Shell shell, boolean newTab) throws Exception {

        OpenJournalJsonFileDialog dialog = new OpenJournalJsonFileDialog(shell);
        if (dialog.open() == Dialog.OK) {
            String connectionName = dialog.getConnectionName();
            String jsonFile = dialog.getJsonFileName();
            String whereClause = dialog.getSqlWhere();
            openJournalExplorerView(ensureShell(shell), connectionName, jsonFile, whereClause, newTab);
        }
    }

    /**
     * Opens the journal explorer for exploring journal entries exported as a PC
     * text file.<br>
     * The file must have been saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell.
     * @param connectionName - connection name that overwrites the connection
     *        name stored in the import file.
     * @param jsonFile - path of the Json file.
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries using native SQL on the server.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String path, String whereClause, boolean newTab) throws Exception {

        SQLWhereClause sqlWhereClause = new SQLWhereClause(whereClause);

        JournalExplorerJsonFileInput input = new JournalExplorerJsonFileInput(connectionName, path, sqlWhereClause);

        JournalExplorerView.openJournal(ensureShell(shell), input, newTab);
    }

    /**
     * Opens a dialog for selecting an output file with journal entries that are
     * loaded into the journal explorer.<br>
     * The file must have been created with the <code>DSPJRN</code> command and
     * and output format one of *TYPE1, *TYPE2, *TYPE3, *TYPE4 or *TYPE5.
     * 
     * @param shell - the parent shell.
     * @param newTab - specifies whether the journal explorer is opened in a new
     *        tab. (Not yet implemented. Defaults to true.)
     * @throws Exception
     */
    public static void exploreJournalEntriesFromOutputFile(Shell shell, boolean newTab) throws Exception {

        OpenJournalOutputFileDialog dialog = new OpenJournalOutputFileDialog(shell);

        if (dialog.open() == Window.OK) {
            String connectionName = dialog.getConnectionName();
            String libraryName = dialog.getLibrary();
            String fileName = dialog.getFileName();
            String memberName = dialog.getMemberName();
            String sqlWhereClause = dialog.getSqlWhere();
            openJournalExplorerView(ensureShell(shell), connectionName, libraryName, fileName, memberName, sqlWhereClause, newTab);
        }
    }

    /**
     * Opens the journal explorer for exploring journal entries stored in an
     * output file on the host.<br>
     * The file must have been created with the <code>DSPJRN</code> command and
     * and output format one of *TYPE1, *TYPE2, *TYPE3, *TYPE4 or *TYPE5.
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
     * @throws Exception
     * @see QualifiedConnectionName
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String fileName, String memberName,
        String whereClause, boolean newTab) throws Exception {

        OutputFile outputFile = new OutputFile(connectionName, libraryName, fileName, memberName);
        SQLWhereClause sqlWhereClause = new SQLWhereClause(whereClause);

        JournalExplorerOutputFileInput input = new JournalExplorerOutputFileInput(outputFile, sqlWhereClause);

        JournalExplorerView.openJournal(ensureShell(shell), input, newTab);
    }
}
