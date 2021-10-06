/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.rse.ISphereRSEPlugin;

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

    /*
     * Preferences keys:
     */

    private static final String DOMAIN = ISphereRSEPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String APPEARANCE = DOMAIN + "APPEARANCE."; //$NON-NLS-1$

    private static final String DECORATION = APPEARANCE + "DECORATION."; //$NON-NLS-1$

    private static final String DECORATION_OBJECT_EXTENSION = DECORATION + "OBJECT_EXTENSION"; //$NON-NLS-1$

    private static final String DECORATION_SOURCE_MEMBER_EXTENSION = DECORATION + "SOURCE_MEMBER_EXTENSION"; //$NON-NLS-1$

    private static final String DECORATION_DATA_MEMBER_EXTENSION = DECORATION + "DATA_MEMBER_EXTENSION"; //$NON-NLS-1$

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
            instance.preferenceStore = ISphereRSEPlugin.getDefault().getPreferenceStore();
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

    public boolean isObjectDecorationExtension() {
        return preferenceStore.getBoolean(DECORATION_OBJECT_EXTENSION);
    }

    public boolean isSourceMemberDecorationExtension() {
        return preferenceStore.getBoolean(DECORATION_SOURCE_MEMBER_EXTENSION);
    }

    public boolean isDataMemberDecorationExtension() {
        return preferenceStore.getBoolean(DECORATION_DATA_MEMBER_EXTENSION);
    }

    /*
     * Preferences: SETTER
     */

    public void setObjectDecorationExtension(boolean enabled) {
        preferenceStore.setValue(DECORATION_OBJECT_EXTENSION, enabled);
    }

    public void setSourceMemberDecorationExtension(boolean enabled) {
        preferenceStore.setValue(DECORATION_SOURCE_MEMBER_EXTENSION, enabled);
    }

    public void setDataMemberDecorationExtension(boolean enabled) {
        preferenceStore.setValue(DECORATION_DATA_MEMBER_EXTENSION, enabled);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(DECORATION_OBJECT_EXTENSION, getDefaultObjectDecorationExtension());
        preferenceStore.setDefault(DECORATION_SOURCE_MEMBER_EXTENSION, getDefaultSourceMemberDecorationExtension());
        preferenceStore.setDefault(DECORATION_DATA_MEMBER_EXTENSION, getDefaultDataMemberDecorationExtension());
    }

    /*
     * Preferences: Default Values
     */

    public boolean getDefaultObjectDecorationExtension() {
        return false;
    }

    public boolean getDefaultSourceMemberDecorationExtension() {
        return false;
    }

    public boolean getDefaultDataMemberDecorationExtension() {
        return false;
    }

    public boolean getDefaultUseISphereJdbcConnectionManager() {
        return false;
    }
}