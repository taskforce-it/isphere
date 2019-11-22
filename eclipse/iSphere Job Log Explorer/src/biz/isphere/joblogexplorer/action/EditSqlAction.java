/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

public abstract class EditSqlAction extends Action {

    // private static final String IMAGE =
    // ISphereJournalExplorerCorePlugin.IMAGE_EDIT_SQL;

    public EditSqlAction(Shell shell) {
        super("Edit SQL", SWT.TOGGLE);

        // setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        // return ISphereJournalExplorerCorePlugin.getDefault().getImage(IMAGE);
        return null;
    }

    @Override
    public void run() {
        postRunAction();
    }

    protected abstract void postRunAction();
}
