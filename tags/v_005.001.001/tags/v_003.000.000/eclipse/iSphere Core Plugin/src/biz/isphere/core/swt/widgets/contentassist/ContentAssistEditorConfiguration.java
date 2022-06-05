/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class ContentAssistEditorConfiguration extends SourceViewerConfiguration {

    private String[] completionProposals;
    private String[] labels;
    private char autoCompletionChar;

    private ContentAssistant assistant;

    public ContentAssistEditorConfiguration(char autoCompletionChar, String[] completionProposals) {
        this(autoCompletionChar, completionProposals, null);
    }

    public ContentAssistEditorConfiguration(char autoCompletionChar, String[] completionProposals, String[] labels) {

        this.autoCompletionChar = autoCompletionChar;
        this.completionProposals = completionProposals;
        this.labels = labels;
    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

        this.assistant = new ContentAssistant();

        // http://grepcode.com/file/repository.grepcode.com/java/eclipse.org/3.7.2/org.eclipse.ui.workbench/texteditor/3.7.0/org/eclipse/ui/texteditor/templates/TemplatePreferencePage.java#TemplatePreferencePage.EditTemplateDialog.createEditor%28org.eclipse.ui.texteditor.templates.Composite%2Cjava.lang.String%29
        // http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Feditors_contentassist.htm
        // https://wiki.eclipse.org/FAQ_How_do_I_add_Content_Assist_to_my_editor%3F
        // http://www.vogella.com/tutorials/EclipseEditors/article.html
        IContentAssistProcessor fTemplateProcessor = new ContentAssistProcessor(autoCompletionChar, completionProposals, labels);

        assistant.setContentAssistProcessor(fTemplateProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
        assistant.enableAutoActivation(true);
        assistant.enableAutoInsert(true);

        return assistant;
    }

}
