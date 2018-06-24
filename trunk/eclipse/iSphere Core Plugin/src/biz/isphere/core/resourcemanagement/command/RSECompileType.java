/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.resourcemanagement.filter.RSEProfile;

public class RSECompileType {

    private RSEProfile profile;
    private String type;
    private boolean _default;
    private Object origin;
    private List<RSECommand> commands;

    public RSECompileType(RSEProfile profile) {
        this(profile, null, false, null);
    }

    private RSECompileType(RSEProfile profile, String type, boolean _default, Object origin) {
        this.profile = profile;
        this.type = type;
        this._default = _default;
        this.origin = origin;
        commands = new ArrayList<RSECommand>();
    }

    public RSEProfile getProfile() {
        return profile;
    }

    public void setProfile(RSEProfile profile) {
        this.profile = profile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDefault() {
        return _default;
    }

    public void setDefault(boolean _default) {
        this._default = _default;
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    public RSECommand[] getCommands() {
        return commands.toArray(new RSECommand[commands.size()]);
    }

    public RSECommand[] getCommands(String type) {

        List<RSECommand> commands = new ArrayList<RSECommand>();

        for (RSECommand command : this.commands) {
            if (type == null || command.getLabel().equals(type)) {
                commands.add(command);
            }
        }

        return commands.toArray(new RSECommand[commands.size()]);
    }

    public String[] getCommandLabels() {
        return getCommandLabels(null);
    }

    public String[] getCommandLabels(String type) {

        List<String> commandNames = new ArrayList<String>();

        RSECommand[] commands = getCommands();
        for (RSECommand command : commands) {
            commandNames.add(command.getLabel());
        }

        return commandNames.toArray(new String[commandNames.size()]);
    }

    public void addCommand(RSECommand command) {
        commands.add(command);
    }

    @Override
    public String toString() {
        return getType();
    }

}
