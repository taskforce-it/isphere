/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import biz.isphere.core.clcommands.CLCommand;
import biz.isphere.core.clcommands.CLParameter;
import biz.isphere.core.clcommands.CLParser;

public class StrPrePrcCommand {

    private static final String SPACE = " "; //$NON-NLS-1$
    
    private StrPrePrcCommandType type;
    private String command;
    private Map<String, Integer> parametersIndex;
    private List<StrPrePrcParameter> orderedParametersList;

    private StrPrePrcCommand(String commandString, StrPrePrcCommandType type) {

        this.type = type;
        this.parametersIndex = new HashMap<String, Integer>();
        this.orderedParametersList = new LinkedList<StrPrePrcParameter>();

        if (commandString == null) {
            this.command = "";
            return;
        }

        CLParser parameterParser = new CLParser();
        CLCommand clCommand = parameterParser.parseCommand(commandString);

        this.command = clCommand.getCommand();
        for (CLParameter parameter : clCommand.getParameters()) {
            setParameter(StrPrePrcParameter.createBaseParameter(parameter.toString()));
        }
    }

    static StrPrePrcCommand createCreateCommand(String commandString) {
        return new StrPrePrcCommand(commandString, StrPrePrcCommandType.CREATE);
    }

    static StrPrePrcCommand createPreCommand(String commandString) {
        return new StrPrePrcCommand(commandString, StrPrePrcCommandType.PRE);
    }

    static StrPrePrcCommand createPostCommand(String commandString) {
        return new StrPrePrcCommand(commandString, StrPrePrcCommandType.PRE);
    }

    public void setParameter(StrPrePrcParameter parameter) {

        String keyword = parameter.getKeyword();
        Integer index = parametersIndex.get(keyword);
        if (index == null) {
            orderedParametersList.add(parameter);
            parametersIndex.put(parameter.getKeyword(), orderedParametersList.indexOf(parameter));
        } else {
            orderedParametersList.get(index).setValue(parameter.getValue());
        }
    }

    public boolean hasParameter(String keyword) {
        return parametersIndex.containsKey(keyword);
    }

    public StrPrePrcCommandType getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public String getFullCommand() {

        StringBuilder buffer = new StringBuilder(command);
        for (StrPrePrcParameter parameter : orderedParametersList) {
            buffer.append(SPACE);
            buffer.append(parameter.getParameter());
        }

        return buffer.toString();
    }

    public StrPrePrcParameter[] getParameters() {

        return orderedParametersList.toArray(new StrPrePrcParameter[orderedParametersList.size()]);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(type.getType());
        buffer.append(": ");
        buffer.append(super.toString().trim());
        return buffer.toString();
    }
}
