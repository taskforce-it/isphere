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

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.memberrename.exceptions.InvalidMemberNameException;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;

public class RenameMemberActor {

    private AS400 system;
    // private Set<String> memberOnSystemPaths;
    private IMemberRenamingRule memberRenamingRule;

    public RenameMemberActor(AS400 system, IMemberRenamingRule backupNameRule) {
        this.system = system;
        this.memberRenamingRule = backupNameRule;
    }

    /**
     * Produces a new member name based of a given member and renaming rule.
     * 
     * @param baseQSYSMemberName - name of member that is renamed
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
    public QSYSObjectPathName produceNewMemberName(String libraryName, String fileName, String memberName)
        throws NoMoreNamesAvailableException, InvalidMemberNameException, PropertyVetoException, AS400Exception, AS400SecurityException,
        ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        if (memberName.length() > 9) {
            // Too long, because we cannot add the asterisk required for the
            // member list
            throw new InvalidMemberNameException(Messages.bind(Messages.Error_Invalid_member_name_Name_is_too_long_A, memberName));
        }

        memberRenamingRule.initialize(system, libraryName, fileName, memberName);

        QSYSObjectPathName nextQSYSMemberName = null;
        while (nextQSYSMemberName == null) {

            String nextMemberName = memberRenamingRule.getNextName();
            if (nextMemberName.length() > 10) {
                throw new InvalidMemberNameException(Messages.bind(Messages.Error_Invalid_member_name_Name_is_too_long_A, nextMemberName));
            }

            nextQSYSMemberName = new QSYSObjectPathName(libraryName, fileName, nextMemberName, "MBR"); //$NON-NLS-1$

            if (exists(system, libraryName, fileName, nextMemberName)) {
                // May happen, when skip gaps is disabled.
                nextQSYSMemberName = null;
            }
        }

        return nextQSYSMemberName;
    }

    protected boolean exists(AS400 system, String libraryName, String fileName, String nextMemberName) {
        return ISphereHelper.checkMember(system, libraryName, fileName, nextMemberName);
    }
}
