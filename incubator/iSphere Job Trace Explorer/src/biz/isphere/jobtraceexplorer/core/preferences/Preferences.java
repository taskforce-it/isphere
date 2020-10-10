/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;

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

    private static final String DOMAIN = ISphereJobTraceExplorerCorePlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    public static final String LIMITATIONS = DOMAIN + "LIMITATIONS."; //$NON-NLS-1$

    public static final String MAX_NUM_ROWS_TO_FETCH = LIMITATIONS + "MAX_NUM_ROWS_TO_FETCH"; //$NON-NLS-1$

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

    /*
     * Preferences: SETTER
     */

    public void setMaximumNumberOfRowsToFetch(int maxNumRows) {
        preferenceStore.setValue(MAX_NUM_ROWS_TO_FETCH, maxNumRows);
    }

    /*
     * Others
     */

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(MAX_NUM_ROWS_TO_FETCH, getInitialMaximumNumberOfRowsToFetch());
    }

    /*
     * Preferences: Default Values
     */

    public int getInitialMaximumNumberOfRowsToFetch() {
        return 1000;
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
}