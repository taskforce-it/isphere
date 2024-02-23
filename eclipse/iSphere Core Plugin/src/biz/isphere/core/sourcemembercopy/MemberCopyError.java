/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

public enum MemberCopyError {
    ERROR_NONE (false, SynchronizeMembersAction.CONTINUE),
    ERROR_TO_FILE_NOT_FOUND (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),
    ERROR_TO_MEMBER_EXISTS (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),
    ERROR_TO_MEMBER_RENAME_EXCEPTION (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR);

    private boolean isError;
    private SynchronizeMembersAction action;

    private MemberCopyError(boolean isError, SynchronizeMembersAction action) {
        this.isError = isError;
        this.action = action;
    }

    public boolean isError() {
        return isError;
    }

    public SynchronizeMembersAction getDefaultAction() {
        return action;
    }
}
