/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.framework.transport.SocketConnector;

import biz.isphere.tn5250j.core.Messages;
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

    private String[] groupByLabels;

    /*
     * Preferences keys:
     */

    private static final String BIZ_ISPHERE_TN5250J_PORT = "BIZ.ISPHERE.TN5250J.PORT"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_CODEPAGE = "BIZ.ISPHERE.TN5250J.CODEPAGE"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_SCREENSIZE = "BIZ.ISPHERE.TN5250J.SCREENSIZE"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_AREA = "BIZ.ISPHERE.TN5250J.AREA"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_MULTI_SESSIONS_ENABLED = "BIZ.ISPHERE.TN5250J.MULTI_SESSIONS_ENABLED"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_SESSION_GROUPING = "BIZ.ISPHERE.TN5250J.SESSION.GROUPING"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_MSACTIVE = "BIZ.ISPHERE.TN5250J.MSACTIVE"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_MSHSIZE = "BIZ.ISPHERE.TN5250J.MSHSIZE"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_MSVSIZE = "BIZ.ISPHERE.TN5250J.MSVSIZE"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_ACTIVATE_VIEWS_ON_STARTUP = "BIZ.ISPHERE.TN5250J.ACTIVATE.VIEWS.ON.STARTUP"; //$NON-NLS-1$

    private static final String BIZ_ISPHERE_TN5250J_SSL_TYPE = "BIZ.ISPHERE.TN5250J.SSL_TYPE"; //$NON-NLS-1$

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

    public String getSessionGrouping() {
        return preferenceStore.getString(BIZ_ISPHERE_TN5250J_SESSION_GROUPING);
    }

    public String getSessionGroupingLabel() {

        String groupBy = preferenceStore.getString(BIZ_ISPHERE_TN5250J_SESSION_GROUPING);

        if (ISession.GROUP_BY_SESSION.equals(groupBy)) {
            return Messages.SessionGrouping_Session;
        } else if (ISession.GROUP_BY_CONNECTION.equals(groupBy)) {
            return Messages.SessionGrouping_Connection;
        } else {
            return Messages.SessionGrouping_No_Grouping;
        }
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

    public boolean isActivateViewsOnStartup() {
        return preferenceStore.getBoolean(BIZ_ISPHERE_TN5250J_ACTIVATE_VIEWS_ON_STARTUP);
    }

    public String getSSLType() {
        return preferenceStore.getString(BIZ_ISPHERE_TN5250J_SSL_TYPE);
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

    public void setSessionGroupingByLabel(String groupByLabel) {

        String[] labels = getSessionGroupingLables();
        if (labels[2].equals(groupByLabel)) {
            preferenceStore.setValue(BIZ_ISPHERE_TN5250J_SESSION_GROUPING, ISession.GROUP_BY_SESSION); // Session
        } else if (labels[1].equals(groupByLabel)) {
            preferenceStore.setValue(BIZ_ISPHERE_TN5250J_SESSION_GROUPING, ISession.GROUP_BY_CONNECTION); // Connection
        } else {
            preferenceStore.setValue(BIZ_ISPHERE_TN5250J_SESSION_GROUPING, ISession.GROUP_BY_NOTHING); // All
        }
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

    public void setActivateViewsOnStartup(boolean enable) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_ACTIVATE_VIEWS_ON_STARTUP, enable);
    }

    public void setSSLType(String sslType) {
        preferenceStore.setValue(BIZ_ISPHERE_TN5250J_SSL_TYPE, sslType);
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
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_SESSION_GROUPING, getDefaultSessionGrouping());

        if (getDefaultIsMinimalSessionSizeEnabled()) {
            preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSACTIVE, "Y");
        } else {
            preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSACTIVE, "N");
        }

        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSHSIZE, getDefaultMinimalSessionHorizontalSize());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_MSVSIZE, getDefaultMinimalSessionVerticalSize());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_ACTIVATE_VIEWS_ON_STARTUP, getDefaultActivateViewsOnStartup());
        preferenceStore.setDefault(BIZ_ISPHERE_TN5250J_SSL_TYPE, getDefaultSSLType());
    }

    /*
     * Preferences: Default Values
     */

    public int getDefaultSessionPortNumber() {
        return Integer.parseInt(TN5250jConstants.PORT_NUMBER);
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

    public String getDefaultSessionGrouping() {
        return ISession.GROUP_BY_NOTHING;
    }

    public String getDefaultSessionGroupingLabel() {
        return Messages.SessionGrouping_No_Grouping;
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

    public boolean getDefaultActivateViewsOnStartup() {
        return false;
    }

    public String[] getSessionGroupingLables() {

        if (groupByLabels == null) {
            groupByLabels = new String[] { Messages.SessionGrouping_No_Grouping, Messages.SessionGrouping_Connection,
                Messages.SessionGrouping_Session };
        }

        return groupByLabels;
    }

    public String getDefaultSSLType() {
        return TN5250jConstants.SSL_TYPE_NONE;
    }

    /*
     * Preferences: Others
     */

    public String[] getSSLTypeOptions() {

        List<String> sslProtocols = new ArrayList<String>();

        sslProtocols.add(TN5250jConstants.SSL_TYPE_NONE);
        sslProtocols.add(TN5250jConstants.SSL_TYPE_DEFAULT);
        sslProtocols.addAll(Arrays.asList(SocketConnector.getSupportedSSLProtocols()));

        return sslProtocols.toArray(new String[sslProtocols.size()]);
    }
}