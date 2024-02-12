/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public class DialogUIJob extends UIJob {

    private Display display;
    private String title;
    private String message;
    private int kind;
    private String[] buttonLabels;

    public DialogUIJob(String title, String message, int kind) {
        this(null, title, message, kind);
    }

    public DialogUIJob(Display display, String title, String message, int kind) {
        super("");
        this.display = display;
        this.title = title;
        this.message = message;
        this.kind = kind;
        this.buttonLabels = MessageDialogAsync.getButtonLabels(kind);
    }

    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        MessageDialog dialog = new MessageDialog(ensureDisplay().getActiveShell(), title, null, message, kind, buttonLabels, 0);
        dialog.open();
        return Status.OK_STATUS;
    }

    private Display ensureDisplay() {
        if (display == null) {
            display = Display.getCurrent();
            if (display == null) {
                display = Display.getDefault();
            }
        }
        return display;
    }
}
