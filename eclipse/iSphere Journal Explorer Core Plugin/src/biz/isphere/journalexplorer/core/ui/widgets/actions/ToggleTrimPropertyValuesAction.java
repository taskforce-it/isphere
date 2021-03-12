/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.preferences.Preferences;

public class ToggleTrimPropertyValuesAction extends Action {

    private Preferences preferences;

    public ToggleTrimPropertyValuesAction() {
        super("Trim values", Action.AS_CHECK_BOX);

        this.preferences = Preferences.getInstance();
        setChecked(preferences.isTrimValues());

        ImageDescriptor image;

        if (isChecked()) {
            image = ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_CHECKED);
        } else {
            image = ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_UNCHECKED);
        }

        setImageDescriptor(image);
    }

    @Override
    public void run() {
        performToggleTrimOption();
    }

    private void performToggleTrimOption() {
        preferences.setTrimValues(isChecked());
    }
}
