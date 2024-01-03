/*******************************************************************************
 * Copyright (c) 2012-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Bin2;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharConverter;
import com.ibm.as400.access.DateTimeConverter;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.preferences.Preferences;

/**
 * Defines the structure of a, so called, "format" used for an API call on the
 * IBM i.
 * 
 * @author Thomas Raddatz
 */
public class APIFormat extends AbstractAPIFieldDescription {

    private AS400 system;
    private Map<String, AbstractAPIFieldDescription> fields = new LinkedHashMap<String, AbstractAPIFieldDescription>();
    private byte[] bytes;

    private CharConverter charConv;
    private AS400Bin2 int2Conv;
    private AS400Bin4 int4Conv;
    private DateTimeConverter dateTimeConv;

    /**
     * Constructs a APIFormat object.
     * 
     * @param system - System that is used to create the converters
     * @param name - Name of the format as it is passed to the API
     * @throws UnsupportedEncodingException
     */
    public APIFormat(AS400 system, String name) {
        super(name, 0, -1);

        this.system = system;
        this.fields = null;

        this.int4Conv = null;
        this.charConv = null;
        this.dateTimeConv = null;
    }

    /**
     * Returns the system the format was created for.
     * 
     * @return system
     */
    public AS400 getSystem() {
        return system;
    }

    /**
     * Sets the byte value of the format.
     * 
     * @param bytes - array of bytes with the byte value of the format
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Returns the value of the format as an array of bytes.
     * 
     * @return byte array
     */
    public byte[] getBytes() {

        if (bytes == null) {
            bytes = new byte[getLength()];
        }

        return bytes;
    }

    /**
     * Sets the offset of the format.
     * 
     * @param offset - offset this format starts in the byte buffer
     */
    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    /**
     * Returns the offset of this format.
     * 
     * @return offset this format starts in the byte buffer
     */
    // public int getOffset() {
    // if (parent != null) {
    // super.setOffset(parent.getIntValue(offsetFieldName));
    // }
    // return super.getOffset();
    // }

    // public void setParent(APIFormat parent, String fieldName) {
    // this.parent = parent;
    // this.offsetFieldName = fieldName;
    // }

    // public boolean isBasedOnField() {
    // if (parent == null) {
    // return false;
    // } else {
    // return true;
    // }
    // }

    /**
     * Returns the value of a given bit field.
     * 
     * @param name - field name
     * @return array of bytes
     */
    protected boolean getBitValue(String name, String bitName) {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        checkFieldDescription(field, APIBitFieldDescription.class);
        checkDataAvailable(field);

        int offset = getAbsoluteFieldOffset(field);
        int length = field.getLength();
        byte[] tBytes = Arrays.copyOfRange(bytes, offset, offset + length);

        APIBitFieldDescription bitField = (APIBitFieldDescription)field;
        boolean isSet = bitField.getBit(bitName, tBytes);

        return isSet;
    }

    /**
     * Returns the value of a given integer field.
     * 
     * @param name - field name
     * @return integer value
     */
    protected int getIntValue(String name) {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        if (field == null) {
            return -1;
        }

        switch (field.getLength()) {
        case 2:
            checkFieldDescription(field, APIInt2FieldDescription.class);
            checkDataAvailable(field);
            return getInt2Converter().toShort(bytes, getAbsoluteFieldOffset(field));
        case 4:
            checkFieldDescription(field, APIInt4FieldDescription.class);
            checkDataAvailable(field);
            return getInt4Converter().toInt(bytes, getAbsoluteFieldOffset(field));
        default:
            throw new APIFieldTypeMismatchException(name);
        }
    }

    /**
     * Returns an array of a 4-byte integer fields.
     * 
     * @param fieldName - field name
     * @param size - number of array items
     * @return array of integer values
     */
    protected int[] getInt4Array(String fieldName, int size) {

        int[] intArray = new int[size];
        AbstractAPIFieldDescription field = getFieldDescription(fieldName);
        checkFieldDescription(field, APIInt4FieldDescription.class);
        checkDataAvailable(field);

        for (int i = 0; i < size; i++) {
            intArray[i] = getInt4Converter().toInt(bytes, field.getOffset() + i * 4);
        }

        return intArray;
    }

    /**
     * Sets the value of a given integer field.
     * 
     * @param name - field name
     * @param value - integer value
     */
    protected void setIntValue(String name, int value) {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        switch (field.getLength()) {
        case 2:
            checkFieldDescription(field, APIInt2FieldDescription.class);
            getInt2Converter().toBytes(value, getBytes(), getAbsoluteFieldOffset(field));
            return;
        case 4:
            checkFieldDescription(field, APIInt4FieldDescription.class);
            getInt4Converter().toBytes(value, getBytes(), getAbsoluteFieldOffset(field));
            return;
        default:
            throw new APIFieldTypeMismatchException(name);
        }
    }

    protected APIFormat getFormatValue(String name) {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        checkFieldDescription(field, APIFormat.class);

        APIFormat format = (APIFormat)field;
        if (format.isBasedOnField()) {
            if (format.getOffset() == 0) {
                throw new APIFieldDataNotAvailableException(name);
            }
        }

        checkDataAvailable(field);

        format.setBytes(bytes);

        return format;
    }

    /**
     * Returns the value of a given character field.
     * 
     * @param name - field name
     * @return string value
     */
    protected String getCharValue(String name) throws UnsupportedEncodingException {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        checkFieldDescription(field, APICharFieldDescription.class);
        checkDataAvailable(field);

        return getCharConverter().byteArrayToString(bytes, getAbsoluteFieldOffset(field), field.getLength());
    }

    /**
     * Sets the value of a given character field.
     * 
     * @param name - field name
     * @param value - string value
     */
    protected void setCharValue(String name, String value) throws CharConversionException, UnsupportedEncodingException {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        checkFieldDescription(field, APICharFieldDescription.class);

        getCharConverter().stringToByteArray(StringHelper.getFixLength(value, field.getLength()), getBytes(), getAbsoluteFieldOffset(field),
            field.getLength());
    }

    /**
     * Returns the value of a given date and time field.
     * 
     * @param name - field name
     * @return date and time value
     * @throws AS400SecurityException
     * @throws ErrorCompletingRequestException
     * @throws InterruptedException
     * @throws IOException
     * @throws ObjectDoesNotExistException
     */
    protected Date getDateTimeValue(String name)
        throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException, ObjectDoesNotExistException {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        checkFieldDescription(field, APIDateTimeFieldDescription.class);
        checkDataAvailable(field);

        byte[] subBytes = retrieveBytesFromBuffer(getAbsoluteFieldOffset(field), field.getLength());

        if (isDateTimeSet(subBytes)) {
            APIDateTimeFieldDescription dateTimeField = (APIDateTimeFieldDescription)field;
            return getDateTimeConverter().convert(subBytes, dateTimeField.getFormat());
        }

        return null;
    }

    /**
     * Sets the value of a given date and time field.
     * 
     * @param name - field name
     * @param value - date and time value
     * @throws AS400SecurityException
     * @throws ErrorCompletingRequestException
     * @throws InterruptedException
     * @throws IOException
     * @throws ObjectDoesNotExistException
     */
    protected void setDateTime(String name, Date value)
        throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException, ObjectDoesNotExistException {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        checkFieldDescription(field, APIDateTimeFieldDescription.class);

        APIDateTimeFieldDescription dateTimeField = (APIDateTimeFieldDescription)field;
        byte[] dateBytes = getDateTimeConverter().convert(value, dateTimeField.getFormat());

        int offset = getAbsoluteFieldOffset(dateTimeField);
        byte[] bytes = getBytes();

        for (byte b : dateBytes) {
            bytes[offset] = b;
            offset++;
        }
    }

    /**
     * Sets the value of a given byte field.
     * 
     * @param name - field name
     * @param value - byte value
     */
    protected void setByteValue(String name, byte[] value) {

        AbstractAPIFieldDescription field = getFieldDescription(name);
        checkFieldDescription(field, APICharFieldDescription.class);

        int offset = getAbsoluteFieldOffset(field);
        int length = field.getLength();

        if (value.length > length) {
            throw new IllegalArgumentException("Invalid length of byte array parameter 'value'");
        }

        for (byte b : value) {
            bytes[offset] = b;
            offset++;
            length--;
        }

        while (length > 0) {
            bytes[offset] = 0x00;
            offset++;
            length--;
        }
    }

    /**
     * Returns the text starting at a given offset and length.
     * 
     * @param offset - offset to start from
     * @param length - number of characters to return
     * @return text value
     * @throws UnsupportedEncodingException
     */
    protected String convertToText(byte[] bytes) throws UnsupportedEncodingException {
        return convertToText(bytes, false);
    }

    /**
     * Returns the text starting at a given offset and length.
     * 
     * @param offset - offset to start from
     * @param length - number of characters to return
     * @return text value
     * @throws UnsupportedEncodingException
     */
    protected String convertToText(byte[] bytes, boolean replaceControlCharacters) throws UnsupportedEncodingException {

        if (!replaceControlCharacters) {
            return getCharConverter().byteArrayToString(bytes);
        }

        StringBuilder text = new StringBuilder(getCharConverter().byteArrayToString(bytes));
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
                text.replace(i, i + 1, getReplacementCharacter()); // $NON-NLS-1$
            }
        }

        return text.toString();
    }

    protected APIFormat addFormat(int offset, APIFormat format) {
        format.setOffset(offset);
        return (APIFormat)addField(format);
    }

    protected APIFormat addFormat(APIFormat parent, String offsetFieldName, APIFormat format) {
        format.setParent(parent, offsetFieldName);
        return (APIFormat)addField(format);
    }

    protected APICharFieldDescription addCharField(String name, int offset, int length) {
        return (APICharFieldDescription)addField(new APICharFieldDescription(name, offset, length));
    }

    protected APIBitFieldDescription addBitField(String name, int offset, int numBits) {
        return (APIBitFieldDescription)addField(new APIBitFieldDescription(name, offset, numBits));
    }

    protected APIInt2FieldDescription addInt2Field(String name, int offset) {
        return (APIInt2FieldDescription)addField(new APIInt2FieldDescription(name, offset));
    }

    protected APIInt4FieldDescription addInt4Field(String name, int offset) {
        return (APIInt4FieldDescription)addField(new APIInt4FieldDescription(name, offset));
    }

    protected APIDateTimeFieldDescription addDateTimeField(String name, int offset, int length, String format) {
        return (APIDateTimeFieldDescription)addField(new APIDateTimeFieldDescription(name, offset, length, format));
    }

    protected AbstractAPIFieldDescription getFieldDescription(String name) {
        return fields.get(name);
    }

    protected boolean isOverflow(String fieldName, int maxLength) {

        AbstractAPIFieldDescription field = getFieldDescription(fieldName);
        if (field.getOffset() + field.getLength() > maxLength) {
            return true;
        }

        return false;
    }

    /**
     * Returns the bytes starting at a given offset and length relative to where
     * this format starts in the byte buffer.
     * 
     * @param offset - offset to start from
     * @param length - number of bytes to return
     * @return byte array
     */
    protected byte[] getBytesAt(int offset, int length) {

        byte[] subBytes = new byte[length];
        ByteBuffer.wrap(bytes, offset + getOffset(), length).get(subBytes, 0, length).array();

        return subBytes;
    }

    private void checkFieldDescription(AbstractAPIFieldDescription field, Class<? extends AbstractAPIFieldDescription> clazz) {
        if (clazz.isAssignableFrom(field.getClass())) {
        } else {
            throw new APIFieldTypeMismatchException(field.getName());
        }
    }

    private void checkDataAvailable(AbstractAPIFieldDescription field) {
        if (bytes.length >= getAbsoluteFieldOffset(field) + field.getLength()) {
            return;
        } else {
            throw new APIFieldDataNotAvailableException(field.getName());
        }
    }

    private byte[] retrieveBytesFromBuffer(int offset, int length) {

        byte[] subBytes = new byte[length];
        ByteBuffer.wrap(bytes, offset, length).get(subBytes, 0, length).array();

        return subBytes;
    }

    private AS400Bin2 getInt2Converter() {
        if (int2Conv == null) {
            int2Conv = new AS400Bin2();
        }
        return int2Conv;
    }

    private AS400Bin4 getInt4Converter() {
        if (int4Conv == null) {
            int4Conv = new AS400Bin4();
        }
        return int4Conv;
    }

    private CharConverter getCharConverter() throws UnsupportedEncodingException {
        if (charConv == null) {
            charConv = new CharConverter(system.getCcsid(), system);
        }
        return charConv;
    }

    private DateTimeConverter getDateTimeConverter() {
        if (dateTimeConv == null) {
            dateTimeConv = new DateTimeConverter(system);
        }
        return dateTimeConv;
    }

    private int getAbsoluteFieldOffset(AbstractAPIFieldDescription field) {
        return getOffset() + field.getOffset();
    }

    private AbstractAPIFieldDescription addField(AbstractAPIFieldDescription field) {

        if (fields == null) {
            fields = new LinkedHashMap<String, AbstractAPIFieldDescription>();
            setLength(0);
        }

        if (fields.containsKey(getName())) {
            throw new RuntimeException("A field with name '" + getName() + " already exists."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        fields.put(field.getName(), field);

        // Ignore fields that have a based-on field.
        if (!(field instanceof APIFormat && ((APIFormat)field).isBasedOnField())) {
            if (field.getOffset() + field.getLength() > getLength()) {
                setLength(field.getOffset() + field.getLength());
            }
        }

        return field;
    }

    private boolean isDateTimeSet(byte[] subBytes) {
        int sum = 0;
        for (int e : subBytes) {
            sum += e;
        }

        if (sum == 0) {
            return false;
        }

        return true;
    }

    private String getReplacementCharacter() {
        return Preferences.getInstance().getDataQueueReplacementCharacter();
    }
}
