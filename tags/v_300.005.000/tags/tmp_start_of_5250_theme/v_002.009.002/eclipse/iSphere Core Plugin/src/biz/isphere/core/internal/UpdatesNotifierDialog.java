/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class UpdatesNotifierDialog extends MessageDialog {

    private String availableVersion;
    private Button doNotShowAgainButton;

    public UpdatesNotifierDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType,
        String[] dialogButtonLabels, int defaultIndex, String availableVersion) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
        this.availableVersion = availableVersion;
    }

    protected Control createDialogArea(Composite parent) {
        Control rtnControl = super.createDialogArea(parent);

        doNotShowAgainButton = WidgetFactory.createCheckbox((Composite)rtnControl);
        doNotShowAgainButton.setText(Messages.Do_not_show_this_message_again);

        return rtnControl;
    }

    protected void buttonPressed(int buttonId) {
        if (doNotShowAgainButton.getSelection()) Preferences.getInstance().setLastVersionForUpdates(availableVersion);
        super.buttonPressed(buttonId);
    }

}
