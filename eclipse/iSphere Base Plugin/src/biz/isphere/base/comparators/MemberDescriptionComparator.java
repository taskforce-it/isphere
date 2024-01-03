/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.comparators;

import java.util.Comparator;

import com.ibm.as400.access.MemberDescription;

public class MemberDescriptionComparator implements Comparator<MemberDescription> {

    public int compare(MemberDescription memberDescription1, MemberDescription memberDescription2) {
        return memberDescription1.getPath().compareTo(memberDescription2.getPath());
    }
}
