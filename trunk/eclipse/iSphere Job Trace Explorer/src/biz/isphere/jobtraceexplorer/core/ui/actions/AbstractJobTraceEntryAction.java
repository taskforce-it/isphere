/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractJobTraceEntryAction extends Action {

    private JobTraceEntryActionHandler jobTraceEntryActionHandler;

    public AbstractJobTraceEntryAction(Shell shell, TableViewer tableViewer) {

        this.jobTraceEntryActionHandler = new JobTraceEntryActionHandler(shell, tableViewer);
    }

    public abstract Image getImage();

    protected JobTraceEntryActionHandler getHandler() {
        return jobTraceEntryActionHandler;
    }
}
