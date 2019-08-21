/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.rse;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataspaceeditordesigner.gui.designer.DataSpaceEditorDesignerInput;

public class OpenDataSpaceEditorDesignerHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            DataSpaceEditorDesignerInput editorInput = new DataSpaceEditorDesignerInput();
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, AbstractDataSpaceEditorDesigner.ID);

        } catch (PartInitException e) {
            ISpherePlugin.logError("Failed to open data area editor", e);
        }
        return null;
    }

}
