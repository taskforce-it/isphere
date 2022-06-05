/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jeditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TN5250JEditorInput implements IEditorInput {

    private String id;
    private String name;
    private String toolTip;
    private Image image;

    public TN5250JEditorInput(String id, String name, String toolTip, Image image) {
        this.id = id;
        this.name = name;
        this.toolTip = toolTip;
        this.image = image;
    }

    public TN5250JEditorInput(String id, String name) {
        this.id = id;
        this.name = name;
        toolTip = "";
        image = null;
    }

    public boolean exists() {
        return false;
    }

    public Image getImage() {
        return image;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return name;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return toolTip;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TN5250JEditorInput other = (TN5250JEditorInput)obj;
        if (!id.equals(other.id)) return false;
        return true;
    }

    public String getId() {
        return id;
    }

}
