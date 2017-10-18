/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.contentassist;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ContentAssistEditorConfiguration extends SourceViewerConfiguration implements IPropertyChangeListener {

    private static final String PROPERTY_CONTENT_ASSIST_AUTOACTIVATION_DELAY = "content_assist_autoactivation_delay";
    private String[] completionProposals;
    private String[] labels;
    private boolean isAutoActivation;
    private boolean isAutoInsert;

    private ContentAssistant assistant;
    private IPreferenceStore javaEditorPreferencesStore;

    public ContentAssistEditorConfiguration(String[] completionProposals) {
        this(completionProposals, null);
    }

    public ContentAssistEditorConfiguration(String[] completionProposals, String[] labels) {

        this.completionProposals = completionProposals;
        this.labels = labels;

        javaEditorPreferencesStore = new ScopedPreferenceStore(new InstanceScope(), "org.eclipse.jdt.ui");
        javaEditorPreferencesStore.addPropertyChangeListener(this);
    }

    public void enableAutoActivation(boolean autoActivation) {
        this.isAutoActivation = autoActivation;
    }

    public void enableAutoInsert(boolean autoInsert) {
        this.isAutoInsert = autoInsert;
    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

        this.assistant = new ContentAssistant();

        // http://grepcode.com/file/repository.grepcode.com/java/eclipse.org/3.7.2/org.eclipse.ui.workbench/texteditor/3.7.0/org/eclipse/ui/texteditor/templates/TemplatePreferencePage.java#TemplatePreferencePage.EditTemplateDialog.createEditor%28org.eclipse.ui.texteditor.templates.Composite%2Cjava.lang.String%29
        // http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Feditors_contentassist.htm
        // https://wiki.eclipse.org/FAQ_How_do_I_add_Content_Assist_to_my_editor%3F
        // http://www.vogella.com/tutorials/EclipseEditors/article.html
        ContentAssistProcessor fTemplateProcessor = new ContentAssistProcessor(completionProposals, labels);
        fTemplateProcessor.enableAutoActivation(isAutoActivation);

        assistant.setContentAssistProcessor(fTemplateProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
        assistant.enableAutoActivation(isAutoActivation);
        assistant.enableAutoInsert(isAutoInsert);
        assistant.setAutoActivationDelay(getAutoActivationDelay());

        return assistant;
    }

    private int getAutoActivationDelay() {

        IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), "org.eclipse.jdt.ui");

        int delay = store.getInt(PROPERTY_CONTENT_ASSIST_AUTOACTIVATION_DELAY);
        if (delay > 0) {
            return delay;
        }

        return 500;
    }

    public void propertyChange(PropertyChangeEvent event) {

        if (!PROPERTY_CONTENT_ASSIST_AUTOACTIVATION_DELAY.equals(event.getProperty())) {
            return;
        }

        assistant.setAutoActivationDelay(getAutoActivationDelay());
    }

    public void unistall() {
        javaEditorPreferencesStore.removePropertyChangeListener(this);
    }
}
