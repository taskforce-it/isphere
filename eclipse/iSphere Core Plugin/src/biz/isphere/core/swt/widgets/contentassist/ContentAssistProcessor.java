/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.contentassist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private char[] autoCompletionChars;
    private boolean isAutoActivation;

    public ContentAssistProcessor(String[] completionProposals) {
        this(completionProposals, null);
    }

    public ContentAssistProcessor(String[] completionProposals, String[] labels) {

        this.completionProposals = completionProposals;
        this.autoCompletionChars = getAutoCompletionChars(completionProposals);
        this.isAutoActivation = false;

        if (completionProposals != null) {
            this.labels = new String[completionProposals.length];
            for (int i = 0; i < this.labels.length; i++) {
                if (labels != null && i < labels.length && labels[i] != null && labels[i].length() > 0) {
                    this.labels[i] = labels[i];
                } else {
                    this.labels[i] = completionProposals[i];
                }
            }
        }
    }

    public void enableAutoActivation(boolean isAutoActivation) {
        this.isAutoActivation = isAutoActivation;
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
        return autoCompletionChars;
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

        StringBuilder completionProposalPrefix = new StringBuilder();
        int pReplacementOffset = offset;
        int pReplacementLength = 0;
        if (pReplacementOffset > 0) {
            try {
                while (isAutoActivation && pReplacementOffset > 0) {
                    char charToTest = viewer.getDocument().get(pReplacementOffset - 1, 1).toUpperCase().toCharArray()[0];
                    pReplacementOffset--;
                    pReplacementLength += 1;
                    completionProposalPrefix.append(charToTest);
                    if (isAutoCompletionChar(charToTest)) {
                        break;
                    }
                }
            } catch (BadLocationException e) {
            }
        }

        completionProposalPrefix.reverse();

        collectCompletionProposals(completionProposalsList, pReplacementOffset, pReplacementLength, completionProposalPrefix.toString());
        if (completionProposalsList.size() == 0) {
            pReplacementOffset = offset;
            pReplacementLength = 0;
            collectCompletionProposals(completionProposalsList, offset, 0, null);
        }

        return completionProposalsList.toArray(new ICompletionProposal[completionProposalsList.size()]);
    }

    public boolean isAutoCompletionChar(char charToTest) {

        final String EATABLE_CHARS = new String(autoCompletionChars);

        if (EATABLE_CHARS.indexOf(charToTest) >= 0) {
            return true;
        }

        return false;
    }

    private void collectCompletionProposals(List<ICompletionProposal> completionProposalsList, int replacementOffset, int replacementLength,
        String completionProposalPrefix) {

        for (int i = 0; i < completionProposals.length; i++) {
            if (completionProposalPrefix == null || completionProposals[i].startsWith(completionProposalPrefix.toString())) {
                CompletionProposal proposal = new CompletionProposal(completionProposals[i], replacementOffset, replacementLength,
                    completionProposals[i].length(), null, labels[i], null, null);
                completionProposalsList.add(proposal);
            }
        }
    }

    public char[] getAutoCompletionChars(String[] completionProposals) {

        Set<String> autoCompletionChars = new HashSet<String>();

        if (completionProposals != null) {
            for (int i = 0; i < completionProposals.length; i++) {
                if (completionProposals[i] != null && completionProposals[i].length() > 0) {
                    String autoCompletionChar = completionProposals[i].substring(0, 1);
                    autoCompletionChars.add(autoCompletionChar.toLowerCase());
                    autoCompletionChars.add(autoCompletionChar.toUpperCase());
                }
            }
        }

        String[] strings = autoCompletionChars.toArray(new String[autoCompletionChars.size()]);
        char[] chars = new char[strings.length];
        for (int i = 0; i < strings.length; i++) {
            chars[i] = strings[i].charAt(0);
        }

        return chars;
    }

}
