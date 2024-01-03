/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit.sourcememberrename;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumber;

public class JUnitMemberRenamingRuleNumber extends MemberRenamingRuleNumber {

    private Set<String> memberList = new HashSet<String>();

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

    @Override
    protected String[] loadMemberList(String libraryName, String fileName, String memberNameFilter) throws PropertyVetoException, AS400Exception,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        return memberList.toArray(new String[memberList.size()]);
    }
}
