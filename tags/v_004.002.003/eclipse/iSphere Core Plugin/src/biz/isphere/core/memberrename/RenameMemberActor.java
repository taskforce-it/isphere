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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import biz.isphere.base.comparators.QSYSObjectPathNameComparator;
import biz.isphere.core.Messages;
import biz.isphere.core.memberrename.exceptions.InvalidMemberNameException;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.MemberDescription;
import com.ibm.as400.access.MemberList;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;

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

    /**
     * Adds a member path name to the list of existing members. This method is
     * only for JUnit testing without needing a system connection.
     * 
     * @param memberPath - path of the member that is added to the list of
     *        existing members
     * @throws PropertyVetoException
     */
    public void addMemberName(QSYSObjectPathName memberPath) throws PropertyVetoException {
        addMemberName(memberPath.getPath());
    }

    public void addMemberName(String memberPath) throws PropertyVetoException {
        qsysMemberPaths.add(memberPath);
    }

    /**
     * Produces a new member name based of a given member and renaming rule.
     * 
     * @param oldMemberPath - name of member that is renamed
     * @return QSYS path of the new member.
     * @throws NoMoreNamesAvailableException
     * @throws InvalidMemberNameException
     * @throws PropertyVetoException
     * @throws AS400Exception
     * @throws AS400SecurityException
     * @throws ErrorCompletingRequestException
     * @throws IOException
     * @throws InterruptedException
     * @throws ObjectDoesNotExistException
     */
    public QSYSObjectPathName produceNewMemberName(QSYSObjectPathName oldMemberPath) throws NoMoreNamesAvailableException,
        InvalidMemberNameException, PropertyVetoException, AS400Exception, AS400SecurityException, ErrorCompletingRequestException, IOException,
        InterruptedException, ObjectDoesNotExistException {

        String oldMemberName = oldMemberPath.getMemberName();
        if (oldMemberName.length() > 9) {
            // Too long, because we cannot add the asterisk required for the
            // member list
            throw new InvalidMemberNameException(Messages.bind(Messages.Error_Invalid_member_name_Name_is_too_long_A, oldMemberName));
        }

        if (system != null) {

            String baseMemberName = memberRenamingRule.getBaseName(oldMemberPath.getMemberName());
            String memberNameFilter = baseMemberName + "*";

            QSYSObjectPathName qsysMemberFilter = new QSYSObjectPathName(oldMemberPath.getPath());
            qsysMemberFilter.setMemberName(memberNameFilter);

            MemberList memberList = new MemberList(system, qsysMemberFilter);
            memberList.load();
            MemberDescription[] memberDescriptions = memberList.getMemberDescriptions();

            clearMemberList();
            for (MemberDescription memberDescription : memberDescriptions) {
                addMemberName(memberDescription.getPath());
            }
        }

        if (memberRenamingRule.isSkipGapsEnabled() && qsysMemberPaths.size() > 0) {
            String[] memberPaths = qsysMemberPaths.toArray(new String[qsysMemberPaths.size()]);
            QSYSObjectPathName[] qsysMemberPaths = new QSYSObjectPathName[memberPaths.length];
            for (int i = 0; i < memberPaths.length; i++) {
                qsysMemberPaths[i] = new QSYSObjectPathName(memberPaths[i]);
            }
            Arrays.sort(qsysMemberPaths, new QSYSObjectPathNameComparator());
            oldMemberPath = qsysMemberPaths[qsysMemberPaths.length - 1];
        }

        QSYSObjectPathName nextMemberNamePath = null;
        while (nextMemberNamePath == null) {

            String nextMemberName = memberRenamingRule.getNextName(oldMemberPath.getMemberName());
            if (nextMemberName.length() > 10) {
                throw new InvalidMemberNameException(Messages.bind(Messages.Error_Invalid_member_name_Name_is_too_long_A, nextMemberName));
            }

            nextMemberNamePath = new QSYSObjectPathName(oldMemberPath.getPath());
            nextMemberNamePath.setMemberName(nextMemberName);
            if (qsysMemberPaths.contains(nextMemberNamePath.getPath())) {
                oldMemberPath = nextMemberNamePath;
                nextMemberNamePath = null;
            }
        }

        return nextMemberNamePath;
    }
}
