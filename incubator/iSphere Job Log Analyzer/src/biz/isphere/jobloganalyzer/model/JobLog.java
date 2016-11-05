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

public class JobLog {

    private String jobName;
    private String userName;
    private String jobNumber;

    private String jobDescriptionName;
    private String jobDescriptionLibraryName;

    private boolean isHeaderComplete;
    private List<JobLogPage> jobLogPages;

    public JobLog() {
        this.isHeaderComplete = validateJobLogHeader();
        this.jobLogPages = new LinkedList<JobLogPage>();
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
        buffer.append(getJobName()); //$NON-NLS-1$

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

        JobLogPage page = new JobLogPage();
        jobLogPages.add(page);

        return page;
    }

    public JobLogPage[] getPages() {
        return jobLogPages.toArray(new JobLogPage[jobLogPages.size()]);
    }

    public void dump() {

        System.out.println("Job log . . . . : " + getQualifiedJobName());
        System.out.println("Job description : " + getQualifiedJobDescriptionName());

        for (JobLogPage page : getPages()) {
            System.out.println("  Page#: " + page.getPageNumber());
            for (JobLogMessage message : page.getMessages()) {

                System.out.println("    " + message.toString());

                printMessageAttribute("      Cause: ", message.getCause());
                printMessageAttribute("         to: ", message.getToModule());
                printMessageAttribute("           : ", message.getToProcedure());
                printMessageAttribute("           : ", message.getToStatement());
                printMessageAttribute("       from: ", message.getFromModule());
                printMessageAttribute("           : ", message.getFromProcedure());
                printMessageAttribute("           : ", message.getFromStatement());
            }
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
