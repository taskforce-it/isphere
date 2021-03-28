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

import biz.isphere.journalexplorer.core.internals.QualifiedMemberName;
import biz.isphere.journalexplorer.core.internals.QualifiedName;

public class TestQualifiedMemberName {

    private static final String CONNECTION = "CONNECTION";
    private static final String LIBRARY = "LIBRARY";
    private static final String OBJECT = "OBJECT";
    private static final String MEMBER = "MEMBER";

    private String getErrorMessage(String qualifiedName) {
        return "Invalid qualified object name: " + qualifiedName.trim().toUpperCase().replaceFirst("\\.", "/");
    }

    @Test
    public void testInvalidQsysFileName() {

        String qualifiedName;

        // invalid delimiter between connection and object name
        qualifiedName = "connection.library/object(member)";
        try {
            new QualifiedName(qualifiedName);
            fail("Should have faild: " + qualifiedName);
        } catch (Exception e) {
            assertEquals(java.lang.IllegalArgumentException.class, e.getClass());
            assertEquals(getErrorMessage(qualifiedName), e.getLocalizedMessage());
        }

        // too many spaces between file and member names
        qualifiedName = "connection:library/object  (member)";
        try {
            new QualifiedName(qualifiedName);
            fail("Should have faild: " + qualifiedName);
        } catch (Exception e) {
            assertEquals(java.lang.IllegalArgumentException.class, e.getClass());
            assertEquals(getErrorMessage(qualifiedName), e.getLocalizedMessage());
        }

    }

    @Test
    public void testQsysFileName() {

        QualifiedMemberName qName;

        qName = new QualifiedMemberName("library/object(member)");
        assertEquals(null, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

        qName = new QualifiedMemberName("connection::library/object(member)");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

        qName = new QualifiedMemberName("connection::library/object (member)");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

    }

    @Test
    public void testParsingQsysFileName() {

        QualifiedMemberName qName;

        qName = QualifiedMemberName.parse("before connection:library/object (member) after");
        assertEquals(true, null == qName);

        qName = QualifiedMemberName.parse("connection::library/object (member)");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());
        assertEquals(MEMBER, qName.getMemberName());

    }

    @Test
    public void testQSQTableName() {

        QualifiedMemberName qName;

        qName = new QualifiedMemberName("library.object(member)");
        assertEquals(null, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

        qName = new QualifiedMemberName("connection::library.object(member)");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());
        assertEquals(MEMBER, qName.getMemberName());

    }

    @Test
    public void testCompareTo() {

        assertEquals(0, new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").compareTo(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));

        assertEquals(1, new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").compareTo(null));

        assertEquals(1, new QualifiedMemberName("con1", "lib1", "obj1", "mbr2").compareTo(new QualifiedMemberName(null, "lib1", "obj1", "mbr1")));
        assertEquals(1, new QualifiedMemberName("con1", "lib1", "obj1", "mbr2").compareTo(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));
        assertEquals(1, new QualifiedMemberName("con1", "lib1", "obj2", "mbr1").compareTo(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));
        assertEquals(1, new QualifiedMemberName("con1", "lib2", "obj1", "mbr1").compareTo(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));
        assertEquals(1, new QualifiedMemberName("con2", "lib1", "obj1", "mbr1").compareTo(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));

        assertEquals(-1, new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").compareTo(new QualifiedMemberName("con1", "lib1", "obj1", "mbr2")));
        assertEquals(-1, new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").compareTo(new QualifiedMemberName("con1", "lib1", "obj2", "mbr1")));
        assertEquals(-1, new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").compareTo(new QualifiedMemberName("con1", "lib2", "obj1", "mbr1")));
        assertEquals(-1, new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").compareTo(new QualifiedMemberName("con2", "lib1", "obj1", "mbr1")));
    }

    @Test
    public void testEquals() {

        assertTrue(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").equals(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));

        assertFalse(new QualifiedMemberName("con1", "lib1", "obj1", "mbr2").equals(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));
        assertFalse(new QualifiedMemberName("con1", "lib1", "obj2", "mbr1").equals(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));
        assertFalse(new QualifiedMemberName("con1", "lib2", "obj1", "mbr1").equals(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));
        assertFalse(new QualifiedMemberName("con2", "lib1", "obj1", "mbr1").equals(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")));
    }

    @Test
    public void testHashCode() {

        assertTrue(new QualifiedMemberName("con1", "lib1", "obj1", "mbr1").hashCode() == new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")
            .hashCode());

        assertFalse(new QualifiedMemberName("con1", "lib1", "obj1", "mbr2").hashCode() == new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")
            .hashCode());
        assertFalse(new QualifiedMemberName("con1", "lib1", "obj2", "mbr1").hashCode() == new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")
            .hashCode());
        assertFalse(new QualifiedMemberName("con1", "lib2", "obj1", "mbr1").hashCode() == new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")
            .hashCode());
        assertFalse(new QualifiedMemberName("con2", "lib1", "obj1", "mbr1").hashCode() == new QualifiedMemberName("con1", "lib1", "obj1", "mbr1")
            .hashCode());
    }
}
