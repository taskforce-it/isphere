/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.gui.designer;

import org.eclipse.jface.viewers.LabelProvider;

import biz.isphere.core.dataspaceeditor.model.DEditor;
import biz.isphere.core.dataspaceeditor.model.DReferencedObject;

public class TreeViewLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof DEditor) {
            DEditor dialog = (DEditor)element;
            return dialog.getName();
        } else if (element instanceof DReferencedObject) {
            DReferencedObject object = (DReferencedObject)element;
            return object.toString();
        }
        return super.getText(element);
    }
}
