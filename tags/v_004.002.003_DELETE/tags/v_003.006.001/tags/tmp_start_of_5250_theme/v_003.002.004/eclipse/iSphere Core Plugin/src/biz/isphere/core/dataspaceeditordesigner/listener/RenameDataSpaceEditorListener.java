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

import biz.isphere.core.dataspaceeditordesigner.gui.dialog.DEditorRenameDialog;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class RenameDataSpaceEditorListener extends SelectionAdapter {

    private Shell shell;
    private IDialogEditor editor;

    public RenameDataSpaceEditorListener(Shell shell, IDialogEditor editor) {
        this.shell = shell;
        this.editor = editor;
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        DEditor[] editors = editor.getSelectedDataSpaceEditors();
        DEditorRenameDialog renameDDialogDialog = new DEditorRenameDialog(shell, editors[0].getName());
        if (renameDDialogDialog.open() == Dialog.OK) {
            editor.renameDataSpaceEditor(editors[0], renameDDialogDialog.getName());
        }
    }
}
