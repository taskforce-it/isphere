/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.dialog;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.TracedItemsValidationStatus;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractDialog extends XDialog {

    private StatusLineManager statusLineManager;
    private TracedItemsValidationStatus validatedControls;
    private Button okButton;
    private boolean showError;

    protected AbstractDialog(Shell parentShell) {
        super(parentShell);
        validatedControls = new TracedItemsValidationStatus();
        showError = false;
    }

    @Override
    public void create() {
        super.create();

        setInitialValues();

        validate();
        showError = true;
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = createMainArea(parent);

        createContent(mainArea);

        statusLineManager = createStatusLineManager(mainArea);

        return dialogArea;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    protected Text createNameField(Composite parent, String label) {
        // Text textField = createTextField(parent, label);
        // textField.setTextLimit(10);
        createLabel(parent, label);

        Text textField = WidgetFactory.createText(parent);
        textField.setTextLimit(10);
        textField.setLayoutData(createTextFieldLayoutData());
        return textField;
    }

    protected Text createTextField(Composite parent, String label) {
        createLabel(parent, label);

        Text textField = WidgetFactory.createText(parent);
        textField.setLayoutData(createTextFieldLayoutData());

        return textField;
    }

    protected GridData createTextFieldLayoutData() {
        GridData textNameLayoutData = new GridData();
        textNameLayoutData.widthHint = 150;
        textNameLayoutData.horizontalAlignment = SWT.FILL;
        textNameLayoutData.grabExcessHorizontalSpace = true;
        return textNameLayoutData;
    }

    protected Combo createComboField(Composite parent, String label, boolean isReadOnly) {
        createLabel(parent, label);

        Combo combo;
        if (isReadOnly) {
            combo = WidgetFactory.createReadOnlyCombo(parent);
        } else {
            combo = WidgetFactory.createCombo(parent);
        }

        combo.setLayoutData(createTextFieldLayoutData());

        return combo;
    }

    protected Text createIntegerField(Composite parent, String label) {
        // Text text = createTextField(parent, label);
        // text.addVerifyListener(new NumericOnlyVerifyListener());
        createLabel(parent, label);

        Text text = WidgetFactory.createIntegerText(parent);
        text.setLayoutData(createTextFieldLayoutData());

        return text;
    }

    protected void setInitialValues() {
    }

    @Override
    protected void okPressed() {
        if (!validate()) {
            return;
        }

        performOKPressed();

        super.okPressed();
    }

    protected Composite createMainArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        GridData mainAreaLayoutData = getLayoutData(1, SWT.FILL);
        mainArea.setLayoutData(mainAreaLayoutData);

        return mainArea;
    }

    protected StatusLineManager createStatusLineManager(Composite parent) {

        StatusLineManager statusLineManager = new StatusLineManager();
        statusLineManager.createControl(parent, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = getLayoutData(2, SWT.END);
        statusLine.setLayoutData(gridDataStatusLine);

        return statusLineManager;
    }

    protected void setErrorMessage(Control control, String errorMessage) {
        changeErrorMessage(control, errorMessage);
    }

    protected void clearErrorMessage(Control control) {
        changeErrorMessage(control, null);
    }

    private void createLabel(Composite parent, String label) {
        Label labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText(label);
    }

    private void changeErrorMessage(Control control, String errorMessage) {
        if (errorMessage != null) {
            if (showError) {
                statusLineManager.setErrorMessage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ERROR), errorMessage);
            }
            validatedControls.addOrUpdateItemStatus(control, false);
        } else {
            statusLineManager.setErrorMessage(null, null);
            validatedControls.addOrUpdateItemStatus(control, true);
        }

        okButton.setEnabled(validatedControls.isValid());
    }

    private GridData getLayoutData(int columns, int verticalAlignment) {
        GridData layoutData = new GridData(SWT.FILL, verticalAlignment, true, true);
        layoutData.horizontalSpan = columns;
        return layoutData;
    }

    protected abstract boolean validate();

    protected abstract void performOKPressed();

    protected abstract void createContent(Composite parent);

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
        return new Point(280, 210);
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
