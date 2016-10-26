/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import org.eclipse.ui.actions.ActionFactory;

public class UndoAction extends AbstractEditorAction {

    public UndoAction() {
        super(ActionFactory.UNDO.getId());
    }

    @Override
    public void run() {
        activeEditor.doUndo();
    }

    @Override
    public boolean isEnabled() {
        return activeEditor.canUndo();
    }
}
