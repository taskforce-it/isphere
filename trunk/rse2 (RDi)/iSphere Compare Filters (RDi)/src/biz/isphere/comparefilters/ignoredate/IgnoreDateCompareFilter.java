/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.comparefilters.ignoredate;

import java.util.HashMap;

import org.eclipse.compare.ICompareFilter;
import org.eclipse.compare.ITypedElement;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.IRegion;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.comparefilters.preferences.Preferences;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.compareeditor.CompareDialog;

public class IgnoreDateCompareFilter implements ICompareFilter {

    private boolean isEnabled;

    public IgnoreDateCompareFilter() {
        return;
    }

    public boolean canCacheFilteredRegions() {
        return true;
    }

    public IRegion[] getFilteredRegions(HashMap arg0) {

        if (isEnabled && arg0.get(THIS_LINE) instanceof String) {
            String line = (String)arg0.get(THIS_LINE);
            if (line.length() >= 6) {
                return new IRegion[] { new IgnoredDateRegion() };
            }
        }

        return new IRegion[0];
    }

    public boolean isEnabledInitially() {

        IDialogSettings dialogSettings = ISpherePlugin.getDefault().getDialogSettings().getSection(CompareDialog.DIALOG_SETTINGS);
        if (dialogSettings == null) {
            return false;
        }

        return !dialogSettings.getBoolean(CompareDialog.CONSIDER_DATE_PROPERTY);
    }

    public void setInput(Object input, Object anchestor, Object left, Object right) {

        String anchestorExt = getFileExtension(anchestor);
        String leftExt = getFileExtension(left);
        String rightExt = getFileExtension(right);

        if ((anchestorExt == null || isSupportedFileExtension(anchestorExt)) && isSupportedFileExtension(leftExt)
            && isSupportedFileExtension(rightExt)) {
            isEnabled = true;
        } else {
            isEnabled = false;
        }

        return;
    }

    private boolean isSupportedFileExtension(String fileExtension) {
        return Preferences.getInstance().supportsFileExtension(fileExtension);
    }

    private String getFileExtension(Object node) {

        // if (node instanceof ITypedElement) {
        if (node instanceof biz.isphere.core.compareeditor.CompareNode) {
            return FileHelper.getFileExtension(((ITypedElement)node).getName());
        }

        return null;
    }

    private class IgnoredDateRegion implements IRegion {

        public int getOffset() {
            return 0;
        }

        public int getLength() {
            return 6;
        }
    }
}
