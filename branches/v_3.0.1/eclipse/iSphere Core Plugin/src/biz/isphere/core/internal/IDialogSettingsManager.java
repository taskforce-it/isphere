/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.IDialogSettings;

import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.base.jface.dialogs.XDialogPage;
import biz.isphere.core.ISpherePlugin;

/**
 * Dialog settings manager that provides a dedicated settings section for the
 * class it is created for.
 * <p>
 * This settings manager is intended to be used everywhere, where a dedicated
 * settings section is needed.
 * <p>
 * <b>Notice:</b><br>
 * Do not use the <code>IDialogSettingsManager</code> for objects that inherit
 * from {@link XDialog} or {@link XDialogPage}. These classes do already provide
 * methods for loading and storing settings data.
 * 
 * @author Thomas Raddatz
 */
public class IDialogSettingsManager {

    private DialogSettingsManager dialogSettingsManager = null;

    public IDialogSettingsManager(Class<?> clazz) {

        dialogSettingsManager = new DialogSettingsManager(getDialogSettings(clazz));
    }

    /**
     * Retrieves the screen value that was last displayed on the dialog.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the screen value that was last shown
     */
    public String loadValue(String aKey, String aDefault) {
        return dialogSettingsManager.loadValue(aKey, aDefault);
    }

    /**
     * Stores a given string value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, String aValue) {
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
    public boolean loadBooleanValue(String aKey, boolean aDefault) {
        return dialogSettingsManager.loadBooleanValue(aKey, aDefault);
    }

    /**
     * Stores a given boolean value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, boolean aValue) {
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
    public int loadIntValue(String aKey, int aDefault) {
        return dialogSettingsManager.loadIntValue(aKey, aDefault);
    }

    /**
     * Stores a given integer value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, int aValue) {
        dialogSettingsManager.storeValue(aKey, aValue);
    }

    private IDialogSettings getDialogSettings(Class<?> clazz) {

        IDialogSettings workbenchSettings = ISpherePlugin.getDefault().getDialogSettings();

        String sectionName = clazz.getName();
        IDialogSettings dialogSettings = workbenchSettings.getSection(sectionName);
        if (dialogSettings == null) {
            dialogSettings = workbenchSettings.addNewSection(sectionName);
        }
        return dialogSettings;
    }
}
