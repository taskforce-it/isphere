package biz.isphere.rse.dataspacemonitor.rse;

import biz.isphere.core.dataspacemonitor.rse.AbstractChangeWatchingListener;
import biz.isphere.core.internal.IControlDecoration;

public class ChangeWatchingListener extends AbstractChangeWatchingListener {
    
    private IControlDecoration decorator;
    
    public ChangeWatchingListener(IControlDecoration decorator) {
        this.decorator = decorator;
    }

    @Override
    protected boolean isVisible() {
        return decorator.isVisible();
    }

    @Override
    protected void setVisible(boolean visible) {
        
        if (visible) {
            decorator.show();
        } else {
            decorator.hide();
        }
    }
}
