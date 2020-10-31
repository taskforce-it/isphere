/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogEditor;

public class TreeViewerDoubleClickListener implements IDoubleClickListener {

    IDialogEditor editor;

    public TreeViewerDoubleClickListener(IDialogEditor editor) {
        this.editor = editor;
    }

    public void doubleClick(DoubleClickEvent event) {
        // TreeViewer viewer = (TreeViewer)event.getViewer();
        IStructuredSelection thisSelection = (IStructuredSelection)event.getSelection();
        Object selectedNode = thisSelection.getFirstElement();

        if (selectedNode instanceof DEditor) {
            editor.setDataSpaceEditor((DEditor)selectedNode);
        }
    }
}
