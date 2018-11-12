/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.dialog;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.Messages;

public class DEditorRenameDialog extends AbstractDialog {

    private String dialogName;

    private Text textName;

    public DEditorRenameDialog(Shell parentShell, String name) {
        super(parentShell);
        dialogName = name;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Rename_Editor);
    }

    @Override
    protected void createContent(Composite parent) {

        // Name
        textName = createTextField(parent, Messages.Name_colon);
        textName.setText(dialogName);
        textName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateName();
            }
        });
    }

    public String getName() {
        return dialogName;
    }

    @Override
    protected boolean validate() {

        // Name
        if (!validateName()) {
            return false;
        }

        return true;
    }

    private boolean validateName() {

        if (StringHelper.isNullOrEmpty(textName.getText())) {
            setErrorMessage(textName, "Name is missing. Please specify a name.");
            return false;
        }

        clearErrorMessage(textName);
        return true;
    }

    @Override
    protected void performOKPressed() {

        dialogName = textName.getText();
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return new Point(280, 115);
    }
}
