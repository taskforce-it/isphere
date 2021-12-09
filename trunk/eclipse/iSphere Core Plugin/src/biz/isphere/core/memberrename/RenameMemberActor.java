/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.MemberDescription;
import com.ibm.as400.access.MemberList;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.Messages;
import biz.isphere.core.memberrename.exceptions.InvalidMemberNameException;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;

public class RenameMemberActor {

    private AS400 system;
    private Set<String> qsysMemberPaths;
    private IMemberRenamingRule memberRenamingRule;

    public RenameMemberActor(AS400 system, IMemberRenamingRule backupNameRule) {

        this.system = system;
        this.memberRenamingRule = backupNameRule;

        this.qsysMemberPaths = new HashSet<String>();
    }

    public void clearMemberList() {
        qsysMemberPaths.clear();
    }

    public void addMemberName(QSYSObjectPathName memberPath) throws PropertyVetoException {
        qsysMemberPaths.add(memberPath.getPath());
    }

    public QSYSObjectPathName produceNewMemberName(QSYSObjectPathName oldMemberPath)
        throws NoMoreNamesAvailableException, InvalidMemberNameException, PropertyVetoException, AS400Exception, AS400SecurityException,
        ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        String oldMemberName = oldMemberPath.getMemberName();
        if (oldMemberName.length() > 9) {
            // Too long, because we cannot add the asterisk required for the
            // member list
            throw new InvalidMemberNameException(Messages.bind(Messages.Invalid_member_name_Name_is_too_long_A, oldMemberName));
        }

        MemberDescription[] memberDescriptions = null;
        if (system != null) {

            String baseMemberName = memberRenamingRule.getBaseName(oldMemberPath.getMemberName());
            String memberNameFilter = baseMemberName + "*";

            QSYSObjectPathName qsysMemberFilter = new QSYSObjectPathName(oldMemberPath.getPath());
            qsysMemberFilter.setMemberName(memberNameFilter);

            MemberList memberList = new MemberList(system, qsysMemberFilter);
            memberList.load();
            memberDescriptions = memberList.getMemberDescriptions();
        }

        QSYSObjectPathName nextMemberNamePath = null;
        while (nextMemberNamePath == null) {

            String nextMemberName = memberRenamingRule.getNextName(oldMemberPath.getMemberName());
            if (nextMemberName.length() > 10) {
                throw new InvalidMemberNameException(Messages.bind(Messages.Invalid_member_name_Name_is_too_long_A, nextMemberName));
            }

            nextMemberNamePath = new QSYSObjectPathName(oldMemberPath.getPath());
            nextMemberNamePath.setMemberName(nextMemberName);
            if (qsysMemberPaths.contains(nextMemberNamePath.getPath()) || isMemberOnSystem(memberDescriptions, nextMemberNamePath)) {
                oldMemberPath = nextMemberNamePath;
                nextMemberNamePath = null;
            }
        }

        return nextMemberNamePath;
    }

    private boolean isMemberOnSystem(MemberDescription[] memberDescriptions, QSYSObjectPathName newName) throws PropertyVetoException, AS400Exception,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        if (memberDescriptions != null) {
            for (MemberDescription memberDescription : memberDescriptions) {
                if (memberDescription.getValue(MemberDescription.MEMBER_NAME).equals(newName.getMemberName())) {
                    return true;
                }
            }
        }

        return false;
    }
}
