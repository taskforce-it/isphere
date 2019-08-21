/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.rse;

import biz.isphere.core.dataspaceeditordesigner.rse.IDropObjectListener;
import biz.isphere.core.rse.AbstractDropRemoteObjectListerner;

public abstract class AbstractDropDataDataQueueListener extends AbstractDropRemoteObjectListerner {

    public AbstractDropDataDataQueueListener(IDropObjectListener iDropObjectListener) {
        super(iDropObjectListener);
    }
}
