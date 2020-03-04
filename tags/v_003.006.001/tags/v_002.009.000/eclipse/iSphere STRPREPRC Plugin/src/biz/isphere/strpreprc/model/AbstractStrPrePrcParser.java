/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.clcommands.CLFormatter;

public abstract class AbstractStrPrePrcParser implements StrPrePrc {

    private static final int DEFAULT_WIDTH = 70;

    private static final String END_OF_LINE = ";";
    private static final String SEARCH_HEADER = "*SCAN";

    private int lineCounter;
    private boolean doPostCommands;

    private Set<String> modeTransitions;
    private Stack<String> scanModeStack;
    private String leftCommentChars;
    private String rightCommentChars;
    private Mode mode;

    private int rightCommentPosition;
    private String indent;

    public AbstractStrPrePrcParser() {
    }

    protected void parseLines(List<String> textLines) {

        initializeParser();

        String line;
        int width = -1;
        Iterator<String> textIterator = textLines.iterator();
        while (mode != Mode.STOP && (line = readLine(textIterator, width)) != null) {

            // String line = textIterator.next();

            String tag = retrieveTag(line);
            if (tag != null) {
                checkTransition(tag);
            }

            if (PRE_COMPILER_START.equals(tag)) {
                pushTagMode(PRE_COMPILER_START);
                mode = Mode.SCAN_HEADER;
                leftCommentChars = retrieveLeftCommentChars(line);
                rightCommentChars = retrieveRightCommentChars(line, line.indexOf(PRE_COMPILER_START) + PRE_COMPILER_START.length());
                if (!StringHelper.isNullOrEmpty(rightCommentChars)) {
                    rightCommentPosition = line.lastIndexOf(rightCommentChars);
                } else {
                    rightCommentPosition = 0;
                }
                indent = getIndention(line, leftCommentChars);
                storeFirstLine(getLineCounter());
            }

            if (mode == Mode.SCAN_HEADER) {
                if (WIDTH.equals(tag)) {
                    width = retrieveWidth(line);
                    storeWidth(width);
                } else if (IMPORTANT_START.equals(tag)) {
                    pushTagMode(IMPORTANT_START);
                } else if (IMPORTANT_END.equals(tag)) {
                    popTagMode(); // IMPORTANT_START
                } else if (COMPILE_START.equals(tag)) {
                    pushTagMode(COMPILE_START);
                } else if (COMPILE_END.equals(tag)) {
                    popTagMode(); // COMPILE_START
                } else if (LINK_START.equals(tag)) {
                    pushTagMode(LINK_START);
                } else if (LINK_END.equals(tag)) {
                    popTagMode(); // LINK_START
                } else if (PARAMETER.equals(tag)) {
                    String parameterString = retrieveTagValue(tag, line, width, textIterator);
                    if (parameterString != null) {
                        if (IMPORTANT_START.equals(getTagMode())) {
                            storeImportantParameter(parameterString);
                        } else if (COMPILE_START.equals(getTagMode())) {
                            storeCompileParameter(parameterString);
                        } else {
                            storeLinkParameter(parameterString);
                        }
                    }
                } else if (CREATE_COMMAND.equals(tag)) {
                    String commandString = retrieveTagValue(tag, line, width, textIterator);
                    if (commandString != null) {
                        storeCreateCommand(commandString);
                    }
                } else if (EXECUTE.equals(tag)) {
                    doPostCommands = true;
                } else if (COMMAND.equals(tag)) {
                    int start = getLineCounter();
                    String commandString = retrieveTagValue(tag, line, width, textIterator);
                    if (commandString != null) {
                        if (doPostCommands) {
                            storePostCommand(commandString, start, getLineCounter());
                        } else {
                            storePreCommand(commandString, start, getLineCounter());
                        }
                    }
                }
            }

            if (PRE_COMPILER_END.equals(tag)) {
                popTagMode(); // PRE_COMPILER_START
                mode = Mode.STOP;
                storeLastLine(getLineCounter());
            }
        }

        popTagMode(); // *SCAN
    }

    protected String[] produceHeader(CLFormatter formatter) {

        ensurePreconditions();

        List<String> textLines = new ArrayList<String>();

        // >>PRE-COMPILER<<
        produceMainTag(textLines, PRE_COMPILER_START);

        // >>WIDTH<<
        produceWidth(textLines, getWidth());

        // >>CRTCMD<<
        produceCreateCommand(textLines, getCreateCommand(formatter));

        // >>IMPORTANT<<
        produceParameters(textLines, getImportantParameters(), IMPORTANT_START, IMPORTANT_END);

        // >>COMPILE<<
        produceParameters(textLines, getCompileParameters(), COMPILE_START, COMPILE_END);

        // >>LINK<<
        produceParameters(textLines, getLinkParameters(), LINK_START, LINK_END);

        // >>CMD<< (pre-command)
        produceCreateCommands(textLines, getPreCommands(formatter));

        // >>EXECUTE<<
        produceExecute(textLines);

        // >>CMD<< (post-commands
        produceCreateCommands(textLines, getPostCommands(formatter));

        // >>END-PRE-COMPILER<<
        produceMainTag(textLines, PRE_COMPILER_END);

        return textLines.toArray(new String[textLines.size()]);
    }

    private void ensurePreconditions() {

        if (StringHelper.isNullOrEmpty(rightCommentChars)) {
            rightCommentChars = "";
        }

        if (StringHelper.isNullOrEmpty(leftCommentChars)) {
            leftCommentChars = "//*";
        }
    }

    private int ensureWidth() {

        if (getWidth() <= 0) {
            return DEFAULT_WIDTH;
        }

        return getWidth();
    }

    private void produceWidth(List<String> textLines, int width) {

        if (width <= 0) {
            return;
        }

        StringBuilder line = new StringBuilder();
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" " + "  ");
        line.append(WIDTH);
        line.append("  ");
        line.append(width);
        line.append(END_OF_LINE);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());
    }

    private void produceMainTag(List<String> textLines, String mainTag) {

        StringBuilder line = new StringBuilder();
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" ");
        line.append(mainTag);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());
    }

    private void produceCreateCommand(List<String> textLines, String commandString) {

        if (commandString == null) {
            return;
        }

        StringBuilder line = new StringBuilder();

        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" " + "  ");
        line.append(CREATE_COMMAND);
        line.append("  ");

        String[] crtCmd;
        if (ensureWidth() > 0) {
            crtCmd = splittCreateCommand(commandString, ensureWidth() - line.length());
        } else {
            crtCmd = splittCreateCommand(commandString, rightCommentPosition - 2 - line.length());
        }

        int parameterIndentPos = line.length() + 2;

        line.append(crtCmd[0]);
        line.append(getContinuationOrEndOfLineChar(crtCmd, 0));
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());

        for (int i = 1; i < crtCmd.length; i++) {
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(spaces(parameterIndentPos - line.length()));
            line.append(crtCmd[i]);
            line.append(getContinuationOrEndOfLineChar(crtCmd, i));
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());
        }

    }

    private void produceParameters(List<String> textLines, String[] parameters, String startTag, String endTag) {

        if (parameters == null || parameters.length <= 0) {
            return;
        }

        StringBuilder line = new StringBuilder();

        // >>START-*<<
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" " + "  ");
        line.append(startTag);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());

        // >>PARM<<
        for (String parameter : parameters) {
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  " + " " + " ");
            line.append(PARAMETER);
            line.append(" " + " ");
            line.append(parameter);
            line.append(END_OF_LINE);
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());
        }

        // >>END-*<<
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" " + "  ");
        line.append(endTag);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());

    }

    private void produceExecute(List<String> textLines) {

        StringBuilder line = new StringBuilder();
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" " + "  ");
        line.append(EXECUTE);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());
    }

    private void produceCreateCommands(List<String> textLines, String[] commandStrings) {

        if (commandStrings == null || commandStrings.length <= 0) {
            return;
        }

        StringBuilder line = new StringBuilder();
        for (String commandString : commandStrings) {
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  ");
            line.append(COMMAND);
            line.append(" " + " ");
            line.append(commandString);
            line.append(END_OF_LINE);
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());
        }
    }

    private void appendRightCommentChars(StringBuilder line, int rightCommentPosition) {

        if (rightCommentPosition <= line.length()) {
            return;
        }

        line.append(spaces(rightCommentPosition - line.length()));
        line.append(rightCommentChars);
    }

    private String spaces(int i) {
        return StringHelper.getFixLength("", i);
    }

    private String[] splittCreateCommand(String command, int maxLength) {

        if (maxLength <= 0) {
            maxLength = ensureWidth();
        }

        List<String> cmdLines = new ArrayList<String>();

        int startPos = 0;
        int tokenLength = 0;
        int lastDelimiterPosition = 0;

        for (int i = 0; i < command.length(); i++) {
            String currentChar = command.substring(i, i + 1);
            tokenLength++;
            if (" ".equals(currentChar)) {
                lastDelimiterPosition = i;
            }
            if (tokenLength > maxLength) {
                String token = command.substring(startPos, lastDelimiterPosition + 1);
                cmdLines.add(token.trim());
                startPos = lastDelimiterPosition + 1;
                tokenLength = i - lastDelimiterPosition;
            }
        }

        if (tokenLength > 0) {
            String token = command.substring(startPos, startPos + tokenLength);
            cmdLines.add(token.trim());
        }

        return cmdLines.toArray(new String[cmdLines.size()]);
    }

    private String getContinuationOrEndOfLineChar(String[] crtCmd, int i) {

        if (i < crtCmd.length - 1) {
            return " +";
        }

        return END_OF_LINE;
    }

    private int retrieveWidth(String line) {

        int width = -1;

        int start = line.indexOf(WIDTH);
        if (start < 0) {
            return -1;
        }

        start = start + WIDTH.length();
        start = skipWhiteSpaces(line, start);

        StringBuilder numbers = new StringBuilder();
        while (start < line.length()) {
            String currentChar = line.substring(start, start + 1);
            if ("0123456789".indexOf(currentChar) >= 0) {
                numbers.append(currentChar);
            } else {
                break;
            }
            start++;
        }

        if (numbers.length() > 0) {
            width = Integer.parseInt(numbers.toString());
        }

        return width;
    }

    private String retrieveTagValue(String tag, String line, int maxLength, Iterator<String> textIterator) {

        int start = line.indexOf(tag) + tag.length();
        start = skipWhiteSpaces(line, start);

        StringBuilder buffer = new StringBuilder();

        String continuationChar = retrieveTagValue(buffer, line, start);
        while (continuationChar != null) {
            line = readLine(textIterator, maxLength);
            if (line == null) {
                throw new RuntimeException("Premature end of file after line " + getLineCounter() + ".");
            }

            line = line.substring(start);
            if ("+".equals(continuationChar)) {
                line = StringHelper.trimL(line);
                continuationChar = retrieveTagValue(buffer, line, 0);
            } else {
                continuationChar = retrieveTagValue(buffer, line, start);
            }
        }

        return buffer.toString();
    }

    private String retrieveTagValue(StringBuilder value, String line, int start) {

        while (start < line.length()) {
            String currentChar = line.substring(start, start + 1);
            if ("+".equals(currentChar)) {
                return currentChar;
            } else if ("-".equals(currentChar)) {
                return currentChar;
            } else if (END_OF_LINE.equals(currentChar)) {
                return null;
            } else {
                value.append(currentChar);
            }
            start++;
        }

        throw new RuntimeException("The end-of-line character could not be found in line " + getLineCounter() + ".");
    }

    private String retrieveTag(String line) {

        int start = line.indexOf(">>");
        if (start < 0) {
            return null;
        }

        int end = line.lastIndexOf("<<");
        if (end < 0) {
            return null;
        }

        end = end + 2;

        String tag = line.substring(start, end);

        return tag;
    }

    private String retrieveLeftCommentChars(String line) {

        String trimmed = line.trim();
        int end = trimmed.indexOf(" ");
        if (end < 0) {
            return "";
        }

        return trimmed.substring(0, end);
    }

    private String retrieveRightCommentChars(String line, int startPos) {

        // Trim trailing spaces
        String trimmed = StringHelper.trimR(line);

        int start = trimmed.lastIndexOf(" ");
        if (start < 0) {
            return "";
        }
        
        int end = trimmed.length();

        if (start < startPos) {
            return "";
        }

        return trimmed.substring(start + 1, end);
    }

    private void initializeParser() {

        lineCounter = 0;
        modeTransitions = loadModeTransitions();
        scanModeStack = new Stack<String>();
        leftCommentChars = null;
        rightCommentChars = null;
        rightCommentPosition = -1;
        indent = null;

        mode = Mode.SCAN;
        pushTagMode(SEARCH_HEADER);

        initializeStore();
    }

    private Set<String> loadModeTransitions() {

        Set<String> transitions = new HashSet<String>();
        transitions.add(SEARCH_HEADER + PRE_COMPILER_START);
        transitions.add(IMPORTANT_START + IMPORTANT_END);
        transitions.add(IMPORTANT_START + PARAMETER);
        transitions.add(COMPILE_START + COMPILE_END);
        transitions.add(COMPILE_START + PARAMETER);
        transitions.add(LINK_START + LINK_END);
        transitions.add(LINK_START + PARAMETER);
        transitions.add(PRE_COMPILER_START + PRE_COMPILER_END);
        transitions.add(PRE_COMPILER_START + COMMAND);
        transitions.add(PRE_COMPILER_START + IMPORTANT_START);
        transitions.add(PRE_COMPILER_START + COMPILE_START);
        transitions.add(PRE_COMPILER_START + CREATE_COMMAND);
        transitions.add(PRE_COMPILER_START + EXECUTE);
        transitions.add(PRE_COMPILER_START + LINK_START);
        transitions.add(PRE_COMPILER_START + WIDTH);

        return transitions;
    }

    private String readLine(Iterator<String> textIterator, int maxLength) {

        if (!textIterator.hasNext()) {
            return null;
        }

        String line = textIterator.next();
        if (line == null) {
            return null;
        }

        lineCounter++;

        if (maxLength > 0 && line.length() > maxLength) {
            line = line.substring(0, maxLength);
        }

        return line;
    }

    private int getLineCounter() {
        return lineCounter;
    }
    
    private String getIndention(String line, String leftCommentChars) {

        int end = line.indexOf(leftCommentChars);

        return line.substring(0, end);
    }

    private int skipWhiteSpaces(String line, int start) {

        while (start < line.length() && " ".equals(line.substring(start, start + 1))) {
            start++;
        }

        return start;
    }

    private String pushTagMode(String mode) {

        if (!SEARCH_HEADER.equals(mode)) {
            checkTransition(mode);
        }

        scanModeStack.push(mode);
        return mode;
    }

    private String popTagMode() {
        scanModeStack.pop();
        return getTagMode();
    }

    private String getTagMode() {

        if (scanModeStack.isEmpty()) {
            return null;
        }

        return scanModeStack.peek();
    }

    private void checkTransition(String mode) {

        String transition = getTagMode() + mode;
        if (!modeTransitions.contains(transition)) {
            throw new RuntimeException("Wrong mode sequence in line " + getLineCounter() + ".");
        }
    }

    private enum Mode {
        SCAN,
        SCAN_HEADER,
        STOP
    }

    abstract protected void initializeStore();

    abstract protected void storeWidth(int width);

    abstract void storeFirstLine(int lineNumber);

    abstract void storeLastLine(int lineNumber);

    abstract protected void storeCreateCommand(String commandString);

    abstract protected void storeImportantParameter(String parameterString);

    abstract protected void storeCompileParameter(String parameterString);

    abstract protected void storeLinkParameter(String parameterString);

    abstract protected void storePreCommand(String commandString, int fromLine, int toLine);

    abstract protected void storePostCommand(String commandString, int fromLine, int toLine);

    abstract protected int getWidth();

    abstract protected String getCreateCommand(CLFormatter formatter);

    abstract protected String[] getImportantParameters();

    abstract protected String[] getCompileParameters();

    abstract protected String[] getLinkParameters();

    abstract protected String[] getPreCommands(CLFormatter formatter);

    abstract protected String[] getPostCommands(CLFormatter formatter);
}
