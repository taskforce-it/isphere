/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.action.StatusLineManager;
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
import biz.isphere.core.swt.widgets.WidgetFactory;

public class FilterDialog extends XDialog {

    private Text textFilter;
    private StatusLineManager statusLineManager;
    private String filter = null;

    public FilterDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Composite compositeFilter = new Composite(container, SWT.NONE);
        compositeFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        compositeFilter.setLayout(new GridLayout(2, false));

        Label labelFilter = new Label(compositeFilter, SWT.NONE);
        labelFilter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        labelFilter.setText(Messages.Filter_colon);

        textFilter = WidgetFactory.createText(compositeFilter);
        textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textFilter.setText("");

        statusLineManager = new StatusLineManager();
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();
        final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
        statusLine.setLayoutData(gridDataStatusLine);

        return container;
    }

    private void setErrorMessage(String errorMessage) {
        if (errorMessage != null) {
            statusLineManager.setErrorMessage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ERROR), errorMessage);
        } else {
            statusLineManager.setErrorMessage(null, null);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Specify_a_filter);
    }

    @Override
    protected void okPressed() {

        textFilter.setText(textFilter.getText().trim());

        if (!Validator.validateFile(textFilter.getText())) {
            setErrorMessage(Messages.The_value_in_field_Filter_is_not_valid);
            textFilter.setFocus();
            return;
        }

        filter = textFilter.getText();

        super.okPressed();

    }

    public String getFilter() {
        return filter;
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return false;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(Size.getSize(250), SWT.DEFAULT, true);
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
