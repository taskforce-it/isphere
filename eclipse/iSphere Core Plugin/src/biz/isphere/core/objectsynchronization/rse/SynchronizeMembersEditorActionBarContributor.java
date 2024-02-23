/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.rse;

import biz.isphere.core.dataspaceeditor.StatusLine;

public class SynchronizeMembersEditorActionBarContributor extends AbstractSynchronizeMembersEditorActionBarContributor {

    @Override
    public String getStatusLineId() {
        return StatusLine.STATUS_LINE_ID + "_" + getClass().getName(); //$NON-NLS-1$
    }

}
