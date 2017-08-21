package biz.isphere.journalexplorer.core.preferences;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryAppearanceAttributes;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
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

    public Map<String, JournalEntryAppearanceAttributes> getJournalEntriesAppearances() {

        Map<String, JournalEntryAppearanceAttributes> colors = new HashMap<String, JournalEntryAppearanceAttributes>();

        for (String columnName : ColumnsDAO.ALL) {

            Color color = null;
            String rgb = preferenceStore.getString(getColorKey(columnName));

            if (!StringHelper.isNullOrEmpty(rgb)) {
                color = deserializeColor(rgb);
            }

            if (color == null) {
                color = getInitialColumnColor(columnName);
            }

            colors.put(columnName, new JournalEntryAppearanceAttributes(columnName, color));
        }

        return colors;
    }

    public String[] getJournalEntryColumnsOrder() {

        List<String> sortedColumnNames = new LinkedList<String>();

        int i = 0;
        String columnName;
        do {
            columnName = preferenceStore.getString(getColumnOrderKey(i));
            if (columnName.trim().length() > 0) {
                sortedColumnNames.add(columnName);
                i++;
            }
        } while (columnName.trim().length() > 0);

        return sortedColumnNames.toArray(new String[sortedColumnNames.size()]);
    }

    /*
     * Preferences: SETTER
     */

    public void setJounalEntriesAppearances(JournalEntryColumn[] journalEntries) {

        for (JournalEntryColumn journalEntryColumn : journalEntries) {
            setJounalEntryAppearance(new JournalEntryAppearanceAttributes(journalEntryColumn.getName(), journalEntryColumn.getColor()));
        }
    }

    public void setJounalEntryAppearance(JournalEntryAppearanceAttributes appearance) {

        String columnName = appearance.getColumnName();
        Color color;

        if (appearance.getColor() != null) {
            color = appearance.getColor();
        } else {
            color = getInitialColumnColor();
        }

        preferenceStore.setValue(getColorKey(columnName), serializeColor(color));
    }

    public void setJournalEntriesAppearances(Map<String, JournalEntryAppearanceAttributes> colors) {

        for (String columnName : ColumnsDAO.ALL) {
            JournalEntryAppearanceAttributes color = colors.get(columnName);
            setJounalEntryAppearance(color);
        }
    }

    public void setHighlightUserEntries(boolean enabled) {
        preferenceStore.setValue(HIGHLIGHT_USER_ENTRIES, enabled);
    }

    public void setColoringEnabled(boolean enabled) {
        preferenceStore.setValue(ENABLED, enabled);
    }

    public void setJournalEntryColumnsOrder(String[] sortedColumnNames) {

        for (int i = 0; i < sortedColumnNames.length; i++) {
            preferenceStore.setValue(getColumnOrderKey(i), sortedColumnNames[i]);
        }
    }

    /*
     * Others
     */

    private String getColorKey(String columnName) {
        return COLORS + columnName;
    }

    private String serializeColor(Color color) {
        return "R:" + color.getRed() + ",G:" + color.getGreen() + ",B:" + color.getBlue();
    }

    private String getColumnOrderKey(int index) {
        return COLUMNS_ORDER + index;
    }

    public Color deserializeColor(String rgb) {

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

        for (String columnName : ColumnsDAO.ALL) {
            preferenceStore.setDefault(getColorKey(columnName), serializeColor(getInitialColumnColor(columnName)));
        }

        String[] sortedColumnNames = getInitialJournalEntryColumnsOrder();
        for (int i = 0; i < sortedColumnNames.length; i++) {
            preferenceStore.setDefault(getColumnOrderKey(i), sortedColumnNames[i]);
        }
    }

    /*
     * Preferences: Default Values
     */

    public boolean getInitialHighlightUserEntries() {
        return false;
    }

    public boolean getInitialColoringEnabled() {
        return true;
    }

    public Color getInitialColumnColor(String columnName) {

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

        return ISphereJournalExplorerCorePlugin.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    }

    public Color getInitialColumnColor() {
        return ISphereJournalExplorerCorePlugin.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    }

    private String[] getInitialJournalEntryColumnsOrder() {

        List<String> sortedNames = new LinkedList<String>();

        // Entry seq#, code, type, ...
        sortedNames.add(JournalEntryColumnUI.ID.name());
        sortedNames.add(JournalEntryColumnUI.JOENTL.name());
        sortedNames.add(JournalEntryColumnUI.JOSEQN.name());
        sortedNames.add(JournalEntryColumnUI.JOCODE.name());
        sortedNames.add(JournalEntryColumnUI.JOENTT.name());
        sortedNames.add(JournalEntryColumnUI.JODATE.name());
        sortedNames.add(JournalEntryColumnUI.JOTIME.name());

        // Job, that added the journal entry ...
        sortedNames.add(JournalEntryColumnUI.JOJOB.name());
        sortedNames.add(JournalEntryColumnUI.JOUSER.name());
        sortedNames.add(JournalEntryColumnUI.JONBR.name());
        // .. extended attributes
        sortedNames.add(JournalEntryColumnUI.JOUSPF.name());
        sortedNames.add(JournalEntryColumnUI.JOTHDX.name());

        // Program, that added the journal entry
        sortedNames.add(JournalEntryColumnUI.JOPGMLIB.name());
        sortedNames.add(JournalEntryColumnUI.JOPGM.name());
        // .. extended attributes
        sortedNames.add(JournalEntryColumnUI.JOPGMDEV.name());
        sortedNames.add(JournalEntryColumnUI.JOPGMASP.name());

        // Object that was changed
        sortedNames.add(JournalEntryColumnUI.JOLIB.name());
        sortedNames.add(JournalEntryColumnUI.JOOBJ.name());
        sortedNames.add(JournalEntryColumnUI.JOMBR.name());
        sortedNames.add(JournalEntryColumnUI.JOOBJTYP.name());
        sortedNames.add(JournalEntryColumnUI.JOFILTYP.name());

        // System that the object resides on
        sortedNames.add(JournalEntryColumnUI.JOSYNM.name());

        // Journal entry flags
        sortedNames.add(JournalEntryColumnUI.JOCTRR.name());
        sortedNames.add(JournalEntryColumnUI.JOFLAG.name());
        sortedNames.add(JournalEntryColumnUI.JOCCID.name());
        sortedNames.add(JournalEntryColumnUI.JOJID.name());
        sortedNames.add(JournalEntryColumnUI.JORCST.name());
        sortedNames.add(JournalEntryColumnUI.JOTGR.name());
        sortedNames.add(JournalEntryColumnUI.JOINCDAT.name());
        sortedNames.add(JournalEntryColumnUI.JOIGNAPY.name());
        sortedNames.add(JournalEntryColumnUI.JOMINESD.name());
        sortedNames.add(JournalEntryColumnUI.JOOBJIND.name());
        sortedNames.add(JournalEntryColumnUI.JOSYSSEQ.name());

        // Journal receiver
        sortedNames.add(JournalEntryColumnUI.JORCV.name());
        sortedNames.add(JournalEntryColumnUI.JORCVLIB.name());
        sortedNames.add(JournalEntryColumnUI.JORCVDEV.name());
        sortedNames.add(JournalEntryColumnUI.JORCVASP.name());
        sortedNames.add(JournalEntryColumnUI.JOARM.name());

        // Remote address
        sortedNames.add(JournalEntryColumnUI.JOADF.name());
        sortedNames.add(JournalEntryColumnUI.JORPORT.name());
        sortedNames.add(JournalEntryColumnUI.JORADR.name());

        // Logical unit of work
        sortedNames.add(JournalEntryColumnUI.JOLUW.name());
        sortedNames.add(JournalEntryColumnUI.JOXID.name());
        sortedNames.add(JournalEntryColumnUI.JOCMTLVL.name());

        // Entry specific data
        sortedNames.add(JournalEntryColumnUI.JOESD.name());

        return sortedNames.toArray(new String[sortedNames.size()]);
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