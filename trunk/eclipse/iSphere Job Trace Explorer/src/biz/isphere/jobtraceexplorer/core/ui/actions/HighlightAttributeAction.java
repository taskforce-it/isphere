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

public class HighlightAttributeAction extends AbstractJobTraceEntryAction {

    private static final String IMAGE = ISphereJobTraceExplorerCorePlugin.IMAGE_HIGHLIGHT_ATTR;

    public HighlightAttributeAction(Shell shell, TableViewer tableViewer) {
        super(shell, tableViewer);

        setText(Messages.MenuItem_Highlight_attribute);
        setImageDescriptor(ISphereJobTraceExplorerCorePlugin.getDefault().getImageDescriptor(IMAGE));
    }

    public Image getImage() {
        return ISphereJobTraceExplorerCorePlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {
        getHandler().handleHighlightRowAttribute();
    }
}
