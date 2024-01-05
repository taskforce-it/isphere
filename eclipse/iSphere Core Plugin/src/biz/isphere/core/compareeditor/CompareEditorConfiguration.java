/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.rangedifferencer.RangeDifference;

import biz.isphere.core.preferences.Preferences;

/**
 * Class to store the compare configuration values of the Source Compare Dialog
 * and task. The following properties are set in class
 * <code>biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler</code>,
 * if the <i>Compare Source Members</i> dialog is shown (property: <b>show
 * dialog</b>):
 * <ul>
 * <li>left editable</li>
 * <li>right editable</li>
 * <li>consider date</li>
 * <li>ignore case</li>
 * <li>ignore changes left</li>
 * <li>ignore changes right</li>
 * <li>three way</li>
 * </ul>
 * The following properties are always set in class
 * <code>biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler</code>:
 * <ul>
 * <li>drop sequence numbers and date fields</li>
 * </ul>
 */
public class CompareEditorConfiguration extends CompareConfiguration {

    private static String EDIT_MODE = "biz.isphere.core.compareeditor.editMode"; //$NON-NLS-1$
    private static String SHOW_DIALOG = "biz.isphere.core.compareeditor.showDialog"; //$NON-NLS-1$
    private static String OPEN_IN_EDITOR = "biz.isphere.core.compareeditor.openInEditor"; //$NON-NLS-1$
    private static String CONSIDER_DATE = "biz.isphere.core.compareeditor.considerDate"; //$NON-NLS-1$
    private static String IGNORE_CASE = "biz.isphere.core.compareeditor.ignoreCase"; //$NON-NLS-1$
    private static String IGNORE_CHANGES_LEFT = "biz.isphere.core.compareeditor.ignoreChangesLeft"; //$NON-NLS-1$
    private static String IGNORE_CHANGES_RIGHT = "biz.isphere.core.compareeditor.ignoreChangesRight"; //$NON-NLS-1$
    private static String THREE_WAY = "biz.isphere.core.compareeditor.threeWay"; //$NON-NLS-1$
    private static String SEQUENCE_NUMBERS_AND_DATE_FIELDS = "biz.isphere.core.compareeditor.sequenceNumbersAndDates"; //$NON-NLS-1$

    /**
     * Produces a new CompareEditorConfiguration object.
     */
    public CompareEditorConfiguration() {
        setIgnoreCase(false);
        setIgnoreChangesLeft(false);
        setIgnoreChangesRight(false);
        setConsiderDate(false);
        setThreeWay(false);
        setLeftEditable(false);
        setRightEditable(false);
        setOpenInEditor(true);

        setProperty(CompareConfiguration.IGNORE_WHITESPACE, Preferences.getInstance().isSourceMemberCompareIgnoreWhiteSpaces());
    }

    /**
     * Returns true if the case is not considered, else case matters.
     * 
     * @return true if the case is not considered; false otherwise
     */
    public boolean isIgnoreCase() {
        return ((Boolean)getProperty(IGNORE_CASE)).booleanValue();
    }

    /**
     * Specifies whether or not the case is considered when comparing members.
     * 
     * @param anIgnoreCase - true, case is considered, else case does not matter
     */
    public void setIgnoreCase(boolean anIgnoreCase) {
        setProperty(IGNORE_CASE, anIgnoreCase);
    }

    /**
     * Returns true if the changes of the left member are ignored.
     * 
     * @return true if left changes are ignored; false otherwise
     */
    public boolean isIgnoreChangesLeft() {
        return ((Boolean)getProperty(IGNORE_CHANGES_LEFT)).booleanValue();
    }

    /**
     * Specifies whether or not changes of the left member are ignored.
     * 
     * @param anIgnoreChangesLeft - true, left changes are ignored, else not
     *        ignored
     */
    public void setIgnoreChangesLeft(boolean anIgnoreChangesLeft) {
        invokeSetChangeIgnored(RangeDifference.LEFT, anIgnoreChangesLeft);
        setProperty(IGNORE_CHANGES_LEFT, anIgnoreChangesLeft);
    }

    /**
     * Returns true if the changes of the right member are ignored.
     * 
     * @return true if right changes are ignored; false otherwise
     */
    public boolean isIgnoreChangesRight() {
        return ((Boolean)getProperty(IGNORE_CHANGES_RIGHT)).booleanValue();
    }

    /**
     * Specifies whether or not changes of the right member are ignored.
     * 
     * @param anIgnoreChangesRight - true, right changes are ignored, else not
     *        ignored
     */
    public void setIgnoreChangesRight(boolean anIgnoreChangesRight) {
        invokeSetChangeIgnored(RangeDifference.RIGHT, anIgnoreChangesRight);
        setProperty(IGNORE_CHANGES_RIGHT, anIgnoreChangesRight);
    }

    /**
     * Returns true if method <code>setChangeIgnored</code> of class
     * {@link #CompareEditorConfiguration()} is available.
     * 
     * @return true if method is available; false otherwise
     */
    public static boolean isMethodSetChangeIgnoredAvailable() {
        try {
            if (CompareConfiguration.class.getDeclaredMethod("setChangeIgnored", int.class, boolean.class) != null) {
                return true;
            }
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    /**
     * Invokes method <code>setChangeIgnored</code> of class
     * {@link #CompareEditorConfiguration()}.
     */
    private void invokeSetChangeIgnored(int who, boolean ignore) {
        if (CompareEditorConfiguration.isMethodSetChangeIgnoredAvailable()) {
            try {
                Method _method = this.getClass().getMethod("setChangeIgnored", int.class, boolean.class);
                if (_method != null) {
                    try {
                        _method.invoke(this, new Object[] { who, ignore });
                    } catch (IllegalArgumentException e) {
                        System.out.println("IllegalArgumentException received while invoked method setChangeIgnored in class CompareConfiguration.");
                    } catch (IllegalAccessException e) {
                        System.out.println("IllegalAccessException received while invoked method setChangeIgnored in class CompareConfiguration.");
                    } catch (InvocationTargetException e) {
                        System.out.println("InvocationTargetException received while invoked method setChangeIgnored in class CompareConfiguration.");
                    }
                }
            } catch (SecurityException e) {
                System.out.println("Method setChangeIgnored not accessable in class CompareConfiguration.");
            } catch (NoSuchMethodException e) {
                System.out.println("Method setChangeIgnored not available in class CompareConfiguration.");
            }
        }
    }

    /**
     * Returns true if the the last changed date of the source lines is
     * compared.
     * 
     * @return true if date is compared; false otherwise
     */
    public boolean isConsiderDate() {
        return ((Boolean)getProperty(CONSIDER_DATE)).booleanValue();
    }

    /**
     * Specifies whether or not the last changed date of the source lines is
     * compared.
     * 
     * @param aConsiderDate - true, case is compared, else date is ignored
     */
    public void setConsiderDate(boolean aConsiderDate) {
        setProperty(CONSIDER_DATE, aConsiderDate);
    }

    /**
     * Returns true if three-way compare is requested.
     * 
     * @return true if three-way is required; false otherwise
     */
    public boolean isThreeWay() {
        return ((Boolean)getProperty(THREE_WAY)).booleanValue();
    }

    /**
     * Specifies whether or not the last changed date of the source lines is
     * compared.
     * 
     * @param aThreeWay - true, editor is started in three-way mode, else
     *        two-way mode
     */
    public void setThreeWay(boolean aThreeWay) {
        setProperty(THREE_WAY, aThreeWay);
    }

    /**
     * Specifies whether or not sequence numbers and dates are dropped. This
     * property is set in class
     * <code>biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler</code>.
     * 
     * @param dropped - true, sequence numbers and dates are dropped
     */
    public void setDropSequenceNumbersAndDateFields(boolean dropped) {
        setProperty(SEQUENCE_NUMBERS_AND_DATE_FIELDS, dropped);
    }

    /**
     * Returns true if sequence number and date fields are dropped.
     * 
     * @return true if sequence number and date fields are dropped; false
     *         otherwise
     */
    public boolean dropSequenceNumbersAndDateFields() {
        return ((Boolean)getProperty(SEQUENCE_NUMBERS_AND_DATE_FIELDS)).booleanValue();
    }

    /**
     * Returns true if the editor is displayed on the active workbench page.
     * 
     * @return true if editor is displayed on the active workbench page; false
     *         otherwise
     */
    public boolean isOpenInEditor() {
        return ((Boolean)getProperty(OPEN_IN_EDITOR)).booleanValue();
    }

    /**
     * Specifies whether or not the compare editor is displayed on the active
     * workbench page. The default value is true.
     * 
     * @param enabled - true, editor is displayed on the active workbench page,
     *        else it is displayed in a popup window
     */
    public void setOpenInEditor(boolean enabled) {
        setProperty(OPEN_IN_EDITOR, enabled);
    }

    /**
     * Returns true if the <i>Compare Source Members</i> dialog is displayed.
     * 
     * @return true if the <i>Compare Source Members</i> dialog is displayed;
     *         false otherwise
     */
    public boolean isShowDialog() {
        return ((Boolean)getProperty(SHOW_DIALOG)).booleanValue();
    }

    /**
     * Specifies whether or not the <i>Compare Source Members</i> dialog is
     * displayed.
     * 
     * @param enabled - true, <i>Compare Source Members</i> is displayed, false
     *        otherwise
     */
    public void setShowDialog(boolean enabled) {
        setProperty(SHOW_DIALOG, enabled);
    }

    /**
     * Returns true if the <i>Compare Source Members</i> dialog is displayed in
     * <b>edit</b> mode.
     * 
     * @return true if the <i>Compare Source Members</i> dialog is displayed in
     *         <b>edit</b> mode; false otherwise;
     */
    public boolean isEditMode() {
        return ((Boolean)getProperty(EDIT_MODE)).booleanValue();
    }

    /**
     * Specifies whether or not the <i>Compare Source Members</i> dialog is
     * displayed in <b>edit</b> mode. This parameter is ignored if the
     * <i>Compare Source Members</i> dialog is not shown.
     * 
     * @param enabled - true, <i>Compare Source Members</i> is displayed in
     *        <b>edit</b> mode, false otherwise
     */
    public void setEditMode(boolean enabled) {
        setProperty(EDIT_MODE, enabled);
    }

    // TODO: remove this method. used for WDSC only.
    // TODO: refactor biz.isphere.core.compareeditor.CompareNode
    public boolean hasCompareFilters() {
        return true;
    }
}
