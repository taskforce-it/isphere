/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferences;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;

public class DoNotAskMeAgainDialog extends MessageDialog implements DoNotAskMeAgain {

    private Button doNotShowAgain;
    private String showWarningKey;

    public DoNotAskMeAgainDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType,
        String[] dialogButtonLabels, String showWarningKey) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, 0);
        this.showWarningKey = showWarningKey;
    }

    public static void openWarning(Shell parent, String showWarningKey, String message) {
        if (Preferences.getInstance().isShowWarningMessage(showWarningKey)) {
            open(WARNING, parent, Messages.Warning, message, SWT.NONE, showWarningKey);
        }
    }

    public static void openInformation(Shell parent, String showWarningKey, String message) {
        if (Preferences.getInstance().isShowWarningMessage(showWarningKey)) {
            open(INFORMATION, parent, Messages.Informational, message, SWT.NONE, showWarningKey);
        }
    }

    public static boolean openConfirm(Shell parent, String showWarningKey, String message) {
        if (Preferences.getInstance().isShowWarningMessage(showWarningKey)) {
            return open(QUESTION, parent, Messages.Confirmation, message, SWT.NONE, showWarningKey, new String[] { IDialogConstants.OK_LABEL,
                IDialogConstants.CANCEL_LABEL });
        } else {
            return true;
        }
    }
    
    public static void resetAllMessages() {
        Preferences preferences = Preferences.getInstance();
        preferences.setShowWarningMessage(WARNING_COMPARE_FILTERS_NOT_INSTALLED, true);
        preferences.setShowWarningMessage(WARNING_REMOVE_STRPREPRC_SECTIONS, true);
        preferences.setShowWarningMessage(INFORMATION_DATA_SPACE_FIND_REPLACE_INFORMATION, true);
        preferences.setShowWarningMessage(CONFIRM_REMOVE_STRPREPRC_HEADER, true);
    }

    private static boolean open(int kind, Shell parent, String title, String message, int style, String showWarningKey) {
        return open(kind, parent, title, message, style, showWarningKey, new String[] { IDialogConstants.OK_LABEL });
    }

    private static boolean open(int kind, Shell parent, String title, String message, int style, String showWarningKey, String[] buttons) {
        MessageDialog dialog = new DoNotAskMeAgainDialog(parent, title, null, message, kind, buttons, showWarningKey);
        return dialog.open() == 0;
    }

    @Override
    public boolean close() {
        if (doNotShowAgain.getSelection()) {
            Preferences.getInstance().setShowWarningMessage(showWarningKey, false);
        }
        return super.close();
    }

    @Override
    protected Control createCustomArea(Composite parent) {
        Composite customArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginTop = 5;
        customArea.setLayout(gridLayout);
        doNotShowAgain = new Button(customArea, SWT.CHECK);
        doNotShowAgain.setText(Messages.Do_not_show_this_message_again);
        return parent;
    }
}
