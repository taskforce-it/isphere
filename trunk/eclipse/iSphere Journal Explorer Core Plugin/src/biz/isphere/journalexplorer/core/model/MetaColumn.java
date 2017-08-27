/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model;

public class MetaColumn {

    public enum DataType {
        INTEGER,
        SMALLINT,
        BIGINT,
        NUMERIC,
        DECIMAL,
        FLOAT, // Returns as REAL or DOUBLE from IQDBRTVFD API
        REAL,
        DOUBLE,
        CHAR,
        VARCHAR,
        BINARY,
        VARBINARY,
        DATE,
        TIME,
        TIMESTMP,
        GRAPHIC,
        VARGRAPHIC,
        LOB,
        CLOB, // returned as LOB with CCSID from IQDBRTVFD API.
        BLOB, // returned as LOB with CCSID = 65535 from IQDBRTVFD API.
        UNKNOWN,
        // Not yet supported
        DECFLOAT,
        LONG_VARCHAR,
        LONG_VARGRAPHIC
    };

    private int index;
    private String name;
    private DataType type;
    private int length;
    private int decimalPositions;
    private int bufferLength;
    private int ccsid;
    private boolean isVaryingLength;
    private boolean isBinary;
    private boolean isNullable;
    private String dateTimeFormat;
    private String dateTimeSeparator;
    private String text;
    private boolean isNumeric;
    private boolean isDateTime;

    public MetaColumn(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
    
    public String getName() {
        return name;
    }

    public void setFieldName(String name) {
        this.name = name.trim();
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType dataType) {
        this.type = dataType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDecimalPositions() {
        return decimalPositions;
    }

    public void setDecimalPositions(int decPos) {
        this.decimalPositions = decPos;
    }

    public int getCcsid() {
        return ccsid;
    }

    public void setCcsid(int ccsid) {
        this.ccsid = ccsid;
    }

    public boolean isVaryingLength() {
        return isVaryingLength;
    }

    public void setVaryingLength(boolean varyingLength) {
        this.isVaryingLength = varyingLength;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public void setBinary(boolean binary) {
        this.isBinary = binary;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public void setNullable(boolean nullable) {
        this.isNullable = nullable;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String format) {
        this.dateTimeFormat = format.trim();
    }

    public String getDateTimeSeparator() {
        return dateTimeSeparator;
    }

    public void setDateTimeSeparator(String separator) {
        this.dateTimeSeparator = separator.trim();
    }

    public String getText() {
        return text;
    }

    public void setText(String columnText) {
        this.text = columnText.trim();
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    public void setNumeric(boolean binary) {
        this.isNumeric = binary;
    }

    public boolean isDateTime() {
        return isDateTime;
    }

    public void setDateTime(boolean dateTime) {
        this.isDateTime = dateTime;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        try {

            buffer.append(getName());
            buffer.append(": "); //$NON-NLS-1$
            buffer.append(getType());
            buffer.append("("); //$NON-NLS-1$
            buffer.append(getLength());
            if (isNumeric()) {
                buffer.append(", "); //$NON-NLS-1$
                buffer.append(getDecimalPositions());
            } else if (isDateTime()) {
                buffer.append(", "); //$NON-NLS-1$
                buffer.append(getDateTimeFormat());
                buffer.append(", '"); //$NON-NLS-1$
                buffer.append(getDateTimeSeparator());
                buffer.append("'"); //$NON-NLS-1$
            }
            buffer.append(")"); //$NON-NLS-1$

            return buffer.toString();

        } catch (Exception e) {
            return super.toString();
        }
    }
}
