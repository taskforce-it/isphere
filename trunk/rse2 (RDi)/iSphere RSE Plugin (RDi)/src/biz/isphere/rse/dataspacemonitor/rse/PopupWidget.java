package biz.isphere.rse.dataspacemonitor.rse;

import org.eclipse.swt.events.SelectionListener;

import biz.isphere.core.dataspacemonitor.rse.AbstractPopupWidget;
import biz.isphere.core.internal.IControlDecoration;

public class PopupWidget extends AbstractPopupWidget {

    private IControlDecoration decorator;

    public PopupWidget(IControlDecoration decorator) {
        super();
        this.decorator = decorator;
    }

    @Override
    protected SelectionListener createChangeWatchingListener() {
        return new ChangeWatchingListener(decorator);
    }

    @Override
    protected boolean isVisible() {
        return decorator.isVisible();
    }

}
