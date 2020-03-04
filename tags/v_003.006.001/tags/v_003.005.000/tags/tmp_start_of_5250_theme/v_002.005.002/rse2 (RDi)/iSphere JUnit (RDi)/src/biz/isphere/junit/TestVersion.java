/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import biz.isphere.core.internal.Version;

public class TestVersion {

    @Test
    public void testIllegalVersionNumbers() throws Exception {

        try {
            new Version(null);
            Assert.fail("NULL is not allowed for the constructor.");
        } catch (IllegalArgumentException e) {
            // exception see ==> OK
            Assert.assertEquals("Version can not be null", e.getMessage());
        }

        try {
            new Version("2.1.a");
            Assert.fail("Version number must not contain characters.");
        } catch (IllegalArgumentException e) {
            // exception see ==> OK
            Assert.assertEquals("Invalid version format", e.getMessage());
        }

        try {
            new Version(".1.1");
            Assert.fail("Version number must not start with a dot.");
        } catch (IllegalArgumentException e) {
            // exception see ==> OK
            Assert.assertEquals("Invalid version format", e.getMessage());
        }

        try {
            new Version("2.1.1.b");
            Assert.fail("Beta part must specify the beta number.");
        } catch (IllegalArgumentException e) {
            // exception see ==> OK
            Assert.assertEquals("Invalid version format", e.getMessage());
        }

        try {
            new Version("2.b1.1");
            Assert.fail("Beta part must be at the end.");
        } catch (IllegalArgumentException e) {
            // exception see ==> OK
            Assert.assertEquals("Invalid version format", e.getMessage());
        }

    }

    @Test
    public void testLegalVersionNumbers() throws Exception {

        Assert.assertTrue(new Version("1.1").compareTo(new Version("1.1.1")) < 0);
        Assert.assertFalse(new Version("1.1").equals(new Version("1.1.1")));

        Assert.assertTrue(new Version("2.0").compareTo(new Version("1.9.9")) > 0);
        Assert.assertFalse(new Version("2.0").equals(new Version("1.9.9")));

        Assert.assertTrue(new Version("1.0").compareTo(new Version("1")) == 0);
        Assert.assertTrue(new Version("1.0").equals(new Version("1")));

        Assert.assertTrue(new Version("1.0").compareTo(null) > 0);
        Assert.assertFalse(new Version("1.0").equals(null));

        List<Version> versions = new ArrayList<Version>();
        versions.add(new Version("1.00.1")); // max version
        versions.add(new Version("1.0.5"));
        versions.add(new Version("1.01.0"));
        versions.add(new Version("2")); // min version
        String minVersion = Collections.min(versions).get();
        String maxVersion = Collections.max(versions).get();

        Assert.assertEquals("1.0.1", minVersion);
        Assert.assertEquals("2", maxVersion);

        Version a = new Version("2.06");
        Version b = new Version("2.060");
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void testBetaVersionNumbers() throws Exception {

        Assert.assertTrue(new Version("2.4.2").compareTo(new Version("2.4.2.b16")) > 0);
        Assert.assertTrue(new Version("2.4.2.b010").compareTo(new Version("2.4.2.b010")) == 0);
        Assert.assertTrue(new Version("2.4.2.b5").compareTo(new Version("2.4.2.b6")) < 0);
        Assert.assertTrue(new Version("2.4.b1").compareTo(new Version("2.4.b2")) < 0);

        Assert.assertFalse(new Version("2.4.2").isBeta());
        Assert.assertTrue(new Version("2.4.2.b010").isBeta());
        Assert.assertTrue(new Version("2.4.2.b5").isBeta());
        Assert.assertTrue(new Version("2.4.b1").isBeta());
        
    }

}
