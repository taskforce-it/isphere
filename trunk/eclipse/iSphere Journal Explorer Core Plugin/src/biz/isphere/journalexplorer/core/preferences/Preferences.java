package biz.isphere.journalexplorer.core.preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.internal.DateTimeHelper;
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

    public static final String LOAD_JOURNAL_ENTRIES = DOMAIN + "LOAD_JOURNAL_ENTRIES."; //$NON-NLS-1$

    public static final String STARTING_DATE = LOAD_JOURNAL_ENTRIES + "STARTING_DATE"; //$NON-NLS-1$

    public static final String ENDING_DATE = LOAD_JOURNAL_ENTRIES + "ENDING_DATE"; //$NON-NLS-1$

    public static final String RECORD_ENTRIES_ONLY = LOAD_JOURNAL_ENTRIES + "RECORD_ENTRIES_ONLY"; //$NON-NLS-1$

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

            journalEntryAppearanceAttributes.add(new JournalEntryAppearanceAttributes(columnName, color));
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

    public Calendar getStartingDate() {

        String date = preferenceStore.getString(STARTING_DATE);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormatter.parse(date));
            return calendar;
        } catch (ParseException e) {
            return getInitialStartingDate();
        }
    }

    public Calendar getEndingDate() {

        String date = preferenceStore.getString(ENDING_DATE);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormatter.parse(date));
            return calendar;
        } catch (ParseException e) {
            return getInitialEndingDate();
        }
    }

    public boolean isRecordsOnly() {

        return preferenceStore.getBoolean(RECORD_ENTRIES_ONLY);
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

    public void setStartingDate(Calendar calendar) {

        String dateString = dateFormatter.format(calendar.getTime());

        preferenceStore.setValue(STARTING_DATE, dateString);
    }

    public void setEndingDate(Calendar calendar) {

        String dateString = dateFormatter.format(calendar.getTime());

        preferenceStore.setValue(ENDING_DATE, dateString);
    }

    public void setRecordsOnly(boolean recordsOnly) {

        preferenceStore.setValue(RECORD_ENTRIES_ONLY, recordsOnly);
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

        preferenceStore.setDefault(STARTING_DATE, dateFormatter.format(getInitialStartingDate()));
        preferenceStore.setDefault(ENDING_DATE, dateFormatter.format(getInitialEndingDate()));
        preferenceStore.setDefault(RECORD_ENTRIES_ONLY, getInitialRecordsOnly());
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
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.ID.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOENTL.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOSEQN.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCODE.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOENTT.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JODATE.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOTIME.name()));

        // Job, that added the journal entry ...
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOJOB.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOUSER.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JONBR.name()));
        // .. extended attributes
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOUSPF.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOTHDX.name()));

        // Program, that added the journal entry
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGMLIB.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGM.name()));
        // .. extended attributes
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGMDEV.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOPGMASP.name()));

        // Object that was changed
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOLIB.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOOBJ.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOMBR.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOOBJTYP.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOFILTYP.name()));

        // System that the object resides on
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOSYNM.name()));

        // Journal entry flags
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCTRR.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOFLAG.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCCID.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOJID.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCST.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOTGR.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOINCDAT.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOIGNAPY.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOMINESD.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOOBJIND.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOSYSSEQ.name()));

        // Journal receiver
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCV.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCVLIB.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCVDEV.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORCVASP.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOARM.name()));

        // Remote address
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOADF.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORPORT.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JORADR.name()));

        // Logical unit of work
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOLUW.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOXID.name()));
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOCMTLVL.name()));

        // Entry specific data
        sortedNames.add(createAppearanceAttributes(JournalEntryColumnUI.JOESD.name()));

        return sortedNames.toArray(new JournalEntryAppearanceAttributes[sortedNames.size()]);
    }

    public int getInitialMaximumNumberOfRowsToFetch() {
        return 1000;
    }

    public int getInitialRetrieveJournalEntriesBufferSize() {
        return 128 * 1024; // 128 KB
    }

    public Calendar getInitialStartingDate() {
        return DateTimeHelper.getStartOfDay();
    }

    public Calendar getInitialEndingDate() {
        return DateTimeHelper.getEndOfDay();
    }

    public boolean getInitialRecordsOnly() {
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

    private JournalEntryAppearanceAttributes createAppearanceAttributes(String columnName) {

        Color color = getInitialColumnColor(columnName);

        return new JournalEntryAppearanceAttributes(columnName, color);
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