/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.dataspaceeditordesigner.gui.dialog.DEditorDialog;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DTemplateEditor;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class ChangeDataSpaceEditorPropertiesListener extends SelectionAdapter {

    private Shell shell;
    private IDialogEditor editor;

    public ChangeDataSpaceEditorPropertiesListener(Shell shell, IDialogEditor editor) {
        this.shell = shell;
        this.editor = editor;
    }

    @Override
    public void widgetSelected(SelectionEvent event) {

        DEditor[] editors = editor.getSelectedDataSpaceEditors();
        DEditorDialog renameDDialogDialog = new DEditorDialog(shell, editors[0]);
        if (renameDDialogDialog.open() == Dialog.OK) {
            DTemplateEditor template = renameDDialogDialog.getDialog();
            editor.changeDataSpaceEditorProperties(editors[0], template.getDescription(), template.getColumns());
        }
    }
}
