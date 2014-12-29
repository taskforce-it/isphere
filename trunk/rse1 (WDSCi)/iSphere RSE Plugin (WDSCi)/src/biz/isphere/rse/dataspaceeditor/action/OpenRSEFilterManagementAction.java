package biz.isphere.rse.dataspaceeditor.action;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.ui.action.WorkbenchWindowActionDelegate;
import org.eclipse.jface.action.IAction;

import biz.isphere.rse.handler.OpenRSEManagementHandler;

public class OpenRSEFilterManagementAction extends WorkbenchWindowActionDelegate {

    public static final String ID = "biz.isphere.rse.dataspaceeditor.action.OpenRSEFilterManagementAction";

    public void run(IAction action) {
        try {
            OpenRSEManagementHandler handler = new OpenRSEManagementHandler();
            ExecutionEvent event = new ExecutionEvent();
            handler.execute(event);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
