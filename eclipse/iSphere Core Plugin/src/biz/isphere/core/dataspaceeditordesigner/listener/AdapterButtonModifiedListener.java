package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;

import biz.isphere.core.dataspaceeditordesigner.gui.designer.ControlPayload;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;

public class AdapterButtonModifiedListener implements SelectionListener {

    private IWidgetModifyListener modifyListener;
    private DataSpaceEditorManager manager;

    public AdapterButtonModifiedListener(IWidgetModifyListener modifyListener) {
        this.modifyListener = modifyListener;
        manager = new DataSpaceEditorManager();
    }

    public void widgetDefaultSelected(SelectionEvent event) {
    }

    public void widgetSelected(SelectionEvent event) {
        ControlPayload payload = manager.getPayloadFromControl((Control)event.getSource());
        AbstractDWidget widget = payload.getWidget();
        DataModifiedEvent modifiedEvent = new DataModifiedEvent(widget);
        modifyListener.dataModified(modifiedEvent);
    }
}
