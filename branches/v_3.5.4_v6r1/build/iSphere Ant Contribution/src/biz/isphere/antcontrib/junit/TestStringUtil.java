/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.junit;

import org.junit.Test;

import biz.isphere.antcontrib.utils.StringUtil;
import static org.junit.Assert.*;

public class TestStringUtil {

    @Test
    public void testMid() {
        
        assertTrue("Hello".equals(StringUtil.trim("Hello  ", " ")));
        assertTrue("Hello".equals(StringUtil.trim("  Hello", " ")));
        assertTrue("Hello".equals(StringUtil.trim("  Hello  ", " ")));
        
        assertTrue("Hello".equals(StringUtil.trim("Hello**", "*")));
        assertTrue("Hello".equals(StringUtil.trim("**Hello", "*")));
        assertTrue("Hello".equals(StringUtil.trim("**Hello**", "*")));
        
        assertTrue("Hello".equals(StringUtil.trim("HelloXX", "X")));
        assertTrue("Hello".equals(StringUtil.trim("XXHello", "X")));
        assertTrue("Hello".equals(StringUtil.trim("XXHelloXX", "X")));
    }

    @Test
    public void testIsNullOrEmpty() {

        assertTrue(StringUtil.isNullOrEmpty(null));
        assertTrue(StringUtil.isNullOrEmpty(""));

        assertFalse(StringUtil.isNullOrEmpty(" "));
        assertFalse(StringUtil.isNullOrEmpty("abc"));
    }

    @Test
    public void testNullValuesPositives() {

        // Positive null values Tests
        assertTrue(StringUtil.matchWildcard(null, null));
        assertTrue(StringUtil.matchWildcard(null, ""));
        assertTrue(StringUtil.matchWildcard(null, " "));

        assertTrue(StringUtil.matchWildcard("", ""));
        assertTrue(StringUtil.matchWildcard("", " "));

        assertTrue(StringUtil.matchWildcard(null, "abc"));
        assertTrue(StringUtil.matchWildcard("", "abc"));

        assertTrue(StringUtil.matchWildcard("*", null));
        assertTrue(StringUtil.matchWildcard("*", ""));
        assertTrue(StringUtil.matchWildcard("*", " "));
    }

    @Test
    public void testNullValuesNegatives() {

        // Negative null values Tests
        assertFalse(StringUtil.matchWildcard("?", null));
        assertFalse(StringUtil.matchWildcard("*?", null));
        assertFalse(StringUtil.matchWildcard(" ", null));

        assertFalse(StringUtil.matchWildcard("?", ""));
        assertFalse(StringUtil.matchWildcard("*?", ""));
        assertFalse(StringUtil.matchWildcard(" ", ""));
    }

    @Test
    public void testPositives() {

        // Positive Tests
        assertTrue(StringUtil.matchWildcard(" ", " "));
        assertTrue(StringUtil.matchWildcard("*", ""));
        assertTrue(StringUtil.matchWildcard("?", " "));

        assertTrue(StringUtil.matchWildcard("*?", " "));
        assertTrue(StringUtil.matchWildcard("?*", " "));

        assertTrue(StringUtil.matchWildcard("*", "a"));
        assertTrue(StringUtil.matchWildcard("*", "ab"));
        assertTrue(StringUtil.matchWildcard("?", "a"));
        assertTrue(StringUtil.matchWildcard("*?", "abc"));
        assertTrue(StringUtil.matchWildcard("?*", "abc"));
        assertTrue(StringUtil.matchWildcard("*abc", "abc"));
        assertTrue(StringUtil.matchWildcard("*abc*", "abc"));
        assertTrue(StringUtil.matchWildcard("*a*bc*", "aXXXbc"));
    }

    @Test
    public void testNegatives() {

        // Negative Tests
        assertFalse(StringUtil.matchWildcard("*a", ""));
        assertFalse(StringUtil.matchWildcard("a*", ""));
        assertFalse(StringUtil.matchWildcard("*b*", "a"));
        assertFalse(StringUtil.matchWildcard("b*a", "ab"));
        assertFalse(StringUtil.matchWildcard("??", "a"));
        assertFalse(StringUtil.matchWildcard("*?", ""));
        assertFalse(StringUtil.matchWildcard("??*", "a"));
        assertFalse(StringUtil.matchWildcard("*abc", "abX"));
        assertFalse(StringUtil.matchWildcard("*abc*", "Xbc"));
        assertFalse(StringUtil.matchWildcard("*a*bc*", "ac"));
        assertFalse(StringUtil.matchWildcard("*abc ", "abc"));
        assertFalse(StringUtil.matchWildcard("*abc* ", "abc"));

        assertFalse(StringUtil.matchWildcard("abcd", "abc"));
        assertFalse(StringUtil.matchWildcard("abc", "abcd"));
    }
}
