/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.moduleviewer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;

import biz.isphere.core.ISpherePlugin;

public class ModuleViewEditor extends TextEditor {

    public static final String ID = "biz.isphere.core.moduleviewer.ModuleViewEditor"; //$NON-NLS-1$

    private String contentDescription;

    public ModuleViewEditor() {
        super();
        setSourceViewerConfiguration(new SourceViewerConfiguration());
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
        contentDescription = ((ModuleViewEditorInput)input).getContentDescription();
    }

    @Override
    public String getContentDescription() {
        return contentDescription;
    }

    @Override
    public Image getTitleImage() {
        return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DISPLAY_MODULE_VIEW);
    }

}
