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
import org.eclipse.jface.resource.ImageRegistry;
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
    public static final String IMAGE_COLLAPSE_ALL = "collapseall.gif";
    public static final String IMAGE_EDIT_SQL = "edit_sql.png";
    public static final String IMAGE_EXCEL = "excel.png";
    public static final String IMAGE_RESET_COLUMN_SIZE = "reset_column_size.png";

    public static final String IMAGE_CHECKED = "checked.gif"; //$NON-NLS-1$
    public static final String IMAGE_UNCHECKED = "unchecked.gif"; //$NON-NLS-1$

    public static final String IMAGE_WARNING_OV = "warning_ov.gif";
    public static final String IMAGE_ERROR_OV = "error_ov.gif";
    public static final String IMAGE_LOADED_OV = "loaded_ov.gif";
    public static final String IMAGE_CHECKED_OV = "checked_ov.gif";
    public static final String IMAGE_DIRTY_OV = "dirty_ov.gif";
    public static final String IMAGE_NULL_OV = "null_ov.gif";

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

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put(IMAGE_COMPARE, getImageDescriptor(IMAGE_COMPARE));
        reg.put(IMAGE_DETAILS, getImageDescriptor(IMAGE_DETAILS));
        reg.put(IMAGE_HIGHLIGHT, getImageDescriptor(IMAGE_HIGHLIGHT));
        reg.put(IMAGE_HORIZONTAL_RESULTS_VIEW, getImageDescriptor(IMAGE_HORIZONTAL_RESULTS_VIEW));
        reg.put(IMAGE_JOURNAL_EXPLORER, getImageDescriptor(IMAGE_JOURNAL_EXPLORER));
        reg.put(IMAGE_CONFIGURE_PARSERS, getImageDescriptor(IMAGE_CONFIGURE_PARSERS));
        reg.put(IMAGE_REFRESH, getImageDescriptor(IMAGE_REFRESH));
        reg.put(IMAGE_SEGMENT_EDIT, getImageDescriptor(IMAGE_SEGMENT_EDIT));
        reg.put(IMAGE_OPEN_JOURNAL_OUTFILE, getImageDescriptor(IMAGE_OPEN_JOURNAL_OUTFILE));
        reg.put(IMAGE_COLLAPSE_ALL, getImageDescriptor(IMAGE_COLLAPSE_ALL));
        reg.put(IMAGE_EDIT_SQL, getImageDescriptor(IMAGE_EDIT_SQL));
        reg.put(IMAGE_EXCEL, getImageDescriptor(IMAGE_EXCEL));
        reg.put(IMAGE_RESET_COLUMN_SIZE, getImageDescriptor(IMAGE_RESET_COLUMN_SIZE));

        reg.put(IMAGE_CHECKED, getImageDescriptor(IMAGE_CHECKED));
        reg.put(IMAGE_UNCHECKED, getImageDescriptor(IMAGE_UNCHECKED));

        reg.put(IMAGE_WARNING_OV, getImageDescriptor(IMAGE_WARNING_OV));
        reg.put(IMAGE_ERROR_OV, getImageDescriptor(IMAGE_ERROR_OV));
        reg.put(IMAGE_LOADED_OV, getImageDescriptor(IMAGE_LOADED_OV));
        reg.put(IMAGE_CHECKED_OV, getImageDescriptor(IMAGE_CHECKED_OV));
        reg.put(IMAGE_DIRTY_OV, getImageDescriptor(IMAGE_DIRTY_OV));
        reg.put(IMAGE_NULL_OV, getImageDescriptor(IMAGE_NULL_OV));
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
