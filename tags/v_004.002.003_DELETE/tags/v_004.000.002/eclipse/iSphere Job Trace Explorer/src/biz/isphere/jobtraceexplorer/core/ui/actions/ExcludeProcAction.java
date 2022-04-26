/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
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

public class ExcludeProcAction extends AbstractJobTraceEntryAction {

    private static final String IMAGE_HIDE_PROCEDURE = ISphereJobTraceExplorerCorePlugin.IMAGE_HIDE_PROCEDURE;
    private static final String IMAGE_SHOW_PROCEDURE = ISphereJobTraceExplorerCorePlugin.IMAGE_SHOW_PROCEDURE;

    private boolean isExcluded;

    public ExcludeProcAction(Shell shell, TableViewer tableViewer, boolean isExcluded) {
        super(shell, tableViewer);

        this.isExcluded = isExcluded;

        if (!this.isExcluded) {
            setText(Messages.MenuItem_Exclude_procedure);
            setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE_HIDE_PROCEDURE));
        } else {
            setText(Messages.MenuItem_Include_procedure);
            setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE_SHOW_PROCEDURE));
        }
    }

    public Image getImage() {
        if (!this.isExcluded) {
            return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE_HIDE_PROCEDURE);
        } else {
            return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE_SHOW_PROCEDURE);
        }
    }

    @Override
    public void run() {
        if (!this.isExcluded) {
            getHandler().handleHideProc();
        } else {
            getHandler().handleShowProc();
        }
    }
}
