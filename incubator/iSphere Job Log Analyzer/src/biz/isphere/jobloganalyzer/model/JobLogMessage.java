/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.model;

public class JobLogMessage {

    private String id;
    private String type;
    private int severity;
    private String date;
    private String time;
    private String text;
    private String cause;

    private String toModule;
    private String toProcedure;
    private String toStatement;

    private String fromModule;
    private String fromProcedure;
    private String fromStatement;

    private String lastAttribute;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        lastAttribute = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        lastAttribute = null;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
        lastAttribute = null;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        lastAttribute = null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        lastAttribute = null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        lastAttribute = this.text;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
        lastAttribute = this.cause;
    }

    public String getToModule() {
        return toModule;
    }

    public void setToModule(String toModule) {
        lastAttribute = null;
        this.toModule = toModule;
    }

    public String getToProcedure() {
        return toProcedure;
    }

    public void setToProcedure(String toProcedure) {
        this.toProcedure = toProcedure;
        lastAttribute = null;
    }

    public String getToStatement() {
        return toStatement;
    }

    public void setToStatement(String toStatement) {
        this.toStatement = toStatement;
        lastAttribute = null;
    }

    public String getFromModule() {
        return fromModule;
    }

    public void setFromModule(String fromModule) {
        this.fromModule = fromModule;
        lastAttribute = null;
    }

    public String getFromProcedure() {
        return fromProcedure;
    }

    public void setFromProcedure(String fromProcedure) {
        this.fromProcedure = fromProcedure;
        lastAttribute = null;
    }

    public String getFromStatement() {
        return fromStatement;
    }

    public void setFromStatement(String fromStatement) {
        this.fromStatement = fromStatement;
        lastAttribute = null;
    }

//    public void appendAttributeValue(String string) {
//
//        if (lastAttribute == null) {
//            return;
//        }
//
//        lastAttribute = lastAttribute + " " + string;
//    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append(getId());
        buffer.append(" (");
        buffer.append(getType());
        buffer.append(") ");
        buffer.append(getText());

        return buffer.toString();
    }
}
