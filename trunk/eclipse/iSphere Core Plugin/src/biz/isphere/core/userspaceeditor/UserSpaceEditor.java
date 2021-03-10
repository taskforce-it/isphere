/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.userspaceeditor;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.UserSpaceEditorInput;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.dataspace.WrappedDataSpace;

import com.ibm.as400.access.AS400;

public class UserSpaceEditor extends AbstractDataSpaceEditor {

    public static final String ID = "biz.isphere.core.userspaceeditor.UserSpaceEditor"; //$NON-NLS-1$

    @Override
    protected AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception {
        return new WrappedDataSpace(remoteObject);
    }

    @CMOne(info = "This method is used by CMOne")
    public static void openEditor(AS400 anAS400, RemoteObject remoteObject, String aMode) {
        if (ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), anAS400)) {
            try {
    
                UserSpaceEditorInput editorInput = new UserSpaceEditorInput(anAS400, remoteObject, aMode);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, UserSpaceEditor.ID);
    
            } catch (PartInitException e) {
                ISpherePlugin.logError("Failed to open user space editor", e); //$NON-NLS-1$
            }
        }
    }
    
    public static void openEditor(String connectionName, RemoteObject remoteObject, String aMode) {
        if (ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), connectionName)) {
            try {

                UserSpaceEditorInput editorInput = new UserSpaceEditorInput(connectionName, remoteObject, aMode);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, UserSpaceEditor.ID);

            } catch (PartInitException e) {
            }
        }
    }
    
}
