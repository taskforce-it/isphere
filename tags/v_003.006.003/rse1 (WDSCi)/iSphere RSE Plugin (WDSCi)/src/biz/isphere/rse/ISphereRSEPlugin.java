/*******************************************************************************
 * Copyright (c) 2012-2018 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.core.lpex.menu.ILpexMenuExtension;
import biz.isphere.core.lpex.menu.LpexMenuExtensionPlugin;
import biz.isphere.rse.connection.ConnectionManager;
import biz.isphere.rse.internal.Editor;
import biz.isphere.rse.internal.MessageFileSearchObjectFilterCreator;
import biz.isphere.rse.internal.SourceFileSearchMemberFilterCreator;
import biz.isphere.rse.internal.ViewManager;
import biz.isphere.rse.search.SearchArgumentsListEditorProvider;
import biz.isphere.rse.spooledfiles.SpooledFileAdapterFactory;
import biz.isphere.rse.spooledfiles.SpooledFileResource;

import com.ibm.etools.systems.core.SystemPlugin;

public class ISphereRSEPlugin extends AbstractUIPlugin implements LpexMenuExtensionPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.rse"; //$NON-NLS-1$

    // The Lpex menu extension
    private ILpexMenuExtension menuExtension;

    // The shared instance
    private static ISphereRSEPlugin plugin;

    private static URL installURL;

    private Map<String, IViewManager> viewManagers;

    public ISphereRSEPlugin() {
        super();
        plugin = this;
        viewManagers = new HashMap<String, IViewManager>();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        installURL = context.getBundle().getEntry("/");
        ISpherePlugin.setSaveNeededHandling(true);
        ISpherePlugin.setEditor(new Editor());
        ISpherePlugin.setSourceFileSearchMemberFilterCreator(new SourceFileSearchMemberFilterCreator());
        ISpherePlugin.setMessageFileSearchObjectFilterCreator(new MessageFileSearchObjectFilterCreator());
        ISpherePlugin.setSearchArgumentsListEditor(true);
        ISpherePlugin.setSearchArgumentsListEditorProvider(new SearchArgumentsListEditorProvider());
        setupAdapters();

        SystemPlugin.getTheSystemRegistry().addSystemModelChangeListener(ConnectionManager.getInstance());
    }

    @Override
    public void stop(BundleContext context) throws Exception {

        if (menuExtension != null) {
            menuExtension.uninstall();
        }

        super.stop(context);

        ConnectionManager.dispose();

        for (IViewManager viewManager : viewManagers.values()) {
            viewManager.dispose();
        }
    }

    public void setLpexMenuExtension(ILpexMenuExtension menuExtension) {
        this.menuExtension = menuExtension;
    }

    public static ISphereRSEPlugin getDefault() {
        return plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
    }

    @CMOne(info = "Don`t change this method due to CMOne compatibility reasons")
    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/";
        try {
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

    private void setupAdapters() {
        IAdapterManager manager = Platform.getAdapterManager();
        SpooledFileAdapterFactory spooledFactory = new SpooledFileAdapterFactory();
        manager.registerAdapters(spooledFactory, SpooledFileResource.class);
    }

    public IViewManager getViewManager(String name) {

        IViewManager viewManager = viewManagers.get(name);
        if (viewManager == null) {
            viewManager = new ViewManager(name);
            viewManagers.put(name, viewManager);
        }

        return viewManager;
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
}
