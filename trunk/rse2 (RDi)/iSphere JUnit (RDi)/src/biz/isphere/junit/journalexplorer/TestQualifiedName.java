/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit.journalexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import biz.isphere.journalexplorer.core.internals.QualifiedName;

public class TestQualifiedName {

    private static final String CONNECTION = "CONNECTION";
    private static final String LIBRARY = "LIBRARY";
    private static final String OBJECT = "OBJECT";

    private String getErrorMessage(String qualifiedName) {
        return "Invalid qualified object name: " + qualifiedName.trim().toUpperCase().replaceFirst("\\.", "/");
    }

    @Test
    public void testInvalidQsysObjectName() {

        String qualifiedName;

        // invalid delimiter between connection and object name
        qualifiedName = "connection.library/object";
        try {
            new QualifiedName(qualifiedName);
            fail("Should have faild: " + qualifiedName);
        } catch (Exception e) {
            assertEquals(java.lang.IllegalArgumentException.class, e.getClass());
            assertEquals(getErrorMessage(qualifiedName), e.getLocalizedMessage());
        }

        // invalid delimiter between library and object name
        qualifiedName = "library,object";
        try {
            new QualifiedName(qualifiedName);
            fail("Should have faild: " + qualifiedName);
        } catch (Exception e) {
            assertEquals(java.lang.IllegalArgumentException.class, e.getClass());
            assertEquals(getErrorMessage(qualifiedName), e.getLocalizedMessage());
        }

        // object name embedded into text
        qualifiedName = "before connection:library/object after";
        try {
            new QualifiedName(qualifiedName);
            fail("Should have faild: " + qualifiedName);
        } catch (Exception e) {
            assertEquals(java.lang.IllegalArgumentException.class, e.getClass());
            assertEquals(getErrorMessage(qualifiedName), e.getLocalizedMessage());
        }

    }

    @Test
    public void testQsysObjectName() {

        QualifiedName qName;

        qName = new QualifiedName("library/object");
        assertEquals(null, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getObjectName());

        qName = new QualifiedName("connection::library/object");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getObjectName());

    }

    @Test
    public void testParsingQsysObjectName() {

        QualifiedName qName;

        qName = QualifiedName.parse("before connection:library/object after");
        assert (qName == null);

        qName = QualifiedName.parse("connection::library/object");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getObjectName());

    }

    @Test
    public void testQSQTableName() {

        QualifiedName qName;

        qName = new QualifiedName("library.object");
        assertEquals(null, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getObjectName());

        qName = new QualifiedName("connection::library.object");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getObjectName());

    }

    @Test
    public void testCompareTo() {

        assertEquals(0, new QualifiedName("con1", "lib1", "obj1").compareTo(new QualifiedName("con1", "lib1", "obj1")));
        assertEquals(0, new QualifiedName(null, "lib1", "obj1").compareTo(new QualifiedName(null, "lib1", "obj1")));

        assertEquals(1, new QualifiedName("con1", "lib1", "obj1").compareTo(null));

        assertEquals(1, new QualifiedName("con1", "lib1", "obj2").compareTo(new QualifiedName(null, "lib1", "obj1")));
        assertEquals(1, new QualifiedName("con1", "lib1", "obj2").compareTo(new QualifiedName("con1", "lib1", "obj1")));
        assertEquals(1, new QualifiedName("con1", "lib2", "obj1").compareTo(new QualifiedName("con1", "lib1", "obj1")));
        assertEquals(1, new QualifiedName("con2", "lib1", "obj1").compareTo(new QualifiedName("con1", "lib1", "obj1")));

        assertEquals(-1, new QualifiedName(null, "lib1", "obj2").compareTo(new QualifiedName("con1", "lib1", "obj1")));
        assertEquals(-1, new QualifiedName("con1", "lib1", "obj1").compareTo(new QualifiedName("con1", "lib1", "obj2")));
        assertEquals(-1, new QualifiedName("con1", "lib1", "obj1").compareTo(new QualifiedName("con1", "lib2", "obj1")));
        assertEquals(-1, new QualifiedName("con1", "lib1", "obj1").compareTo(new QualifiedName("con2", "lib1", "obj1")));
    }

    @Test
    public void testEquals() {

        assertTrue(new QualifiedName("con1", "lib1", "obj1").equals(new QualifiedName("con1", "lib1", "obj1")));
        assertTrue(new QualifiedName(null, "lib1", "obj1").equals(new QualifiedName(null, "lib1", "obj1")));

        assertFalse(new QualifiedName("con1", "lib1", "obj1").equals(new QualifiedName(null, "lib1", "obj1")));
        assertFalse(new QualifiedName(null, "lib1", "obj2").equals(new QualifiedName(null, "lib1", "obj1")));

        assertFalse(new QualifiedName("con1", "lib1", "obj2").equals(new QualifiedName("con1", "lib1", "obj1")));
        assertFalse(new QualifiedName("con1", "lib2", "obj1").equals(new QualifiedName("con1", "lib1", "obj1")));
        assertFalse(new QualifiedName("con2", "lib1", "obj1").equals(new QualifiedName("con1", "lib1", "obj1")));
    }

    @Test
    public void testHashCode() {

        assertTrue(new QualifiedName("con1", "lib1", "obj1").hashCode() == new QualifiedName("con1", "lib1", "obj1").hashCode());

        assertFalse(new QualifiedName("con1", "lib1", "obj1").hashCode() == new QualifiedName(null, "lib1", "obj1").hashCode());
        assertFalse(new QualifiedName(null, "lib1", "obj2").hashCode() == new QualifiedName(null, "lib1", "obj1").hashCode());

        assertFalse(new QualifiedName("con1", "lib1", "obj2").hashCode() == new QualifiedName("con1", "lib1", "obj1").hashCode());
        assertFalse(new QualifiedName("con1", "lib2", "obj1").hashCode() == new QualifiedName("con1", "lib1", "obj1").hashCode());
        assertFalse(new QualifiedName("con2", "lib1", "obj1").hashCode() == new QualifiedName("con1", "lib1", "obj1").hashCode());
    }
}
