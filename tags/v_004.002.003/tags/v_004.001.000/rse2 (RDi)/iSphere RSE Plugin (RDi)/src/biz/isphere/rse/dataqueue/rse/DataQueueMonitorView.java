/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataqueue.rse;

import biz.isphere.core.dataqueue.rse.AbstractDataQueueMonitorView;
import biz.isphere.core.dataqueue.rse.AbstractDropDataDataQueueListener;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;

import com.ibm.as400.access.AS400;

public class DataQueueMonitorView extends AbstractDataQueueMonitorView {

    @Override
    protected AbstractDropDataDataQueueListener createDropListener(IDialogView editor) {
        return new DropDataQueueListener(editor);
    }

    @Override
    protected IViewManager getViewManager() {
        return ISphereRSEPlugin.getDefault().getViewManager(IViewManager.DATA_QUEUE_MONITOR_VIEWS);
    }

    @Override
    protected AS400 getSystem(String connectionName) {
        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        return system;
    }
}
