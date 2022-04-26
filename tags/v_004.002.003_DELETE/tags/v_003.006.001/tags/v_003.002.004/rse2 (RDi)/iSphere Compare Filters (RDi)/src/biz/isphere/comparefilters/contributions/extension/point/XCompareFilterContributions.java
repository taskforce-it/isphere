/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.comparefilters.contributions.extension.point;

import biz.isphere.comparefilters.preferences.Preferences;
import biz.isphere.core.comparefilter.contributions.extension.point.ICompareFilterContributions;

/**
 * This class connects to the
 * <i>biz.isphere.core.ibmi.contributions.extension.point
 * .IIBMiHostContributions</i> extension point of the <i>iSphere Core
 * Plugin</i>.
 * 
 * @author Thomas Raddatz
 */
public class XCompareFilterContributions implements ICompareFilterContributions {

    public String[] getFileExtensions() {
        return Preferences.getInstance().getFileExtensions();
    }

    public String[] getDefaultFileExtensions() {
        return Preferences.getInstance().getDefaultFileExtensions();
    }

    public void setFileExtensions(String[] extensions) {
        Preferences.getInstance().setFileExtensions(extensions);
    }

    public String getImportExportLocation() {
        return Preferences.getInstance().getImportExportLocation();
    }

    public void setImportExportLocation(String location) {
        Preferences.getInstance().setImportExportLocation(location);
    }
}
