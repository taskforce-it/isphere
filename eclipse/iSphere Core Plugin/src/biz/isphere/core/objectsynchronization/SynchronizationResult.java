/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.util.LinkedList;
import java.util.List;

public class SynchronizationResult {

    /**
     * Job finished successfully.
     */
    public static final String OK = "OK";

    /**
     * Job ended with errors.
     */
    public static final String ERROR = "ERROR";

    /**
     * Job has been canceled.
     */
    public static final String CANCELED = "CANCELED";

    private String status;
    private int countCopied;
    private String jobFinishedMessage;
    private List<String> errorMessages;

    public SynchronizationResult() {
        this.errorMessages = new LinkedList<String>();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCountCopied(int countCopied) {
        this.countCopied = countCopied;
    }

    public void setJobFinishedMessage(String jobFinishedMessage) {
        this.jobFinishedMessage = jobFinishedMessage;
    }

    public void addMessage(String message) {
        errorMessages.add(message);
    }

    public String[] getMesssages() {
        return errorMessages.toArray(new String[errorMessages.size()]);
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        for (String errorMessages : errorMessages) {
            buffer.append(errorMessages);
            buffer.append("\n");
        }

        return buffer.toString();
    }
}
