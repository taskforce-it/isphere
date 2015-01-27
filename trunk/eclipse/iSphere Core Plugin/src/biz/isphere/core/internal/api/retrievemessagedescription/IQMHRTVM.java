/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrievemessagedescription;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;
import biz.isphere.core.messagefileeditor.MessageDescription;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.ProgramParameter;

/**
 * This class retrieves the messages of a given data queue. The messages are
 * retrieved but not removed from the queue.
 * 
 * @author Thomas Raddatz
 */
public class IQMHRTVM extends APIProgramCallDocument {

    public static final String RETRIEVE_FIRST = "*FIRST";
    public static final String RETRIEVE_NEXT = "*NEXT";
    public static final String RETRIEVE_MSGID = "*MSGID";

    private String messageFile;
    private String library;

    /**
     * Constructs a QMHRDQM object for a given data queue and system.
     * 
     * @param system - System that hosts the data queue.
     * @param dataQueue - Name of the data queue.
     * @param library - Library that contains the data queue.
     * @throws PropertyVetoException
     */
    public IQMHRTVM(AS400 system) throws PropertyVetoException {
        super(system, "IQMHRTVM", "ISPHEREDVP");
    }

    /**
     * Sets the name of the data queue and library.
     * 
     * @param name - name of the data queue
     * @param library - name of the data queue library
     */
    public void setMessageFile(String name, String library) {

        this.messageFile = name;
        this.library = library;

    }

    public MessageDescription[] retrieveAllMessageDescriptions() {

        List<MessageDescription> messages = new ArrayList<MessageDescription>();

        try {

            int bufferSize = 1024 * 1024 * 4;
            IQMHRTVMResult result = retrieveMessageDescriptions(IQMHRTVM.RETRIEVE_FIRST, -1, bufferSize);

            while (result != null && result.getBytesAvailable() > 0 && result.getNumberOfMessagesReturned() > 0) {
                messages.addAll(result.getMessages());
                result = retrieveMessageDescriptions(IQMHRTVM.RETRIEVE_NEXT, result.getLastMessageIdReturned(), -1, bufferSize);
            }

        } catch (Exception e) {

            // System.out.println(e.getLocalizedMessage());
            // e.printStackTrace();
            // System.out.println("*** Call to QMHRTVM failed. ***");
            // return null;
            ISpherePlugin.logError("Fails to call the iSphere IQMHRTVM API", e);

        }
        
        return messages.toArray(new MessageDescription[messages.size()]);
    }

    public IQMHRTVMResult retrieveMessageDescriptions(String retrieveOption, int numMessages, int bufferSize) throws Exception {
        return retrieveMessageDescriptions(retrieveOption, "", numMessages, bufferSize);
    }

    public IQMHRTVMResult retrieveMessageDescriptions(String retrieveOption, String messageID, int numMessages, int bufferSize) throws Exception {

        if (execute(createParameterList(retrieveOption, messageID, numMessages, bufferSize))) {
            return new IQMHRTVMResult(getSystem(), messageFile, library, getParameterList()[0].getOutputData());
        }

        AS400Message[] msgList = getMessageList();
        for (int j = 0; j < msgList.length; j++) {
            System.out.println(msgList[j].getID() + " - " + msgList[j].getText()); //$NON-NLS-1$
        }

        return null;
    }

    protected ProgramParameter[] createParameterList(String retrieveOption, String messageID, int numMessages, int bufferSize) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[9];
        parameterList[0] = new ProgramParameter(bufferSize); // Receiver
        parameterList[1] = produceIntegerParameter(bufferSize); // Length
        parameterList[2] = produceStringParameter("RTVM0300", 8); // Format
        parameterList[3] = produceStringParameter(messageID, 7); // MsgID
        parameterList[4] = produceQualifiedObjectName(messageFile, library); // Object
        parameterList[5] = produceStringParameter("*YES", 10); // Rtn Fmt Ctrl
        parameterList[6] = produceByteParameter(new APIErrorCode().getBytes());
        parameterList[7] = produceStringParameter(retrieveOption, 10);
        parameterList[8] = produceIntegerParameter(numMessages);

        return parameterList;
    }
}
