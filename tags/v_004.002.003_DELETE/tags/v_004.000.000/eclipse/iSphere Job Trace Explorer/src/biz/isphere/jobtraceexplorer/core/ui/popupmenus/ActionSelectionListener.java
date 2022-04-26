/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.popupmenus;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import biz.isphere.jobtraceexplorer.core.ui.actions.AbstractJobTraceEntryAction;

public class ActionSelectionListener extends SelectionAdapter {

    private AbstractJobTraceEntryAction action;

    public ActionSelectionListener(AbstractJobTraceEntryAction action) {
        this.action = action;
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
        action.run();
    }
}
