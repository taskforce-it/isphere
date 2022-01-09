/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.annotations.CMOneDeprecated;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;
import biz.isphere.tn5250j.core.preferences.Preferences;
import biz.isphere.tn5250j.rse.sessionsview.SessionsView;

public class TN5250JRSEPlugin extends AbstractUIPlugin {

    private static TN5250JRSEPlugin plugin;
    private static URL installURL;
    public static final String PLUGIN_ID = "biz.isphere.tn5250j.rse";
    public static final String IMAGE_TN5250J = "tn5250j.png";
    public static final String IMAGE_INFO = "info.gif";
    public static final String IMAGE_NEW = "new.gif";
    public static final String IMAGE_CHANGE = "change.gif";
    public static final String IMAGE_DISPLAY = "display.gif";
    public static final String IMAGE_DELETE = "delete.gif";
    public static final String IMAGE_DESIGNER = "designer.gif";
    public static final String IMAGE_EDIT_DESIGNER = "edit_designer.gif";
    public static final String IMAGE_BROWSE_DESIGNER = "browse_designer.gif";

    public TN5250JRSEPlugin() {
        plugin = this;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        installURL = context.getBundle().getEntry("/");

        IAdapterManager manager = Platform.getAdapterManager();
        biz.isphere.tn5250j.rse.model.RSESessionAdapterFactory factory = new biz.isphere.tn5250j.rse.model.RSESessionAdapterFactory();
        manager.registerAdapters(factory, biz.isphere.tn5250j.rse.model.RSESession.class);

        initializePreferenceStoreDefaults();

        if (Preferences.getInstance().isActivateViewsOnStartup()) {
            activatePinned5250Sessions();
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static TN5250JRSEPlugin getDefault() {
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
        reg.put(IMAGE_TN5250J, getImageDescriptor(IMAGE_TN5250J));
        reg.put(IMAGE_INFO, getImageDescriptor(IMAGE_INFO));
        reg.put(IMAGE_NEW, getImageDescriptor(IMAGE_NEW));
        reg.put(IMAGE_CHANGE, getImageDescriptor(IMAGE_CHANGE));
        reg.put(IMAGE_DISPLAY, getImageDescriptor(IMAGE_DISPLAY));
        reg.put(IMAGE_DELETE, getImageDescriptor(IMAGE_DELETE));
        reg.put(IMAGE_DESIGNER, getImageDescriptor(IMAGE_DESIGNER));
        reg.put(IMAGE_EDIT_DESIGNER, getImageDescriptor(IMAGE_EDIT_DESIGNER));
        reg.put(IMAGE_BROWSE_DESIGNER, getImageDescriptor(IMAGE_BROWSE_DESIGNER));
    }

    protected void initializePreferenceStoreDefaults() {
    }

    public static URL getInstallURL() {
        return installURL;
    }

    public static String getRSEConnectionDirectory() {
        return TN5250JCorePlugin.getTN5250JPluginDirectory();
    }

    @CMOneDeprecated(info = "Replaced by: getRSESessionDirectory(String rseProfile, String rseConnection)")
    @CMOne(info = "Don`t change this method due to CMOne compatibility reasons")
    public static String getRSESessionDirectory(String connection) {
        return getRSEConnectionDirectory() + File.separator + connection.replaceAll(":", "_");
    }

    public static String getRSESessionDirectory(String rseProfile, String rseConnection) {
        String connection = rseProfile.replaceAll(":", "_") + "-" + rseConnection;
        return getRSEConnectionDirectory() + File.separator + connection;
    }

    private void activatePinned5250Sessions() {

        UIJob job = new UIJob("Restoring 5250 sessions ...") {

            @Override
            public IStatus runInUIThread(IProgressMonitor paramIProgressMonitor) {

                IViewReference[] viewReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
                for (IViewReference iViewReference : viewReferences) {
                    if (SessionsView.ID.equals(iViewReference.getId())) {
                        IWorkbenchPart part = iViewReference.getPart(false);
                        if (part == null) {
                            part = iViewReference.getPart(true);
                        }
                        if (part instanceof SessionsView) {
                            SessionsView sessionsView = (SessionsView)part;
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(sessionsView);
                        }
                    }
                }

                return Status.OK_STATUS;
            }
        };

        job.schedule();
    }

}
