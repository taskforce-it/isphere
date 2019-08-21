/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.retrievebindersource;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.retrievebindersource.AbstractRetrieveBinderSourceDialog;

public class RetrieveBinderSourceDialog extends AbstractRetrieveBinderSourceDialog {

    private RetrieveBinderSourcePanel composite;

    public RetrieveBinderSourceDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void createDialogPanel(Composite parent) {

        composite = new RetrieveBinderSourcePanel(parent, SWT.NONE);
        composite.setEnableConnectionCombo(false);
        composite.setConnectionName(getConnectionName());
        composite.setSourceMember(getSourceMember());
        composite.setSourceMemberDescription(getSourceMemberDescription());
    }

    @Override
    public void setFocus() {
        composite.setFocus();
    }

    @Override
    protected void okPressed() {

        setConnectionName(composite.getConnectionName());
        setSourceFileLibrary(composite.getSourceFileLibrary());
        setSourceFile(composite.getSourceFile());
        setSourceMember(composite.getSourceMember());
        setSourceMemberDescription(composite.getSourceMemberDescription());
        setCopyToClipboard(composite.isCopyToClipboard());

        setErrorMessage(null);

        if (!composite.validate()) {
            setErrorMessage(composite.getErrorMessage());
            return;
        }

        saveScreenValues();

        composite.updateHistory();

        super.okPressed();
    }

    protected void loadScreenValues() {
        composite.loadScreenValues(getDialogSettingsManager());
    }

    protected void saveScreenValues() {
        composite.storeScreenValues(getDialogSettingsManager());
    }
}
