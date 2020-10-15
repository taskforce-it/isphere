/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;

public abstract class EditSqlAction extends Action {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_EDIT_SQL;

    public EditSqlAction(Shell shell) {
        super(Messages.MenuItem_Edit_sql, SWT.TOGGLE);

        setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        postRunAction();
    }

    protected abstract void postRunAction();
}
