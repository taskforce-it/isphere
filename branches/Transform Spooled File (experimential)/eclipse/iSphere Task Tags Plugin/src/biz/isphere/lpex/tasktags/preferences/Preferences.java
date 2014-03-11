package biz.isphere.lpex.tasktags.preferences;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.lpex.tasktags.ISphereLpexTasksPlugin;
import biz.isphere.lpex.tasktags.utils.StringUtils;

/**
 * Class to manage access to the preferences of the plugin.
 * 
 * @author Thomas Raddatz
 */
public final class Preferences {

    private static final String TOKEN_SEPARATOR = "|"; //$NON-NLS-1$

    /**
     * The instance of this Singleton class.
     */
    private static Preferences instance;

    /**
     * Global preferences of the LPEX Task-Tags plugin.
     */
    private static IPreferenceStore preferenceStore;

    HashSet<String> fileExtensionsSet;

    /**
     * Base configuration key:
     */
    private static final String LPEX_TASK_TAGS = "lpextasktags"; //$NON-NLS-1$

    public static final String LPEX_TASK_TAGS_ENABLED = LPEX_TASK_TAGS + ".enabled"; //$NON-NLS-1$

    public static final String LPEX_TASK_TAGS_FILE_EXTENSIONS = LPEX_TASK_TAGS + ".fileextensions"; //$NON-NLS-1$

    public static final String LPEX_IMPORT_EXPORT_LOCATION = LPEX_TASK_TAGS + ".importexportlocation"; //$NON-NLS-1$

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
            preferenceStore = ISphereLpexTasksPlugin.getDefault().getPreferenceStore();
        }
        return instance;
    }

    /*
     * Preferences: GETTER
     */

    public boolean isEnabled() {
        return preferenceStore.getBoolean(LPEX_TASK_TAGS_ENABLED);
    }

    public String[] getFileExtensions() {
        return getFileExtensions(false);
    }

    public String[] getDefaultFileExtensions() {
        String tList = getDefaultFileExtensionsAsString();
        return StringUtils.getTokens(tList, TOKEN_SEPARATOR);
    }

    public boolean supportsResource(IResource resource) {
        if (getOrCreateFileExtensionsSet().contains(resource.getFileExtension().toUpperCase())) {
            return true;
        }
        return false;
    }

    public String getImportExportLocation() {
        return preferenceStore.getString(LPEX_IMPORT_EXPORT_LOCATION);
    }

    /*
     * Preferences: SETTER
     */

    public void setEnabled(boolean anEnabledState) {
        saveEnabledState(anEnabledState);
    }

    public void setFileExtensions(String[] anExtensions) {
        saveFileExtensions(anExtensions);
    }

    public void setImportExportLocation(String aLocation) {
        saveImportExportLocation(aLocation);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {
        preferenceStore.setDefault(LPEX_TASK_TAGS_ENABLED, getDefaultEnabledState());
        preferenceStore.setDefault(LPEX_TASK_TAGS_FILE_EXTENSIONS, getDefaultFileExtensionsAsString());
        preferenceStore.setDefault(LPEX_IMPORT_EXPORT_LOCATION, getDefaultImportExportLocation());
    }

    /*
     * Preferences: Default Values
     */

    public boolean getDefaultEnabledState() {
        return true;
    }

    public String getDefaultFileExtensionsAsString() {
        return "rpg,sqlrpg,rpgle,sqlrpgle,clp,clle,cbl,cblle,c,cle,dspf,prtf,pf,lf,cmd,pnlgrp".replaceAll(",", TOKEN_SEPARATOR); //$NON-NLS-1$
    }

    public String getDefaultImportExportLocation() {
        return "";
    }

    /*
     * Preferences: Save Values
     */

    private void saveEnabledState(boolean anEnabledState) {
        preferenceStore.setValue(LPEX_TASK_TAGS_ENABLED, anEnabledState);
    }

    private void saveFileExtensions(String[] anExtensions) {
        preferenceStore.setValue(LPEX_TASK_TAGS_FILE_EXTENSIONS, StringUtils.concatTokens(anExtensions, TOKEN_SEPARATOR));
        fileExtensionsSet = null;
    }

    private void saveImportExportLocation(String aLocation) {
        if (new File(aLocation).exists()) {
            preferenceStore.setValue(LPEX_IMPORT_EXPORT_LOCATION, aLocation);
        }
    }

    /*
     * Others.
     */

    private HashSet<String> getOrCreateFileExtensionsSet() {
        if (fileExtensionsSet == null) {
            fileExtensionsSet = new HashSet<String>(Arrays.asList(getFileExtensions(true)));
        }
        return fileExtensionsSet;
    }

    private String[] getFileExtensions(boolean anUpperCase) {
        String tList = preferenceStore.getString(LPEX_TASK_TAGS_FILE_EXTENSIONS);
        if (anUpperCase) {
            return StringUtils.getTokens(tList.toUpperCase(), TOKEN_SEPARATOR);
        } else {
            return StringUtils.getTokens(tList, TOKEN_SEPARATOR);
        }
    }
}