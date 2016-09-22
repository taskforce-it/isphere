/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.rse;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

public abstract class AbstractMessageFileCompareEditorActionBarContributor extends EditorActionBarContributor {

    AbstractMessageFileCompareEditor activeEditorPart;
    StatusLineContributionItem statusLineContribution;

    @Override
    public void contributeToStatusLine(IStatusLineManager statusLineManager) {

        statusLineContribution = new StatusLineContributionItem(getStatusLineId());
        statusLineManager.add(statusLineContribution);
    }

    @Override
    public void setActiveEditor(IEditorPart editorPart) {

        if (editorPart == null) {
            return;
        }

        if (activeEditorPart == editorPart) {
            return;
        }

        activeEditorPart = (AbstractMessageFileCompareEditor)editorPart;

        activeEditorPart.setStatusLine(statusLineContribution.getStatusLine());
        activeEditorPart.updateActionsStatusAndStatusLine();
    }

    public abstract String getStatusLineId();

    @Override
    public void dispose() {

        setActiveEditor(null);
        super.dispose();
    }
}
