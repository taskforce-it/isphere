/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.message;

import java.beans.PropertyVetoException;

import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.ProgramParameter;

/**
 * This class retrieves the messages of a given data queue. The messages are
 * retrieved but not removed from the queue.
 * 
 * @author Thomas Raddatz
 */
public class QMHRDQM extends APIProgramCallDocument {

    private String dataQueue;
    private String library;
    private boolean isSenderIdIncluded;

    /**
     * Constructs a QMHRDQM object for a given data queue and system.
     * 
     * @param system - System that hosts the data queue.
     * @param dataQueue - Name of the data queue.
     * @param library - Library that contains the data queue.
     * @throws PropertyVetoException
     */
    public QMHRDQM(AS400 system) throws PropertyVetoException {
        super(system, "QMHRDQM", "*LIBL");
    }

    /**
     * Sets the name of the data queue and library.
     * 
     * @param name - name of the data queue
     * @param library - name of the data queue library
     */
    public void setDataQueue(String name, String library, boolean isSenderIdIncluded) {

        this.dataQueue = name;
        this.library = library;
        this.isSenderIdIncluded = isSenderIdIncluded;

    }

    /**
     * Retrieves messages from a given data queue.
     * <p>
     * This method tries to calculate the required buffer size based on the
     * number of messages to retrieve and the message length specified. Though
     * it cannot be guaranteed, that all messages can be retrieved, because of
     * several "reserved" fields with an unknown length.
     * 
     * @param selectionType - selection type
     * @param numMessages - number of messages to retrieve
     * @param messageLengthToRetrieve - the number of message text bytes to
     *        return for each data queue entry
     * @param includeSenderID - specifies whether or not to include the sender
     *        ID in the message text
     * @return messages of the queue
     * @throws Exception
     */
    public RDQM0200 retrieveMessages(String selectionType, int numMessages, int messageLengthToRetrieve) throws Exception {

        RDQS0100 rdqs0100 = new RDQS0100(getSystem(), selectionType, messageLengthToRetrieve);
        RDQM0200 rdqm0200 = new RDQM0200(getSystem(), numMessages, messageLengthToRetrieve, isSenderIdIncluded);

        if (execute(createParameterList(rdqm0200, rdqs0100))) {
            rdqm0200.setBytes(getParameterList()[0].getOutputData());
            return rdqm0200;
        }

        AS400Message[] msgList = getMessageList();
        for (int j = 0; j < msgList.length; j++) {
            // Xystem.out.println(msgList[j].getID() + " - " + msgList[j].getText()); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Retrieves messages from a given keyed data queue.
     * 
     * @param keySearchOrder - key search order. The possible search order
     *        values are defined in {@link RDQS0200} as ORDER_* constants.
     * @param key - the key field to be compared with the actual keys of the
     *        messages on the data queue.
     * @param numMessages - number of messages to retrieve
     * @param messageLengthToRetrieve - the number of message text bytes to
     *        return for each data queue entry
     * @return
     * @throws Exception
     */
    public RDQM0200 retrieveMessagesByKey(String keySearchOrder, String key, int numMessages, int messageLengthToRetrieve) throws Exception {

        return retrieveMessagesByKey(keySearchOrder, key, numMessages, messageLengthToRetrieve, key.length());
    }

    /**
     * Retrieves messages from a given keyed data queue.
     * 
     * @param keySearchOrder - key search order. The possible search order
     *        values are defined in {@link RDQS0200} as ORDER_* constants.
     * @param key - the key field to be compared with the actual keys of the
     *        messages on the data queue.
     * @param numMessages - number of messages to retrieve
     * @param messageLengthToRetrieve - the number of message text bytes to
     *        return for each data queue entry
     * @param keyLengthToRetrieve - The number of message key bytes to return
     *        for each data queue entry
     * @return
     * @throws Exception
     */
    public RDQM0200 retrieveMessagesByKey(String keySearchOrder, String key, int numMessages, int messageLengthToRetrieve, int keyLengthToRetrieve)
        throws Exception {

        RDQS0200 rdqs0200 = new RDQS0200(getSystem(), key, keyLengthToRetrieve, messageLengthToRetrieve);
        rdqs0200.setKeySearchOrder(keySearchOrder);

        RDQM0200 rdqm0200 = new RDQM0200(getSystem(), numMessages, messageLengthToRetrieve, isSenderIdIncluded, keyLengthToRetrieve);

        if (execute(createParameterList(rdqm0200, rdqs0200))) {
            rdqm0200.setBytes(getParameterList()[0].getOutputData());
            return rdqm0200;
        }

        AS400Message[] msgList = getMessageList();
        for (int j = 0; j < msgList.length; j++) {
            // Xystem.out.println(msgList[j].getID() + " - " + msgList[j].getText()); //$NON-NLS-1$
        }

        return null;
    }

    protected ProgramParameter[] createParameterList(APIFormat... formats) throws Exception {

        APIFormat format = formats[0];
        APIFormat selectionInformation = formats[1];

        ProgramParameter[] parameterList = new ProgramParameter[8];
        parameterList[0] = new ProgramParameter(format.getLength()); // Receiver
        parameterList[1] = produceIntegerParameter(format.getLength()); // Length
        parameterList[2] = produceStringParameter(format.getName(), 8); // Format
        parameterList[3] = produceQualifiedObjectName(dataQueue, library); // Object
        parameterList[4] = produceByteParameter(selectionInformation.getBytes());
        parameterList[5] = produceIntegerParameter(selectionInformation.getLength());
        parameterList[6] = produceStringParameter(selectionInformation.getName(), 10);
        parameterList[7] = produceByteParameter(new APIErrorCode().getBytes());

        return parameterList;
    }
}
