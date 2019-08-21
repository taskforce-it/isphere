/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import org.eclipse.compare.CompareConfiguration;

import biz.isphere.core.comparefilter.contributions.extension.handler.CompareFilterContributionsHandler;

/**
 * Class to store the compare configuration values of the Source Compare Dialog
 * and task.
 */
public class CompareEditorConfiguration extends CompareConfiguration {

    private static String CONSIDER_DATE = "biz.isphere.core.compareeditor.considerDate"; //$NON-NLS-1$

    private static String IGNORE_CASE = "biz.isphere.core.compareeditor.ignoreCase"; //$NON-NLS-1$

    private static String THREE_WAY = "biz.isphere.core.compareeditor.threeWay"; //$NON-NLS-1$

    private static String SEQUENCE_NUMBERS_AND_DATE_FIELDS = "biz.isphere.core.compareeditor.sequenceNumbersAndDates"; //$NON-NLS-1$

    private boolean hasCompareFilters;

    public CompareEditorConfiguration() {
        setIgnoreCase(false);
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
