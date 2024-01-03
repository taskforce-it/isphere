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

import biz.isphere.core.memberrename.exceptions.InvalidMemberNameException;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;

public class TestRenameSourceMemberActor {

    private static final String LIBRARY_NAME = "ISPHEREDVP";
    private static final String FILE_NAME = "QRPGLESRC";

    private static String[] delimiters = { ".", "#" };

    private static AS400 system = null;

    @Test
    public void testGetNextNameFirst() throws Exception {

        for (String delimiter : delimiters) {

            JUnitRenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            String baseName = "OLD";

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "01", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameSecond() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName existingMember = produceQSYSObjectPathName(baseName + delimiter + "01");

            actor.addMemberName(existingMember);
            rule.addMemberName(existingMember);

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "02", newName.getMemberName());
        }
    }

    @Test
    public void testMinValueOutOfBoundsFillGaps() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter, true);
            rule.setMinValue(20);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName[] existingMembers = new QSYSObjectPathName[] { produceQSYSObjectPathName(baseName + delimiter + "01"),
                produceQSYSObjectPathName(baseName + delimiter + "02"), produceQSYSObjectPathName(baseName + delimiter + "03"),
                produceQSYSObjectPathName(baseName + delimiter + "04"), produceQSYSObjectPathName(baseName + delimiter + "05"),
                produceQSYSObjectPathName(baseName + delimiter + "06"), produceQSYSObjectPathName(baseName + delimiter + "07") };

            actor.setMemberNames(existingMembers);
            rule.setMemberNames(existingMembers);

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "20", newName.getMemberName());
        }
    }

    @Test
    public void testMinValueOutOfBoundsSkipGaps() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter, false);
            rule.setMinValue(20);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName[] existingMembers = new QSYSObjectPathName[] { produceQSYSObjectPathName(baseName + delimiter + "01"),
                produceQSYSObjectPathName(baseName + delimiter + "02"), produceQSYSObjectPathName(baseName + delimiter + "03"),
                produceQSYSObjectPathName(baseName + delimiter + "04"), produceQSYSObjectPathName(baseName + delimiter + "05"),
                produceQSYSObjectPathName(baseName + delimiter + "06"), produceQSYSObjectPathName(baseName + delimiter + "07") };

            actor.setMemberNames(existingMembers);
            rule.setMemberNames(existingMembers);

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "20", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameFillGaps() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter, true);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName[] existingMembers = new QSYSObjectPathName[] { produceQSYSObjectPathName(baseName + delimiter + "01"),
                produceQSYSObjectPathName(baseName + delimiter + "02"), produceQSYSObjectPathName(baseName + delimiter + "03"),
                produceQSYSObjectPathName(baseName + delimiter + "04"), produceQSYSObjectPathName(baseName + delimiter + "05"),
                produceQSYSObjectPathName(baseName + delimiter + "06"), produceQSYSObjectPathName(baseName + delimiter + "07"),
                // must be filled with OLD.08, because of different lengths of
                // extensions:
                produceQSYSObjectPathName(baseName + delimiter + "8"), produceQSYSObjectPathName(baseName + delimiter + "091") };

            actor.setMemberNames(existingMembers);
            rule.setMemberNames(existingMembers);

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "08", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameSkipGaps() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter, false);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName[] existingMembers = new QSYSObjectPathName[] { produceQSYSObjectPathName(baseName + delimiter + "01"),
                produceQSYSObjectPathName(baseName + delimiter + "02"), produceQSYSObjectPathName(baseName + delimiter + "03"),
                produceQSYSObjectPathName(baseName + delimiter + "04"), produceQSYSObjectPathName(baseName + delimiter + "05"),
                produceQSYSObjectPathName(baseName + delimiter + "06"), produceQSYSObjectPathName(baseName + delimiter + "07"),
                // must not be skipped, because of different lengths of
                // extensions:
                produceQSYSObjectPathName(baseName + delimiter + "8"), produceQSYSObjectPathName(baseName + delimiter + "091") };

            actor.setMemberNames(existingMembers);
            rule.setMemberNames(existingMembers);

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "08", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameLast() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName existingMember = produceQSYSObjectPathName(baseName + delimiter + "98");

            actor.addMemberName(existingMember);
            rule.addMemberName(existingMember);

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "99", newName.getMemberName());
        }
    }

    @Test
    public void testGetNextNameNoMoreNames() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName existingMember = produceQSYSObjectPathName(baseName + delimiter + "99");

            actor.addMemberName(existingMember);
            rule.addMemberName(existingMember);

            try {
                actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);
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

            JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter, true);

            JUnitRenameMemberActor actor = produceActor(system, rule);

            QSYSObjectPathName existingMember = produceQSYSObjectPathName(baseName + delimiter + "99");

            actor.addMemberName(existingMember);
            rule.addMemberName(existingMember);

            QSYSObjectPathName newName;

            newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "01", newName.getMemberName());

            actor.addMemberName(newName);
            rule.addMemberName(newName);

            newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals(baseName + delimiter + "02", newName.getMemberName());
        }
    }

    @Test
    public void testNameTooLong() throws Exception {

        for (String delimiter : delimiters) {

            JUnitRenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            try {
                actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, "TOO_LONG");
                fail("Should have faild with an InvalidMemberNameException");
            } catch (Exception e) {
                assertEquals(InvalidMemberNameException.class, e.getClass());
            }
        }
    }

    @Test
    public void testNameNumbersOnly() throws Exception {

        for (String delimiter : delimiters) {

            JUnitRenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, "0815");

            assertEquals("0815" + delimiter + "01", newName.getMemberName());
        }
    }

    @Test
    public void testRenameBackupMember() throws Exception {

        for (String delimiter : delimiters) {

            JUnitRenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

            String baseName = "OLD.015";

            QSYSObjectPathName newName = actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, baseName);

            assertEquals("OLD.015" + delimiter + "01", newName.getMemberName());
        }
    }

    @Test
    public void testEmptyDelimiter() throws Exception {

        String delimiter = "";

        JUnitRenameMemberActor actor = produceActor(system, produceNewNameRule(delimiter));

        try {
            actor.produceNewMemberName(LIBRARY_NAME, FILE_NAME, "123");
            fail("Should have faild with an InvalidDelimiterException");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    private JUnitRenameMemberActor produceActor(AS400 system, IMemberRenamingRule newNameRule) {
        return new JUnitRenameMemberActor(system, newNameRule);
    }

    private JUnitMemberRenamingRuleNumber produceNewNameRule(String delimiter) {

        JUnitMemberRenamingRuleNumber rule = produceNewNameRule(delimiter, false);

        return rule;
    }

    private JUnitMemberRenamingRuleNumber produceNewNameRule(String delimiter, boolean isFillGapsEnabled) {

        JUnitMemberRenamingRuleNumber newNameRule = new JUnitMemberRenamingRuleNumber();

        newNameRule.setFillGapsEnabled(isFillGapsEnabled);
        newNameRule.setDelimiter(delimiter);
        newNameRule.setMinValue(1);
        newNameRule.setMaxValue(99);

        return newNameRule;
    }

    private QSYSObjectPathName produceQSYSObjectPathName(String oldMemberName) {
        return new QSYSObjectPathName("ISPHEREDVP", "QRPGLESRC", oldMemberName, "MBR");
    }
}