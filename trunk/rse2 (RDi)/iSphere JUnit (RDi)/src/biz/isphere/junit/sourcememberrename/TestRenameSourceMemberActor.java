/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit.sourcememberrename;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.memberrename.RenameMemberActor;
import biz.isphere.core.memberrename.exceptions.InvalidMemberNameException;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumber;

public class TestRenameSourceMemberActor {

    private static String[] delimiters = { ".", "#" };

    private static AS400 system = null;

    @Test
    public void testGetNextNameFirst() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceOldName("OLD"));

            assertEquals("OLD" + delimiter + "001", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameSecond() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceOldName("OLD" + delimiter + "1"));

            assertEquals("OLD" + delimiter + "002", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameSkipExistingNames() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));
            actor.addMemberName(produceOldName("OLD" + delimiter + "001"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "002"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "003"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "004"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "005"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "006"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "007"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "08"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "009"));
            actor.addMemberName(produceOldName("OLD" + delimiter + "0010"));

            QSYSObjectPathName oldMemberPath = produceOldName("OLD" + delimiter + "005");
            QSYSObjectPathName newName = actor.produceNewMemberName(oldMemberPath);

            assertEquals("OLD" + delimiter + "005", oldMemberPath.getMemberName());
            assertEquals("OLD" + delimiter + "008", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameLast() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceOldName("OLD" + delimiter + "998"));

            assertEquals("OLD" + delimiter + "999", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameNoMoreNames() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            try {
                actor.produceNewMemberName(produceOldName("OLD" + delimiter + "999"));
                fail("Should have faild with a NoMoreNamesAvailableException");
            } catch (Exception e) {
                assertEquals(NoMoreNamesAvailableException.class, e.getClass());
            }
        }
    }

    @Test
    public void testNameTooLong() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            try {
                actor.produceNewMemberName(produceOldName("TOO_LONG"));
                fail("Should have faild with an InvalidMemberNameException");
            } catch (Exception e) {
                assertEquals(InvalidMemberNameException.class, e.getClass());
            }
        }
    }

    @Test
    public void testNameNumbersOnly() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceOldName("0815"));

            assertEquals("0815" + delimiter + "001", newName.getMemberName());
        }
    }

    @Test
    public void testEmptyDelimiter() throws Exception {

        String delimiter = "";

        RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

        try {
            actor.produceNewMemberName(produceOldName("123"));
            fail("Should have faild with an RuntimeException");
        } catch (Exception e) {
            assertEquals(RuntimeException.class, e.getClass());
        }
    }

    private RenameMemberActor produceActor(AS400 system, IMemberRenamingRule newNameRule) {
        return new RenameMemberActor(system, newNameRule);
    }

    private IMemberRenamingRule produceNewNameRule(String delimiter) {

        MemberRenamingRuleNumber newNameRule = new MemberRenamingRuleNumber();
        newNameRule.setDelimiter(delimiter);
        newNameRule.setMinValue(1);
        newNameRule.setMaxValue(999);

        return newNameRule;
    }

    private QSYSObjectPathName produceOldName(String oldMemberName) {
        return new QSYSObjectPathName("ISPHEREDVP", "QRPGLESRC", oldMemberName, "MBR");
    }
}
