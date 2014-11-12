/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspace.rse;

import java.math.BigDecimal;

import biz.isphere.base.internal.QsysObjectHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.dataareaeditor.QWCRDTAA;
import biz.isphere.core.dataspaceeditor.rse.RemoteObject;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.exception.IllegalMethodAccessException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.DataArea;
import com.ibm.as400.access.DecimalDataArea;
import com.ibm.as400.access.LogicalDataArea;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.UserSpace;

public abstract class AbstractWrappedDataSpace {

    public static final String CHARACTER = "*CHAR"; //$NON-NLS-1$
    public static final String DECIMAL = "*DEC"; //$NON-NLS-1$
    public static final String LOGICAL = "*LGL"; //$NON-NLS-1$

    private AS400 as400;
    private RemoteObject remoteObject;
    private String dataType;
    private String text;
    private Object dataSpaceObject;

    public AbstractWrappedDataSpace(RemoteObject remoteObject) throws Exception {
        initialize(getSystem(remoteObject.getConnectionName()), remoteObject);
    }

    public AbstractWrappedDataSpace(AS400 as400, RemoteObject remoteObject) {
        initialize(as400, remoteObject);
    }

    private void initialize(AS400 as400, RemoteObject remoteObject) {
        this.as400 = as400;
        this.remoteObject = remoteObject;
        this.dataType = retrieveDataSpaceType(as400, remoteObject);
        this.text = retrieveDescription(as400, remoteObject);
        this.dataSpaceObject = getOrLoadDataSpace();
    }

    protected abstract AS400 getSystem(String connection) throws Exception;

    public String getConnection() {
        return remoteObject.getConnectionName();
    }

    public String getName() {
        return remoteObject.getName();
    }

    public String getLibrary() {
        return remoteObject.getLibrary();
    }

    public String getText() {
        return text;
    }

    public String getObjectType() {
        return remoteObject.getObjectType();
    }

    public RemoteObject getRemoteObject() {
        return remoteObject;
    }

    public String getDataType() {
        return dataType;
    }

    public String getLengthAsText() {
        if (DECIMAL.equals(getDataType())) {
            return getLength() + ", " + getDecimalPositions();
        } else {
            return "" + getLength();
        }
    }

    public int getTextLimit() {
        if (DECIMAL.equals(getDataType()) && getDecimalPositions() > 0) {
            return getLength() + 1;
        }
        return getLength();
    }

    /**
     * Returns the length of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *ALL)</li>
     * <li>user Space (*USRSPC)</li>
     * </ul>
     * 
     * @return length
     */
    public int getLength() {
        try {
            return getLengthInternal(getOrLoadDataSpace());
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve data area value.", e);
        }
        return -1;
    }

    /**
     * Returns the number of digits of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *DEC)</li>
     * </ul>
     * 
     * @return number of digits
     */
    public int getDigits() {
        if (!(getOrLoadDataSpace() instanceof DecimalDataArea)) {
            throw produceIllegalMethodAccessException("getDigits()");
        }
        return getLength() - getDecimalPositions();
    }

    /**
     * Returns the number of decimal positions of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *DEC)</li>
     * </ul>
     * 
     * @return number of decimal positions
     */
    public int getDecimalPositions() {
        if (!(getOrLoadDataSpace() instanceof DecimalDataArea)) {
            throw produceIllegalMethodAccessException("getDecimalPositions()");
        }

        try {
            return ((DecimalDataArea)getOrLoadDataSpace()).getDecimalPositions();
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve data area value.", e);
        }
        return -1;
    }

    /**
     * Returns the character encoding of the space object.
     * <p>
     * For now this method always returns <code>null</code>.
     * 
     * @return encoding of the space object
     * @throws Exception
     */
    public String getCCSIDEncoding() throws Exception {
        if (isDataArea()) {
            if (AbstractWrappedDataSpace.CHARACTER.equals(getDataType())) {
                return ((CharacterDataArea)getOrLoadDataSpace()).getSystem().getJobCCSIDEncoding();
            } else {
                return null;
            }
        } else {
            return ((UserSpace)getOrLoadDataSpace()).getSystem().getJobCCSIDEncoding();
        }
    }

    /**
     * Returns the string value of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *CHAR)</li>
     * </ul>
     * 
     * @return value of the space object as <code>String</code>.
     */
    public String getStringValue() {
        if (!(getOrLoadDataSpace() instanceof CharacterDataArea)) {
            throw produceIllegalMethodAccessException("getStringValue()");
        }

        try {
            String value = ((CharacterDataArea)getOrLoadDataSpace()).read();
            if (value.length() > getLength()) {
                return value.substring(0, getLength());
            }
            return value;
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve data area value.", e);
        }
        return null;
    }

    /**
     * Sets the string value of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *CHAR)</li>
     * </ul>
     * 
     * @param aValue - String value of the space object
     */
    public Throwable setValue(String aValue) {
        if (!(getOrLoadDataSpace() instanceof CharacterDataArea)) {
            throw produceIllegalMethodAccessException("setValue(String)");
        }

        try {
            ((CharacterDataArea)getOrLoadDataSpace()).write(aValue);
            return null;
        } catch (Exception e) {
            return handleSaveError("Failed to save character data area value.", e);
        }
    }

    /**
     * Returns the decimal value of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *DEC)</li>
     * </ul>
     * 
     * @return value of the space object as <code>BigDecimal</code>.
     */
    public BigDecimal getDecimalValue() {
        if (!(getOrLoadDataSpace() instanceof DecimalDataArea)) {
            throw produceIllegalMethodAccessException("getDecimalValue()");
        }

        try {
            return ((DecimalDataArea)getOrLoadDataSpace()).read();
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve decimal data area value.", e);
        }

        return null;
    }

    /**
     * Sets the decimal value of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *DEC)</li>
     * </ul>
     * 
     * @param aValue - Decimal value of the space object
     */
    public Throwable setValue(BigDecimal aValue) {
        if (!(getOrLoadDataSpace() instanceof DecimalDataArea)) {
            throw produceIllegalMethodAccessException("setValue(BigDecimal)");
        }

        try {
            ((DecimalDataArea)getOrLoadDataSpace()).write(aValue);
            return null;
        } catch (Exception e) {
            return handleSaveError("Failed to save decimal data area value.", e);
        }
    }

    /**
     * Returns the boolean value of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *LGL)</li>
     * </ul>
     * 
     * @return value of the space object as <code>Boolean</code>.
     */
    public Boolean getBooleanValue() {
        if (!(getOrLoadDataSpace() instanceof LogicalDataArea)) {
            throw produceIllegalMethodAccessException("getBooleanValue()");
        }

        try {
            return ((LogicalDataArea)getOrLoadDataSpace()).read();
        } catch (Exception e) {
            ISpherePlugin.logError("Failed to retrieve boolean data area value.", e);
        }
        return null;
    }

    /**
     * Sets the boolean value of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *LGL)</li>
     * </ul>
     * 
     * @param aValue - Boolean value of the space object
     */
    public Throwable setValue(Boolean aValue) {
        if (!(getOrLoadDataSpace() instanceof LogicalDataArea)) {
            throw produceIllegalMethodAccessException("setValue(Boolean)");
        }

        try {
            ((LogicalDataArea)getOrLoadDataSpace()).write(aValue);
            return null;
        } catch (Exception e) {
            return handleSaveError("Failed to save boolean data area value.", e);
        }
    }

    /**
     * Returns the byte value of the space object.
     * <p>
     * Valid for basing objects:
     * <ul>
     * <li>Data Area (*DTAARA *ALL)</li>
     * <li>User Space (*USRSPC)</li>
     * </ul>
     * 
     * @return value of the space object as <code>byte</code>.
     */
    public byte[] getBytes() {

        if (isDataArea()) {
            return getDataAreaBytes();
        } else {
            return getUserSpaceBytes();
        }

    }

    private byte[] getDataAreaBytes() {

        byte[] bytes;
        try {
            String type = getDataType();
            if (CHARACTER.equals(type)) {
                CharacterDataArea characterDataArea = (CharacterDataArea)getOrLoadDataSpace();
                bytes = loadCharacterDataAreaBytes(characterDataArea);
            } else if (DECIMAL.equals(type)) {
                DecimalDataArea decimalDataArea = (DecimalDataArea)getOrLoadDataSpace();
                BigDecimal decimal = decimalDataArea.read();
                String[] parts = decimal.toString().split("\\.");
                String digits = "";
                String fraction = "";
                if (parts.length > 0) {
                    digits = parts[0];
                    if (parts.length > 1) {
                        fraction = parts[1];
                    }
                }
                int lenDigits = decimalDataArea.getLength() - decimalDataArea.getDecimalPositions();
                int lenFraction = decimalDataArea.getDecimalPositions();
                digits = StringHelper.getFixLengthLeading(digits, lenDigits).replaceAll(" ", "0");
                fraction = StringHelper.getFixLength(fraction, lenFraction).replaceAll(" ", "0");
                bytes = (digits + fraction).toString().getBytes();
            } else if (LOGICAL.equals(type)) {
                boolean isTrue = ((LogicalDataArea)getOrLoadDataSpace()).read();
                if (isTrue) {
                    bytes = "1".getBytes();
                } else {
                    bytes = "0".getBytes();
                }
            } else {
                throw produceIllegalMethodAccessException("getDataAreaBytes()");
            }
        } catch (Exception e) {
            // FIXME: add error handling
            bytes = new byte[] {};
        }
        return bytes;
    }

    protected abstract byte[] loadCharacterDataAreaBytes(CharacterDataArea characterDataArea) throws Exception;

    private byte[] getUserSpaceBytes() {

        byte[] bytes;
        try {
            UserSpace userSpace = (UserSpace)getOrLoadDataSpace();
            bytes = new byte[userSpace.getLength()];
            userSpace.read(bytes, 0);
            userSpace.close();
        } catch (Exception e) {
            bytes = new byte[] {};
            // FIXME: add error handling
        }

        return bytes;
    }

    private int getLengthInternal(Object object) throws Exception {
        if (object instanceof DataArea) {
            return ((DataArea)object).getLength();
        } else {
            return ((UserSpace)object).getLength();
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
        value.append(getDataType());
        value.append(")");
        return value.toString();
    }

    private Object getOrLoadDataSpace() {
        if (dataSpaceObject == null) {
            QSYSObjectPathName path = getObjectPathName();

            if (isDataArea()) {
                String type = getDataType();
                if (CHARACTER.equals(type)) {
                    dataSpaceObject = new CharacterDataArea(as400, path.getPath());
                } else if (DECIMAL.equals(type)) {
                    dataSpaceObject = new DecimalDataArea(as400, path.getPath());
                } else if (LOGICAL.equals(type)) {
                    dataSpaceObject = new LogicalDataArea(as400, path.getPath());
                } else {
                    throw produceIllegalMethodAccessException("getOrLoadDataSpace()");
                }
            } else {
                dataSpaceObject = new UserSpace(as400, path.getPath());
            }
        }
        return dataSpaceObject;
    }

    private QSYSObjectPathName getObjectPathName() {
        return new QSYSObjectPathName(remoteObject.getLibrary(), remoteObject.getName(), QsysObjectHelper.getAPIObjectType(remoteObject
            .getObjectType()));
    }

    protected boolean isDataArea() {
        return ISeries.DTAARA.equals(getObjectType());
    }

    private String retrieveDataSpaceType(AS400 anAS400, RemoteObject remoteObject) {
        if (isDataArea()) {
            QWCRDTAA qwcrdtaa = new QWCRDTAA();
            return qwcrdtaa.getType(anAS400, remoteObject.getLibrary(), remoteObject.getName());
        }
        // User spaces are treated like *CHAR data areas.
        return AbstractWrappedDataSpace.CHARACTER;
    }

    private String retrieveDescription(AS400 anAS400, RemoteObject remoteObject) {
        ObjectDescription objectDescription = new ObjectDescription(as400, remoteObject.getLibrary(), remoteObject.getName(),
            QsysObjectHelper.getAPIObjectType(remoteObject.getObjectType()));
        String text;
        try {
            text = (String)objectDescription.getValue(ObjectDescription.TEXT_DESCRIPTION);
        } catch (Exception e) {
            text = ""; // $NON-NLS-1$
        }
        return text;
    }

    private IllegalMethodAccessException produceIllegalMethodAccessException(String aMethodName) {
        return new IllegalMethodAccessException("Method " + aMethodName + " is not applicable for a data area of type: " + getDataType());
    }
}
