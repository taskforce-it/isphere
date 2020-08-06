/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.ide.lpex.menu.ILpexMenuExtension;
import biz.isphere.ide.lpex.menu.LpexMenuExtensionPlugin;
import biz.isphere.strpreprc.model.HeaderTemplates;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereStrPrePrcPlugin extends AbstractUIPlugin implements LpexMenuExtensionPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.strpreprc"; //$NON-NLS-1$

    // The shared instance
    private static ISphereStrPrePrcPlugin plugin;

    // The Lpex menu extension
    private ILpexMenuExtension menuExtension;

    /**
     * The constructor
     */
    public ISphereStrPrePrcPlugin() {
        super();
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {

        if (menuExtension != null) {
            menuExtension.uninstall();
        }

        plugin = null;
        super.stop(context);

        HeaderTemplates.dispose();
    }

    public void setLpexMenuExtension(ILpexMenuExtension menuExtension) {
        this.menuExtension = menuExtension;
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereStrPrePrcPlugin getDefault() {
        return plugin;
    }

}
