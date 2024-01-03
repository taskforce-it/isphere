/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.rules;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Exception;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.MemberDescription;
import com.ibm.as400.access.MemberList;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.memberrename.adapters.IMemberRenamingRuleAdapter;
import biz.isphere.core.preferences.Preferences;

public abstract class AbstractMemberRenamingRule<A extends IMemberRenamingRuleAdapter> implements IMemberRenamingRule, Serializable {

    private static final long serialVersionUID = 1L;

    private AS400 system;
    private String label;
    private String libraryName;
    private String fileName;
    private String memberName;

    public AbstractMemberRenamingRule(String label) {
        this.label = label;
    }

    public AS400 getSystem() {
        return system;
    }

    public String getLabel() {
        return label;
    }

    public void initialize(AS400 system, String libraryName, String fileName, String memberName) throws AS400Exception, PropertyVetoException,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {
        this.system = system;
        this.libraryName = libraryName;
        this.fileName = fileName;
        this.memberName = memberName;
    }

    public String getBaseMemberLibrary() {
        return this.libraryName;
    }

    public String getBaseMemberFile() {
        return this.fileName;
    }

    public String getBaseMemberName() {
        return memberName;
    }

    public A getAdapter() {

        Preferences preferences = Preferences.getInstance();
        if (preferences == null) {
            return null;
        }

        A adapter = (A)preferences.getMemberRenamingRuleAdapter(this.getClass());
        return adapter;
    }

    protected String[] loadMemberList(String libraryName, String fileName, String memberNameFilter) throws PropertyVetoException, AS400Exception,
        AS400SecurityException, ErrorCompletingRequestException, IOException, InterruptedException, ObjectDoesNotExistException {

        List<String> memberPaths = new LinkedList<String>();

        if (system == null) {
            // system is null when running JUnit tests
            return memberPaths.toArray(new String[memberPaths.size()]);
        }

        MemberList memberList = new MemberList(system, new QSYSObjectPathName(libraryName, fileName, memberNameFilter, "MBR")); //$NON-NLS-1$
        memberList.load();
        MemberDescription[] memberDescriptions = memberList.getMemberDescriptions();

        for (MemberDescription memberDescription : memberDescriptions) {
            memberPaths.add(memberDescription.getPath());
        }

        return memberPaths.toArray(new String[memberPaths.size()]);
    }
}
