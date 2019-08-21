/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.jface.dialogs;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import biz.isphere.base.internal.DialogSettingsManager;

public abstract class XDialogPage extends DialogPage {
    
    private DialogSettingsManager dialogSettingsManager = null;

    protected XDialogPage() {
        initializeDialogSettingsManager();
    }
    
    protected XDialogPage(String title) {
        super(title);
        initializeDialogSettingsManager();
    }
    
    protected XDialogPage(String title, ImageDescriptor image) {
        super(title, image);
        initializeDialogSettingsManager();
    }

    /**
     * Initializes the dialog settings manager of this dialog.
     */
    private void initializeDialogSettingsManager() {
        dialogSettingsManager = new DialogSettingsManager(getDialogSettings());
    }

    /**
     * Retrieves the screen value that was last displayed on the dialog.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the screen value that was last shown
     */
    protected String loadValue(String aKey, String aDefault) {
        return dialogSettingsManager.loadValue(aKey, aDefault);
    }

    /**
     * Stores a given string value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    protected void storeValue(String aKey, String aValue) {
        dialogSettingsManager.storeValue(aKey, aValue);
    }

    /**
     * Retrieves the screen value that was last displayed on the dialog.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the screen value that was last shown
     */
    protected boolean loadBooleanValue(String aKey, boolean aDefault) {
        return dialogSettingsManager.loadBooleanValue(aKey, aDefault);
    }

    /**
     * Stores a given boolean value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    protected void storeValue(String aKey, boolean aValue) {
        dialogSettingsManager.storeValue(aKey, aValue);
    }

    /**
     * Stores a given numeric value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    protected void storeValue(String aKey, int aValue) {
        dialogSettingsManager.storeValue(aKey, aValue);
    }

    /**
     * Retrieves the the value that is assigned to a given key.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the value that is assigned to the key
     */
    protected int loadIntValue(String aKey, int aDefault) {
        return dialogSettingsManager.loadIntValue(aKey, aDefault);
    }

    /**
     * A plug-in that wants to use the XDialogPage class must implement
     * {@link XDialogPage#getPlugin()} as shown in the example below.
     * <p>
     * Example:
     * 
     * <pre>
     * protected AbstractUIPlugin getPlugin() {
     *     return Activator.getDefault());
     * }
     * </pre>
     * 
     * @return settings the dialog settings used to store the dialog's location
     *         and/or size, or null if the dialog's bounds should never be
     *         stored.
     */
    protected IDialogSettings getDialogSettings() {
        IDialogSettings workbenchSettings = getPlugin().getDialogSettings();
        if (workbenchSettings == null) {
            throw new IllegalArgumentException("Parameter 'workbenchSettings' must not be null.");
        }
        String sectionName = getClass().getName();
        IDialogSettings dialogSettings = workbenchSettings.getSection(sectionName);
        if (dialogSettings == null) {
            dialogSettings = workbenchSettings.addNewSection(sectionName);
        }
        return dialogSettings;
    }

    /**
     * Returns the plug-in the dialog belongs to. Must be implemented by the
     * class that uses XDialogPage.
     * 
     * @return plug-in, the dialog belongs to
     */
    protected abstract AbstractUIPlugin getPlugin();

}
