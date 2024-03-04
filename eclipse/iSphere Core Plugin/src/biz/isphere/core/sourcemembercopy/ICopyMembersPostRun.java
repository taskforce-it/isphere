/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import biz.isphere.core.sourcemembercopy.rse.CopyMembersJob;

public interface ICopyMembersPostRun {

    /**
     * PostRun methods called by {@link CopyMembersJob} at the end of the copy
     * member process.
     *
     * @param isCanceled - true, if the job has been canceled;otherwise false
     * @param countTotal - total number of members processed
     * @param countSkipped - number of members skipped
     * @param countCopied - number of members copied
     * @param countErrors - number of members that could not be processed
     * @param averageTime - average processing time per member
     * @param cancelErrorId - reason for canceling the job
     * @param cancelMessage - job cancel error message
     */
    public void returnCopyMembersResult(boolean isCanceled, int countTotal, int countSkipped, int countCopied, int countErrors, long averageTime,
        MemberCopyError cancelErrorId, String cancelMessage);

}
