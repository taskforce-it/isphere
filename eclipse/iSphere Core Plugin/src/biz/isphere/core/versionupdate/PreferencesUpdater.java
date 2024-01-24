/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.versionupdate;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;

import biz.isphere.base.internal.UIHelper;
import biz.isphere.base.versioncheck.IObsoleteBundles;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.preferences.Preferences;

public final class PreferencesUpdater implements IObsoleteBundles, IObsoletePreferences {

    private static boolean performUpdate_v5210 = false;

    private PreferencesUpdater() {
    }

    public static void update() {
        PreferencesUpdater tUpdater = new PreferencesUpdater();
        tUpdater.performSettingsUpdate();
    }

    public static void displayUpdateInformation() {
        PreferencesUpdater tUpdater = new PreferencesUpdater();
        tUpdater.displayInformation_v5210();
    }

    private void performSettingsUpdate() {
        performUpdate_v142();
        performUpdate_v400();
    }

    private void performUpdate_v400() {
        if (!hasBundle(ISpherePlugin.PLUGIN_ID)) {
            return;
        }
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

    private synchronized void displayInformation_v5210() {
        if (hasBundle(BIZ_ISPHERE_COMPAREFILTER)) {
            if (!performUpdate_v5210) {
                performUpdate_v5210 = true;
                System.out.println("Scheduling job.");
                new UIJob("") {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        //@formatter:off
                        String message =
                            "The 'iSphere Compare Filters for RDi 9.5+' plug-in has been removed.\nPlease uninstall the plug-in:\n\n"
                          + "1. Go to 'Help -> About -> Installation Details -> Installed Software'.\n"
                          + "2. filter for 'iSphere Compare Filters'.\n"
                          + "3. Select the plug-in.\n" 
                          + "4. Click the 'Uninstall...' button.'.\n\n"
                          + "The plug-in will be removed with the next iSphere version. You will not be able to update iSphere "
                          + "as long as the plug-in is installed.";
                        //@formatter:on
                        DoNotAskMeAgainDialog.openInformation(UIHelper.getActiveShell(), DoNotAskMeAgain.INFORMATION_UPDATE_5_2_10, message);
                        return Status.OK_STATUS;
                    }
                }.schedule();
            }
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
