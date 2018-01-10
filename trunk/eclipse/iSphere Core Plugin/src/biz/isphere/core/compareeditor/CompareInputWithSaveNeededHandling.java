/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.core.internal.Member;

/**
 * This class is used when WDSCi is the host application.
 * 
 * @see: {@link CompareInput}
 */
public class CompareInputWithSaveNeededHandling extends CompareInput {

    private boolean isSaveNeeded = false;

    public CompareInputWithSaveNeededHandling(CompareEditorConfiguration config, Member ancestorMember, Member leftMember, Member rightMember) {
        super(config, ancestorMember, leftMember, rightMember);
    }

    @Override
    public boolean isSaveNeeded() {
        if (super.isSaveNeeded()) {
            isSaveNeeded = true;
        }
        return isSaveNeeded;
    }

    @Override
    public void saveChanges(IProgressMonitor pm) throws CoreException {
        isSaveNeeded = false;
        super.saveChanges(pm);
    }

}
