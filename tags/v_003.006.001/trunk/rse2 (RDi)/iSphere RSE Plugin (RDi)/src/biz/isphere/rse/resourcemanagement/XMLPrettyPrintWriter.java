/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement;

import java.util.Stack;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLPrettyPrintWriter {

    private enum State {
        UNKNOWN,
        ELEMENT,
        CHARACTERS
    }

    private static final String NEW_LINE = "\n";
    private static final String INDENTION = "  ";

    private XMLStreamWriter writer;
    private State currentState;
    private Stack<State> stateStack;
    private StringBuilder indention;

    public XMLPrettyPrintWriter(XMLStreamWriter writer) {

        this.writer = writer;

        this.stateStack = new Stack<State>();
        this.currentState = State.UNKNOWN;
    }

    public void writeStartDocument() throws XMLStreamException {
        writer.writeStartDocument();
    }

    public void writeEndDocument() throws XMLStreamException {
        writer.writeEndDocument();
    }

    public void writeStartElement(String name) throws XMLStreamException {
        performStartElement();
        writer.writeStartElement(name);
    }

    public void writeEndElement() throws XMLStreamException {
        performEndElement();
        writer.writeEndElement();
    }

    public void writeAttribute(String name, String value) throws XMLStreamException {
        writer.writeAttribute(name, value);
    }

    public void writeCharacters(String value) throws XMLStreamException {
        performWriteCharacters(value);
        writer.writeCharacters(value);
    }

    public void flush() throws XMLStreamException {
        writer.flush();
    }

    public void close() throws XMLStreamException {
        writer.close();
    }

    private void performStartElement() throws XMLStreamException {

        writer.writeCharacters(NEW_LINE);
        writer.writeCharacters(getIndention(getDepth()));

        stateStack.push(currentState);
        currentState = State.ELEMENT;
    }

    private void performEndElement() throws XMLStreamException {

        if (currentState != State.CHARACTERS) {
            writer.writeCharacters(NEW_LINE);
            writer.writeCharacters(getIndention(getDepth() - 1));
        }

        currentState = stateStack.pop();
    }

    private void performWriteCharacters(String value) throws XMLStreamException {

        currentState = State.CHARACTERS;
    }

    private int getDepth() {
        return stateStack.size();
    }

    private String getIndention(int depth) {

        if (indention == null) {
            indention = new StringBuilder();
        }

        int requiredIndentionLength = getRequiredIndentionLength(depth);
        while (indention.length() < requiredIndentionLength) {
            indention.append(INDENTION);
        }

        return indention.toString().substring(0, requiredIndentionLength);
    }

    private int getRequiredIndentionLength(int depth) {
        return depth * INDENTION.length();
    }
}
