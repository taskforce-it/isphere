/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.gui.designer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import biz.isphere.core.Messages;

public class DataSpaceEditorDesignerInput implements IEditorInput {

    private String id;

    public DataSpaceEditorDesignerInput() {
        id = this.getClass().getName();
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return Messages.Data_Space_Editor_Designer_Title;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return null;
    }

    public String getToolTipText() {
        return Messages.Data_Space_Editor_Designer_Tooltip;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        DataSpaceEditorDesignerInput other = (DataSpaceEditorDesignerInput)obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}
