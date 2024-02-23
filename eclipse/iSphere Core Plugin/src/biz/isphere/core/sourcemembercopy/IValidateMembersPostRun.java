/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

public interface IValidateMembersPostRun {

    /**
     * PostRun method called by {@link ValidateMembersJob} at the end of the
     * validation process.
     * 
     * @param isCanceled - true, if the job has been canceled;otherwise false
     * @param countTotal - total number of members processed
     * @param countSkipped - number of members skipped
     * @param countValidated - number of members validated
     * @param countErrors - number of members that could not be processed
     * @param averageTime - average processing time per member
     * @param cancelErrorId - reason for canceling the job
     * @param cancelMessage - job cancel error message
     */
    public void returnValidateMembersResult(boolean isCanceled, int countTotal, int countSkipped, int countValidated, int countErrors,
        long averageTime, MemberValidationError cancelErrorId, String cancelMessage);

}
