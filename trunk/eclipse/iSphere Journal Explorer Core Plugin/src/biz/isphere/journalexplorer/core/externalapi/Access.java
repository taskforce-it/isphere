package biz.isphere.journalexplorer.core.externalapi;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.externalapi.AbstractAccess;
import biz.isphere.journalexplorer.core.model.OutputFile;
import biz.isphere.journalexplorer.core.model.SQLWhereClause;
import biz.isphere.journalexplorer.core.ui.views.JournalExplorerView;

public class Access extends AbstractAccess {

    /**
     * Open the iSphere Journal Explorer for a given output file of journal
     * entries. The file may contain journal entries of *TYPE1, *TYPE2, *TYPE3,
     * *TYPE4 or *TYPE5.
     * 
     * @param shell - the parent shell
     * @param connectioName - name of the host connection
     * @param libraryName - name of the library that contains the file
     * @param fileName - name of the file that contains the member with the
     *        exported journal entries
     * @param memberName - name of the member that contains the exported records
     *        of journal entries
     * @param whereClause - SQL where clause of JO* fields for filtering the
     *        journal entries using native SQL on the server
     * @return Comparable version String.
     */
    public static void openJournalExplorerView(Shell shell, String connectionName, String libraryName, String fileName, String memberName,
        String whereClause) throws Exception {

        OutputFile outputFile = new OutputFile(connectionName, libraryName, fileName, memberName);
        SQLWhereClause sqlWhereClause = new SQLWhereClause(whereClause);

        JournalExplorerView.openJournalOutputFile(ensureShell(shell), outputFile, sqlWhereClause);
    }
}
