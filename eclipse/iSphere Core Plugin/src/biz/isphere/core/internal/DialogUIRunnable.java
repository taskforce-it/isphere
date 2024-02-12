/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.UIHelper;

public class DialogUIRunnable implements Runnable {

    private String title;
    private String message;
    private int kind;
    private String[] buttonLabels;
    private Shell shell;

    private int result;

    public DialogUIRunnable(String title, String message, int kind) {
        this(null, title, message, kind);
    }

    public DialogUIRunnable(Shell shell, String title, String message, int kind) {
        this.shell = shell;
        this.title = title;
        this.message = message;
        this.kind = kind;
        this.buttonLabels = MessageDialogAsync.getButtonLabels(kind);
    }

    public void run() {
        MessageDialog dialog = new MessageDialog(ensureShell(), title, null, message, kind, buttonLabels, 0);
        result = dialog.open();
    }

    public int getResult() {
        return result;
    }

    private Shell ensureShell() {
        if (shell == null) {
            shell = UIHelper.getActiveShell();
        }
        return shell;
    }
}
