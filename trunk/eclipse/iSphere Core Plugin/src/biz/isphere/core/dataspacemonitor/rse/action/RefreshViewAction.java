package biz.isphere.core.dataspacemonitor.rse.action;

import org.eclipse.jface.action.Action;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;

public class RefreshViewAction extends Action {

    IDialogView view;

    public RefreshViewAction(IDialogView view) {
        super("Refresh");
        this.view = view;

        setToolTipText("Refresh the content of the display object");
        setImageDescriptor(ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_REFRESH));
        setEnabled(false);
    }

    @Override
    public void run() {
        view.refreshDataSynchronously();
    }

}
