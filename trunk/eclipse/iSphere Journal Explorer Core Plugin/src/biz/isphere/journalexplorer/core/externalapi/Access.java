/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.externalapi;

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.ui.dialogs.OpenJournalJsonFileDialog;
import biz.isphere.journalexplorer.core.ui.dialogs.OpenJournalOutputFileDialog;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

public class Access extends AbstractAccess {

    /**
     * Open the iSphere Journal Explorer and presents a dialog for selecting a
     * Json file that contains the journal entries. The Json file must have been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell
     */
    public static void loadJournalEntriesFromJsonFile(Shell shell) throws Exception {

        OpenJournalJsonFileDialog dialog = new OpenJournalJsonFileDialog(shell);
        if (dialog.open() == Dialog.OK) {
            String connectionName = dialog.getConnectionName();
            String jsonFile = dialog.getJsonFileName();
            String whereClause = dialog.getSqlWhere();
            openJournalExplorerView(ensureShell(shell), connectionName, jsonFile, whereClause);
        }
    }

    /**
     * Opens the iSphere Journal Explorer for a given Json file, which has been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell
     * @param jsonFile - path of the Json file
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries
     */
    public static void openJournalExplorerView(Shell shell, String jsonFile, String whereClause) throws Exception {

        openJournalExplorerView(ensureShell(shell), null, jsonFile, whereClause);
    }

    /**
     * Opens the iSphere Journal Explorer for a given Json file, which has been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell
     * @param connectionName - connection name that overwrites the connection
     *        name stored in the import file
     * @param jsonFile - path of the Json file
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries using native SQL on the server
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String jsonFile, String whereClause) throws Exception {

        JournalExplorerView.openJournalJsonFile(ensureShell(shell), connectionName, new File(jsonFile), new SQLWhereClause(whereClause));
    }

    /**
     * Opens the iSphere Journal Explorer and presents a dialog for selecting
     * the output file that contains the journal entries. The file must have
     * been created with the <code>DSPJRN</code> command and and output format
     * one of *TYPE1, *TYPE2, *TYPE3, *TYPE4 or *TYPE5.
     * 
     * @param shell - the parent shell
     */
    public static void loadJournalEntriesFromOutputFile(Shell shell) throws Exception {

        OpenJournalOutputFileDialog openJournalOutputFileDialog = new OpenJournalOutputFileDialog(shell);
        openJournalOutputFileDialog.create();

        if (openJournalOutputFileDialog.open() == Window.OK) {
            String connectionName = openJournalOutputFileDialog.getConnectionName();
            String libraryName = openJournalOutputFileDialog.getLibrary();
            String fileName = openJournalOutputFileDialog.getFileName();
            String memberName = openJournalOutputFileDialog.getMemberName();
            String sqlWhereClause = openJournalOutputFileDialog.getSqlWhere();
            openJournalExplorerView(ensureShell(shell), connectionName, libraryName, fileName, memberName, sqlWhereClause);
        }
    }

    /**
     * Opens the iSphere Journal Explorer for a given output file of journal
     * entries. The file must have been created with the <code>DSPJRN</code>Ta
     * command and and output format one of *TYPE1, *TYPE2, *TYPE3, *TYPE4 or
     * *TYPE5.
     * 
     * @param shell - the parent shell
     * @param connectioName - name of the host connection
     * @param libraryName - name of the library that contains the file
     * @param fileName - name of the file that stores the member that contains
     *        the exported journal entries
     * @param memberName - name of the member that contains the exported records
     *        of journal entries
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries using native SQL on the server
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String fileName, String memberName,
        String whereClause) throws Exception {

        OutputFile outputFile = new OutputFile(connectionName, libraryName, fileName, memberName);
        SQLWhereClause sqlWhereClause = new SQLWhereClause(whereClause);

        JournalExplorerView.openJournalOutputFile(ensureShell(shell), outputFile, sqlWhereClause);
    }
}
