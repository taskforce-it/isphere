/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestString {

    @Test
    public void testMid() throws Exception {

        final String value = "DEMO01";

        assertEquals("mid() must return 'DEMO01'", "DEMO01", mid(value, 1, 6));
        assertEquals("mid() must return 'DEMO0'", "DEMO0", mid(value, 1, 5));
        assertEquals("mid() must return 'D'", "D", mid(value, 1, 1));

        assertEquals("mid() must return 'EMO01'", "EMO01", mid(value, 2, 5));

        assertEquals("mid() must return an empty string", "", mid(value, 7, 0));

        assertEquals("mid() must return an empty string", "", mid(value, 1, 0));

        try {
            mid(value, 2, 80);
            fail("Must fail, because length exceeds maximum of 5");
        } catch (Exception e) {
            assertEquals(StringIndexOutOfBoundsException.class, e.getClass());
        }

        try {
            mid(value, 0, 10);
            fail("Start position is out of range: 0");
        } catch (Exception e) {
            assertEquals(StringIndexOutOfBoundsException.class, e.getClass());
        }

        try {
            mid(value, 8, 1);
            fail("Must fail, because start position is greater than string length + 1");
        } catch (Exception e) {
            assertEquals(StringIndexOutOfBoundsException.class, e.getClass());
        }

        try {
            mid(value, 7, 1);
            fail("Must fail, because mid() returns an empty string at position 7");
        } catch (Exception e) {
            assertEquals(StringIndexOutOfBoundsException.class, e.getClass());
        }

    }

    private String mid(String value, int start, int length) {
        return value.substring(start - 1, start + length - 1);
    }
}