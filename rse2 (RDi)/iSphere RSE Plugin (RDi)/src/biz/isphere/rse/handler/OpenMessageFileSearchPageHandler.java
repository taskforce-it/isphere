package biz.isphere.rse.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.search.ui.NewSearchUI;

import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.messagefilesearch.MessageFileSearchPage;

public class OpenMessageFileSearchPageHandler extends AbstractHandler implements IHandler {

    public static final String ID = "biz.isphere.rse.handler.OpenMessageFileSearchPageHandler";

    public Object execute(ExecutionEvent arg0) throws ExecutionException {

        NewSearchUI.openSearchDialog(ISphereRSEPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow(), MessageFileSearchPage.ID);

        return null;
    }

}
