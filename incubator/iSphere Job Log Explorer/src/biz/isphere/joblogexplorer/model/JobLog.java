/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model;

import java.util.LinkedList;
import java.util.List;

public class JobLog {

    private String jobName;
    private String userName;
    private String jobNumber;

    private String jobDescriptionName;
    private String jobDescriptionLibraryName;

    private boolean isHeaderComplete;
    private List<JobLogPage> jobLogPages;
    private List<JobLogMessage> jobLogMessages;
    private JobLogPage currentPage;

    public JobLog() {
        this.isHeaderComplete = validateJobLogHeader();
        this.jobLogPages = new LinkedList<JobLogPage>();
        this.jobLogMessages = new LinkedList<JobLogMessage>();
        this.currentPage = null;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
        validateJobLogHeader();
    }

    public String getJobUserName() {
        return userName;
    }

    public void setJobUserName(String userName) {
        this.userName = userName;
        validateJobLogHeader();
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
        validateJobLogHeader();
    }

    public String getJobDescriptionName() {
        return jobDescriptionName;
    }

    public void setJobDescriptionName(String jobDescriptionName) {
        this.jobDescriptionName = jobDescriptionName;
        validateJobLogHeader();
    }

    public String getJobDescriptionLibraryName() {
        return jobDescriptionLibraryName;
    }

    public void setJobDescriptionLibraryName(String jobDescriptionLibraryName) {
        this.jobDescriptionLibraryName = jobDescriptionLibraryName;
        validateJobLogHeader();
    }

    public boolean isHeaderComplete() {
        return isHeaderComplete;
    }

    public String getQualifiedJobName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getJobNumber());
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(getJobUserName());
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(getJobName());

        return buffer.toString();
    }

    public String getQualifiedJobDescriptionName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getJobDescriptionLibraryName());
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(getJobDescriptionName());

        return buffer.toString();
    }

    public JobLogPage addPage() {

        currentPage = new JobLogPage();
        jobLogPages.add(currentPage);

        return currentPage;
    }

    public JobLogMessage addMessage() {

        JobLogMessage message = new JobLogMessage(currentPage.getPageNumber());
        jobLogMessages.add(message);

        if (currentPage.getFirstMessage() == null) {
            currentPage.setFirstMessage(message);
        }
        currentPage.setLastMessage(message);

        return message;
    }

    public JobLogPage[] getPages() {
        return jobLogPages.toArray(new JobLogPage[jobLogPages.size()]);
    }

    public List<JobLogMessage> getMessages() {

        return jobLogMessages;
    }

    public void dump() {

        System.out.println("Job log . . . . : " + getQualifiedJobName()); //$NON-NLS-1$
        System.out.println("Job description : " + getQualifiedJobDescriptionName()); //$NON-NLS-1$

        for (JobLogMessage message : jobLogMessages) {

            System.out.println("  " + message.toString()); //$NON-NLS-1$

            printMessageAttribute("  Page#: ", "" + message.getPageNumber()); //$NON-NLS-1$ //$NON-NLS-2$
            printMessageAttribute("    Cause: ", message.getCause()); //$NON-NLS-1$
            printMessageAttribute("       to: ", message.getToModule()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getToProcedure()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getToStatement()); //$NON-NLS-1$
            printMessageAttribute("     from: ", message.getFromModule()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getFromProcedure()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getFromStatement()); //$NON-NLS-1$
        }
    }

    private void printMessageAttribute(String label, String value) {

        if (value == null) {
            return;
        }

        System.out.println(label + value);
    }

    private boolean validateJobLogHeader() {

        if (getJobName() != null && getJobUserName() != null && getJobNumber() != null && getJobDescriptionLibraryName() != null
            && getJobDescriptionName() != null) {
            isHeaderComplete = true;
        } else {
            isHeaderComplete = false;
        }

        return isHeaderComplete;
    }

    @Override
    public String toString() {

        return getQualifiedJobName();
    }
}
