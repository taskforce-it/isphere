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
        TIME,
        TIMESTMP,
        DATE,
        CHAR,
        VARCHAR,
        CLOB,
        REAL,
        DOUBLE,
        SMALLINT,
        INTEGER,
        BIGINT,
        DECIMAL,
        NUMERIC
    };

    private String name;

    private String columnText;

    private int size;

    private int precision;

    private DataType dataType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setDataType(String dataType) throws Exception {
        try {
            this.dataType = DataType.valueOf(dataType.toUpperCase());
        } catch (Exception exception) {
            throw new Exception("Unsupported datatype: " + dataType);
        }
    }

    public String getColumnText() {
        return columnText;
    }

    public void setColumnText(String columnText) {
        this.columnText = columnText.trim();
    }
}
