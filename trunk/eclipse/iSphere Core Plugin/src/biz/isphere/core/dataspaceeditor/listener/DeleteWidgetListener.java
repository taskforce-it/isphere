/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;

import biz.isphere.core.dataspaceeditor.DE;
import biz.isphere.core.dataspaceeditor.gui.designer.ControlPayload;
import biz.isphere.core.dataspaceeditor.model.DEditor;
import biz.isphere.core.dataspaceeditor.rse.IDialogEditor;

public class DeleteWidgetListener extends SelectionAdapter {

    private IDialogEditor editor;
    private DEditor dialog;

    public DeleteWidgetListener(IDialogEditor editor, DEditor dialog) {
        this.editor = editor;
        this.dialog = dialog;
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            MenuItem menuItem = (MenuItem)event.getSource();
            ControlPayload payload = (ControlPayload)menuItem.getData(DE.KEY_PAYLOAD);
            editor.deleteWidget(dialog, payload.getWidget());
        }
    }
}
