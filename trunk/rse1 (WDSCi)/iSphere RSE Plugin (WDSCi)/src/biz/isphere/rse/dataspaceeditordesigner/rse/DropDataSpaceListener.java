/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataspaceeditordesigner.rse;

import biz.isphere.core.dataspaceeditordesigner.rse.AbstractDropDataSpaceListener;
import biz.isphere.core.dataspaceeditordesigner.rse.IDropObjectListener;
import biz.isphere.core.dataspaceeditordesigner.rse.IListOfRemoteObjectsReceiver;
import biz.isphere.rse.internal.job.LoadQsysRemoteObjectsJob;

public class DropDataSpaceListener extends AbstractDropDataSpaceListener {

    public DropDataSpaceListener(IDropObjectListener editor) {
        super(editor);
    }

    protected void loadRemoteObjectsAsync(String[] droppedObjects, IListOfRemoteObjectsReceiver receiver, String jobName) {
        LoadQsysRemoteObjectsJob job = new LoadQsysRemoteObjectsJob(jobName, droppedObjects, this);
        job.schedule();
    }
}
