/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.model;

import java.util.LinkedList;
import java.util.List;

public class JobLogPage {

    private int pageNumber;
    private List<JobLogMessage> jobLogMessages;

    public JobLogPage() {
        this.pageNumber = -1;
        this.jobLogMessages = new LinkedList<JobLogMessage>();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public JobLogMessage addMessage() {

        JobLogMessage message = new JobLogMessage();
        jobLogMessages.add(message);

        return message;
    }

    public JobLogMessage[] getMessages() {
        return jobLogMessages.toArray(new JobLogMessage[jobLogMessages.size()]);
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("#");
        buffer.append(getPageNumber());
        buffer.append(" (#msg: ");
        buffer.append(jobLogMessages.size());
        buffer.append(")");

        return buffer.toString();
    }
}
