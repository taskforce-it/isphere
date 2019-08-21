/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleContext;

import biz.isphere.journalexplorer.core.preferences.Preferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereJournalExplorerCorePlugin extends AbstractUIPlugin {

    private static final String ICON_PATH = "icons/";
    public static final String IMAGE_COMPARE = "compare.png";
    public static final String IMAGE_DETAILS = "details.png";
    public static final String IMAGE_HIGHLIGHT = "highlight.png";
    public static final String IMAGE_HORIZONTAL_RESULTS_VIEW = "horizontal_results_view.gif";
    public static final String IMAGE_JOURNAL_EXPLORER = "journal_explorer.png";
    public static final String IMAGE_CONFIGURE_PARSERS = "configure_parsers.gif";
    public static final String IMAGE_REFRESH = "refresh.gif";
    public static final String IMAGE_SEGMENT_EDIT = "segment_edit.png";
    public static final String IMAGE_OPEN_JOURNAL_OUTFILE = "open_journal_outfile.png";
    public static final String IMAGE_WARNING_OV = "warning_ov.gif";
    public static final String IMAGE_ERROR_OV = "error_ov.gif";
    public static final String IMAGE_LOADED_OV = "loaded_ov.gif";
    public static final String IMAGE_CHECKED_OV = "checked_ov.gif";
    public static final String IMAGE_DIRTY_OV = "dirty_ov.gif";
    public static final String IMAGE_NULL_OV = "null_ov.gif";
    public static final String IMAGE_COLLAPSE_ALL = "collapseall.gif";
    public static final String IMAGE_EDIT_SQL = "edit_sql.png";

    // The plug-in ID
    public static final String PLUGIN_ID = "biz.isphere.journalexplorer.core"; //$NON-NLS-1$

    // The shared instance
    private static ISphereJournalExplorerCorePlugin plugin;

    /**
     * The constructor
     */
    public ISphereJournalExplorerCorePlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
     * BundleContext )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
     * BundleContext )
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
    public static ISphereJournalExplorerCorePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public ImageDescriptor getImageDescriptor(String path) {
        return ResourceManager.getPluginImageDescriptor(PLUGIN_ID, ICON_PATH + path);
    }

    public Image getImage(String path) {
        return ResourceManager.getPluginImage(PLUGIN_ID, ICON_PATH + path);
    }

    public Color getColor(RGB rgb) {
        return ResourceManager.getColor(rgb);
    }

    public Color getColor(String color) {
        return Preferences.getInstance().deserializeColor(color);
    }

    public Color getSystemColor(int color) {
        return Display.getCurrent().getSystemColor(color);
    }
}
