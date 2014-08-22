/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class BrowserEditorInput implements IEditorInput {

    private String id;
    private String name;
    private String toolTip;
    private Image image;
    private String url;

    public BrowserEditorInput(String id, String name, String toolTip, Image image, String url) {
        this.id = id;
        this.name = name;
        this.toolTip = toolTip;
        this.image = image;
        this.url = url;
    }

    public BrowserEditorInput(String id, String name) {
        this.id = id;
        this.name = name;
        toolTip = "";
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
        BrowserEditorInput other = (BrowserEditorInput)obj;
        if (!id.equals(other.id)) return false;
        return true;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

}
