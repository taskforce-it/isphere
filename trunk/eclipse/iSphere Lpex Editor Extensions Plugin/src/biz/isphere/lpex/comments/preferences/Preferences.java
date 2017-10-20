/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Version;

import biz.isphere.base.versioncheck.PluginCheck;
import biz.isphere.lpex.comments.ISphereLpexEditorExtensionsPlugin;
import biz.isphere.lpex.comments.lpex.MenuExtension;

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

    private static final String DOMAIN = ISphereLpexEditorExtensionsPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    /*
     * Outdated with iSphere 3.1
     */
    private static final String ENABLED = DOMAIN + "ENABLED."; //$NON-NLS-1$

    private static final String COMMENTS_ENABLED = DOMAIN + "COMMENTS_ENABLED."; //$NON-NLS-1$

    private static final String INDENTION_ENABLED = DOMAIN + "INDENTION_ENABLED."; //$NON-NLS-1$

    private static final String USER_KEY_ACTIONS = DOMAIN + "USER_KEY_ACTIONS"; //$NON-NLS-1$

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
            instance.preferenceStore = ISphereLpexEditorExtensionsPlugin.getDefault().getPreferenceStore();
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

    public String getUserKeyActions() {
        return preferenceStore.getString(USER_KEY_ACTIONS);
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

    public void setUserKeyActions(String userKeyActions) {
        preferenceStore.setValue(USER_KEY_ACTIONS, userKeyActions);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(COMMENTS_ENABLED, getDefaultCommentsEnabled());
        preferenceStore.setDefault(INDENTION_ENABLED, getDefaultIndentionEnabled());
        preferenceStore.setDefault(USER_KEY_ACTIONS, getInitialUserKeyActions());

        /*
         * Outdated setting
         */
        preferenceStore.setDefault(ENABLED, getDefaultCommentsEnabled());
    }

    /*
     * Preferences: Default Values
     */

    public boolean getDefaultCommentsEnabled() {

        if (isWDSCiDevelomentEnvironment()) {
            return true;
        }

        return false;
    }

    public boolean getDefaultIndentionEnabled() {

        return true;
    }

    public String getInitialUserKeyActions() {
        return MenuExtension.getInitialUserKeyActions();
    }

    /*
     * Others
     */

    public boolean isCSpecPositionEnabled() {

        IPreferenceStore preferenceStore = getIBMPreferencesStore();
        String key;
        if (isWDSCiDevelomentEnvironment()) {
            key = "com.ibm.etools.iseries.core.preferences.parser.ilerpg.enter.setpos.cfreespec"; //$NON-NLS-1$
        } else {
            key = "com.ibm.etools.iseries.edit.preferences.parser.ilerpg.enter.setpos.cfreespec"; //$NON-NLS-1$
        }

        boolean enabled = preferenceStore.getBoolean(key);

        return enabled;
    }

    public int getCSpecPosition() {

        IPreferenceStore preferenceStore = getIBMPreferencesStore();
        String key;
        if (isWDSCiDevelomentEnvironment()) {
            key = "com.ibm.etools.iseries.core.preferences.parser.ilerpg.enter.setpos.cfreespec.value"; //$NON-NLS-1$
        } else {
            key = "com.ibm.etools.iseries.edit.preferences.parser.ilerpg.enter.setpos.cfreespec.value"; //$NON-NLS-1$
        }

        int position = preferenceStore.getInt(key);

        return position;
    }

    public int getCSpecIndention() {

        IPreferenceStore preferenceStore = getIBMPreferencesStore();
        String key;
        if (isWDSCiDevelomentEnvironment()) {
            key = "com.ibm.etools.iseries.core.preferences.parser.ilerpg.enter.autoindent.S1_Blanks"; //$NON-NLS-1$
        } else {
            key = "com.ibm.etools.iseries.edit.preferences.parser.ilerpg.enter.autoindent.S1_Blanks"; //$NON-NLS-1$
        }

        int indention = preferenceStore.getInt(key);

        return indention;
    }

    private IPreferenceStore getIBMPreferencesStore() {

        if (isWDSCiDevelomentEnvironment()) {
            return new ScopedPreferenceStore(new InstanceScope(), "com.ibm.etools.iseries.core"); //$NON-NLS-1$
        } else {
            return new ScopedPreferenceStore(new InstanceScope(), "com.ibm.etools.iseries.edit"); //$NON-NLS-1$
        }
    }

    private boolean isWDSCiDevelomentEnvironment() {

        Version platformVersion = PluginCheck.getPlatformVersion();
        if (platformVersion.getMajor() <= WDSCI70.getMajor()) {
            if (platformVersion.getMinor() <= WDSCI70.getMinor()) {
                return true;
            }
        }

        return false;
    }
}