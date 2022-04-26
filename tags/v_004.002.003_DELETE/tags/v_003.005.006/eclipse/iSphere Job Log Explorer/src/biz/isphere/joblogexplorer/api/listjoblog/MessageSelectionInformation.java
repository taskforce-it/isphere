/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.listjoblog;

import java.io.CharConversionException;
import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class MessageSelectionInformation extends APIFormat {

    public static final String LIST_DIRECTION_NEXT = "*NEXT"; //$NON-NLS-1$
    public static final String LIST_DIRECTION_PRV = "*PRV"; //$NON-NLS-1$
    public static final String MESSAGE_KEY_OLDEST = new String(new byte[] { 0x00, 0x00, 0x00, 0x00, });
    public static final String MESSAGE_KEY_NEWEST = new String(new byte[] { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, });

    public static final int KEY_ALERT_OPTION = 101; // char(9)
    public static final int KEY_REPLACEMENT_DATA_OR_IMPROMPTU_TEXT = 201; // char(*)
    public static final int KEY_MESSAGE = 301; // char(*)
    public static final int KEY_MESSAGE_WITH_REPLACEMENT_DATA = 302; // char(*)
    public static final int KEY_MESSAGE_HELP = 401; // char(*)
    public static final int KEY_MESSAGE_HELP_WITH_REPLACEMENT_DATA = 402; // char(*)
    public static final int KEY_MESSAGE_HELP_WITH_FORMATTING_CHARACTERS = 403; // char(*)
    public static final int KEY_MESSAGE_HELP_WITH_REPLACEMENT_DATA_AND_FORMATTING_CHARACTERS = 404; // char(*)
    public static final int KEY_DEFAULT_REPLY = 501; // char(*)
    public static final int KEY_QUALIFIED_SENDER_JOB_NAME = 601; // char(26)
    public static final int KEY_SENDER_TYPE = 602; // char(1)
    public static final int KEY_SENDING_PROGRAM_NAME = 603; // char(*)
    public static final int KEY_SENDING_MODULE_NAME = 604; // char(10)
    public static final int KEY_SENDING_PROCEDURE_NAME = 605; // char(*)
    public static final int KEY_SENDING_STATEMENT_NUMBER = 606; // binary(4) +
    // array(char(10))
    public static final int KEY_SENDING_USER_PROFILE = 607; // char(10)
    public static final int KEY_SENDING_PROGRAM_LIBRARY = 608; // char(10)
    public static final int KEY_RECEIVING_TYPE = 702; // char(1)
    public static final int KEY_RECEIVING_PROGRAM_NAME = 703; // char(10)
    public static final int KEY_RECEIVING_MODULE_NAME = 704; // char(10)
    public static final int KEY_RECEIVING_PROCEDURE_NAME = 705; // char(*)
    public static final int KEY_RECEIVING_STATEMENT_NUMBER = 706; // binary(4) +
    // array(char(10))
    public static final int KEY_RECEIVING_PROGRAM_LIBRARY = 708; // char(10)
    public static final int KEY_MESSAGE_FILE_LIBRARY_USED = 801; // char(10)
    public static final int KEY_PROBLEM_IDENTIFICATION = 901; // char(30)
    public static final int KEY_REPLY_STATUS = 1001; // char(1)
    public static final int KEY_REQUEST_STATUS = 1101; // char(1)
    public static final int KEY_REQUEST_LEVEL = 1201; // binary(4)
    public static final int KEY_CCSID_TEXT = 1301; // binary(4)
    public static final int KEY_CCSID_CONVERSIONS_STATUS_TEXT = 1302; // binary(4)
    public static final int KEY_CCSID_DATA = 1303; // binary(4)
    public static final int KEY_CCSID_CONVERSIONS_STATUS_DATA = 1304; // binary(4)

    private static final String LIST_DIRECTION = "listDirection"; //$NON-NLS-1$
    private static final String JOB_NAME = "jobName"; //$NON-NLS-1$
    private static final String JOB_USER = "jobUser"; //$NON-NLS-1$
    private static final String JOB_NUMBER = "jobNumber"; //$NON-NLS-1$
    private static final String INTERNAL_JOB_IDENTIFIER = "internalJobIdentifier"; //$NON-NLS-1$
    private static final String STARTING_MESSAGE_KEY = "startingMessageKey"; //$NON-NLS-1$
    private static final String MAXIMUM_MESSAGE_LENGTH = "maximumMessageLength"; //$NON-NLS-1$
    private static final String MAXIMUM_MESSAGE_HELP_LENGTH = "maximumMessageHelpLength"; //$NON-NLS-1$
    private static final String OFFSET_TO_IDENTIFIERS_OF_FIELDS_TO_RETURN = "offsetOfIdentifiersOfFieldsToReturn"; //$NON-NLS-1$
    private static final String NUMBER_OF_FIELDS_TO_RETURN = "numberOfFieldsToReturn"; //$NON-NLS-1$
    private static final String OFFSET_TO_CALL_MESSAGE_QUEUE_NAME = "offsetToCallMessageQueueName"; //$NON-NLS-1$
    private static final String SIZE_OF_CALL_MESSAGE_QUEUE_NAME = "sizeOfCallMessageQueueName"; //$NON-NLS-1$
    private static final String CALL_MESSAGE_QUEUE_NAME = "callMessageQueueName"; //$NON-NLS-1$
    private static final String KEY = "key-"; //$NON-NLS-1$

    private static final int NUM_KEYS = 32;

    private int offsetCallMessageQueueName;
    private int offsetFieldIdentifiers;
    private int numKeys;

    public MessageSelectionInformation(AS400 system) throws CharConversionException, UnsupportedEncodingException {
        super(system, "MessageSelectionInformation"); //$NON-NLS-1$

        createStructure();

        setCharValue(LIST_DIRECTION, LIST_DIRECTION_NEXT);
        setCharValue(JOB_NAME, "*"); //$NON-NLS-1$
        setCharValue(JOB_USER, ""); //$NON-NLS-1$
        setCharValue(JOB_NUMBER, ""); //$NON-NLS-1$
        setCharValue(INTERNAL_JOB_IDENTIFIER, ""); //$NON-NLS-1$
        setCharValue(STARTING_MESSAGE_KEY, MESSAGE_KEY_OLDEST);
        setInt4Value(MAXIMUM_MESSAGE_LENGTH, 200);
        setInt4Value(MAXIMUM_MESSAGE_HELP_LENGTH, 500);
        setInt4Value(OFFSET_TO_IDENTIFIERS_OF_FIELDS_TO_RETURN, offsetFieldIdentifiers);
        setInt4Value(NUMBER_OF_FIELDS_TO_RETURN, 0);
        setInt4Value(OFFSET_TO_CALL_MESSAGE_QUEUE_NAME, offsetCallMessageQueueName);
        setInt4Value(SIZE_OF_CALL_MESSAGE_QUEUE_NAME, 1);
        setCharValue(CALL_MESSAGE_QUEUE_NAME, "*"); //$NON-NLS-1$
    }

    public void setJob(String name, String user, String number) throws CharConversionException, UnsupportedEncodingException {
        setCharValue(JOB_NAME, name);
        setCharValue(JOB_USER, user);
        setCharValue(JOB_NUMBER, number);
    }

    public void addField(int key) {

        numKeys++;
        String fieldName = KEY + numKeys;
        setInt4Value(fieldName, key);

        setInt4Value(NUMBER_OF_FIELDS_TO_RETURN, numKeys);
    }

    /**
     * Creates the PRDI0100 structure.
     */
    private void createStructure() {

        addCharField(LIST_DIRECTION, 0, 10);
        addCharField(JOB_NAME, 10, 10);
        addCharField(JOB_USER, 20, 10);
        addCharField(JOB_NUMBER, 30, 6);
        addCharField(INTERNAL_JOB_IDENTIFIER, 36, 16);
        addCharField(STARTING_MESSAGE_KEY, 52, 4);
        addInt4Field(MAXIMUM_MESSAGE_LENGTH, 56);
        addInt4Field(MAXIMUM_MESSAGE_HELP_LENGTH, 60);
        addInt4Field(OFFSET_TO_IDENTIFIERS_OF_FIELDS_TO_RETURN, 64);
        addInt4Field(NUMBER_OF_FIELDS_TO_RETURN, 68);
        addInt4Field(OFFSET_TO_CALL_MESSAGE_QUEUE_NAME, 72);
        addInt4Field(SIZE_OF_CALL_MESSAGE_QUEUE_NAME, 76);

        offsetFieldIdentifiers = getLength();
        for (int i = 1; i <= NUM_KEYS; i++) {
            addInt4Field(KEY + i, offsetFieldIdentifiers + ((i - 1) * 4));
        }

        offsetCallMessageQueueName = getLength();
        addCharField(CALL_MESSAGE_QUEUE_NAME, offsetCallMessageQueueName, 10);
    }
}
