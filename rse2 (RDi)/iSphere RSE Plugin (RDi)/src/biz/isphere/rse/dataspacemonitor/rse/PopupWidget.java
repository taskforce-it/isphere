package biz.isphere.rse.dataspacemonitor.rse;

import org.eclipse.swt.events.SelectionListener;

import biz.isphere.core.dataspacemonitor.rse.AbstractPopupWidget;
import biz.isphere.core.dataspacemonitor.rse.WatchItemManager;
import biz.isphere.core.internal.IControlDecoration;

public class PopupWidget extends AbstractPopupWidget {

    private WatchItemManager watchManager;
    private IControlDecoration decorator;

    public PopupWidget(WatchItemManager watchManager, IControlDecoration decorator) {
        super();
        this.watchManager = watchManager;
        this.decorator = decorator;
    }

    @Override
    protected SelectionListener createChangeWatchingListener() {
        return new ChangeWatchingListener(watchManager, decorator);
    }

    @Override
    protected boolean isVisible() {
        if (decorator == null) {
            return false;
        }
        return decorator.isVisible();
    }

}
