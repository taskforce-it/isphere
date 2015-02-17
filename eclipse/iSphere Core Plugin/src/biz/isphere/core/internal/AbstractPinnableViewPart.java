package biz.isphere.core.internal;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.internal.viewmanager.IPinnableView;

public class AbstractPinnableViewPart extends AbstractSaveableViewPart implements IPinnableView {

    public boolean isPinned() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setPinned(boolean pinned) {
        // TODO Auto-generated method stub

    }

    public String getContentId() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, String> getPinProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createPartControl(Composite parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
