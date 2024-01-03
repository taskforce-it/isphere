/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit.sourcememberrename;

import java.util.HashSet;
import java.util.Set;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.memberrename.RenameMemberActor;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;

public class JUnitRenameMemberActor extends RenameMemberActor {

    private Set<String> memberList = new HashSet<String>();

    public JUnitRenameMemberActor(AS400 system, IMemberRenamingRule backupNameRule) {
        super(system, backupNameRule);
    }

    public void addMemberName(QSYSObjectPathName memberPath) {
        memberList.add(memberPath.getPath());
    }

    public void setMemberNames(QSYSObjectPathName[] memberPaths) {
        for (QSYSObjectPathName memberPath : memberPaths) {
            addMemberName(memberPath);
        }
    }

    protected boolean exists(AS400 system, String libraryName, String fileName, String memberName) {
        return memberList.contains(new QSYSObjectPathName(libraryName, fileName, memberName, "MBR").getPath());
    }
}
