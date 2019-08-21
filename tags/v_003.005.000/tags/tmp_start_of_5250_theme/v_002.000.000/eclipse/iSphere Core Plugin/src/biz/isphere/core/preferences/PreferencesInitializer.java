package biz.isphere.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import biz.isphere.core.versionupdate.PreferencesUpdater;

/**
 * Class used to initialize default preference values.
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
     * initializeDefaultPreferences()
     */
    @Override
    public void initializeDefaultPreferences() {
        Preferences.getInstance().initializeDefaultPreferences();
        PreferencesUpdater.update();
    }

}
