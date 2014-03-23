/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.internal.IEditor;
import biz.isphere.internal.IMessageFileSearchObjectFilterCreator;
import biz.isphere.internal.ISourceFileSearchMemberFilterCreator;


public class ISpherePlugin extends AbstractUIPlugin {

	private static ISpherePlugin plugin;
	private static URL installURL;
	public static IEditor editor = null;
	public static ISourceFileSearchMemberFilterCreator sourceFileSearchMemberFilterCreator = null;
	public static IMessageFileSearchObjectFilterCreator messageFileSearchObjectFilterCreator = null;
	private File spooledFilesDirectory;
	private IProject spooledFilesProject;
	public static final String IMAGE_TASKFORCE = "TaskForce.bmp";
    public static final String IMAGE_TOOLS400 = "Tools400.bmp";
	public static final String IMAGE_ERROR = "error.gif";
	public static final String IMAGE_NEW = "new.gif";
	public static final String IMAGE_CHANGE = "change.gif";
	public static final String IMAGE_COPY = "copy.gif";
	public static final String IMAGE_DELETE = "delete.gif";
	public static final String IMAGE_DISPLAY = "display.gif";
	public static final String IMAGE_REFRESH = "refresh.gif";
	public static final String IMAGE_MESSAGE_FILE = "message_file.gif";
	public static final String IMAGE_MESSAGE = "message.gif";
	public static final String IMAGE_COMPARE = "compare.gif";
	public static final String IMAGE_BINDING_DIRECTORY = "binding_directory.gif";
	public static final String IMAGE_MINUS = "minus.gif";
	public static final String IMAGE_SOURCE_FILE_SEARCH = "source_file_search.gif";
	public static final String IMAGE_MESSAGE_FILE_SEARCH = "message_file_search.png";
	public static final String IMAGE_SELECT_ALL = "select_all.gif";
	public static final String IMAGE_DESELECT_ALL = "deselect_all.gif";
	public static final String IMAGE_OPEN_EDITOR = "open_editor.gif";
	public static final String IMAGE_SPOOLED_FILE = "spooled_file.gif";
	public static final String IMAGE_SPOOLED_FILE_FILTER = "spooled_file_filter.gif";
	public static final String IMAGE_EXCEL = "excel.png";
	public static final String IMAGE_MEMBER_FILTER = "member_filter.gif";
	public static final String IMAGE_OBJECT_FILTER = "object_filter.gif";
	public static final String IMAGE_ISPHERE = "isphere.gif";
	
	public ISpherePlugin() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {

		super.start(context);
		
		installURL = context.getBundle().getEntry("/");
		
		initializePreferenceStoreDefaults();
		
		spooledFilesDirectory = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "iSphereSpooledFiles");
		if (!spooledFilesDirectory.exists())
			spooledFilesDirectory.mkdirs();
		
		spooledFilesProject = ResourcesPlugin.getWorkspace().getRoot().getProject("iSphereSpooledFiles");
		if (!spooledFilesProject.exists()) {
			IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(spooledFilesProject.getName());
			spooledFilesProject.create(description, null);
		}
		
	}

	public void stop(BundleContext context) throws Exception {

		super.stop(context);
		
		File[] files = getSpooledFilesDirectory().listFiles();
		for (int idx = 0; idx < files.length; idx++) {
			if (!files[idx].getName().equals(".project")) {
				files[idx].delete();
			}
		} 
		
	}

	public static ISpherePlugin getDefault() {
		return plugin;
	}
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMAGE_TASKFORCE, getImageDescriptor(IMAGE_TASKFORCE));
        reg.put(IMAGE_TOOLS400, getImageDescriptor(IMAGE_TOOLS400));
		reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
		reg.put(IMAGE_NEW, getImageDescriptor(IMAGE_NEW));
		reg.put(IMAGE_CHANGE, getImageDescriptor(IMAGE_CHANGE));
		reg.put(IMAGE_COPY, getImageDescriptor(IMAGE_COPY));
		reg.put(IMAGE_DELETE, getImageDescriptor(IMAGE_DELETE));
		reg.put(IMAGE_DISPLAY, getImageDescriptor(IMAGE_DISPLAY));
		reg.put(IMAGE_REFRESH, getImageDescriptor(IMAGE_REFRESH));
		reg.put(IMAGE_MESSAGE_FILE, getImageDescriptor(IMAGE_MESSAGE_FILE));
		reg.put(IMAGE_MESSAGE, getImageDescriptor(IMAGE_MESSAGE));
		reg.put(IMAGE_COMPARE, getImageDescriptor(IMAGE_COMPARE));
		reg.put(IMAGE_BINDING_DIRECTORY, getImageDescriptor(IMAGE_BINDING_DIRECTORY));
		reg.put(IMAGE_MINUS, getImageDescriptor(IMAGE_MINUS));
		reg.put(IMAGE_SOURCE_FILE_SEARCH, getImageDescriptor(IMAGE_SOURCE_FILE_SEARCH));
		reg.put(IMAGE_MESSAGE_FILE_SEARCH, getImageDescriptor(IMAGE_MESSAGE_FILE_SEARCH));
		reg.put(IMAGE_SELECT_ALL, getImageDescriptor(IMAGE_SELECT_ALL));
		reg.put(IMAGE_DESELECT_ALL, getImageDescriptor(IMAGE_DESELECT_ALL));
		reg.put(IMAGE_OPEN_EDITOR, getImageDescriptor(IMAGE_OPEN_EDITOR));
		reg.put(IMAGE_SPOOLED_FILE, getImageDescriptor(IMAGE_SPOOLED_FILE));
		reg.put(IMAGE_SPOOLED_FILE_FILTER, getImageDescriptor(IMAGE_SPOOLED_FILE_FILTER));
		reg.put(IMAGE_EXCEL, getImageDescriptor(IMAGE_EXCEL));
		reg.put(IMAGE_MEMBER_FILTER, getImageDescriptor(IMAGE_MEMBER_FILTER));
		reg.put(IMAGE_OBJECT_FILTER, getImageDescriptor(IMAGE_OBJECT_FILTER));
		reg.put(IMAGE_ISPHERE, getImageDescriptor(IMAGE_ISPHERE));
	}
	
	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "icons/"; 
		try {
			URL url = new URL(installURL, iconPath + name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	protected void initializePreferenceStoreDefaults(){
		
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.LIBRARY", "ISPHERE");
		
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.DEFAULT_FORMAT", "*TEXT");
		
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT", "*DFT");
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY", "");
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND", "");
	
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML", "*DFT");
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY", "");
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND", "");
		
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF", "*DFT");
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY", "");
		getPreferenceStore().setDefault("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND", "");
	
	}
	
	public static URL getInstallURL() {
		return installURL;
	}
	
	public static String getISphereLibrary() {
		return ISpherePlugin.getDefault().getPreferenceStore().getString("DE.TASKFORCE.ISPHERE.LIBRARY");
	}

	public static IEditor getEditor() {
		return editor;
	}

	public static void setEditor(IEditor _editor) {
		editor = _editor;
	}

	public static ISourceFileSearchMemberFilterCreator getSourceFileSearchMemberFilterCreator() {
		return sourceFileSearchMemberFilterCreator;
	}

	public static void setSourceFileSearchMemberFilterCreator(ISourceFileSearchMemberFilterCreator _sourceFileSearchMemberFilterCreator) {
		sourceFileSearchMemberFilterCreator = _sourceFileSearchMemberFilterCreator;
	}

	public static IMessageFileSearchObjectFilterCreator getMessageFileSearchObjectFilterCreator() {
		return messageFileSearchObjectFilterCreator;
	}

	public static void setMessageFileSearchObjectFilterCreator(IMessageFileSearchObjectFilterCreator _messageFileSearchObjectFilterCreator) {
		messageFileSearchObjectFilterCreator = _messageFileSearchObjectFilterCreator;
	}
	
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public File getSpooledFilesDirectory() {
		return spooledFilesDirectory;
	}

	public IProject getSpooledFilesProject() {
		return spooledFilesProject;
	}

}
