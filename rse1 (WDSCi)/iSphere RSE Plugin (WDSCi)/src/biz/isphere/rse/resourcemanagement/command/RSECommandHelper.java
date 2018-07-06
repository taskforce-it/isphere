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

        SystemCompileManager compileManager = getCompileManager();
        SystemProfile systemProfile = getSystemProfile(rseProfile.getName());
        SystemCompileProfile compileProfile = compileManager.getCompileProfile(systemProfile);
        Vector<?> compileTypes = compileProfile.getCompileTypes();
        for (Object object : compileTypes) {
            if (object instanceof SystemCompileType) {
                SystemCompileType systemCompileType = (SystemCompileType)object;
                RSECompileType rseCompileType = new RSECompileType(rseProfile, systemCompileType.getType(), systemCompileType);
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

        for (SystemCompileCommand command : commands) {
            RSECommand rseCommand = produceCommand(compileType, command);
            rseCommands.add(rseCommand);
        }

        RSECommand[] _rseCommands = new RSECommand[rseCommands.size()];
        rseCommands.toArray(_rseCommands);

        return _rseCommands;
    }

    public static RSECommand getCommand(RSECompileType compileType, String label) {

        SystemCompileCommand systemCompileCommand = findCompileCommand(compileType.getProfile().getName(), compileType.getType(), label);
        if (systemCompileCommand != null) {

            RSECommand rseCommand = produceCommand(compileType, systemCompileCommand);

            return rseCommand;
        }

        return null;
    }

    private static SystemCompileCommand findCompileCommand(String systemProfileName, String compileTypeType, String label) {

        SystemCompileManager compileManager = getCompileManager();
        SystemProfile systemProfile = getSystemProfile(systemProfileName);
        if (systemProfile == null) {
            return null;
        }

        SystemCompileProfile systemCompileProfile = compileManager.getCompileProfile(systemProfile);
        if (systemCompileProfile == null) {
            return null;
        }

        SystemCompileType systemCompileType = systemCompileProfile.getCompileType(compileTypeType);
        if (systemCompileType == null) {
            return null;
        }

        SystemCompileCommand systemCompileCommand = systemCompileType.getCompileLabel(label);

        return systemCompileCommand;
    }

    private static RSECommand produceCommand(RSECompileType compileType, SystemCompileCommand systemCompileCommand) {

        RSECommand rseCommand = new RSECommand(compileType, systemCompileCommand.getLabel(), systemCompileCommand.isLabelEditable(),
            systemCompileCommand.getCurrentString(), systemCompileCommand.isCommandStringEditable(), systemCompileCommand.getId(),
            systemCompileCommand.getNature(), systemCompileCommand.getMenuOption(), systemCompileCommand.getOrder(), systemCompileCommand);

        return rseCommand;
    }

    public static void createCommand(RSECompileType compileType, String label, boolean isLabelEditable, String commandString,
        boolean isCommandStringEditable, String id, String nature, String menuOption, int order) {

        SystemCompileManager compileManager = getCompileManager();
        SystemProfile systemProfile = getSystemProfile(compileType.getProfile().getName());

        SystemCompileType type = (SystemCompileType)compileType.getOrigin();
        if (type == null) {

            RSECompileType[] rseTypes = getCompileTypes(compileType.getProfile());
            for (RSECompileType rseType : rseTypes) {
                if (rseType.getType().equals(compileType.getType())) {
                    type = (SystemCompileType)rseType.getOrigin();
                }
            }

            if (type == null) {
                SystemCompileProfile compileProfile = compileManager.getCompileProfile(systemProfile);
                if (compileProfile != null) {

                    SystemCompileType systemCompileType = new SystemCompileType(compileProfile, compileType.getType());
                    compileProfile.addCompileType(systemCompileType);
                    compileProfile.writeToDisk();

                    type = systemCompileType;
                }
            }
        }

        if (type != null) {
            SystemCompileCommand compileCommand = new SystemCompileCommand(type);

            compileCommand.setId(id);
            compileCommand.setNature(nature);
            compileCommand.setMenuOption(menuOption);

            compileCommand.setOrder(order);
            compileCommand.setLabel(label);
            compileCommand.setLabelEditable(isLabelEditable);

            compileCommand.setDefaultString(commandString);
            compileCommand.setCurrentString(commandString);
            compileCommand.setCommandStringEditable(isCommandStringEditable);

            type.addCompileCommand(compileCommand);

            type.getParentProfile().writeToDisk();
        }

    }

    public static void deleteCommand(RSECompileType compileType, String label) {

        SystemCompileType type = (SystemCompileType)compileType.getOrigin();
        if (type != null) {
            for (SystemCompileCommand compileCommand : type.getCompileCommandsArray()) {
                if (compileCommand.getLabel().equals(label)) {
                    type.removeCompileCommand(compileCommand);
                    type.getParentProfile().writeToDisk();
                }
            }

        }
    }

    public static void updateCommand(RSECompileType compileType, String label, boolean isLabelEditable, String commandString,
        boolean isCommandStringEditable, String id, String nature, String menuOption, int Order) {

        SystemCompileCommand systemCompileCommand = findCompileCommand(compileType.getProfile().getName(), compileType.getType(), label);
        if (systemCompileCommand != null) {
            systemCompileCommand.setCurrentString(commandString);
            SystemCompileType type = (SystemCompileType)compileType.getOrigin();
            type.getParentProfile().writeToDisk();
        }
    }

    private static SystemProfile getSystemProfile(String name) {
        return SystemStartHere.getSystemRegistry().getSystemProfile(name);
    }

    private static SystemCompileManager getCompileManager() {

        SubSystemFactory subSystemConfiguration = getSubSystemConfiguration();
        SystemCompileManager compileManager = new ISeriesCompileManager();
        compileManager.setSubSystemFactory(subSystemConfiguration);

        return compileManager;
    }

}
