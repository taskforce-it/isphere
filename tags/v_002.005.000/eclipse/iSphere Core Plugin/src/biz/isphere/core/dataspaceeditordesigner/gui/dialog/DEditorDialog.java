/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DTemplateEditor;
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

public class DEditorDialog extends AbstractDialog {

    private DTemplateEditor dialogTemplate;

    private Text textName;
    private Text textDescription;
    private Spinner spinnerColumns;

    public DEditorDialog(Shell parentShell) {
        super(parentShell);
    }

    public DEditorDialog(Shell parentShell, DEditor editor) {
        super(parentShell);
        dialogTemplate = new DTemplateEditor(editor.getName(), editor.getDescription(), editor.getColumns());
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (dialogTemplate != null) {
            newShell.setText(Messages.Properties);
        } else {
            newShell.setText(Messages.New_Editor);
        }
    }

    @Override
    protected void createContent(Composite parent) {

        // Name
        textName = createTextField(parent, Messages.Name_colon);
        if (dialogTemplate != null) {
            textName.setText(dialogTemplate.getName());
            textName.setEnabled(false);
        }
        textName.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateName();
            }
        });

        // Description
        textDescription = createTextField(parent, Messages.Description_colon);
        if (dialogTemplate != null) {
            textDescription.setText(dialogTemplate.getDescription());
        }
        textDescription.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateDescription();
            }
        });

        // Number of columns
        Label labelColumns = new Label(parent, SWT.NONE);
        labelColumns.setText(Messages.Columns_colon);

        spinnerColumns = WidgetFactory.createSpinner(parent);
        GridData gd_spinner = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_spinner.widthHint = 25;
        spinnerColumns.setLayoutData(gd_spinner);
        spinnerColumns.setMinimum(1);
        spinnerColumns.setMaximum(5);
        if (dialogTemplate != null) {
            spinnerColumns.setSelection(dialogTemplate.getColumns());
        }
        spinnerColumns.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                validateColumns();
            }
        });
    }

    public DTemplateEditor getDialog() {
        return dialogTemplate;
    }

    @Override
    protected boolean validate() {

        // Name
        if (!validateName()) {
            return false;
        }

        // Description
        if (!validateDescription()) {
            return false;
        }

        // Columns
        if (!validateColumns()) {
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

    private boolean validateDescription() {

        clearErrorMessage(textName);
        return true;
    }

    private boolean validateColumns() {

        // FIXME: spinnerColumns.getText() - available with 3.4 :-(
        // int columns = IntHelper.tryParseInt(spinnerColumns.getText(), -1);
        // if (columns < spinnerColumns.getMinimum() || columns >
        // spinnerColumns.getMaximum()) {
        // setErrorMessage(spinnerColumns, "Number of columns are out of
        // range.");
        // return false;
        // }

        clearErrorMessage(spinnerColumns);
        return true;
    }

    @Override
    protected void performOKPressed() {

        String name = textName.getText();
        String description = textDescription.getText();
        int columns = spinnerColumns.getSelection();
        dialogTemplate = new DTemplateEditor(name, description, columns);
    }
}
