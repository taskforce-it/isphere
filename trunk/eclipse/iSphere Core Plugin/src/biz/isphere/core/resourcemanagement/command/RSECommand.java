/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import biz.isphere.core.resourcemanagement.AbstractResource;

public class RSECommand extends AbstractResource implements Comparable<RSECommand> {

    public static final String MENU_OPTION_BOTH = "Both";
    public static final String MENU_OPTION_PROMPT = "Prompt";
    public static final String MENU_OPTION_NO_PROMPT = "NoPrompt";

    public static final String NATURE_IBM = "IBM defined";
    public static final String NATURE_USER = "User defined";
    public static final String NATURE_ISV = "ISV defined";

    private RSECompileType compileType;
    private String id;
    private String label;
    private boolean isLabelEditable;
    private String commandString;
    private boolean isCommandStringEditable;
    private String nature;
    private String menuOption;
    int order;
    private Object origin;

    public RSECommand() {
        this(null, null, true, null, true, null, null, null, 0, null);
    }

    public RSECommand(RSECompileType compileType, String label, boolean isLabelEditable, String commandString, boolean isCommandStringEditable,
        String id, String nature, String menuOption, int order, Object origin) {
        super(true);

        setCompileType(compileType);
        setId(id);
        setLabel(label);
        setCommandString(commandString);
        setNature(nature);
        setMenuOption(menuOption);
        setOrder(order);
        setOrigin(origin);

        setLabelEditable(isLabelEditable);
        setCommandStringEditable(isCommandStringEditable);
    }

    public boolean isUserDefined() {
        return NATURE_USER.equals(getNature());
    }

    public RSECompileType getCompileType() {
        return compileType;
    }

    public void setCompileType(RSECompileType compileType) {
        this.compileType = compileType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = ensureNotNull(id);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = ensureNotNull(label);
    }

    public boolean isLabelEditable() {
        return isLabelEditable;
    }

    public void setLabelEditable(boolean editable) {
        this.isLabelEditable = editable;
    }

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = ensureNotNull(commandString);
    }

    public boolean isCommandStringEditable() {
        return isCommandStringEditable;
    }

    public void setCommandStringEditable(boolean editable) {
        isCommandStringEditable = editable;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = ensureNotNull(nature);
    }

    public String getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(String menuOption) {
        this.menuOption = ensureNotNull(menuOption);
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    @Override
    public boolean isEditable() {
        return isUserDefined();
    }

    @Override
    public void setEditable(boolean editable) {
        throw new RuntimeException("Calling setEditable() for RSECommand is not allowed."); //$NON-NLS-1$
    }

    @Override
    public String getKey() {
        return compileType.getType() + ":" + label;
    }

    @Override
    public String getValue() {
        return ensureNotNull(commandString);
    }

    private String ensureNotNull(String value) {

        if (value == null) {
            return ""; //$NON-NLS-1$
        }

        return value;
    }

    @Override
    public String toString() {
        return compileType.getType() + ": " + getLabel() + ": " + getCommandString();
    }

    public int compareTo(RSECommand other) {

        if (other == null || other.getKey() == null) {
            return 1;
        } else {
            return getKey().compareTo(other.getKey());
        }
    }
}
