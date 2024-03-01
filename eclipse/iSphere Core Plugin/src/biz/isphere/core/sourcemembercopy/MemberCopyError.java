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
    ERROR_EXCEPTION (true, SynchronizeMembersAction.CANCEL),

    ERROR_FROM_CONNECTION_NOT_FOUND (true, SynchronizeMembersAction.CANCEL),
    ERROR_TO_CONNECTION_NOT_FOUND (true, SynchronizeMembersAction.CANCEL),

    ERROR_FROM_LIBRARY_NAME_NOT_VALID (true, SynchronizeMembersAction.CANCEL),
    ERROR_FROM_LIBRARY_NOT_FOUND (true, SynchronizeMembersAction.CANCEL),
    ERROR_FROM_FILE_NAME_NOT_VALID (true, SynchronizeMembersAction.CANCEL),
    ERROR_FROM_FILE_NOT_FOUND (true, SynchronizeMembersAction.CANCEL),

    ERROR_FROM_MEMBER_IS_DIRTY (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),
    ERROR_TO_MEMBER_COPY_TO_SAME_NAME (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),

    ERROR_TO_LIBRARY_NAME_NOT_VALID (true, SynchronizeMembersAction.CANCEL),
    ERROR_TO_LIBRARY_NOT_FOUND (true, SynchronizeMembersAction.CANCEL),
    ERROR_TO_FILE_NAME_NOT_VALID (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),
    ERROR_TO_FILE_NOT_FOUND (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),

    ERROR_TO_MEMBER_EXISTS (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),
    ERROR_TO_MEMBER_RENAME_EXCEPTION (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),
    ERROR_TO_FILE_DATA_LOST (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR),
    ERROR_COPY_FILE_COMMAND (true, SynchronizeMembersAction.CONTINUE_WITH_ERROR);

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
