/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

public class JobLogExplorerStatusChangedEvent {

    public enum EventType {
        STARTED_DATA_LOADING,
        FINISHED_DATA_LOADING,
        FILTER_CHANGED,
        DATA_LOAD_ERROR
    }

    private EventType eventType;
    private Object source;
    private int numberOfMessages;
    private int numberOfMessagesSelected;
    private String message;
    private Throwable throwable;
    private boolean isReload;

    public JobLogExplorerStatusChangedEvent(EventType eventType, Object source) {
        this.eventType = eventType;
        this.source = source;
    }

    public JobLogExplorerStatusChangedEvent(JobLogExplorerStatusChangedEvent data, Object source) {
        this.eventType = data.eventType;
        this.source = source;
        this.numberOfMessages = data.numberOfMessages;
        this.numberOfMessagesSelected = data.numberOfMessagesSelected;
        this.isReload = false;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Object getSource() {
        return source;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public int getNumberOfMessagesSelected() {
        return numberOfMessagesSelected;
    }

    public void setNumberOfMessagesSelected(int numberOfMessagesSelected) {
        this.numberOfMessagesSelected = numberOfMessagesSelected;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setException(String message) {
        setException(message, null);
    }

    public void setException(String message, Throwable throwable) {
        this.message = message;
        this.throwable = throwable;
    }

    public boolean isReload() {
        return isReload;
    }

    public void setReload(boolean isReload) {
        this.isReload = isReload;
    }

    @Override
    public String toString() {
        return eventType.toString();
    }
}
