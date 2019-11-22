/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;

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

    private static final String DOMAIN = ISphereJobLogExplorerPlugin.PLUGIN_ID + "."; //$NON-NLS-1$
    private static final String LANGUAGE = DOMAIN + "LANGUAGE."; //$NON-NLS-1$
    private static final String COLORS = DOMAIN + "COLORS."; //$NON-NLS-1$
    private static final String ENABLED = COLORS + "ENABLED"; //$NON-NLS-1$
    private static final String EXPORT_FOLDER = DOMAIN + "EXPORT_FOLDER"; //$NON-NLS-1$

    private ColorRegistry colorRegistry;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Preferences() {

        this.colorRegistry = new ColorRegistry();
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
            instance.preferenceStore = ISphereJobLogExplorerPlugin.getDefault().getPreferenceStore();
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

    public boolean isColoringEnabled() {
        return preferenceStore.getBoolean(ENABLED);
    }

    public Color getColorSeverity(SeverityColor severity) {
        return loadColor(severity);
    }

    public String getLanguage() {
        return preferenceStore.getString(LANGUAGE);
    }

    public String getExportFolder() {

        String directory = preferenceStore.getString(EXPORT_FOLDER);
        if (StringHelper.isNullOrEmpty(directory)) {
            directory = FileHelper.getDefaultRootDirectory();
        }

        return directory;
    }

    /*
     * Preferences: SETTER
     */

    public void setColoringEnabled(boolean enabled) {
        preferenceStore.setValue(ENABLED, enabled);
    }

    public void setColorSeverity(SeverityColor severity, RGB rgb) {
        storeColor(severity, rgb);
    }

    public void setLanguage(String languageId) {
        preferenceStore.setValue(LANGUAGE, languageId);
    }

    public void setExportFolder(String folderPath) {
        preferenceStore.setValue(EXPORT_FOLDER, folderPath);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(ENABLED, getDefaultColoringEnabled());
        preferenceStore.setDefault(getColorKey(SeverityColor.SEVERITY_BL), rgbToString(getDefaultColorSeverity(SeverityColor.SEVERITY_BL)));
        preferenceStore.setDefault(getColorKey(SeverityColor.SEVERITY_00), rgbToString(getDefaultColorSeverity(SeverityColor.SEVERITY_00)));
        preferenceStore.setDefault(getColorKey(SeverityColor.SEVERITY_10), rgbToString(getDefaultColorSeverity(SeverityColor.SEVERITY_10)));
        preferenceStore.setDefault(getColorKey(SeverityColor.SEVERITY_20), rgbToString(getDefaultColorSeverity(SeverityColor.SEVERITY_20)));
        preferenceStore.setDefault(getColorKey(SeverityColor.SEVERITY_30), rgbToString(getDefaultColorSeverity(SeverityColor.SEVERITY_30)));
        preferenceStore.setDefault(getColorKey(SeverityColor.SEVERITY_40), rgbToString(getDefaultColorSeverity(SeverityColor.SEVERITY_40)));
        preferenceStore.setDefault(LANGUAGE, getDefaultLanguage());
        preferenceStore.setDefault(EXPORT_FOLDER, getDefaultExportFolder());
    }

    /*
     * Preferences: Default Values
     */

    public boolean getDefaultColoringEnabled() {
        return true;
    }

    public RGB getDefaultColorSeverity(SeverityColor severity) {

        if (severity == SeverityColor.SEVERITY_BL) {
            return new RGB(185, 255, 185);
        } else if (severity == SeverityColor.SEVERITY_20) {
            return new RGB(255, 255, 128);
        } else if (severity == SeverityColor.SEVERITY_30) {
            return new RGB(255, 128, 128);
        } else if (severity == SeverityColor.SEVERITY_40) {
            return new RGB(255, 0, 0);
        } else {
            Color color = ColorHelper.getListBackground();
            return color.getRGB();
        }
    }

    public String getDefaultLanguage() {
        return "*CURRENT";//$NON-NLS-1$
    }

    private String getDefaultExportFolder() {
        return FileHelper.getDefaultRootDirectory();
    }

    /*
     * Helpers
     */

    private void storeColor(SeverityColor severity, RGB rgb) {

        String colorKey = getColorKey(severity);

        preferenceStore.setValue(colorKey, rgbToString(rgb));
        colorRegistry.put(colorKey, rgb);
    }

    private String rgbToString(RGB rgb) {
        return rgb.red + DELIMITER + rgb.green + DELIMITER + rgb.blue;
    }

    private Color loadColor(SeverityColor severity) {

        String colorKey = getColorKey(severity);

        Color color = colorRegistry.get(colorKey);
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

            colorRegistry.put(colorKey, new RGB(red, green, blue));
            color = colorRegistry.get(colorKey);
        }

        return color;
    }

    private String getColorKey(SeverityColor severity) {

        return COLORS + severity.key();
    }
}