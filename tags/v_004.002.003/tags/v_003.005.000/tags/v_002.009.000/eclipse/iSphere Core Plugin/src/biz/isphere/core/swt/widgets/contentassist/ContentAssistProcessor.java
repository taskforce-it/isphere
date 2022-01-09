/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class ContentAssistProcessor implements IContentAssistProcessor {

    private String[] completionProposals;
    private String[] labels;
    private char autoCompletionChar;

    public ContentAssistProcessor(char autoCompletionChar, String[] completionProposals) {
        this(autoCompletionChar, completionProposals, null);
    }

    public ContentAssistProcessor(char autoCompletionChar, String[] completionProposals, String[] labels) {

        this.autoCompletionChar = autoCompletionChar;
        this.completionProposals = completionProposals;

        if (completionProposals != null) {
            this.labels = new String[completionProposals.length];
            for (int i = 0; i < this.labels.length; i++) {
                if (labels != null && i < labels.length) {
                    this.labels[i] = completionProposals[i] + " - " + labels[i];
                } else {
                    this.labels[i] = completionProposals[i];
                }
            }
        }
    }

    /**
     * Returns the reason why this content assist processor was unable to
     * produce any completion proposals or context information.
     * 
     * @return an error message or null if no error occurred
     */
    public String getErrorMessage() {
        return null;
    }

    /**
     * Returns a validator used to determine when displayed context information
     * should be dismissed. May only return null if the processor is incapable
     * of computing context information.
     * 
     * @return a context information validator, or null if the processor is
     *         incapable of computing context information
     */
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

    /**
     * Returns the characters which when entered by the user should
     * automatically trigger the presentation of context information.
     * 
     * @return the auto activation characters for presenting context information
     *         or null if no auto activation is desired
     */
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    /**
     * Returns the characters which when entered by the user should
     * automatically trigger the presentation of possible completions.
     * 
     * @return the auto activation characters for completion proposal or null if
     *         no auto activation is desired
     */
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[] { autoCompletionChar };
    }

    /**
     * Returns information about possible contexts based on the specified
     * location within the document that corresponds to the current cursor
     * position within the text viewer.
     * 
     * @param viewer - the viewer whose document is used to compute the possible
     *        contexts
     * @param offset - an offset within the document for which context
     *        information should be computed
     * @return an array of context information objects or null if no context
     *         could be found
     */
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }

    /**
     * Returns a list of completion proposals based on the specified location
     * within the document that corresponds to the current cursor position
     * within the text viewer.
     * 
     * @param viewer - the viewer whose document is used to compute the
     *        proposals
     * @param offset - an offset within the document for which completions
     *        should be computed
     * @return an array of completion proposals or null if no proposals are
     *         possible
     */
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {

        List<ICompletionProposal> completionProposalsList = new ArrayList<ICompletionProposal>();

        int pReplacementOffset = offset;
        if (pReplacementOffset > 0) {
            try {
                String autoCompChar = viewer.getDocument().get(pReplacementOffset - 1, 1);
                if (isAutoCompleteChar(autoCompChar)) {
                    pReplacementOffset--;
                }
            } catch (BadLocationException e) {
            }
        }

        for (int i = 0; i < completionProposals.length; i++) {
            CompletionProposal proposal = new CompletionProposal(completionProposals[i], pReplacementOffset, 1, completionProposals[i].length(),
                null, labels[i], null, null);
            completionProposalsList.add(proposal);
        }

        return completionProposalsList.toArray(new ICompletionProposal[completionProposalsList.size()]);
    }

    private boolean isAutoCompleteChar(String autoCompChar) {
        if (!(new String(getCompletionProposalAutoActivationCharacters()).contains(autoCompChar))) {
            return true;
        }
        return false;
    }

}
