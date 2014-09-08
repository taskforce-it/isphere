/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

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
    private String currentVersionToShow;
    private String currentVersion;
    private String availableVersionToShow;
    private String availableVersion;
    
	public SearchForUpdates(boolean showResultAlways) {
		super(Messages.iSphere_Search_for_updates);
		this.showResultAlways = showResultAlways;
	}

	protected IStatus run(IProgressMonitor monitor) {

	    newVersionAvailable = false;
        
        try {
            URL url = new URL(Preferences.getInstance().getURLForUpdates());
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
                ((HttpURLConnection)connection).setRequestMethod("GET");
            }
            String[] propertyValues = getPropertyValues(connection.getInputStream(), "Bundle-Version");
            if (propertyValues != null && propertyValues.length == 1) {
                currentVersionToShow = ISpherePlugin.getDefault().getVersion();
                currentVersion = ISphereHelper.comparableVersion(currentVersionToShow);
                availableVersionToShow = propertyValues[0];
                availableVersion = ISphereHelper.comparableVersion(availableVersionToShow);
                if (currentVersion.compareTo(availableVersion) != 0) {
                    newVersionAvailable = true;
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        if (showResultAlways || newVersionAvailable) {
            
            if (!showResultAlways) {
                String lastVersion = Preferences.getInstance().getLastVersionForUpdates();
                if (lastVersion.compareTo(availableVersion) != 0) {
                    new UIJob("ISPHERE_UPDATES") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                            UpdatesNotifierDialog dialog = new UpdatesNotifierDialog(
                                parent, 
                                "iSphere", 
                                null, 
                                Messages.There_is_a_new_version_available + "\n" + 
                                    Messages.Current_version + ": " + currentVersionToShow + "\n" + 
                                    Messages.Available_version + ": " + availableVersionToShow,
                                MessageDialog.INFORMATION, 
                                new String[] {Messages.OK},
                                0,
                                availableVersion);
                            dialog.open();
                            return Status.OK_STATUS;
                        }
                    }.schedule();
                }
            }
            else {
                new UIJob("ISPHERE_UPDATES") {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                        MessageBox tMessageBox = new MessageBox(parent, SWT.ICON_INFORMATION);
                        tMessageBox.setText("iSphere");
                        if (newVersionAvailable) {
                            tMessageBox.setMessage(Messages.There_is_a_new_version_available);
                        }
                        else {
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

    private String[] getPropertyValues(InputStream manifestStream, String property) {
        String[] propertyValues = null;
        try {
            Manifest manifest = new Manifest(manifestStream);
            Properties prop = _manifestToProperties(manifest.getMainAttributes());
            String requires = prop.getProperty(property);
            if(requires != null) {
                ManifestElement elements[] = ManifestElement.parseHeader(property, requires);
                propertyValues = new String[elements.length];
                for(int idx = 0; idx < elements.length; idx++) {
                    propertyValues[idx] = elements[idx].getValue();
                }
            }
        }
        catch (Exception e) { 
            e.printStackTrace();
        }
        finally {
            try {
                if(manifestStream != null) {
                    manifestStream.close();
                }
            }
            catch(Exception e) { 
                e.printStackTrace();
            }
        }
        return propertyValues;
    }

    private Properties _manifestToProperties(Attributes d) {
        Iterator<?> iter = d.keySet().iterator();
        Properties result = new Properties();
        Attributes.Name key;
        for(; iter.hasNext(); result.put(key.toString(), d.get(key)))
            key = (Attributes.Name)iter.next();

        return result;
    }

}
