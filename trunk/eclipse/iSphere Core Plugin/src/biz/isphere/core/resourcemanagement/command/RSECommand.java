/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import biz.isphere.core.resourcemanagement.AbstractResource;

public class RSECommand extends AbstractResource {

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
        super(false);
        this.compileType = null;
        this.id = null;
        this.label = null;
        this.commandString = null;
        this.nature = null;
        this.menuOption = null;
        this.order = 0;
        this.origin = null;

        setLabelEditable(true);
        setCommandStringEditable(true);
    }

    public RSECommand(RSECompileType compileType, String label, boolean isLabelEditable, String commandString, boolean isCommandStringEditable,
        String id, String nature, String menuOption, int order, Object origin) {
        super(true);
        this.compileType = compileType;
        this.id = id;
        this.label = label;
        this.commandString = commandString;
        this.nature = nature;
        this.menuOption = menuOption;
        this.order = order;
        this.origin = origin;

        setLabelEditable(isLabelEditable);
        setCommandStringEditable(isCommandStringEditable);
    }

    public boolean isUserDefined() {
        return !NATURE_IBM.equals(getNature());
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
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
        this.commandString = commandString;
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
        this.nature = nature;
    }

    public String getMenuOption() {
        return menuOption;
    }

    public void setMenuOption(String menuOption) {
        this.menuOption = menuOption;
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
        return NATURE_USER.equals(getNature());
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
        return commandString;
    }

    @Override
    public String toString() {
        return compileType.getType() + ": " + getLabel() + ": " + getCommandString();
    }
}
