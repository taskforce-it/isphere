/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.perspective;

public class JournalExplorerPerspectiveLayout extends AbstractJournalExplorerPerspectiveLayout {

    public static final String ID = "biz.isphere.journalexplorer.core.ui.perspective.JournalExplorerPerspectiveLayout";//$NON-NLS-1$

    protected String getRemoveSystemsViewID() {
        return "org.eclipse.rse.ui.view.systemView";//$NON-NLS-1$
    }

    @Override
    protected String getCommandLogViewID() {
        return "com.ibm.etools.iseries.rse.ui.view.cmdlog";
    }

    @Override
    protected String getPerspectiveLayoutID() {
        return ID;
    }
}
