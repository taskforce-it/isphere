/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferencepages.IPreferences;

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

    private static final String DOMAIN = ISpherePlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String SPOOLED_FILES_SAVE_DIRECTORY = DOMAIN + "SPOOLED_FILES.SAVE.DIRECTORY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_DEFAULT_FORMAT = DOMAIN + "SPOOLED_FILES.DEFAULT_FORMAT"; //$NON-NLS-1$

    private static final String SOURCEFILESEARCH_SEARCHSTRING = DOMAIN + "SOURCEFILESEARCH.SEARCHSTRING"; //$NON-NLS-1$

    private static final String MESSAGEFILESEARCH_SEARCHSTRING = DOMAIN + "MESSAGEFILESEARCH.SEARCHSTRING"; //$NON-NLS-1$

    private static final String ISPHERE_LIBRARY = DOMAIN + "LIBRARY"; //$NON-NLS-1$

    private static final String HOST_NAME = DOMAIN + "HOST_NAME"; //$NON-NLS-1$

    private static final String FTP_PORT_NUMBER = DOMAIN + "FTP_PORT_NUMBER"; //$NON-NLS-1$

    private static final String SEARCH_FOR_UPDATES = DOMAIN + "SEARCH_FOR_UPDATES"; //$NON-NLS-1$

    private static final String SEARCH_FOR_BETA_VERSIONS = DOMAIN + "SEARCH_FOR_BETA_VERSIONS"; //$NON-NLS-1$

    private static final String URL_FOR_UPDATES = DOMAIN + "URL_FOR_UPDATES"; //$NON-NLS-1$

    private static final String LAST_VERSION_FOR_UPDATES = DOMAIN + "LAST_VERSION_FOR_UPDATES"; //$NON-NLS-1$

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
            instance.preferenceStore = ISpherePlugin.getDefault().getPreferenceStore();
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

    public String getISphereLibrary() {
        return preferenceStore.getString(ISPHERE_LIBRARY);
    }

    public String getHostName() {
        return preferenceStore.getString(HOST_NAME);
    }

    public int getFtpPortNumber() {
        return preferenceStore.getInt(FTP_PORT_NUMBER);
    }

    public boolean isSearchForUpdates() {
        return preferenceStore.getBoolean(SEARCH_FOR_UPDATES);
    }

    public boolean isSearchForBetaVersions() {
        return preferenceStore.getBoolean(SEARCH_FOR_BETA_VERSIONS);
    }

    public String getURLForUpdates() {
        return preferenceStore.getString(URL_FOR_UPDATES);
    }

    public String getLastVersionForUpdates() {
        return preferenceStore.getString(LAST_VERSION_FOR_UPDATES);
    }

    public String getMessageFileSearchString() {
        return preferenceStore.getString(MESSAGEFILESEARCH_SEARCHSTRING);
    }

    public String getSourceFileSearchString() {
        return preferenceStore.getString(SOURCEFILESEARCH_SEARCHSTRING);
    }

    public String getSpooledFileConversionDefaultFormat() {
        return preferenceStore.getString(SPOOLED_FILES_DEFAULT_FORMAT);
    }

    public String getSpooledFileConversionText() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_TEXT);
    }

    public String getSpooledFileConversionTextLibrary() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_TEXT_LIBRARY);
    }

    public String getSpooledFileConversionTextCommand() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_TEXT_COMMAND);
    }

    public String getSpooledFileConversionHTML() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_HTML);
    }

    public String getSpooledFileConversionHTMLLibrary() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_HTML_LIBRARY);
    }

    public String getSpooledFileConversionHTMLCommand() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_HTML_COMMAND);
    }

    public String getSpooledFileConversionPDF() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_PDF);
    }

    public String getSpooledFileConversionPDFLibrary() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_PDF_LIBRARY);
    }

    public String getSpooledFileConversionPDFCommand() {
        return preferenceStore.getString(SPOOLED_FILES_CONVERSION_PDF_COMMAND);
    }

    public String getSpooledFileSaveDirectory() {
        String directory = preferenceStore.getString(SPOOLED_FILES_SAVE_DIRECTORY);
        if (StringHelper.isNullOrEmpty(directory)) {
            return "C:\\"; //$NON-NLS-1$
        }
        return directory;
    }

    /*
     * Preferences: SETTER
     */

    public void setISphereLibrary(String aLibrary) {
        preferenceStore.setValue(ISPHERE_LIBRARY, aLibrary.trim());
    }

    public void setHostName(String aHostName) {
        preferenceStore.setValue(HOST_NAME, aHostName);
    }

    public void setFtpPortNumber(int aPortNumber) {
        preferenceStore.setValue(FTP_PORT_NUMBER, aPortNumber);
    }

    public void setSearchForUpdates(boolean aSearchForUpdates) {
        preferenceStore.setValue(SEARCH_FOR_UPDATES, aSearchForUpdates);
    }

    public void setSearchForBetaVersions(boolean aSearchForUpdates) {
        preferenceStore.setValue(SEARCH_FOR_BETA_VERSIONS, aSearchForUpdates);
    }

    public void setURLForUpdates(String aURLForUpdates) {
        preferenceStore.setValue(URL_FOR_UPDATES, aURLForUpdates);
    }

    public void setLastVersionForUpdates(String aLastVersionForUpdates) {
        preferenceStore.setValue(LAST_VERSION_FOR_UPDATES, aLastVersionForUpdates);
    }

    public void setMessageFileSearchString(String aSearchString) {
        preferenceStore.setValue(MESSAGEFILESEARCH_SEARCHSTRING, aSearchString.trim());
    }

    public void setSourceFileSearchString(String aSearchString) {
        preferenceStore.setValue(SOURCEFILESEARCH_SEARCHSTRING, aSearchString.trim());
    }

    public void setSpooledFileDefaultFormat(String aFormat) {
        preferenceStore.setValue(SPOOLED_FILES_DEFAULT_FORMAT, aFormat);
    }

    public void setSpooledFileConversionText(String aConversionType) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_TEXT, aConversionType);
    }

    public void setSpooledFileConversionLibraryText(String aLibrary) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_TEXT_LIBRARY, aLibrary);
    }

    public void setSpooledFileConversionCommandText(String aCommand) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_TEXT_COMMAND, aCommand);
    }

    public void setSpooledFileConversionHTML(String aConversionType) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_HTML, aConversionType);
    }

    public void setSpooledFileConversionLibraryHTML(String aLibrary) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_HTML_LIBRARY, aLibrary);
    }

    public void setSpooledFileConversionCommandHTML(String aCommand) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_HTML_COMMAND, aCommand);
    }

    public void setSpooledFileConversionPDF(String aConversionType) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_PDF, aConversionType);
    }

    public void setSpooledFileConversionLibraryPDF(String aLibrary) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_PDF_LIBRARY, aLibrary);
    }

    public void setSpooledFileConversionCommandPDF(String aCommand) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_PDF_COMMAND, aCommand);
    }

    public void setSpooledFileSaveDirectory(String aDirectory) {
        preferenceStore.setValue(SPOOLED_FILES_SAVE_DIRECTORY, aDirectory);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {
        preferenceStore.setDefault(ISPHERE_LIBRARY, getDefaultISphereLibrary());
        preferenceStore.setDefault(HOST_NAME, getDefaultHostName());
        preferenceStore.setDefault(FTP_PORT_NUMBER, getDefaultFtpPortNumber());

        preferenceStore.setDefault(SEARCH_FOR_UPDATES, getDefaultSearchForUpdates());
        preferenceStore.setDefault(SEARCH_FOR_BETA_VERSIONS, getDefaultSearchForBetaVersions());
        preferenceStore.setDefault(URL_FOR_UPDATES, getDefaultURLForUpdates());
        preferenceStore.setDefault(LAST_VERSION_FOR_UPDATES, getDefaultLastVersionForUpdates());

        preferenceStore.setDefault(SPOOLED_FILES_DEFAULT_FORMAT, getDefaultSpooledFileConversionDefaultFormat());

        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT, getDefaultSpooledFileConversionText());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT_COMMAND, getDefaultSpooledFileConversionTextCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT_LIBRARY, getDefaultSpooledFileConversionTextLibrary());

        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML, getDefaultSpooledFileConversionHTML());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML_COMMAND, getDefaultSpooledFileConversionHTMLCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML_LIBRARY, getDefaultSpooledFileConversionHTMLLibrary());

        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF, getDefaultSpooledFileConversionPDF());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF_COMMAND, getDefaultSpooledFileConversionPDFCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF_LIBRARY, getDefaultSpooledFileConversionPDFLibrary());
    }

    /*
     * Preferences: Default Values
     */

    /**
     * Returns the default iSphere library name.
     * 
     * @return default iSphere library name
     */
    public String getDefaultISphereLibrary() {
        return "ISPHERE";
    }

    /**
     * Returns the default host name where to upload the iSphere library.
     * 
     * @return default host name
     */
    public String getDefaultHostName() {
        return "";
    }

    /**
     * Returns the default FTP port number.
     * 
     * @return default FTPport number
     */
    public int getDefaultFtpPortNumber() {
        return 21;
    }

    /**
     * Returns the default search for updates flag.
     * 
     * @return default search for updates flag.
     */
    public boolean getDefaultSearchForUpdates() {
        return true;
    }

    /**
     * Returns the default 'search for beta versions' flag.
     * 
     * @return default 'search for beta versions' flag.
     */
    public boolean getDefaultSearchForBetaVersions() {
        return false;
    }

    /**
     * Returns the default URL for updates.
     * 
     * @return default URL for updates.
     */
    public String getDefaultURLForUpdates() {
        return "http://sourceforge.net/p/isphere/code/HEAD/tree/trunk/build/iSphere%20Notifier/MANIFEST.MF?format=raw";
    }

    /**
     * Returns the default last version for updates.
     * 
     * @return default last version for updates.
     */
    public String getDefaultLastVersionForUpdates() {
        return "000000";
    }

    /**
     * Returns the default format for spooled file conversion on double-click on
     * a spooled file.
     * 
     * @return default format on spooled file double-click
     */
    public String getDefaultSpooledFileConversionDefaultFormat() {
        return IPreferences.OUTPUT_FORMAT_TEXT;
    }

    /**
     * Returns the default conversion type for spooled file to text conversion.
     * 
     * @return default spooled file to TEXT conversion type
     */
    public String getDefaultSpooledFileConversionText() {
        return IPreferences.SPLF_CONVERSION_TRANSFORM;
    }

    /**
     * Return the default library name for user defined spooled file to text
     * conversion.
     * 
     * @return library name for user defined spooled file to text conversion
     */
    public String getDefaultSpooledFileConversionTextLibrary() {
        return "";
    }

    /**
     * Return the default command for user defined spooled file to text
     * conversion.
     * 
     * @return command for user defined spooled file to text conversion
     */
    public String getDefaultSpooledFileConversionTextCommand() {
        return "";
    }

    /**
     * Returns the default conversion type for spooled file to HTML conversion.
     * 
     * @return default spooled file to HTML conversion type
     */
    public String getDefaultSpooledFileConversionHTML() {
        return IPreferences.SPLF_CONVERSION_TRANSFORM;
    }

    /**
     * Return the default library name for user defined spooled file to HTML
     * conversion.
     * 
     * @return library name for user defined spooled file to HTML conversion
     */
    public String getDefaultSpooledFileConversionHTMLLibrary() {
        return "";
    }

    /**
     * Return the default command for user defined spooled file to HTML
     * conversion.
     * 
     * @return command for user defined spooled file to HTML conversion
     */
    public String getDefaultSpooledFileConversionHTMLCommand() {
        return "";
    }

    /**
     * Returns the default conversion type for spooled file to PDF conversion.
     * 
     * @return default spooled file to PDF conversion type
     */
    public String getDefaultSpooledFileConversionPDF() {
        return IPreferences.SPLF_CONVERSION_TRANSFORM;
    }

    /**
     * Return the default library name for user defined spooled file to PDF
     * conversion.
     * 
     * @return library name for user defined spooled file to PDF conversion
     */
    public String getDefaultSpooledFileConversionPDFLibrary() {
        return "";
    }

    /**
     * Return the default command for user defined spooled file to PDF
     * conversion.
     * 
     * @return command for user defined spooled file to PDF conversion
     */
    public String getDefaultSpooledFileConversionPDFCommand() {
        return "";
    }
}