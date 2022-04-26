/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.externalapi;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractAccess {

    protected static Shell ensureShell(Shell shell) {
        if (shell != null && !shell.isDisposed()) {
            return shell;
        }
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench != null) {
            IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
            if (activeWorkbenchWindow != null) {
                activeWorkbenchWindow.getShell();
            }
        }
        return null;
    }

}
