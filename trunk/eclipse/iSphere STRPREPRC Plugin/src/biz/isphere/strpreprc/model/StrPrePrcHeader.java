/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import biz.isphere.core.clcommands.CLFormatter;

import com.ibm.lpex.core.LpexDocumentLocation;
import com.ibm.lpex.core.LpexView;

public class StrPrePrcHeader {

    private static final String END_OF_LINE = ";";
    private static final String SEARCH_HEADER = "*SCAN";
    private static final String PRE_COMPILER_START = ">>PRE-COMPILER<<";
    private static final String PRE_COMPILER_END = ">>END-PRE-COMPILER<<";
    private static final String CREATE_COMMAND = ">>CRTCMD<<";
    private static final String COMPILE_START = ">>COMPILE<<";
    private static final String COMPILE_END = ">>END-COMPILE<<";
    private static final String LINK_START = ">>LINK<<";
    private static final String LINK_END = ">>END-LINK<<";
    private static final String PARAMETER = ">>PARM<<";
    private static final String EXECUTE = ">>EXECUTE<<";
    private static final String COMMAND = ">>CMD<<";
    private static final String WIDTH = ">>WIDTH<<";

    private int firstLine;
    private int lastLine;
    private int lineCounter;
    private StrPrePrcCommand createCommand;
    private Map<String, StrPrePrcParameter> compileParameters;
    private List<StrPrePrcCommand> preCommands;
    private List<StrPrePrcCommand> postCommands;
    private boolean doPostCommands;

    private Set<String> modeTransitions;
    private Stack<String> scanModeStack;
    private String leftCommentChars;
    private String rightCommentChars;
    private int rightCommentPosition;
    private String indent;
    private int width;
    private Mode mode;

    public StrPrePrcCommand getCreateCommand() {
        return createCommand;
    }

    public void setCreateCommand(StrPrePrcCommand command) {
        createCommand = command;
    }

    public StrPrePrcParameter[] getAllParameters() {

        List<StrPrePrcParameter> tempList = new ArrayList<StrPrePrcParameter>();
        for (StrPrePrcParameter parameter : compileParameters.values()) {
            tempList.add(parameter);
        }

        return tempList.toArray(new StrPrePrcParameter[tempList.size()]);
    }

    public String[] getPreCommands() {
        return preCommands.toArray(new String[preCommands.size()]);
    }

    public String[] getPostCommands() {
        return postCommands.toArray(new String[postCommands.size()]);
    }

    public boolean loadFromResource(String resource) throws IOException {

        String line;
        InputStream inStream = null;

        try {

            inStream = getClass().getClassLoader().getResourceAsStream(resource);
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

            List<String> textLines = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                textLines.add(line);
            }

            parseLines(textLines);

        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }

        return true;
    }

    public boolean loadFromLpexView(LpexView view) {

        int numElements = view.elements();
        if (numElements <= 0) {
            return false;
        }

        List<String> textLines = new ArrayList<String>();
        for (int i = 1; i <= numElements; i++) {
            textLines.add(view.elementText(i));
        }

        parseLines(textLines);

        if (firstLine <= 0 || lastLine <= 0) {
            return false;
        }

        return true;
    }

    public void updateLpexView(LpexView view) {

        // Remove existing header
        removeFromLpexView(view);

        // Add new header
        String[] headerLines = produceHeader();

        int insertLine = firstLine - 1;
        for (String line : headerLines) {
            if (insertLine == 0) {
                view.doCommand(new LpexDocumentLocation(insertLine, 0), "add before 1");
                view.setElementText(1, line);
            } else {
                view.doCommand(new LpexDocumentLocation(insertLine, 0), "insert " + line);
            }
            insertLine++;
        }

        return;
    }

    public void removeFromLpexView(LpexView view) {

        // Remove existing header
        view.doCommand(new LpexDocumentLocation(firstLine, 0), "delete " + (lastLine - firstLine + 1));
    }

    public String[] produceHeader() {
        return produceHeader(null);
    }

    public String[] produceHeader(CLFormatter formatter) {

        List<String> textLines = new ArrayList<String>();

        StringBuilder line = new StringBuilder();

        // >>PRE-COMPILER<<
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" ");
        line.append(PRE_COMPILER_START);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());

        if (width > 0) {
            // >>WIDTH<<
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

        String baseCommand = formatCommand(formatter, getCreateCommand().getFullCommand());
        if (baseCommand != null) {
            // >>CRTCMD<<
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  ");
            line.append(CREATE_COMMAND);
            line.append("  ");

            String[] crtCmd;
            if (width > 0) {
                crtCmd = splittCreateCommand(baseCommand, width - line.length());
            } else {
                crtCmd = splittCreateCommand(baseCommand, rightCommentPosition - 2 - line.length());
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

        if (hasParameters(StrPrePrcParameterType.COMPILE)) {
            // >>COMPILE<<
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  ");
            line.append(COMPILE_START);
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());

            // >>PARM<<
            createParameters(textLines, compileParameters.values(), StrPrePrcParameterType.COMPILE);

            // >>END-COMPILE<<
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  ");
            line.append(COMPILE_END);
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());
        }

        if (hasParameters(StrPrePrcParameterType.LINK)) {
            // >>LINK<<
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  ");
            line.append(LINK_START);
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());

            // >>PARM<<
            createParameters(textLines, compileParameters.values(), StrPrePrcParameterType.LINK);

            // >>END-LINK<<
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  ");
            line.append(LINK_END);
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());
        }

        // >>CMD<< (pre-command)
        if (preCommands != null && preCommands.size() > 0) {
            createCommands(textLines, preCommands);
        }

        // >>EXECUTE<<
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" " + "  ");
        line.append(EXECUTE);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());

        // >>CMD<< (post-commands
        if (postCommands != null && postCommands.size() > 0) {
            createCommands(textLines, postCommands);
        }

        // >>END-PRE-COMPILER<<
        line.delete(0, line.length()); // Clear buffer
        line.append(indent);
        line.append(leftCommentChars);
        line.append(" ");
        line.append(PRE_COMPILER_END);
        appendRightCommentChars(line, rightCommentPosition);
        textLines.add(line.toString());

        return textLines.toArray(new String[textLines.size()]);
    }

    private String formatCommand(CLFormatter formatter, String clCommand) {

        if (formatter == null) {
            return clCommand;
        }

        return formatter.format(clCommand);
    }

    private boolean hasParameters(StrPrePrcParameterType parameterType) {

        for (StrPrePrcParameter parameter : compileParameters.values()) {
            if (parameter.getType() == parameterType) {
                return true;
            }
        }

        return false;
    }

    private void appendRightCommentChars(StringBuilder line, int rightCommentPosition2) {
        line.append(spaces(rightCommentPosition - line.length()));
        line.append(rightCommentChars);
    }

    private void createParameters(List<String> textLines, Collection<StrPrePrcParameter> parameters, StrPrePrcParameterType parameterType) {

        StringBuilder line = new StringBuilder();

        for (StrPrePrcParameter parameter : parameters) {
            if (parameter.getType() == parameterType) {
                line.delete(0, line.length()); // Clear buffer
                line.append(indent);
                line.append(leftCommentChars);
                line.append(" " + "  " + " " + " ");
                line.append(PARAMETER);
                line.append(" " + " ");
                line.append(parameter.getParameter());
                line.append(END_OF_LINE);
                appendRightCommentChars(line, rightCommentPosition);
                textLines.add(line.toString());
            }
        }
    }

    private void createCommands(List<String> textLines, List<StrPrePrcCommand> commands) {

        StringBuilder line = new StringBuilder();

        for (StrPrePrcCommand command : commands) {
            line.delete(0, line.length()); // Clear buffer
            line.append(indent);
            line.append(leftCommentChars);
            line.append(" " + "  ");
            line.append(COMMAND);
            line.append(" " + " ");
            line.append(command.getFullCommand());
            line.append(END_OF_LINE);
            appendRightCommentChars(line, rightCommentPosition);
            textLines.add(line.toString());
        }
    }

    private String getContinuationOrEndOfLineChar(String[] crtCmd, int i) {

        if (i < crtCmd.length - 1) {
            return " +";
        }

        return END_OF_LINE;
    }

    private String[] splittCreateCommand(String command, int maxLength) {

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

    private String spaces(int length) {
        StringBuilder fixLength = new StringBuilder();
        while (fixLength.length() < length) {
            fixLength.append(" ");
        }
        return fixLength.toString();
    }

    private void parseLines(List<String> textLines) {

        initializeParser();

        Iterator<String> textIterator = textLines.iterator();
        // while (textIterator.hasNext()) {
        String line;
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
                if (rightCommentChars != null) {
                    rightCommentPosition = line.lastIndexOf(rightCommentChars);
                }
                indent = getIndention(line, leftCommentChars);
                firstLine = lineCounter;
            }

            if (mode == Mode.SCAN_HEADER) {
                if (WIDTH.equals(tag)) {
                    width = retrieveWidth(line);
                } else if (COMPILE_START.equals(tag)) {
                    pushTagMode(COMPILE_START);
                } else if (COMPILE_END.equals(tag)) {
                    popTagMode(); // COMPILE_START
                } else if (LINK_START.equals(tag)) {
                    pushTagMode(LINK_START);
                } else if (LINK_END.equals(tag)) {
                    popTagMode(); // LINK_START
                } else if (PARAMETER.equals(tag)) {
                    int startLine = lineCounter;
                    String parameter = retrieveTagValue(tag, line, width, textIterator);
                    if (parameter != null) {
                        StrPrePrcParameter tempParameter;
                        if (COMPILE_START.equals(getTagMode())) {
                            tempParameter = StrPrePrcParameter.createCompileParameter(parameter, startLine, lineCounter);
                        } else {
                            tempParameter = StrPrePrcParameter.createLinkParameter(parameter, startLine, lineCounter);
                        }
                        compileParameters.put(tempParameter.getKeyword(), tempParameter);
                    }
                } else if (CREATE_COMMAND.equals(tag)) {
                    int startLine = lineCounter;
                    String commandString = retrieveTagValue(tag, line, width, textIterator);
                    if (commandString != null) {
                        createCommand = StrPrePrcCommand.createCreateCommand(commandString);
                    }
                } else if (EXECUTE.equals(tag)) {
                    doPostCommands = true;
                } else if (COMMAND.equals(tag)) {
                    String commandString = retrieveTagValue(tag, line, width, textIterator);
                    if (commandString != null) {
                        if (doPostCommands) {
                            postCommands.add(StrPrePrcCommand.createPostCommand(commandString));
                        } else {
                            preCommands.add(StrPrePrcCommand.createPreCommand(commandString));
                        }
                    }
                }
            }

            if (PRE_COMPILER_END.equals(tag)) {
                popTagMode(); // PRE_COMPILER_START
                mode = Mode.STOP;
                lastLine = lineCounter;
            }
        }

        popTagMode(); // *SCAN
    }

    private void initializeParser() {

        modeTransitions = loadModeTransitions();
        scanModeStack = new Stack<String>();

        createCommand = StrPrePrcCommand.createCreateCommand(null);
        compileParameters = new HashMap<String, StrPrePrcParameter>();
        preCommands = new ArrayList<StrPrePrcCommand>();
        postCommands = new ArrayList<StrPrePrcCommand>();

        leftCommentChars = null;
        rightCommentChars = null;
        rightCommentPosition = 0;
        indent = null;
        width = -1;

        mode = Mode.SCAN;
        pushTagMode(SEARCH_HEADER);
    }

    private void checkTransition(String mode) {

        String transition = getTagMode() + mode;
        if (!modeTransitions.contains(transition)) {
            throw new RuntimeException("Wrong mode sequence in line " + lineCounter + ".");
        }
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

    private Set<String> loadModeTransitions() {

        Set<String> transitions = new HashSet<String>();
        transitions.add(SEARCH_HEADER + PRE_COMPILER_START);
        transitions.add(COMPILE_START + COMPILE_END);
        transitions.add(COMPILE_START + PARAMETER);
        transitions.add(LINK_START + LINK_END);
        transitions.add(LINK_START + PARAMETER);
        transitions.add(PRE_COMPILER_START + PRE_COMPILER_END);
        transitions.add(PRE_COMPILER_START + COMMAND);
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

    private int skipWhiteSpaces(String line, int start) {

        while (start < line.length() && " ".equals(line.substring(start, start + 1))) {
            start++;
        }

        return start;
    }

    private String retrieveTagValue(String tag, String line, int maxLength, Iterator<String> textIterator) {

        int start = line.indexOf(tag) + tag.length();
        start = skipWhiteSpaces(line, start);

        StringBuilder buffer = new StringBuilder();

        String continuationChar = retrieveTagValue(buffer, line, start);
        while (continuationChar != null) {
            line = readLine(textIterator, maxLength);
            if (line == null) {
                throw new RuntimeException("Premature end of file after line " + lineCounter + ".");
            }

            line = line.substring(start);
            if ("+".equals(continuationChar)) {
                line = trimL(line);
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

        throw new RuntimeException("The end-of-line character could not be found in line " + lineCounter + ".");
    }

    private String getIndention(String line, String leftCommentChars) {

        int end = line.indexOf(leftCommentChars);

        return line.substring(0, end);
    }

    private String retrieveLeftCommentChars(String line) {

        String trimmed = line.trim();
        int end = trimmed.indexOf(" ");

        return trimmed.substring(0, end);
    }

    private String retrieveRightCommentChars(String line, int startPos) {

        // Trim trailing spaces
        String trimmed = trimR(line);

        int start = trimmed.lastIndexOf(" ");
        int end = trimmed.length();

        return trimmed.substring(start + 1, end);
    }

    private String trimR(String text) {

        if (text == null) {
            return null;
        }

        while (text.endsWith(" ")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    private String trimL(String text) {

        if (text == null) {
            return null;
        }

        int start = 0;
        while (start < text.length() && " ".equals(text.substring(start, start + 1))) {
            start++;
        }
        return text.substring(start);
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

    private enum Mode {
        SCAN,
        SCAN_HEADER,
        STOP
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append("Command=" + createCommand);
        buffer.append(", ");
        buffer.append("Parameters=" + compileParameters);
        buffer.append(", ");
        buffer.append("Pre-Commands=" + preCommands);
        buffer.append(", ");
        buffer.append("Post-Commands=" + preCommands);

        return buffer.toString();
    }
}