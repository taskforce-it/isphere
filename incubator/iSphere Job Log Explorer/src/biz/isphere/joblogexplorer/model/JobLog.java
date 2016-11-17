/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyEvent;
import biz.isphere.joblogexplorer.model.listeners.MessageModifyListener;

public class JobLog implements MessageModifyListener, IAdaptable {

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
        this.numMessagesSelected = 0;
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

    public String[] getMessageIds() {
        return messageIds.toArray(new String[messageIds.size()]);
    }

    public String[] getMessageTypes() {
        return messageTypes.toArray(new String[messageTypes.size()]);
    }

    public String[] getMessageSeverities() {
        return messageSeverities.toArray(new String[messageSeverities.size()]);
    }

    public JobLogPage addPage() {

        currentPage = new JobLogPage();
        jobLogPages.add(currentPage);

        return currentPage;
    }

    public JobLogMessage addMessage() {

        JobLogMessage message = new JobLogMessage(currentPage.getPageNumber());
        message.addModifyChangedListener(this);

        jobLogMessages.add(message);

        if (currentPage.getFirstMessage() == null) {
            currentPage.setFirstMessage(message);
        }
        currentPage.setLastMessage(message);

        // addNotNullOrEmptyFilterItem(messageTypes, message.getType());

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

    public int getNumMessagesSelected() {
        return numMessagesSelected;
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

    public class JobLogPropertySource implements IPropertySource {

        private static final String PROPERTY_JOB_NAME = "biz.isphere.joblogexplorer.model.JobLog.jobName";//$NON-NLS-1$
        private static final String PROPERTY_JOB_USER = "biz.isphere.joblogexplorer.model.JobLog.jobUser";//$NON-NLS-1$
        private static final String PROPERTY_JOB_NUMBER = "biz.isphere.joblogexplorer.model.JobLog.jobNUmber";//$NON-NLS-1$
        private static final String PROPERTY_PAGES = "biz.isphere.joblogexplorer.model.JobLog.numPages";//$NON-NLS-1$
        private static final String PROPERTY_START_DATE = "biz.isphere.joblogexplorer.model.JobLog.startDate";//$NON-NLS-1$
        private static final String PROPERTY_END_DATE = "biz.isphere.joblogexplorer.model.JobLog.endDate";//$NON-NLS-1$

        private JobLog jobLog;
        private IPropertyDescriptor[] propertyDescriptors;

        public JobLogPropertySource(JobLog jobLog) {
            this.jobLog = jobLog;
        }

        public Object getEditableValue() {
            // TODO Auto-generated method stub
            return null;
        }

        public IPropertyDescriptor[] getPropertyDescriptors() {

            if (propertyDescriptors == null) {

                // "Job" descriptors
                PropertyDescriptor jobNameDescriptor = createPropertyDescriptor(PROPERTY_JOB_NAME, Messages.Property_job_name,
                    Messages.Property_Category_job);

                PropertyDescriptor jobUserDescriptor = createPropertyDescriptor(PROPERTY_JOB_USER, Messages.Property_job_user,
                    Messages.Property_Category_job);

                PropertyDescriptor jobNumberDescriptor = createPropertyDescriptor(PROPERTY_JOB_NUMBER, Messages.Property_job_number,
                    Messages.Property_Category_job);

                // "Statistics" descriptors
                PropertyDescriptor pagesDescriptor = createPropertyDescriptor(PROPERTY_PAGES, Messages.Property_pages,
                    Messages.Property_Category_statistics);

                PropertyDescriptor startDateDescriptor = createPropertyDescriptor(PROPERTY_START_DATE, Messages.Property_start_date,
                    Messages.Property_Category_statistics);

                PropertyDescriptor endDateDescriptor = createPropertyDescriptor(PROPERTY_END_DATE, Messages.Property_last_date,
                    Messages.Property_Category_statistics);

                // Read-only (instance of PropertyDescriptor)
                propertyDescriptors = new IPropertyDescriptor[] { jobNameDescriptor, jobUserDescriptor, jobNumberDescriptor, pagesDescriptor,
                    startDateDescriptor, endDateDescriptor };
            }

            return propertyDescriptors;
        }

        private PropertyDescriptor createPropertyDescriptor(Object id, String displayName, String category) {

            PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
            descriptor.setCategory(category);

            return descriptor;
        }

        public Object getPropertyValue(Object name) {
            if (name.equals(PROPERTY_JOB_NAME)) {
                return getJobName();
            } else if (name.equals(PROPERTY_JOB_USER)) {
                return getJobUserName();
            } else if (name.equals(PROPERTY_JOB_NUMBER)) {
                return getJobNumber();
            } else if (name.equals(PROPERTY_PAGES)) {
                return getPages().length;
            } else if (name.equals(PROPERTY_START_DATE)) {
                if (getFirstPage() == null) {
                    return "";//$NON-NLS-1$
                } else {
                    return getDateTimeValue(getFirstPage().getFirstMessage());
                }
            } else if (name.equals(PROPERTY_END_DATE)) {
                if (getLastPage() == null) {
                    return "";//$NON-NLS-1$
                } else {
                    return getDateTimeValue(getLastPage().getLastMessage());
                }
            } else {
                return null;
            }
        }

        private Object getDateTimeValue(JobLogMessage message) {

            if (message == null) {
                return "";//$NON-NLS-1$
            }

            String date = message.getDate();
            String time = message.getTime();

            if (date != null && time != null) {
                return date + " / " + time;//$NON-NLS-1$
            } else if (date != null) {
                return date + " / ?";//$NON-NLS-1$
            } else {
                return "? / " + time;//$NON-NLS-1$
            }
        }

        public boolean isPropertySet(Object arg0) {
            return false;
        }

        public void resetPropertyValue(Object arg0) {
        }

        public void setPropertyValue(Object arg0, Object arg1) {
        }

    }
}
