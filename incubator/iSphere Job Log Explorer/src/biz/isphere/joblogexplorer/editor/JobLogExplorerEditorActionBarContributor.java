package biz.isphere.joblogexplorer.editor;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

public class JobLogExplorerEditorActionBarContributor extends EditorActionBarContributor {

    private StatusLineContributionItem statusLineContribution;
    private JobLogExplorerEditor activeEditorPart;

    @Override
    public void contributeToStatusLine(IStatusLineManager statusLineManager) {

        statusLineContribution = new StatusLineContributionItem();
        statusLineManager.add(statusLineContribution);
    }

    @Override
    public void setActiveEditor(IEditorPart editorPart) {

        if (editorPart instanceof JobLogExplorerEditor) {
            activeEditorPart = (JobLogExplorerEditor)editorPart;
            activeEditorPart.setStatusLine(statusLineContribution.getStatusLine());
            activeEditorPart.updateActionsStatusAndStatusLine();
        }
    }

}
