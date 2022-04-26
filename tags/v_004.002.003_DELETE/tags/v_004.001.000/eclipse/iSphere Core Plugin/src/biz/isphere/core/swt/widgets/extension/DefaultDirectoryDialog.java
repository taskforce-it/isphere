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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.extension.point.IDirectoryDialog;

/**
 * Basic DirectoryDialog, which is used, when no other plugin contributes a
 * better one.
 * 
 * @author traddatz
 */
public class DefaultDirectoryDialog implements IDirectoryDialog {

    private DirectoryDialog dialog = null;

    public DefaultDirectoryDialog(Shell aParent) {
        dialog = new DirectoryDialog(aParent, SWT.APPLICATION_MODAL);
    }

    public DefaultDirectoryDialog(Shell aParent, int aStyle) {
        dialog = new DirectoryDialog(aParent, aStyle);
    }

    public String open() throws SWTException {
        return dialog.open();
    }

    public void setText(String aText) {
        dialog.setText(aText);
    }

    public void setFilterPath(String aFilterPath) {
        dialog.setFilterPath(aFilterPath);
    }

    public String getFilterPath() {
        return FileHelper.getDefaultRootDirectory();
    }

}
