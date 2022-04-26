/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.FastDateFormat;
import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.action.MessageLengthAction;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.sourcefilesearch.SearchResultManager;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileTransformerPDF.PageSize;

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

    /*
     * Preferences keys:
     */

    private static final String DOMAIN = ISpherePlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String WARNING_BASE_KEY = DOMAIN + "SHOW_WARNING."; //$NON-NLS-1$

    private static final String SPOOLED_FILES_LOAD_ASYNCHRONOUSLY = DOMAIN + "SPOOLED_FILES.LOAD.ASYNCHRONOUSLY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_SUGGESTED_FILE_NAME = DOMAIN + "SPOOLED_FILES.SUGGESTED.FILE_NAME"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_SAVE_DIRECTORY = DOMAIN + "SPOOLED_FILES.SAVE.DIRECTORY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_PDF = DOMAIN + "SPOOLED_FILES.CONVERSION_PDF"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_HTML_EDIT_ALLOWED = DOMAIN + "SPOOLED_FILES.CONVERSION_HTML.EDIT_ALLOWED"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT_COMMAND = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT.COMMAND"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT_LIBRARY = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT.LIBRARY"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_CONVERSION_TEXT_EDIT_ALLOWED = DOMAIN + "SPOOLED_FILES.CONVERSION_TEXT.EDIT_ALLOWED"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_PAGE_SIZE = DOMAIN + "SPOOLED_FILES.PAGE_SIZE"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_ADJUST_FONT_SIZE = DOMAIN + "SPOOLED_FILES.ADJUST_FONT_SIZE"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_DEFAULT_FORMAT = DOMAIN + "SPOOLED_FILES.DEFAULT_FORMAT"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_MAX_FILES_TO_LOAD = DOMAIN + "MAX_FILES_TO_LOAD"; //$NON-NLS-1$

    private static final String SPOOLED_FILES_RSE_DESCRIPTION = DOMAIN + "RSE_DESCRIPTION"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_MEMBER_COLUMN_WIDTH = DOMAIN + "SOURCEFILESEARCH.MEMBER_COLUMN_WIDTH"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_SRC_TYPE_COLUMN_WIDTH = DOMAIN + "SOURCEFILESEARCH.SRC_TYPE_COLUMN_WIDTH"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_LAST_CHANGED_DATE_COLUMN_WIDTH = DOMAIN + "SOURCEFILESEARCH.LAST_CHANGED_DATE_COLUMN_WIDTH"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_STATEMENTS_COUNT_COLUMN_WIDTH = DOMAIN + "SOURCEFILESEARCH.STATEMENTS_COUNT_COLUMN_WIDTH"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_SEARCHSTRING = DOMAIN + "SOURCEFILESEARCH.SEARCHSTRING"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_EXPORT_DIRECTORY = DOMAIN + "SOURCEFILESEARCH.EXPORT_DIRECTORY"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_SEARCHSTRING = DOMAIN + "MESSAGEFILESEARCH.SEARCHSTRING"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_EXPORT_DIRECTORY = DOMAIN + "MESSAGEFILESEARCH.EXPORT_DIRECTORY"; //$NON-NLS-1$

    private static final String ISPHERE_LIBRARY = DOMAIN + "LIBRARY"; //$NON-NLS-1$

    private static final String ASP_GROUP = "ASP_GROUP"; //$NON-NLS-1$

    private static final String HOST_NAME = DOMAIN + "HOST_NAME"; //$NON-NLS-1$

    private static final String FTP_PORT_NUMBER = DOMAIN + "FTP_PORT_NUMBER"; //$NON-NLS-1$

    private static final String SYSTEM_CCSID = DOMAIN + "SYSTEM_CCSID"; //$NON-NLS-1$

    private static final String SEARCH_FOR_UPDATES = DOMAIN + "SEARCH_FOR_UPDATES"; //$NON-NLS-1$

    private static final String SEARCH_FOR_BETA_VERSIONS = DOMAIN + "SEARCH_FOR_BETA_VERSIONS"; //$NON-NLS-1$

    private static final String URL_FOR_UPDATES = DOMAIN + "URL_FOR_UPDATES"; //$NON-NLS-1$

    private static final String LAST_VERSION_FOR_UPDATES = DOMAIN + "LAST_VERSION_FOR_UPDATES"; //$NON-NLS-1$

    private static final String MONITOR = DOMAIN + "MONITOR."; //$NON-NLS-1$

    private static final String MONITOR_DTAQ = MONITOR + "DTAQ."; //$NON-NLS-1$

    public static final String MONITOR_DTAQ_LENGTH = MONITOR_DTAQ + "LENGTH"; //$NON-NLS-1$

    public static final String MONITOR_DTAQ_VIEW_IN_HEX = MONITOR_DTAQ + "VIEW_IN_HEX"; //$NON-NLS-1$

    public static final String MONITOR_DTAQ_DISPLAY_END_OF_DATA = MONITOR_DTAQ + "DISPLAY_END_OF_DATA"; //$NON-NLS-1$

    public static final String MONITOR_DTAQ_REPLACEMENT_CHARACTER = MONITOR_DTAQ + "REPLACEMENT_CHARACTER"; //$NON-NLS-1$

    public static final String MONITOR_DTAQ_NUMBER_OF_MESSAGES = MONITOR_DTAQ + "NUMBER_OF_MESSAGES"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_RESULTS = DOMAIN + "SOURCE_FILE_SEARCH_RESULTS."; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_RESULTS_IS_EDIT_ENABLED = SOURCE_FILE_SEARCH_RESULTS + "IS_EDIT_ENABLED"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY = SOURCE_FILE_SEARCH_RESULTS + "SAVE_DIRECTORY"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME = SOURCE_FILE_SEARCH_RESULTS + "LAST_USED_FILE_NAME"; //$NON-NLS-1$

    public static final String SOURCE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED = SOURCE_FILE_SEARCH_RESULTS + "IS_AUTO_SAVE_ENABLED"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE = SOURCE_FILE_SEARCH_RESULTS + "AUTO_SAVE_FILE"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_DIRECTORY = "sourceFileSearch"; //$NON-NLS-1$

    private static final String SOURCE_FILE_SEARCH_FILE_NAME = "SourceFileSearchResult"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_RESULTS = DOMAIN + "MESSAGE_FILE_SEARCH_RESULTS."; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY = MESSAGE_FILE_SEARCH_RESULTS + "SAVE_DIRECTORY"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME = MESSAGE_FILE_SEARCH_RESULTS + "LAST_USED_FILE_NAME"; //$NON-NLS-1$

    public static final String MESSAGE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED = MESSAGE_FILE_SEARCH_RESULTS + "IS_AUTO_SAVE_ENABLED"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE = MESSAGE_FILE_SEARCH_RESULTS + "AUTO_SAVE_FILE"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_DIRECTORY = "messageFileSearch"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_SEARCH_FILE_NAME = "MessageFileSearchResult"; //$NON-NLS-1$

    private static final String MESSAGE_FILE_COMPARE = DOMAIN + "MESSAGE_FILE_COMPARE."; //$NON-NLS-1$

    private static final String MESSAGE_FILE_COMPARE_LINE_WIDTH = MESSAGE_FILE_COMPARE + "LINE_WIDTH"; //$NON-NLS-1$

    private static final String SOURCE_MEMBER_COMPARE = DOMAIN + "SOURCE_MEMBER_COMPARE."; //$NON-NLS-1$

    private static final String SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_RIGHT_MEMBER = SOURCE_MEMBER_COMPARE + "LOAD_PREVIOUS_VALUES_RIGHT_MEMBER"; //$NON-NLS-1$

    private static final String SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_ANCESTOR_MEMBER = SOURCE_MEMBER_COMPARE
        + "LOAD_PREVIOUS_VALUES_ANCESTOR_MEMBER"; //$NON-NLS-1$

    private static final String APPEARANCE = DOMAIN + "APPEARANCE."; //$NON-NLS-1$

    private static final String APPEARANCE_DATE_FORMAT = APPEARANCE + "DATE_FORMAT"; //$NON-NLS-1$

    private static final String APPEARANCE_TIME_FORMAT = APPEARANCE + "TIME_FORMAT"; //$NON-NLS-1$

    private static final String APPEARANCE_FORMAT_RESOURCE_DATES = APPEARANCE + "FORMAT_RESOURCE_DATES"; //$NON-NLS-1$

    private static final String APPEARANCE_DATE_FORMAT_LOCALE = "*LOCALE"; //$NON-NLS-1$

    private static final String APPEARANCE_TIME_FORMAT_LOCALE = "*LOCALE"; //$NON-NLS-1$

    private static final String APPEARANCE_AUTO_REFRESH = APPEARANCE + "AUTO_REFRESH."; //$NON-NLS-1$

    private static final String APPEARANCE_AUTO_REFRESH_DELAY = APPEARANCE_AUTO_REFRESH + "DELAY"; //$NON-NLS-1$

    private static final String APPEARANCE_AUTO_REFRESH_THRESHOLD = APPEARANCE_AUTO_REFRESH + "THRESHOLD"; //$NON-NLS-1$

    private static final String APPEARANCE_SHOW_ERROR_LOG = APPEARANCE_AUTO_REFRESH + "APPEARANCE_SHOW_ERROR_LOG"; //$NON-NLS-1$

    private static final String DECORATION = DOMAIN + "DECORATION."; //$NON-NLS-1$

    private static final String DECORATION_OBJECT_EXTENSION = APPEARANCE + "OBJECT_EXTENSION"; //$NON-NLS-1$

    private static final String DECORATION_SOURCE_MEMBER_EXTENSION = APPEARANCE + "SOURCE_MEMBER_EXTENSION"; //$NON-NLS-1$

    private static final String DECORATION_DATA_MEMBER_EXTENSION = APPEARANCE + "DATA_MEMBER_EXTENSION"; //$NON-NLS-1$

    private static final String JDBC = DOMAIN + "JDBC."; //$NON-NLS-1$

    private static final String JDBC_USE_ISPHERE_MANAGER = "USE_ISPHERE_MANAGER"; //$NON-NLS-1$

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

    public boolean isShowWarningMessage(String showWarningKey) {
        String key = getShowWarningKey(showWarningKey);
        if (!preferenceStore.contains(key)) {
            return true;
        }
        return preferenceStore.getBoolean(key);
    }

    public String getISphereLibrary() {
        return preferenceStore.getString(ISPHERE_LIBRARY);
    }

    public String getASPGroup() {
        return preferenceStore.getString(ASP_GROUP);
    }

    public String getHostName() {
        return preferenceStore.getString(HOST_NAME);
    }

    public int getFtpPortNumber() {
        return preferenceStore.getInt(FTP_PORT_NUMBER);
    }

    public int getSystemCcsid() {
        return preferenceStore.getInt(SYSTEM_CCSID);
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
        return preferenceStore.getString(MESSAGE_FILE_SEARCH_SEARCHSTRING);
    }

    public String getMessageFileSearchExportDirectory() {
        return preferenceStore.getString(MESSAGE_FILE_SEARCH_EXPORT_DIRECTORY);
    }

    public String getSourceFileSearchString() {
        return preferenceStore.getString(SOURCE_FILE_SEARCH_SEARCHSTRING);
    }

    public String getSourceFileSearchExportDirectory() {
        return preferenceStore.getString(SOURCE_FILE_SEARCH_EXPORT_DIRECTORY);
    }

    public boolean isLoadSpooledFilesAsynchronousliy() {
        return preferenceStore.getBoolean(SPOOLED_FILES_LOAD_ASYNCHRONOUSLY);
    }

    public String getSpooledFilesSuggestedFileName() {
        return preferenceStore.getString(SPOOLED_FILES_SUGGESTED_FILE_NAME);
    }

    public String getSpooledFileConversionDefaultFormat() {
        return preferenceStore.getString(SPOOLED_FILES_DEFAULT_FORMAT);
    }

    public int getSpooledFilesMaxFilesToLoad() {
        return preferenceStore.getInt(SPOOLED_FILES_MAX_FILES_TO_LOAD);
    }

    public String getSpooledFileRSEDescription() {
        return preferenceStore.getString(SPOOLED_FILES_RSE_DESCRIPTION);
    }

    public int getSourceFileSearchMemberColumnWidth() {
        return preferenceStore.getInt(SOURCE_FILE_SEARCH_MEMBER_COLUMN_WIDTH);
    }

    public int getSourceFileSearchSrcTypeColumnWidth() {
        return preferenceStore.getInt(SOURCE_FILE_SEARCH_SRC_TYPE_COLUMN_WIDTH);
    }

    public int getSourceFileSearchLastChangedDateColumnWidth() {
        return preferenceStore.getInt(SOURCE_FILE_SEARCH_LAST_CHANGED_DATE_COLUMN_WIDTH);
    }

    public int getSourceFileSearchStatementsCountColumnWidth() {
        return preferenceStore.getInt(SOURCE_FILE_SEARCH_STATEMENTS_COUNT_COLUMN_WIDTH);
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

    public boolean isSpooledFileConversionTextEditAllowed() {
        return preferenceStore.getBoolean(SPOOLED_FILES_CONVERSION_TEXT_EDIT_ALLOWED);
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

    public boolean isSpooledFileConversionHTMLEditAllowed() {
        return preferenceStore.getBoolean(SPOOLED_FILES_CONVERSION_HTML_EDIT_ALLOWED);
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
            return FileHelper.getDefaultRootDirectory();
        }
        return directory;
    }

    public String getSpooledFilePageSize() {
        return preferenceStore.getString(SPOOLED_FILES_PAGE_SIZE);
    }

    public boolean getSpooledFileAdjustFontSize() {
        return preferenceStore.getBoolean(SPOOLED_FILES_ADJUST_FONT_SIZE);
    }

    public int getDataQueueMaximumMessageLength() {
        return preferenceStore.getInt(MONITOR_DTAQ_LENGTH);
    }

    public boolean isDataQueueViewInHex() {
        return preferenceStore.getBoolean(MONITOR_DTAQ_VIEW_IN_HEX);
    }

    public boolean isDataQueueDisplayEndOfData() {
        return preferenceStore.getBoolean(MONITOR_DTAQ_DISPLAY_END_OF_DATA);
    }

    public String getDataQueueReplacementCharacter() {
        return preferenceStore.getString(MONITOR_DTAQ_REPLACEMENT_CHARACTER);
    }

    public int getDataQueueNumberOfMessagesToRetrieve() {
        return preferenceStore.getInt(MONITOR_DTAQ_NUMBER_OF_MESSAGES);
    }

    public boolean isSourceFileSearchResultsEditEnabled() {
        return preferenceStore.getBoolean(SOURCE_FILE_SEARCH_RESULTS_IS_EDIT_ENABLED);
    }

    public String getSourceFileSearchResultsSaveDirectory() {
        String directory = preferenceStore.getString(SOURCE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY);
        if (!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        return directory;
    }

    public String getSourceFileSearchResultsLastUsedFileName() {
        String filename = preferenceStore.getString(SOURCE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME);
        if (!StringHelper.isNullOrEmpty(filename)) {
            File file = new File(filename);
            if (!file.exists()) {
                filename = null;
            } else {
                if (file.isDirectory()) {
                    filename = filename + SOURCE_FILE_SEARCH_FILE_NAME; // $NON-NLS-1$
                }
            }
        } else {
            filename = null;
        }

        if (filename == null) {
            return getDefaultSourceFileSearchResultsSaveDirectory() + SOURCE_FILE_SEARCH_FILE_NAME; // $NON-NLS-1$
        }

        return filename;
    }

    public boolean isSourceFileSearchResultsAutoSaveEnabled() {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result.
         */
        if (!IBMiHostContributionsHandler.hasContribution()) {
            return false;
        }

        return preferenceStore.getBoolean(SOURCE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED);
    }

    public String getSourceFileSearchResultsAutoSaveFileName() {
        return preferenceStore.getString(SOURCE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE);
    }

    public String getMessageFileSearchResultsSaveDirectory() {
        String directory = preferenceStore.getString(MESSAGE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY);
        if (!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        return directory;
    }

    public String getMessageFileSearchResultsLastUsedFileName() {
        String filename = preferenceStore.getString(MESSAGE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME);
        if (!StringHelper.isNullOrEmpty(filename)) {
            File file = new File(filename);
            if (!file.exists()) {
                filename = null;
            } else {
                if (file.isDirectory()) {
                    filename = filename + MESSAGE_FILE_SEARCH_FILE_NAME; // $NON-NLS-1$
                }
            }
        } else {
            filename = null;
        }

        if (filename == null) {
            return getDefaultMessageFileSearchResultsSaveDirectory() + MESSAGE_FILE_SEARCH_FILE_NAME; // $NON-NLS-1$
        }

        return filename;
    }

    public boolean isMessageFileSearchResultsAutoSaveEnabled() {
        return preferenceStore.getBoolean(MESSAGE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED);
    }

    public String getMessageFileSearchResultsAutoSaveFileName() {
        return preferenceStore.getString(MESSAGE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE);
    }

    public int getMessageFileCompareLineWidth() {
        return preferenceStore.getInt(MESSAGE_FILE_COMPARE_LINE_WIDTH);
    }

    public boolean isSourceMemberCompareLoadingPreviousValuesOfRightMemberEnabled() {
        return preferenceStore.getBoolean(SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_RIGHT_MEMBER);
    }

    public boolean isSourceMemberCompareLoadingPreviousValuesOfAncestorMemberEnabled() {
        return preferenceStore.getBoolean(SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_ANCESTOR_MEMBER);
    }

    public String getDateFormatLabel() {
        return preferenceStore.getString(APPEARANCE_DATE_FORMAT);
    }

    public String getTimeFormatLabel() {
        return preferenceStore.getString(APPEARANCE_TIME_FORMAT);
    }

    public boolean isFormatResourceDates() {
        return preferenceStore.getBoolean(APPEARANCE_FORMAT_RESOURCE_DATES);
    }

    public int getAutoRefreshDelay() {
        return preferenceStore.getInt(APPEARANCE_AUTO_REFRESH_DELAY);
    }

    public int getAutoRefreshThreshold() {
        return preferenceStore.getInt(APPEARANCE_AUTO_REFRESH_THRESHOLD);
    }

    public boolean isShowErrorLog() {
        return preferenceStore.getBoolean(APPEARANCE_SHOW_ERROR_LOG);
    }

    public boolean isObjectDecorationExtension() {
        return preferenceStore.getBoolean(DECORATION_OBJECT_EXTENSION);
    }

    public boolean isSourceMemberDecorationExtension() {
        return preferenceStore.getBoolean(DECORATION_SOURCE_MEMBER_EXTENSION);
    }

    public boolean isDataMemberDecorationExtension() {
        return preferenceStore.getBoolean(DECORATION_DATA_MEMBER_EXTENSION);
    }

    public boolean isISphereJdbcConnectionManager() {
        return preferenceStore.getBoolean(JDBC_USE_ISPHERE_MANAGER);
    }

    public boolean isKerberosAuthentication() {

        return IBMiHostContributionsHandler.isKerberosAuthentication();
    }

    /*
     * Preferences: SETTER
     */

    public void setShowWarningMessage(String showWarningKey, boolean enable) {
        preferenceStore.setValue(getShowWarningKey(showWarningKey), enable);
    }

    public void setISphereLibrary(String aLibrary) {
        preferenceStore.setValue(ISPHERE_LIBRARY, aLibrary.trim());
    }

    public void setASPGroup(String aASPGroup) {
        preferenceStore.setValue(ASP_GROUP, aASPGroup.trim());
    }

    public void setHostName(String aHostName) {
        preferenceStore.setValue(HOST_NAME, aHostName);
    }

    public void setFtpPortNumber(int aPortNumber) {
        preferenceStore.setValue(FTP_PORT_NUMBER, aPortNumber);
    }

    public void setSystemCcsid(int ccsid) {
        preferenceStore.setValue(SYSTEM_CCSID, ccsid);
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
        preferenceStore.setValue(MESSAGE_FILE_SEARCH_SEARCHSTRING, aSearchString.trim());
    }

    public void setMessageFileSearchExportDirectory(String aPath) {
        preferenceStore.setValue(MESSAGE_FILE_SEARCH_EXPORT_DIRECTORY, aPath);
    }

    public void setSourceFileSearchString(String aSearchString) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_SEARCHSTRING, aSearchString.trim());
    }

    public void setSourceFileSearchExportDirectory(String aPath) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_EXPORT_DIRECTORY, aPath);
    }

    public void setLoadSpooledFilesAsynchronousliy(boolean asynchronously) {
        preferenceStore.setValue(SPOOLED_FILES_LOAD_ASYNCHRONOUSLY, asynchronously);
    }

    public void setSpooledFilesSuggestedFileName(String fileName) {
        preferenceStore.setValue(SPOOLED_FILES_SUGGESTED_FILE_NAME, fileName);
    }

    public void setSpooledFileDefaultFormat(String aFormat) {
        preferenceStore.setValue(SPOOLED_FILES_DEFAULT_FORMAT, aFormat);
    }

    public void setSpooledFileMaxFilesToLoad(int count) {
        preferenceStore.setValue(SPOOLED_FILES_MAX_FILES_TO_LOAD, count);
    }

    public void setSpooledFileRSEDescription(String description) {
        preferenceStore.setValue(SPOOLED_FILES_RSE_DESCRIPTION, description);
    }

    public void setSourceFileSearchMemberColumnWidth(int width) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_MEMBER_COLUMN_WIDTH, width);
    }

    public void setSourceFileSearchSrcTypeColumnWidth(int width) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_SRC_TYPE_COLUMN_WIDTH, width);
    }

    public void setSourceFileSearchLastChangedDateColumnWidth(int width) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_LAST_CHANGED_DATE_COLUMN_WIDTH, width);
    }

    public void setSourceFileSearchStatementsCountColumnWidth(int width) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_STATEMENTS_COUNT_COLUMN_WIDTH, width);
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

    public void setSpooledFileConversionTextEditAllowed(boolean enableEdit) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_TEXT_EDIT_ALLOWED, enableEdit);
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

    public void setSpooledFileConversionHTMLEditAllowed(boolean enableEdit) {
        preferenceStore.setValue(SPOOLED_FILES_CONVERSION_HTML_EDIT_ALLOWED, enableEdit);
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

    public void setSpooledFilePageSize(String aPageSize) {
        preferenceStore.setValue(SPOOLED_FILES_PAGE_SIZE, aPageSize);
    }

    public void setSpooledFileAdjustFontSize(boolean anAdjustSize) {
        preferenceStore.setValue(SPOOLED_FILES_ADJUST_FONT_SIZE, anAdjustSize);
    }

    public void setDataQueueMaximumMessageLength(int length) {
        preferenceStore.setValue(MONITOR_DTAQ_LENGTH, length);
    }

    public void setDataQueueViewInHex(boolean viewInHex) {
        preferenceStore.setValue(MONITOR_DTAQ_VIEW_IN_HEX, viewInHex);
    }

    public void setDataQueueDisplayEndOfData(boolean viewInHex) {
        preferenceStore.setValue(MONITOR_DTAQ_DISPLAY_END_OF_DATA, viewInHex);
    }

    public void setDataQueueReplacementCharacter(String replacementCharacter) {
        preferenceStore.setValue(MONITOR_DTAQ_REPLACEMENT_CHARACTER, replacementCharacter);
    }

    public void setDataQueueNumberOfMessagesToRetrieve(int numberOfMessages) {
        preferenceStore.setValue(MONITOR_DTAQ_NUMBER_OF_MESSAGES, numberOfMessages);
    }

    public void setSourceFileSearchResultsEditEnabled(boolean editable) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_RESULTS_IS_EDIT_ENABLED, editable);
    }

    public void setSourceFileSearchResultsSaveDirectory(String directory) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY, directory);
    }

    public void setSourceFileSearchResultsLastUsedFileName(String directory) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME, directory);
    }

    public void setSourceFileSearchResultsAutoSaveEnabled(boolean enabled) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED, enabled);
    }

    public void setSourceFileSearchResultsAutoSaveFileName(String filename) {
        preferenceStore.setValue(SOURCE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE, filename);
    }

    public void setMessageFileSearchResultsSaveDirectory(String directory) {
        preferenceStore.setValue(MESSAGE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY, directory);
    }

    public void setMessageFileSearchResultsLastUsedFileName(String directory) {
        preferenceStore.setValue(MESSAGE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME, directory);
    }

    public void setMessageFileSearchResultsAutoSaveEnabled(boolean enabled) {
        preferenceStore.setValue(MESSAGE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED, enabled);
    }

    public void setMessageFileSearchResultsAutoSaveFileName(String filename) {
        preferenceStore.setValue(MESSAGE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE, filename);
    }

    public void setMessageFileCompareLineWidth(int lineWidth) {
        preferenceStore.setValue(MESSAGE_FILE_COMPARE_LINE_WIDTH, lineWidth);
    }

    public void setSourceMemberCompareLoadingPreviousValuesOfRightMemberEnabled(boolean enabled) {
        preferenceStore.setValue(SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_RIGHT_MEMBER, enabled);
    }

    public void setSourceMemberCompareLoadingPreviousValuesOfAncestorMemberEnabled(boolean enabled) {
        preferenceStore.setValue(SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_ANCESTOR_MEMBER, enabled);
    }

    public void setDateFormatLabel(String dateFormatLabel) {
        preferenceStore.setValue(APPEARANCE_DATE_FORMAT, dateFormatLabel);
    }

    public void setTimeFormatLabel(String dateFormatLabel) {
        preferenceStore.setValue(APPEARANCE_TIME_FORMAT, dateFormatLabel);
    }

    public void setFormatResourceDates(boolean format) {
        preferenceStore.setValue(APPEARANCE_FORMAT_RESOURCE_DATES, format);
    }

    public void setAutoRefreshDelay(int delayMillis) {
        preferenceStore.setValue(APPEARANCE_AUTO_REFRESH_DELAY, delayMillis);
    }

    public void setAutoRefreshThreshold(int threshold) {
        preferenceStore.setValue(APPEARANCE_AUTO_REFRESH_THRESHOLD, threshold);
    }

    public void setShowErrorLog(boolean show) {
        preferenceStore.setValue(APPEARANCE_SHOW_ERROR_LOG, show);
    }

    public void setObjectDecorationExtension(boolean enabled) {
        preferenceStore.setValue(DECORATION_OBJECT_EXTENSION, enabled);
    }

    public void setSourceMemberDecorationExtension(boolean enabled) {
        preferenceStore.setValue(DECORATION_SOURCE_MEMBER_EXTENSION, enabled);
    }

    public void setDataMemberDecorationExtension(boolean enabled) {
        preferenceStore.setValue(DECORATION_DATA_MEMBER_EXTENSION, enabled);
    }

    public void setUseISphereJdbcConnectionManager(boolean enabled) {
        preferenceStore.setValue(JDBC_USE_ISPHERE_MANAGER, enabled);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        String[] showWarningKeys = DoNotAskMeAgainDialog.getKeys();
        for (String showWarningKey : showWarningKeys) {
            preferenceStore.setDefault(getShowWarningKey(showWarningKey), DoNotAskMeAgainDialog.getDefaultShowWarning());
        }

        preferenceStore.setDefault(ISPHERE_LIBRARY, getDefaultISphereLibrary());
        preferenceStore.setDefault(ASP_GROUP, getDefaultASPGroup());
        preferenceStore.setDefault(HOST_NAME, getDefaultHostName());
        preferenceStore.setDefault(FTP_PORT_NUMBER, getDefaultFtpPortNumber());
        preferenceStore.setDefault(SYSTEM_CCSID, getDefaultSystemCcsid());

        preferenceStore.setDefault(SEARCH_FOR_UPDATES, getDefaultSearchForUpdates());
        preferenceStore.setDefault(SEARCH_FOR_BETA_VERSIONS, getDefaultSearchForBetaVersions());
        preferenceStore.setDefault(URL_FOR_UPDATES, getDefaultURLForUpdates());
        preferenceStore.setDefault(LAST_VERSION_FOR_UPDATES, getDefaultLastVersionForUpdates());

        preferenceStore.setDefault(SPOOLED_FILES_LOAD_ASYNCHRONOUSLY, getDefaultLoadSpooledFilesAsynchronously());
        preferenceStore.setDefault(SPOOLED_FILES_SUGGESTED_FILE_NAME, getDefaultSpooledFilesSuggestedFileName());
        preferenceStore.setDefault(SPOOLED_FILES_DEFAULT_FORMAT, getDefaultSpooledFileConversionDefaultFormat());

        preferenceStore.setDefault(SPOOLED_FILES_MAX_FILES_TO_LOAD, getDefaultSpooledFileMaxFilesToLoad());
        preferenceStore.setDefault(SPOOLED_FILES_RSE_DESCRIPTION, getDefaultSpooledFileRSEDescription());

        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT, getDefaultSpooledFileConversionText());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT_COMMAND, getDefaultSpooledFileConversionTextCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT_LIBRARY, getDefaultSpooledFileConversionTextLibrary());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_TEXT_EDIT_ALLOWED, getDefaultSpooledFileConversionTextEditAllowed());

        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML, getDefaultSpooledFileConversionHTML());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML_COMMAND, getDefaultSpooledFileConversionHTMLCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML_LIBRARY, getDefaultSpooledFileConversionHTMLLibrary());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_HTML_EDIT_ALLOWED, getDefaultSpooledFileConversionHTMLEditAllowed());

        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF, getDefaultSpooledFileConversionPDF());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF_COMMAND, getDefaultSpooledFileConversionPDFCommand());
        preferenceStore.setDefault(SPOOLED_FILES_CONVERSION_PDF_LIBRARY, getDefaultSpooledFileConversionPDFLibrary());

        preferenceStore.setDefault(SPOOLED_FILES_PAGE_SIZE, getDefaultSpooledFilePageSize());
        preferenceStore.setDefault(SPOOLED_FILES_ADJUST_FONT_SIZE, getDefaultSpooledFileAdjustFontSize());

        preferenceStore.setDefault(SOURCE_FILE_SEARCH_MEMBER_COLUMN_WIDTH, getDefaultSourceFileSearchMemberColumnWidth());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_SRC_TYPE_COLUMN_WIDTH, getDefaultSourceFileSearchSrcTypeColumnWidth());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_LAST_CHANGED_DATE_COLUMN_WIDTH, getDefaultSourceFileSearchLastChangedDateColumnWidth());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_STATEMENTS_COUNT_COLUMN_WIDTH, getDefaultSourceFileSearchStatementsCountColumnWidth());

        preferenceStore.setDefault(MONITOR_DTAQ_LENGTH, getDefaultDataQueueMaximumMessageLength());
        preferenceStore.setDefault(MONITOR_DTAQ_VIEW_IN_HEX, getDefaultDataQueueViewInHex());
        preferenceStore.setDefault(MONITOR_DTAQ_DISPLAY_END_OF_DATA, getDefaultDataQueueDisplayEndOfData());
        preferenceStore.setDefault(MONITOR_DTAQ_REPLACEMENT_CHARACTER, getDefaultDataQueueReplacementCharacter());
        preferenceStore.setDefault(MONITOR_DTAQ_NUMBER_OF_MESSAGES, getDefaultDataQueueNumberOfMessagesToRetrieve());

        preferenceStore.setDefault(SOURCE_FILE_SEARCH_RESULTS_IS_EDIT_ENABLED, getDefaultSourceFileSearchResultsEditEnabled());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY, getDefaultSourceFileSearchResultsSaveDirectory());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME, getDefaultSourceFileSearchResultsLastUsedFileName());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED, getDefaultSourceFileSearchResultsAutoSaveEnabled());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE, getDefaultSourceFileSearchResultsAutoSaveFileName());
        preferenceStore.setDefault(SOURCE_FILE_SEARCH_EXPORT_DIRECTORY, getDefaultSourceFileSearchExportDirectory());

        preferenceStore.setDefault(MESSAGE_FILE_SEARCH_RESULTS_SAVE_DIRECTORY, getDefaultMessageFileSearchResultsSaveDirectory());
        preferenceStore.setDefault(MESSAGE_FILE_SEARCH_RESULTS_LAST_USED_FILE_NAME, getDefaultMessageFileSearchResultsLastUsedFileName());
        preferenceStore.setDefault(MESSAGE_FILE_SEARCH_RESULTS_IS_AUTO_SAVE_ENABLED, getDefaultMessageFileSearchResultsAutoSaveEnabled());
        preferenceStore.setDefault(MESSAGE_FILE_SEARCH_RESULTS_AUTO_SAVE_FILE, getDefaultMessageFileSearchResultsAutoSaveFileName());
        preferenceStore.setDefault(MESSAGE_FILE_SEARCH_EXPORT_DIRECTORY, getDefaultMessageFileSearchExportDirectory());

        preferenceStore.setDefault(MESSAGE_FILE_COMPARE_LINE_WIDTH, getDefaultMessageFileCompareMinLineWidth());
        preferenceStore.setDefault(SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_RIGHT_MEMBER,
            getDefaultSourceMemberCompareLoadingPreviousValuesEnabled());
        preferenceStore.setDefault(SOURCE_MEMBER_COMPARE_LOAD_PREVIOUS_VALUES_ANCESTOR_MEMBER,
            getDefaultSourceMemberCompareLoadingPreviousValuesEnabled());

        preferenceStore.setDefault(APPEARANCE_DATE_FORMAT, getDefaultDateFormatLabel());
        preferenceStore.setDefault(APPEARANCE_TIME_FORMAT, getDefaultTimeFormatLabel());
        preferenceStore.setDefault(APPEARANCE_FORMAT_RESOURCE_DATES, getDefaultFormatResourceDates());

        preferenceStore.setDefault(APPEARANCE_AUTO_REFRESH_DELAY, getDefaultAutoRefreshDelay());
        preferenceStore.setDefault(APPEARANCE_AUTO_REFRESH_THRESHOLD, getDefaultAutoRefreshThreshold());
        preferenceStore.setDefault(APPEARANCE_SHOW_ERROR_LOG, getDefaultShowErrorLog());

        preferenceStore.setDefault(DECORATION_OBJECT_EXTENSION, getDefaultObjectDecorationExtension());
        preferenceStore.setDefault(DECORATION_SOURCE_MEMBER_EXTENSION, getDefaultSourceMemberDecorationExtension());
        preferenceStore.setDefault(DECORATION_DATA_MEMBER_EXTENSION, getDefaultDataMemberDecorationExtension());

        preferenceStore.setDefault(JDBC_USE_ISPHERE_MANAGER, getDefaultUseISphereJdbcConnectionManager());
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
     * Returns the default asp group.
     * 
     * @return default asp group
     */
    public String getDefaultASPGroup() {
        return "*NONE";
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
     * Returns the default system ccsid.
     * 
     * @return default system ccsid
     */
    public int getDefaultSystemCcsid() {
        // return com.ibm.as400.access.NLS.localeToCCSID(Locale.getDefault());
        return 273; // keep backward compatibility to previous releases
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
        return "http://isphere.sourceforge.net/MANIFEST.MF?raw";
    }

    /**
     * Returns the default last version for updates.
     * 
     * @return default last version for updates.
     */
    public String getDefaultLastVersionForUpdates() {
        return "0.0.0.r";
    }

    public boolean getDefaultLoadSpooledFilesAsynchronously() {
        return false;
    }

    public String getDefaultSpooledFilesSuggestedFileName() {
        return "*SIMPLE";
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
     * Returns the default value for maximum number of spooled files to load.
     * 
     * @return default maximum number of spooled files to load
     */
    public int getDefaultSpooledFileMaxFilesToLoad() {
        return 5000;
    }

    /**
     * Returns the default spooled file description.
     * 
     * @return default maximum number of spooled files to load
     */
    public String getDefaultSpooledFileRSEDescription() {
        return SpooledFile.VARIABLE_SPLF + " - " + SpooledFile.VARIABLE_STATUS; //$NON-NLS-1$
    }

    /**
     * Returns the default width of the 'member' column of SearchResultViewer of
     * the iSphere Source File Search.
     * 
     * @return default column width
     */
    public int getDefaultSourceFileSearchMemberColumnWidth() {
        return 400;
    }

    /**
     * Returns the default width of the 'type' column of SearchResultViewer of
     * the iSphere Source File Search.
     * 
     * @return default column width
     */
    public int getDefaultSourceFileSearchSrcTypeColumnWidth() {
        return 80;
    }

    /**
     * Returns the default width of the 'last changed' column of
     * SearchResultViewer of the iSphere Source File Search.
     * 
     * @return default column width
     */
    public int getDefaultSourceFileSearchLastChangedDateColumnWidth() {
        return 120;
    }

    /**
     * Returns the default width of the 'statements count' column of
     * SearchResultViewer of the iSphere Source File Search.
     * 
     * @return default column width
     */
    public int getDefaultSourceFileSearchStatementsCountColumnWidth() {
        return 80;
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
     * Return the default for "allow edit" for spooled files converted to text.
     * 
     * @return <code>true</code>, when editing is allowed, else
     *         <code>false</code>.
     */
    public boolean getDefaultSpooledFileConversionTextEditAllowed() {
        return false;
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
     * Return the default for "allow edit" for spooled files converted to HTML.
     * 
     * @return <code>true</code>, when editing is allowed, else
     *         <code>false</code>.
     */
    public boolean getDefaultSpooledFileConversionHTMLEditAllowed() {
        return false;
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

    /**
     * Return the default page size for spooled file conversion to PDF.
     * 
     * @return default page size for PDF conversion
     */
    public String getDefaultSpooledFilePageSize() {
        return PageSize.PAGE_SIZE_CALCULATE;
    }

    /**
     * Return whether the font size is adjusted to the page size for spooled
     * file conversion to PDF.
     * 
     * @return default for adjusting the font size
     */
    public boolean getDefaultSpooledFileAdjustFontSize() {
        return false;
    }

    /**
     * Return the default command for user defined spooled file to PDF
     * conversion.
     * 
     * @return command for user defined spooled file to PDF conversion
     */
    public int getDefaultDataQueueMaximumMessageLength() {
        return 2048;
    }

    /**
     * Returns the default 'view hex' flag of the data queue view.
     * 
     * @return default 'view hex' flag.
     */
    public boolean getDefaultDataQueueViewInHex() {
        return true;
    }

    /**
     * Returns the default 'display end of data' flag of the data queue view.
     * 
     * @return default 'display end of data' flag.
     */
    public boolean getDefaultDataQueueDisplayEndOfData() {
        return false;
    }

    /**
     * Returns the default 'replacement character' that is used to replace
     * non-displayable characters.
     * 
     * @return default 'replacement character'.
     */
    public String getDefaultDataQueueReplacementCharacter() {
        return "÷";
    }

    /**
     * Returns the default 'number of messages' that are retrieved in the
     * iSphere data queue monitor view.
     * 
     * @return default 'number of messages'.
     */
    public int getDefaultDataQueueNumberOfMessagesToRetrieve() {
        return 10;
    }

    /**
     * Returns the default 'is edit mode' flag of the view search results view.
     * 
     * @return default 'is edit mode' flag.
     */
    public boolean getDefaultSourceFileSearchResultsEditEnabled() {
        return true;
    }

    /**
     * Returns the default 'source file search save path'.
     * 
     * @return default path for saving source file search results
     */
    public String getDefaultSourceFileSearchResultsSaveDirectory() {

        String path = ISpherePlugin.getDefault().getStateLocation().toFile().getAbsolutePath();
        path = path + File.separator + SOURCE_FILE_SEARCH_DIRECTORY + File.separator;

        try {
            FileHelper.ensureDirectory(path);
        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to create directory: " + path, e); //$NON-NLS-1$
        }

        return path;
    }

    /**
     * Returns the default 'save path' that was last selected to save source
     * file search results.
     * 
     * @return default path last used for saving
     */
    public String getDefaultSourceFileSearchResultsLastUsedFileName() {
        return "";
    }

    /**
     * Returns the default 'is auto save' flag of the view search results view.
     * 
     * @return default 'is auto save' flag.
     */
    public boolean getDefaultSourceFileSearchResultsAutoSaveEnabled() {
        return false;
    }

    /**
     * Returns the default 'source file search auto save file name'.
     * 
     * @return default file name for saving search results
     */
    public String getDefaultSourceFileSearchResultsAutoSaveFileName() {

        return "iSphereSourceFileSearchResultAutoSave." + SearchResultManager.FILE_EXTENSION;
    }

    /**
     * Returns the default export directory of the iSphere Source File Search
     * option.
     * 
     * @return default directory
     */
    public String getDefaultSourceFileSearchExportDirectory() {
        return FileHelper.getDefaultRootDirectory();
    }

    /**
     * Returns the default 'message file search save path'.
     * 
     * @return default path for saving message file search results
     */
    public String getDefaultMessageFileSearchResultsSaveDirectory() {

        String path = ISpherePlugin.getDefault().getStateLocation().toFile().getAbsolutePath();
        path = path + File.separator + MESSAGE_FILE_SEARCH_DIRECTORY + File.separator;

        try {
            FileHelper.ensureDirectory(path);
        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to create directory: " + path, e); //$NON-NLS-1$
        }

        return path;
    }

    /**
     * Returns the default 'save path' that was last selected to save message
     * file search results.
     * 
     * @return default path last used for saving
     */
    public String getDefaultMessageFileSearchResultsLastUsedFileName() {
        return "";
    }

    /**
     * Returns the default 'is auto save' flag of the view search results view.
     * 
     * @return default 'is auto save' flag.
     */
    public boolean getDefaultMessageFileSearchResultsAutoSaveEnabled() {
        return false;
    }

    /**
     * Returns the default 'message file search auto save file name'.
     * 
     * @return default file name for saving search results
     */
    public String getDefaultMessageFileSearchResultsAutoSaveFileName() {

        return "iSphereMessageFileSearchResultAutoSave." + SearchResultManager.FILE_EXTENSION;
    }

    /**
     * Returns the default export directory of the iSphere Message File Search
     * option.
     * 
     * @return default directory
     */
    public String getDefaultMessageFileSearchExportDirectory() {
        return FileHelper.getDefaultRootDirectory();
    }

    /**
     * Returns the default 'line width' for comparing message files.
     * 
     * @return default line width of first and second level text
     */
    public int getDefaultMessageFileCompareMinLineWidth() {

        return 70;
    }

    public boolean getDefaultSourceMemberCompareLoadingPreviousValuesEnabled() {
        return false;
    }

    public String getDefaultDateFormatLabel() {
        return APPEARANCE_DATE_FORMAT_LOCALE;
    }

    public String getDefaultTimeFormatLabel() {
        return APPEARANCE_TIME_FORMAT_LOCALE;
    }

    public boolean getDefaultFormatResourceDates() {
        return false;
    }

    public int getDefaultAutoRefreshDelay() {
        return 400;
    }

    public int getDefaultAutoRefreshThreshold() {
        return 5000;
    }

    public boolean getDefaultShowErrorLog() {
        return false;
    }

    public boolean getDefaultObjectDecorationExtension() {
        return false;
    }

    public boolean getDefaultSourceMemberDecorationExtension() {
        return false;
    }

    public boolean getDefaultDataMemberDecorationExtension() {
        return false;
    }

    public boolean getDefaultUseISphereJdbcConnectionManager() {
        return false;
    }

    /**
     * Returns an arrays of maximum lengths values for retrieving data queue
     * entries.
     * 
     * @return message length values
     */
    public int[] getDataQueueMaximumMessageLengthValues() {

        int[] lengths = new int[6];
        lengths[0] = -1;
        lengths[1] = 64;
        lengths[2] = 512;
        lengths[3] = 2048;
        lengths[4] = 8196;
        lengths[5] = MessageLengthAction.MAX_LENGTH;

        Arrays.sort(lengths);
        if (Arrays.binarySearch(lengths, getDataQueueMaximumMessageLength()) < 0) {
            lengths[0] = getDataQueueMaximumMessageLength();
            Arrays.sort(lengths);
            return lengths;
        }

        int[] lengths2 = new int[lengths.length - 1];
        System.arraycopy(lengths, 1, lengths2, 0, lengths2.length);
        return lengths2;
    }

    public SimpleDateFormat getDateFormatter() {

        String pattern = getDateFormatPattern();
        if (pattern == null) {
            pattern = getDateFormatsMap().get(getDefaultDateFormatLabel());
        }

        if (pattern == null) {
            return new SimpleDateFormat(FastDateFormat.getDateInstance(FastDateFormat.SHORT).getPattern());
        }

        return new SimpleDateFormat(pattern);
    }

    public String getDateFormatPattern() {
        return getDateFormatsMap().get(getDateFormatLabel());
    }

    public String[] getSpooledFileAllowEditLabels() {

        return new String[] { Messages.Label_Viewer, Messages.Label_Editor };
    }

    public String[] getDateFormatLabels() {

        Set<String> formats = getDateFormatsMap().keySet();

        String[] dateFormats = formats.toArray(new String[formats.size()]);
        Arrays.sort(dateFormats);

        return dateFormats;
    }

    private Map<String, String> getDateFormatsMap() {

        if (dateFormats != null) {
            return dateFormats;
        }

        dateFormats = new HashMap<String, String>();

        dateFormats.put(getDefaultDateFormatLabel(), null);
        dateFormats.put("de (dd.mm.yyyy)", "dd.MM.yyyy");
        dateFormats.put("us (mm/dd/yyyy)", "MM/dd/yyyy");
        dateFormats.put("iso (yyyy.mm.dd)", "yyyy.MM.dd");

        return dateFormats;
    }

    public SimpleDateFormat getTimeFormatter() {

        String pattern = getTimeFormatPattern();
        if (pattern == null) {
            pattern = getTimeFormatsMap().get(getDefaultTimeFormatLabel());
        }

        if (pattern == null) {
            return new SimpleDateFormat(FastDateFormat.getTimeInstance(FastDateFormat.SHORT).getPattern());
        }

        return new SimpleDateFormat(pattern);
    }

    public String getTimeFormatPattern() {
        return getTimeFormatsMap().get(getTimeFormatLabel());
    }

    public String[] getTimeFormatLabels() {

        Set<String> formats = getTimeFormatsMap().keySet();

        String[] timeFormats = formats.toArray(new String[formats.size()]);
        Arrays.sort(timeFormats);

        return timeFormats;
    }

    public String[] getSpooledFileSuggestedNames() {

        Set<String> names = getSpooledFileSuggestedNamesMap().keySet();

        String[] suggestedNames = names.toArray(new String[names.size()]);
        Arrays.sort(suggestedNames);

        return suggestedNames;
    }

    public String getSuggestedSpooledFileName() {

        String key = getSpooledFilesSuggestedFileName();
        if (!getSpooledFileSuggestedNamesMap().containsKey(key)) {
            return key;
        }

        return getSpooledFileSuggestedNamesMap().get(key);
    }

    public String[] getDataQueueNumberOfMessagesToRetrieveItems() {
        return new String[] { "1", "5", "10", "50", "100" };
    }

    private Map<String, String> getTimeFormatsMap() {

        if (timeFormats != null) {
            return timeFormats;
        }

        timeFormats = new HashMap<String, String>();

        timeFormats.put(getDefaultDateFormatLabel(), null);
        timeFormats.put("de (hh:mm:ss)", "HH:mm:ss"); //$NON-NLS-1$
        timeFormats.put("us (hh:mm:ss AM/PM)", "KK:mm:ss a"); //$NON-NLS-1$
        timeFormats.put("iso (hh.mm.ss)", "HH.mm.ss");

        return timeFormats;
    }

    private Map<String, String> getSpooledFileSuggestedNamesMap() {

        if (suggestedSpooledFileNames != null) {
            return suggestedSpooledFileNames;
        }

        final String UNDERSCORE = "_"; //$NON-NLS-1$

        suggestedSpooledFileNames = new HashMap<String, String>();

        suggestedSpooledFileNames.put("*DEFAULT", "spooled_file"); //$NON-NLS-1$
        suggestedSpooledFileNames.put("*SIMPLE", SpooledFile.VARIABLE_SPLF);
        suggestedSpooledFileNames.put("*QUALIFIED", SpooledFile.VARIABLE_SPLF + UNDERSCORE + SpooledFile.VARIABLE_SPLFNBR + UNDERSCORE
            + SpooledFile.VARIABLE_JOBNBR + UNDERSCORE + SpooledFile.VARIABLE_JOBUSR + UNDERSCORE + SpooledFile.VARIABLE_JOBNAME + UNDERSCORE
            + SpooledFile.VARIABLE_JOBSYS);

        return suggestedSpooledFileNames;
    }

    private String getShowWarningKey(String showWarningKey) {
        return WARNING_BASE_KEY + showWarningKey;
    }
}