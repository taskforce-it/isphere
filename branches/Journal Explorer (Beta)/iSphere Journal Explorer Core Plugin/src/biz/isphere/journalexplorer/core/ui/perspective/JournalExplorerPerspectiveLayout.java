package biz.isphere.journalexplorer.core.ui.perspective;

public class JournalExplorerPerspectiveLayout extends AbstractJournalExplorerPerspectiveLayout {

    protected String getRemoveSystemsViewID() {
        return "org.eclipse.rse.ui.view.systemView";//$NON-NLS-1$
    }

    @Override
    protected String getCommandLogViewID() {
        return "com.ibm.etools.iseries.rse.ui.view.cmdlog";
    }
}
