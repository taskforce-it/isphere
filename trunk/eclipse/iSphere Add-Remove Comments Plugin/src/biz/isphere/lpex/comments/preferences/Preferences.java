/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Version;

import biz.isphere.base.versioncheck.PluginCheck;
import biz.isphere.lpex.comments.ISphereAddRemoveCommentsPlugin;

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

    // WDSCi 7.0 = Eclipse 3.2
    private static final Version WDSCI70 = new Version(3, 2, 0);

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

    private static final String DOMAIN = ISphereAddRemoveCommentsPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    /*
     * Outdated with iSphere 3.1
     */
    private static final String ENABLED = DOMAIN + "ENABLED."; //$NON-NLS-1$

    private static final String COMMENTS_ENABLED = DOMAIN + "COMMENTS_ENABLED."; //$NON-NLS-1$

    private static final String INDENTION_ENABLED = DOMAIN + "INDENTION_ENABLED."; //$NON-NLS-1$

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
            instance.preferenceStore = ISphereAddRemoveCommentsPlugin.getDefault().getPreferenceStore();
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

    public boolean isCommentsEnabled() {
        return preferenceStore.getBoolean(COMMENTS_ENABLED);
    }

    public boolean isIndentionEnabled() {
        return preferenceStore.getBoolean(INDENTION_ENABLED);
    }

    /*
     * Preferences: SETTER
     */

    public void setCommentsEnabled(boolean enabled) {
        preferenceStore.setValue(COMMENTS_ENABLED, enabled);
    }

    public void setIndentionEnabled(boolean enabled) {
        preferenceStore.setValue(INDENTION_ENABLED, enabled);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(COMMENTS_ENABLED, getDefaultCommentsEnabled());
        preferenceStore.setDefault(INDENTION_ENABLED, getDefaultIndentionEnabled());

        /*
         * Outdated setting
         */
        preferenceStore.setDefault(ENABLED, getDefaultCommentsEnabled());
    }

    /*
     * Preferences: Default Values
     */

    public boolean getDefaultCommentsEnabled() {

        if (PluginCheck.getPlatformVersion().compareTo(WDSCI70) <= 0) {
            return true;
        }

        return false;
    }

    public boolean getDefaultIndentionEnabled() {

        return true;
    }
}