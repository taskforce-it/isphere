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

            String baseName = "OLD";

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "01", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameSecond() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            String baseName = "OLD";
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "01"));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "02", newName.getMemberName());
        }
    }

    @Test
    public void testMinValueOutOfBoundsFillGaps() throws Exception {

        for (String delimiter : delimiters) {

            MemberRenamingRuleNumber renameRule = (MemberRenamingRuleNumber)produceNewNameRule(delimiter, false);
            renameRule.setMinValue(20);

            RenameMemberActor actor = produceActor(system, renameRule);

            String baseName = "OLD";

            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "01"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "02"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "03"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "04"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "05"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "06"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "07"));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "20", newName.getMemberName());
        }
    }

    @Test
    public void testMinValueOutOfBoundsSkipGaps() throws Exception {

        for (String delimiter : delimiters) {

            MemberRenamingRuleNumber renameRule = (MemberRenamingRuleNumber)produceNewNameRule(delimiter, true);
            renameRule.setMinValue(20);

            RenameMemberActor actor = produceActor(system, renameRule);

            String baseName = "OLD";

            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "01"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "02"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "03"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "04"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "05"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "06"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "07"));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "20", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameFillGaps() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter, false));

            String baseName = "OLD";

            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "01"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "02"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "03"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "04"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "05"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "06"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "07"));
            // must be filled with OLD.08, because of different lengths of
            // extensions:
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "8"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "091"));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "08", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameSkipGaps() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter, true));

            String baseName = "OLD";

            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "01"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "02"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "03"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "04"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "05"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "06"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "07"));
            // must not be skipped, because of different lengths of extensions:
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "8"));
            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "091"));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "08", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameLast() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            String baseName = "OLD";

            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "98"));

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "99", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameNoMoreNames() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "99"));

            try {
                actor.produceNewMemberName(produceQSYSObjectPathName(baseName));
                fail("Should have faild with a NoMoreNamesAvailableException");
            } catch (Exception e) {
                assertEquals(NoMoreNamesAvailableException.class, e.getClass());
            }
        }
    }

    @Test
    public void testGetNextNameNoMoreNamesFillGaps() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter, false));

            actor.addMemberName(produceQSYSObjectPathName(baseName + delimiter + "99"));

            QSYSObjectPathName newName;

            newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "01", newName.getMemberName());

            actor.addMemberName(newName);

            newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals(baseName + delimiter + "02", newName.getMemberName());
        }
    }

    @Test
    public void testNameTooLong() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            try {
                actor.produceNewMemberName(produceQSYSObjectPathName("TOO_LONG"));
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

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName("0815"));

            assertEquals("0815" + delimiter + "01", newName.getMemberName());
        }
    }

    @Test
    public void testRenameBackupMember() throws Exception {

        for (String delimiter : delimiters) {

            RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            String baseName = "OLD.015";

            QSYSObjectPathName newName = actor.produceNewMemberName(produceQSYSObjectPathName(baseName));

            assertEquals("OLD.015" + delimiter + "01", newName.getMemberName());
        }
    }

    @Test
    public void testEmptyDelimiter() throws Exception {

        String delimiter = "";

        RenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

        try {
            actor.produceNewMemberName(produceQSYSObjectPathName("123"));
            fail("Should have faild with an InvalidDelimiterException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    private RenameMemberActor produceActor(AS400 system, IMemberRenamingRule newNameRule) {
        return new RenameMemberActor(system, newNameRule);
    }

    private IMemberRenamingRule produceNewNameRule(String delimiter) {
        return produceNewNameRule(delimiter, true);
    }

    private IMemberRenamingRule produceNewNameRule(String delimiter, boolean isSkipGapsEnabled) {

        MemberRenamingRuleNumber newNameRule = new MemberRenamingRuleNumber();
        newNameRule.setSkipGapsEnabled(isSkipGapsEnabled);
        newNameRule.setDelimiter(delimiter);
        newNameRule.setMinValue(1);
        newNameRule.setMaxValue(99);

        return newNameRule;
    }

    private QSYSObjectPathName produceQSYSObjectPathName(String oldMemberName) {
        return new QSYSObjectPathName("ISPHEREDVP", "QRPGLESRC", oldMemberName, "MBR");
    }
}
