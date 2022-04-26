/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.RemoteObject;

public class BindingDirectoryEditor extends EditorPart {

    public static final String ID = "biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor";

    private BindingDirectoryEditorInput input;

    @Override
    public void createPartControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        String level;
        try {
            level = input.getLevel();
        } catch (Exception e) {
            MessageDialogAsync.displayError(getSite().getShell(), Messages.Retrieving_host_release_level, ExceptionHelper.getLocalizedMessage(e));
            level = "V0R0M0";
        }

        BindingDirectoryEntryViewer _bindingDirectoryEntryViewer = new BindingDirectoryEntryViewer(level, input.getAS400(),
            input.getJdbcConnection(), input.getConnection(), input.getObjectLibrary(), input.getBindingDirectory(), input.getMode());

        _bindingDirectoryEntryViewer.createContents(container);

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        setTitleImage(((BindingDirectoryEditorInput)input).getTitleImage());
        this.input = (BindingDirectoryEditorInput)input;
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public boolean isSaveOnCloseNeeded() {
        return false;
    }

    public static void openEditor(String connectionName, RemoteObject remoteObject, String mode) throws PartInitException {
        if (ISphereHelper.checkISphereLibrary(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), connectionName)) {
            BindingDirectoryEditorInput editorInput = new BindingDirectoryEditorInput(connectionName, remoteObject, mode);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, BindingDirectoryEditor.ID);
        }
    }

}
