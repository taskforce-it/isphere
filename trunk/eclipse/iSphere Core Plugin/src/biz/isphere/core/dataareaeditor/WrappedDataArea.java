/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor;

import java.math.BigDecimal;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.exception.IllegalMethodAccessException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.DataArea;
import com.ibm.as400.access.DecimalDataArea;
import com.ibm.as400.access.LogicalDataArea;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.QSYSObjectPathName;

public class WrappedDataArea {

    public static final String CHARACTER = "*CHAR";
    public static final String DECIMAL = "*DEC";
    public static final String LOGICAL = "*LGL";

    private AS400 as400;
    private String library;
    private String name;
    private String type;
    private String text;
    private DataArea dataArea;

    public WrappedDataArea(AS400 anAS400, String aLibrary, String aName) {
        as400 = anAS400;
        library = aLibrary;
        name = aName;
        type = retrieveDataAreaType(as400, library, name);
        text = retrieveDataAreaText(as400, library, name);
        dataArea = getDataArea();
    }

    public String getName() {
        return name;
    }

    public String getLibrary() {
        return library;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int getTextLimit() {
        if (DECIMAL.equals(type) && getDecimalPositions() > 0) {
            return getLength() + 1;
        }
        return getLength();
    }

    public int getLength() {
        try {
            return getDataArea().getLength();
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve data area value.", e);
        }
        return -1;
    }

    public int getDigits() {
        return getLength() - getDecimalPositions();
    }

    public int getDecimalPositions() {
        if (!(getDataArea() instanceof DecimalDataArea)) {
            throw produceIllegalMethodAccessException("getStringValue()");
        }

        try {
            return ((DecimalDataArea)getDataArea()).getDecimalPositions();
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve data area value.", e);
        }
        return -1;
    }

    public String getStringValue() {
        if (!(getDataArea() instanceof CharacterDataArea)) {
            throw produceIllegalMethodAccessException("getStringValue()");
        }

        try {
            String value = ((CharacterDataArea)getDataArea()).read();
            if (value.length() > getLength()) {
                return value.substring(0, getLength());
            }
            return value;
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve data area value.", e);
        }
        return null;
    }

    public Throwable setValue(String aValue) {
        if (!(getDataArea() instanceof CharacterDataArea)) {
            throw produceIllegalMethodAccessException("setCharacterValue()");
        }

        try {
            ((CharacterDataArea)getDataArea()).write(aValue);
            return null;
        } catch (Exception e) {
            return handleSaveError("Failed to save character data area value.", e);
        }
    }

    public BigDecimal getDecimalValue() {
        if (!(getDataArea() instanceof DecimalDataArea)) {
            throw produceIllegalMethodAccessException("getDecimalValue()");
        }

        try {
            return ((DecimalDataArea)getDataArea()).read();
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve decimal data area value.", e);
        }

        return null;
    }

    public Throwable setValue(BigDecimal aValue) {
        if (!(getDataArea() instanceof DecimalDataArea)) {
            throw produceIllegalMethodAccessException("setDecimalValue()");
        }

        try {
            ((DecimalDataArea)getDataArea()).write(aValue);
            return null;
        } catch (Exception e) {
            return handleSaveError("Failed to save decimal data area value.", e);
        }
    }

    public Boolean getBooleanValue() {
        if (!(getDataArea() instanceof LogicalDataArea)) {
            throw produceIllegalMethodAccessException("getBooleanValue()");
        }

        try {
            return ((LogicalDataArea)getDataArea()).read();
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve boolean data area value.", e);
        }
        return null;
    }

    public Throwable setValue(Boolean aValue) {
        if (!(getDataArea() instanceof LogicalDataArea)) {
            throw produceIllegalMethodAccessException("setBooleanValue()");
        }

        try {
            ((LogicalDataArea)getDataArea()).write(aValue);
            return null;
        } catch (Exception e) {
            return handleSaveError("Failed to save boolean data area value.", e);
        }
    }

    private Throwable handleSaveError(String aMessage, Exception anException) {
        ISpherePlugin.logError(aMessage, anException);
        return anException;
    }

    @Override
    public String toString() {
        StringBuilder value = new StringBuilder();
        value.append(getLibrary());
        value.append("/");
        value.append(getName());
        value.append("(");
        value.append(getType());
        value.append(")");
        return value.toString();
    }

    private DataArea getDataArea() {
        if (dataArea == null) {
            QSYSObjectPathName path = getObjectPathName();
            String type = getType();
            if (CHARACTER.equals(type)) {
                dataArea = new CharacterDataArea(as400, path.getPath());
            } else if (DECIMAL.equals(type)) {
                dataArea = new DecimalDataArea(as400, path.getPath());
            } else if (LOGICAL.equals(type)) {
                dataArea = new LogicalDataArea(as400, path.getPath());
            } else {
                // FIXME: add error handling
            }
        }
        return dataArea;
    }

    private QSYSObjectPathName getObjectPathName() {
        return new QSYSObjectPathName(library, name, "DTAARA");
    }

    private String retrieveDataAreaType(AS400 anAS400, String aLibrary, String aDataArea) {
        QWCRDTAA qwcrdtaa = new QWCRDTAA();
        return qwcrdtaa.getType(anAS400, aLibrary, aDataArea);
    }

    private String retrieveDataAreaText(AS400 anAS400, String aLibrary, String aDataArea) {
        ObjectDescription objectDescription = new ObjectDescription(as400, aLibrary, aDataArea, "DTAARA");
        String text;
        try {
            text = (String)objectDescription.getValue(ObjectDescription.TEXT_DESCRIPTION);
        } catch (Exception e) {
            text = "";
        }
        return text;
    }

    private IllegalMethodAccessException produceIllegalMethodAccessException(String aMethodName) {
        return new IllegalMethodAccessException("Method " + aMethodName + " is not applicable for a data area of type: " + getType());
    }

}
