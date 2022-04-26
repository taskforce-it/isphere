/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractJournalExplorerInput {

    private SQLWhereClause whereClause;

    public abstract String getName();

    public abstract String getToolTipText();

    public abstract String getContentId();

    public AbstractJournalExplorerInput(SQLWhereClause whereClause) {
        this.whereClause = whereClause;
    }

    public SQLWhereClause getWhereClause() {
        return whereClause;
    }

    public boolean isSameInput(AbstractJournalExplorerInput otherInput) {

        if (otherInput == null) {
            return false;
        }

        String otherContentId = otherInput.getContentId();
        String contentId = getContentId();

        if (otherContentId == null && contentId == null) {
            return true;
        }

        if (contentId == null) {
            return false;
        }

        return contentId.equals(otherContentId);
    }

    public abstract JournalEntries load(IProgressMonitor monitor) throws Exception;
}
