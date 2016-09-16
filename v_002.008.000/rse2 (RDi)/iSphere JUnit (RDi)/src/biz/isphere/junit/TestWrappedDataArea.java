/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import java.io.IOException;
import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.exception.IllegalMethodAccessException;
import biz.isphere.rse.dataspace.rse.WrappedDataSpace;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.DecimalDataArea;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.LogicalDataArea;
import com.ibm.as400.access.ObjectAlreadyExistsException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * JUnit test suite to test the DataAreaDelegate.
 * <p>
 * This test suite requires the following system properties:
 * 
 * <pre>
 * Run Configuration -> Arguments -> VM arguments:
 * 
 *  -Disphere.junit.as400=ghentw.gfd.de
 *  -Disphere.junit.username=WEBUSER
 *  -Disphere.junit.password=WEBUSER
 * </pre>
 */
public class TestWrappedDataArea {

    private static String system = System.getProperty("isphere.junit.as400");
    private static String user = System.getProperty("isphere.junit.username");
    private static String password = System.getProperty("isphere.junit.password");

    private static AS400 as400;
    private static String library = "ISPHEREDVP";
    private static String text = "ISphere JUnit test object";

    private static CharacterDataArea char_dataArea = null;
    private static String char_type = AbstractWrappedDataSpace.CHARACTER;
    private static int char_length = 100;
    private static String char_name = "ISPHERE.C";
    private static String char_value = "The quick brown fox jumps over the lazy dog.";

    private static LogicalDataArea lgl_dataArea = null;
    private static String lgl_type = AbstractWrappedDataSpace.LOGICAL;
    private static int lgl_length = 1;
    private static String lgl_name = "ISPHERE.L";
    private static boolean lgl_value = true;

    private static DecimalDataArea dec_dataArea = null;
    private static String dec_type = AbstractWrappedDataSpace.DECIMAL;
    private static int dec_length = 18;
    private static int dec_decPos = 6;
    private static String dec_name = "ISPHERE.D";
    private static BigDecimal dec_value = new BigDecimal("123456789012.123456");

    @Test
    public void testIllegalAccessOfCharacterDataArea() throws Exception {
        
        RemoteObject object = new RemoteObject(null, char_name, library, ISeries.DTAARA, "");
        AbstractWrappedDataSpace char_delegate = new WrappedDataSpace(as400, object);
        
        try {
            char_delegate.getBooleanValue();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
        try {
            char_delegate.getDecimalValue();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
        try {
            char_delegate.getDecimalPositions();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
    }

    @Test
    public void testIllegalAccessOfLogicalDataArea() throws Exception {
        
        RemoteObject object = new RemoteObject(null, lgl_name, library, ISeries.DTAARA, "");
        AbstractWrappedDataSpace lgl_delegate = new WrappedDataSpace(as400, object);
        
        try {
            lgl_delegate.getStringValue();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
        try {
            lgl_delegate.getDecimalValue();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
        try {
            lgl_delegate.getDecimalPositions();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
    }

    @Test
    public void testIllegalAccessOfDecimalDataArea() throws Exception {
        
        RemoteObject object = new RemoteObject(null, dec_name, library, ISeries.DTAARA, "");
        AbstractWrappedDataSpace dec_delegate = new WrappedDataSpace(as400, object);
        
        try {
            dec_delegate.getBooleanValue();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
        try {
            dec_delegate.getStringValue();
        } catch (IllegalMethodAccessException e) {
            // exception seen ==> OK
        }
        
    }

    @Test
    public void testCharacterDataArea() throws Exception {

        RemoteObject object = new RemoteObject(null, char_name, library, ISeries.DTAARA, "");
        AbstractWrappedDataSpace delegate = new WrappedDataSpace(as400, object);

        Assert.assertEquals(char_type, delegate.getDataType());
        Assert.assertEquals(char_length, delegate.getLength());
        Assert.assertEquals(char_value, StringHelper.trimR(delegate.getStringValue()));

    }

    @Test
    public void testLogicalDataArea() throws Exception {

        RemoteObject object = new RemoteObject(null, lgl_name, library, ISeries.DTAARA, "");
        AbstractWrappedDataSpace delegate = new WrappedDataSpace(as400, object);

        Assert.assertEquals(lgl_type, delegate.getDataType());
        Assert.assertEquals(lgl_length, delegate.getLength());
        Assert.assertEquals(lgl_value, delegate.getBooleanValue().booleanValue());

    }

    @Test
    public void testDecimalDataArea() throws Exception {

        RemoteObject object = new RemoteObject(null, dec_name, library, ISeries.DTAARA, "");
        AbstractWrappedDataSpace delegate = new WrappedDataSpace(as400, object);

        Assert.assertEquals(dec_type, delegate.getDataType());
        Assert.assertEquals(dec_length, delegate.getLength());
        Assert.assertEquals(dec_decPos, delegate.getDecimalPositions());
        Assert.assertEquals(dec_value, delegate.getDecimalValue());

    }

    @BeforeClass
    public static void setupTestCase() throws Exception {

        as400 = new AS400(system, user, password);

        createCharacterDataArea();
        createLogicalDataArea();
        createDecimalDataArea();
    }

    @AfterClass
    public static void tearDownTestCase() throws Exception {
        deleteCharacterDataArea();
        deleteLogicalDataArea();
        deleteDecimalDataArea();
    }

    private static void createCharacterDataArea() throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException,
        ObjectAlreadyExistsException, ObjectDoesNotExistException {

        // Create a CharacterDataArea object.
        QSYSObjectPathName path = new QSYSObjectPathName(library, char_name, "DTAARA");
        char_dataArea = new CharacterDataArea(as400, path.getPath());

        try {
            char_dataArea.delete();
        } catch (Exception e) {
            // ignore exception
        }

        // Create the character data area on the system using default values.
        char_dataArea.create(char_length, " ", text, "*LIBCRTAUT");

        // Write to the data area.
        char_dataArea.write(char_value);
    }

    private static void createLogicalDataArea() throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException,
        ObjectAlreadyExistsException, ObjectDoesNotExistException {

        // Create a CharacterDataArea object.
        QSYSObjectPathName path = new QSYSObjectPathName(library, lgl_name, "DTAARA");
        lgl_dataArea = new LogicalDataArea(as400, path.getPath());

        try {
            lgl_dataArea.delete();
        } catch (Exception e) {
            // ignore exception
        }

        // Create the character data area on the system using default values.
        lgl_dataArea.create(false, text, "*LIBCRTAUT");

        // Write to the data area.
        lgl_dataArea.write(lgl_value);
    }

    private static void createDecimalDataArea() throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException,
        ObjectAlreadyExistsException, ObjectDoesNotExistException {

        // Create a CharacterDataArea object.
        QSYSObjectPathName path = new QSYSObjectPathName(library, dec_name, "DTAARA");
        dec_dataArea = new DecimalDataArea(as400, path.getPath());

        try {
            dec_dataArea.delete();
        } catch (Exception e) {
            // ignore exception
        }

        // Create the character data area on the system using default values.
        dec_dataArea.create(dec_length, dec_decPos, new BigDecimal(0), text, "*LIBCRTAUT");

        // Write to the data area.
        dec_dataArea.write(dec_value);
    }

    private static void deleteCharacterDataArea() throws Exception {
        if (char_dataArea != null) {
            char_dataArea.delete();
            char_dataArea = null;
        }
    }

    private static void deleteLogicalDataArea() throws Exception {
        if (lgl_dataArea != null) {
            lgl_dataArea.delete();
            lgl_dataArea = null;
        }
    }

    private static void deleteDecimalDataArea() throws Exception {
        if (dec_dataArea != null) {
            dec_dataArea.delete();
            dec_dataArea = null;
        }
    }

}
