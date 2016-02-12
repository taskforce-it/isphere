/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.cl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class parses a given CL command and splits it into pieces.
 * 
 * @author Thomas Raddatz
 */
public class CLParser {

    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String OPEN_PARENTHESIS = "("; //$NON-NLS-1$
    private static final String CLOSE_PARENTHESIS = ")"; //$NON-NLS-1$

    public static final int COMMAND = 1;
    public static final int PARAMETER = 2;

    private int type;
    private String command;
    private List<CLParameter> parameters;

    /* Parser state fields */
    private int offset;
    private String currentKeyword;
    private String currentValue;
    boolean isError;
    String errorMessage;

    /**
     * Produces a new CLParser object.
     */
    private CLParser(int type) {
        this.type = type;
        this.parameters = new ArrayList<CLParameter>();
    }

    public static CLParser createCommandParser() {
        return new CLParser(COMMAND);
    }

    public static CLParser createParameterParser() {
        return new CLParser(PARAMETER);
    }

    /**
     * Returns the basic CL command without parameter, such as CRTRPGMOD or
     * CRTCLPGM.
     * 
     * @return Basic CL command without parameters.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the first CL parameters of the parsed command.
     * 
     * @return First CL parameter of the command.
     */
    public CLParameter getParameter() {
        return parameters.get(0);
    }

    /**
     * Returns the CL parameters of the parsed command.
     * 
     * @return CL parameters of the command.
     */
    public CLParameter[] getParameters() {
        return parameters.toArray(new CLParameter[parameters.size()]);
    }

    /**
     * Parses a given CL command. The command must be correctly formatted.
     * Keywords must be present. Use <i>biz.isphere.strpreprc.cl.CLFormatter</i>
     * to format a command.
     * 
     * @param clCommand - Command that is formatted.
     */
    public void parse(String clCommand) {

        if (clCommand == null) {
            return;
        }

        clCommand = clCommand.trim();

        ScanMode scanMode = ScanMode.INITIALIZE;
        while (scanMode != ScanMode.FINISHED) {
            if (scanMode == ScanMode.INITIALIZE) {
                scanMode = initializeParser();
            } else if (scanMode == ScanMode.COMMAND) {
                scanMode = retrieveCommand(clCommand);
            } else if (scanMode == ScanMode.PARAMETERS) {
                scanMode = retrieveParameters(clCommand);
            } else if (scanMode == ScanMode.STORE_PARAMETER) {
                storeParameter();
            }
        }
    }

    /**
     * Initializes the parser.
     */
    private ScanMode initializeParser() {

        setOffset(0);
        currentKeyword = null;

        isError = false;
        errorMessage = null;

        if (type == COMMAND) {
            return ScanMode.COMMAND;
        } else {
            return ScanMode.PARAMETERS;
        }
    }

    /**
     * Retrieves the command from a given CL command string.
     * 
     * @param clCommand - Trimmed CL command with all parameters
     * @return new scan mode
     */
    private ScanMode retrieveCommand(String clCommand) {

        int endPos = clCommand.indexOf(SPACE);
        if (endPos == 0) {
            endPos = clCommand.length();
        }

        if (endPos == 0) {
            return ScanMode.FINISHED;
        }

        command = clCommand.substring(0, endPos);

        setOffset(endPos);

        return ScanMode.PARAMETERS;
    }

    /**
     * Retrieves the next parameter from a given CL command.
     * 
     * @param clCommand - Trimmed CL command.
     * @return new scan mode
     */
    private ScanMode retrieveParameters(String clCommand) {

        if (offset >= clCommand.length()) {
            return ScanMode.FINISHED;
        }

        ScanMode scanMode = ScanMode.KEYWORD;
        while (scanMode != ScanMode.FINISHED) {
            if (scanMode == ScanMode.KEYWORD) {
                scanMode = retrieveKeyword(clCommand);
            } else if (scanMode == ScanMode.VALUE) {
                scanMode = retrieveValue(clCommand);
            } else if (scanMode == ScanMode.STORE_PARAMETER) {
                scanMode = storeParameter();
            }
        }

        return scanMode;
    }

    /**
     * Retrieves the next keyword from the parsed command string. The keyword is
     * temporarily stored in field 'currentKeyword' until it is associated with
     * its value.
     * 
     * @param clCommand - Trimmed CL command.
     * @return new scan mode
     */
    private ScanMode retrieveKeyword(String clCommand) {

        skipWhitespaces(clCommand);

        int startPos = offset;
        while (offset < clCommand.length()) {
            if (OPEN_PARENTHESIS.equals(clCommand.substring(offset, offset + 1))) {
                // end of keyword detected
                currentKeyword = clCommand.substring(startPos, offset);
                return ScanMode.VALUE;
            }
            incrementOffset();
        }

        return ScanMode.FINISHED;
    }

    /**
     * Retrieves the next value from the parsed command string. The value is
     * temporarily stored in field 'currentValue' until it is associated with
     * its keyword.
     * 
     * @param clCommand - Trimmed CL command.
     * @return new scan mode
     */
    private ScanMode retrieveValue(String clCommand) {

        if (offset >= clCommand.length()) {
            return ScanMode.FINISHED;
        }

        if (!OPEN_PARENTHESIS.equals(clCommand.substring(offset, offset + 1))) {
            return ScanMode.FINISHED;
        }

        incrementOffset(); // skip opening parenthesis

        int startPos = offset;
        while (offset < clCommand.length() && !CLOSE_PARENTHESIS.equals(clCommand.substring(offset, offset + 1))) {
            incrementOffset();
        }

        if (offset >= clCommand.length()) {
            currentValue = "";
            return ScanMode.FINISHED;
        }
        
        if (CLOSE_PARENTHESIS.equals(clCommand.substring(offset, offset + 1))) {
            currentValue = clCommand.substring(startPos, offset);
            incrementOffset(); // skip closing parenthesis
            return ScanMode.STORE_PARAMETER;
        }

        return ScanMode.FINISHED;
    }

    /**
     * Adds a new parameter to the parameter list.
     */
    private ScanMode storeParameter() {

        CLParameter parameter = new CLParameter(currentKeyword, currentValue);
        parameters.add(parameter);

        currentKeyword = null;
        currentValue = null;

        return ScanMode.KEYWORD;
    }

    /**
     * Skips white spaces.
     * 
     * @param clCommand - Trimmed CL command with all parameters
     */
    private void skipWhitespaces(String clCommand) {

        while (offset < clCommand.length() && SPACE.equals(clCommand.substring(offset, offset + 1))) {
            incrementOffset();
        }
    }

    /**
     * Sets the current offset of the parsed command string by 1.
     */
    private void incrementOffset() {
        this.offset++;
    }

    /**
     * Sets the current offset of the parsed command string.
     * 
     * @param offset - Offset in the currently parsed command string.
     */
    private void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Enumeration that defines the scan modes used by the parser.
     */
    private enum ScanMode {
        INITIALIZE,
        COMMAND,
        PARAMETERS,
        KEYWORD,
        VALUE,
        STORE_PARAMETER,
        FINISHED
    }
}
