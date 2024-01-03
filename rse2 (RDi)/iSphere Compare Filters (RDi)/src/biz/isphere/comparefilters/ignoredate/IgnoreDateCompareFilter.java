/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.comparefilters.ignoredate;

import java.lang.reflect.Field;
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
        return false;
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

    public void setInput(Object input, Object ancestor, Object left, Object right) {

        String ancestorExt = getFileExtension(ancestor);
        String leftExt = getFileExtension(left);
        String rightExt = getFileExtension(right);

        if ((ancestorExt == null || isSupportedFileExtension(ancestorExt)) && isSupportedFileExtension(leftExt)
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

        if (node == null) {
            return null;
        }

        if (node instanceof ITypedElement) {
            // RDi 9.6: biz.isphere.core.compareeditor.CompareNode
            return FileHelper.getFileExtension(((ITypedElement)node).getName());
        } else {
            // Hack for RDi 9.5, where node is a type of
            // com.ibm.etools.iseries.compare.internal.QSYSMemberAccessorTypedElement
            if ("com.ibm.etools.iseries.compare.internal.QSYSMemberAccessorTypedElement".equals(node.getClass().getName())) { //$NON-NLS-1$
                try {
                    Field[] fields = node.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("accessor".equals(field.getName())) {
                            field.setAccessible(true);
                            return getFileExtension(field.get(node));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
