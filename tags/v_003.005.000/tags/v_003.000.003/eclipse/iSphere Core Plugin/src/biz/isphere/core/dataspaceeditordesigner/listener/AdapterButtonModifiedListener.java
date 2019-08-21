/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

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
