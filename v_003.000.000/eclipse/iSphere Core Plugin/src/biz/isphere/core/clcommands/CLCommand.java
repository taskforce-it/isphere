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

import biz.isphere.base.internal.StringHelper;

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

    public void setParameters(List<CLParameter> parameters) {
        this.parameters = parameters;
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

    public CLParameter removeParameter(String keyword) {

        if (StringHelper.isNullOrEmpty(keyword)) {
            return null;
        }

        for (CLParameter parameter : parameters) {
            if (keyword.equals(parameter.getKeyword())) {
                parameters.remove(parameter);
                return parameter;
            }
        }

        return null;
    }

    public String getCommand() {
        return command;
    }

    public CLParameter[] getParameters() {
        return parameters.toArray(new CLParameter[parameters.size()]);
    }

    public String getParametersString() {

        StringBuilder buffer = new StringBuilder();
        for (CLParameter parameter : parameters) {
            if (buffer.length() > 0) {
                buffer.append(" "); //$NON-NLS-1$
            }
            buffer.append(parameter.toString());
        }

        return buffer.toString();
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

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder(command);
        for (CLParameter clParameter : parameters) {
            buffer.append(" ");
            buffer.append(clParameter.toString());
        }

        return buffer.toString();
    }
}
