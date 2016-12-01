/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.rse.perspective;

import biz.isphere.joblogexplorer.perspective.rse.AbstractJobLogExplorerPerspectiveLayout;

public class JobLogExplorerPerspectiveLayout extends AbstractJobLogExplorerPerspectiveLayout {

    protected String getRemoveSystemsViewID() {
        return "com.ibm.etools.systems.core.ui.view.systemView";//$NON-NLS-1$
    }

    @Override
    protected String getCommandLogViewID() {
        return "com.ibm.etools.iseries.core.ui.view.cmdlog";//$NON-NLS-1$
    }
}
