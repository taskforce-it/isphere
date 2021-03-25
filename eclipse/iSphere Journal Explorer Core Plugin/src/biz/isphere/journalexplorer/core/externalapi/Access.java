package biz.isphere.journalexplorer.core.externalapi;

import java.io.File;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.preferences.Preferences;
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
    public static void openJournalJsonFile(Shell shell) throws Exception {

        IFileDialog dialog = WidgetFactory.getFileDialog(shell, SWT.OPEN);
        dialog.setFilterNames(new String[] { "Json Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$
        dialog.setFilterExtensions(new String[] { "*.json", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$
        dialog.setFilterPath(Preferences.getInstance().getExportPath());
        dialog.setFileName(Preferences.getInstance().getExportFileJson());
        dialog.setOverwrite(false);
        final String importPath = dialog.open();

        if (importPath != null) {
            Preferences.getInstance().setExportPath(dialog.getFilterPath());
            Preferences.getInstance().setExportFileJson(FileHelper.getFileName(importPath));
            openJournalExplorerView(ensureShell(shell), importPath, null);
        }

    }

    /**
     * Opens the iSphere Journal Explorer for a given Json file, which has been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell
     * @param filePath - path of the Json file
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries
     */
    public static void openJournalExplorerView(Shell shell, String filePath, String whereClause) throws Exception {

        openJournalExplorerView(ensureShell(shell), null, filePath, whereClause);
    }

    /**
     * Opens the iSphere Journal Explorer for a given Json file, which has been
     * saved from the iSphere Journal Explorer view.
     * 
     * @param shell - the parent shell
     * @param connectionName - connection name that overwrites the connection
     *        name stored in the import file
     * @param filePath - path of the Json file
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries using native SQL on the server
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String filePath, String whereClause) throws Exception {

        JournalExplorerView.openJournalJsonFile(ensureShell(shell), connectionName, new File(filePath), new SQLWhereClause(whereClause));
    }

    /**
     * Opens the iSphere Journal Explorer and presents a dialog for selecting
     * the output file that contains the journal entries. The file must have
     * been created with the <code>DSPJRN</code> command and and output format
     * one of *TYPE1, *TYPE2, *TYPE3, *TYPE4 or *TYPE5.
     * 
     * @param shell - the parent shell
     */
    public static void openJournalOutputFile(Shell shell) throws Exception {

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
     * entries. The file must have been created with the <code>DSPJRN</code>
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
