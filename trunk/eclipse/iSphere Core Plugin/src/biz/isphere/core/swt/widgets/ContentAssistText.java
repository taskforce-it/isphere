/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import biz.isphere.core.swt.widgets.contentassist.ContentAssistEditorConfiguration;

public class ContentAssistText {

    private String[] completionProposals;
    private String[] labels;
    private ContentAssistEditorConfiguration configuration;
    private boolean isAutoActivation;
    private boolean isAutoInsert;
    private boolean isHintDisplayed;
    private String hint;
    private Color forgroundColor;

    private SourceViewer sourceViewer;
    private TextTraverseListener traverseListener;
    private boolean traverseEnabled;

    public ContentAssistText(Composite parent) {
        this(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);

        this.traverseEnabled = false;
    }

    public ContentAssistText(Composite parent, int style) {
        // super(parent, null, null, false, style);

        this.configuration = (ContentAssistEditorConfiguration)configuration;

        this.sourceViewer = new SourceViewer(parent, null, null, false, style);
        this.sourceViewer.setDocument(new Document());
        this.sourceViewer.getTextWidget().addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent paramFocusEvent) {
                updateHint();
            }

            public void focusGained(FocusEvent paramFocusEvent) {
                hideHint();
            }
        });

        this.forgroundColor = sourceViewer.getTextWidget().getForeground();
        this.isHintDisplayed = false;

        this.traverseListener = new TextTraverseListener();

        this.sourceViewer.getTextWidget().addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent event) {
                sourceViewer.getTextWidget().addTraverseListener(traverseListener);
            }

            public void focusLost(FocusEvent e) {
                sourceViewer.getTextWidget().removeTraverseListener(traverseListener);
            }

        });
    }

    public void setEnabled(boolean enabled) {
        sourceViewer.setEditable(enabled);
        sourceViewer.getControl().setEnabled(enabled);
    }

    public void setTraverseEnabled(boolean enabled) {
        this.traverseEnabled = enabled;
    }

    public void setHint(String hint) {
        this.hint = hint;
        updateHint();
    }

    private void updateHint() {

        if (hint != null && sourceViewer.getDocument().get().length() == 0) {
            showHint();
        } else {
            hideHint();
        }
    }

    private void showHint() {

        sourceViewer.getTextWidget().setText(this.hint);
        sourceViewer.getTextWidget().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        isHintDisplayed = true;
    }

    private void hideHint() {

        if (isHintDisplayed) {
            sourceViewer.getTextWidget().setText(""); //$NON-NLS-1$
            sourceViewer.getTextWidget().setForeground(forgroundColor);
            isHintDisplayed = false;
        }
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
        configure(createContentAssistConfiguration());
    }

    public void setContentAssistProposals(String[] completionProposals) {
        this.completionProposals = completionProposals;
        configure(createContentAssistConfiguration());
    }

    public void setContentAssistProposalsLabels(String[] labels) {
        this.labels = labels;
        configure(createContentAssistConfiguration());
    }

    public void enableAutoActivation(boolean autoActivation) {
        this.isAutoActivation = autoActivation;
    }

    public void enableAutoInsert(boolean autoInsert) {
        this.isAutoInsert = autoInsert;
    }

    public void doOperation(int operation) {
        sourceViewer.doOperation(operation);
    }

    public void setSelectedRange(int selectionOffset, int selectionLength) {
        sourceViewer.setSelectedRange(selectionOffset, selectionLength);
    }

    public StyledText getTextWidget() {
        return sourceViewer.getTextWidget();
    }

    public String getText() {

        if (isHintDisplayed) {
            return ""; //$NON-NLS-1$
        }

        return sourceViewer.getDocument().get();
    }

    public void setText(String text) {
        hideHint();
        sourceViewer.getDocument().set(text);
        updateHint();
    }

    public void setLayoutData(Object layoutData) {
        sourceViewer.getControl().setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return sourceViewer.getControl().getLayoutData();
    }

    public void addFocusListener(FocusListener listener) {
        sourceViewer.getControl().addFocusListener(listener);
    }

    public boolean setFocus() {
        return sourceViewer.getTextWidget().setFocus();
    }

    public void configure(SourceViewerConfiguration configuration) {
        this.configuration = (ContentAssistEditorConfiguration)configuration;
        sourceViewer.unconfigure();
        sourceViewer.configure(configuration);
    }

    private SourceViewerConfiguration createContentAssistConfiguration() {

        ContentAssistEditorConfiguration configuration = new ContentAssistEditorConfiguration(this.completionProposals, this.labels);
        configuration.enableAutoActivation(isAutoActivation);
        configuration.enableAutoInsert(isAutoInsert);

        return configuration;
    }

    public boolean isDisposed() {
        return sourceViewer.getTextWidget().isDisposed();
    }

    public void dispose() {
        if (configuration != null) {
            configuration.unistall();
            configuration = null;
        }
        sourceViewer.unconfigure();
    }

    private class TextTraverseListener implements TraverseListener {

        public void keyTraversed(TraverseEvent event) {
            if (traverseEnabled && SWT.TAB == event.keyCode) {
                event.doit = true;
            }
        }

    }
}
