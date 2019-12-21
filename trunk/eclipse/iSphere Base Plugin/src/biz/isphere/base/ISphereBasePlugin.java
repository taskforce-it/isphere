/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereBasePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.base"; //$NON-NLS-1$

    // Contributor logos
    public static final String IMAGE_ISPHERE = "isphere.gif";
    public static final String IMAGE_TASKFORCE = "TaskForce.png";
    public static final String IMAGE_TOOLS400 = "Tools400.bmp";

    // Other iamges
    public static final String IMAGE_ERROR = "error.gif";
    public static final String IMAGE_RESET_COLUMN_SIZE = "reset_column_size.png";

    // The shared instance
    private static ISphereBasePlugin plugin;

    private static URL installURL;

    /**
     * The constructor
     */
    public ISphereBasePlugin() {

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

        installURL = context.getBundle().getEntry("/");

    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static ISphereBasePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the version of the plugin, as assigned to "Bundle-Version" in
     * "MANIFEST.MF".
     * <p>
     * The build process retrieves the version number from
     * <i>build.properties</i> and updates all MANIFEST, feature and plugin
     * files. (See: build.xml - updateVersionNumber)
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

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IMAGE_TASKFORCE, getImageDescriptor(IMAGE_TASKFORCE));
        reg.put(IMAGE_TOOLS400, getImageDescriptor(IMAGE_TOOLS400));
        reg.put(IMAGE_ISPHERE, getImageDescriptor(IMAGE_ISPHERE));
        reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
        reg.put(IMAGE_RESET_COLUMN_SIZE, getImageDescriptor(IMAGE_RESET_COLUMN_SIZE));
    }

    private ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/";
        try {
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

}
