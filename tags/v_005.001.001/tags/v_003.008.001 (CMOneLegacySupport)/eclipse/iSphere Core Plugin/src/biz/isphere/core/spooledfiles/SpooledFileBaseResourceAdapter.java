/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;

public class SpooledFileBaseResourceAdapter {

    public static final String FILE = "File";
    public static final String FILE_NUMBER = "File_number";
    public static final String JOB_NAME = "Job_name";
    public static final String JOB_USER = "Job_user";
    public static final String JOB_NUMBER = "Job_number";
    public static final String JOB_SYSTEM = "Job_system";
    public static final String CREATION_DATE = "Creation_date";
    public static final String CREATION_TIME = "Creation_time";
    public static final String STATUS = "Status";
    public static final String OUTPUT_QUEUE = "Output_queue";
    public static final String OUTPUT_PRIORITY = "Output_priority";
    public static final String USER_DATA = "User_data";
    public static final String FORM_TYPE = "Form_type";
    public static final String COPIES = "Copies";
    public static final String PAGES = "Pages";
    public static final String CURRENT_PAGE = "Current_page";
    public static final String CREATION_TIMESTAMP = "Creation_timestamp";

    public ImageDescriptor getImageDescriptor(SpooledFile splf) {
        return ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SPOOLED_FILE);
    }

    public boolean handleDoubleClick(SpooledFile splf) {

        String defaultFormat = Preferences.getInstance().getSpooledFileConversionDefaultFormat();

        // splf.asyncOpen(defaultFormat, Display.getCurrent().getActiveShell());
        // return true;
        String message = splf.open(defaultFormat);
        if (message == null) {
            return true;
        } else {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Error, message);
            return false;
        }

    }

    public String getText(SpooledFile splf, SpooledFileTextDecoration decorationStyle) {
        return decorationStyle.createDecoration(splf);
    }

    public String getAbsoluteName(SpooledFile splf) {
        return splf.getAbsoluteName();
    }

    public String getType(SpooledFile splf) {
        return Messages.Spooled_file_resource;
    }

    public String getFile(SpooledFile splf) {
        return splf.getFile();
    }

    public IPropertyDescriptor[] internalGetPropertyDescriptors() {

        PropertyDescriptor[] ourPDs = new PropertyDescriptor[17];

        ourPDs[0] = new PropertyDescriptor(FILE, Messages.File);
        ourPDs[0].setDescription(Messages.File);

        ourPDs[1] = new PropertyDescriptor(FILE_NUMBER, Messages.File_number);
        ourPDs[1].setDescription(Messages.File_number);

        ourPDs[2] = new PropertyDescriptor(JOB_NAME, Messages.Job_name);
        ourPDs[2].setDescription(Messages.Job_name);

        ourPDs[3] = new PropertyDescriptor(JOB_USER, Messages.Job_user);
        ourPDs[3].setDescription(Messages.Job_user);

        ourPDs[4] = new PropertyDescriptor(JOB_NUMBER, Messages.Job_number);
        ourPDs[4].setDescription(Messages.Job_number);

        ourPDs[5] = new PropertyDescriptor(JOB_SYSTEM, Messages.Job_system);
        ourPDs[5].setDescription(Messages.Job_system);

        ourPDs[6] = new PropertyDescriptor(CREATION_DATE, Messages.Creation_date);
        ourPDs[6].setDescription(Messages.Creation_date);

        ourPDs[7] = new PropertyDescriptor(CREATION_TIME, Messages.Creation_time);
        ourPDs[7].setDescription(Messages.Creation_time);

        ourPDs[8] = new PropertyDescriptor(STATUS, Messages.Status);
        ourPDs[8].setDescription(Messages.Status);

        ourPDs[9] = new PropertyDescriptor(OUTPUT_QUEUE, Messages.Output_queue);
        ourPDs[9].setDescription(Messages.Output_queue);

        ourPDs[10] = new PropertyDescriptor(OUTPUT_PRIORITY, Messages.Output_priority);
        ourPDs[10].setDescription(Messages.Output_priority);

        ourPDs[11] = new PropertyDescriptor(USER_DATA, Messages.User_data);
        ourPDs[11].setDescription(Messages.User_data);

        ourPDs[12] = new PropertyDescriptor(FORM_TYPE, Messages.Form_type);
        ourPDs[12].setDescription(Messages.Form_type);

        ourPDs[13] = new PropertyDescriptor(COPIES, Messages.Copies);
        ourPDs[13].setDescription(Messages.Copies);

        ourPDs[14] = new PropertyDescriptor(PAGES, Messages.Pages);
        ourPDs[14].setDescription(Messages.Pages);

        ourPDs[15] = new PropertyDescriptor(CURRENT_PAGE, Messages.Current_page);
        ourPDs[15].setDescription(Messages.Current_page);

        ourPDs[16] = new PropertyDescriptor(CREATION_TIMESTAMP, Messages.Creation_timestamp);
        ourPDs[16].setDescription(Messages.Creation_timestamp);

        return ourPDs;

    }

    public Object internalGetPropertyValue(SpooledFile splf, Object propKey) {
        if (FILE.equals(propKey)) return splf.getFile();
        if (FILE_NUMBER.equals(propKey)) return new Integer(splf.getFileNumber()); // Integer.toString(splf.getFileNumber());
        if (JOB_NAME.equals(propKey)) return splf.getJobName();
        if (JOB_USER.equals(propKey)) return splf.getJobUser();
        if (JOB_NUMBER.equals(propKey)) return new Integer(splf.getJobNumber()); // splf.getJobNumber();
        if (JOB_SYSTEM.equals(propKey)) return splf.getJobSystem();
        if (CREATION_DATE.equals(propKey)) return getCreationDateProperty(splf);
        if (CREATION_TIME.equals(propKey)) return getCreationTimeProperty(splf);
        if (STATUS.equals(propKey)) return splf.getStatus();
        if (OUTPUT_QUEUE.equals(propKey)) return splf.getOutputQueueFormatted();
        if (OUTPUT_PRIORITY.equals(propKey)) return splf.getOutputPriority();
        if (USER_DATA.equals(propKey)) return splf.getUserData();
        if (FORM_TYPE.equals(propKey)) return splf.getFormType();
        if (COPIES.equals(propKey)) return new Integer(splf.getCopies());
        if (PAGES.equals(propKey)) return new Integer(splf.getPages());
        if (CURRENT_PAGE.equals(propKey)) return new Integer(splf.getCurrentPage());
        if (CREATION_TIMESTAMP.equals(propKey)) return getCreationTimestampProperty(splf);
        return null;
    }

    private Object getCreationTimeProperty(SpooledFile splf) {
        if (Preferences.getInstance().isFormatResourceDates()) {
            return splf.getCreationTimeFormatted();
        } else {
            return splf.getCreationTimeAsDate();
        }
    }

    private Object getCreationDateProperty(SpooledFile splf) {
        if (Preferences.getInstance().isFormatResourceDates()) {
            return splf.getCreationDateFormatted();
        } else {
            return splf.getCreationDateAsDate();
        }
    }

    private Object getCreationTimestampProperty(SpooledFile splf) {
        if (Preferences.getInstance().isFormatResourceDates()) {
            return splf.getCreationTimestampFormatted();
        } else {
            return splf.getCreationTimestamp();
        }
    }

    public String getAbsoluteParentName(SpooledFile splf) {
        return "root";
    }

    public String getSubSystemFactoryId() {
        return "biz.isphere.core.spooledfiles.subsystems.factory";
    }

    public String getRemoteTypeCategory(SpooledFile splf) {
        return "spooled files";
    }

    public String getRemoteType(SpooledFile splf) {
        return "spooled file";
    }

}
