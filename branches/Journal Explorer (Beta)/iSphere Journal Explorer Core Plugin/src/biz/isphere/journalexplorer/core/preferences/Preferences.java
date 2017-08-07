package biz.isphere.journalexplorer.core.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;

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

    private static final String DOMAIN = ISphereJournalExplorerCorePlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String HIGHLIGHT_USER_ENTRIES = DOMAIN + "HIGHLIGHT_USER_ENTRIES."; //$NON-NLS-1$

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

    /*
     * Preferences: SETTER
     */

    public void setHighlightUserEntries(boolean enabled) {
        preferenceStore.setValue(HIGHLIGHT_USER_ENTRIES, enabled);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(HIGHLIGHT_USER_ENTRIES, getInitialHighlightUserEntries());
    }

    /*
     * Preferences: Default Values
     */

    public boolean getInitialHighlightUserEntries() {
        return false;
    }
}