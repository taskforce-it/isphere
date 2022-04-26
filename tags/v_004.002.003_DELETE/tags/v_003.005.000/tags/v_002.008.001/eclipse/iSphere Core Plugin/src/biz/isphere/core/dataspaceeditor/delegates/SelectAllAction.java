/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import org.eclipse.ui.actions.ActionFactory;


public class SelectAllAction extends AbstractEditorAction {

    public SelectAllAction() {
        super(ActionFactory.SELECT_ALL.getId());
    }

    @Override
    public void run() {
        activeEditor.doSelectAll();
    }

    @Override
    public boolean isEnabled() {
        return activeEditor.canSelectAll();
    }
}
