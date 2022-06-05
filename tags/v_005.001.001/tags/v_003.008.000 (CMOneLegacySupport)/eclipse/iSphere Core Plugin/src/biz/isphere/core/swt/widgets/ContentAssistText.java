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
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TypedListener;

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
    private CtrlSpaceKeyAdapter crtlSpaceKeyAdapter;
    private DisableEnterKeyListener disableEnterKeyAdapter;
    private UndoKeyAdapter undoKeyAdapter;

    public ContentAssistText(Composite parent) {
        this(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
    }

    public ContentAssistText(Composite parent, int style) {

        this.configuration = (ContentAssistEditorConfiguration)configuration;

        /*
         * Ugly hack to fix background issue. Without that hack the background
         * color is light-grey in Eclipse 4.6, when the style does not define a
         * vertical or horizontal scroll bar.
         */
        if (!isScrollable(style)) {
            this.sourceViewer = new SourceViewer(parent, null, null, false, style | SWT.V_SCROLL);
            this.sourceViewer.getTextWidget().getVerticalBar().setVisible(false);
        } else {
            this.sourceViewer = new SourceViewer(parent, null, null, false, style);
        }

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

        enableEnterKey(true);
        enableCrtlSpace(true);
        enableUndo(true);
    }

    private boolean isScrollable(int style) {
        return (style & (SWT.V_SCROLL | SWT.H_SCROLL)) != 0;
    }

    public boolean enableCrtlSpace(boolean enabled) {

        boolean oldEnabled;
        if (crtlSpaceKeyAdapter != null) {
            oldEnabled = true;
        } else {
            oldEnabled = false;
        }

        if (oldEnabled == enabled) {
            return oldEnabled;
        }

        if (sourceViewer != null && !sourceViewer.getTextWidget().isDisposed()) {
            if (enabled) {
                crtlSpaceKeyAdapter = new CtrlSpaceKeyAdapter();
                sourceViewer.getTextWidget().addKeyListener(crtlSpaceKeyAdapter);
            } else {
                sourceViewer.getTextWidget().removeKeyListener(crtlSpaceKeyAdapter);
                crtlSpaceKeyAdapter = null;
            }
        }

        return oldEnabled;
    }

    public boolean enableEnterKey(boolean enabled) {

        boolean oldEnabled;
        if (disableEnterKeyAdapter == null) {
            oldEnabled = true;
        } else {
            oldEnabled = false;
        }

        if (oldEnabled == enabled) {
            return oldEnabled;
        }

        if (sourceViewer != null && !sourceViewer.getTextWidget().isDisposed()) {
            if (enabled) {
                sourceViewer.getTextWidget().removeVerifyKeyListener(disableEnterKeyAdapter);
                disableEnterKeyAdapter = null;
            } else {
                disableEnterKeyAdapter = new DisableEnterKeyListener();
                sourceViewer.getTextWidget().addVerifyKeyListener(disableEnterKeyAdapter);
            }
        }

        return oldEnabled;
    }

    public boolean enableUndo(boolean enabled) {

        boolean oldEnabled;
        if (disableEnterKeyAdapter == null) {
            oldEnabled = true;
        } else {
            oldEnabled = false;
        }

        if (oldEnabled == enabled) {
            return oldEnabled;
        }

        if (sourceViewer != null && !sourceViewer.getTextWidget().isDisposed()) {
            if (enabled) {
                undoKeyAdapter = new UndoKeyAdapter();
                sourceViewer.getTextWidget().addKeyListener(undoKeyAdapter);
            } else {
                sourceViewer.getTextWidget().removeKeyListener(undoKeyAdapter);
                undoKeyAdapter = null;
            }
        }

        return oldEnabled;
    }

    public void setEnabled(boolean enabled) {
        sourceViewer.setEditable(enabled);
        sourceViewer.getTextWidget().setEnabled(enabled);
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

        sourceViewer.getDocument().set(this.hint);
        sourceViewer.getTextWidget().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
        isHintDisplayed = true;
    }

    private void hideHint() {

        if (isHintDisplayed) {
            sourceViewer.getDocument().set("");
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

        if (operation == ISourceViewer.CONTENTASSIST_PROPOSALS) {
            boolean oldEnabled = enableEnterKey(true);
            sourceViewer.doOperation(operation);
            enableEnterKey(oldEnabled);
        } else {
            sourceViewer.doOperation(operation);
        }
    }

    public void setSelectedRange(int selectionOffset, int selectionLength) {
        sourceViewer.setSelectedRange(selectionOffset, selectionLength);
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

        sourceViewer.getTextWidget().notifyListeners(SWT.Modify, new Event());
    }

    public void setToolTipText(String tooltip) {
        sourceViewer.getTextWidget().setToolTipText(tooltip);
    }

    public void setLayoutData(Object layoutData) {
        sourceViewer.getTextWidget().setLayoutData(layoutData);
    }

    public Object getLayoutData() {
        return sourceViewer.getTextWidget().getLayoutData();
    }

    public void addModifyListener(ModifyListener listener) {

        TypedListener typedListener = new TypedListener(listener);
        sourceViewer.getTextWidget().addListener(SWT.Modify, typedListener);
    }

    public void addFocusListener(FocusListener listener) {
        sourceViewer.getTextWidget().addFocusListener(listener);
    }

    public void addKeyListener(KeyListener listener) {
        sourceViewer.getTextWidget().addKeyListener(listener);
    }

    public void addVerifyKeyListener(VerifyKeyListener listener) {
        sourceViewer.getTextWidget().addVerifyKeyListener(listener);
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

    private class DisableEnterKeyListener implements VerifyKeyListener {

        public void verifyKey(VerifyEvent e) {

            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                e.doit = false;
                return;
            }
        }
    };

    private class CtrlSpaceKeyAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {

            if (!e.doit) {
                return;
            }

            if (e.stateMask == SWT.CTRL) {
                switch (e.character) {
                case ' ':
                    doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
                    break;
                }

            }
        }
    };

    private class UndoKeyAdapter extends KeyAdapter {

        public void keyReleased(KeyEvent e) {

            if (!e.doit) {
                return;
            }

            if (e.stateMask == SWT.CTRL) {
                switch (e.character) {
                case '\032':
                    doOperation(ITextOperationTarget.UNDO);
                    break;
                }

            }
        }
    };
}
