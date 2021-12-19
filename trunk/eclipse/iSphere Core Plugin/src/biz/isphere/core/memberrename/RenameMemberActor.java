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
    private Set<String> memberOnSystemPaths;
    private IMemberRenamingRule memberRenamingRule;

    public RenameMemberActor(AS400 system, IMemberRenamingRule backupNameRule) {

        this.system = system;
        this.memberRenamingRule = backupNameRule;

        this.memberOnSystemPaths = new HashSet<String>();
    }

    public void clearMemberList() {
        memberOnSystemPaths.clear();
    }

    /**
     * Adds a member path name to the list of existing members. This method is
     * only for JUnit testing without needing a system connection.
     * 
     * @param qsysMemberPath - path of the member that is added to the list of
     *        existing members
     * @throws PropertyVetoException
     * @throws InvalidDelimiterException
     */
    public void addMemberName(QSYSObjectPathName qsysMemberPath) throws PropertyVetoException {
        addMemberName(qsysMemberPath.getPath());
    }

    /**
     * Adds a member path name to the list of existing members. This method is
     * only for JUnit testing without needing a system connection.
     * 
     * @param memberPath - path of the member that is added to the list of
     *        existing members. The path must match the value returned by
     *        {@link QSYSObjectPathName#getPath()}.
     * @throws PropertyVetoException
     * @throws InvalidDelimiterException
     */
    public void addMemberName(String memberPath) throws PropertyVetoException {
        memberOnSystemPaths.add(memberPath);
    }

    /**
     * Produces a new member name based of a given member and renaming rule.
     * 
     * @param oldQSYSMember - name of member that is renamed
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
    public QSYSObjectPathName produceNewMemberName(QSYSObjectPathName oldQSYSMember)
        throws NoMoreNamesAvailableException, InvalidMemberNameException, PropertyVetoException, AS400Exception, AS400SecurityException,
        ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        String baseMemberName = oldQSYSMember.getMemberName();

        if (baseMemberName.length() > 9) {
            // Too long, because we cannot add the asterisk required for the
            // member list
            throw new InvalidMemberNameException(Messages.bind(Messages.Error_Invalid_member_name_Name_is_too_long_A, baseMemberName));
        }

        memberRenamingRule.setBaseMemberName(baseMemberName);

        loadExistingMembers(oldQSYSMember.getLibraryName(), oldQSYSMember.getObjectName(), memberRenamingRule.getMemberNameFilter());

        memberRenamingRule.setExistingMembers(memberOnSystemPaths.toArray(new String[memberOnSystemPaths.size()]));

        QSYSObjectPathName nextQSYSMemberName = null;
        while (nextQSYSMemberName == null) {

            String nextMemberName = memberRenamingRule.getNextName();
            if (nextMemberName.length() > 10) {
                throw new InvalidMemberNameException(Messages.bind(Messages.Error_Invalid_member_name_Name_is_too_long_A, nextMemberName));
            }

            nextQSYSMemberName = new QSYSObjectPathName(oldQSYSMember.getPath());
            nextQSYSMemberName.setMemberName(nextMemberName);

            if (memberOnSystemPaths.contains(nextQSYSMemberName.getPath())) {
                // May happen, when skip gaps is disabled.
                nextQSYSMemberName = null;
            }
        }

        return nextQSYSMemberName;
    }

    private void loadExistingMembers(String libraryName, String fileName, String memberNameFilter) throws PropertyVetoException, AS400Exception,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        if (system == null) {
            // system is null when running JUnit tests
            return;
        }

        MemberList memberList = new MemberList(system, new QSYSObjectPathName(libraryName, fileName, memberNameFilter, "MBR")); //$NON-NLS-1$
        memberList.load();
        MemberDescription[] memberDescriptions = memberList.getMemberDescriptions();

        clearMemberList();
        for (MemberDescription memberDescription : memberDescriptions) {
            addMemberName(memberDescription.getPath());
        }
    }
}
