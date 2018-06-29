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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;

public class SearchForUpdates extends Job {

    private boolean showResultAlways;
    private boolean newVersionAvailable;
    private Version currentVersion;
    private Version availableVersion;

    public SearchForUpdates(boolean showResultAlways) {
        super(Messages.iSphere_Search_for_updates);
        this.showResultAlways = showResultAlways;
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

                URL url = new URL(Preferences.getInstance().getURLForUpdates());
                URLConnection connection = url.openConnection();
                if (connection instanceof HttpURLConnection) {
                    ((HttpURLConnection)connection).setRequestMethod("GET");
                }

                Manifest manifest = readManifest(connection.getInputStream());
                availableVersion = getVersion(manifest, "Bundle-Version");

                currentVersion = new Version(ISpherePlugin.getDefault().getVersion());
                if (availableVersion != null && availableVersion.compareTo(currentVersion) > 0) {
                    newVersionAvailable = true;
                }

                if (!newVersionAvailable && (Preferences.getInstance().isSearchForBetaVersions()) || showResultAlways) {
                    availableVersion = getVersion(manifest, "X-Beta-Version");
                    if (availableVersion != null && availableVersion.compareTo(currentVersion) > 0) {
                        newVersionAvailable = true;
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
                    lastVersion = new Version("0");
                }
                if (lastVersion.compareTo(availableVersion) != 0) {
                    new UIJob("ISPHERE_UPDATES") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                            UpdatesNotifierDialog dialog = new UpdatesNotifierDialog(parent, "iSphere", null, getNewVersionText(currentVersion,
                                availableVersion), MessageDialog.INFORMATION, new String[] { Messages.OK }, 0, availableVersion.toString());
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
                            tMessageBox.setMessage(getNewVersionText(currentVersion, availableVersion));
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

    private String getNewVersionText(Version currentVersion, Version availableVersion) {

        String text;
        if (availableVersion.isBeta()) {
            text = Messages.There_is_a_new_beta_version_available;
        } else {
            text = Messages.There_is_a_new_version_available;
        }

        text = text + "\n" + Messages.Current_version + ": " + currentVersion + "\n" + Messages.Available_version + ": " + availableVersion;
        return text;
    }

    private Version getVersion(Manifest manifest, String version) {

        String[] propertyValues = getPropertyValues(manifest, version);
        if (propertyValues != null && propertyValues.length == 1) {
            return new Version(propertyValues[0]);
        }

        return null;
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
            e.printStackTrace();
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
