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
    private Object origin;

    public RSECommand(boolean editable) {
        super(editable);
        this.compileType = null;
        this.id = null;
        this.label = null;
        this.isLabelEditable = true;
        this.commandString = null;
        this.isCommandStringEditable = true;
        this.nature = null;
        this.menuOption = null;
        this.origin = null;
    }

    public RSECommand(RSECompileType compileType, String name, String type, Object origin) {
        super(true);
        this.compileType = compileType;
        this.label = name;
        this.commandString = type;
        this.origin = origin;
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
        isLabelEditable = editable;
        updateEditable();
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
        updateEditable();
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

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    private void updateEditable() {
        setEditable(isLabelEditable() && isCommandStringEditable());
    }

    @Override
    public String getKey() {
        return compileType.getName() + ":" + label;
    }

    @Override
    public String getValue() {
        return commandString;
    }

    @Override
    public String toString() {
        return compileType.getName() + ": " + getLabel() + ": " + getCommandString();
    }
}
