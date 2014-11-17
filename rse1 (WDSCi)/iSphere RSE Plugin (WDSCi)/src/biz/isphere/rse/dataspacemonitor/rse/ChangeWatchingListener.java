package biz.isphere.rse.dataspacemonitor.rse;

import biz.isphere.core.dataspacemonitor.rse.AbstractChangeWatchingListener;
import biz.isphere.core.dataspacemonitor.rse.WatchItemManager;
import biz.isphere.core.internal.IControlDecoration;

public class ChangeWatchingListener extends AbstractChangeWatchingListener {

    private WatchItemManager watchManager;
    private IControlDecoration decorator;

    public ChangeWatchingListener(WatchItemManager watchManager, IControlDecoration decorator) {
        this.watchManager = watchManager;
        this.decorator = decorator;
    }

    @Override
    protected boolean isVisible() {
        if (decorator == null) {
            return false;
        }
        return decorator.isVisible();
    }

    @Override
    protected void setVisible(boolean visible) {

        if (visible) {
            watchManager.addControl(decorator, getControlValue(decorator.getControl()));
        } else {
            watchManager.removeControl(decorator);
        }
    }
}
