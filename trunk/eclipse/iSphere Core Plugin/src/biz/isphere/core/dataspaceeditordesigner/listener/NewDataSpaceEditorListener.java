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
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class NewDataSpaceEditorListener extends SelectionAdapter {

    private Shell shell;
    private IDialogEditor editor;
    private DataSpaceEditorManager manager;

    public NewDataSpaceEditorListener(Shell shell, IDialogEditor editor) {
        this.shell = shell;
        this.editor = editor;
        this.manager = new DataSpaceEditorManager();
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        DEditorDialog newDDialogDialog = new DEditorDialog(shell);
        if (newDDialogDialog.open() == Dialog.OK) {
            DEditor newDialog = manager.createDialogFromTemplate(newDDialogDialog.getDialog());
            editor.addDataSpaceEditor(newDialog);
            editor.setDataSpaceEditor(newDialog);
        }
    }
}
