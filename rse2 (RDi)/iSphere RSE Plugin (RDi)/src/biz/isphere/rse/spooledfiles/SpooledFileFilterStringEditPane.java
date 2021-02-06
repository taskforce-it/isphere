/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.rse.services.clientserver.messages.SystemMessage;
import org.eclipse.rse.ui.SystemWidgetHelpers;
import org.eclipse.rse.ui.filters.SystemFilterStringEditPane;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.spooledfiles.SpooledFileBaseFilterStringEditPane;

public class SpooledFileFilterStringEditPane extends SystemFilterStringEditPane {

    private SpooledFileBaseFilterStringEditPane base = new SpooledFileBaseFilterStringEditPane();
    private EditPaneModifyListener keyListener;

    public SpooledFileFilterStringEditPane(Shell shell) {
        super(shell);
    }

    @Override
    public Control createContents(Composite parent) {

        int nbrColumns = 3;
        Composite composite_prompts = SystemWidgetHelpers.createComposite(parent, nbrColumns);
        ((GridLayout)composite_prompts.getLayout()).marginWidth = 0;

        keyListener = new EditPaneModifyListener();

        base.createContents(composite_prompts, keyListener, inputFilterString);

        return composite_prompts;

    }

    @Override
    public Control getInitialFocusControl() {
        return base.getInitialFocusControl();
    }

    @Override
    protected void doInitializeFields() {
        base.doInitializeFields(inputFilterString);
    }

    @Override
    protected void resetFields() {
        boolean oldEnabledState = keyListener.isEnabled();
        try {
            keyListener.setEnabled(false);
            base.resetFields();
        } finally {
            keyListener.setEnabled(oldEnabledState);
        }
    }

    @Override
    protected boolean areFieldsComplete() {
        return base.areFieldsComplete();
    }

    @Override
    public String getFilterString() {
        return base.getFilterString();
    }

    @Override
    public SystemMessage verify() {
        return null;
    }

    @Override
    protected SystemMessage validateStringInput() {

        SystemMessage systemMessage = super.validateStringInput();
        if (systemMessage != null) {
            return systemMessage;
        }

        String errorText = base.validateInput();
        if (errorText == null) {
            return null;
        }

        fireChangeEvent(new SystemMessage("", "", "", SystemMessage.ERROR, errorText, null));
        return systemMessage;
    }

    private class EditPaneModifyListener implements ModifyListener {
        private boolean isEnabled;

        public EditPaneModifyListener() {
            this.isEnabled = true;
        }

        public boolean isEnabled() {
            return this.isEnabled;
        }

        public void setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        public void modifyText(ModifyEvent e) {
            if (isEnabled) {
                validateStringInput();
            }
        }
    }
}
