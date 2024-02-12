/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;

public class ConfirmationMessageDialog extends MessageDialog {

    public ConfirmationMessageDialog(Shell shell, String message) {
        this(shell, Messages.Confirmation, message);
    }

    public ConfirmationMessageDialog(Shell shell, String title, String message) {
        super(shell, title, null, message, MessageDialog.CONFIRM, getButtonLabelsInternal(), 0);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(getButtonIDsInternal()[buttonId]);
    }

    protected int[] getButtonIDsInternal() {
        int[] buttonIDs = new int[] { IDialogConstants.YES_ID, IDialogConstants.YES_TO_ALL_ID, IDialogConstants.CANCEL_ID };
        return buttonIDs;
    }

    protected static String[] getButtonLabelsInternal() {
        String[] buttonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.CANCEL_LABEL };
        return buttonLabels;
    }
}
