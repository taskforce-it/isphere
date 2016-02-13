/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a CL command.
 * 
 * @author Thomas Raddatz
 */
public class CLCommand {

    private String command;
    private List<CLParameter> parameters;

    public CLCommand(String command) {
        this.command = command;
        this.parameters = new LinkedList<CLParameter>();
    }

    public void setParameter(CLParameter parameter) {

        if (parameter == null || parameter.getKeyword() == null) {
            return;
        }

        if (hasParameter(parameter.getKeyword())) {
            replace(parameter);
        } else {
            parameters.add(parameter);
        }
    }

    public String getCommand() {
        return command;
    }

    public CLParameter[] getParameters() {
        return parameters.toArray(new CLParameter[parameters.size()]);
    }

    public boolean hasParameter(String keyword) {
        for (CLParameter tempParameter : parameters) {
            if (keyword.equals(tempParameter.getKeyword())) {
                return true;
            }
        }
        return false;
    }

    private void replace(CLParameter parameter) {
        for (CLParameter tempParameter : parameters) {
            if (parameter.getKeyword().equals(tempParameter.getKeyword())) {
                tempParameter.setValue(parameter.getValue());
            }
        }
    }
}
