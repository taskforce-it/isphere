/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rexec.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.rexec.iSphereRexecPlugin;

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

    private static final String DOMAIN = iSphereRexecPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String REXEC_SERVER = DOMAIN + "REXEC_SERVER."; //$NON-NLS-1$

    public static final String SERVER_ENABLED = REXEC_SERVER + "SERVER_ENABLED"; //$NON-NLS-1$

    public static final String SERVER_LISTENER_PORT = REXEC_SERVER + "SERVER_LISTENER_PORT"; //$NON-NLS-1$

    private static final String SERVER_SOCKET_READ_TIMEOUT = REXEC_SERVER + "SERVER_SOCKET_READ_TIMEOUT"; //$NON-NLS-1$

    private static final String CAPTURE_OUTPUT = REXEC_SERVER + "CAPTURE_OUTPUT"; //$NON-NLS-1$

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
            instance.preferenceStore = iSphereRexecPlugin.getDefault().getPreferenceStore();
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

    public boolean isServerEnabled() {
        return preferenceStore.getBoolean(SERVER_ENABLED);
    }

    public int getListenerPort() {
        return preferenceStore.getInt(SERVER_LISTENER_PORT);
    }

    public int getSocketReadTimeout() {
        return preferenceStore.getInt(SERVER_SOCKET_READ_TIMEOUT);
    }

    public boolean isCaptureOutput() {
        return preferenceStore.getBoolean(CAPTURE_OUTPUT);
    }

    /*
     * Preferences: SETTER
     */

    public void setServerEnabled(boolean enabled) {
        preferenceStore.setValue(SERVER_ENABLED, enabled);
    }

    public void setListenerPort(int port) {
        preferenceStore.setValue(SERVER_LISTENER_PORT, port);
    }

    public void setSocketReadTimeout(int timeout) {
        preferenceStore.setValue(SERVER_SOCKET_READ_TIMEOUT, timeout);
    }

    public void setCaptureOutput(boolean enabled) {
        preferenceStore.setValue(CAPTURE_OUTPUT, enabled);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(SERVER_ENABLED, getInitialServerEnabled());
        preferenceStore.setDefault(SERVER_LISTENER_PORT, getInitialListenerPort());
        preferenceStore.setDefault(SERVER_SOCKET_READ_TIMEOUT, getInitialSocketReadTimeout());
        preferenceStore.setDefault(CAPTURE_OUTPUT, getInitialCaptureOutput());
    }

    /*
     * Preferences: Default Values
     */

    public boolean getInitialServerEnabled() {
        return false;
    }

    public int getInitialListenerPort() {
        return 512;
    }

    public int getInitialSocketReadTimeout() {
        return 500;
    }

    public boolean getInitialCaptureOutput() {
        return true;
    }
}