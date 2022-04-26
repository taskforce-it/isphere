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

import org.eclipse.core.runtime.IProgressMonitor;

import biz.isphere.base.internal.Buffer;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;
import biz.isphere.core.messagefileeditor.MessageDescription;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

/**
 * This class retrieves the messages descriptions of a given message file.
 * 
 * @author Thomas Raddatz
 */
public class IQMHRTVM extends APIProgramCallDocument {

    public static final int ALL_MESSAGES = -1;

    public static final String RTVM0300 = "RTVM0300";
    public static final String RTVM0400 = "RTVM0400";

    public static final String RETRIEVE_FIRST = "*FIRST";
    public static final String RETRIEVE_NEXT = "*NEXT";
    public static final String RETRIEVE_MSGID = "*MSGID";

    private String connectionName;
    private String messageFile;
    private String library;
    private String format;

    /**
     * Constructs a IQMHRTVM object for a system and connection.
     * 
     * @param system - System that hosts the message file.
     * @param connectionName - Name of the RDi connection.
     * @throws PropertyVetoException
     */
    public IQMHRTVM(AS400 system, String connectionName) {
        this(system, connectionName, RTVM0400);
    }

    /**
     * Constructs a IQMHRTVM object for a system and connection.
     * 
     * @param system - System that hosts the message file.
     * @param connectionName - Name of the RDi connection.
     * @param format - Format of the retrieved data ({@link #RTVM0300} or
     *        {@link #RTVM0400}).
     * @throws PropertyVetoException
     */
    private IQMHRTVM(AS400 system, String connectionName, String format) {
        super(system, "IQMHRTVM", IBMiHostContributionsHandler.getISphereLibrary(connectionName));

        this.connectionName = connectionName;
        this.format = format;
    }

    /**
     * Sets the name of the message file and library.
     * 
     * @param name - name of the message file
     * @param library - name of the message file library
     */
    public void setMessageFile(String name, String library) {

        this.messageFile = name;
        this.library = library;

    }

    /**
     * Returns the message description of a given message ID.
     * 
     * @param messageId - ID of the message description being retrieved
     * @return message description
     */
    public MessageDescription retrieveMessageDescription(String messageId) {

        MessageDescription messageDescription = null;

        try {

            int bufferSize = Buffer.size("4 kByte");
            IQMHRTVMResult result = retrieveMessageDescriptions(IQMHRTVM.RETRIEVE_MSGID, messageId, 1, bufferSize);

            if (result != null && result.getBytesAvailable() > 0 && result.getNumberOfMessagesReturned() == 1) {
                messageDescription = result.getMessages().get(0);
            }

        } catch (Exception e) {
            ISpherePlugin.logError("Failed calling the iSphere IQMHRTVM API.", e);
        }

        return messageDescription;
    }

    /**
     * Retrieves all message descriptions of the message file.
     * 
     * @param monitor - monitor
     * @return array of message descriptions
     */
    public MessageDescription[] retrieveAllMessageDescriptions() {
        return retrieveAllMessageDescriptions(null);
    }

    /**
     * Retrieves all message descriptions of the message file.
     * 
     * @param monitor - monitor
     * @return array of message descriptions
     */
    public MessageDescription[] retrieveAllMessageDescriptions(IProgressMonitor monitor) {

        List<MessageDescription> messages = new ArrayList<MessageDescription>();

        try {

            // long startTime = System.currentTimeMillis();
            // long numCalls = 0;

            // int bufferSize = 1024 * 1024 * 1 / 2;
            int bufferSize = Buffer.size("512 kByte");
            IQMHRTVMResult result = retrieveMessageDescriptions(IQMHRTVM.RETRIEVE_FIRST, "", ALL_MESSAGES, bufferSize);

            while (result != null && result.getBytesAvailable() > 0 && result.getNumberOfMessagesReturned() > 0) {
                // numCalls++;
                messages.addAll(result.getMessages());
                if (monitor != null && monitor.isCanceled()) {
                    break;
                }
                result = retrieveMessageDescriptions(IQMHRTVM.RETRIEVE_NEXT, result.getLastMessageIdReturned(), ALL_MESSAGES, bufferSize);
            }

            // Xystem.out.println("mSecs for " + messageFile +
            // " and buffer size " + (bufferSize / 1024) + "KB: "
            // + (System.currentTimeMillis() - startTime));
            // Xystem.out.println("Loaded " + messages.size() +
            // " messages. Called the API " + numCalls + " times.");

        } catch (Exception e) {

            // Xystem.out.println(e.getLocalizedMessage());
            // e.printStackTrace();
            // Xystem.out.println("*** Call to QMHRTVM failed. ***");
            // return null;
            ISpherePlugin.logError("Failed calling the iSphere IQMHRTVM API.", e);

        }

        return messages.toArray(new MessageDescription[messages.size()]);
    }

    /**
     * Checks whether or not the message description that is associated to a
     * given message ID exists.
     * 
     * @param messageId - message ID
     * @return <code>true</code> on success, else <code>false</code>.
     */
    public boolean exists(String messageId) {

        try {

            if (execute(createParameterList(RETRIEVE_MSGID, messageId, 1, 8))) {
                return true;
            }

        } catch (Exception e) {
            ISpherePlugin.logError("Failed calling the iSphere IQMHRTVM API.", e);
        }

        return false;
    }

    /**
     * Retrieves a given number of message descriptions, starting at a specified
     * message ID description.
     * 
     * @param retrieveOption - specifies the starting point. Must be one of
     *        {@link #RETRIEVE_FIRST}, {@link #RETRIEVE_NEXT} or
     *        {@link #RETRIEVE_MSGID    }.
     * @param messageID - message that defines the starting point. This
     *        parameter is ignored when <i>retrieveOption</i> is set to
     *        {@link #RETRIEVE_FIRST}.
     * @param numMessages - number of messages to retrieve or
     *        {@link #ALL_MESSAGES} for all messages. The method may return less
     *        messages than specified, if the buffer is not large enough.
     * @param bufferSize - buffer to return the message descriptions
     * @return result of the retrieve operation
     * @throws Exception
     */
    private IQMHRTVMResult retrieveMessageDescriptions(String retrieveOption, String messageID, int numMessages, int bufferSize) throws Exception {

        if (execute(createParameterList(retrieveOption, messageID, numMessages, bufferSize))) {
            return new IQMHRTVMResult(getSystem(), connectionName, messageFile, library, getParameterList()[0].getOutputData(), format);
        }

        // AS400Message[] msgList = getMessageList();
        // for (int j = 0; j < msgList.length; j++) {
        //            ISpherePlugin.logError(msgList[j].getID() + " - " + msgList[j].getText(), null); //$NON-NLS-1$
        // }
        //        ISpherePlugin.logError("*** Call to IQMHRTVM failed. See previous messages ***", null); //$NON-NLS-1$
        return null;
    }

    /**
     * Produces the parameter list for calling the IQMHRTVM API.
     * 
     * @param retrieveOption - specifies the starting point. Must be one of
     *        {@link #RETRIEVE_FIRST}, {@link #RETRIEVE_NEXT} or
     *        {@link #RETRIEVE_MSGID    }.
     * @param messageID - message that defines the starting point. This
     *        parameter is ignored when <i>retrieveOption</i> is set to
     *        {@link #RETRIEVE_FIRST}.
     * @param numMessages - number of messages to retrieve or
     *        {@link #ALL_MESSAGES} for all messages. The method may return less
     *        messages than specified, if the buffer is not large enough.
     * @param bufferSize - buffer to return the message descriptions
     * @return parameter list
     * @throws Exception
     */
    protected ProgramParameter[] createParameterList(String retrieveOption, String messageID, int numMessages, int bufferSize) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[9];
        parameterList[0] = new ProgramParameter(bufferSize); // Receiver
        parameterList[1] = produceIntegerParameter(bufferSize); // Length
        parameterList[2] = produceStringParameter(format, 8); // Format
        parameterList[3] = produceStringParameter(messageID, 7); // MsgID
        parameterList[4] = produceQualifiedObjectName(messageFile, library); // Object
        parameterList[5] = produceStringParameter("*YES", 10); // Rtn Fmt Ctrl
        parameterList[6] = produceByteParameter(new APIErrorCode().getBytes());
        parameterList[7] = produceStringParameter(retrieveOption, 10);
        parameterList[8] = produceIntegerParameter(numMessages);

        return parameterList;
    }
}
