/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.model.dao.JobTraceSQLDAO;

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

    public static final String EXPORT_JOURNAL_ENTRIES = DOMAIN + "EXPORT_JOB_TRACE_ENTRIES."; //$NON-NLS-1$

    public static final String EXPORT_PATH = DOMAIN + "EXPORT_PATH"; //$NON-NLS-1$

    public static final String EXPORT_FILE_JSON = DOMAIN + "EXPORT_FILE_JSON"; //$NON-NLS-1$

    public static final String LOAD_JOB_TRACE_DATA = DOMAIN + "LOAD_JOB_TRACE_DATA."; //$NON-NLS-1$

    public static final String SQL_WHERE_NO_IBM_DATA = LOAD_JOB_TRACE_DATA + "SQL_WHERE_NO_IBM_DATA"; //$NON-NLS-1$

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