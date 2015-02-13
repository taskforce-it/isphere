/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objecteditor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.RemoteObject;

import com.ibm.as400.access.AS400;

public abstract class AbstractObjectEditorInput implements IEditorInput, IObjectEditor {

    private AS400 as400;
    private RemoteObject remoteObject;
    private String mode;
    private Image titleImage;

    public AbstractObjectEditorInput(AS400 anAS400, RemoteObject anRemoteObject, String aMode, String anImageID) {
        as400 = anAS400;
        remoteObject = anRemoteObject;
        mode = aMode;
        titleImage = ISpherePlugin.getDefault().getImageRegistry().get(anImageID);
    }

    // TODO: CMOne - remove constructor
    @Deprecated
    public AbstractObjectEditorInput(AS400 anAS400, String aConnection, String aLibrary, String anObjectName, String anObjectType, String aMode,
        String anImageID) {
        this(anAS400, new RemoteObject(aConnection, anObjectName, aLibrary, anObjectType, ""), aMode, anImageID);
    }

    public AS400 getAS400() {
        return as400;
    }

    public RemoteObject getRemoteObject() {
        return remoteObject;
    }

    public String getConnection() {
        return remoteObject.getConnectionName();
    }

    public String getObjectLibrary() {
        return remoteObject.getLibrary();
    }

    public String getObjectName() {
        return remoteObject.getName();
    }

    public Image getTitleImage() {
        return titleImage;
    }

    public String getName() {
        return remoteObject.getQuaifiedObject();
    }

    public String getToolTipText() {
        return "\\\\" + getAS400().getSystemName() + "\\QSYS.LIB\\" + getObjectLibrary() + ".LIB\\" + getObjectName() + "."
            + remoteObject.getObjectType();
    }

    public String getMode() {
        return mode;
    }

    @Override
    public int hashCode() {
        /*
         * Siehe: http://www.ibm.com/developerworks/library/j-jtp05273/
         */
        int hash = 3;
        hash = hash * 17 + remoteObject.getConnectionName().hashCode();
        hash = hash * 17 + remoteObject.getLibrary().hashCode();
        hash = hash * 17 + remoteObject.getName().hashCode();
        return hash;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IObjectEditor other = (IObjectEditor)obj;
        if (!getConnection().equals(other.getConnection()) || !getObjectLibrary().equals(other.getObjectLibrary())
            || !getObjectName().equals(other.getObjectName())) return false;
        return true;
    }

}
