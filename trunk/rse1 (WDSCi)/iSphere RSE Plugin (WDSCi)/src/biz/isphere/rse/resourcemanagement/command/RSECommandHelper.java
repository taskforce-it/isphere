/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.command;

import java.util.ArrayList;
import java.util.Vector;

import biz.isphere.core.resourcemanagement.command.RSECommand;
import biz.isphere.core.resourcemanagement.command.RSECompileType;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.resourcemanagement.AbstractSystemHelper;

import com.ibm.etools.iseries.core.compile.ISeriesCompileManager;
import com.ibm.etools.systems.core.ui.compile.SystemCompileCommand;
import com.ibm.etools.systems.core.ui.compile.SystemCompileManager;
import com.ibm.etools.systems.core.ui.compile.SystemCompileProfile;
import com.ibm.etools.systems.core.ui.compile.SystemCompileType;
import com.ibm.etools.systems.model.SystemProfile;
import com.ibm.etools.systems.model.SystemStartHere;
import com.ibm.etools.systems.subsystems.SubSystemFactory;

@SuppressWarnings("restriction")
public class RSECommandHelper extends AbstractSystemHelper {

    public static RSECompileType[] getCompileTypes(RSEProfile rseProfile) {

        ArrayList<RSECompileType> rseCompileTypes = new ArrayList<RSECompileType>();

        SystemProfile profile = SystemStartHere.getSystemRegistry().getSystemProfile(rseProfile.getName());

        SubSystemFactory subSystemFactory = SystemStartHere.getSystemRegistry().getSubSystemFactory("ibm.files400");
        SystemCompileManager compileManager = new ISeriesCompileManager();
        compileManager.setSubSystemFactory(subSystemFactory);

        SystemCompileProfile compileProfile = compileManager.getCompileProfile(profile);
        Vector compileTypes = compileProfile.getCompileTypes();
        for (Object object : compileTypes) {
            if (object instanceof SystemCompileType) {
                SystemCompileType systemCompileType = (SystemCompileType)object;
                RSECompileType rseCompileType = new RSECompileType();
                rseCompileType.setName(systemCompileType.getType());
                rseCompileType.setOrigin(systemCompileType);
                rseCompileTypes.add(rseCompileType);
            }
        }

        return rseCompileTypes.toArray(new RSECompileType[rseCompileTypes.size()]);
    }

    public static RSECommand[] getCommands(RSEProfile rseProfile) {

        ArrayList<RSECommand> allCommands = new ArrayList<RSECommand>();

        RSECompileType[] compileTypes = getCompileTypes(rseProfile);
        for (int idx1 = 0; idx1 < compileTypes.length; idx1++) {
            RSECommand[] commands = getCommands(compileTypes[idx1]);
            for (int idx2 = 0; idx2 < commands.length; idx2++) {
                allCommands.add(commands[idx2]);
            }
        }

        RSECommand[] _commands = new RSECommand[allCommands.size()];
        allCommands.toArray(_commands);

        return _commands;
    }

    public static RSECommand[] getCommands(RSECompileType compileType) {

        SystemCompileCommand[] commands = ((SystemCompileType)compileType.getOrigin()).getCompileCommandsArray();

        ArrayList<RSECommand> rseCommands = new ArrayList<RSECommand>();

        for (int idx = 0; idx < commands.length; idx++) {
            RSECommand rseCommand = new RSECommand(compileType, commands[idx].getLabel(), commands[idx].getDefaultString(), commands[idx]);
            rseCommand.setCommandStringEditable(commands[idx].isCommandStringEditable());
            rseCommand.setLabelEditable(commands[idx].isLabelEditable());
            rseCommand.setId(commands[idx].getId());
            rseCommand.setNature(commands[idx].getNature());
            rseCommand.setMenuOption(commands[idx].getMenuOption());
            rseCommands.add(rseCommand);
        }

        RSECommand[] _rseCommands = new RSECommand[rseCommands.size()];
        rseCommands.toArray(_rseCommands);
        return _rseCommands;

    }

    public static void createCommand(RSECompileType compileType, String label, boolean isLabelEditable, String commandString,
        boolean isCommandStringEditable, String id, String nature, String menuOption) {

        SystemCompileType type = (SystemCompileType)compileType.getOrigin();
        if (type != null) {
            SystemCompileCommand compileCommand = new SystemCompileCommand(type);

            compileCommand.setId(id);
            compileCommand.setNature(nature);
            compileCommand.setMenuOption(menuOption);

            compileCommand.setLabel(label);
            compileCommand.setLabelEditable(isLabelEditable);

            compileCommand.setDefaultString(commandString);
            compileCommand.setCommandStringEditable(isCommandStringEditable);

            type.addCompileCommand(compileCommand);
        }

    }

    public static void deleteCommand(RSECompileType compileType, String label) {

        SystemCompileType type = (SystemCompileType)compileType.getOrigin();
        if (type != null) {
            for (SystemCompileCommand compileCommand : type.getCompileCommandsArray()) {
                if (compileCommand.getLabel().equals(label)) {
                    type.removeCompileCommand(compileCommand);
                    return;
                }
            }

        }
    }

}
