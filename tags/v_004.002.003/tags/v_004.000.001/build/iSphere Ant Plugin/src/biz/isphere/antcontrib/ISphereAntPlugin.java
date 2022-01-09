/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import biz.isphere.antcontrib.configuration.Configuration;

public class ISphereAntPlugin implements BundleActivator {

    private static BundleContext context;
    private static ISphereAntPlugin plugin;
    private Configuration antConfig;

    public ISphereAntPlugin() {
        super();
        plugin = this;
    }

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
        ISphereAntPlugin.context = context;

        Bundle bundle = context.getBundle();
        URL url = FileLocator.find(bundle, new Path("/lib/jacob.jar"), null);
        String location = null;
        try {
            location = FileLocator.resolve(url).getPath();
        } catch (IOException e) {
            throw new RuntimeException("Could not locate file: jacob.jar", e);
        }

        String path = new File(location).getParent();
        antConfig = Configuration.getInstance();
        antConfig.configurePlugin(path);
    }
    

    public static ISphereAntPlugin getDefault() {
        return plugin;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        antConfig.dispose();
        ISphereAntPlugin.context = null;
    }

}
