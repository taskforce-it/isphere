/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.swt.widgets;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;

import biz.isphere.journalexplorer.core.swt.widgets.contentassist.ContentAssistEditorConfiguration;

public class ContentAssistText extends SourceViewer {

    private String[] completionProposals;
    private String[] labels;
    private ContentAssistEditorConfiguration configuration;

    public ContentAssistText(Composite parent) {
        this(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
    }

    public ContentAssistText(Composite parent, int style) {
        super(parent, null, null, false, style);

        setDocument(new Document(""));
    }

    public void setContentAssistProposals(ContentAssistProposal[] proposals) {

        List<String> completionProposals = new LinkedList<String>();
        List<String> labels = new LinkedList<String>();
        for (ContentAssistProposal proposal : proposals) {
            completionProposals.add(proposal.getValue());
            labels.add(proposal.getLabel());
        }

        this.completionProposals = completionProposals.toArray(new String[completionProposals.size()]);
        this.labels = labels.toArray(new String[labels.size()]);
        configure(new ContentAssistEditorConfiguration(this.completionProposals, this.labels));
    }

    public void setContentAssistProposals(String[] completionProposals) {
        this.completionProposals = completionProposals;
        configure(new ContentAssistEditorConfiguration(this.completionProposals, this.labels));
    }

    public void setContentAssistProposalsLabels(String[] labels) {
        this.labels = labels;
        configure(new ContentAssistEditorConfiguration(this.completionProposals, this.labels));
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
        this.configuration = (ContentAssistEditorConfiguration)configuration;
        super.unconfigure();
        super.configure(configuration);
    }

    public boolean isDisposed() {
        return getTextWidget().isDisposed();
    }

    public void dispose() {
        if (configuration != null) {
            configuration.unistall();
            configuration = null;
        }
        super.unconfigure();
    }
}
