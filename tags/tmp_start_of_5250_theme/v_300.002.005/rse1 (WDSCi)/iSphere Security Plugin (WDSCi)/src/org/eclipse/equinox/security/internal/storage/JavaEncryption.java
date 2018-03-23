/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.eclipse.equinox.security.internal.storage;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class JavaEncryption {

    private static final String ALGO = "AES";

    private byte[] keyValue = null;
    private Key secretKey;

    public JavaEncryption(byte[] key) {
        this.keyValue = key;
    }

    public String encrypt(String Data) throws Exception {

        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);

        return encryptedValue;
    }

    public String decrypt(String encryptedData) throws Exception {

        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);

        return decryptedValue;
    }

    private Key generateKey() throws Exception {
        if (secretKey == null) {
            secretKey = new SecretKeySpec(keyValue, ALGO);
        }
        return secretKey;
    }

}
