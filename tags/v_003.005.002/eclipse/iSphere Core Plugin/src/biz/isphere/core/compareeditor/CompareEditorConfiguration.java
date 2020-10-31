/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
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

import biz.isphere.core.comparefilter.contributions.extension.handler.CompareFilterContributionsHandler;

/**
 * Class to store the compare configuration values of the Source Compare Dialog
 * and task.
 */
public class CompareEditorConfiguration extends CompareConfiguration {

    private static String CONSIDER_DATE = "biz.isphere.core.compareeditor.considerDate"; //$NON-NLS-1$

    private static String IGNORE_CASE = "biz.isphere.core.compareeditor.ignoreCase"; //$NON-NLS-1$

    private static String IGNORE_CHANGES_LEFT = "biz.isphere.core.compareeditor.ignoreChangesLeft"; //$NON-NLS-1$

    private static String IGNORE_CHANGES_RIGHT = "biz.isphere.core.compareeditor.ignoreChangesRight"; //$NON-NLS-1$

    private static String THREE_WAY = "biz.isphere.core.compareeditor.threeWay"; //$NON-NLS-1$

    private static String SEQUENCE_NUMBERS_AND_DATE_FIELDS = "biz.isphere.core.compareeditor.sequenceNumbersAndDates"; //$NON-NLS-1$

    private boolean hasCompareFilters;

    public CompareEditorConfiguration() {
        setIgnoreCase(false);
        setIgnoreChangesLeft(false);
        setIgnoreChangesRight(false);
        setConsiderDate(false);
        setThreeWay(false);
        setLeftEditable(false);
        setRightEditable(false);

        setProperty(CompareConfiguration.IGNORE_WHITESPACE, new Boolean(true));

        hasCompareFilters = CompareFilterContributionsHandler.hasContribution();
    }

    public boolean isIgnoreCase() {
        return ((Boolean)getProperty(IGNORE_CASE)).booleanValue();
    }

    public void setIgnoreCase(boolean anIgnoreCase) {
        setProperty(IGNORE_CASE, anIgnoreCase);
    }

    public boolean isIgnoreChangesLeft() {
        return ((Boolean)getProperty(IGNORE_CHANGES_LEFT)).booleanValue();
    }

    public void setIgnoreChangesLeft(boolean anIgnoreChangesLeft) {
        invokeSetChangeIgnored(RangeDifference.LEFT, anIgnoreChangesLeft); 
        setProperty(IGNORE_CHANGES_LEFT, anIgnoreChangesLeft);
    }

    public boolean isIgnoreChangesRight() {
        return ((Boolean)getProperty(IGNORE_CHANGES_RIGHT)).booleanValue();
    }

    public void setIgnoreChangesRight(boolean anIgnoreChangesRight) {
        invokeSetChangeIgnored(RangeDifference.RIGHT, anIgnoreChangesRight); 
        setProperty(IGNORE_CHANGES_RIGHT, anIgnoreChangesRight);
    }
 
    public static boolean isMethodSetChangeIgnoredAvailable() {
        try {
            if (CompareConfiguration.class.getDeclaredMethod("setChangeIgnored", int.class, boolean.class) != null) {
                return true;
            }
        } 
        catch (SecurityException e) {
        } 
        catch (NoSuchMethodException e) {
        }
        return false;
    }
    
    private void invokeSetChangeIgnored(int who, boolean ignore) {
        if (CompareEditorConfiguration.isMethodSetChangeIgnoredAvailable()) {
            try {
                Method _method = this.getClass().getMethod("setChangeIgnored", int.class, boolean.class);
                if (_method != null) {
                    try {
                        _method.invoke(this, new Object[]{who, ignore});
                    } 
                    catch (IllegalArgumentException e) {
                        System.out.println("IllegalArgumentException received while invoked method setChangeIgnored in class CompareConfiguration.");
                    } 
                    catch (IllegalAccessException e) {
                        System.out.println("IllegalAccessException received while invoked method setChangeIgnored in class CompareConfiguration.");
                    } 
                    catch (InvocationTargetException e) {
                        System.out.println("InvocationTargetException received while invoked method setChangeIgnored in class CompareConfiguration.");
                    }
                }
            } 
            catch (SecurityException e) {
                System.out.println("Method setChangeIgnored not accessable in class CompareConfiguration.");
            } 
            catch (NoSuchMethodException e) {
                System.out.println("Method setChangeIgnored not available in class CompareConfiguration.");
            }
        }
    }
    
    public boolean isConsiderDate() {
        return ((Boolean)getProperty(CONSIDER_DATE)).booleanValue();
    }

    public void setConsiderDate(boolean aConsiderDate) {
        setProperty(CONSIDER_DATE, aConsiderDate);
    }

    public boolean isThreeWay() {
        return ((Boolean)getProperty(THREE_WAY)).booleanValue();
    }

    public void setDropSequenceNumbersAndDateFields(boolean dropped) {
        setProperty(SEQUENCE_NUMBERS_AND_DATE_FIELDS, dropped);
    }

    public boolean dropSequenceNumbersAndDateFields() {
        return ((Boolean)getProperty(SEQUENCE_NUMBERS_AND_DATE_FIELDS)).booleanValue();
    }

    public void setThreeWay(boolean aThreeWay) {
        setProperty(THREE_WAY, aThreeWay);
    }

    public boolean hasCompareFilters() {
        return hasCompareFilters;
    }
}
