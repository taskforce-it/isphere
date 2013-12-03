/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package de.taskforce.isphere;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.taskforce.isphere.internal.IEditor;

public class ISpherePlugin extends AbstractUIPlugin {

	private static ISpherePlugin plugin;
	private static URL installURL;
	public static IEditor editor = null;
	public static final String IMAGE_CMONE = "cmone.bmp";
	public static final String IMAGE_TASKFORCE = "TaskForce.bmp";
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
	
	public ISpherePlugin() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		installURL = context.getBundle().getEntry("/");
		initializePreferenceStoreDefaults();
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public static ISpherePlugin getDefault() {
		return plugin;
	}
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMAGE_CMONE, getImageDescriptor(IMAGE_CMONE));
		reg.put(IMAGE_TASKFORCE, getImageDescriptor(IMAGE_TASKFORCE));
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
	
}
