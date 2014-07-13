/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import org.eclipse.compare.CompareConfiguration;

/**
 * Class to store the compare configuration values of the Source Compare Dialog
 * and task.
 */
public class CompareEditorConfiguration extends CompareConfiguration {

    private static String CONSIDER_DATE = "biz.isphere.core.compareeditor.considerDate";

    private static String IGNORE_CASE = "biz.isphere.core.compareeditor.ignoreCase";

    private static String THREE_WAY = "biz.isphere.core.compareeditor.threeWay";

    public CompareEditorConfiguration() {
        setIgnoreCase(false);
        setConsiderDate(false);
        setThreeWay(false);

        setProperty(CompareConfiguration.IGNORE_WHITESPACE, new Boolean(true));
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

    public void setThreeWay(boolean aThreeWay) {
        setProperty(THREE_WAY, aThreeWay);
    }

}
