/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.listener;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

public class PartCloseAdapter implements IPartListener {

    private IWorkbenchPage page;

    public PartCloseAdapter(IWorkbenchPage page) {
        this.page = page;
        page.addPartListener(this);
    }

    public void partActivated(IWorkbenchPart paramIWorkbenchPart) {
    }

    public void partBroughtToTop(IWorkbenchPart paramIWorkbenchPart) {
    }

    public void partClosed(IWorkbenchPart paramIWorkbenchPart) {
        if (page != null) {
            page.removePartListener(this);
        }
    }

    public void partDeactivated(IWorkbenchPart paramIWorkbenchPart) {
    }

    public void partOpened(IWorkbenchPart paramIWorkbenchPart) {
    }

}
