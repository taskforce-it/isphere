/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.preferences;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.tn5250j.core.TN5250JCorePlugin;
import biz.isphere.tn5250j.core.session.ISession;

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

    /**
     * The instance of this Singleton class.
     */
    private static Preferences instance;

    /**
     * Global preferences of the plugin.
     */
    private IPreferenceStore preferenceStore;

    /**
     * List of date formats.
     */
    private Map<String, String> dateFormats;

    /**
     * List of time formats.
     */
    private Map<String, String> timeFormats;

    /**
     * List of suggested spooled file names.
     */
    private Map<String, String> suggestedSpooledFileNames;

    /*
     * Preferences keys:
     */

    public static final String BIZ_ISPHERE_TN5250J_PORT = "BIZ.ISPHERE.TN5250J.PORT"; //$NON-NLS-1$

    public static final String BIZ_ISPHERE_TN5250J_CODEPAGE = "BIZ.ISPHERE.TN5250J.CODEPAGE"; //$NON-NLS-1$

    public static final String BIZ_ISPHERE_TN5250J_SCREENSIZE = "BIZ.ISPHERE.TN5250J.SCREENSIZE"; //$NON-NLS-1$

    public static final String BIZ_ISPHERE_TN5250J_AREA = "BIZ.ISPHERE.TN5250J.AREA"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_MULTI_SESSIONS_ENABLED = "BIZ.ISPHERE.TN5250J.MULTI_SESSIONS_ENABLED"; //$NON-NLS-1$

    public static final String BIZ_ISPHERE_TN5250J_MSACTIVE = "BIZ.ISPHERE.TN5250J.MSACTIVE"; //$NON-NLS-1$

    public static final String BIZ_ISPHERE_TN5250J_MSHSIZE = "BIZ.ISPHERE.TN5250J.MSHSIZE"; //$NON-NLS-1$

    public static final String BIZ_ISPHERE_TN5250J_MSVSIZE = "BIZ.ISPHERE.TN5250J.MSVSIZE"; //$NON-NLS-1$

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
            instance.preferenceStore = TN5250JCorePlugin.getDefault().getPreferenceStore();
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

    public int getSessionPortNumber() {
        return preferenceStore.getInt(BIZ_ISPHERE_TN5250J_PORT);
    }

    public String getSessionCodepage() {
        return preferenceStore.getString(BIZ_ISPHERE_TN5250J_CODEPAGE);
    }

    public String getSessionScreenSize() {
        return preferenceStore.getString(BIZ_ISPHERE_TN5250J_SCREENSIZE);
    }

    public String getSessionArea() {
        return preferenceStore.getString(BIZ_ISPHERE_TN5250J_AREA);
    }

    public boolean isMultiSessionEnabled() {
        return preferenceStore.getBoolean(BIZ_ISPHERE_TN5250J_MULTI_SESSIONS_ENABLED);
    }

    public boolean isMinimalSessionSizeEnabled() {
        if ("Y".equals(preferenceStore.getString(BIZ_ISPHERE_TN5250J_MSACTIVE))) {
            return true;
        } else {
            return false;
        }
    }

    public int getMinimalSessionHorizontalSize() {
        return preferenceStore.getInt(BIZ_ISPHERE_TN5250J_MSHSIZE);
    }

    public int getMinimalSessionVerticalSize() {
        return preferenceStore.getInt(BIZ_ISPHERE_TN5250J_MSVSIZE);
    }

    /*
     * Preferences: SETTER
     */

    public void setSessionPortNumber(int portNumber) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_PORT, portNumber);
    }

    public void setSessionCodepage(String codepage) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_CODEPAGE, codepage);
    }

    public void setSessionScreenSize(String screenSize) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_SCREENSIZE, screenSize);
    }

    public void setSessionArea(String area) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_AREA, area);
    }

    public void setIsMultiSessionEnabled(boolean enabled) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_MULTI_SESSIONS_ENABLED, enabled);
    }

    public void setIsMinimalSessionEnabled(boolean enabled) {
        if (enabled) {
            preferenceStore.setValue(BIZ_ISPHERE_TN5250J_MSACTIVE, "Y");
        } else {
            preferenceStore.setValue(BIZ_ISPHERE_TN5250J_MSACTIVE, "N");
        }
    }

    public void setMinimalSessionHorizontalSize(int hSize) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_MSHSIZE, hSize);
    }

    public void setMinimalSessionVerticalSize(int vSize) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_MSVSIZE, vSize);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_PORT, getDefaultSessionPortNumber());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_CODEPAGE, getDefaultSessionCodepage());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_SCREENSIZE, getDefaultSessionScreenSize());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_AREA, getDefaultSessionArea());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MULTI_SESSIONS_ENABLED, getDefaultIsMultiSessionEnabled());

        if (getDefaultIsMinimalSessionSizeEnabled()) {
            preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSACTIVE, "Y");
        } else {
            preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSACTIVE, "N");
        }

        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSHSIZE, getDefaultMinimalSessionHorizontalSize());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSVSIZE, getDefaultMinimalSessionVerticalSize());
    }

    /*
     * Preferences: Default Values
     */

    public int getDefaultSessionPortNumber() {
        return 23;
    }

    public String getDefaultSessionCodepage() {
        return "";
    }

    public String getDefaultSessionScreenSize() {
        return ISession.SIZE_132;
    }

    public String getDefaultSessionArea() {
        return ISession.AREA_VIEW;
    }

    public boolean getDefaultIsMultiSessionEnabled() {
        return true;
    }

    public boolean getDefaultIsMinimalSessionSizeEnabled() {
        return false;
    }

    public int getDefaultMinimalSessionHorizontalSize() {
        return 0;
    }

    public int getDefaultMinimalSessionVerticalSize() {
        return 0;
    }
}