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

import biz.isphere.journalexplorer.core.api.retrievefielddescription.RVFD0100;

public class MetaColumn {

    public enum DataType {
        INTEGER,
        SMALLINT,
        BIGINT,
        NUMERIC,
        DECIMAL,
        FLOAT, // Returned as REAL or DOUBLE from IQDBRTVFD API
        REAL,
        DOUBLE,
        DECREAL,
        DECDOUBLE,
        CHAR,
        VARCHAR,
        BINARY,
        VARBINARY,
        DATE,
        TIME,
        TIMESTAMP,
        GRAPHIC,
        VARGRAPHIC,
        LOB,
        CLOB, // returned as LOB with CCSID from IQDBRTVFD API.
        BLOB, // returned as LOB with CCSID = 65535 from IQDBRTVFD API.
        DATALINK,
        UNKNOWN,
        // Not yet supported
        LONG_VARCHAR,
        LONG_VARGRAPHIC
    };

    private int index;
    private String name;
    private DataType type;
    private int length;
    private int decimalPositions;
    private int inputBufferOffset;
    private int outputBufferOffset;
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

    public MetaColumn(int index, RVFD0100 fieldDescription) throws Exception {

        this.index = index;

        setFieldName(fieldDescription.getFieldName());
        setType(fieldDescription.getSqlDataType());
        setLength(fieldDescription.getFieldLength());
        setDecimalPositions(fieldDescription.getDecimalPositions());
        setInputBufferOffset(fieldDescription.getInputBufferOffset());
        setOutputBufferOffset(fieldDescription.getOutputBufferOffset());
        setBufferLength(fieldDescription.getBufferLength());
        setCcsid(fieldDescription.getCcsid());
        setVaryingLength(fieldDescription.isVaryingLength());
        setBinary(fieldDescription.isBinary());
        setNullable(fieldDescription.isNullable());
        setDateTimeFormat(removeLeadingAsterisk(fieldDescription.getDateTimeFormat()));
        setDateTimeSeparator(fieldDescription.getDateTimeSeparator());
        setText(fieldDescription.getText());

        setNumeric(fieldDescription.isNumericFieldType());
        setDateTime(fieldDescription.isDateTimeFieldType());
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    private void setFieldName(String name) {
        this.name = name.trim();
    }

    public DataType getType() {
        return type;
    }

    private void setType(DataType dataType) {
        this.type = dataType;
    }

    public int getLength() {
        return length;
    }

    private void setLength(int length) {
        this.length = length;
    }

    public int getDecimalPositions() {
        return decimalPositions;
    }

    private void setDecimalPositions(int decPos) {
        this.decimalPositions = decPos;
    }

    public int getCcsid() {
        return ccsid;
    }

    private void setCcsid(int ccsid) {
        this.ccsid = ccsid;
    }

    public boolean isVaryingLength() {
        return isVaryingLength;
    }

    private void setVaryingLength(boolean varyingLength) {
        this.isVaryingLength = varyingLength;
    }

    public boolean isBinary() {
        return isBinary;
    }

    private void setBinary(boolean binary) {
        this.isBinary = binary;
    }

    public boolean isNullable() {
        return isNullable;
    }

    private void setNullable(boolean nullable) {
        this.isNullable = nullable;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    private void setDateTimeFormat(String format) {
        this.dateTimeFormat = format.trim();
    }

    public String getDateTimeSeparator() {
        return dateTimeSeparator;
    }

    private void setDateTimeSeparator(String separator) {
        this.dateTimeSeparator = separator.trim();
    }

    public String getText() {
        return text;
    }

    private void setText(String columnText) {
        this.text = columnText.trim();
    }

    public boolean isNumeric() {
        return isNumeric;
    }

    private void setNumeric(boolean binary) {
        this.isNumeric = binary;
    }

    public boolean isDateTime() {
        return isDateTime;
    }

    private void setDateTime(boolean dateTime) {
        this.isDateTime = dateTime;
    }

    public int getInputBufferOffset() {
        return inputBufferOffset;
    }

    private void setInputBufferOffset(int offset) {
        this.inputBufferOffset = offset;
    }

    public int getOutputBufferOffset() {
        return outputBufferOffset;
    }

    private void setOutputBufferOffset(int offset) {
        this.outputBufferOffset = offset;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    private void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    private String removeLeadingAsterisk(String format) {

        if (format.startsWith("*")) { //$NON-NLS-1$
            return format.substring(1);
        }

        return format;
    }

    public String getFormattedType() {

        StringBuilder buffer = new StringBuilder();

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
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        try {

            buffer.append(getName());
            buffer.append(": "); //$NON-NLS-1$
            buffer.append(getFormattedType());

            return buffer.toString();

        } catch (Exception e) {
            return super.toString();
        }
    }
}
