/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

import biz.isphere.base.internal.BigDecimalHelper;
import biz.isphere.base.internal.ByteHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspace.rse.DE;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.exception.ValueTooLargeException;

import com.ibm.as400.access.CharConverter;

/**
 * This class serves as the data provider for the data space editors. It does
 * not now anything about of the source of the data, such as data areas and user
 * spaces, but works on their byte data.
 * <p>
 * In terms of iSphere 'data space objects' are data areas and user spaces. The
 * term 'data space object' is a made-up of 'Data Area' and 'User Space'.
 */
public class DDataSpaceValue {

    private CharConverter converter;
    private byte[] bytes;
    private String dataType;

    private RemoteObject remoteObject;

    public static DDataSpaceValue getCharacterInstance(RemoteObject remoteObject, String ccsidEncoding, byte[] bytes)
        throws UnsupportedEncodingException {
        DDataSpaceValue dataArea = new DDataSpaceValue(remoteObject, AbstractWrappedDataSpace.CHARACTER, ccsidEncoding, bytes);
        return dataArea;
    }

    // public static DDataSpaceValue getDecimalInstance(byte[] bytes) throws
    // UnsupportedEncodingException {
    // DDataSpaceValue dataArea = new
    // DDataSpaceValue(AbstractWrappedDataSpace.DECIMAL, bytes);
    // return dataArea;
    // }
    //
    // public static DDataSpaceValue getLogicalInstance(byte[] bytes) throws
    // UnsupportedEncodingException {
    // DDataSpaceValue dataArea = new
    // DDataSpaceValue(AbstractWrappedDataSpace.LOGICAL, bytes);
    // return dataArea;
    // }

    private DDataSpaceValue(RemoteObject remoteObject, String dataType, byte[] bytes) throws UnsupportedEncodingException {
        this(remoteObject, dataType, null, bytes);
    }

    private DDataSpaceValue(RemoteObject remoteObject, String dataType, String ccsidEncoding, byte[] bytes) throws UnsupportedEncodingException {
        if (ccsidEncoding != null) {
            this.converter = new CharConverter(ccsidEncoding);
        } else {
            this.converter = null;
        }
        this.bytes = bytes;

        this.remoteObject = remoteObject;
        this.dataType = dataType;
    }

    public String getConnection() {
        return remoteObject.getConnectionName();
    }

    public String getName() {
        return remoteObject.getName();
    }

    public String getLibrary() {
        return remoteObject.getLibrary();
    }

    public String getObjectType() {
        return remoteObject.getObjectType();
    }

    public String getDescrption() {
        return remoteObject.getDescription();
    }

    public RemoteObject getRemoteObject() {
        return remoteObject;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Boolean getBoolean(int offset, int length) {
        ensureLogicalType();
        String data = convertByteArrayToString(bytes, offset, length);
        return convertStringToBoolean(data);
    }

    public void setBoolean(Boolean value, int offset, int length) throws CharConversionException {
        ensureLogicalType();
        String data = StringHelper.getFixLength(convertBooleanToString(value), length);
        convertStringToByteArray(data, bytes, offset, length);
    }

    public String getString(int offset, int length) {
        ensureCharcaterType();
        return convertByteArrayToString(bytes, offset, length);
    }

    public void setString(String value, int offset, int length) throws CharConversionException {
        ensureCharcaterType();
        String data = StringHelper.getFixLength(value, length);
        convertStringToByteArray(data, bytes, offset, length);
    }

    public byte getTiny(int offset, int length) {
        ensureCharcaterType();
        byte[] intBytes = ByteHelper.copyOfRange(bytes, offset, length);
        return ByteBuffer.wrap(intBytes).get();
    }

    public void setTiny(byte value, int offset, int length) {
        ensureCharcaterType();
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(value);
        replaceBytes(buffer, offset, length);
    }

    public short getShort(int offset, int length) {
        ensureCharcaterType();
        byte[] intBytes = ByteHelper.copyOfRange(bytes, offset, length);
        return ByteBuffer.wrap(intBytes).getShort();
    }

    public void setShort(short value, int offset, int length) {
        ensureCharcaterType();
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.putShort(value);
        replaceBytes(buffer, offset, length);
    }

    public int getInteger(int offset, int length) {
        ensureCharcaterType();
        byte[] intBytes = ByteHelper.copyOfRange(bytes, offset, length);
        return ByteBuffer.wrap(intBytes).getInt();
    }

    public void setInteger(int value, int offset, int length) {
        ensureCharcaterType();
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.putInt(value);
        replaceBytes(buffer, offset, length);
    }

    public long getLongInteger(int offset, int length) {
        ensureCharcaterType();
        byte[] intBytes = ByteHelper.copyOfRange(bytes, offset, length);
        return ByteBuffer.wrap(intBytes).getLong();
    }

    public void setLongInteger(long value, int offset, int length) {
        ensureCharcaterType();
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.putLong(value);
        replaceBytes(buffer, offset, length);
    }

    public BigDecimal getDecimal(int offset, int length, int fraction) {
        ensureDecimalType();
        String data = convertByteArrayToString(bytes, offset, length);
        String digits = data.substring(0, length - fraction);
        String decPos = data.substring(length - fraction);
        return new BigDecimal(digits + "." + decPos);
    }

    public void setDecimal(BigDecimal value, int offset, int length, int fraction) throws CharConversionException, ValueTooLargeException {
        ensureDecimalType();

        String strDigits = StringHelper.getFixLength("", length - fraction, "9");
        String strFraction = StringHelper.getFixLength("", fraction, "9");
        BigDecimal maxValue = new BigDecimal(strDigits + "." + strFraction);
        if (value.compareTo(maxValue) > 0) {
            throw new ValueTooLargeException(value);
        }

        String data = BigDecimalHelper.getFixLength(value, length, fraction);
        convertStringToByteArray(data.replaceAll("\\.", ""), bytes, offset, length);
    }

    private String convertByteArrayToString(byte[] bytes, int offset, int length) {
        if (converter == null) {
            return convertByteArrayToStringRaw(bytes, offset, length);
        } else {
            return converter.byteArrayToString(bytes, offset, length);
        }
    }

    private String convertByteArrayToStringRaw(byte[] bytes, int offset, int length) {
        return new String(ByteHelper.copyOfRange(bytes, offset, length));
    }

    private void convertStringToByteArray(String string, byte[] bytes, int offset, int length) throws CharConversionException {
        if (converter == null) {
            convertStringToByteArrayRaw(string, bytes, offset, length);
        } else {
            converter.stringToByteArray(string, bytes, offset, length);
        }
    }

    private void convertStringToByteArrayRaw(String data, byte[] bytes2, int offset, int length) {
        byte[] dataBytes = data.getBytes();
        for (int i = 0; i < length; i++) {
            bytes2[i + offset] = dataBytes[i];
        }
    }

    private Boolean convertStringToBoolean(String data) {
        if (DE.BOOLEAN_TRUE_1.equals(data)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    private String convertBooleanToString(Boolean boolValue) {
        if (boolValue) {
            return DE.BOOLEAN_TRUE_1;
        } else {
            return DE.BOOLEAN_FALSE_0;
        }
    }

    private void ensureLogicalType() {
        if (!AbstractWrappedDataSpace.LOGICAL.equals(dataType) && !AbstractWrappedDataSpace.CHARACTER.equals(dataType)) {
            throw new IllegalAccessError("getBoolean not allowed for type: " + dataType);
        }
    }

    private void ensureDecimalType() {
        if (!AbstractWrappedDataSpace.DECIMAL.equals(dataType) && !AbstractWrappedDataSpace.CHARACTER.equals(dataType)) {
            throw new IllegalAccessError("getBoolean not allowed for type: " + dataType);
        }
    }

    private void ensureCharcaterType() {
        if (!AbstractWrappedDataSpace.CHARACTER.equals(dataType)) {
            throw new IllegalAccessError("getBoolean not allowed for type: " + dataType);
        }
    }

    private void replaceBytes(ByteBuffer buffer, int offset, int length) {
        byte[] newBytes = new byte[length];
        buffer.position(0);
        buffer.get(newBytes, 0, length);
        for (int i = 0; i < length; i++) {
            bytes[offset + i] = newBytes[i];
        }
    }
}
