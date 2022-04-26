/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.rsemanagement.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import biz.isphere.core.rsemanagement.filter.RSEFilter;
import biz.isphere.core.rsemanagement.filter.RSEFilterPool;

public class XMLFilterHelper {

    private static final String FILTERS = "filters";
    private static final String FILTER = "filter";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String FILTERSTRINGS = "filterstrings";
    private static final String FILTERSTRING = "filterstring";

    public static boolean saveFiltersToXML(File toFile, RSEFilter[] filters) {

        try {

            // Create output factory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            // Create event writer
            XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(toFile));

            // Create event factory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            XMLEvent tab = eventFactory.createDTD("\t");

            // Start document
            eventWriter.add(eventFactory.createStartDocument());

            // Start filters
            eventWriter.add(eventFactory.createStartElement("", "", FILTERS));
            eventWriter.add(end);

            // Write filter nodes
            for (int idx1 = 0; idx1 < filters.length; idx1++) {

                eventWriter.add(eventFactory.createStartElement("", "", FILTER));
                eventWriter.add(end);

                createNode(eventWriter, eventFactory, end, tab, NAME, filters[idx1].getName());
                createNode(eventWriter, eventFactory, end, tab, TYPE, filters[idx1].getType());

                eventWriter.add(eventFactory.createStartElement("", "", FILTERSTRINGS));
                eventWriter.add(end);

                for (int idx2 = 0; idx2 < filters[idx1].getFilterStrings().length; idx2++) {
                    createNode(eventWriter, eventFactory, end, tab, FILTERSTRING, filters[idx1].getFilterStrings()[idx2]);
                }

                eventWriter.add(eventFactory.createEndElement("", "", FILTERSTRINGS));
                eventWriter.add(end);

                eventWriter.add(eventFactory.createEndElement("", "", FILTER));
                eventWriter.add(end);

            }

            // End filters
            eventWriter.add(eventFactory.createEndElement("", "", FILTERS));
            eventWriter.add(end);

            // End document
            eventWriter.add(eventFactory.createEndDocument());

            // Close Document
            eventWriter.close();

            return true;

        } catch (Exception e) {

            return false;

        }

    }

    private static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, String name, String value) throws XMLStreamException {

        // Start node
        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", name));

        // Write content
        eventWriter.add(eventFactory.createCharacters(value));

        // End node
        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(end);

    }

    public static RSEFilter[] restoreFiltersFromXML(File fromFile, RSEFilterPool filterPool) {

        try {

            ArrayList<RSEFilter> items = new ArrayList<RSEFilter>();
            ArrayList<String> filterStrings = null;

            // Create input factory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Create event reader
            InputStream in = new FileInputStream(fromFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            // Read the XML document
            RSEFilter item = null;

            while (eventReader.hasNext()) {

                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    if (event.asStartElement().getName().getLocalPart().equals(FILTER)) {
                        item = new RSEFilter(true);
                        item.setFilterPool(filterPool);
                    }
                    else if (event.asStartElement().getName().getLocalPart().equals(NAME)) {
                        event = eventReader.nextEvent();
                        item.setName(event.asCharacters().getData());
                    }
                    else if (event.asStartElement().getName().getLocalPart().equals(TYPE)) {
                        event = eventReader.nextEvent();
                        item.setType(event.asCharacters().getData());
                    }
                    else if (event.asStartElement().getName().getLocalPart().equals(FILTERSTRINGS)) {
                        filterStrings = new ArrayList<String>();
                    }
                    else if (event.asStartElement().getName().getLocalPart().equals(FILTERSTRING)) {
                        event = eventReader.nextEvent();
                        filterStrings.add(event.asCharacters().getData());
                    }
                }
                else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart().equals(FILTER)) {
                        items.add(item);
                    }
                    else if (event.asEndElement().getName().getLocalPart().equals(FILTERSTRINGS)) {
                        String[] _filterStrings = new String[filterStrings.size()];
                        filterStrings.toArray(_filterStrings);
                        item.setFilterStrings(_filterStrings);
                    }
                }

            }

            RSEFilter[] filters = new RSEFilter[items.size()];
            items.toArray(filters);

            return filters;


        } catch (Exception e) {

            return null;

        }

    }

}
