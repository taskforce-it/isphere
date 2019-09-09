/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.joblogexplorer.editor.tableviewer.filters.AbstractIntegerFilter;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyEvent;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyListener;

public class JobLogMessage {

    public static final int SEVERITY_BLANK = AbstractIntegerFilter.NULL_VALUE;

    private int pageNumber;
    private boolean selected;

    private List<MessageModifyListener> listeners;

    private String id;
    private String type;
    private String severity;
    private String date;
    private String time;
    private String text;
    private String help;

    private String toLibrary;
    private String toProgram;
    private String toModule;
    private String toProcedure;
    private String toStatement;

    private String fromLibrary;
    private String fromProgram;
    private String fromModule;
    private String fromProcedure;
    private String fromStatement;

    private int severityInt;
    private String lowerCaseText;

    // private static final int ID = 0;
    // private static final int TYPE = 1;
    // private static final int SEVERITY = 2;
    // private static final int FROM_LIBRARY = 3;
    // private static final int FROM_PROGRAM = 4;
    // private static final int FROM_STATEMENT = 5;
    // private static final int TO_LIBRARY = 6;
    // private static final int TO_PROGRAM = 7;
    // private static final int TO_STATEMENT = 8;
    // private static final int TEXT = 9;
    //
    // public static final String FIELD_NAME_ID = "ID";
    // public static final String FIELD_NAME_TYPE = "TYPE";
    // public static final String FIELD_NAME_SEVERITY = "SEVERITY";
    // public static final String FIELD_NAME_FROM_LIBRARY = "FROM_LIBRARY";
    // public static final String FIELD_NAME_FROM_PROGRAM = "FROM_PROGRAM";
    // public static final String FIELD_NAME_FROM_STATEMENT = "FROM_STATEMENT";
    // public static final String FIELD_NAME_TO_LIBRARY = "TO_LIBRARY";
    // public static final String FIELD_NAME_TO_PROGRAM = "TO_PROGRAM";
    // public static final String FIELD_NAME_TO_STATEMENT = "TO_STATEMENT";
    // public static final String FIELD_NAME_TEXT = "TEXT";

    public static enum Fields {
        ID ("ID", 0),
        TYPE ("TYPE", 1),
        SEVERITY ("SEVERITY", 2),
        FROM_LIBRARY ("FROM_LIBRARY", 3),
        FROM_PROGRAM ("FROM_PROGRAM", 4),
        FROM_STATEMENT ("FROM_STATEMENT", 5),
        TO_LIBRARY ("TO_LIBRARY", 6),
        TO_PROGRAM ("TO_PROGRAM", 7),
        TO_STATEMENT ("TO_STATEMENT", 8),
        TEXT ("TEXT", 9);

        private String fieldName;
        private int fieldIndex;

        private Fields(String fieldName, int fieldIndex) {
            this.fieldName = fieldName;
            this.fieldIndex = fieldIndex;
        }

        public String fieldName() {
            return fieldName;
        }

        public int fieldIndex() {
            return fieldIndex;
        }
    }

    private static HashMap<String, Integer> columnMappings;
    static {
        columnMappings = new HashMap<String, Integer>();
        columnMappings.put(Fields.ID.fieldName(), Fields.ID.fieldIndex());
        columnMappings.put(Fields.TYPE.fieldName(), Fields.TYPE.fieldIndex());
        columnMappings.put(Fields.SEVERITY.fieldName(), Fields.SEVERITY.fieldIndex());
        columnMappings.put(Fields.FROM_LIBRARY.fieldName(), Fields.FROM_LIBRARY.fieldIndex());
        columnMappings.put(Fields.FROM_PROGRAM.fieldName(), Fields.FROM_PROGRAM.fieldIndex());
        columnMappings.put(Fields.FROM_STATEMENT.fieldName(), Fields.FROM_STATEMENT.fieldIndex());
        columnMappings.put(Fields.TO_LIBRARY.fieldName(), Fields.TO_LIBRARY.fieldIndex());
        columnMappings.put(Fields.TO_PROGRAM.fieldName(), Fields.TO_PROGRAM.fieldIndex());
        columnMappings.put(Fields.TO_STATEMENT.fieldName(), Fields.TO_STATEMENT.fieldIndex());
        columnMappings.put(Fields.TEXT.fieldName(), Fields.TEXT.fieldIndex());
    }

    public JobLogMessage(int pageNumber) {
        this.selected = false;
        this.pageNumber = pageNumber;
        this.listeners = new ArrayList<MessageModifyListener>();

        setSeverity(null);
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean select) {
        this.selected = select;
        if (this.selected) {
            notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.SELECTED, "1"));////$NON-NLS-1$
        } else {
            notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.SELECTED, "0"));//$NON-NLS-1$
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.ID, this.id));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.TYPE, this.type));
    }

    public String getSeverity() {
        return severity;
    }

    public int getSeverityInt() {
        return severityInt;
    }

    public void setSeverity(String severity) {
        // if (severity == null) {
        //            this.severity = "";//$NON-NLS-1$
        // } else {
        this.severity = severity;
        // }
        setSeverityIntValue(this.severity);
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.SEVERITY, this.severity));
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public String getLowerCaseText() {
        return lowerCaseText;
    }

    public void setText(String text) {
        this.text = text;
        this.lowerCaseText = this.text.toLowerCase();
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getToModule() {
        return toModule;
    }

    public void setToModule(String toModule) {
        this.toModule = toModule;
    }

    public String getToLibrary() {
        return toLibrary;
    }

    public void setToLibrary(String toLibrary) {
        this.toLibrary = toLibrary;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.TO_LIBRARY, this.toLibrary));
    }

    public String getToProgram() {
        return toProgram;
    }

    public void setToProgram(String toProgram) {
        this.toProgram = toProgram;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.TO_PROGRAM, this.toProgram));
    }

    public String getToProcedure() {
        return toProcedure;
    }

    public void setToProcedure(String toProcedure) {
        this.toProcedure = toProcedure;
    }

    public String getToStatement() {
        return toStatement;
    }

    public void setToStatement(String toStatement) {
        this.toStatement = toStatement;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.TO_STMT, this.toStatement));
    }

    public String getFromModule() {
        return fromModule;
    }

    public void setFromModule(String fromModule) {
        this.fromModule = fromModule;
    }

    public String getFromLibrary() {
        return fromLibrary;
    }

    public void setFromLibrary(String fromLibrary) {
        this.fromLibrary = fromLibrary;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.FROM_LIBRARY, this.fromLibrary));
    }

    public String getFromProgram() {
        return fromProgram;
    }

    public void setFromProgram(String fromProgram) {
        this.fromProgram = fromProgram;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.FROM_PROGRAM, this.fromProgram));
    }

    public String getFromProcedure() {
        return fromProcedure;
    }

    public void setFromProcedure(String fromProcedure) {
        this.fromProcedure = fromProcedure;
    }

    public String getFromStatement() {
        return fromStatement;
    }

    public void setFromStatement(String fromStatement) {
        this.fromStatement = fromStatement;
        notifyModifyListeners(new MessageModifyEvent(MessageModifyEvent.FROM_STMT, this.fromStatement));
    }

    private void setSeverityIntValue(String severity) {
        if (severity == null || severity.length() == 0) {
            severityInt = SEVERITY_BLANK;
        } else {
            severityInt = IntHelper.tryParseInt(severity, SEVERITY_BLANK);
        }
    }

    public void addModifyChangedListener(MessageModifyListener listener) {

        listeners.add(listener);
    }

    public void removeModifyListener(MessageModifyListener listener) {

        listeners.remove(listener);
    }

    private void notifyModifyListeners(MessageModifyEvent event) {

        for (MessageModifyListener listener : listeners) {
            listener.modifyText(event);
        }

    }

    public static HashMap<String, Integer> getColumnMapping() {
        return columnMappings;
    }

    public Comparable[] getRow() {

        Comparable[] row = new Comparable[columnMappings.size()];

        row[Fields.ID.fieldIndex()] = getId();
        row[Fields.TYPE.fieldIndex()] = getType();
        row[Fields.SEVERITY.fieldIndex()] = getSeverityInt();
        row[Fields.FROM_LIBRARY.fieldIndex()] = getFromLibrary();
        row[Fields.FROM_PROGRAM.fieldIndex()] = getFromProgram();
        row[Fields.FROM_STATEMENT.fieldIndex()] = getFromStatement();
        row[Fields.TO_LIBRARY.fieldIndex()] = getToLibrary();
        row[Fields.TO_PROGRAM.fieldIndex()] = getToProgram();
        row[Fields.TO_STATEMENT.fieldIndex()] = getToStatement();
        row[Fields.TEXT.fieldIndex()] = getText();

        // System.out.println("Date: " + row[TO_STATEMENT] + ", Time: " +
        // row[TEXT]
        // + ", Timestamp: " + row[JOTSTP]);

        return row;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append(getId());
        buffer.append(" ("); //$NON-NLS-1$
        buffer.append(getType());
        buffer.append(") "); //$NON-NLS-1$
        buffer.append(getText());

        return buffer.toString();
    }
}
