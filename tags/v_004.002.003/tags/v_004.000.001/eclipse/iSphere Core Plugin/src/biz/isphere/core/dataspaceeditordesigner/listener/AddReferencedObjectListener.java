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

import biz.isphere.core.dataspaceeditordesigner.gui.dialog.DReferencedObjectDialog;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class AddReferencedObjectListener extends SelectionAdapter {

    private Shell shell;
    private IDialogEditor editor;
    String type;

    public AddReferencedObjectListener(Shell shell, IDialogEditor editor, String type) {
        this.shell = shell;
        this.editor = editor;
        this.type = type;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        super.widgetDefaultSelected(e);
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        DReferencedObjectDialog addDataAreaDialog = new DReferencedObjectDialog(shell, type);
        if (addDataAreaDialog.open() == Dialog.OK) {
            editor.addReferencedObjectToSelectedEditors(addDataAreaDialog.getReferencedObject());
        }
    }
}
