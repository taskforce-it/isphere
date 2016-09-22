/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.tn5250j.core.tn5250jpart.HandleBindingService;

public class TN5250JCorePlugin extends AbstractUIPlugin {

    private static TN5250JCorePlugin plugin;
    private static URL installURL;
    public static final String PLUGIN_ID = "biz.isphere.tn5250j.core";
    public static final String BASIC = "XO498BDE993DET";
    public static final String IMAGE_ERROR = "error.gif";
    public static final String IMAGE_TN5250JSPLASH = "tn5250jSplash.jpg";
    public static final String IMAGE_PLUS = "plus.gif";
    public static final String IMAGE_MINUS = "minus.gif";
    public static final String IMAGE_ON = "on.gif";
    public static final String IMAGE_OFF = "off.gif";

    public TN5250JCorePlugin() {
        plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        installURL = context.getBundle().getEntry("/");

        String directory1 = getTN5250JHomeDirectory();
        File directory1TN5250J = new File(directory1);
        if (!directory1TN5250J.exists()) {
            directory1TN5250J.mkdir();
        }

        String directory2 = getTN5250JPluginDirectory();
        File directory2TN5250J = new File(directory2);
        if (!directory2TN5250J.exists()) {
            directory2TN5250J.mkdir();
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);

        HandleBindingService.dispose();
    }

    public static TN5250JCorePlugin getDefault() {
        return plugin;
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

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
        reg.put(IMAGE_TN5250JSPLASH, getImageDescriptor(IMAGE_TN5250JSPLASH));
        reg.put(IMAGE_PLUS, getImageDescriptor(IMAGE_PLUS));
        reg.put(IMAGE_MINUS, getImageDescriptor(IMAGE_MINUS));
        reg.put(IMAGE_ON, getImageDescriptor(IMAGE_ON));
        reg.put(IMAGE_OFF, getImageDescriptor(IMAGE_OFF));
    }

    public static URL getInstallURL() {
        return installURL;
    }

    public static String getTN5250JHomeDirectory() {
        return System.getProperty("user.home") + File.separator + ".tn5250j";
    }

    public static String getTN5250JPluginDirectory() {
        return getTN5250JHomeDirectory() + File.separator + "biz.isphere.tn5250j";
    }

}
