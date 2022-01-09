/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.designer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DReferencedObject;

public class TreeViewContentProvider implements ITreeContentProvider {

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        return;
    }

    public Object[] getElements(Object inputElement) {
        return (DEditor[])inputElement;
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof DEditor) {
            DEditor dialog = (DEditor)parentElement;
            return dialog.getReferencedObjects();
        }
        return null;
    }

    public Object getParent(Object element) {
        if (element instanceof DReferencedObject) {
            DReferencedObject object = (DReferencedObject)element;
            return object.getParent();
        }
        return null;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof DEditor) {
            DEditor dialog = (DEditor)element;
            if (dialog.getReferencedObjects().length > 0) {
                return true;
            }
        }
        return false;
    }

}
