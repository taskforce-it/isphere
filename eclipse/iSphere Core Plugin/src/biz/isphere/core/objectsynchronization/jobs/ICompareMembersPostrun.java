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

    public void returnResult(boolean isCanceled, MemberDescription[] leftMemberDescriptions, MemberDescription[] rightMemberDescriptions);
}
