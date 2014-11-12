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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.model.DTemplateEditor;

public class DEditorDialog extends XDialog {

    private DTemplateEditor dialogTemplate;

    private Text textName;

    private Spinner spinner;

    public DEditorDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.New_Editor);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        GridData mainAreaLayoutdata = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainArea.setLayoutData(mainAreaLayoutdata);

        // Name
        Label labelName = new Label(mainArea, SWT.NONE);
        labelName.setText(Messages.Name_colon);

        textName = new Text(mainArea, SWT.BORDER);
        textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        // Number of columns
        Label labelColumns = new Label(mainArea, SWT.NONE);
        labelColumns.setText(Messages.Columns_colon);

        spinner = new Spinner(mainArea, SWT.BORDER); // | SWT.READ_ONLY
        GridData gd_spinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_spinner.widthHint = 25;
        spinner.setLayoutData(gd_spinner);
        spinner.setMinimum(1);
        spinner.setMaximum(5);

        return dialogArea;
    }

    public DTemplateEditor getDialog() {
        return dialogTemplate;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    @Override
    protected void okPressed() {
        String name = textName.getText();
        dialogTemplate = new DTemplateEditor(name, spinner.getSelection());
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
