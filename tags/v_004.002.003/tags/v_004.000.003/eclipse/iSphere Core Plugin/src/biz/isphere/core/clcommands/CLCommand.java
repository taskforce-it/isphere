/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

import java.util.Arrays;
import java.util.Comparator;
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

    public String getUnifiedCommandString() {

        StringBuilder cmdBuffer = new StringBuilder();

        cmdBuffer.append(getCommand());
        for (CLParameter clParameter : getSortedParameters()) {
            cmdBuffer.append(" " + clParameter.toString());
        }

        return cmdBuffer.toString();
    }

    private void replace(CLParameter parameter) {
        for (CLParameter tempParameter : parameters) {
            if (parameter.getKeyword().equals(tempParameter.getKeyword())) {
                tempParameter.setValue(parameter.getValue());
            }
        }
    }

    private CLParameter[] getSortedParameters() {

        CLParameter[] clParameters = parameters.toArray(new CLParameter[parameters.size()]);

        Arrays.sort(clParameters, new Comparator<CLParameter>() {
            public int compare(CLParameter o1, CLParameter o2) {

                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o2 == null) {
                    return 1;
                } else if (o1 == null) {
                    return -1;
                } else {
                    String k1 = o1.getKeyword();
                    String k2 = o2.getKeyword();
                    if (k1 == null && k2 == null) {
                        return 0;
                    } else if (k2 == null) {
                        return 1;
                    } else if (k1 == null) {
                        return -1;
                    } else {
                        return k1.compareTo(k2);
                    }
                }
            }
        });

        return clParameters;
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
