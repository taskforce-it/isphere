/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;

import biz.isphere.core.dataspaceeditordesigner.gui.designer.ControlPayload;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;

public class AdapterTextModifiedListener implements ModifyListener {

    IWidgetModifyListener modifyListener;
    private DataSpaceEditorManager manager;

    public AdapterTextModifiedListener(IWidgetModifyListener modifyListener) {
        this.modifyListener = modifyListener;
        manager = new DataSpaceEditorManager();
    }

    public void modifyText(ModifyEvent event) {
        ControlPayload payload = manager.getPayloadFromControl((Control)event.getSource());
        AbstractDWidget widget = payload.getWidget();
        DataModifiedEvent modifiedEvent = new DataModifiedEvent(widget);
        modifyListener.dataModified(modifiedEvent);
    }
}
