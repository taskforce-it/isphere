/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

import java.util.Arrays;

import biz.isphere.core.Messages;
import biz.isphere.core.clcommands.CLTokenizer;

public abstract class AbstractResource {

    public static final String PUSH_TO_REPOSITORY = "Push_to_repository";
    public static final String PUSH_TO_WORKSPACE = "Push_to_workspace";
    public static final String DELETE_FROM_REPOSITORY = "Delete_from_repository";
    public static final String DELETE_FROM_WORKSPACE = "Delete_from_workspace";
    public static final String DELETE_FROM_BOTH = "Delete_from_both";

    private boolean editable;
    private String action;

    public AbstractResource(boolean editable) {
        this.editable = editable;
        action = null;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public static String getActionText(String action) {
        if (action.equals(PUSH_TO_REPOSITORY)) {
            return Messages.Push_to_repository;
        } else if (action.equals(PUSH_TO_WORKSPACE)) {
            return Messages.Push_to_workspace;
        } else if (action.equals(DELETE_FROM_REPOSITORY)) {
            return Messages.Delete_from_repository;
        } else if (action.equals(DELETE_FROM_WORKSPACE)) {
            return Messages.Delete_from_workspace;
        } else if (action.equals(DELETE_FROM_BOTH)) {
            return Messages.Delete_from_both;
        } else {
            return "*UNKNOWN";
        }
    }

    public abstract String getKey();

    public abstract String getValue();

    protected void appendAttribute(StringBuilder buffer, String value) {

        if (buffer.length() > 0) {
            buffer.append(":"); //$NON-NLS-1$
        }
        buffer.append(value);
    }

    protected void appendAttribute(StringBuilder buffer, String[] values) {

        if (buffer.length() > 0) {
            buffer.append(":"); //$NON-NLS-1$
        }

        buffer.append("["); //$NON-NLS-1$
        boolean isFirstItem = true;
        for (String value : values) {
            if (!isFirstItem) {
                buffer.append(","); //$NON-NLS-1$
            } else {
                isFirstItem = false;
            }
            buffer.append(value);
        }
        buffer.append("]"); //$NON-NLS-1$
    }

    protected void appendAttribute(StringBuilder buffer, boolean value) {

        if (buffer.length() > 0) {
            buffer.append(":"); //$NON-NLS-1$
        }
        buffer.append(Boolean.toString(value));
    }

    protected String unifyCommandString(String commandString) {

        try {

            CLTokenizer parser = new CLTokenizer(true);
            String[] tokens = parser.tokenizeCommand(commandString);

            StringBuilder cmdBuffer = new StringBuilder();

            if (tokens.length > 1) {
                // Append command to buffer and sort remaining parameters
                cmdBuffer.append(tokens[0]);
                String[] parameters = new String[tokens.length - 1];
                System.arraycopy(tokens, 1, parameters, 0, parameters.length);
                Arrays.sort(parameters);
                tokens = parameters;
            }

            for (String token : tokens) {
                if (cmdBuffer.length() > 0) {
                    cmdBuffer.append(" ");
                }
                cmdBuffer.append(token);
            }

            return cmdBuffer.toString();

        } catch (Exception e) {
            return commandString;
        }
    }

    protected String ensureNotNull(String value) {

        if (value == null) {
            return ""; //$NON-NLS-1$
        }

        return value;
    }

}
