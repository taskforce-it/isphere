/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

/**
 * Basic FileDialog, which is used, when no other plugin contributes a better
 * one. This dialog gracefully ignores the 'overwrite' attribut that is not
 * available for WDSCi.
 * 
 * @author traddatz
 */
public class DefaultFileDialog implements IFileDialog {

    private FileDialog dialog = null;

    public DefaultFileDialog(Shell aParent) {
        dialog = new FileDialog(aParent, SWT.APPLICATION_MODAL);
    }

    public DefaultFileDialog(Shell aParent, int aStyle) {
        dialog = new FileDialog(aParent, aStyle);
    }

    public String open() throws SWTException {
        return dialog.open();
    }

    public void setOverwrite(boolean anOverwrite) {
        // Gracefully ignore the 'overwrite' attribute
        return;
    }

    public boolean getOverwrite() {
        return false;
    }

    public void setText(String aText) {
        dialog.setText(aText);
    }

    public void setFileName(String aFileName) {
        dialog.setFileName(aFileName);
    }

    public void setFilterPath(String aFilterPath) {
        dialog.setFilterPath(aFilterPath);
    }

    public void setFilterNames(String[] aFilterNames) {
        dialog.setFilterNames(aFilterNames);
    }

    public void setFilterExtensions(String[] aFilterExtensions) {
        dialog.setFilterExtensions(aFilterExtensions);
    }

    public void setFilterIndex(int index) {
        return; // Not supported for WDSCi 7.0
    }

    public String getFilterPath() {
        return FileHelper.getDefaultRootDirectory();
    }

}
