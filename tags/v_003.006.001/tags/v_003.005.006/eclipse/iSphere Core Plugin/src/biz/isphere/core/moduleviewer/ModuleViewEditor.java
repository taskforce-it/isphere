/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.moduleviewer;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.api.debugger.moduleviews.DebuggerView;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ModuleViewEditor extends TextEditor {

    public static final String ID = "biz.isphere.core.moduleviewer.ModuleViewEditor"; //$NON-NLS-1$

    private String contentDescription;
    private DebuggerView[] debuggerViews;

    private Combo moduleViewSelector;

    public ModuleViewEditor() {
        super();
        setSourceViewerConfiguration(new SourceViewerConfiguration());
    }

    @Override
    public void createPartControl(Composite parent) {

        Composite partComposite = new Composite(parent, SWT.NONE);
        GridLayout partLayout = new GridLayout();
        partLayout.marginWidth = 0;
        partLayout.marginHeight = 0;
        partComposite.setLayout(partLayout);
        partComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite headerComposite = new Composite(partComposite, SWT.NONE);
        GridLayout headerLayout = new GridLayout(2, false);
        partLayout.marginHeight = 0;
        partLayout.verticalSpacing = 0;
        headerComposite.setLayout(headerLayout);
        headerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label moduleViewSelectorLabel = new Label(headerComposite, SWT.NONE);
        moduleViewSelectorLabel.setText(Messages.Selected_view_colon);

        moduleViewSelector = WidgetFactory.createReadOnlyCombo(headerComposite);
        GridData moduleViewSelectorLayoutData = new GridData();
        moduleViewSelectorLayoutData.widthHint = 200;
        moduleViewSelector.setLayoutData(moduleViewSelectorLayoutData);
        moduleViewSelector.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {

                try {
                    int i = moduleViewSelector.getSelectionIndex();
                    getEditorInput().setSelectedStorage(i);
                    setInputWithNotify(getEditorInput());
                } catch (Exception e) {
                }
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {
            }
        });

        Composite editorComposite = new Composite(partComposite, SWT.BORDER);
        FillLayout editorLayout = new FillLayout();
        editorComposite.setLayout(editorLayout);
        editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        super.createPartControl(editorComposite);

        try {
            setModuleViewSelectorItems();
        } catch (CoreException e) {
        }
    }

    @Override
    public void dispose() {

        disposeEditorInput();

        super.dispose();
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {

        if (input == null) {
            disposeEditorInput();
        }

        super.doSetInput(input);

        contentDescription = getEditorInput().getContentDescription();

        setModuleViewSelectorItems();
    }

    private void setModuleViewSelectorItems() throws CoreException {

        if (moduleViewSelector == null) {
            return;
        }

        debuggerViews = getEditorInput().getDebuggerViews();
        List<String> moduleItems = new LinkedList<String>();

        for (DebuggerView debuggerView : debuggerViews) {
            moduleItems.add(debuggerView.getDescription());
        }

        moduleViewSelector.setItems(moduleItems.toArray(new String[moduleItems.size()]));
        moduleViewSelector.select(getEditorInput().getSelectedView());
    }

    public ModuleViewEditorInput getEditorInput() {
        return (ModuleViewEditorInput)super.getEditorInput();
    }

    private void disposeEditorInput() {

        ModuleViewEditorInput editorInput = getEditorInput();
        if (editorInput != null) {
            editorInput.dispose();
        }
    }

    @Override
    public String getContentDescription() {
        return contentDescription;
    }

    @Override
    public Image getTitleImage() {
        return ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_DISPLAY_MODULE_VIEW);
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }
}
