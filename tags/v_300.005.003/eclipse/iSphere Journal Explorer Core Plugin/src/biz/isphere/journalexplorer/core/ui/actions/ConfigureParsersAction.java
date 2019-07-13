/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.ui.dialogs.ConfigureParsersDialog;

public class ConfigureParsersAction extends Action {

    private static final String IMAGE = ISphereJournalExplorerCorePlugin.IMAGE_CONFIGURE_PARSERS;

    private Shell shell;
    private int buttonPressed;

    public ConfigureParsersAction(Shell shell) {
        super(Messages.JournalEntryView_ConfigureParsers);

        this.shell = shell;

        setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        performConfigureParsers();
    }

    public int getButtonPressed() {
        return buttonPressed;
    }

    private void performConfigureParsers() {

        ConfigureParsersDialog configureParsersDialog = new ConfigureParsersDialog(shell);
        configureParsersDialog.create();
        buttonPressed = configureParsersDialog.open();
    }
}
