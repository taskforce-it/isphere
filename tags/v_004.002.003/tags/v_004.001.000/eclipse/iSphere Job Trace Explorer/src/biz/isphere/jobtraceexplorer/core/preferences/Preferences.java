/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.model.dao.JobTraceSQLDAO;
import biz.isphere.jobtraceexplorer.core.ui.preferencepages.HighlightColor;

/**
 * Class to manage access to the preferences of the plugin.
 * <p>
 * Eclipse stores the preferences as <i>diffs</i> to their default values in
 * directory
 * <code>[workspace]\.metadata\.plugins\org.eclipse.core.runtime\.settings\</code>.
 * 
 * @author Thomas Raddatz
 */
public final class Preferences {

    private static String DELIMITER = "|"; //$NON-NLS-1$

    /**
     * The instance of this Singleton class.
     */
    private static Preferences instance;

    /**
     * Global preferences of the plugin.
     */
    private IPreferenceStore preferenceStore;

    /*
     * Preferences keys:
     */

    private static final String DOMAIN = ISphereJobTraceExplorerCorePlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String COLORS = DOMAIN + "COLORS."; //$NON-NLS-1$

    public static final String LIMITATIONS = DOMAIN + "LIMITATIONS."; //$NON-NLS-1$

    public static final String MAX_NUM_ROWS_TO_FETCH = LIMITATIONS + "MAX_NUM_ROWS_TO_FETCH"; //$NON-NLS-1$

    public static final String EXPORT_JOURNAL_ENTRIES = DOMAIN + "EXPORT_JOB_TRACE_ENTRIES."; //$NON-NLS-1$

    public static final String EXPORT_PATH = DOMAIN + "EXPORT_PATH"; //$NON-NLS-1$

    public static final String EXPORT_FILE_JSON = DOMAIN + "EXPORT_FILE_JSON"; //$NON-NLS-1$

    public static final String LOAD_JOB_TRACE_DATA = DOMAIN + "LOAD_JOB_TRACE_DATA."; //$NON-NLS-1$

    public static final String SQL_WHERE_NO_IBM_DATA = LOAD_JOB_TRACE_DATA + "SQL_WHERE_NO_IBM_DATA"; //$NON-NLS-1$

    private ColorRegistry colorRegistry;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Preferences() {
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
            instance.preferenceStore = ISphereJobTraceExplorerCorePlugin.getDefault().getPreferenceStore();
        }
        return instance;
    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }

    /*
     * Preferences: GETTER
     */

    public int getMaximumNumberOfRowsToFetch() {

        int maxNumRows = preferenceStore.getInt(MAX_NUM_ROWS_TO_FETCH);
        if (maxNumRows <= 0) {
            maxNumRows = Integer.MAX_VALUE;
        }

        return maxNumRows;
    }

    public String getExportPath() {
        return preferenceStore.getString(EXPORT_PATH);
    }

    public String getExportFileJson() {
        return preferenceStore.getString(EXPORT_FILE_JSON);
    }

    public String getExcludeIBMDataSQLWhereClause() {
        return preferenceStore.getString(SQL_WHERE_NO_IBM_DATA);
    }

    public Color getColorSeverity(HighlightColor highlight) {
        return loadColor(highlight);
    }

    /*
     * Preferences: SETTER
     */

    public void setMaximumNumberOfRowsToFetch(int maxNumRows) {
        preferenceStore.setValue(MAX_NUM_ROWS_TO_FETCH, maxNumRows);
    }

    public void setExportPath(String exportPath) {
        preferenceStore.setValue(EXPORT_PATH, exportPath);
    }

    public void setExportFileJson(String exportFile) {
        preferenceStore.setValue(EXPORT_FILE_JSON, exportFile);
    }

    public void setExcludeIBMDataSQLWhereClause(String sqlWhereClause) {
        preferenceStore.setValue(SQL_WHERE_NO_IBM_DATA, sqlWhereClause);
    }

    public void setColorSeverity(HighlightColor highlight, RGB rgb) {
        storeColor(highlight, rgb);
    }

    /*
     * Others
     */

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(MAX_NUM_ROWS_TO_FETCH, getInitialMaximumNumberOfRowsToFetch());

        preferenceStore.setDefault(EXPORT_PATH, getInitialExportPath());
        preferenceStore.setDefault(EXPORT_FILE_JSON, getInitialExportFileJson());
        preferenceStore.setDefault(SQL_WHERE_NO_IBM_DATA, getInitialExcludeIBMDataSQLWhereClause());

        preferenceStore.setDefault(getColorKey(HighlightColor.ATTRIBUTES), rgbToString(getDefaultColorSeverity(HighlightColor.ATTRIBUTES)));
        preferenceStore.setDefault(getColorKey(HighlightColor.PROCEDURES), rgbToString(getDefaultColorSeverity(HighlightColor.PROCEDURES)));
        preferenceStore.setDefault(getColorKey(HighlightColor.HIDDEN_PROCEDURES),
            rgbToString(getDefaultColorSeverity(HighlightColor.HIDDEN_PROCEDURES)));
    }

    /*
     * Preferences: Default Values
     */

    public int getInitialMaximumNumberOfRowsToFetch() {
        return 5000;
    }

    public String getInitialExportPath() {
        return FileHelper.getDefaultRootDirectory();
    }

    public String getInitialExportFileJson() {
        return "ExportJobTraceEntries"; //$NON-NLS-1$
    }

    public String getInitialExcludeIBMDataSQLWhereClause() {
        return JobTraceSQLDAO.SQL_WHERE_NO_IBM_DATA;
    }

    public RGB getDefaultColorSeverity(HighlightColor severity) {

        if (severity == HighlightColor.ATTRIBUTES) {
            return new RGB(185, 255, 185);
        } else if (severity == HighlightColor.PROCEDURES) {
            return new RGB(255, 255, 190);
        } else if (severity == HighlightColor.HIDDEN_PROCEDURES) {
            return new RGB(225, 225, 225);
        } else {
            Color color = ColorHelper.getListBackground();
            return color.getRGB();
        }
    }

    /*
     * Property change listeners
     */

    public void addPropertyChangeListener(IPropertyChangeListener listener) {
        preferenceStore.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(IPropertyChangeListener listener) {
        preferenceStore.removePropertyChangeListener(listener);
    }

    /*
     * Helpers
     */

    private void storeColor(HighlightColor highlight, RGB rgb) {

        String colorKey = getColorKey(highlight);

        preferenceStore.setValue(colorKey, rgbToString(rgb));
        getColorRegistry().put(colorKey, rgb);
    }

    private String rgbToString(RGB rgb) {
        return rgb.red + DELIMITER + rgb.green + DELIMITER + rgb.blue;
    }

    private Color loadColor(HighlightColor highlight) {

        String colorKey = getColorKey(highlight);

        Color color = getColorRegistry().get(colorKey);
        if (color == null) {

            String rgb = preferenceStore.getString(colorKey);

            String[] rgbParts = rgb.split("\\" + DELIMITER); //$NON-NLS-1$
            if (rgbParts.length < 3) {
                return null;
            }

            int red = IntHelper.tryParseInt(rgbParts[0], -1);
            int green = IntHelper.tryParseInt(rgbParts[1], -1);
            int blue = IntHelper.tryParseInt(rgbParts[2], -1);

            if (red < 0 || green < 0 || blue < 0) {
                return null;
            }

            getColorRegistry().put(colorKey, new RGB(red, green, blue));
            color = getColorRegistry().get(colorKey);
        }

        return color;
    }

    private String getColorKey(HighlightColor highlight) {

        return COLORS + highlight.key();
    }

    private ColorRegistry getColorRegistry() {

        if (colorRegistry == null) {
            colorRegistry = new ColorRegistry();
        }

        return colorRegistry;
    }
}