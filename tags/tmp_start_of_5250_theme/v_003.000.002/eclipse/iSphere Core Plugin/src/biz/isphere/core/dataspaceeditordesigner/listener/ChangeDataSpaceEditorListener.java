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

import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class ChangeDataSpaceEditorListener extends SelectionAdapter {

    private IDialogEditor editor;

    public ChangeDataSpaceEditorListener(IDialogEditor editor) {
        this.editor = editor;
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        DEditor[] editors = editor.getSelectedDataSpaceEditors();
        if (editors.length == 0) {
            return;
        }
        editor.setDataSpaceEditor(editors[0]);
    }
}
