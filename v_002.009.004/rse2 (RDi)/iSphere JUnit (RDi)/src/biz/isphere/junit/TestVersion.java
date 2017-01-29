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
            new Version("2.1.1.b0001");
            Assert.fail("Number of beta part is too long.");
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

        try {
            new Version("2.1.1.r1");
            Assert.fail("Release qualifier must not specify a number.");
        } catch (IllegalArgumentException e) {
            // exception see ==> OK
            Assert.assertEquals("Invalid version format", e.getMessage());
        }

    }

    @Test
    public void testLegalVersionNumbers() throws Exception {

        Assert.assertTrue(new Version("1.10.r").compareTo(new Version("1.1.1.r")) > 0);

        Assert.assertTrue(new Version("1.1.r").compareTo(new Version("1.1.1.r")) < 0);
        Assert.assertFalse(new Version("1.1.r").equals(new Version("1.1.1.r")));

        Assert.assertTrue(new Version("2.0.r").compareTo(new Version("1.9.9.r")) > 0);
        Assert.assertFalse(new Version("2.0.r").equals(new Version("1.9.9.r")));

        Assert.assertTrue(new Version("1.0.r").compareTo(new Version("1.r")) == 0);
        Assert.assertTrue(new Version("1.0.r").equals(new Version("1.r")));

        Assert.assertTrue(new Version("1.0.r").compareTo(null) > 0);
        Assert.assertFalse(new Version("1.0.r").equals(null));

        List<Version> versions = new ArrayList<Version>();
        versions.add(new Version("1.00.1.r")); // max version
        versions.add(new Version("1.0.5.r"));
        versions.add(new Version("1.01.0.r"));
        versions.add(new Version("2.r")); // min version
        String minVersion = Collections.min(versions).get();
        String maxVersion = Collections.max(versions).get();

        Assert.assertEquals("1.0.1.r", minVersion);
        Assert.assertEquals("2.r", maxVersion);

        Version a = new Version("2.06.r");
        Version b = new Version("2.060.r");
        Assert.assertFalse(a.equals(b));
    }

    @Test
    public void testBetaVersionNumbers() throws Exception {

        Assert.assertTrue(new Version("2.4.2.r").compareTo(new Version("2.4.2.b16")) > 0);
        Assert.assertTrue(new Version("2.4.r").compareTo(new Version("2.4.1.b1")) < 0);
        Assert.assertTrue(new Version("2.4.r").compareTo(new Version("2.4.b1")) > 0);

        Assert.assertTrue(new Version("2.4.2.b1").compareTo(new Version("2.4.2.b016")) < 0);
        Assert.assertTrue(new Version("2.4.2.b005").compareTo(new Version("2.4.2.b6")) < 0);

        Assert.assertFalse(new Version("2.4.2.r").isBeta());
        Assert.assertTrue(new Version("2.4.2.b010").isBeta());
        Assert.assertTrue(new Version("2.4.2.b5").isBeta());
        Assert.assertTrue(new Version("2.4.b1").isBeta());

    }

    @Test
    public void testOldVersionNumbers() throws Exception {

        Assert.assertTrue(new Version("2.4.0").get().equals("2.4.0.r"));
        Assert.assertTrue(new Version("2.5.0").get().equals("2.5.0.r"));
        Assert.assertTrue(new Version("2.5.1").get().equals("2.5.1.r"));
        Assert.assertTrue(new Version("2.5.2").get().equals("2.5.2.r"));

    }

}
