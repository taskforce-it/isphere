/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;

import biz.isphere.joblogexplorer.action.rse.ExportToExcelAction;

public class JobLogExplorerEditorActionBarContributor extends EditorActionBarContributor {

    private StatusLineContributionItem statusLineContribution;
    private JobLogExplorerEditor activeEditorPart;

    private ExportToExcelAction exportToExcelAction;

    public JobLogExplorerEditorActionBarContributor() {
        
        this.exportToExcelAction = new ExportToExcelAction();
    }

    @Override
    public void contributeToToolBar(IToolBarManager toolBarManager) {

        toolBarManager.add(exportToExcelAction);
    }

    @Override
    public void contributeToStatusLine(IStatusLineManager statusLineManager) {

        statusLineContribution = new StatusLineContributionItem();
        statusLineManager.add(statusLineContribution);
    }

    @Override
    public void setActiveEditor(IEditorPart editorPart) {

        if (editorPart instanceof JobLogExplorerEditor) {
            activeEditorPart = (JobLogExplorerEditor)editorPart;
            activeEditorPart.setStatusLine(statusLineContribution.getStatusLine());
            activeEditorPart.updateActionsStatusAndStatusLine();
            
            exportToExcelAction.setActiveEditor(activeEditorPart);
        }
    }

}
