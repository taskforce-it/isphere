/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import biz.isphere.base.internal.StringHelper;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Bin2;
import com.ibm.as400.access.AS400Bin4;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharConverter;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;

/**
 * This class is the base class for API calls.
 * 
 * @author Thomas Raddatz
 */
public abstract class APIProgramCallDocument {

    private static final short VARLEN_BYTES = 2;

    private AS400 system;
    private String programName;
    private String programLibraryName;
    private ProgramCall program;
    private ProgramParameter[] parameterList;

    private CharConverter conv;
    private AS400Bin2 shortConv;
    private AS400Bin4 intConv;
    private AS400Message[] errorMessages;

    /**
     * Produces an APIProgramCallDocument object.
     * 
     * @param system - system used to create the data converter
     * @param name - program name
     * @param library - program library name
     * @throws PropertyVetoException
     */
    protected APIProgramCallDocument(AS400 system, String name, String library) {
        this.system = system;
        this.programName = name;
        this.programLibraryName = library;

        program = new ProgramCall(getSystem());
    }

    public boolean execute(ProgramParameter[] parameterList) throws PropertyVetoException, AS400SecurityException, ErrorCompletingRequestException,
        IOException, InterruptedException, ObjectDoesNotExistException {

        // Set the program name here, to get rid of the PropertyVetoException in
        // the constructor.
        if (StringHelper.isNullOrEmpty(program.getProgram())) {
            program.setProgram(getPath(programName, programLibraryName, "*PGM")); //$NON-NLS-1$
        }

        this.parameterList = parameterList;

        program.setParameterList(getParameterList());
        if (program.run()) {
            errorMessages = new AS400Message[] {};
            return true;
        }

        errorMessages = program.getMessageList();
        return false;
    }

    public ProgramParameter[] getParameterList() {

        return parameterList;
    }

    /**
     * Returns the list of error messages of the last program call.
     * 
     * @return error messages
     */
    public AS400Message[] getMessageList() {
        if (errorMessages == null) {
            return new AS400Message[] {};
        }
        return errorMessages;
    }

    public String getErrorMessage() {

        AS400Message[] messages = getMessageList();
        if (messages == null || messages.length == 0) {
            return ""; //$NON-NLS-1$
        }

        // for (AS400Message message : messages) {
        // if (message.getType() == AS400Message.ESCAPE) {
        //                return message.getID() + ": " +  message.getText(); //$NON-NLS-1$
        // }
        // }
        
        return messages[0].getID() + ": " +  messages[0].getText();
    }

    /**
     * Produces the path to an IBM i object.
     * 
     * @param object - name of the object
     * @param library - library that contains the object
     * @param type - type of the object
     * @return object path
     */
    protected String getPath(String object, String library, String type) {
        if (type.startsWith("*")) { //$NON-NLS-1$
            type = type.substring(1);
        }
        if (library.equals("QSYS")) {
            return "/QSYS.LIB/" + object + "." + type; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } else {
            return "/QSYS.LIB/" + library + ".LIB/" + object + "." + type; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * Produces a NULL (*omit) parameter.
     * 
     * @return NULL parameter
     * @throws PropertyVetoException
     */
    protected ProgramParameter produceNullParameter() throws PropertyVetoException {
        ProgramParameter tParameter = new ProgramParameter();
        tParameter.setNullParameter(true);
        tParameter.setParameterType(ProgramParameter.PASS_BY_REFERENCE);
        return tParameter;
    }

    /**
     * Produces a 4-byte integer parameter.
     * 
     * @param aValue - integer parameter value
     * @return 4-byte integer parameter
     */
    protected ProgramParameter produceIntegerParameter(int aValue) {
        byte[] bytes = new byte[4];
        getIntConverter().toBytes(new Integer(aValue), bytes);
        return new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, bytes, bytes.length);
    }

    /**
     * Produces a byte array parameter.
     * 
     * @param bytes - byte value
     * @return byte parameter
     */
    protected ProgramParameter produceByteParameter(byte[] bytes) {
        return new ProgramParameter(ProgramParameter.PASS_BY_REFERENCE, bytes);
    }

    /**
     * Produces a string parameter.
     * 
     * @param value - string value
     * @param length - maximum length of the parameter value
     * @return string parameter
     */
    protected ProgramParameter produceStringParameter(String value, int length) throws Exception {

        byte[] tBytes = new byte[length];
        Arrays.fill(tBytes, 0, tBytes.length, (byte)0x40);
        getCharConverter().stringToByteArray(value, tBytes);

        ProgramParameter tParameter = new ProgramParameter(length);
        tParameter.setParameterType(ProgramParameter.PASS_BY_REFERENCE);
        tParameter.setInputData(tBytes);

        return tParameter;
    }

    /**
     * Produces a varying string parameter.
     * 
     * @param value - string parameter value
     * @param maxLen - maximum length of the parameter value
     * @return string parameter
     * @throws Exception
     */
    protected ProgramParameter produceVarlenStringParameter(String value, int maxLen) throws Exception {

        int tLength = value.length() + VARLEN_BYTES;
        byte[] tBytes = new byte[maxLen];
        Arrays.fill(tBytes, 0, VARLEN_BYTES, (byte)0x00);
        Arrays.fill(tBytes, VARLEN_BYTES, tBytes.length - VARLEN_BYTES, (byte)0x40);
        getShortConverter().toBytes((short)value.length(), tBytes);
        getCharConverter().stringToByteArray(value, tBytes, VARLEN_BYTES);

        ProgramParameter tParameter = new ProgramParameter(tLength);
        tParameter.setParameterType(ProgramParameter.PASS_BY_REFERENCE);
        tParameter.setInputData(tBytes);

        return tParameter;
    }

    /**
     * Produces a parameter of an array of strings.
     * 
     * @param stringArray - array of strings
     * @param maxLength - maximum length of an array item
     * @return string array parameter
     * @throws Exception
     */
    protected ProgramParameter produceStringArrayParameter(String[] stringArray, int maxLength) throws Exception {
        byte[] tBytes = new byte[(maxLength * stringArray.length) + 2];

        // Number of array items
        Arrays.fill(tBytes, 0, 2, (byte)0x00);

        // Array of Char(10)
        Arrays.fill(tBytes, 2, tBytes.length - 2, (byte)0x40);

        // Set number of array entries to the first 2 bytes
        int tOffset = 0;
        tBytes[tOffset] = (byte)(stringArray.length >> 8);
        tBytes[tOffset + 1] = (byte)(stringArray.length /* >> 0 */);

        // Set array items, starting after the number of array items.
        tOffset = tOffset + 2;
        byte[] tItemBytes = new byte[maxLength];
        for (int i = 0; i < stringArray.length; i++) {
            Arrays.fill(tItemBytes, 0, tItemBytes.length, (byte)0x40);
            getCharConverter().stringToByteArray(stringArray[i], tItemBytes);
            System.arraycopy(tItemBytes, 0, tBytes, tOffset, tItemBytes.length);
            tOffset = tOffset + tItemBytes.length;
        }

        ProgramParameter tParameter = new ProgramParameter(tOffset);
        tParameter.setParameterType(ProgramParameter.PASS_BY_REFERENCE);
        tParameter.setInputData(tBytes);

        return tParameter;
    }

    /**
     * Produces a parameter of an array of strings.
     * 
     * @param stringArray - array of strings
     * @param maxLength - maximum length of an array item
     * @return string array parameter
     * @throws Exception
     */
    protected ProgramParameter produceVarlenStringArrayParameter(ArrayList<String> stringArray, int maxLength) throws Exception {

        // aLength = length of RPG array item 'procedure name'
        byte[] tBytes = new byte[((maxLength + 2) * stringArray.size()) + 2];

        // Number of array items
        Arrays.fill(tBytes, 0, 2, (byte)0x00);

        // Set number of array entries to the first 2 bytes
        int tOffset = 0;
        tBytes[tOffset] = (byte)(stringArray.size() >> 8);
        tBytes[tOffset + 1] = (byte)(stringArray.size());

        // Set array items, starting after the number of array items.
        tOffset = tOffset + 2;
        byte[] tItemBytes = new byte[maxLength + 2];
        for (String tStringEntry : stringArray) {
            tItemBytes[0] = (byte)((byte)tStringEntry.length() >> 8);
            tItemBytes[1] = (byte)(tStringEntry.length());
            Arrays.fill(tItemBytes, 2, tItemBytes.length - 2, (byte)0x40);
            getCharConverter().stringToByteArray(tStringEntry, tItemBytes, VARLEN_BYTES);
            System.arraycopy(tItemBytes, 0, tBytes, tOffset, tItemBytes.length);
            tOffset = tOffset + tItemBytes.length;
        }

        ProgramParameter tParameter = new ProgramParameter(tOffset);
        tParameter.setParameterType(ProgramParameter.PASS_BY_REFERENCE);
        tParameter.setInputData(tBytes);

        return tParameter;
    }

    /**
     * Produces a parameter that contains a qualified object name.
     * 
     * @param object - name of the object
     * @param library - name of the library that contains the object
     * @return qualified object name parameter
     * @throws Exception
     */
    protected ProgramParameter produceQualifiedObjectName(String object, String library) throws Exception {
        byte[] tBytes = new byte[20];
        Arrays.fill(tBytes, 0, tBytes.length, (byte)0x40);

        int tOffset = 0;
        byte[] tItemBytes = new byte[10];

        // Copy object name
        Arrays.fill(tItemBytes, 0, tItemBytes.length, (byte)0x40);
        getCharConverter().stringToByteArray(object, tItemBytes);
        System.arraycopy(tItemBytes, 0, tBytes, tOffset, tItemBytes.length);
        tOffset = tOffset + tItemBytes.length;

        // Copy library name
        Arrays.fill(tItemBytes, 0, tItemBytes.length, (byte)0x40);
        getCharConverter().stringToByteArray(library, tItemBytes);
        System.arraycopy(tItemBytes, 0, tBytes, tOffset, tItemBytes.length);
        tOffset = tOffset + tItemBytes.length;

        ProgramParameter tParameter = new ProgramParameter(tOffset);
        tParameter.setParameterType(ProgramParameter.PASS_BY_REFERENCE);
        tParameter.setInputData(tBytes);

        return tParameter;
    }

    /**
     * Produces a string converter.
     * 
     * @return string converter
     */
    protected CharConverter getCharConverter() throws Exception {
        if (conv == null) {
            conv = new CharConverter(getSystem().getCcsid(), getSystem());
        }
        return conv;
    }

    /**
     * Produces a converter to convert 4-byte integer values.
     * 
     * @return 4-byte integer converter
     */
    protected AS400Bin4 getIntConverter() {
        if (intConv == null) {
            intConv = new AS400Bin4();
        }
        return intConv;
    }

    /**
     * Produces a converter to convert 2-byte integer values.
     * 
     * @return 2-byte integer converter
     */
    protected AS400Bin2 getShortConverter() {
        if (shortConv == null) {
            shortConv = new AS400Bin2();
        }
        return shortConv;
    }

    /**
     * Returns the system that is used to create the converters.
     * 
     * @return system
     */
    protected AS400 getSystem() {
        return system;
    }
}
