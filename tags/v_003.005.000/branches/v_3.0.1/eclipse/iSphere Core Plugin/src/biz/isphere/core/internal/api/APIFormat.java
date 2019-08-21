/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.preferences.Preferences;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharConverter;
import com.ibm.as400.access.DateTimeConverter;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;

/**
 * Defines the structure of a, so called, "format" used for an API call on the
 * IBM i.
 * 
 * @author Thomas Raddatz
 */
public class APIFormat {

    private AS400 system;
    private String name;
    private int length;
    private Map<String, AbstractAPIFieldDescription> fields = new LinkedHashMap<String, AbstractAPIFieldDescription>();
    private byte[] bytes;
    private int offset;

    private CharConverter charConv;
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

        this.system = system;
        this.name = name;
        this.length = -1;
        this.fields = null;

        this.int4Conv = null;
        this.charConv = null;
        this.dateTimeConv = null;

        this.offset = 0;
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
     * Returns the 8-character format name.
     * 
     * @return name of the format
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the length of the format.
     * 
     * @return format length
     */
    public int getLength() {
        return length;
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
        this.offset = offset;
    }

    /**
     * Returns the offset of this format.
     * 
     * @return offset this format starts in the byte buffer
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the value of a given 4-byte integer field.
     * 
     * @param name - field name
     * @return integer value
     */
    protected int getInt4Value(String name) {

        APIInt4FieldDescription field = getInt4FieldDescription(name);
        return getInt4Converter().toInt(bytes, getAbsoluteFieldOffset(field));
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
        APIInt4FieldDescription field = getInt4FieldDescription(fieldName);

        for (int i = 0; i < size; i++) {
            intArray[i] = getInt4Converter().toInt(bytes, field.getOffset() + i * 4);
        }

        return intArray;
    }

    /**
     * Sets the value of a given 4-byte integer field.
     * 
     * @param name - field name
     * @param value - integer value
     */
    protected void setInt4Value(String name, int value) {

        APIInt4FieldDescription field = getInt4FieldDescription(name);
        getInt4Converter().toBytes(value, getBytes(), getAbsoluteFieldOffset(field));
    }

    /**
     * Returns the value of a given character field.
     * 
     * @param name - field name
     * @return string value
     */
    protected String getCharValue(String name) throws UnsupportedEncodingException {

        APICharFieldDescription field = getCharFieldDescription(name);
        return getCharConverter().byteArrayToString(bytes, getAbsoluteFieldOffset(field), field.getLength());
    }

    /**
     * Sets the value of a given character field.
     * 
     * @param name - field name
     * @param value - string value
     */
    protected void setCharValue(String name, String value) throws CharConversionException, UnsupportedEncodingException {

        APICharFieldDescription field = getCharFieldDescription(name);
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
    protected Date getDateTimeValue(String name) throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException, IOException,
        ObjectDoesNotExistException {

        APIDateTimeFieldDescription field = getDateTimeFieldDescription(name);
        byte[] subBytes = retrieveBytesFromBuffer(getAbsoluteFieldOffset(field), field.getLength());

        if (isDateTimeSet(subBytes)) {
            return getDateTimeConverter().convert(subBytes, field.getFormat());
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
    protected void setDateTime(String name, Date value) throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException,
        IOException, ObjectDoesNotExistException {

        APIDateTimeFieldDescription field = getDateTimeFieldDescription(name);
        byte[] dateBytes = getDateTimeConverter().convert(value, field.getFormat());

        int offset = getAbsoluteFieldOffset(field);
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
    protected void setByteValue(String name, byte[] value) throws CharConversionException, UnsupportedEncodingException {

        APICharFieldDescription field = getCharFieldDescription(name);
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
                text.replace(i, i + 1, getReplacementCharacter()); //$NON-NLS-1$
            }
        }

        return text.toString();
    }

    protected APICharFieldDescription addCharField(String name, int offset, int length) {
        return (APICharFieldDescription)addField(new APICharFieldDescription(name, offset, length));
    }

    protected APIInt4FieldDescription addInt4Field(String name, int offset) {
        return (APIInt4FieldDescription)addField(new APIInt4FieldDescription(name, offset));
    }

    protected APIDateTimeFieldDescription addDateTimeField(String name, int offset, int length, String format) {
        return (APIDateTimeFieldDescription)addField(new APIDateTimeFieldDescription(name, offset, length, format));
    }

    protected AbstractAPIFieldDescription getField(String name) {
        return fields.get(name);
    }

    protected boolean isOverflow(String fieldName, int maxLength) {

        AbstractAPIFieldDescription field = getField(fieldName);
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
        ByteBuffer.wrap(bytes, offset + this.offset, length).get(subBytes, 0, length).array();

        return subBytes;
    }

    private byte[] retrieveBytesFromBuffer(int offset, int length) {

        byte[] subBytes = new byte[length];
        ByteBuffer.wrap(bytes, offset, length).get(subBytes, 0, length).array();

        return subBytes;
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
        return offset + field.getOffset();
    }

    private AbstractAPIFieldDescription addField(AbstractAPIFieldDescription field) {

        if (fields == null) {
            fields = new LinkedHashMap<String, AbstractAPIFieldDescription>();
            length = 0;
        }

        if (fields.containsKey(name)) {
            throw new RuntimeException("A field with name '" + name + " already exists."); //$NON-NLS-1$ //$NON-NLS-2$
        }

        fields.put(field.getName(), field);
        length = length + field.getLength();
        return field;
    }

    private APIInt4FieldDescription getInt4FieldDescription(String name) {

        AbstractAPIFieldDescription field = getField(name);
        if (field instanceof APIInt4FieldDescription) {
            return (APIInt4FieldDescription)field;
        }

        throw new APIFieldTypeMismatchException(name);
    }

    private APICharFieldDescription getCharFieldDescription(String name) {

        AbstractAPIFieldDescription field = getField(name);
        if (field instanceof APICharFieldDescription) {
            return (APICharFieldDescription)field;
        }

        throw new APIFieldTypeMismatchException(name);
    }

    private APIDateTimeFieldDescription getDateTimeFieldDescription(String name) {

        AbstractAPIFieldDescription field = getField(name);
        if (field instanceof APIDateTimeFieldDescription) {
            return (APIDateTimeFieldDescription)field;
        }

        throw new APIFieldTypeMismatchException(name);
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
