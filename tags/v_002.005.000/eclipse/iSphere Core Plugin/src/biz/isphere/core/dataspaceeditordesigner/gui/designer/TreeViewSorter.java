/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.designer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.model.DReferencedObject;

public class TreeViewSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object object1, Object object2) {
        if (object1 instanceof DEditor && object2 instanceof DReferencedObject) {
            return 1;
        } else if (object1 instanceof DReferencedObject && object2 instanceof DEditor) {
            return -1;
        }
        if (object1 instanceof DEditor) {
            DEditor dialog1 = (DEditor)object1;
            DEditor dialog2 = (DEditor)object2;
            return dialog1.compareTo(dialog2);
        } else if (object1 instanceof DReferencedObject) {
            DReferencedObject referencedObject1 = (DReferencedObject)object1;
            DReferencedObject referencedObject2 = (DReferencedObject)object2;
            return referencedObject1.compareTo(referencedObject2);
        }
        return 0;
    }

}
