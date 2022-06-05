package biz.isphere.junit;

import junit.framework.Assert;

import org.junit.Test;

import biz.isphere.base.internal.StringHelper;

public class TestGenericStringCompare {

    @Test
    public void testGeneric() throws Exception {

        Assert.assertTrue(StringHelper.matchesGeneric("Hello World", "*llo World*"));
        Assert.assertTrue(StringHelper.matchesGeneric("Hello World", "*LLO WORLD*"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLO WORLD", "*llo World*"));

        Assert.assertTrue(StringHelper.matchesGeneric("HELLO xxRLD", "*llo ??rld*"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLO WORLD", "*ll* ??rld*"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLOWORLD", "*ll*??rld*"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLOWORLD", "*"));

        Assert.assertFalse(StringHelper.matchesGeneric("HELLx WORLD", "*llo World*"));
        Assert.assertFalse(StringHelper.matchesGeneric("HELLO WORLD", "*llx ??rld*"));
        Assert.assertFalse(StringHelper.matchesGeneric("HELxOWORLD", "*ll*??rld*"));
        Assert.assertFalse(StringHelper.matchesGeneric(null, "*"));
    }

    @Test
    public void testAssertsFromTheInternet() {

        Assert.assertFalse(StringHelper.matchesGeneric("HELLO WORLD", "ELLO"));
        Assert.assertFalse(StringHelper.matchesGeneric("HELLO WORLD", "HELLO"));
        Assert.assertFalse(StringHelper.matchesGeneric("HELLO WORLD", "*HELLO"));
        Assert.assertFalse(StringHelper.matchesGeneric("HELLO WORLD", "HELLO WORLD2"));
        Assert.assertFalse(StringHelper.matchesGeneric("HELLO WORLD", "HELLO WORL"));

        Assert.assertTrue(StringHelper.matchesGeneric("HELLO WORLD", "*ELLO*"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLO WORLD", "HELLO*"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLO WORLD", "*LLO*"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLO WORLD", "HELLO WORLD"));
        Assert.assertTrue(StringHelper.matchesGeneric("HELLO WORLD", "hello world"));
    }

    @Test
    public void testEmbeddedDots() {

        Assert.assertFalse(StringHelper.matchesGeneric("PTF index is about to be destroyed (C G)", "*in.ex*"));
        Assert.assertTrue(StringHelper.matchesGeneric("PTF in.ex is about to be destroyed (C G)", "*in.ex*"));

    }

    @Test
    public void testEmbeddedBackslashes() {

        Assert.assertTrue(StringHelper.matchesGeneric("HELLO\\WORLD", "hello\\world"));

    }
}