/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import biz.isphere.core.dataspaceeditordesigner.repository.DataSpaceEditorRepository;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.IMessageFileSearchObjectFilterCreator;
import biz.isphere.core.internal.ISourceFileSearchMemberFilterCreator;
import biz.isphere.core.internal.SearchForUpdates;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.ISearchArgumentsListEditorProvider;
import biz.isphere.core.swt.widgets.extension.WidgetFactory;

public class ISpherePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.core"; //$NON-NLS-1$

    private static final String MIN_SERVER_VERSION = "2.5.0"; //$NON-NLS-1$

    private static ISpherePlugin plugin;
    private static URL installURL;
    public static IEditor editor = null;
    public static ISourceFileSearchMemberFilterCreator sourceFileSearchMemberFilterCreator = null;
    public static IMessageFileSearchObjectFilterCreator messageFileSearchObjectFilterCreator = null;
    private File spooledFilesDirectory;
    private IProject spooledFilesProject;
    public static final String IMAGE_ERROR = "error.gif";
    public static final String IMAGE_NEW = "new.gif";
    public static final String IMAGE_CHANGE = "change.gif";
    public static final String IMAGE_COPY = "copy.gif";
    public static final String IMAGE_DELETE = "delete.gif";
    public static final String IMAGE_DISPLAY = "display.gif";
    public static final String IMAGE_REFRESH = "refresh.gif";
    public static final String IMAGE_MESSAGE_FILE = "message_file.gif";
    public static final String IMAGE_MESSAGE = "message.gif";
    public static final String IMAGE_DATA_AREA = "data_area.gif";
    public static final String IMAGE_USER_SPACE = "user_space.gif";
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
    public static final String IMAGE_SWITCH_MEMBER = "switch_member.gif";
    public static final String IMAGE_EXPAND_ALL = "expandall.gif";
    public static final String IMAGE_COLLAPSE_ALL = "collapseall.gif";
    public static final String IMAGE_NEW_DIALOG = "newdialog.gif";
    public static final String IMAGE_ADD_DATA_AREA = "add_data_area.gif";
    public static final String IMAGE_ADD_USER_SPACE = "add_user_space.gif";
    public static final String IMAGE_REMOVE_DATA_SPACE = "remove_data_space.gif";
    public static final String IMAGE_WATCHING = "watching.gif";
    public static final String IMAGE_DATA_AREA_MONITOR = "data_monitor.gif";
    public static final String IMAGE_USER_SPACE_MONITOR = "data_monitor.gif";
    public static final String IMAGE_VALUE_CHANGED = "value_changed.gif";
    public static final String IMAGE_SYSTEM_HELP = "systemhelp.gif";
    public static final String IMAGE_AUTO_REFRESH_OFF = "auto_refresh_off.gif";
    public static final String IMAGE_UP = "up.gif";
    public static final String IMAGE_DOWN = "down.gif";
    public static final String IMAGE_RENAME = "rename.gif";
    public static final String IMAGE_PIN = "pin.gif";
    public static final String IMAGE_REMOVE = "remove.gif";
    public static final String IMAGE_INVERT_SELECTION = "invert_selection.gif";

    private static boolean searchArgumentsListEditor = false;
    private static ISearchArgumentsListEditorProvider searchArgumentsListEditorProvider = null;

    public ISpherePlugin() {
        super();
        plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception {

        super.start(context);

        installURL = context.getBundle().getEntry("/");

        spooledFilesDirectory = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separator + "iSphereSpooledFiles");
        if (!spooledFilesDirectory.exists()) spooledFilesDirectory.mkdirs();

        spooledFilesProject = ResourcesPlugin.getWorkspace().getRoot().getProject("iSphereSpooledFiles");
        if (!spooledFilesProject.exists()) {
            IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(spooledFilesProject.getName());
            spooledFilesProject.create(description, null);
        }

        if (Preferences.getInstance().isSearchForUpdates()) {
            SearchForUpdates search = new SearchForUpdates(false);
            search.setUser(false);
            search.schedule();
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {

        super.stop(context);

        File[] files = getSpooledFilesDirectory().listFiles();
        for (int idx = 0; idx < files.length; idx++) {
            if (!files[idx].getName().equals(".project")) {
                files[idx].delete();
            }
        }

        Preferences.dispose();
        DataSpaceEditorRepository.dispose();
        WidgetFactory.dispose();

    }

    public static ISpherePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the name of the plugin, as assigned to "Bundle-Name" in
     * "MANIFEST.MF".
     * 
     * @return Name of the plugin.
     */
    public String getName() {
        String name = (String)getBundle().getHeaders().get(Constants.BUNDLE_NAME);
        if (name == null) {
            name = "";
        }
        return name;
    }

    /**
     * Returns the version of the plugin, as assigned to "Bundle-Version" in
     * "MANIFEST.MF".
     * 
     * @return Version of the plugin.
     */
    public String getVersion() {
        String version = (String)getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        if (version == null) {
            version = "0.0.0";
        }
        return version;
    }

    /**
     * Returns the version of the plugin, as assigned to "Bundle-Version" in
     * "MANIFEST.MF" formatted as "vvrrmm".
     * 
     * @return Version of the plugin.
     */
    public String getMinServerVersion() {
        return MIN_SERVER_VERSION;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
        reg.put(IMAGE_NEW, getImageDescriptor(IMAGE_NEW));
        reg.put(IMAGE_CHANGE, getImageDescriptor(IMAGE_CHANGE));
        reg.put(IMAGE_COPY, getImageDescriptor(IMAGE_COPY));
        reg.put(IMAGE_DELETE, getImageDescriptor(IMAGE_DELETE));
        reg.put(IMAGE_DISPLAY, getImageDescriptor(IMAGE_DISPLAY));
        reg.put(IMAGE_REFRESH, getImageDescriptor(IMAGE_REFRESH));
        reg.put(IMAGE_MESSAGE_FILE, getImageDescriptor(IMAGE_MESSAGE_FILE));
        reg.put(IMAGE_MESSAGE, getImageDescriptor(IMAGE_MESSAGE));
        reg.put(IMAGE_DATA_AREA, getImageDescriptor(IMAGE_DATA_AREA));
        reg.put(IMAGE_USER_SPACE, getImageDescriptor(IMAGE_USER_SPACE));
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
        reg.put(IMAGE_SWITCH_MEMBER, getImageDescriptor(IMAGE_SWITCH_MEMBER));
        reg.put(IMAGE_EXPAND_ALL, getImageDescriptor(IMAGE_EXPAND_ALL));
        reg.put(IMAGE_COLLAPSE_ALL, getImageDescriptor(IMAGE_COLLAPSE_ALL));
        reg.put(IMAGE_NEW_DIALOG, getImageDescriptor(IMAGE_NEW_DIALOG));
        reg.put(IMAGE_ADD_DATA_AREA, getImageDescriptor(IMAGE_ADD_DATA_AREA));
        reg.put(IMAGE_ADD_USER_SPACE, getImageDescriptor(IMAGE_ADD_USER_SPACE));
        reg.put(IMAGE_REMOVE_DATA_SPACE, getImageDescriptor(IMAGE_REMOVE_DATA_SPACE));
        reg.put(IMAGE_WATCHING, getImageDescriptor(IMAGE_WATCHING));
        reg.put(IMAGE_DATA_AREA_MONITOR, getImageDescriptor(IMAGE_DATA_AREA_MONITOR));
        reg.put(IMAGE_USER_SPACE_MONITOR, getImageDescriptor(IMAGE_USER_SPACE_MONITOR));
        reg.put(IMAGE_VALUE_CHANGED, getImageDescriptor(IMAGE_VALUE_CHANGED));
        reg.put(IMAGE_SYSTEM_HELP, getImageDescriptor(IMAGE_SYSTEM_HELP));
        reg.put(IMAGE_AUTO_REFRESH_OFF, getImageDescriptor(IMAGE_AUTO_REFRESH_OFF));
        reg.put(IMAGE_UP, getImageDescriptor(IMAGE_UP));
        reg.put(IMAGE_DOWN, getImageDescriptor(IMAGE_DOWN));
        reg.put(IMAGE_RENAME, getImageDescriptor(IMAGE_RENAME));
        reg.put(IMAGE_PIN, getImageDescriptor(IMAGE_PIN));
        reg.put(IMAGE_REMOVE, getImageDescriptor(IMAGE_REMOVE));
        reg.put(IMAGE_INVERT_SELECTION, getImageDescriptor(IMAGE_INVERT_SELECTION));
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

    public static URL getInstallURL() {
        return installURL;
    }

    public static String getISphereLibrary() {
        return Preferences.getInstance().getISphereLibrary();
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

    /**
     * Convenience method to log error messages to the application log.
     * 
     * @param message Message
     * @param e The exception that has produced the error
     */
    public static void logError(String message, Throwable e) {
        plugin.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e));
    }

    public static boolean isSearchArgumentsListEditor() {
        return searchArgumentsListEditor;
    }

    public static void setSearchArgumentsListEditor(boolean searchArgumentsListEditor) {
        ISpherePlugin.searchArgumentsListEditor = searchArgumentsListEditor;
    }

    public static ISearchArgumentsListEditorProvider getSearchArgumentsListEditorProvider() {
        return searchArgumentsListEditorProvider;
    }

    public static void setSearchArgumentsListEditorProvider(ISearchArgumentsListEditorProvider searchArgumentsListEditorProvider) {
        ISpherePlugin.searchArgumentsListEditorProvider = searchArgumentsListEditorProvider;
    }
}
