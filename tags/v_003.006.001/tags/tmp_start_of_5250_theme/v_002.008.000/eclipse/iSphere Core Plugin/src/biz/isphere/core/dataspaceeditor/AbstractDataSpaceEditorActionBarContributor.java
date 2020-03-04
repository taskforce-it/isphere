/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

import biz.isphere.core.dataspaceeditor.delegates.AbstractEditorAction;
import biz.isphere.core.dataspaceeditor.delegates.CopyAction;
import biz.isphere.core.dataspaceeditor.delegates.CutAction;
import biz.isphere.core.dataspaceeditor.delegates.DeleteAction;
import biz.isphere.core.dataspaceeditor.delegates.GoToAction;
import biz.isphere.core.dataspaceeditor.delegates.PasteAction;
import biz.isphere.core.dataspaceeditor.delegates.RedoAction;
import biz.isphere.core.dataspaceeditor.delegates.SelectAllAction;
import biz.isphere.core.dataspaceeditor.delegates.UndoAction;

public abstract class AbstractDataSpaceEditorActionBarContributor extends EditorActionBarContributor {

    private AbstractDataSpaceEditor activeEditorPart;
    private StatusLineContributionItem statusLineContribution;
    private Set<AbstractEditorAction> actions;
    private boolean haveGlobalActions;

    public AbstractDataSpaceEditorActionBarContributor() {
        actions = new HashSet<AbstractEditorAction>();
    }

    @Override
    public void contributeToStatusLine(IStatusLineManager statusLineManager) {

        statusLineContribution = new StatusLineContributionItem(getStatusLineId());
        statusLineManager.add(statusLineContribution);
    }

    @Override
    public void contributeToMenu(IMenuManager menuManager) {

        // Override menu item:
        // Navigate -> Go to Location...
        IMenuManager menu = menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
        if (menu != null) {
            menu.appendToGroup("additions", addAction(new GoToAction())); //$NON-NLS-1$
        }
    }

    @Override
    public void setActiveEditor(IEditorPart editorPart) {

        if (editorPart == null) {
            return;
        }

        if (!(editorPart instanceof AbstractDataSpaceEditor)) {
            return;
        }

        if (!haveGlobalActions) {
            registerGlobalActions(editorPart);
        }

        activeEditorPart = (AbstractDataSpaceEditor)editorPart;

        for (AbstractEditorAction action : actions) {
            action.setActiveEditor(activeEditorPart);
        }

        activeEditorPart.setStatusLine(statusLineContribution.getStatusLine());
        activeEditorPart.updateActionsStatusAndStatusLine();
    }

    private void registerGlobalActions(IEditorPart editorPart) {

        IActionBars actionBars = editorPart.getEditorSite().getActionBars();

        actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), addAction(new CutAction()));
        actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), addAction(new CopyAction()));
        actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), addAction(new PasteAction()));
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), addAction(new DeleteAction()));
        actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), addAction(new RedoAction()));
        actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), addAction(new UndoAction()));
        actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), addAction(new SelectAllAction()));

        actionBars.updateActionBars();

        haveGlobalActions = true;
    }

    private AbstractEditorAction addAction(AbstractEditorAction action) {
        actions.add(action);
        System.out.println("#actions: " + actions.size() + " ("  + action.getClass().getSimpleName() + ")");
        return action;
    }

    public abstract String getStatusLineId();

    @Override
    public void dispose() {

        setActiveEditor(null);
        super.dispose();
    }
}
