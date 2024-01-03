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

import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumber;

public class TestNewNameNumberRule {

    private static final String LIBRARY_NAME = "ISPHEREDVP";
    private static final String FILE_NAME = "QRPGLESRC";

    private static String[] delimiters = { ".", "#" };

    @Test
    public void testIsValidNameTrue() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            MemberRenamingRuleNumber newNameRule = (MemberRenamingRuleNumber)produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName);

            assertEquals(true, newNameRule.isMatchingName(baseName + delimiter + "01"));
        }

        for (String delimiter : delimiters) {

            String baseName = "OLD.01";

            MemberRenamingRuleNumber newNameRule = (MemberRenamingRuleNumber)produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName);

            assertEquals(true, newNameRule.isMatchingName(baseName + delimiter + "01"));
        }
    }

    @Test
    public void testIsValidNameFalse() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            MemberRenamingRuleNumber newNameRule = (MemberRenamingRuleNumber)produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName);

            assertEquals(false, newNameRule.isMatchingName(baseName + delimiter + "1"));
        }

        for (String delimiter : delimiters) {

            String baseName = "OLD" + delimiter + "01";

            MemberRenamingRuleNumber newNameRule = (MemberRenamingRuleNumber)produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName);

            assertEquals(false, newNameRule.isMatchingName(baseName + delimiter + "001"));
        }
    }

    @Test
    public void testIsValidNameFalseRegExDotValue() throws Exception {

        String baseName = "OLD";

        MemberRenamingRuleNumber newNameRule = (MemberRenamingRuleNumber)produceNewNameRule(".");
        initializeRule(newNameRule, baseName);

        assertEquals(false, newNameRule.isMatchingName(baseName + "#01"));
    }

    @Test
    public void testInvalidExtension() throws Exception {

        String delimiter = ".";

        String baseName = "OLD";

        IMemberRenamingRule newNameRule = produceNewNameRule(delimiter);
        initializeRule(newNameRule, baseName, new String[] { produceQSYSObjectPathName(baseName + delimiter + "AB").getPath() });

        String newName = newNameRule.getNextName();

        assertEquals(baseName + delimiter + "01", newName);
    }

    @Test
    public void testGetNextNameFirst() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";

            IMemberRenamingRule newNameRule = produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName);

            String newName = newNameRule.getNextName();

            assertEquals(baseName + delimiter + "01", newName);
        }
    }

    @Test
    public void testGetNextNameSecond() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";
            String memberOnSystem = baseName + delimiter + "01";

            IMemberRenamingRule newNameRule = produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName, new String[] { produceQSYSObjectPathName(memberOnSystem).getPath() });

            String newName = newNameRule.getNextName();

            assertEquals(baseName + delimiter + "02", newName);
        }
    }

    @Test
    public void testGetNextNameLast() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";
            String memberOnSystem = baseName + delimiter + "98";

            IMemberRenamingRule newNameRule = produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName, new String[] { produceQSYSObjectPathName(memberOnSystem).getPath() });

            String newName = newNameRule.getNextName();

            assertEquals(baseName + delimiter + "99", newName);
        }
    }

    @Test
    public void testGetNextNameNoMoreNames() throws Exception {

        for (String delimiter : delimiters) {

            String baseName = "OLD";
            String memberOnSystem = baseName + delimiter + "99";

            IMemberRenamingRule newNameRule = produceNewNameRule(delimiter);
            initializeRule(newNameRule, baseName, new String[] { produceQSYSObjectPathName(memberOnSystem).getPath() });

            try {
                newNameRule.getNextName();
                fail("Should have faild with a NoMoreNamesAvailableException");
            } catch (NoMoreNamesAvailableException e) {
                assertEquals(NoMoreNamesAvailableException.class, e.getClass());
            }
        }
    }

    @Test
    public void testEmptyDelimiter() throws Exception {

        String baseName = "OLD";

        IMemberRenamingRule newNameRule = produceNewNameRule("");

        try {
            initializeRule(newNameRule, baseName);
            // newNameRule.getNextName();
            fail("Should have faild with an InvalidDelimiterException");
        } catch (Throwable e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
        }
    }

    private void initializeRule(IMemberRenamingRule newNameRule, String memberName) throws Exception {
        initializeRule(newNameRule, memberName, null);
    }

    private void initializeRule(IMemberRenamingRule newNameRule, String memberName, String[] existingMemberPaths) throws Exception {
        newNameRule.initialize(null, LIBRARY_NAME, FILE_NAME, memberName);
        ((MemberRenamingRuleNumber)newNameRule).calculateLastMemberNameUsedOnSystem(existingMemberPaths);
    }

    private QSYSObjectPathName produceQSYSObjectPathName(String memberName) {
        return new QSYSObjectPathName(LIBRARY_NAME, FILE_NAME, memberName, "MBR");
    }

    private IMemberRenamingRule produceNewNameRule(String delimiter) {

        JUnitMemberRenamingRuleNumber newNameRule = new JUnitMemberRenamingRuleNumber();
        newNameRule.setFillGapsEnabled(false);
        newNameRule.setDelimiter(delimiter);
        newNameRule.setMinValue(1);
        newNameRule.setMaxValue(99);

        return newNameRule;
    }
}
