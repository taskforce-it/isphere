/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import biz.isphere.core.resourcemanagement.InvalidRepositoryVersionException;
import biz.isphere.core.resourcemanagement.command.CommandXmlComparator;
import biz.isphere.core.resourcemanagement.command.DuplicateCommandException;
import biz.isphere.core.resourcemanagement.command.RSECommand;
import biz.isphere.core.resourcemanagement.command.RSECompileType;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.rse.resourcemanagement.AbstractXmlHelper;
import biz.isphere.rse.resourcemanagement.XMLPrettyPrintWriter;

public class XMLCommandHelper extends AbstractXmlHelper {

    private static final String CURRENT_VERSION = "1.0.0";
    private static final String MIN_VERSION = "1.0.0";

    private static final String COMPILE_TYPES = "compiletypes";
    private static final String COMPILE_TYPE = "compiletype";
    private static final String COMPILE_TYPE_TYPE = "type";

    private static final String COMMANDS = "commands";
    private static final String ID = "id";
    private static final String COMMAND = "command";
    private static final String COMMAND_STRING_EDITABLE = "isCommandEditable";
    private static final String ORDER = "order";
    private static final String LABEL = "label";
    private static final String LABEL_EDITABLE = "isLabelEditable";
    private static final String DEFAULT_COMMAND_STRING = "defaultCommandString";
    private static final String CURRENT_COMMAND_STRING = "currentCommandString";
    private static final String NATURE = "nature";
    private static final String MENU_OPTION = "menuOption";

    public static void saveCommandsToXML(File toFile, boolean singleCompileType, RSECommand[] commands) throws Exception {

        Arrays.sort(commands, new CommandXmlComparator());

        XMLPrettyPrintWriter streamWriter = createXMLStreamWriter(toFile);

        streamWriter.writeStartDocument();

        if (singleCompileType) {

            startContainer(streamWriter, CURRENT_VERSION);

            createCommands(streamWriter, commands);

            endContainer(streamWriter);

        } else {

            startContainer(streamWriter, CURRENT_VERSION);

            streamWriter.writeStartElement(COMPILE_TYPES);

            Map<String, List<RSECommand>> _pools = new TreeMap<String, List<RSECommand>>();
            for (int idx = 0; idx < commands.length; idx++) {
                String _pool = commands[idx].getCompileType().getType();
                List<RSECommand> _commands = (List<RSECommand>)_pools.get(_pool);
                if (_commands == null) {
                    _commands = new LinkedList<RSECommand>();
                    _pools.put(_pool, _commands);
                }
                _commands.add(commands[idx]);
            }

            for (Map.Entry<String, List<RSECommand>> entry : _pools.entrySet()) {

                streamWriter.writeStartElement(COMPILE_TYPE);

                createNode(streamWriter, COMPILE_TYPE_TYPE, entry.getKey());

                RSECommand[] _commands = new RSECommand[entry.getValue().size()];
                entry.getValue().toArray(_commands);
                createCommands(streamWriter, _commands);

                streamWriter.writeEndElement();

            }

            streamWriter.writeEndElement();

            endContainer(streamWriter);

        }

        streamWriter.writeEndDocument();

        streamWriter.flush();
        streamWriter.close();

    }

    private static void createCommands(XMLPrettyPrintWriter streamWriter, RSECommand[] commands) throws XMLStreamException {

        streamWriter.writeStartElement(COMMANDS);

        for (int idx1 = 0; idx1 < commands.length; idx1++) {

            streamWriter.writeStartElement(COMMAND);

            createNode(streamWriter, ID, commands[idx1].getId());
            createNode(streamWriter, ORDER, integerToXml(commands[idx1].getOrder()));
            createNode(streamWriter, LABEL, commands[idx1].getLabel());
            createNode(streamWriter, LABEL_EDITABLE, commands[idx1].isLabelEditable());
            createNode(streamWriter, DEFAULT_COMMAND_STRING, commands[idx1].getDefaultCommandString());
            createNode(streamWriter, CURRENT_COMMAND_STRING, commands[idx1].getCurrentCommandString());
            createNode(streamWriter, COMMAND_STRING_EDITABLE, commands[idx1].isCommandStringEditable());
            createNode(streamWriter, NATURE, commands[idx1].getNature());
            createNode(streamWriter, MENU_OPTION, commands[idx1].getMenuOption());

            streamWriter.writeEndElement();

        }

        streamWriter.writeEndElement();

    }

    public static RSECommand[] restoreCommandsFromXML(File fromFile, boolean singleCompileType, RSEProfile profile, RSECompileType compileType)
        throws Exception {

        Set<String> keys = new HashSet<String>();

        ArrayList<RSECommand> items = new ArrayList<RSECommand>();

        XMLEventReader eventReader = null;

        try {

            eventReader = createXMLEventReader(fromFile);

            RSECompileType type = null;
            if (singleCompileType) {
                type = compileType;
            }
            RSECommand command = null;
            StringBuilder elementData = new StringBuilder();

            boolean isValidated = false;
            String versionNumber = null;

            while (eventReader.hasNext()) {

                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    // Element: Container
                    if (isContainerStartElement(event)) {
                        versionNumber = getVersionNumber(event);
                        isValidated = validateVersionNumber(event, MIN_VERSION);
                    } else
                    // Element: Compile type
                    if (event.asStartElement().getName().getLocalPart().equals(COMPILE_TYPE)) {
                        type = new RSECompileType(profile);
                    } else if (event.asStartElement().getName().getLocalPart().equals(COMPILE_TYPE_TYPE)) {
                        startElementCharacters(elementData, event);
                    } else
                    // Element: Command
                    if (event.asStartElement().getName().getLocalPart().equals(COMMAND)) {
                        command = new RSECommand();
                        command.setCompileType(type);
                    } else if (event.asStartElement().getName().getLocalPart().equals(ID)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(ORDER)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(LABEL)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(LABEL_EDITABLE)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(DEFAULT_COMMAND_STRING)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(CURRENT_COMMAND_STRING)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(COMMAND_STRING_EDITABLE)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(NATURE)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(MENU_OPTION)) {
                        startElementCharacters(elementData, event);
                    } else {
                        clearElementCharacters(elementData);
                    }
                } else if (event.isEndElement()) {
                    if (!isValidated) {
                        throw new InvalidRepositoryVersionException(versionNumber);
                    }
                    if (event.asEndElement().getName().getLocalPart().equals(COMPILE_TYPE_TYPE)) {
                        type.setType(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(ID)) {
                        command.setId(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(ORDER)) {
                        command.setOrder(xmlToInteger(elementData.toString()));
                    } else if (event.asEndElement().getName().getLocalPart().equals(LABEL)) {
                        command.setLabel(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(LABEL_EDITABLE)) {
                        command.setLabelEditable(xmlToBoolean(elementData.toString(), true));
                    } else if (event.asEndElement().getName().getLocalPart().equals(DEFAULT_COMMAND_STRING)) {
                        command.setDefaultCommandString(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(CURRENT_COMMAND_STRING)) {
                        command.setCurrentCommandString(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(COMMAND_STRING_EDITABLE)) {
                        command.setCommandStringEditable(xmlToBoolean(elementData.toString(), true));
                    } else if (event.asEndElement().getName().getLocalPart().equals(NATURE)) {
                        command.setNature(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(MENU_OPTION)) {
                        command.setMenuOption(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(COMMAND)) {

                        String commandKey = command.getKey();
                        if (keys.contains(commandKey)) {
                            throw new DuplicateCommandException();
                        }

                        items.add(command);
                        keys.add(commandKey);
                    }
                    clearElementCharacters(elementData);
                } else if (event.isCharacters()) {
                    collectElementCharacters(elementData, event);
                }

            }

        } finally {
            if (eventReader != null) {
                eventReader.close();
            }
        }

        RSECommand[] commands = new RSECommand[items.size()];
        items.toArray(commands);

        return commands;
    }

}
