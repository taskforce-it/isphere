/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.clcommands.CLCommand;
import biz.isphere.core.clcommands.CLFormatter;
import biz.isphere.core.clcommands.CLParameter;
import biz.isphere.core.clcommands.CLParser;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.preferences.Preferences;

import com.ibm.lpex.core.LpexDocumentLocation;
import com.ibm.lpex.core.LpexView;

public class StrPrePrcParser extends AbstractStrPrePrcParser {

    private CLParser clParser;

    private static final Set<String> REPLACEMENT_VARIABLES_VALUES = new HashSet<String>(Arrays.asList(new String[] { "&LI", "&OB", "&TY", "&SL",
        "&SF", "&SM", "&TL", "&TO", "&TR", "&FL", "&FF", "&FM", "&U0", "&U1", "&U2", "&U3", "&U4", "&U5", "&U6", "&U7", "&U8", "&U9" }));

    private static final Set<String> REPLACEMENT_VARIABLES_HEADERS = new HashSet<String>(Arrays.asList(new String[] { Messages.RPLVAR_LI,
        Messages.RPLVAR_OB, Messages.RPLVAR_TY, Messages.RPLVAR_SL, Messages.RPLVAR_SF, Messages.RPLVAR_SM, Messages.RPLVAR_TL, Messages.RPLVAR_TO,
        Messages.RPLVAR_TR, Messages.RPLVAR_FL, Messages.RPLVAR_FF, Messages.RPLVAR_FM, Messages.RPLVAR_U0, Messages.RPLVAR_U1, Messages.RPLVAR_U2,
        Messages.RPLVAR_U3, Messages.RPLVAR_U4, Messages.RPLVAR_U5, Messages.RPLVAR_U6, Messages.RPLVAR_U7, Messages.RPLVAR_U8, Messages.RPLVAR_U9 }));

    private String memberType;

    private int width;
    private int firstLine;
    private int lastLine;
    private String createCommand;
    private Map<String, CLParameter> parameters;
    private LinkedList<String> baseParameters;
    private LinkedList<String> importantParameters;
    private LinkedList<String> compileParameters;
    private LinkedList<String> linkParameters;
    private LinkedList<Command> preCommands;
    private LinkedList<Command> postCommands;
    private LinkedList<String> parameterSequence;

    private boolean createTemplates;

    public StrPrePrcParser(String memberType) {

        this.memberType = memberType;

        this.clParser = new CLParser();
        this.parameters = new HashMap<String, CLParameter>();
        this.baseParameters = new LinkedList<String>();
        this.importantParameters = new LinkedList<String>();
        this.compileParameters = new LinkedList<String>();
        this.linkParameters = new LinkedList<String>();
        this.preCommands = new LinkedList<Command>();
        this.postCommands = new LinkedList<Command>();
        this.parameterSequence = new LinkedList<String>();

        this.createTemplates = false;

        initializeStore();
    }

    public static boolean isVariable(String variable) {
        return REPLACEMENT_VARIABLES_VALUES.contains(variable);
    }

    public static String[] getReplacementVariables() {
        return REPLACEMENT_VARIABLES_VALUES.toArray(new String[REPLACEMENT_VARIABLES_VALUES.size()]);
    }

    public static String[] getReplacementVariablesHeaders() {
        return REPLACEMENT_VARIABLES_HEADERS.toArray(new String[REPLACEMENT_VARIABLES_VALUES.size()]);
    }

    public String getCommand() {
        return createCommand;
    }

    public String getFullCommand() {

        if (createCommand == null) {
            return null;
        }

        CLCommand clCommand = new CLCommand(createCommand);
        clCommand.setParameters(new ArrayList<CLParameter>(parameters.values()));

        return clCommand.toString();
    }

    public void setFullCommand(String fullCommandString) {

        initializeStore();

        if (fullCommandString == null) {
            throw new IllegalArgumentException("Parameter 'commandString' must not be null.");
        }

        CLCommand clCommand = clParser.parseCommand(fullCommandString);
        if (clCommand == null) {
            return;
        }

        createCommand = clCommand.getCommand();

        for (CLParameter clParameter : clCommand.getParameters()) {
            if (isBaseKeyword(clParameter)) {
                setBaseParameter(clParameter);
            } else {
                String defaultSection = Preferences.getInstance().getDefaultSection();
                if (Preferences.IMPORTANT.equals(defaultSection)) {
                    setImportantParameter(clParameter);
                } else if (Preferences.COMPILE.equals(defaultSection)) {
                    setCompileParameter(clParameter);
                } else {
                    setLinkParameter(clParameter);
                }
            }
        }
    }

    public String getCommandAtLine(int sequenceNumber) {

        Command command = findCommandAtLine(sequenceNumber);
        if (command == null) {
            return null;
        }

        return command.getCommand();
    }

    public boolean changeCommandAtLine(int sequenceNumber, String commandString) {

        Command command = findCommandAtLine(sequenceNumber);
        if (command == null) {
            return false;
        }

        command.setCommand(commandString);

        return true;
    }

    public void addPreCompileCommand(String commandString) {

        Command command = new Command(commandString, 0, 0);

        preCommands.add(command);
    }

    public void addPostCompileCommand(String commandString) {

        Command command = new Command(commandString, 0, 0);

        postCommands.add(command);
    }

    public void updateFullCommand(String commandString) {

        CLCommand clCommand = clParser.parseCommand(commandString);
        if (clCommand == null) {
            return;
        }

        if (createCommand == null) {
            throw new IllegalArgumentException("Current value of 'createCommand' must not be null.");
        }

        if (!createCommand.equals(clCommand.getCommand())) {
            throw new IllegalArgumentException("Current value of 'createCommand' must match command of parameter 'commandString'.");
        }

        updateParameters(clCommand.getParameters());
    }

    public String[] getHeader(CLFormatter formatter) {

        try {
            createTemplates = true;
            produceParameterSequence(getFullCommand(), formatter);
            return produceHeader(null);
        } finally {
            createTemplates = false;
        }
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

        if (firstLine > 0 && lastLine > 0) {
            return true;
        }

        return false;
    }

    public boolean loadTemplate(String[] lines) {

        parseLines(Arrays.asList(lines));

        if (firstLine > 0 && lastLine > 0) {
            return true;
        }

        return false;
    }

    public void loadDefaultTemplate() {

        String[] lines = HeaderTemplates.getInstance().getTemplate(memberType);

        parseLines(Arrays.asList(lines));

        firstLine = -1;
        lastLine = -1;
    }

    public void updateLpexView(LpexView view, CLFormatter formatter) {

        produceParameterSequence(getFullCommand(), formatter);

        int positionCursorTo = IntHelper.tryParseInt(view.query("line"), 0);

        // Remove existing header
        int insertAtLine;
        if (removeFromLpexView(view)) {
            insertAtLine = firstLine - 1;
        } else {
            insertAtLine = IntHelper.tryParseInt(view.query("line"), 0) - 1;
        }

        // Add new header
        String[] headerLines = produceHeader(formatter);

        for (String line : headerLines) {
            if (insertAtLine <= 0) {
                view.doCommand(new LpexDocumentLocation(1, 0), "add before 1");
                view.setElementText(1, line);
                positionCursorTo = 1;
            } else {
                view.doCommand(new LpexDocumentLocation(insertAtLine, 0), "insert " + line);
            }
            insertAtLine++;
        }

        view.doCommand("locate element " + Math.max(positionCursorTo, 1));
    }

    public boolean hasSections() {

        if (importantParameters.size() > 0 || compileParameters.size() > 0 || linkParameters.size() > 0) {
            return true;
        }

        return false;
    }

    private Command findCommandAtLine(int sequenceNumber) {

        Command command = findCommandAtLine(sequenceNumber, preCommands);
        if (command == null) {
            command = findCommandAtLine(sequenceNumber, postCommands);
            if (command == null) {
                return null;
            }
        }

        return command;
    }

    private Command findCommandAtLine(int sequenceNumber, LinkedList<Command> commands) {

        for (Command command : commands) {
            if (command.getFirstLine() <= sequenceNumber && sequenceNumber <= command.getLastLine()) {
                return command;
            }
        }

        return null;
    }

    private void produceParameterSequence(String fullCommand, CLFormatter formatter) {

        parameterSequence.clear();
        if (formatter == null) {
            parameterSequence.addAll(baseParameters);
            parameterSequence.addAll(importantParameters);
            parameterSequence.addAll(compileParameters);
            parameterSequence.addAll(linkParameters);
        } else {
            String commandString = formatter.format(getFullCommand());
            CLCommand clCommand = clParser.parseCommand(commandString);
            for (CLParameter clParameter : clCommand.getParameters()) {
                parameterSequence.add(clParameter.getKeyword());
            }
        }
    }

    public boolean removeFromLpexView(LpexView view) {

        if (firstLine <= 0) {
            return false;
        }

        // Remove existing header
        view.doCommand(new LpexDocumentLocation(firstLine, 0), "delete " + (lastLine - firstLine + 1));

        return true;
    }

    private void updateParameters(CLParameter[] clParameters) {

        removeDeletedParameters(clParameters);

        List<CLParameter> remainingParameters = new ArrayList<CLParameter>();
        remainingParameters.addAll(Arrays.asList(clParameters));

        for (CLParameter clParameter : clParameters) {
            String keyword = clParameter.getKeyword();
            if (baseParameters.contains(keyword)) {
                setBaseParameter(clParameter);
                remainingParameters.remove(clParameter);
            } else if (importantParameters.contains(keyword)) {
                setImportantParameter(clParameter);
                remainingParameters.remove(clParameter);
            } else if (compileParameters.contains(keyword)) {
                setCompileParameter(clParameter);
                remainingParameters.remove(clParameter);
            } else if (linkParameters.contains(keyword)) {
                setLinkParameter(clParameter);
                remainingParameters.remove(clParameter);
            }
        }

        for (CLParameter clParameter : remainingParameters) {
            if (isBaseKeyword(clParameter)) {
                setBaseParameter(clParameter);
            } else {
                String defaultSection = Preferences.getInstance().getDefaultSection();
                if (Preferences.IMPORTANT.equals(defaultSection)) {
                    setImportantParameter(clParameter);
                } else if (Preferences.COMPILE.equals(defaultSection)) {
                    setCompileParameter(clParameter);
                } else {
                    setLinkParameter(clParameter);
                }
            }
        }

        return;
    }

    private void removeDeletedParameters(CLParameter[] clParameters) {

        Set<String> newKeywords = new HashSet<String>();
        for (CLParameter clParameter : clParameters) {
            newKeywords.add(clParameter.getKeyword());
        }

        Set<String> currentKeywords = new HashSet<String>();
        for (CLParameter clParameter : parameters.values()) {
            currentKeywords.add(clParameter.getKeyword());
        }

        currentKeywords.removeAll(newKeywords);
        for (String keyword : currentKeywords) {
            removeParameter(keyword);
        }
    }

    @Override
    protected void initializeStore() {

        width = -1;
        firstLine = -1;
        lastLine = -1;
        createCommand = null;
        parameters.clear();
        baseParameters.clear();
        baseParameters.clear();
        importantParameters.clear();
        compileParameters.clear();
        preCommands.clear();
        postCommands.clear();
        parameterSequence.clear();
    }

    @Override
    protected void storeWidth(int width) {
        this.width = width;
    }

    @Override
    protected void storeFirstLine(int line) {
        this.firstLine = line;
    }

    @Override
    protected void storeLastLine(int line) {
        this.lastLine = line;
    }

    @Override
    protected void storeCreateCommand(String commandString) {

        if (commandString == null) {
            throw new IllegalArgumentException("Parameter 'commandString' must not be null.");
        }

        CLCommand clCommand = clParser.parseCommand(commandString);
        if (clCommand == null) {
            return;
        }

        createCommand = clCommand.getCommand();

        for (CLParameter clParameter : clCommand.getParameters()) {
            setBaseParameter(clParameter);
        }
    }

    @Override
    protected void storeImportantParameter(String parameterString) {

        CLParameter clParameter = clParser.parseParameter(parameterString);
        setImportantParameter(clParameter);
    }

    @Override
    protected void storeCompileParameter(String parameterString) {

        CLParameter clParameter = clParser.parseParameter(parameterString);
        setCompileParameter(clParameter);
    }

    @Override
    protected void storeLinkParameter(String parameterString) {

        CLParameter clParameter = clParser.parseParameter(parameterString);
        setLinkParameter(clParameter);
    }

    @Override
    protected void storePreCommand(String commandString, int fromLine, int toLine) {
        if (commandString == null) {
            return;
        }
        preCommands.add(new Command(commandString, fromLine, toLine));
    }

    @Override
    protected void storePostCommand(String commandString, int fromLine, int toLine) {
        if (commandString == null) {
            return;
        }
        postCommands.add(new Command(commandString, fromLine, toLine));
    }

    @Override
    protected int getWidth() {
        return width;
    }

    @Override
    protected String getCreateCommand(CLFormatter formatter) {

        LinkedList<String> tempParameters;
        if (useSections()) {
            tempParameters = baseParameters;
        } else {
            tempParameters = getMergedParameters();
        }

        if (formatter == null) {
            CLCommand clCommand = new CLCommand(createCommand);

            for (String keyword : tempParameters) {
                CLParameter parameter = parameters.get(keyword);
                clCommand.setParameter(parameter);
            }

            return clCommand.toString();
        }

        // We have to pass the full object creation command to the formatter,
        // because the QCAPCMD cannot format a command with missing required
        // parameter, such as a missing PGM parameter with a CRTCLPGM command.
        String fullCommandString = formatter.format(getFullCommand());

        // Afterwards the extra parameters have to be removed again.
        if (useSections()) {
            return keepBaseParametersOnly(fullCommandString);
        } else {
            return fullCommandString;
        }
    }

    private LinkedList<String> getMergedParameters() {

        LinkedList<String> tempParameters;
        tempParameters = new LinkedList<String>();
        tempParameters.addAll(baseParameters);
        tempParameters.addAll(importantParameters);
        tempParameters.addAll(compileParameters);
        tempParameters.addAll(linkParameters);

        return tempParameters;
    }

    private String keepBaseParametersOnly(String fullCommand) {

        CLParser parser = new CLParser();
        CLCommand clCommand = parser.parseCommand(fullCommand);
        for (CLParameter clParameter : clCommand.getParameters()) {
            String keyword = clParameter.getKeyword();
            if (!baseParameters.contains(keyword)) {
                // removeKeywords.add(keyword);
                clCommand.removeParameter(keyword);
            }
        }

        return clCommand.toString();
    }

    @Override
    protected String[] getImportantParameters() {
        if (useSections()) {
            return getParameters(importantParameters);
        } else {
            return new String[0];
        }
    }

    @Override
    protected String[] getCompileParameters() {
        if (useSections()) {
            return getParameters(compileParameters);
        } else {
            return new String[0];
        }
    }

    @Override
    protected String[] getLinkParameters() {
        if (useSections()) {
            return getParameters(linkParameters);
        } else {
            return new String[0];
        }
    }

    @Override
    protected String[] getPreCommands(CLFormatter formatter) {
        return getPreOrPostCommands(preCommands, formatter);
    }

    @Override
    protected String[] getPostCommands(CLFormatter formatter) {
        return getPreOrPostCommands(postCommands, formatter);
    }

    private String[] getPreOrPostCommands(LinkedList<Command> commands, CLFormatter formatter) {

        if (formatter == null) {
            return commands.toArray(new String[postCommands.size()]);
        }

        LinkedList<String> formattedCommands = new LinkedList<String>();
        for (Command command : commands) {
            String formattedCommand = formatter.format(command.getCommand());
            if (formattedCommand == null) {
                formattedCommand = command.getCommand() + " /* SYNTAX ERROR? */"; //$NON-NLS-1$
            }
            formattedCommands.add(formattedCommand);
        }

        return formattedCommands.toArray(new String[formattedCommands.size()]);
    }

    private String[] getParameters(LinkedList<String> parametersSet) {

        List<String> tempParameters = new ArrayList<String>();

        for (String keyword : parameterSequence) {
            if (parametersSet.contains(keyword)) {
                tempParameters.add(parameters.get(keyword).toString());
            }
        }

        return tempParameters.toArray(new String[tempParameters.size()]);
    }

    private void setBaseParameter(CLParameter clParameter) {
        setParameter(baseParameters, clParameter);
    }

    private void setImportantParameter(CLParameter clParameter) {
        setParameter(importantParameters, clParameter);
    }

    private void setCompileParameter(CLParameter clParameter) {
        setParameter(compileParameters, clParameter);
    }

    private void setLinkParameter(CLParameter clParameter) {
        setParameter(linkParameters, clParameter);
    }

    private void setParameter(LinkedList<String> parameterSet, CLParameter clParameter) {

        String keyword = clParameter.getKeyword();

        if (parameterSet.contains(keyword)) {
            parameters.get(keyword).setValue(clParameter.getValue());
        } else {
            removeParameter(keyword);
            parameters.put(keyword, clParameter);
            parameterSet.add(keyword);
        }
    }

    private void removeParameter(String keyword) {

        if (parameters.containsKey(keyword)) {
            parameters.remove(keyword);
            baseParameters.remove(keyword);
            importantParameters.remove(keyword);
            compileParameters.remove(keyword);
            linkParameters.remove(keyword);
        }
    }

    private boolean isBaseKeyword(CLParameter clParameter) {
        return HeaderTemplates.getInstance().isBaseKeyword(clParameter.getKeyword(), memberType);
    }

    private boolean useSections() {

        if (createTemplates) {
            return true;
        }

        return Preferences.getInstance().useParameterSections();
    }

    private class Command {

        private int firstLine;
        private int lastLine;
        private String commandString;

        public Command(String commandString, int firstLine, int lastLine) {
            this.firstLine = firstLine;
            this.lastLine = lastLine;
            this.commandString = commandString;
        }

        public int getFirstLine() {
            return firstLine;
        }

        public int getLastLine() {
            return lastLine;
        }

        public String getCommand() {
            return commandString;
        }

        public void setCommand(String commandString) {
            this.commandString = commandString;
        }

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("(");
            buffer.append(firstLine);
            buffer.append("-");
            buffer.append(lastLine);
            buffer.append(") ");
            buffer.append(commandString);
            return buffer.toString();
        }
    }
}
