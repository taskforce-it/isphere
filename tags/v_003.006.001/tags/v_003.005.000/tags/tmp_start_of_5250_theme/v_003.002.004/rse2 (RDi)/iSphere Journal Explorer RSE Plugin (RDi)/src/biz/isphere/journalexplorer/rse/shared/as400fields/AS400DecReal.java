/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.as400fields;

import java.math.BigDecimal;

import com.ibm.as400.access.AS400ByteArray;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400DecFloat;

public class AS400DecReal extends AS400ByteArray {

    private static final long serialVersionUID = -5047314778801863039L;

    private AS400DecFloat decReal;

    public AS400DecReal() {
        super(8);

        decReal = new AS400DecFloat(16);
    }

    @Override
    public Object clone() {
        return super.clone();
    }

    @Override
    public int getByteLength() {
        return super.getByteLength();
    }

    @Override
    public Object getDefaultValue() {
        return new BigDecimal(0);
    }

    @Override
    public int getInstanceType() {
        return AS400DataType.TYPE_BYTE_ARRAY;
    }

    public Class<?> getJavaType() {
        return BigDecimal.class;
    }

    @Override
    public byte[] toBytes(Object object) {
        byte[] objectBytes = new byte[getByteLength()];
        toBytes(object, objectBytes, 0);
        return objectBytes;
    }

    @Override
    public int toBytes(Object object, byte[] bytes) {
        return toBytes(object, bytes, 0);
    }

    @Override
    public int toBytes(Object object, byte[] bytes, int offset) {

        return 0;
    }

    @Override
    public Object toObject(byte[] serverValue) {
        return toObject(serverValue, 0);
    }

    @Override
    public Object toObject(byte[] serverValue, int offset) {

        decReal.getByteLength();

        return decReal.toObject(serverValue, offset);
    }
}
