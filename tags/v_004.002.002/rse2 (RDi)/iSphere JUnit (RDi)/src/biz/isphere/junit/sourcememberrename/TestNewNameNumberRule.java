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

import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.MemberRenamingRuleNumber;

public class TestNewNameNumberRule {

    private static String[] delimiters = { ".", "#" };

    @Test
    public void testGetNextNameFirst() throws Exception {

        for (String delimiter : delimiters) {

            String oldName = "OLD";

            MemberRenamingRuleNumber newNameRule = new MemberRenamingRuleNumber();
            newNameRule.setDelimiter(delimiter);
            newNameRule.setMinValue(1);
            newNameRule.setMaxValue(999);

            String newName = newNameRule.getNextName(oldName);

            assertEquals("OLD" + delimiter + "001", newName);
        }
    }

    @Test
    public void testGetNextNameSecond() throws Exception {

        for (String delimiter : delimiters) {

            String oldName = "OLD" + delimiter + "1";

            MemberRenamingRuleNumber newNameRule = new MemberRenamingRuleNumber();
            newNameRule.setDelimiter(delimiter);
            newNameRule.setMinValue(1);
            newNameRule.setMaxValue(999);

            String newName = newNameRule.getNextName(oldName);

            assertEquals("OLD" + delimiter + "002", newName);
        }
    }

    @Test
    public void testGetNextNameLast() throws Exception {

        for (String delimiter : delimiters) {

            String oldName = "OLD" + delimiter + "998";

            MemberRenamingRuleNumber newNameRule = new MemberRenamingRuleNumber();
            newNameRule.setDelimiter(delimiter);
            newNameRule.setMinValue(1);
            newNameRule.setMaxValue(999);

            String newName = newNameRule.getNextName(oldName);

            assertEquals("OLD" + delimiter + "999", newName);
        }
    }

    @Test
    public void testGetNextNameNoMoreNames() throws Exception {

        for (String delimiter : delimiters) {

            String oldName = "OLD" + delimiter + "999";

            MemberRenamingRuleNumber newNameRule = new MemberRenamingRuleNumber();
            newNameRule.setDelimiter(delimiter);
            newNameRule.setMinValue(1);
            newNameRule.setMaxValue(999);

            try {
                newNameRule.getNextName(oldName);
                fail("Should have faild with a NoMoreNamesAvailableException");
            } catch (NoMoreNamesAvailableException e) {
                assertEquals(NoMoreNamesAvailableException.class, e.getClass());
            }
        }
    }

    @Test
    public void testEmptyDelimiter() throws Exception {

        String oldName = "OLD";

        MemberRenamingRuleNumber newNameRule = new MemberRenamingRuleNumber();
        newNameRule.setDelimiter("");
        newNameRule.setMinValue(1);
        newNameRule.setMaxValue(999);

        try {
            newNameRule.getNextName(oldName);
            fail("Should have faild with a RuntimeException");
        } catch (Throwable e) {
            assertEquals(RuntimeException.class, e.getClass());
        }
    }
}
