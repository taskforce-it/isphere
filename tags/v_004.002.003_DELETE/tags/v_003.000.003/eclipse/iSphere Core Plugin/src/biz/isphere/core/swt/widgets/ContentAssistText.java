/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.swt.widgets.contentassist.ContentAssistEditorConfiguration;

public class ContentAssistText extends SourceViewer {

    private String[] completionProposals;
    private String[] labels;
    private char autoCompletionChar;

    public ContentAssistText(Composite parent) {
        this(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
    }

    public ContentAssistText(Composite parent, int style) {
        super(parent, null, null, false, style);

        setDocument(new Document(""));
    }

    public void setAutoCompletionChar(char autoCompletionChar) {
        this.autoCompletionChar = autoCompletionChar;
        configure(new ContentAssistEditorConfiguration(this.autoCompletionChar, this.completionProposals, this.labels));
    }

    public void setContentAssistProposals(String[] completionProposals) {
        this.completionProposals = completionProposals;
        configure(new ContentAssistEditorConfiguration(this.autoCompletionChar, this.completionProposals, this.labels));
    }

    public void setContentAssistProposalsLabels(String[] labels) {
        this.labels = labels;
        configure(new ContentAssistEditorConfiguration(this.autoCompletionChar, this.completionProposals, this.labels));
    }

    public String getText() {
        return getDocument().get();
    }

    public void setText(String text) {
        getDocument().set(text);
    }

    public void setLayoutData(Object layoutData) {
        getControl().setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return getControl().getLayoutData();
    }

    public void addFocusListener(FocusListener listener) {
        getControl().addFocusListener(listener);
    }

    public void setFocus() {
        getControl().setFocus();
    }
    
    @Override
    protected void createControl(Composite parent, int styles) {
        super.createControl(parent, styles);
    }

    @Override
    public void configure(SourceViewerConfiguration configuration) {
        super.unconfigure();
        super.configure(configuration);
    }
}
