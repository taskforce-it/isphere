/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import org.junit.Test;

import biz.isphere.base.internal.StringHelper;
import junit.framework.Assert;

public class TestStringHelper {

    @Test
    public void testDoubleQuotes() {

        String given = "My string with 'quotes'.";
        String expected = "My string with ''quotes''.";
        String actual = StringHelper.doubleQuotes(given);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAddQuotes() {

        String given = "My unquoted string.";
        String expected = "'My unquoted string.'";
        String actual = StringHelper.addQuotes(given);
        Assert.assertEquals(expected, actual);

        given = "My string with 'embedded' quotes.";
        expected = "'My string with ''embedded'' quotes.'";
        actual = StringHelper.addQuotes(given);
        Assert.assertEquals(expected, actual);
    }

}
