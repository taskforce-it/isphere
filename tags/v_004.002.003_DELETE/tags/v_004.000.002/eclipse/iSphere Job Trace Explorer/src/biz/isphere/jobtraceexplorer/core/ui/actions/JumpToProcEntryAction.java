/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;

public class JumpToProcEntryAction extends AbstractJobTraceEntryAction {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_JUMP_PROC_ENTER;

    public JumpToProcEntryAction(Shell shell, TableViewer tableViewer) {
        super(shell, tableViewer);

        setText(Messages.MenuItem_Jump_to_procedure_entry);
        setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        getHandler().handleJumpToProcEntry();
    }
}
