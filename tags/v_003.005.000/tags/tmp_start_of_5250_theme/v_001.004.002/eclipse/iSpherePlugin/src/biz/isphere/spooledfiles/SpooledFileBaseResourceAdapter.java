/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.spooledfiles;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;


public class SpooledFileBaseResourceAdapter {

	public ImageDescriptor getImageDescriptor(SpooledFile splf) {
		return ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_SPOOLED_FILE);
	}
	
	public boolean handleDoubleClick(SpooledFile splf) {
		
		String defaultFormat = ISpherePlugin.getDefault().getPreferenceStore().getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT");
		
		String message = splf.open(defaultFormat);
		if (message == null) {
			return true;
		}
		else {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.getString("Error"), message);
			return false;
		}
		
	}
	
	public String getText(SpooledFile splf) {
		return splf.getFile() + " - " + splf.getStatus();
	}
	
	public String getAbsoluteName(SpooledFile splf) {
		return "Spooled_File/" + splf.getFile() + "/" + splf.getFileNumber() + "/" + splf.getJobName() + "/" + splf.getJobNumber() + "/" + splf.getJobUser() + "/" + splf.getJobSystem() + "/" + splf.getCreationDate() + "/" + splf.getCreationTime();
	}
	
	public String getType(SpooledFile splf) {
		return Messages.getString("Spooled_file_resource");
	}
	
	public IPropertyDescriptor[] internalGetPropertyDescriptors() {
		
		PropertyDescriptor[] ourPDs = new PropertyDescriptor[16];
		
		ourPDs[0] = new PropertyDescriptor("File", Messages.getString("File"));
		ourPDs[0].setDescription(Messages.getString("File"));
		
		ourPDs[1] = new PropertyDescriptor("File_number", Messages.getString("File_number"));
		ourPDs[1].setDescription(Messages.getString("File_number"));

		ourPDs[2] = new PropertyDescriptor("Job_name", Messages.getString("Job_name"));
		ourPDs[2].setDescription(Messages.getString("Job_name"));

		ourPDs[3] = new PropertyDescriptor("Job_user", Messages.getString("Job_user"));
		ourPDs[3].setDescription(Messages.getString("Job_user"));
		
		ourPDs[4] = new PropertyDescriptor("Job_number", Messages.getString("Job_number"));
		ourPDs[4].setDescription(Messages.getString("Job_number"));

		ourPDs[5] = new PropertyDescriptor("Job_system", Messages.getString("Job_system"));
		ourPDs[5].setDescription(Messages.getString("Job_system"));

		ourPDs[6] = new PropertyDescriptor("Creation_date", Messages.getString("Creation_date"));
		ourPDs[6].setDescription(Messages.getString("Creation_date"));

		ourPDs[7] = new PropertyDescriptor("Creation_time", Messages.getString("Creation_time"));
		ourPDs[7].setDescription(Messages.getString("Creation_time"));

		ourPDs[8] = new PropertyDescriptor("Status", Messages.getString("Status"));
		ourPDs[8].setDescription(Messages.getString("Status"));
		
		ourPDs[9] = new PropertyDescriptor("Output_queue", Messages.getString("Output_queue"));
		ourPDs[9].setDescription(Messages.getString("Output_queue"));
		
		ourPDs[10] = new PropertyDescriptor("Output_priority", Messages.getString("Output_priority"));
		ourPDs[10].setDescription(Messages.getString("Output_priority")); 
		
		ourPDs[11] = new PropertyDescriptor("User_data", Messages.getString("User_data"));
		ourPDs[11].setDescription(Messages.getString("User_data"));

		ourPDs[12] = new PropertyDescriptor("Form_type", Messages.getString("Form_type"));
		ourPDs[12].setDescription(Messages.getString("Form_type"));

		ourPDs[13] = new PropertyDescriptor("Copies", Messages.getString("Copies"));
		ourPDs[13].setDescription(Messages.getString("Copies"));
		
		ourPDs[14] = new PropertyDescriptor("pages", Messages.getString("Pages"));
		ourPDs[14].setDescription(Messages.getString("Pages"));

		ourPDs[15] = new PropertyDescriptor("Current_page", Messages.getString("Current_page"));
		ourPDs[15].setDescription(Messages.getString("Current_page"));
		
		return ourPDs;
		
	}
	
	public Object internalGetPropertyValue(SpooledFile splf, Object propKey) {
		if ("File".equals(propKey)) return splf.getFile();
		if ("File_number".equals(propKey)) return Integer.toString(splf.getFileNumber());
		if ("Job_name".equals(propKey)) return splf.getJobName();
		if ("Job_user".equals(propKey)) return splf.getJobUser();
		if ("Job_number".equals(propKey)) return splf.getJobNumber();
		if ("Job_system".equals(propKey)) return splf.getJobSystem();
		if ("Creation_date".equals(propKey)) return splf.getCreationDateFormated();
		if ("Creation_time".equals(propKey)) return splf.getCreationTimeFormated();
		if ("Status".equals(propKey)) return splf.getStatus();
		if ("Output_queue".equals(propKey)) return splf.getOutputQueueFormated();
		if ("Output_priority".equals(propKey)) return splf.getOutputPriority();
		if ("User_data".equals(propKey)) return splf.getUserData();
		if ("Form_type".equals(propKey)) return splf.getFormType(); 
		if ("Copies".equals(propKey)) return Integer.toString(splf.getCopies()); 
		if ("pages".equals(propKey)) return Integer.toString(splf.getPages());
		if ("Current_page".equals(propKey)) return Integer.toString(splf.getCurrentPage());
		return null;
	}
	
	public String getAbsoluteParentName(SpooledFile splf) {
		return "root";
	}
	
	public String getSubSystemFactoryId() {
		return "biz.isphere.spooledfiles.subsystems.factory";
	}
	
	public String getRemoteTypeCategory(SpooledFile splf) {
		return "spooled files";
	}
	
	public String getRemoteType(SpooledFile splf) {
		return "spooled file";
	}

}
