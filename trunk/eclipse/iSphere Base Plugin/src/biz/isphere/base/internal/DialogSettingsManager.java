/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.IDialogSettings;

public class DialogSettingsManager {

    private IDialogSettings dialogSettings;
    private Class<?> section;

    private SimpleDateFormat dateFormatter;

    public DialogSettingsManager(IDialogSettings aDialogSettings) {
        this(aDialogSettings, null);
    }

    public DialogSettingsManager(IDialogSettings aDialogSettings, Class<?> section) {
        this.dialogSettings = aDialogSettings;
        this.section = section;
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
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
        String tValue = getDialogSettings().get(aKey);
        if (StringHelper.isNullOrEmpty(tValue)) {
            tValue = aDefault;
        }
        return tValue;
    }

    /**
     * Stores a given string value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, String aValue) {
        getDialogSettings().put(aKey, aValue);
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
        String tValue = getDialogSettings().get(aKey);
        return BooleanHelper.tryParseBoolean(tValue, aDefault);
    }

    /**
     * Stores a given boolean value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, boolean aValue) {
        getDialogSettings().put(aKey, aValue);
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
        return IntHelper.tryParseInt(getDialogSettings().get(aKey), aDefault);
    }

    /**
     * Stores a given integer value to preserve it for the next time the dialog
     * is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, int aValue) {
        getDialogSettings().put(aKey, aValue);
    }

    /**
     * Retrieves the the value that is assigned to a given key.
     * 
     * @param aKey - key, that is used to retrieve the value from the store
     * @param aDefault - default value, that is returned if then key does not
     *        yet exist
     * @return the value that is assigned to the key
     */
    public Date loadDateValue(String aKey, Date aDefault) {

        String tValue = getDialogSettings().get(aKey);
        if (StringHelper.isNullOrEmpty(tValue)) {
            return aDefault;
        }

        try {
            Date tDate = dateFormatter.parse(tValue);
            return tDate;
        } catch (Exception e) {
            return aDefault;
        }
    }

    /**
     * Stores a given java.util.Date value to preserve it for the next time the
     * dialog is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    public void storeValue(String aKey, Date aValue) {
        String value = dateFormatter.format(aValue);
        getDialogSettings().put(aKey, value);
    }

    /**
     * Returns the dialog settings store.
     * 
     * @return dialog settings
     */
    private IDialogSettings getDialogSettings() {

        if (section == null) {
            return dialogSettings;
        }

        String sectionName = section.getName();
        IDialogSettings dialogSectionSettings = dialogSettings.getSection(sectionName);
        if (dialogSectionSettings == null) {
            dialogSectionSettings = dialogSettings.addNewSection(sectionName);
        }

        return dialogSectionSettings;
    }

}
