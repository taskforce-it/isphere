/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.objecteditor.AbstractObjectEditorInput;

import com.ibm.as400.access.AS400;

public class DataAreaEditorInput extends AbstractObjectEditorInput {

    private static final String OBJECT_TYPE = "DTAARA";
    
    public DataAreaEditorInput(AS400 anAS400, String aConnection, String aLibrary, String aDataArea, String aMode) {
        super(anAS400, aConnection, aLibrary, aDataArea, OBJECT_TYPE, aMode, ISpherePlugin.IMAGE_DATA_AREA);
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    public String getDataArea() {
        return super.getObjectName();
    }

}
