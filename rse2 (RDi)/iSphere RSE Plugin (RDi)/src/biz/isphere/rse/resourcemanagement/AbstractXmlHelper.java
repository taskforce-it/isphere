/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import biz.isphere.base.internal.BooleanHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.resourcemanagement.XmlVersion;

public abstract class AbstractXmlHelper {

    protected static final String CONTAINER = "container";

    private static final String ARRAY_DEFAULT_DELIMITER = ";";
    private static final String ARRAY_DELIMITERS = " ,\t\n\r\f" + ARRAY_DEFAULT_DELIMITER;

    protected static boolean isContainerStartElement(XMLEvent event) {

        if (event.isStartElement()) {
            if (event.asStartElement().getName().getLocalPart().equals(CONTAINER)) {
                return true;
            }
        }

        return false;
    }

    protected static String getVersionNumber(XMLEvent event) throws Exception {

        StartElement startElement = event.asStartElement();
        Attribute versionAttribute = startElement.getAttributeByName(new QName("version"));
        if (versionAttribute != null) {
            String currentVersionNumber = versionAttribute.getValue();
            return currentVersionNumber;
        }

        return null;
    }

    protected static boolean validateVersionNumber(XMLEvent event, String minVersionNumber) throws Exception {

        StartElement startElement = event.asStartElement();
        Attribute versionAttribute = startElement.getAttributeByName(new QName("version"));
        if (versionAttribute != null) {
            String currentVersionNumber = versionAttribute.getValue();
            XmlVersion currentVersion = new XmlVersion(currentVersionNumber);
            XmlVersion minVersion = new XmlVersion(minVersionNumber);
            if (currentVersion.compareTo(minVersion) < 0) {
                return false;
            }
        }

        return true;
    }

    protected static void startContainer(XMLEventWriter eventWriter, XMLEventFactory eventFactory, String version, XMLEvent end)
        throws XMLStreamException {

        Attribute versionAttribute = eventFactory.createAttribute("version", version);
        List<?> attributeList = Arrays.asList(versionAttribute);
        List<?> nsList = Arrays.asList();

        StartElement commandsElement = eventFactory.createStartElement("", "", CONTAINER, attributeList.iterator(), nsList.iterator());
        eventWriter.add(commandsElement);
        eventWriter.add(end);

    }

    protected static void endContainer(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end) throws XMLStreamException {

        eventWriter.add(eventFactory.createEndElement("", "", CONTAINER));
        eventWriter.add(end);
    }

    protected static String arrayToXml(String[] fileTypes) {

        StringBuilder buffer = new StringBuilder();

        for (String fileType : fileTypes) {
            if (buffer.length() > 0) {
                buffer.append(ARRAY_DEFAULT_DELIMITER);
            }
            buffer.append(fileType);
        }

        return buffer.toString();
    }

    protected static String integerToXml(int value) {
        return Integer.toString(value);
    }

    protected static String booleanToXml(boolean value) {
        return Boolean.toString(value);
    }

    protected static String[] xmlToArray(String xml) {

        StringTokenizer st = new StringTokenizer(xml, ARRAY_DELIMITERS);

        int n = st.countTokens();
        String[] fileTypes = new String[n];
        for (int i = 0; i < n; i++) {
            fileTypes[i] = st.nextToken();
        }

        return fileTypes;
    }

    protected static int xmlToInteger(String xml) {
        return IntHelper.tryParseInt(xml, 0);
    }

    protected static boolean xmlToBoolean(String value, boolean defaultValue) {
        return BooleanHelper.tryParseBoolean(value, defaultValue);
    }

    protected static void startElementCharacters(StringBuilder elementData, XMLEvent event) {
        clearElementCharacters(elementData);
    }

    protected static void collectElementCharacters(StringBuilder elementData, XMLEvent event) {

        if (event.isCharacters()) {
            elementData.append(event.asCharacters().getData());
        }
    }

    protected static void clearElementCharacters(StringBuilder elementData) {
        elementData.replace(0, elementData.length(), ""); //$NON-NLS-1$
    }

    protected static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, String name, boolean value)
        throws XMLStreamException {
        createNode(eventWriter, eventFactory, end, tab, name, booleanToXml(value));
    }

    protected static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, String name, String value)
        throws XMLStreamException {

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", name));

        eventWriter.add(eventFactory.createCharacters(value));

        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(end);

    }

}
