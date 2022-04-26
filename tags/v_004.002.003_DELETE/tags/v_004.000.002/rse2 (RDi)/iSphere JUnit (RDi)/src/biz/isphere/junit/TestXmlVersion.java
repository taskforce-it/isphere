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

import biz.isphere.core.resourcemanagement.InvalidXmlVersionNumberFormatException;
import biz.isphere.core.resourcemanagement.XmlVersion;

public class TestXmlVersion {

    private static final String INVALID_VERSION_NUMBER_FORMAT = "Invalid version number format";

    @Test
    public void testIllegalVersionNumbers() throws Exception {

        try {
            new XmlVersion(null);
        } catch (InvalidXmlVersionNumberFormatException e) {
            // exception see ==> OK
            Assert.assertEquals(INVALID_VERSION_NUMBER_FORMAT, e.getMessage());
        }

        try {
            new XmlVersion("2.1.a").get();
            Assert.fail("Version number must not contain characters.");
        } catch (InvalidXmlVersionNumberFormatException e) {
            // exception see ==> OK
            Assert.assertEquals(INVALID_VERSION_NUMBER_FORMAT, e.getMessage());
        } catch (Throwable e) {
            Assert.assertEquals("Unexpected exception type", e.getMessage());
        }

        try {
            new XmlVersion(".1.1").get();
            Assert.fail("Version number must not start with a dot.");
        } catch (InvalidXmlVersionNumberFormatException e) {
            // exception see ==> OK
            Assert.assertEquals(INVALID_VERSION_NUMBER_FORMAT, e.getMessage());
        }

        try {
            new XmlVersion("2.1.1.1").get();
            Assert.fail("Version number must not have more than 3 segments.");
        } catch (InvalidXmlVersionNumberFormatException e) {
            // exception see ==> OK
            Assert.assertEquals(INVALID_VERSION_NUMBER_FORMAT, e.getMessage());
        }

        try {
            new XmlVersion("2.1.1.b").get();
            Assert.fail("Version number must not have more than 3 segments.");
        } catch (InvalidXmlVersionNumberFormatException e) {
            // exception see ==> OK
            Assert.assertEquals(INVALID_VERSION_NUMBER_FORMAT, e.getMessage());
        }

        try {
            new XmlVersion("2.b1.1").get();
            Assert.fail("Version number must not contain characters.");
        } catch (InvalidXmlVersionNumberFormatException e) {
            // exception see ==> OK
            Assert.assertEquals(INVALID_VERSION_NUMBER_FORMAT, e.getMessage());
        }

    }

    @Test
    public void testLegalVersionNumbers() throws Exception {

        Assert.assertTrue(new XmlVersion("1.10").compareTo(new XmlVersion("1.1.1")) > 0);

        Assert.assertTrue(new XmlVersion("1.1").compareTo(new XmlVersion("1.1.1")) < 0);
        Assert.assertFalse(new XmlVersion("1.1").equals(new XmlVersion("1.1.1")));

        Assert.assertTrue(new XmlVersion("2.0").compareTo(new XmlVersion("1.9.9")) > 0);
        Assert.assertFalse(new XmlVersion("2.0").equals(new XmlVersion("1.9.9")));

        Assert.assertTrue(new XmlVersion("1.0").compareTo(new XmlVersion("1")) == 0);
        Assert.assertTrue(new XmlVersion("1.0").equals(new XmlVersion("1")));

        Assert.assertTrue(new XmlVersion("1.0").compareTo((String)null) > 0);
        Assert.assertTrue(new XmlVersion("1.0").compareTo((XmlVersion)null) > 0);
        Assert.assertFalse(new XmlVersion("1.0").equals(null));

        List<XmlVersion> versions = new ArrayList<XmlVersion>();
        versions.add(new XmlVersion("1.00.1")); // max version
        versions.add(new XmlVersion("1.0.5"));
        versions.add(new XmlVersion("1.01.0"));
        versions.add(new XmlVersion("2")); // min version
        String minVersion = Collections.min(versions).get();
        String maxVersion = Collections.max(versions).get();

        Assert.assertEquals("1.0.1", minVersion);
        Assert.assertEquals("2", maxVersion);

        XmlVersion a = new XmlVersion("2.06");
        XmlVersion b = new XmlVersion("2.060");
        Assert.assertFalse(a.equals(b));
    }

}
