/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

public class ReadOnlyEditor extends AbstractDecoratedTextEditor implements IWorkbenchListener {

    public static final String ID = "biz.isphere.core.internal.ReadOnlyEditor"; //$NON-NLS-1$

    private IEditorPart editorPart;

    public ReadOnlyEditor() {

        PlatformUI.getWorkbench().addWorkbenchListener(this);
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);

        if (site.getPart() instanceof IEditorPart) {
            editorPart = (IEditorPart)site.getPart();
        }
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isEditorInputModifiable() {
        return false;
    }

    @Override
    public boolean isEditorInputReadOnly() {
        return true;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        return false;
    }

    public boolean preShutdown(IWorkbench workbench, boolean forced) {
        if (editorPart != null) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editorPart, false);
        }
        PlatformUI.getWorkbench().removeWorkbenchListener(this);
        return true;
    }

    public void postShutdown(IWorkbench workbench) {
    }
}
