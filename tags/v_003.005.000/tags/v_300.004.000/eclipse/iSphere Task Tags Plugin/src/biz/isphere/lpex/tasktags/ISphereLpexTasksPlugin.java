/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.lpex.tasktags.preferences.Preferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereLpexTasksPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.lpex.tasks"; //$NON-NLS-1$

    // The shared instance
    private static ISphereLpexTasksPlugin plugin;

    /**
     * The constructor
     */
    public ISphereLpexTasksPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        Preferences.dispose();
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereLpexTasksPlugin getDefault() {
        return plugin;
    }

    /**
     * Return the preferences store of this plugin.
     * 
     * @return Preferences store of the plugin.
     */
    public static IPreferenceStore getPreferencesStore() {
        return getDefault().getPreferenceStore();
    }

    /**
     * Convenience method to log error messages to the application log.
     * 
     * @param message Message
     * @param e The exception that has produced the error
     */
    public static void logError(String message, Exception e) {
        plugin.getLog().log(new Status(Status.ERROR, PLUGIN_ID, Status.ERROR, message, e));
    }

}
