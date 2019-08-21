/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.jface.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.base.internal.DialogSettingsManager;

public abstract class XEditorPart extends EditorPart {

    private DialogSettingsManager dialogSettingsManager = null;

    public XEditorPart() {
        initializeDialogSettingsManager();
    }

    /**
     * Initializes the dialog settings manager of this dialog.
     */
    private void initializeDialogSettingsManager() {
        dialogSettingsManager = new DialogSettingsManager(getDialogBoundsSettings());
    }

    /**
     * Gets the editor settings that should be used for remembering persistent
     * editor properties.
     * 
     * @return the editor settings used to store the persistent properties of
     *         the editor part.
     */
    protected IDialogSettings getDialogBoundsSettings() {
        return null;
    }

    /**
     * A plug-in that wants to use the XEditorPart class must override
     * {@link Dialog#getDialogBoundsSettings()} as shown in the example below to
     * provide an IDialogSettings object.
     * <p>
     * Example:
     * 
     * <pre>
     * protected IDialogSettings getDialogBoundsSettings() {
     *     return super.getDialogBoundsSettings(Activator.getDefault().getDialogSettings());
     * }
     * </pre>
     * 
     * @return the editor settings used to store the persistent properties of
     *         the editor part.
     */
    protected IDialogSettings getDialogBoundsSettings(IDialogSettings workbenchSettings) {
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
     * Returns the dialog settings manger of this dialog.
     * 
     * @return dialog settings manager
     */
    protected DialogSettingsManager getDialogSettingsManager() {

        return dialogSettingsManager;
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
}
