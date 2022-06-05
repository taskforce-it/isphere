/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import junit.framework.Assert;

import org.junit.Test;

import biz.isphere.core.internal.Validator;

public class TestValidator {

    @Test
    public void testNameInstanceWithoutCcsid() throws Exception {

        Validator nameValidator = Validator.getNameInstance(null);

        boolean isValid = false;

        isValid = nameValidator.validate("");
        Assert.assertFalse("isValid must be false, because 'name' is empty", isValid);

        isValid = nameValidator.validate("F:OO");
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = nameValidator.validate("*FOO");
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = nameValidator.validate("FOO$@#"); // US special characters
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = nameValidator.validate("FOO$§#"); // German special characters
        Assert.assertTrue("isValid must be true, because 'name' is valid", isValid);

    }

    @Test
    public void testNameInstanceWithCcsid() throws Exception {

        Validator nameValidator = Validator.getNameInstance(Integer.valueOf(1141));

        boolean isValid = false;

        isValid = nameValidator.validate("");
        Assert.assertFalse("isValid must be false, because 'name' is empty", isValid);

        isValid = nameValidator.validate("F:OO");
        Assert.assertFalse("isValid must be false, because of ivalid character ':'", isValid);

        isValid = nameValidator.validate("*FOO");
        Assert.assertFalse("isValid must be false, because of invalid character '*'", isValid);

        isValid = nameValidator.validate("FOO$@#"); // US special characters
        Assert.assertFalse("isValid must be false, because of invalid character '@'", isValid);

        isValid = nameValidator.validate("FOO$§#"); // German special characters
        Assert.assertTrue("isValid must be true, because 'name' is valid", isValid);

    }

    @Test
    public void testLibraryNameInstanceWithoutCcsid() throws Exception {

        Validator libraryNameValidator = Validator.getLibraryNameInstance(null);

        boolean isValid = false;

        isValid = libraryNameValidator.validate("");
        Assert.assertFalse("isValid must be false, because 'name' is empty", isValid);

        isValid = libraryNameValidator.validate("F:OO");
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = libraryNameValidator.validate("*FOO");
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = libraryNameValidator.validate("FOO$@#"); // US special
                                                           // characters
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = libraryNameValidator.validate("FOO$§#"); // German special
                                                           // characters
        Assert.assertTrue("isValid must be true, because 'name' is valid", isValid);

    }

    @Test
    public void testLibraryNameInstanceWithCcsid() throws Exception {

        Validator libraryNameValidator = Validator.getLibraryNameInstance(Integer.valueOf(1141));

        boolean isValid = false;

        isValid = libraryNameValidator.validate("");
        Assert.assertFalse("isValid must be false, because 'name' is empty", isValid);

        isValid = libraryNameValidator.validate("F:OO");
        Assert.assertFalse("isValid must be false, because of ivalid character ':'", isValid);

        isValid = libraryNameValidator.validate("*FOO");
        Assert.assertFalse("isValid must be false, because of invalid character '*'", isValid);

        isValid = libraryNameValidator.validate("FOO$@#"); // US special
                                                           // characters
        Assert.assertFalse("isValid must be true, because of invalid character '@'", isValid);

        isValid = libraryNameValidator.validate("FOO$§#"); // German special
                                                           // characters
        Assert.assertTrue("isValid must be true, because 'name' is valid", isValid);

    }

    @Test
    public void testLibraryNameInstanceWithoutCcsidAndWithSpecialValues() throws Exception {

        Validator libraryNameValidator = Validator.getLibraryNameInstance(null, "*LIBL", "*CURLIB");

        boolean isValid = false;

        isValid = libraryNameValidator.validate("");
        Assert.assertFalse("isValid must be false, because 'name' is empty", isValid);

        isValid = libraryNameValidator.validate("F:OO");
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = libraryNameValidator.validate("*FOO");
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = libraryNameValidator.validate("FOO$@#"); // US special
                                                           // characters
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

        isValid = libraryNameValidator.validate("FOO$§#"); // German special
                                                           // characters
        Assert.assertTrue("isValid must be true, because 'name' is valid", isValid);

        isValid = libraryNameValidator.validate("*LIBL");
        Assert.assertTrue("isValid must be true altough 'name' is invalid, because character checking is disabled due to missing ccsid", isValid);

    }

    @Test
    public void testLibraryNameInstanceWithCcsidAndWithSpecialValues() throws Exception {

        Validator libraryNameValidator = Validator.getLibraryNameInstance(Integer.valueOf(1141), "*LIBL", "*CURLIB");

        boolean isValid = false;

        isValid = libraryNameValidator.validate("");
        Assert.assertFalse("isValid must be false, because 'name' is empty", isValid);

        isValid = libraryNameValidator.validate("F:OO");
        Assert.assertFalse("isValid must be false, because of ivalid character ':'", isValid);

        isValid = libraryNameValidator.validate("*FOO");
        Assert.assertFalse("isValid must be false, because of invalid character '*'", isValid);

        isValid = libraryNameValidator.validate("FOO$@#"); // US special
                                                           // characters
        Assert.assertFalse("isValid must be true, because of invalid character '@'", isValid);

        isValid = libraryNameValidator.validate("FOO$§#"); // German special
                                                           // characters
        Assert.assertTrue("isValid must be true, because 'name' is valid", isValid);

        isValid = libraryNameValidator.validate("*LIBL");
        Assert.assertTrue("isValid must be true, because '*LIBL' is valid", isValid);

    }

}
