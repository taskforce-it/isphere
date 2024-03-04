/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import biz.isphere.core.objectsynchronization.MemberDescription;

public interface ICompareMembersPostrun {

    /**
     * PostRun methods called by {@link CompareMembersJob} at the end of the
     * member compare and load process.
     * 
     * @param isCanceled - true, if the job has been canceled;otherwise false
     * @param leftMemberDescriptions - members descriptions of the left side
     * @param rightMemberDescriptions - member descriptions of the right side
     */
    public void compareMembersPostRun(boolean isCanceled, MemberDescription[] leftMemberDescriptions, MemberDescription[] rightMemberDescriptions);
}
