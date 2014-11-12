/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.gui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.model.DTemplateReferencedObject;
import biz.isphere.core.internal.ISeries;

public class DReferencedObjectDialog extends XDialog {

    private String type;
    private DTemplateReferencedObject referencedObject;

    private Text textName;
    private Text textLibrary;

    public DReferencedObjectDialog(Shell parentShell, String type) {
        super(parentShell);
        this.type = type;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (ISeries.DTAARA.equals(type)) {
            newShell.setText(Messages.Assign_data_area);
        } else if (ISeries.USRSPC.equals(type)) {
            newShell.setText(Messages.Assign_user_space);
        } else {
            throw new IllegalArgumentException("Illegal 'type' value: " + type); //$NON-NLS-1$
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        GridData mainAreaLayoutData = new GridData();
        mainAreaLayoutData.horizontalAlignment = SWT.FILL;
        mainAreaLayoutData.grabExcessHorizontalSpace = true;
        mainArea.setLayoutData(mainAreaLayoutData);

        // Name
        textName = createTextField(mainArea, Messages.Name_colon);

        // Library
        textLibrary = createTextField(mainArea, Messages.Library_colon);

        return dialogArea;
    }

    private Text createTextField(Composite parent, String label) {
        Label labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText(label);

        Text textField = new Text(parent, SWT.BORDER);
        GridData textNameLayoutData = new GridData();
        textNameLayoutData.widthHint = 150;
        textNameLayoutData.horizontalAlignment = SWT.FILL;
        textNameLayoutData.grabExcessHorizontalSpace = true;
        textField.setLayoutData(textNameLayoutData);

        return textField;
    }

    public DTemplateReferencedObject getReferencedObject() {
        return referencedObject;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    @Override
    protected void okPressed() {
        String name = textName.getText();
        String library = textLibrary.getText();
        referencedObject = new DTemplateReferencedObject(name, library, type);
        super.okPressed();
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        // Point point = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        return new Point(250, 150);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }
}
