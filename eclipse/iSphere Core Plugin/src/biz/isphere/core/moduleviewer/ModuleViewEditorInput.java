/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.moduleviewer;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.debugger.moduleviews.DebuggerView;

import com.ibm.as400.access.AS400;

public class ModuleViewEditorInput implements IStorageEditorInput {

    private AS400 system;
    private ModuleViewStorage[] viewStorages;
    private DebuggerView[] debuggerViews;
    private ModuleViewStorage selectedViewStorage;
    private int selectedViewIdx;

    public ModuleViewEditorInput(AS400 system, String iSphereLibrary, DebuggerView[] debuggerViews, int viewNumber) throws Exception {

        this.system = new AS400(system);
        this.debuggerViews = debuggerViews;
        this.viewStorages = new ModuleViewStorage[debuggerViews.length];

        this.selectedViewStorage = null;
        this.selectedViewIdx = -1;

        for (int i = 0; i < debuggerViews.length; i++) {
            ModuleViewStorage storage = new ModuleViewStorage(this.system, iSphereLibrary, debuggerViews[i]);
            viewStorages[i] = storage;
        }

        selectStorage(viewNumber);
    }

    public DebuggerView[] getDebuggerViews() throws CoreException {
        return debuggerViews;
    }

    public int getSelectedView() {
        return selectedViewIdx;
    }

    public void setSelectedStorage(int index) {
        selectStorage(index);
    }

    public IStorage getStorage() throws CoreException {
        return getSelectedStorage();
    }

    public boolean exists() {
        return false;
    }

    public String getName() {
        return getSelectedStorage().getName();
    }

    public String getToolTipText() {
        return getSelectedStorage().getFullQualifiedName();
    }

    public String getContentDescription() {
        return getSelectedStorage().getSystemName() + ":" + getSelectedStorage().getName() + " - " + getSelectedStorage().getDescription(); //$NON-NLS-1$ //$NON-NLS-1$
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public ImageDescriptor getImageDescriptor() {
        return ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_DISPLAY_MODULE_VIEW);
    }

    public Object getAdapter(Class arg0) {
        return null;
    }

    public boolean isSameModuleView(IEditorInput editorInput) throws CoreException {

        if (editorInput == this) {
            return true;
        }

        if (!(editorInput instanceof ModuleViewEditorInput)) {
            return false;
        }

        ModuleViewEditorInput otherEditorInput = (ModuleViewEditorInput)editorInput;

        return getName().equals(otherEditorInput.getName());
    }

    public ModuleViewEditor findEditor(IWorkbenchPage aPage) {
        if (aPage == null) {
            return null;
        }

        IEditorReference[] tEditors = aPage.getEditorReferences();
        for (IEditorReference tEditorReference : tEditors) {
            try {
                if (isSameModuleView(tEditorReference.getEditorInput())) {
                    IEditorPart tEditor = tEditorReference.getEditor(true);
                    if (tEditor instanceof ModuleViewEditor) {
                        return (ModuleViewEditor)tEditor;
                    }
                }
            } catch (Exception e) {
                ISpherePlugin.logError("Could not find 'ModuleViewEditor'.", e); //$NON-NLS-1$
            }
        }
        return null;
    }

    public void dispose() {

        if (system != null && system.isConnected()) {
            system.disconnectAllServices();
            System.out.println("Disconnected system: " + system.hashCode());
        }
    }

    private void selectStorage(int viewNumber) {

        selectedViewStorage = null;
        selectedViewIdx = -1;

        for (int i = 0; i < viewStorages.length; i++) {
            if (viewStorages[i].getViewNumber() == viewNumber) {
                selectedViewStorage = viewStorages[i];
                selectedViewIdx = i;
                break;
            }
        }
    }

    private ModuleViewStorage getSelectedStorage() {
        return selectedViewStorage;
    }
}