/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import org.eclipse.jface.action.Action;

import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;

public abstract class AbstractEditorAction extends Action {

    protected AbstractDataSpaceEditor activeEditor;

    public AbstractEditorAction(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Parameter 'id' must not be null."); //$NON-NLS-1$
        }
        setId(id);
    }

    public void setActiveEditor(AbstractDataSpaceEditor editor) {
        this.activeEditor = editor;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    protected boolean hasEditor() {

        if (activeEditor == null) {
            return false;
        }

        return true;
    }
}
