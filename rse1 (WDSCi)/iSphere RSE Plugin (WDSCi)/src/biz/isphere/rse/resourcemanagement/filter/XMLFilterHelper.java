/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import biz.isphere.core.resourcemanagement.filter.RSEFilter;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;

public class XMLFilterHelper {

    private static final String FILTERPOOLS = "filterpools";
    private static final String FILTERPOOL = "filterpool";
    private static final String FILTERS = "filters";
    private static final String FILTER = "filter";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String FILTERSTRINGS = "filterstrings";
    private static final String FILTERSTRING = "filterstring";

    public static void saveFiltersToXML(File toFile, boolean singleFilterPool, RSEFilter[] filters) throws Exception {

        Arrays.sort(filters);

        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(toFile));

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        eventWriter.add(eventFactory.createStartDocument());

        if (singleFilterPool) {

            createFilters(eventWriter, eventFactory, end, tab, filters);

        } else {

            eventWriter.add(eventFactory.createStartElement("", "", FILTERPOOLS));
            eventWriter.add(end);

            Map<String, List<RSEFilter>> _pools = new TreeMap<String, List<RSEFilter>>();
            for (int idx = 0; idx < filters.length; idx++) {
                String _pool = filters[idx].getFilterPool().getName();
                List<RSEFilter> _filters = (List<RSEFilter>)_pools.get(_pool);
                if (_filters == null) {
                    _filters = new LinkedList<RSEFilter>();
                    _pools.put(_pool, _filters);
                }
                _filters.add(filters[idx]);
            }

            for (Map.Entry<String, List<RSEFilter>> entry : _pools.entrySet()) {

                eventWriter.add(eventFactory.createStartElement("", "", FILTERPOOL));
                eventWriter.add(end);

                createNode(eventWriter, eventFactory, end, tab, NAME, entry.getKey());

                RSEFilter[] _filters = new RSEFilter[entry.getValue().size()];
                entry.getValue().toArray(_filters);
                createFilters(eventWriter, eventFactory, end, tab, _filters);

                eventWriter.add(eventFactory.createEndElement("", "", FILTERPOOL));
                eventWriter.add(end);

            }

            eventWriter.add(eventFactory.createEndElement("", "", FILTERPOOLS));
            eventWriter.add(end);

        }

        eventWriter.add(eventFactory.createEndDocument());

        eventWriter.close();

    }

    private static void createFilters(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, RSEFilter[] filters)
        throws XMLStreamException {

        eventWriter.add(eventFactory.createStartElement("", "", FILTERS));
        eventWriter.add(end);

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

        eventWriter.add(eventFactory.createEndElement("", "", FILTERS));
        eventWriter.add(end);

    }

    private static void createNode(XMLEventWriter eventWriter, XMLEventFactory eventFactory, XMLEvent end, XMLEvent tab, String name, String value)
        throws XMLStreamException {

        eventWriter.add(tab);
        eventWriter.add(eventFactory.createStartElement("", "", name));

        eventWriter.add(eventFactory.createCharacters(value));

        eventWriter.add(eventFactory.createEndElement("", "", name));
        eventWriter.add(end);

    }

    public static RSEFilter[] restoreFiltersFromXML(File fromFile, boolean singleFilterPool, RSEProfile profile, RSEFilterPool filterPool)
        throws Exception {

        ArrayList<RSEFilter> items = new ArrayList<RSEFilter>();
        ArrayList<String> filterStrings = null;

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();

        InputStream in = new FileInputStream(fromFile);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

        RSEFilterPool pool = null;
        if (singleFilterPool) {
            pool = filterPool;
        }
        RSEFilter filter = null;
        String lastElement = null;

        while (eventReader.hasNext()) {

            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                if (event.asStartElement().getName().getLocalPart().equals(FILTERPOOL)) {
                    lastElement = FILTERPOOL;
                    pool = new RSEFilterPool();
                    pool.setProfile(profile);
                } else if (event.asStartElement().getName().getLocalPart().equals(FILTER)) {
                    lastElement = FILTER;
                    filter = new RSEFilter(true);
                    filter.setFilterPool(pool);
                } else if (event.asStartElement().getName().getLocalPart().equals(NAME)) {
                    event = eventReader.nextEvent();
                    if (lastElement.equals(FILTERPOOL)) {
                        pool.setName(event.asCharacters().getData());
                    } else if (lastElement.equals(FILTER)) {
                        filter.setName(event.asCharacters().getData());
                    }
                } else if (event.asStartElement().getName().getLocalPart().equals(TYPE)) {
                    event = eventReader.nextEvent();
                    filter.setType(event.asCharacters().getData());
                } else if (event.asStartElement().getName().getLocalPart().equals(FILTERSTRINGS)) {
                    filterStrings = new ArrayList<String>();
                } else if (event.asStartElement().getName().getLocalPart().equals(FILTERSTRING)) {
                    event = eventReader.nextEvent();
                    filterStrings.add(event.asCharacters().getData());
                }
            } else if (event.isEndElement()) {
                if (event.asEndElement().getName().getLocalPart().equals(FILTER)) {
                    items.add(filter);
                } else if (event.asEndElement().getName().getLocalPart().equals(FILTERSTRINGS)) {
                    String[] _filterStrings = new String[filterStrings.size()];
                    filterStrings.toArray(_filterStrings);
                    filter.setFilterStrings(_filterStrings);
                }
            }

        }

        RSEFilter[] filters = new RSEFilter[items.size()];
        items.toArray(filters);

        return filters;
    }

}
