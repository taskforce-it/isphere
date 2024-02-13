/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import biz.isphere.core.sourcemembercopy.ValidateMembersJob.MemberValidationError;

public interface IValidateMembersPostRun {

    /**
     * PostRun method called by {@link ValidateMembersJob} at the end of the
     * validation process.
     * 
     * @param errorId - value indicating the error type
     * @param errorMessage - error message text
     */
    public void returnResult(MemberValidationError errorId, String errorMessage);

}
