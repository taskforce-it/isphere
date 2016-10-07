/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.rse;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DataSpaceEditorManager;
import biz.isphere.core.rse.AbstractDropRemoteObjectListerner;

public abstract class AbstractDropDataSpaceListener extends AbstractDropRemoteObjectListerner {
    private DataSpaceEditorManager manager;

    public AbstractDropDataSpaceListener(IDropObjectListener editor) {
        super(editor);

        this.manager = new DataSpaceEditorManager();
}

    public void dragOver(DropTargetEvent event) {

        event.detail = DND.DROP_NONE;

        if (event.widget instanceof DropTarget) {
            DropTarget dropTarget = (DropTarget)event.widget;
            if (dropTarget.getControl() instanceof Tree) {
                if (event.item instanceof TreeItem) {
                    TreeItem treeItem = (TreeItem)event.item;
                    if (treeItem.getData() instanceof DEditor) {
                        event.detail = DND.DROP_COPY;
                    }
                }
            } else if (dropTarget.getControl() instanceof Composite) {
                Composite composite = (Composite)dropTarget.getControl();
                if (manager.isDialogArea(composite)) {
                    event.detail = DND.DROP_COPY;
                }
            }
        }
    }
}
