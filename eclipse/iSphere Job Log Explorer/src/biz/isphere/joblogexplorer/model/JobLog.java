/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyEvent;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyListener;

public class JobLog implements MessageModifyListener, IAdaptable {

    private String systemName;
    private Map<String, JobLogMessage> errors;

    private String jobName;
    private String userName;
    private String jobNumber;

    private String jobDescriptionName;
    private String jobDescriptionLibraryName;

    private boolean isHeaderComplete;
    private List<JobLogPage> jobLogPages;
    private List<JobLogMessage> jobLogMessages;
    private JobLogPage currentPage;

    private Set<String> messageIds;
    private Set<String> messageTypes;
    private Set<String> messageSeverities;
    private Set<String> messageFromLibraries;
    private Set<String> messageFromPrograms;
    private Set<String> messageFromStmts;
    private Set<String> messageToLibraries;
    private Set<String> messageToPrograms;
    private Set<String> messageToStmts;
    private int numMessagesSelected;

    private JobLogPropertySource propertySource;

    public JobLog() {
        this.isHeaderComplete = validateJobLogHeader();
        this.jobLogPages = new LinkedList<JobLogPage>();
        this.jobLogMessages = new LinkedList<JobLogMessage>();
        this.currentPage = null;

        this.messageIds = new HashSet<String>();
        this.messageTypes = new HashSet<String>();
        this.messageSeverities = new HashSet<String>();
        this.messageFromLibraries = new HashSet<String>();
        this.messageFromPrograms = new HashSet<String>();
        this.messageFromStmts = new HashSet<String>();
        this.messageToLibraries = new HashSet<String>();
        this.messageToPrograms = new HashSet<String>();
        this.messageToStmts = new HashSet<String>();
        this.numMessagesSelected = 0;

        this.errors = new LinkedHashMap<String, JobLogMessage>();
    }

    public int getErrorCount() {
        return errors.size();
    }

    public String[] getErrors() {

        Collection<JobLogMessage> messages = errors.values();

        List<String> errors = new LinkedList<String>();

        for (JobLogMessage message : messages) {
            errors.add(message.getError());
        }

        return errors.toArray(new String[errors.size()]);
    }

    public void addError(Throwable e, JobLogMessage message) {
        errors.put(ExceptionHelper.getLocalizedMessage(e), message);
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = trim(systemName);
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = trim(jobName);
        validateJobLogHeader();
    }

    public String getJobUserName() {
        return userName;
    }

    public void setJobUserName(String userName) {
        this.userName = trim(userName);
        validateJobLogHeader();
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = trim(jobNumber);
        validateJobLogHeader();
    }

    public String getJobDescriptionName() {
        return jobDescriptionName;
    }

    public void setJobDescriptionName(String jobDescriptionName) {
        this.jobDescriptionName = trim(jobDescriptionName);
        validateJobLogHeader();
    }

    public String getJobDescriptionLibraryName() {
        return jobDescriptionLibraryName;
    }

    public void setJobDescriptionLibraryName(String jobDescriptionLibraryName) {
        this.jobDescriptionLibraryName = trim(jobDescriptionLibraryName);
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

    public String[] getMessageIds() {
        return messageIds.toArray(new String[messageIds.size()]);
    }

    public String[] getMessageTypes() {
        return messageTypes.toArray(new String[messageTypes.size()]);
    }

    public String[] getMessageSeverities() {
        return messageSeverities.toArray(new String[messageSeverities.size()]);
    }

    public String[] getMessageFromLibraries() {
        return messageFromLibraries.toArray(new String[messageFromLibraries.size()]);
    }

    public String[] getMessageFromPrograms() {
        return messageFromPrograms.toArray(new String[messageFromPrograms.size()]);
    }

    public String[] getMessageFromStatements() {
        return messageFromStmts.toArray(new String[messageFromStmts.size()]);
    }

    public String[] getMessageToLibraries() {
        return messageToLibraries.toArray(new String[messageToLibraries.size()]);
    }

    public String[] getMessageToPrograms() {
        return messageToPrograms.toArray(new String[messageToPrograms.size()]);
    }

    public String[] getMessageToStatements() {
        return messageToStmts.toArray(new String[messageToStmts.size()]);
    }

    public JobLogPage addPage() {

        currentPage = new JobLogPage();
        jobLogPages.add(currentPage);

        return currentPage;
    }

    public JobLogMessage addMessage() {

        JobLogMessage message;
        if (currentPage != null) {
            message = new JobLogMessage(currentPage.getPageNumber());
        } else {
            message = new JobLogMessage(0);
        }

        message.addModifyChangedListener(this);

        jobLogMessages.add(message);

        if (currentPage != null) {
            if (currentPage.getFirstMessage() == null) {
                currentPage.setFirstMessage(message);
            }
            currentPage.setLastMessage(message);
        }

        return message;
    }

    private void addNotNullOrEmptyFilterItem(Set<String> set, String value) {

        if (!StringHelper.isNullOrEmpty(value)) {
            set.add(value);
        }
    }

    public JobLogPage[] getPages() {
        return jobLogPages.toArray(new JobLogPage[jobLogPages.size()]);
    }

    public List<JobLogMessage> getMessages() {

        return jobLogMessages;
    }

    public JobLogPage getFirstPage() {
        if (jobLogPages.size() == 0) {
            return null;
        }
        return jobLogPages.get(0);
    }

    public JobLogPage getLastPage() {
        if (jobLogPages.size() == 0) {
            return null;
        }
        return jobLogPages.get(jobLogPages.size() - 1);
    }

    public boolean haveSelectedMessages() {

        if (numMessagesSelected > 0) {
            return true;
        }

        return false;
    }

    public void dump() {

        System.out.println("System  . . . . : " + getSystemName()); //$NON-NLS-1$
        System.out.println("Job log . . . . : " + getQualifiedJobName()); //$NON-NLS-1$
        System.out.println("Job description : " + getQualifiedJobDescriptionName()); //$NON-NLS-1$

        for (JobLogMessage message : jobLogMessages) {

            System.out.println("  " + message.toString()); //$NON-NLS-1$

            printMessageAttribute("  Page#: ", "" + message.getPageNumber()); //$NON-NLS-1$ //$NON-NLS-2$
            printMessageAttribute("    Cause: ", message.getHelp()); //$NON-NLS-1$
            printMessageAttribute("       to: ", message.getToModule()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getToProcedure()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getToStatement()); //$NON-NLS-1$
            printMessageAttribute("     from: ", message.getFromModule()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getFromProcedure()); //$NON-NLS-1$
            printMessageAttribute("         : ", message.getFromStatement()); //$NON-NLS-1$
        }

        System.out.println("Number of messages: " + jobLogMessages.size()); //$NON-NLS-1$
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

    public void modifyText(MessageModifyEvent event) {

        switch (event.type) {
        case MessageModifyEvent.ID:
            addNotNullOrEmptyFilterItem(messageIds, event.value);
            break;

        case MessageModifyEvent.TYPE:
            addNotNullOrEmptyFilterItem(messageTypes, event.value);
            break;

        case MessageModifyEvent.SEVERITY:
            addNotNullOrEmptyFilterItem(messageSeverities, event.value);
            break;

        case MessageModifyEvent.FROM_LIBRARY:
            addNotNullOrEmptyFilterItem(messageFromLibraries, event.value);
            break;

        case MessageModifyEvent.FROM_PROGRAM:
            addNotNullOrEmptyFilterItem(messageFromPrograms, event.value);
            break;

        case MessageModifyEvent.FROM_STMT:
            addNotNullOrEmptyFilterItem(messageFromStmts, event.value);
            break;

        case MessageModifyEvent.TO_LIBRARY:
            addNotNullOrEmptyFilterItem(messageToLibraries, event.value);
            break;

        case MessageModifyEvent.TO_PROGRAM:
            addNotNullOrEmptyFilterItem(messageToPrograms, event.value);
            break;

        case MessageModifyEvent.TO_STMT:
            addNotNullOrEmptyFilterItem(messageToStmts, event.value);
            break;

        case MessageModifyEvent.SELECTED:
            if ("1".equals(event.value)) {//$NON-NLS-1$
                numMessagesSelected++;
            } else {
                numMessagesSelected--;
            }
            break;

        default:
            break;
        }

    }

    private String trim(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }

    public Object getAdapter(Class adapter) {
        if (adapter == IPropertySource.class) {
            if (propertySource == null) {
                // cache the buttonelementpropertysource
                propertySource = new JobLogPropertySource(this);
            }
            return propertySource;
        }
        return null;
    }
}
