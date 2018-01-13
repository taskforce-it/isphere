/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.description;

import java.beans.PropertyVetoException;

import biz.isphere.core.internal.api.APIFormat;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.ProgramParameter;

/**
 * This class retrieves the description of a given data queue.
 * 
 * @author Thomas Raddatz
 */
public class QMHQRDQD extends APIProgramCallDocument {

    private String dataQueue;
    private String library;

    /**
     * Constructs a QMHQRDQD object for a given data queue and system.
     * 
     * @param system - System that hosts the data queue.
     * @param dataQueue - Name of the data queue.
     * @param library - Library that contains the data queue.
     * @throws PropertyVetoException
     */
    public QMHQRDQD(AS400 system) throws PropertyVetoException {
        super(system, "QMHQRDQD", "*LIBL"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Retrieves the description of the data queue.
     * 
     * @return description of the data queue.
     * @throws Exception
     */
    public RDQD0100 retrieveDescription(String dataQueue, String library) throws Exception {

        this.dataQueue = dataQueue;
        this.library = library;

        RDQD0100 rdqm0100 = new RDQD0100(getSystem());

        if (execute(createParameterList(rdqm0100))) {
            rdqm0100.setBytes(getParameterList()[0].getOutputData());
            return rdqm0100;
        }

        AS400Message[] msgList = getMessageList();
        for (int j = 0; j < msgList.length; j++) {
            // Xystem.out.println(msgList[j].getID() + " - " + msgList[j].getText()); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Retrieves the remote description of the data queue.
     * 
     * @return remote description of the data queue.
     * @throws Exception
     */
    public RDQD0200 retrieveRemoteDescription() throws Exception {

        RDQD0200 rdqm0200 = new RDQD0200(getSystem());

        if (execute(createParameterList(rdqm0200))) {
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

        ProgramParameter[] parameterList = new ProgramParameter[4];
        parameterList[0] = new ProgramParameter(format.getLength()); // Receiver
        parameterList[1] = produceIntegerParameter(format.getLength()); // Length
        parameterList[2] = produceStringParameter(format.getName(), 8); // Format
        parameterList[3] = produceQualifiedObjectName(dataQueue, library); // Object

        return parameterList;
    }
}
