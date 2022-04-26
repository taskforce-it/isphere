/*******************************************************************************
 * Copyright (c) project_year-2021 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit.base;

import static org.junit.Assert.assertArrayEquals;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;

import biz.isphere.base.internal.ByteHelper;

public class TestByteHelper {

    @Test
    public void testParseHexString() {

        String hexString;
        byte[] expecteds;
        byte[] actuals;

        hexString = "000102030405060708090A0B0C0D0E0F";
        expecteds = DatatypeConverter.parseHexBinary(hexString);
        actuals = ByteHelper.parseHexString(hexString);
        assertArrayEquals(expecteds, actuals);

        hexString = "00102030405060708090A0B0C0D0E0F0";
        expecteds = DatatypeConverter.parseHexBinary(hexString);
        actuals = ByteHelper.parseHexString(hexString);
        assertArrayEquals(expecteds, actuals);

        hexString = "55AA";
        expecteds = DatatypeConverter.parseHexBinary(hexString);
        actuals = ByteHelper.parseHexString(hexString);
        assertArrayEquals(expecteds, actuals);
    }

}
