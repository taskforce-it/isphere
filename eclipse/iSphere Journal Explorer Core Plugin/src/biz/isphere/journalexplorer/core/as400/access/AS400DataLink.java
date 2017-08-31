/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.as400.access;

import java.math.BigDecimal;

import com.ibm.as400.access.AS400ByteArray;
import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400ZonedDecimal;
import com.ibm.as400.access.BinaryConverter;

public class AS400DataLink extends AS400ByteArray {

    private static final long serialVersionUID = -6535182668478349365L;

    private static final int OFFSET_TOTAL_LENGTH = 0;
    @SuppressWarnings("unused")
    private static final int OFFSET_WHAT_EVER_1 = 2;
    @SuppressWarnings("unused")
    private static final int OFFSET_LINK_SCHEME = 7;
    private static final int OFFSET_LINK_LENGTH = 11;
    @SuppressWarnings("unused")
    private static final int OFFSET_WHAT_EVER_2 = 16;
    private static final int OFFSET_LINK_VALUE = 24;

    private int length;
    private int ccsid;

    @SuppressWarnings("unused")
    private AS400Text linkTypeConverter;
    private AS400Text linkValueConverter;

    public AS400DataLink(int length, int ccsid) {
        super(length + 24);

        this.length = length;
        this.ccsid = ccsid;

        this.linkTypeConverter = new AS400Text(4, this.ccsid);
        this.linkValueConverter = new AS400Text(length, this.ccsid);
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
        return ""; //$NON-NLS-1$
    }

    @Override
    public int getInstanceType() {
        return AS400DataType.TYPE_BYTE_ARRAY;
    }

    public Class<?> getJavaType() {
        return String.class;
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

        // TODO: implement method body

        /*
         * Actually it is not really necessary to implement the method, because
         * we only read data from a journal entry. We do not write data
         * anywhere.
         */

        // Set total length to bytes 1 - 2

        // Set '00001' as zoned value to bytes 3 - 7

        // Set 'URL ' as character value to bytes 8 - 11

        // Set length of URL as zoned value to bytes 12 - 16

        // Set x'00000000' to bytes 17 - 24

        // Set the URL value to bytes 24 - n

        return getByteLength();
    }

    @Override
    public Object toObject(byte[] serverValue) {
        return toObject(serverValue, 0);
    }

    @Override
    public Object toObject(byte[] serverValue, int offset) {

        /**
         * <pre>
         * https://www.ixquick.com/deu = 27 Zeichen
         * 
         * 00001URL 00027????????HTTPS://WWW.IXQUICK.COM/deu = 49 characters
         * 
         * ? ? 0 0 0 0 1 U R L   0 0 0 2 7 ? ? ? ? ? ? ? ? H T T P S : / / W W W .
         * 0031F0F0F0F0F1E4D9D340F0F0F0F2F70000000000000000C8E3E3D7E27A6161E6E6E64B
         * ----          --------          ----------------
         *  49
         * 
         * https://www.google.de/en    = 24 Zeichen
         * 
         * 00001URL 00024????????HTTPS://WWW.GOOGLE.DE/en    = 46 characters
         * 
         * ? ? 0 0 0 0 1 U R L   0 0 0 2 4 ? ? ? ? ? ? ? ? H T T P S : / / W W W .
         * 002EF0F0F0F0F1E4D9D340F0F0F0F2F40000000000000000C8E3E3D7E27A6161E6E6E64B
         * ----          --------          ----------------
         *  46
         * </pre>
         */

        byte[] bytes;

        int totalLength = BinaryConverter.byteArrayToUnsignedShort(serverValue, offset + OFFSET_TOTAL_LENGTH);
        if (totalLength > getByteLength()) {
            throw new IllegalArgumentException("totalLength (" + totalLength + ")"); //$NON-NLS-1$ 
        }
        if (offset + getByteLength() > serverValue.length) {
            throw new IllegalArgumentException("serverValue too short"); //$NON-NLS-1$
        }

        AS400ZonedDecimal linkLengthConverter = new AS400ZonedDecimal(5, 0);
        BigDecimal linkLength = (BigDecimal)linkLengthConverter.toObject(serverValue, offset + OFFSET_LINK_LENGTH);

        bytes = new byte[length];
        System.arraycopy(serverValue, offset + OFFSET_LINK_VALUE, bytes, 0, linkLength.intValue());
        String link = ((String)linkValueConverter.toObject(bytes)).substring(0, linkLength.intValue());

        return link;
    }
}
