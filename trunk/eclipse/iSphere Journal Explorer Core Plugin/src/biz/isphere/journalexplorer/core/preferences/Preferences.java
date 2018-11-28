/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.preferences;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import biz.isphere.base.internal.Buffer;
import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryAppearanceAttributes;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumnUI;

/**
 * Class to manage access to the preferences of the plugin.
 * <p>
 * Eclipse stores the preferences as <i>diffs</i> to their default values in
 * directory
 * <code>[workspace]\.metadata\.plugins\org.eclipse.core.runtime\.settings\</code>.
 * 
 * @author Thomas Raddatz
 */
public final class Preferences implements ColumnsDAO {

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

    private static final String DOMAIN = ISphereJournalExplorerCorePlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    public static final String HIGHLIGHT_USER_ENTRIES = DOMAIN + "HIGHLIGHT_USER_ENTRIES."; //$NON-NLS-1$

    public static final String COLORS = DOMAIN + "COLOR."; //$NON-NLS-1$

    public static final String ENABLED = COLORS + "ENABLED"; //$NON-NLS-1$

    public static final String COLUMNS_ORDER = DOMAIN + "COLUMNS_ORDER."; //$NON-NLS-1$

    private static Color COLOR_ID = ISphereJournalExplorerCorePlugin.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);

    private static Color COLOR_JOB = ISphereJournalExplorerCorePlugin.getDefault().getColor(new RGB(255, 255, 128)); // yellow

    private static Color COLOR_OBJECT = ISphereJournalExplorerCorePlugin.getDefault().getColor(new RGB(255, 128, 64)); // orange

    private static final String COLOR_NULL = "[null]"; //$NON-NLS-1$

    public static final String LIMITATIONS = DOMAIN + "LIMITATIONS."; //$NON-NLS-1$

    public static final String MAX_NUM_ROWS_TO_FETCH = LIMITATIONS + "MAX_NUM_ROWS_TO_FETCH"; //$NON-NLS-1$

    public static final String BUFFER_SIZE = LIMITATIONS + "BUFFER_SIZE"; //$NON-NLS-1$

    public static final String DYNAMIC_BUFFER_SIZE = LIMITATIONS + "DYNAMIC_BUFFER_SIZE"; //$NON-NLS-1$

    public static final String LOAD_JOURNAL_ENTRIES = DOMAIN + "LOAD_JOURNAL_ENTRIES."; //$NON-NLS-1$

    public static final String EXPORT_JOURNAL_ENTRIES = DOMAIN + "EXPORT_JOURNAL_ENTRIES."; //$NON-NLS-1$

    public static final String EXPORT_PATH = DOMAIN + "EXPORT_PATH"; //$NON-NLS-1$

    public static final String EXPORT_FILE = DOMAIN + "EXPORT_FILE"; //$NON-NLS-1$

    public static final String EXPORT_COLUMN_HEADINGS = DOMAIN + "EXPORT_COLUMN_HEADINGS"; //$NON-NLS-1$

    private SimpleDateFormat dateFormatter;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Preferences() {

        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
            instance.preferenceStore = ISphereJournalExplorerCorePlugin.getDefault().getPreferenceStore();
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

    public boolean isHighlightUserEntries() {
        return preferenceStore.getBoolean(HIGHLIGHT_USER_ENTRIES);
    }

    public boolean isColoringEnabled() {
        return preferenceStore.getBoolean(ENABLED);
    }

    public JournalEntryAppearanceAttributes[] getSortedJournalEntryAppearancesAttributes() {

        List<JournalEntryAppearanceAttributes> journalEntryAppearanceAttributes = new LinkedList<JournalEntryAppearanceAttributes>();

        for (String columnName : getSortedColumnNames()) {

            Color color = null;
            String rgb = preferenceStore.getString(getColorKey(columnName));

            color = deserializeColor(rgb);
            if (color == null) {
                color = getInitialColumnColor(columnName);
            }

            JournalEntryColumnUI columnUI = JournalEntryColumnUI.find(columnName);
            String columnDescription;
            if (columnUI != null) {
                columnDescription = columnUI.description();
            } else {
                columnDescription = ""; //$NON-NLS-1$
            }

            journalEntryAppearanceAttributes.add(new JournalEntryAppearanceAttributes(columnName, columnDescription, color));
        }

        return journalEntryAppearanceAttributes.toArray(new JournalEntryAppearanceAttributes[journalEntryAppearanceAttributes.size()]);
    }

    public String getJournalEntryCcsid() {

        return "IBM037"; //$NON-NLS-1$
    }

    public int getMaximumNumberOfRowsToFetch() {

        int maxNumRows = preferenceStore.getInt(MAX_NUM_ROWS_TO_FETCH);
        if (maxNumRows <= 0) {
            maxNumRows = Integer.MAX_VALUE;
        }

        return maxNumRows;
    }

    public int getRetrieveJournalEntriesBufferSize() {

        return preferenceStore.getInt(BUFFER_SIZE);
    }

    public boolean isRetrieveJournalEntriesDynamicBufferSize() {

        return preferenceStore.getBoolean(DYNAMIC_BUFFER_SIZE);
    }

    public String getExportPath() {

        return preferenceStore.getString(EXPORT_PATH);
    }

    public String getExportFile() {

        return preferenceStore.getString(EXPORT_FILE);
    }

    public boolean isExportColumnHeadings() {

        return preferenceStore.getBoolean(EXPORT_COLUMN_HEADINGS);
    }

    /*
     * Preferences: SETTER
     */
    public void setHighlightUserEntries(boolean enabled) {
        preferenceStore.setValue(HIGHLIGHT_USER_ENTRIES, enabled);
    }

    public void setColoringEnabled(boolean enabled) {
        preferenceStore.setValue(ENABLED, enabled);
    }

    public void setSortedJournalEntryAppearanceAttributes(JournalEntryAppearanceAttributes[] journalEntriesAppearances) {

        for (int i = 0; i < journalEntriesAppearances.length; i++) {
            String columnName = journalEntriesAppearances[i].getColumnName();
            Color color = journalEntriesAppearances[i].getColor();
            preferenceStore.setValue(getColumnOrderKey(i), columnName);
            preferenceStore.setValue(getColorKey(columnName), serializeColor(color));
        }
    }

    public void setMaximumNumberOfRowsToFetch(int maxNumRows) {
        preferenceStore.setValue(MAX_NUM_ROWS_TO_FETCH, maxNumRows);
    }

    public void setRetrieveJournalEntriesBufferSize(int bufferSize) {
        preferenceStore.setValue(BUFFER_SIZE, bufferSize);
    }

    public void setRetrieveJournalEntriesEnableDynamicBufferSize(boolean enabled) {
        preferenceStore.setValue(DYNAMIC_BUFFER_SIZE, enabled);
    }

    public void setExportPath(String exportPath) {

        preferenceStore.setValue(EXPORT_PATH, exportPath);
    }

    public void setExportFile(String exportFile) {

        preferenceStore.setValue(EXPORT_FILE, exportFile);
    }

    public void setExportColumnHeadings(boolean export) {

        preferenceStore.setValue(EXPORT_COLUMN_HEADINGS, export);
    }

    /*
     * Others
     */

    private String getColorKey(String columnName) {
        return COLORS + columnName;
    }

    private String serializeColor(Color color) {

        if (color == null) {
            return COLOR_NULL;
        }

        return "R:" + color.getRed() + ",G:" + color.getGreen() + ",B:" + color.getBlue();
    }

    private String getColumnOrderKey(int index) {
        return COLUMNS_ORDER + index;
    }

    private String[] getSortedColumnNames() {

        Set<String> alreadyAdded = new HashSet<String>();
        List<String> sortedColumnNames = new LinkedList<String>();

        int i = 0;
        String columnName;
        do {
            columnName = preferenceStore.getString(getColumnOrderKey(i));
            if (columnName.trim().length() > 0) {
                if (!alreadyAdded.contains(columnName)) {
                    sortedColumnNames.add(columnName);
                    alreadyAdded.add(columnName);
                } else {
                    // Should not happen, but who knows?
                }
                i++;
            }
        } while (columnName.trim().length() > 0);

        return sortedColumnNames.toArray(new String[sortedColumnNames.size()]);
    }

    public Color deserializeColor(String rgb) {

        if (COLOR_NULL.equals(rgb)) {
            return null;
        }

        int red = -1;
        int green = -1;
        int blue = -1;

        String[] components = rgb.split(",");
        for (String component : components) {
            String[] color = component.split(":");
            if (color.length == 2) {
                if ("R".equalsIgnoreCase(color[0])) {
                    red = Integer.parseInt(color[1]);
                } else if ("G".equalsIgnoreCase(color[0])) {
                    green = Integer.parseInt(color[1]);
                } else if ("B".equalsIgnoreCase(color[0])) {
                    blue = Integer.parseInt(color[1]);
                }
            }
        }

        if (red < 0 || green < 0 || blue < 0) {
            return null;
        }

        return ISphereJournalExplorerCorePlugin.getDefault().getColor(new RGB(red, green, blue));
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(HIGHLIGHT_USER_ENTRIES, getInitialHighlightUserEntries());
        preferenceStore.setDefault(ENABLED, getInitialColoringEnabled());

        JournalEntryAppearanceAttributes[] sortedJournalEntryAppearanceAttributes = getInitialSortedJournalEntryAppearanceAttributes();
        for (int i = 0; i < sortedJournalEntryAppearanceAttributes.length; i++) {
            String columnName = sortedJournalEntryAppearanceAttributes[i].getColumnName();
            preferenceStore.setDefault(getColumnOrderKey(i), columnName);
            preferenceStore.setDefault(getColorKey(columnName), serializeColor(getInitialColumnColor(columnName)));
        }

        preferenceStore.setDefault(MAX_NUM_ROWS_TO_FETCH, getInitialMaximumNumberOfRowsToFetch());
        preferenceStore.setDefault(BUFFER_SIZE, getInitialRetrieveJournalEntriesBufferSize());
        preferenceStore.setDefault(DYNAMIC_BUFFER_SIZE, getInitialRetrieveJournalEntriesIsDynamicBufferSize());

        preferenceStore.setDefault(EXPORT_PATH, getInitialExportPath());
        preferenceStore.setDefault(EXPORT_FILE, getInitialExportFile());
        preferenceStore.setDefault(EXPORT_COLUMN_HEADINGS, getInitialExportColumnHeadings());
    }

    /*
     * Preferences: Default Values
     */

    private boolean getInitialHighlightUserEntries() {
        return false;
    }

    public boolean getInitialColoringEnabled() {
        return true;
    }

    public JournalEntryAppearanceAttributes[] getInitialSortedJournalEntryAppearanceAttributes() {

        List<JournalEntryAppearanceAttributes> sortedNames = new LinkedList<JournalEntryAppearanceAttributes>();

        // Entry seq#, code, type, ...
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.ID.columnName(), JournalEntryColumnUI.ID.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOENTL.columnName(), JournalEntryColumnUI.JOENTL.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOSEQN.columnName(), JournalEntryColumnUI.JOSEQN.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCODE.columnName(), JournalEntryColumnUI.JOCODE.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOENTT.columnName(), JournalEntryColumnUI.JOENTT.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JODATE.columnName(), JournalEntryColumnUI.JODATE.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOTIME.columnName(), JournalEntryColumnUI.JOTIME.description()));

        // Job, that added the journal entry ...
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOJOB.columnName(), JournalEntryColumnUI.JOJOB.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOUSER.columnName(), JournalEntryColumnUI.JOUSER.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JONBR.columnName(), JournalEntryColumnUI.JONBR.description()));
        // .. extended attributes
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOUSPF.columnName(), JournalEntryColumnUI.JOUSPF.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOTHDX.columnName(), JournalEntryColumnUI.JOTHDX.description()));

        // Program, that added the journal entry
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGMLIB.columnName(), JournalEntryColumnUI.JOPGMLIB.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGM.columnName(), JournalEntryColumnUI.JOPGM.description()));
        // .. extended attributes
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGMDEV.columnName(), JournalEntryColumnUI.JOPGMDEV.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGMASP.columnName(), JournalEntryColumnUI.JOPGMASP.description()));

        // Object that was changed
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOLIB.columnName(), JournalEntryColumnUI.JOLIB.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOOBJ.columnName(), JournalEntryColumnUI.JOOBJ.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOMBR.columnName(), JournalEntryColumnUI.JOMBR.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOOBJTYP.columnName(), JournalEntryColumnUI.JOOBJTYP.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOFILTYP.columnName(), JournalEntryColumnUI.JOFILTYP.description()));

        // System that the object resides on
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOSYNM.columnName(), JournalEntryColumnUI.JOSYNM.description()));

        // Journal entry flags
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCTRR.columnName(), JournalEntryColumnUI.JOCTRR.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOFLAG.columnName(), JournalEntryColumnUI.JOFLAG.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCCID.columnName(), JournalEntryColumnUI.JOCCID.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOJID.columnName(), JournalEntryColumnUI.JOJID.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCST.columnName(), JournalEntryColumnUI.JORCST.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOTGR.columnName(), JournalEntryColumnUI.JOTGR.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOINCDAT.columnName(), JournalEntryColumnUI.JOINCDAT.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOIGNAPY.columnName(), JournalEntryColumnUI.JOIGNAPY.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOMINESD.columnName(), JournalEntryColumnUI.JOMINESD.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOOBJIND.columnName(), JournalEntryColumnUI.JOOBJIND.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOSYSSEQ.columnName(), JournalEntryColumnUI.JOSYSSEQ.description()));

        // Journal receiver
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCV.columnName(), JournalEntryColumnUI.JORCV.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCVLIB.columnName(), JournalEntryColumnUI.JORCVLIB.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCVDEV.columnName(), JournalEntryColumnUI.JORCVDEV.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCVASP.columnName(), JournalEntryColumnUI.JORCVASP.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOARM.columnName(), JournalEntryColumnUI.JOARM.description()));

        // Remote address
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOADF.columnName(), JournalEntryColumnUI.JOADF.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORPORT.columnName(), JournalEntryColumnUI.JORPORT.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORADR.columnName(), JournalEntryColumnUI.JORADR.description()));

        // Logical unit of work
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOLUW.columnName(), JournalEntryColumnUI.JOLUW.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOXID.columnName(), JournalEntryColumnUI.JOXID.description()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCMTLVL.columnName(), JournalEntryColumnUI.JOCMTLVL.description()));

        // Null indicators
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JONVI.columnName(), JournalEntryColumnUI.JONVI.description()));

        // Entry specific data
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOESD.columnName(), JournalEntryColumnUI.JOESD.description()));

        return sortedNames.toArray(new JournalEntryAppearanceAttributes[sortedNames.size()]);
    }

    public int getInitialMaximumNumberOfRowsToFetch() {
        return 1000;
    }

    public int getInitialRetrieveJournalEntriesBufferSize() {
        return Buffer.size("128 KB");
    }

    public boolean getInitialRetrieveJournalEntriesIsDynamicBufferSize() {
        return false;
    }

    public String getInitialExportPath() {
        return FileHelper.getDefaultRootDirectory();
    }

    public String getInitialExportFile() {
        return "ExportJournalEntries";
    }

    public boolean getInitialExportColumnHeadings() {
        return true;
    }

    public String[] getRetrieveJournalEntriesBufferSizeLabels() {

        List<String> labels = new LinkedList<String>();

        labels.add("64 " + IntHelper.KILO_BYTE); //$NON-NLS-1$
        labels.add("256 " + IntHelper.KILO_BYTE); //$NON-NLS-1$
        labels.add("1 " + IntHelper.MEGA_BYTE); //$NON-NLS-1$
        labels.add("4 " + IntHelper.MEGA_BYTE); //$NON-NLS-1$
        labels.add("8 " + IntHelper.MEGA_BYTE); //$NON-NLS-1$
        labels.add("16 " + IntHelper.MEGA_BYTE); //$NON-NLS-1$

        return labels.toArray(new String[labels.size()]);
    }

    private JournalEntryAppearanceAttributes createAppearanceAttributes(String columnName, String columnDescription) {

        Color color = getInitialColumnColor(columnName);

        return new JournalEntryAppearanceAttributes(columnName, columnDescription, color);
    }

    private Color getInitialColumnColor(String columnName) {

        if (RRN_OUTPUT_FILE.equals(columnName)) {
            return COLOR_ID;
        }

        if (JOJOB.equals(columnName)) {
            return COLOR_JOB;
        } else if (JOUSER.equals(columnName)) {
            return COLOR_JOB;
        } else if (JONBR.equals(columnName)) {
            return COLOR_JOB;
        } else if (JOUSPF.equals(columnName)) {
            return COLOR_JOB;
        } else if (JOTHDX.equals(columnName)) {
            return COLOR_JOB;
        }

        if (JOLIB.equals(columnName)) {
            return COLOR_OBJECT;
        } else if (JOOBJ.equals(columnName)) {
            return COLOR_OBJECT;
        } else if (JOMBR.equals(columnName)) {
            return COLOR_OBJECT;
        }

        return getInitialColumnColor();
    }

    private Color getInitialColumnColor() {
        return ISphereJournalExplorerCorePlugin.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
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