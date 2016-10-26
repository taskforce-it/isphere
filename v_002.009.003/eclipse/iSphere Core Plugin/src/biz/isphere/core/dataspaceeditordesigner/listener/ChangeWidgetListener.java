/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

import biz.isphere.core.dataspace.rse.DE;
import biz.isphere.core.dataspaceeditordesigner.gui.designer.ControlPayload;
import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class ChangeWidgetListener extends SelectionAdapter {

    private IDialogEditor editor;

    public ChangeWidgetListener(IDialogEditor editor) {
        this.editor = editor;
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            MenuItem menuItem = (MenuItem)event.getSource();
            ControlPayload payload = (ControlPayload)menuItem.getData(DE.KEY_DATA_SPACE_PAYLOAD);
            if (payload != null) {
                performChangeWidget(payload.getWidget());
            }
        }
    }

    private void performChangeWidget(AbstractDWidget widget) {

        editor.changeWidget(widget);
    }
}
