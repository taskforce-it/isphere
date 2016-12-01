/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

public class StatusLineData {

    private int numberOfMessages;
    private int numberOfMessagesSelected;
    private String message;

    public StatusLineData() {
        this(0);
    }

    public StatusLineData(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
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

    public void setMessage(String message) {
        this.message = message;
    }
}
