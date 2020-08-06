/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.ide.lpex.menu.ILpexMenuExtension;
import biz.isphere.ide.lpex.menu.LpexMenuExtensionPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereLpexEditorExtensionsPlugin extends AbstractUIPlugin implements LpexMenuExtensionPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.lpex.comments";

    // The shared instance
    private static ISphereLpexEditorExtensionsPlugin plugin;

    // The Lpex menu extension
    private ILpexMenuExtension menuExtension;

    /**
     * The constructor
     */
    public ISphereLpexEditorExtensionsPlugin() {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {

        if (menuExtension != null) {
            menuExtension.uninstall();
        }

        plugin = null;
        super.stop(context);
    }

    public void setLpexMenuExtension(ILpexMenuExtension menuExtension) {
        this.menuExtension = menuExtension;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereLpexEditorExtensionsPlugin getDefault() {
        return plugin;
    }

}
