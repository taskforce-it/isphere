/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import java.sql.Connection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objecteditor.AbstractObjectEditorInput;

import com.ibm.as400.access.AS400;

public class BindingDirectoryEditorInput extends AbstractObjectEditorInput {

    private Connection jdbcConnection;
    private String level;

    public BindingDirectoryEditorInput(AS400 as400, Connection jdbcConnection, RemoteObject remoteObject, String mode) {
        super(as400, remoteObject, mode, ISpherePlugin.IMAGE_BINDING_DIRECTORY);

        this.jdbcConnection = jdbcConnection;
        this.level = ISpherePlugin.getDefault().getIBMiRelease(as400);
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

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return null;
    }

    public String getBindingDirectory() {
        return super.getObjectName();
    }

    public Connection getJdbcConnection() {
        return jdbcConnection;
    }

    public String getLevel() {
        return level;
    }

}
