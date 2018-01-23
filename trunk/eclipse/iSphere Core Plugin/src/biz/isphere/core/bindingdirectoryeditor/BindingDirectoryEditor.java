/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import java.sql.Connection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.core.internal.RemoteObject;

import com.ibm.as400.access.AS400;

public class BindingDirectoryEditor extends EditorPart {

    public static final String ID = "biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor";

    private BindingDirectoryEditorInput input;

    @Override
    public void createPartControl(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        BindingDirectoryEntryViewer _bindingDirectoryEntryViewer = new BindingDirectoryEntryViewer(input.getLevel(), input.getAS400(),
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

    public static void openEditor(AS400 as400, Connection jdbcConnection, RemoteObject remoteObject, String mode) {

        try {

            BindingDirectoryEditorInput editorInput = new BindingDirectoryEditorInput(as400, jdbcConnection, remoteObject, mode);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, BindingDirectoryEditor.ID);

        } catch (PartInitException e) {
        }
    }
    
}
