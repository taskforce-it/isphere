/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.joblogexplorer.Messages;

public class JobLogPropertySource implements IPropertySource {

    private static final String PROPERTY_SYSTEM_NAME = "biz.isphere.joblogexplorer.model.JobLog.systemName";//$NON-NLS-1$
    private static final String PROPERTY_JOB_NAME = "biz.isphere.joblogexplorer.model.JobLog.jobName";//$NON-NLS-1$
    private static final String PROPERTY_JOB_USER = "biz.isphere.joblogexplorer.model.JobLog.jobUser";//$NON-NLS-1$
    private static final String PROPERTY_JOB_NUMBER = "biz.isphere.joblogexplorer.model.JobLog.jobNumber";//$NON-NLS-1$
    private static final String PROPERTY_JOB_DESCRIPTION = "biz.isphere.joblogexplorer.model.JobLog.jobDescription";//$NON-NLS-1$
    private static final String PROPERTY_PAGES = "biz.isphere.joblogexplorer.model.JobLog.numPages";//$NON-NLS-1$
    private static final String PROPERTY_START_DATE = "biz.isphere.joblogexplorer.model.JobLog.startDate";//$NON-NLS-1$
    private static final String PROPERTY_END_DATE = "biz.isphere.joblogexplorer.model.JobLog.endDate";//$NON-NLS-1$
    private static final String PROPERTY_NUMBER_OF_MESSAGES = "biz.isphere.joblogexplorer.model.JobLog.numberOfMessages";//$NON-NLS-1$

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

            PropertyDescriptor systemName = createPropertyDescriptor(PROPERTY_SYSTEM_NAME, Messages.Property_system_name,
                Messages.Property_Category_system);

            // "Job" descriptors
            PropertyDescriptor jobNameDescriptor = createPropertyDescriptor(PROPERTY_JOB_NAME, Messages.Property_job_name,
                Messages.Property_Category_job);

            PropertyDescriptor jobUserDescriptor = createPropertyDescriptor(PROPERTY_JOB_USER, Messages.Property_job_user,
                Messages.Property_Category_job);

            PropertyDescriptor jobNumberDescriptor = createPropertyDescriptor(PROPERTY_JOB_NUMBER, Messages.Property_job_number,
                Messages.Property_Category_job);

            PropertyDescriptor jobDescriptionDescriptor = createPropertyDescriptor(PROPERTY_JOB_DESCRIPTION, Messages.Property_job_description,
                Messages.Property_Category_job);

            // "Statistics" descriptors
            PropertyDescriptor pagesDescriptor = createPropertyDescriptor(PROPERTY_PAGES, Messages.Property_pages,
                Messages.Property_Category_statistics);

            PropertyDescriptor startDateDescriptor = createPropertyDescriptor(PROPERTY_START_DATE, Messages.Property_start_date,
                Messages.Property_Category_statistics);

            PropertyDescriptor endDateDescriptor = createPropertyDescriptor(PROPERTY_END_DATE, Messages.Property_last_date,
                Messages.Property_Category_statistics);

            PropertyDescriptor numberOfMessagesDescriptor = createPropertyDescriptor(PROPERTY_NUMBER_OF_MESSAGES,
                Messages.Property_number_of_messages, Messages.Property_Category_statistics);

            // Read-only (instance of PropertyDescriptor)
            propertyDescriptors = new IPropertyDescriptor[] { systemName, jobNameDescriptor, jobUserDescriptor, jobNumberDescriptor,
                jobDescriptionDescriptor, pagesDescriptor, startDateDescriptor, endDateDescriptor, numberOfMessagesDescriptor };
        }

        return propertyDescriptors;
    }

    private PropertyDescriptor createPropertyDescriptor(Object id, String displayName, String category) {

        PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
        descriptor.setCategory(category);

        return descriptor;
    }

    public Object getPropertyValue(Object name) {
        if (name.equals(PROPERTY_SYSTEM_NAME)) {
            return jobLog.getSystemName();
        } else if (name.equals(PROPERTY_JOB_NAME)) {
            return jobLog.getJobName();
        } else if (name.equals(PROPERTY_JOB_USER)) {
            return jobLog.getJobUserName();
        } else if (name.equals(PROPERTY_JOB_NUMBER)) {
            return jobLog.getJobNumber();
        } else if (name.equals(PROPERTY_JOB_DESCRIPTION)) {
            return jobLog.getJobDescriptionLibraryName() + "/" + jobLog.getJobDescriptionName(); //$NON-NLS-1$
        } else if (name.equals(PROPERTY_PAGES)) {
            return jobLog.getPages().length;
        } else if (name.equals(PROPERTY_START_DATE)) {
            if (jobLog.getFirstPage() == null) {
                return "";//$NON-NLS-1$
            } else {
                return getDateTimeValue(jobLog.getFirstPage().getFirstMessage());
            }
        } else if (name.equals(PROPERTY_END_DATE)) {
            if (jobLog.getLastPage() == null) {
                return "";//$NON-NLS-1$
            } else {
                return getDateTimeValue(jobLog.getLastPage().getLastMessage());
            }
        } else if (name.equals(PROPERTY_NUMBER_OF_MESSAGES)) {
            return jobLog.getMessages().size();
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
