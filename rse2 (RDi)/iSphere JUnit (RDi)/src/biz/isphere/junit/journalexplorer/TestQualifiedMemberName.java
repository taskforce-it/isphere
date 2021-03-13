/*******************************************************************************
 * Copyright (c) project_year-2021 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit.journalexplorer;

import static org.junit.Assert.assertEquals;
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
    public void testInvalidQsysObjectName() {

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
    public void testQsysObjectName() {

        QualifiedMemberName qName;

        qName = new QualifiedMemberName("library/object(member)");
        assertEquals(null, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

        qName = new QualifiedMemberName("connection:library/object(member)");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

        qName = new QualifiedMemberName("connection:library/object (member)");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

    }

    @Test
    public void testQSQTableName() {

        QualifiedMemberName qName;

        qName = new QualifiedMemberName("library.object(member)");
        assertEquals(null, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());

        qName = new QualifiedMemberName("connection:library.object(member)");
        assertEquals(CONNECTION, qName.getConnectionName());
        assertEquals(LIBRARY, qName.getLibraryName());
        assertEquals(OBJECT, qName.getFileName());
        assertEquals(MEMBER, qName.getMemberName());

    }

}
