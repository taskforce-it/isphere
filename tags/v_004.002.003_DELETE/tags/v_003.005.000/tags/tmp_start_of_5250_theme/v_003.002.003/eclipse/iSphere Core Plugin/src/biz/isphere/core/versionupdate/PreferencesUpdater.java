/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.versionupdate;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import biz.isphere.base.versioncheck.IObsoleteBundles;
import biz.isphere.core.preferences.Preferences;

public final class PreferencesUpdater implements IObsoleteBundles, IObsoletePreferences {

    private PreferencesUpdater() {
    }

    public static void update() {
        PreferencesUpdater tUpdater = new PreferencesUpdater();
        tUpdater.performSettingsUpdate();
    }

    private void performSettingsUpdate() {
        performUpdate_v142();
    }

    private void performUpdate_v142() {
        if (!hasBundle(DE_TASKFORCE_ISPHERE)) {
            return;
        }

        String tValue;

        // iSphere Library
        tValue = getValue(DE_TASKFORCE_ISPHERE_LIBRARY);
        if (tValue != null) {
            Preferences.getInstance().setISphereLibrary(tValue);
        }

        // Spooled files format on double-click.
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_DEFAULT_FORMAT);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileDefaultFormat(tValue);
        }

        // Spooled file conversion to TEXT.
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_TEXT);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionText(tValue);
        }
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_TEXT_LIBRARY);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionLibraryText(tValue);
        }
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_TEXT_COMMAND);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionCommandText(tValue);
        }

        // Spooled file conversion to HTML.
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_HTML);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionHTML(tValue);
        }
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_HTML_LIBRARY);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionLibraryHTML(tValue);
        }
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_HTML_COMMAND);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionCommandHTML(tValue);
        }

        // Spooled file conversion to PDF.
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_PDF);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionPDF(tValue);
        }
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_PDF_LIBRARY);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionLibraryPDF(tValue);
        }
        tValue = getValue(DE_TASKFORCE_ISPHERE_SPOOLED_FILES_CONVERSION_PDF_COMMAND);
        if (tValue != null) {
            Preferences.getInstance().setSpooledFileConversionCommandPDF(tValue);
        }

        // Source file search string.
        tValue = getValue(DE_TASKFORCE_ISPHERE_SOURCEFILESEARCH_SEARCHSTRING);
        if (tValue != null) {
            Preferences.getInstance().setSourceFileSearchString(tValue);
        }

        // Message file search string.
        tValue = getValue(DE_TASKFORCE_ISPHERE_MESSAGEFILESEARCH_SEARCHSTRING);
        if (tValue != null) {
            Preferences.getInstance().setMessageFileSearchString(tValue);
        }
    }

    private String getValue(String aKey) {
        return Platform.getPreferencesService().getString(DE_TASKFORCE_ISPHERE, aKey, null, null);
    }

    private boolean hasBundle(String aBundleID) {
        Bundle bundle = Platform.getBundle(aBundleID);
        return bundle != null;

    }
}
