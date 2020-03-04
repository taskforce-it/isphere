/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.model;

import biz.isphere.journalexplorer.base.interfaces.IDatatypeConverterDelegate;

public final class DatatypeConverterDelegate implements IDatatypeConverterDelegate {

    public byte[] parseHexBinary(String paramString) {
        int i = paramString.length();

        if (i % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + paramString);
        }
        byte[] arrayOfByte = new byte[i / 2];

        for (int j = 0; j < i; j += 2) {
            int k = hexToBin(paramString.charAt(j));
            int m = hexToBin(paramString.charAt(j + 1));
            if ((k == -1) || (m == -1)) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + paramString);
            }
            arrayOfByte[(j / 2)] = ((byte)(k * 16 + m));
        }

        return arrayOfByte;
    }

    private static int hexToBin(char paramChar) {
        if (('0' <= paramChar) && (paramChar <= '9')) return paramChar - '0';
        if (('A' <= paramChar) && (paramChar <= 'F')) return paramChar - 'A' + 10;
        if (('a' <= paramChar) && (paramChar <= 'f')) return paramChar - 'a' + 10;
        return -1;
    }

}
