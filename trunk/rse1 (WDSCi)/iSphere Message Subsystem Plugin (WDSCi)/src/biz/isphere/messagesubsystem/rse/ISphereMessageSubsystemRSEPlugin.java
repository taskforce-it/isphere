/*******************************************************************************
 * Copyright (c) 2005-2006 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import biz.isphere.messagesubsystem.rse.internal.QueuedMessageAdapterFactory;
import biz.isphere.messagesubsystem.rse.internal.QueuedMessageResource;

/**
 * The main plugin class to be used in the desktop.
 */
public class ISphereMessageSubsystemRSEPlugin extends AbstractUIPlugin {
    // The shared instance.
    private static ISphereMessageSubsystemRSEPlugin plugin;
    // Resource bundle.
    private ResourceBundle resourceBundle;
    private static URL installURL;

    public static final String IMAGE_MESSAGE = "message.gif"; //$NON-NLS-1$
    public static final String IMAGE_MESSAGES_CONNECTED = "messages_connected.gif"; //$NON-NLS-1$
    public static final String IMAGE_MESSAGES = "messages.gif"; //$NON-NLS-1$
    public static final String IMAGE_MESSAGE_FILTER = "message_filter.gif"; //$NON-NLS-1$
    public static final String IMAGE_INQUIRY = "inquiry.gif"; //$NON-NLS-1$

    private static final String ID = "biz.isphere.messagesubsystem.rse";

    /**
     * The constructor.
     */
    public ISphereMessageSubsystemRSEPlugin() {
        super();
        plugin = this;
        try {
            resourceBundle = ResourceBundle.getBundle("net.sourceforge.isphere.Resources"); //$NON-NLS-1$
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        ISphereMessageSubsystemRSEPlugin.installURL = context.getBundle().getEntry("/"); //$NON-NLS-1$
        setupAdapters();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static ISphereMessageSubsystemRSEPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    public static String getString(String key, Object[] messageArguments) {
        MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
        formatter.applyPattern(getResourceString(key));
        return formatter.format(messageArguments);
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not
     * found.
     */
    public static String getResourceString(String key) {
        ResourceBundle bundle = ISphereMessageSubsystemRSEPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons/"; //$NON-NLS-1$
        try {
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            // should not happen
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

    public static String getTempProject() {
        return "spooledFiles"; //$NON-NLS-1$
    }

    private void setupAdapters() {
        
        IAdapterManager manager = Platform.getAdapterManager();
        
        QueuedMessageAdapterFactory messagesFactory = new QueuedMessageAdapterFactory();
        manager.registerAdapters(messagesFactory, QueuedMessageResource.class);
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        
        reg.put(IMAGE_MESSAGE, getImageDescriptor(IMAGE_MESSAGE));
        reg.put(IMAGE_MESSAGES, getImageDescriptor(IMAGE_MESSAGES));
        reg.put(IMAGE_MESSAGES_CONNECTED, getImageDescriptor(IMAGE_MESSAGES_CONNECTED));
        reg.put(IMAGE_MESSAGE_FILTER, getImageDescriptor(IMAGE_MESSAGE_FILTER));
        reg.put(IMAGE_INQUIRY, getImageDescriptor(IMAGE_INQUIRY));
    }

    /**
     * Convenience method for logging statuses to the plugin log
     * 
     * @param status the status to log
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    public static void log(String msg) {
        getDefault().getLog().log(new Status(IStatus.INFO, ISphereMessageSubsystemRSEPlugin.ID, 0, msg, null));
    }

    public static void log(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.trim().length() == 0) msg = ISphereMessageSubsystemRSEPlugin.getResourceString("pluginError.internal");
        ISphereMessageSubsystemRSEPlugin.log(msg, e);
    }

    public static void log(String msg, Exception e) {
        getDefault().getLog().log(new Status(IStatus.ERROR, ISphereMessageSubsystemRSEPlugin.ID, 0, msg, e));
        MessageDialog.openError(Display.getCurrent().getActiveShell(),
            ISphereMessageSubsystemRSEPlugin.getResourceString("pluginError"), msg + ISphereMessageSubsystemRSEPlugin.getResourceString("pluginError.log")); //$NON-NLS-1$
    }
}
