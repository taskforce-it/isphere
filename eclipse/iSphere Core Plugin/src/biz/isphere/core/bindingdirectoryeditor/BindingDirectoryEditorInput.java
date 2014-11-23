/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import java.io.IOException;
import java.sql.Connection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objecteditor.AbstractObjectEditorInput;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.ObjectDoesNotExistException;

public class BindingDirectoryEditorInput extends AbstractObjectEditorInput {

    private static final String OBJECT_TYPE = "BNDDIR";

    private Connection jdbcConnection;
    private String level;

    public BindingDirectoryEditorInput(AS400 as400, Connection jdbcConnection, RemoteObject remoteObject, String mode) {
        super(as400, remoteObject, mode, ISpherePlugin.IMAGE_BINDING_DIRECTORY);

        this.jdbcConnection = jdbcConnection;
        this.level = "V9R9M9";
        CharacterDataArea iSphere = new CharacterDataArea(as400, "/QSYS.LIB/QGPL.LIB/ISPHERE.DTAARA");
        try {
            String iSphereContent = iSphere.read();
            level = iSphereContent.substring(0, 6);
        } catch (AS400SecurityException e1) {
        } catch (ErrorCompletingRequestException e1) {
        } catch (IllegalObjectTypeException e1) {
        } catch (InterruptedException e1) {
        } catch (IOException e1) {
        } catch (ObjectDoesNotExistException e1) {
        }

    }

    // TODO: CMOne - remove constructor
    @Deprecated
    public BindingDirectoryEditorInput(AS400 as400, Connection jdbcConnection, String connection, String library, String bindingDirectory, String mode) {
        this(as400, jdbcConnection, new RemoteObject(connection, bindingDirectory, library, OBJECT_TYPE, ""), mode);
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

    public Connection getJDBCConnection() {
        return jdbcConnection;
    }

    public String getLevel() {
        return level;
    }

}
