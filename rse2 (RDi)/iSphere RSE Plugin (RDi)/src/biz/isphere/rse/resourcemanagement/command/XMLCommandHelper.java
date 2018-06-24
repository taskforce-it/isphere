/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import biz.isphere.base.internal.BooleanHelper;
import biz.isphere.core.resourcemanagement.command.RSECommand;
import biz.isphere.core.resourcemanagement.command.RSECompileType;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;

public class XMLCommandHelper {

    private static final String COMPILE_TYPES = "compiletypes";
    private static final String COMPILE_TYPE = "compiletype";
    private static final String TYPE = "type";
    private static final String COMMANDS = "commands";
    private static final String ID = "id";
    private static final String COMMAND = "command";
    private static final String COMMAND_STRING_EDITABLE = "isCommandEditable";
    private static final String LABEL = "label";
    private static final String LABEL_EDITABLE = "isLabelEditable";
    private static final String COMMAND_STRING = "commandString";
    private static final String NATURE = "nature";
    private static final String MENU_OPTION = "menuOption";

    public static void saveCommandsToXML(File toFile, boolean singleCompileType, RSECommand[] commands) throws Exception {

        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(toFile));

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        eventWriter.add(eventFactory.createStartDocument());

        if (singleCompileType) {

            createCommands(eventWriter, eventFactory, end, tab, commands);

        } else {

            eventWriter.add(eventFactory.createStartElement("", "", COMPILE_TYPES));
            eventWriter.add(end);

            HashMap<String, ArrayList<RSECommand>> _pools = new HashMap<String, ArrayList<RSECommand>>();
            for (int idx = 0; idx < commands.length; idx++) {
                String _pool = commands[idx].getCompileType().getType();
                ArrayList<RSECommand> _commands = (ArrayList<RSECommand>)_pools.get(_pool);
                if (_commands == null) {
                    _commands = new ArrayList<RSECommand>();
                    _pools.put(_pool, _commands);
                }
                _commands.add(commands[idx]);
            }

            for (Map.Entry<String, ArrayList<RSECommand>> entry : _pools.entrySet()) {

                eventWriter.add(eventFactory.createStartElement("", "", COMPILE_TYPE));
                eventWriter.add(end);

                createNode(eventWriter, eventFactory, end, tab, TYPE, entry.getKey());

                RSECommand[] _commands = new RSECommand[entry.getValue().size()];
                entry.getValue().toArray(_commands);
                createCommands(eventWriter, eventFactory, end, tab, _commands);

                eventWriter.add(eventFactory.createEndElement("", "", COMPILE_TYPE));
                eventWriter.add(end);

            }

            eventWriter.add(eventFactory.createEndElement("", "", COMPILE_TYPES));
            eventWriter.add(end);

        }

        eventWriter.add(eventFactory.createEndDocument());

        eventWriter.close();

    }

    private static void createCommands(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, RSECommand[] commands)
        throws XMLStreamException {

        eventWriter.add(eventFactory.createStartElement("", "", COMMANDS));
        eventWriter.add(end);

        for (int idx1 = 0; idx1 < commands.length; idx1++) {

            eventWriter.add(eventFactory.createStartElement("", "", COMMAND));
            eventWriter.add(end);

            createNode(eventWriter, eventFactory, end, tab, ID, commands[idx1].getId());
            createNode(eventWriter, eventFactory, end, tab, LABEL, commands[idx1].getLabel());
            createNode(eventWriter, eventFactory, end, tab, LABEL_EDITABLE, commands[idx1].isLabelEditable());
            createNode(eventWriter, eventFactory, end, tab, COMMAND_STRING, commands[idx1].getCommandString());
            createNode(eventWriter, eventFactory, end, tab, COMMAND_STRING_EDITABLE, commands[idx1].isCommandStringEditable());
            createNode(eventWriter, eventFactory, end, tab, NATURE, commands[idx1].getNature());
            createNode(eventWriter, eventFactory, end, tab, MENU_OPTION, commands[idx1].getMenuOption());

            eventWriter.add(eventFactory.createEndElement("", "", COMMAND));
            eventWriter.add(end);

        }

        eventWriter.add(eventFactory.createEndElement("", "", COMMANDS));
        eventWriter.add(end);

    }

    private static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, String name, boolean value)
        throws XMLStreamException {
        createNode(eventWriter, eventFactory, end, tab, name, Boolean.toString(value));
    }

    private static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, String name, String value)
        throws XMLStreamException {

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", name));

        eventWriter.add(eventFactory.createCharacters(value));

        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(end);

    }

    public static RSECommand[] restoreCommandsFromXML(File fromFile, boolean singleCompileType, RSEProfile profile, RSECompileType compileType)
        throws Exception {

        ArrayList<RSECommand> items = new ArrayList<RSECommand>();

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        InputStream in = new FileInputStream(fromFile);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

        RSECompileType type = null;
        if (singleCompileType) {
            type = compileType;
        }
        RSECommand command = null;
        StringBuilder elementData = null;

        while (eventReader.hasNext()) {

            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(COMPILE_TYPE)) {
                    type = new RSECompileType(profile);
                } else if (event.asStartElement().getName().getLocalPart().equals(TYPE)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(COMMAND)) {
                    command = new RSECommand();
                    command.setCompileType(type);
                } else if (event.asStartElement().getName().getLocalPart().equals(ID)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(LABEL)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(LABEL_EDITABLE)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(COMMAND_STRING)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(COMMAND_STRING_EDITABLE)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(NATURE)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(MENU_OPTION)) {
                    event = eventReader.nextEvent();
                    elementData = new StringBuilder(event.asCharacters().getData());
                } else {
                    elementData = null;
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(TYPE)) {
                    type.setType(elementData.toString());
                } else if (event.asEndElement().getName().getLocalPart().equals(ID)) {
                    command.setId(elementData.toString());
                } else if (event.asEndElement().getName().getLocalPart().equals(LABEL)) {
                    command.setLabel(elementData.toString());
                } else if (event.asEndElement().getName().getLocalPart().equals(LABEL_EDITABLE)) {
                    command.setLabelEditable(xmlToBoolean(elementData.toString(), true));
                } else if (event.asEndElement().getName().getLocalPart().equals(COMMAND_STRING)) {
                    command.setCommandString(elementData.toString());
                } else if (event.asEndElement().getName().getLocalPart().equals(COMMAND_STRING_EDITABLE)) {
                    command.setCommandStringEditable(xmlToBoolean(elementData.toString(), true));
                } else if (event.asEndElement().getName().getLocalPart().equals(NATURE)) {
                    command.setNature(elementData.toString());
                } else if (event.asEndElement().getName().getLocalPart().equals(MENU_OPTION)) {
                    command.setMenuOption(elementData.toString());
                } else if (event.asEndElement().getName().getLocalPart().equals(COMMAND)) {
                    items.add(command);
                }
                elementData = null;
            } else if (event.isCharacters()) {
                if (elementData != null) {
                    elementData.append(event.asCharacters().getData());
                }
            }

        }

        RSECommand[] commands = new RSECommand[items.size()];
        items.toArray(commands);

        return commands;
    }

    private static boolean xmlToBoolean(String value, boolean defaultValue) {
        return BooleanHelper.tryParseBoolean(value, defaultValue);
    }

}
