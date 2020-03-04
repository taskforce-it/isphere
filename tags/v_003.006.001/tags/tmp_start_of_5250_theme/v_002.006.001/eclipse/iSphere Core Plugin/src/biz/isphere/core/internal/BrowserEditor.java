/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

public class BrowserEditor extends EditorPart {

    private BrowserEditor editor;
    private BrowserEditorInput input;
    private Browser browser;

    @Override
    public void createPartControl(Composite parent) {

        parent.setLayout(new FillLayout());

        try {
            browser = new Browser(parent, SWT.NONE);
            browser.setUrl(input.getUrl());
            browser.addCloseWindowListener(new CloseWindowListener() {
                public void close(WindowEvent event) {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(editor, false);
                }
            });
        } catch (SWTError e) {
        }

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        setTitleImage(((BrowserEditorInput)input).getImage());
        this.editor = this;
        this.input = (BrowserEditorInput)input;
    }

    @Override
    public void setFocus() {
        if (browser != null) browser.setFocus();
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

}
