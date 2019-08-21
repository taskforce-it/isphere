/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * This splits a given CL command into tokens. By default it keeps track of the
 * number of open and close brackets and throws an error if the number of
 * brackets do not match. The parsed command must not be formatted, because the
 * class basically splits the command on spaces.
 * 
 * @author Thomas Raddatz
 */
public class CLTokenizer {

    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$
    private static final String OPEN_BRAKET = "("; //$NON-NLS-1$
    private static final String CLOSE_BRAKET = ")"; //$NON-NLS-1$

    private boolean ignoreErrors;

    private int offset;
    private String commandString;

    public CLTokenizer() {
        this(false);
    }

    public CLTokenizer(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }

    public String[] tokenizeCommand(String clCommand) throws CLTokenizerException {

        List<String> tokens = new LinkedList<String>();

        offset = 0;
        commandString = clCommand;

        String token;

        while ((token = getToken()) != null) {
            tokens.add(token);
        }

        return tokens.toArray(new String[tokens.size()]);
    }

    private String getToken() throws CLTokenizerException {

        skipWhitespaces();

        Stack<String> waitStack = new Stack<String>();

        int start = offset;
        String currentChar = null;

        while (offset < commandString.length()) {
            currentChar = commandString.substring(offset, offset + 1);

            if (waitForEndOfQuotedString(waitStack)) {
                if (isSingleQuote(currentChar)) {
                    waitStack.pop();
                }
            } else {

                if (isSingleQuote(currentChar)) {
                    waitStack.push(SINGLE_QUOTE);
                } else if (isOpenBracket(currentChar)) {
                    waitStack.push(CLOSE_BRAKET);
                } else if (isCloseBracket(currentChar)) {
                    waitStack.pop();
                }

                if (waitStack.isEmpty()) {
                    if (isSpace(currentChar)) {
                        return commandString.substring(start, offset);
                    }
                }
            }

            offset++;
        }

        if (waitStack.size() > 0) {
            if (!ignoreErrors) {
                throw new CLTokenizerException("Error parsing command string. End character [" + waitStack.peek() + "] not seen.");
            }
        }

        if (start < offset) {
            return commandString.substring(start, offset);
        }

        return null;
    }

    private boolean waitForEndOfQuotedString(Stack<String> waitStack) {

        if (waitStack.size() > 0 && isSingleQuote(waitStack.peek())) {
            return true;
        }

        return false;
    }

    private boolean isSpace(String currentChar) {
        return SPACE.equals(currentChar);
    }

    private boolean isOpenBracket(String currentChar) {
        return OPEN_BRAKET.equals(currentChar);
    }

    private boolean isCloseBracket(String currentChar) {
        return CLOSE_BRAKET.equals(currentChar);
    }

    private boolean isSingleQuote(String currentChar) {
        return SINGLE_QUOTE.equals(currentChar);
    }

    private void skipWhitespaces() {

        while (offset < commandString.length() && SPACE.equals(commandString.substring(offset, offset + 1))) {
            offset++;
        }
    }

    public static void main(String[] args) throws Exception {

        CLTokenizer tokenizer = new CLTokenizer();

        System.out.println();
        tokenizer.tokenizeCommand("CRTCLPGM LIB/PGM SRCLIB/SRCFILE *PGM 'My first ''Hello World'' program' ((*XREF) *GEN)");

        System.out.println();
        tokenizer
            .tokenizeCommand("CRTCLPGM PGM(LIB/PGM) SRCFILE(SRCLIB/SRCFILE) SRCMBR(*PGM) TEXT('My first ''Hello World'' program') OPTION((*XREF) *GEN)");
    }
}
