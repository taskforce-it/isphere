/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataareaeditor;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.DataAreaEditorInput;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.rse.dataspace.rse.WrappedDataSpace;

import com.ibm.as400.access.AS400;

public class DataAreaEditor extends AbstractDataSpaceEditor {

    public static final String ID = "biz.isphere.rse.dataareaeditor.DataAreaEditor"; //$NON-NLS-1$

    @Override
    protected AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception {
        return new WrappedDataSpace(remoteObject);
    }

    /**
     * Opens the data area editor for a given data area.
     * 
     * @param anAS400 - system that hosts the data area.
     * @param aConnection - connection used to access the system.
     * @param aLibrary - library that contains the data area
     * @param aDataArea - the data area
     * @param aMode - mode, the editor is opened for. The only allowed value is
     *        {@link IEditor#EDIT}
     */
    public static void openEditor(AS400 anAS400, RemoteObject remoteObject, String aMode) {

        try {

            DataAreaEditorInput editorInput = new DataAreaEditorInput(anAS400, remoteObject, aMode);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, DataAreaEditor.ID);

        } catch (PartInitException e) {
            ISpherePlugin.logError("Failed to open data area editor", e); //$NON-NLS-1$
        }
    }
}
