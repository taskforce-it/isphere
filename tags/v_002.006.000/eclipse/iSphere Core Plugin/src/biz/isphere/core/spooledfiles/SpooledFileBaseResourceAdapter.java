/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
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

    public ImageDescriptor getImageDescriptor(SpooledFile splf) {
        return ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_SPOOLED_FILE);
    }

    public boolean handleDoubleClick(SpooledFile splf) {

        String defaultFormat = Preferences.getInstance().getSpooledFileConversionDefaultFormat();

        String message = splf.open(defaultFormat);
        if (message == null) {
            return true;
        } else {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Error, message);
            return false;
        }

    }

    public String getText(SpooledFile splf) {
        return splf.getFile() + " - " + splf.getStatus();
    }

    public String getAbsoluteName(SpooledFile splf) {
        return splf.getAbsoluteName();
    }

    public String getType(SpooledFile splf) {
        return Messages.Spooled_file_resource;
    }

    public IPropertyDescriptor[] internalGetPropertyDescriptors() {

        PropertyDescriptor[] ourPDs = new PropertyDescriptor[17];

        ourPDs[0] = new PropertyDescriptor("File", Messages.File);
        ourPDs[0].setDescription(Messages.File);

        ourPDs[1] = new PropertyDescriptor("File_number", Messages.File_number);
        ourPDs[1].setDescription(Messages.File_number);

        ourPDs[2] = new PropertyDescriptor("Job_name", Messages.Job_name);
        ourPDs[2].setDescription(Messages.Job_name);

        ourPDs[3] = new PropertyDescriptor("Job_user", Messages.Job_user);
        ourPDs[3].setDescription(Messages.Job_user);

        ourPDs[4] = new PropertyDescriptor("Job_number", Messages.Job_number);
        ourPDs[4].setDescription(Messages.Job_number);

        ourPDs[5] = new PropertyDescriptor("Job_system", Messages.Job_system);
        ourPDs[5].setDescription(Messages.Job_system);

        ourPDs[6] = new PropertyDescriptor("Creation_date", Messages.Creation_date);
        ourPDs[6].setDescription(Messages.Creation_date);

        ourPDs[7] = new PropertyDescriptor("Creation_time", Messages.Creation_time);
        ourPDs[7].setDescription(Messages.Creation_time);

        ourPDs[8] = new PropertyDescriptor("Status", Messages.Status);
        ourPDs[8].setDescription(Messages.Status);

        ourPDs[9] = new PropertyDescriptor("Output_queue", Messages.Output_queue);
        ourPDs[9].setDescription(Messages.Output_queue);

        ourPDs[10] = new PropertyDescriptor("Output_priority", Messages.Output_priority);
        ourPDs[10].setDescription(Messages.Output_priority);

        ourPDs[11] = new PropertyDescriptor("User_data", Messages.User_data);
        ourPDs[11].setDescription(Messages.User_data);

        ourPDs[12] = new PropertyDescriptor("Form_type", Messages.Form_type);
        ourPDs[12].setDescription(Messages.Form_type);

        ourPDs[13] = new PropertyDescriptor("Copies", Messages.Copies);
        ourPDs[13].setDescription(Messages.Copies);

        ourPDs[14] = new PropertyDescriptor("pages", Messages.Pages);
        ourPDs[14].setDescription(Messages.Pages);

        ourPDs[15] = new PropertyDescriptor("Current_page", Messages.Current_page);
        ourPDs[15].setDescription(Messages.Current_page);

        ourPDs[16] = new PropertyDescriptor("Creation_timestamp", Messages.Creation_timestamp);
        ourPDs[16].setDescription("Creation_timestamp");

        return ourPDs;

    }

    public Object internalGetPropertyValue(SpooledFile splf, Object propKey) {
        if ("File".equals(propKey)) return splf.getFile();
        if ("File_number".equals(propKey)) return new Integer(splf.getFileNumber()); // Integer.toString(splf.getFileNumber());
        if ("Job_name".equals(propKey)) return splf.getJobName();
        if ("Job_user".equals(propKey)) return splf.getJobUser();
        if ("Job_number".equals(propKey)) return new Integer(splf.getJobNumber()); // splf.getJobNumber();
        if ("Job_system".equals(propKey)) return splf.getJobSystem();
        if ("Creation_date".equals(propKey)) return splf.getCreationDateFormated();
        if ("Creation_time".equals(propKey)) return splf.getCreationTimeFormated();
        if ("Status".equals(propKey)) return splf.getStatus();
        if ("Output_queue".equals(propKey)) return splf.getOutputQueueFormated();
        if ("Output_priority".equals(propKey)) return splf.getOutputPriority();
        if ("User_data".equals(propKey)) return splf.getUserData();
        if ("Form_type".equals(propKey)) return splf.getFormType();
        if ("Copies".equals(propKey)) return new Integer(splf.getCopies());
        if ("pages".equals(propKey)) return new Integer(splf.getPages());
        if ("Current_page".equals(propKey)) return new Integer(splf.getCurrentPage());
        if ("Creation_timestamp".equals(propKey)) return splf.getCreationTimestampFormatted();
        return null;
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
