/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.Dialog;

public class DialogUIRunnable implements Runnable {

    private Dialog dialog;

    private int result;

    public DialogUIRunnable(Dialog dialog) {
        this.dialog = dialog;
    }

    public void run() {
        result = dialog.open();
    }

    public int getResult() {
        return result;
    }
}
