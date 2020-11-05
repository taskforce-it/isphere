/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.jface.dialogs;

import java.util.Date;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.ISphereBasePlugin;
import biz.isphere.base.internal.DialogSettingsManager;

/**
 * Special <i>Dialog</i> class that automatically saves and restores its state.
 * A plug-in that wants to use XDialog must override
 * {@link #getDialogBoundsSettings(IDialogSettings)} instead of
 * {@link #getDialogBoundsSettings()} to activate the save and restore settings
 * feature.
 * <p>
 * In order to set a default size, the dialog class must override
 * {@link #getDefaultSize()}. Overriding {@link #getInitialSize()} does not
 * work!
 * <p>
 * The settings file (<i>dialog_settings.xml</i>) is stored in directory
 * <code>[workspaces]\.metadata\.plugins\package.of.plugin\</code>.
 * <p>
 * This class has been inspired by Blog entry "Default Window Sizes in JFace" of
 * Marian Schedenig at {@link http
 * ://marian.schedenig.name/2012/07/01/default-window-sizes-in-jface/}.
 */
public class XDialog extends Dialog {

    /** These are copied from Dialog class, where they are private. */
    public static final String DIALOG_FONT_DATA = "DIALOG_FONT_NAME"; //$NON-NLS-1$

    public static final String DIALOG_WIDTH = "DIALOG_WIDTH"; //$NON-NLS-1$

    public static final String DIALOG_HEIGHT = "DIALOG_HEIGHT"; //$NON-NLS-1$

    private DialogSettingsManager dialogSettingsManager = null;
    private StatusLineManager statusLineManager = null;
    private Point minimalSize = null;
    private Point defaultMinimalSize = null;

    /**
     * {@inheritDoc}
     */
    protected XDialog(Shell parentShell) {
        super(parentShell);
        initializeDialogSettingsManager();
        setStyleResizable();
    }

    /**
     * {@inheritDoc}
     */
    protected XDialog(IShellProvider parentShell) {
        super(parentShell);
        initializeDialogSettingsManager();
        setStyleResizable();
    }

    @Override
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);
        setFocus();
        return control;
    }

    public void setFocus() {
    }

    protected void createStatusLine(Control parent) {
        if (parent instanceof Composite) {
            Composite composite = (Composite)parent;
            statusLineManager = new StatusLineManager();
            statusLineManager.createControl(composite, SWT.NONE);
            Control statusLine = statusLineManager.getControl();
            Layout layout = composite.getLayout();
            if (layout instanceof GridLayout) {
                GridLayout gridLayout = (GridLayout)layout;
                statusLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, gridLayout.numColumns, 1));
            }
        }
    }

    protected void setErrorMessage(String errorMessage) {
        if (statusLineManager != null) {
            if (errorMessage != null) {
                statusLineManager.setErrorMessage(ISphereBasePlugin.getDefault().getImageRegistry().get(ISphereBasePlugin.IMAGE_ERROR), errorMessage);
            } else {
                statusLineManager.setErrorMessage(null, null);
            }
        }
    }

    protected void setStatusMessage(String message) {
        if (statusLineManager != null) {
            if (message != null) {
                statusLineManager.setMessage(null, message);
            } else {
                statusLineManager.setMessage(null, null);
            }
        }
    }

    /**
     * Initializes the dialog settings manager of this dialog.
     */
    private void initializeDialogSettingsManager() {
        dialogSettingsManager = new DialogSettingsManager(getDialogBoundsSettings());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Mostly a copy of the same method in Dialog, but with a call to a separate
     * method for providing a default size that is used if no persisted dialog
     * settings are available.
     * 
     * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
     */
    @Override
    protected Point getInitialSize() {
        Point result = getDefaultSize();
        if (!isStyleResizable()) {
            return result;
        }

        // Check the dialog settings for a stored size.
        if ((getDialogBoundsStrategy() & DIALOG_PERSISTSIZE) != 0) {
            IDialogSettings settings = getDialogBoundsSettings();

            if (settings != null) {
                // Check that the dialog font matches the font used
                // when the bounds was stored. If the font has changed,
                // we do not honor the stored settings.
                // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=132821
                boolean useStoredBounds = true;
                String previousDialogFontData = settings.get(DIALOG_FONT_DATA);

                // There is a previously stored font, so we will check it.
                // Note that if we haven't stored the font before, then we will
                // use the stored bounds. This allows restoring of dialog bounds
                // that were stored before we started storing the fontdata.
                if (previousDialogFontData != null && previousDialogFontData.length() > 0) {
                    FontData[] fontDatas = JFaceResources.getDialogFont().getFontData();

                    if (fontDatas.length > 0) {
                        String currentDialogFontData = fontDatas[0].toString();
                        useStoredBounds = currentDialogFontData.equalsIgnoreCase(previousDialogFontData);
                    }
                }

                if (useStoredBounds) {
                    try {
                        // Get the stored width and height.
                        int width = settings.getInt(DIALOG_WIDTH);

                        if (width != DIALOG_DEFAULT_BOUNDS) {
                            result.x = width;
                        }

                        int height = settings.getInt(DIALOG_HEIGHT);

                        if (height != DIALOG_DEFAULT_BOUNDS) {
                            result.y = height;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }

        result = ensureMinimalHeight(result);
        result = ensureMinimalWidth(result);

        // No attempt is made to constrain the bounds. The default
        // constraining behavior in Window will be used.
        return result;
    }

    private Point ensureMinimalHeight(Point result) {

        if (getMinimalSize().y == SWT.DEFAULT) {
            return result;
        }

        result.y = Math.max(getMinimalSize().y, result.y);
        return result;
    }

    private Point ensureMinimalWidth(Point result) {

        if (getMinimalSize().x == SWT.DEFAULT) {
            return result;
        }

        result.x = Math.max(getMinimalSize().x, result.x);
        return result;
    }

    public Point getMinimalSize() {
        return getDefaultMinimalSize();
    }

    private Point getDefaultMinimalSize() {

        if (defaultMinimalSize == null) {
            defaultMinimalSize = new Point(SWT.DEFAULT, SWT.DEFAULT);
        }

        return defaultMinimalSize;
    }

    /**
     * A plug-in that wants to use the XDialog class must override
     * {@link Dialog#getDialogBoundsSettings()} as shown in the example below.
     * Otherwise all dialogs share section <i>Workbench</i> and hence overwrite
     * their settings.
     * <p>
     * Example:
     * 
     * <pre>
     * protected IDialogSettings getDialogBoundsSettings() {
     *     return super.getDialogBoundsSettings(Activator.getDefault().getDialogSettings());
     * }
     * </pre>
     * 
     * @param - workbenchSettings the <i>Workbench</i> section of the dialog
     *        settings.
     * @return settings the dialog settings used to store the dialog's location
     *         and/or size, or null if the dialog's bounds should never be
     *         stored.
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
     * Provides the dialog's default size. Duplicates the behavior of JFace's
     * standard dialog. Subclasses may override.
     * <p>
     * this method replaces
     * {@link org.eclipse.jface.dialogs.Dialog#getInitialSize()}.
     * 
     * @return the initial size of the shell
     */
    protected Point getDefaultSize() {
        return getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

    /**
     * Returns a boolean indicating whether the dialog should be considered
     * resizable when the shell style is initially set. This method is used to
     * ensure that all style bits appropriate for resizable dialogs are added to
     * the shell style. Individual dialogs may always set the shell style to
     * ensure that a dialog is resizable, but using this method ensures that
     * resizable dialogs will be created with the same set of style bits. Style
     * bits will never be removed based on the return value of this method. For
     * example, if a dialog returns false, but also sets a style bit for a
     * SWT.RESIZE border, the style bit will be honored.
     * <p>
     * Added, because this method is missing for WDSC 7.0.
     * 
     * @return a boolean indicating whether the dialog is resizable and should
     *         have the default style bits for resizable dialogs
     */
    protected boolean isResizable() {
        return false;
    }

    /**
     * Code of the original implementation of class
     * {@link org.eclipse.jface.dialogs.Dialog}.
     * <p>
     * Added, to support {@link #isResizable()}, which is missing in WDSC 7.0.
     */
    private void setStyleResizable() {
        if (isResizable()) {
            setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE | getDefaultOrientation());
        } else {
            setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation());
        }
    }

    /**
     * Checks the style of the shell to determine whether or not the dialog is
     * resizable.
     */
    private boolean isStyleResizable() {
        if ((getShellStyle() & SWT.RESIZE) == SWT.RESIZE) {
            return true;
        }
        return false;
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
    protected Date loadDateValue(String aKey, Date aDefault) {
        return dialogSettingsManager.loadDateValue(aKey, aDefault);
    }

    /**
     * Stores a given java.util.Date value to preserve it for the next time the
     * dialog is shown.
     * 
     * @param aKey - key, the value is assigned to
     * @param aValue - the screen value that is stored
     */
    protected void storeValue(String aKey, Date aValue) {
        dialogSettingsManager.storeValue(aKey, aValue);
    }
}
