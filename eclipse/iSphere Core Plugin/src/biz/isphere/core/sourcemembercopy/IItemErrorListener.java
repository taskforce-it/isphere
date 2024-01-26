/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import biz.isphere.core.sourcemembercopy.CopyMemberValidator.MemberValidationError;

public interface IItemErrorListener {

    /**
     * Methods called by the {@link CopyMemberValidator} each time it encounters
     * an error. Possible error IDs of type {@link MemberValidationError}:
     * <ul>
     * <li>ERROR_TO_CONNECTION</li>
     * <li>ERROR_TO_LIBRARY</li>
     * <li>ERROR_TO_FILE</li>
     * </ul>
     * 
     * @param sender - anything useful for identifying the sender of the event
     * @param errorId - ID identifying the error
     * @param errorMessage - error message text
     * @return true for canceling the job; otherwise false
     */
    public boolean reportError(Object sender, MemberValidationError errorId, String errorMessage);

    /**
     * Methods called by the {@link CopyMemberValidator} for each member error.
     * 
     * @param sender - anything useful for identifying the sender of the event
     * @param item - copy member item in error
     * @param errorMessage - error message text
     * @return true for canceling the job; otherwise false
     */
    public boolean reportError(Object sender, CopyMemberItem item, String errorMessage);
}
