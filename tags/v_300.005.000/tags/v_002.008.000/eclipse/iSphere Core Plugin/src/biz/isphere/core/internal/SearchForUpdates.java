/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.BooleanHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;

public class SearchForUpdates extends Job {

    private boolean showResultAlways;
    private boolean searchForBetaVersion;
    private URL overriddenURL;

    private boolean newVersionAvailable;
    private String newVersionInfo;
    private boolean newRequiresUpdateLibrary;
    private Version currentVersion;
    private Version availableVersion;

    public SearchForUpdates() {
        this(null, false, Preferences.getInstance().isSearchForBetaVersions());
    }

    public SearchForUpdates(URL url, boolean showResultAlways, boolean searchForBetaVersion) {
        super(Messages.iSphere_Search_for_updates);
        this.showResultAlways = showResultAlways;
        this.searchForBetaVersion = searchForBetaVersion;
        this.overriddenURL = url;
    }

    protected IStatus run(IProgressMonitor monitor) {

        newVersionAvailable = false;

        int numTries;
        if (showResultAlways) {
            numTries = 1;
        } else {
            numTries = 3;
        }

        while (numTries > 0) {

            try {

                URL url;
                if (overriddenURL != null) {
                    url = overriddenURL;
                } else {
                    url = new URL(Preferences.getInstance().getURLForUpdates());
                }

                URLConnection connection = url.openConnection();
                if (connection instanceof HttpURLConnection) {
                    ((HttpURLConnection)connection).setRequestMethod("GET");
                }

                currentVersion = new Version(ISpherePlugin.getDefault().getVersion());

                Manifest manifest = readManifest(connection.getInputStream());
                availableVersion = getVersion(manifest, "Bundle-Version");

                if (availableVersion != null && availableVersion.compareTo(currentVersion) > 0) {
                    newVersionAvailable = true;
                    newVersionInfo = getString(manifest, "X-Bundle-Info", true);
                    newRequiresUpdateLibrary = getBoolean(manifest, "X-Bundle-Update-Library", false);
                }

                if (!newVersionAvailable && searchForBetaVersion) {
                    Version availableBetaVersion = getVersion(manifest, "X-Beta-Version");
                    if (availableBetaVersion != null && availableBetaVersion.compareTo(currentVersion) > 0) {
                        availableVersion = availableBetaVersion;
                        newVersionAvailable = true;
                        newVersionInfo = getString(manifest, "X-Beta-Info", true);
                        newRequiresUpdateLibrary = getBoolean(manifest, "X-Beta-Update-Library", false);
                    }
                }

                numTries = 0;

            } catch (Exception e) {
                numTries--;
                if (numTries == 0) {
                    ISpherePlugin.logError(Messages.Failed_to_connect_to_iSphere_update_server, e);
                    if (showResultAlways) {
                        new UIJob("ISPHERE_UPDATES") {
                            @Override
                            public IStatus runInUIThread(IProgressMonitor monitor) {
                                Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                                MessageDialog.openError(parent, "iSphere", Messages.Failed_to_connect_to_iSphere_update_server);
                                return Status.OK_STATUS;
                            }
                        }.schedule();
                    }
                    return Status.OK_STATUS;
                }
            }
        }

        if (showResultAlways || newVersionAvailable) {

            if (!showResultAlways) {
                Version lastVersion;
                try {
                    lastVersion = new Version(Preferences.getInstance().getLastVersionForUpdates());
                } catch (IllegalArgumentException e) {
                    lastVersion = null;
                }
                if (lastVersion == null || lastVersion.compareTo(availableVersion) != 0) {
                    new UIJob("ISPHERE_UPDATES") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                            UpdatesNotifierDialog dialog = new UpdatesNotifierDialog(parent, "iSphere", null, getNewVersionText(currentVersion,
                                availableVersion, newRequiresUpdateLibrary, newVersionInfo), MessageDialog.INFORMATION, new String[] { Messages.OK },
                                0, availableVersion.toString());
                            dialog.open();
                            return Status.OK_STATUS;
                        }
                    }.schedule();
                }
            } else {
                new UIJob("ISPHERE_UPDATES") {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                        MessageBox tMessageBox = new MessageBox(parent, SWT.ICON_INFORMATION);
                        tMessageBox.setText("iSphere");
                        if (newVersionAvailable) {
                            tMessageBox.setMessage(getNewVersionText(currentVersion, availableVersion, newRequiresUpdateLibrary, newVersionInfo));
                        } else {
                            tMessageBox.setMessage(Messages.There_is_no_new_version_available);
                        }
                        tMessageBox.open();
                        return Status.OK_STATUS;
                    }
                }.schedule();
            }

        }

        return Status.OK_STATUS;
    }

    private String getNewVersionText(Version currentVersion, Version availableVersion, boolean requiresUpdateLibrary, String newVersionInfo) {

        StringBuilder text = new StringBuilder();
        if (availableVersion.isBeta()) {
            text.append(Messages.There_is_a_new_beta_version_available);
        } else {
            text.append(Messages.There_is_a_new_version_available);
        }

        text.append("\n");
        text.append(Messages.Current_version);
        text.append(": ");
        text.append(currentVersion);
        text.append("\n");
        text.append(Messages.Available_version);
        text.append(": ");
        text.append(availableVersion);

        if (requiresUpdateLibrary) {
            text.append("\n");
            text.append("\n");
            text.append(Messages.This_version_requires_updating_the_iSphere_library);
        }

        if (newVersionInfo != null) {
            text.append("\n");
            text.append("\n");
            text.append(newVersionInfo);
        }

        return text.toString();
    }

    private Version getVersion(Manifest manifest, String version) {

        String[] propertyValues = getPropertyValues(manifest, version);
        if (propertyValues != null && propertyValues.length == 1) {
            return new Version(propertyValues[0]);
        }

        return null;
    }

    /**
     * Replaces characters that are not allowed in a MANIFEST.MF file. This way
     * we can specify comma and linefeed characters in a manifest file.
     * 
     * <pre>
     * semicolon (;) -> comma (,)
     * </pre>
     * 
     * @param manifest -
     * @param version -
     * @param replaceControlCharacter -
     * @return replaced string
     */
    private String getString(Manifest manifest, String version, boolean replaceControlCharacter) {

        String value = getString(manifest, version);
        if (value == null) {
            return null;
        }

        value = value.replaceAll(";", ", "); //$NON-NLS-1$

        return value;
    }

    private String getString(Manifest manifest, String version) {

        String[] propertyValues = getPropertyValues(manifest, version);
        if (propertyValues != null && propertyValues.length == 1) {
            return propertyValues[0];
        }

        return null;
    }

    private boolean getBoolean(Manifest manifest, String version, boolean defaultValue) {

        String[] propertyValues = getPropertyValues(manifest, version);
        if (propertyValues != null && propertyValues.length == 1) {
            return BooleanHelper.tryParseBoolean(propertyValues[0], defaultValue);
        }

        return defaultValue;
    }

    private Manifest readManifest(InputStream manifestStream) {

        Manifest manifest;
        try {

            manifest = new Manifest(manifestStream);
            return manifest;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (manifestStream != null) {
                try {
                    manifestStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private String[] getPropertyValues(Manifest manifest, String property) {
        String[] propertyValues = null;
        try {
            Properties prop = _manifestToProperties(manifest.getMainAttributes());
            String requires = prop.getProperty(property);
            if (requires != null) {
                ManifestElement elements[] = ManifestElement.parseHeader(property, requires);
                propertyValues = new String[elements.length];
                for (int idx = 0; idx < elements.length; idx++) {
                    propertyValues[idx] = elements[idx].getValue();
                }
            }
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not retrieve property " + property + " from manifest ***", e);
        }
        return propertyValues;
    }

    private Properties _manifestToProperties(Attributes d) {
        Iterator<?> iter = d.keySet().iterator();
        Properties result = new Properties();
        Attributes.Name key;
        for (; iter.hasNext(); result.put(key.toString(), d.get(key)))
            key = (Attributes.Name)iter.next();

        return result;
    }

}
