/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
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
     * @param isError - <code>true</code> if there was an error; otherwise
     *        <code>false</code>
     * @param countMembersCopied - number of members that have been copied
     * @param averageTime - average time it took for copying a member
     */
    public void returnResult(boolean isError, int countMembersCopied, long averageTime);

}
