/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.historycombo;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.stringlisteditor.StringListEditor;

public class WorkWithHistoryDialog extends XDialog {

    private StringListEditor historyListEditor;
    private String[] items;

    public WorkWithHistoryDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite)super.createDialogArea(parent);

        historyListEditor = new StringListEditor(dialogArea, false, SWT.NONE);
        setItems(items);

        return dialogArea;
    }

    public String[] getItems() {
        return historyListEditor.getItems();
    }

    public int getItemCount() {
        return historyListEditor.getItemCount();
    }

    public void setItems(String[] items) {

        this.items = items;

        if (historyListEditor != null) {
            historyListEditor.setItems(this.items);
        }
    }

    /**
     * Overridden to make this dialog resizeable
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {

        if (isResizable()) {
            return new Point(450, 300);
        }

        return super.getDefaultSize();
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }
}
