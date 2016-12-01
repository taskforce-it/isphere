/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model;

public class JobLogPage {

    private int pageNumber;
    private JobLogMessage firstMessage;
    private JobLogMessage lastMessage;

    public JobLogPage() {
        this.pageNumber = -1;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public JobLogMessage getFirstMessage() {
        return this.firstMessage;
    }

    public void setFirstMessage(JobLogMessage firstMessage) {

        if (this.firstMessage != null) {
            throw new RuntimeException("First message already set."); //$NON-NLS-1$
        }

        this.firstMessage = firstMessage;
    }

    public JobLogMessage getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(JobLogMessage lastMessage) {

        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append("#"); //$NON-NLS-1$
        buffer.append(getPageNumber());
        buffer.append(" (from: "); //$NON-NLS-1$
        buffer.append(firstMessage.getId());
        buffer.append(" to: "); //$NON-NLS-1$
        buffer.append(lastMessage.getId());
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }
}
