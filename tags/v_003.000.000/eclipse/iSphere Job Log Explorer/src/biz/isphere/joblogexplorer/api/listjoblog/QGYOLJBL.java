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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

/**
 * Open List of Job Log Messages (QGYOLJBL) API
 */
public class QGYOLJBL extends APIProgramCallDocument {

    // Roughly 1200 bytes per entry
    private static final int BUFFER_SIZE = 256 * 1024;
    private static final int NUMBER_OF_RECORDS = 256;

    private Set<Integer> keys;
    private List<JobLogListener> listeners;

    private String name;
    private String user;
    private String number;

    private String requestHandle;
    private int totalNumberOfRecords;
    private boolean isPending;

    public QGYOLJBL(AS400 system) {
        super(system, "QGYOLJBL", "QSYS"); //$NON-NLS-1$ //$NON-NLS-2$

        this.keys = new HashSet<Integer>();

        try {
            setJob("*"); //$NON-NLS-1$
        } catch (Exception e) {
            // Ignore this error
        }
    }

    public void setJob(String name) {
        setJob(name, "", ""); //$NON-NLS-1$//$NON-NLS-2$
    }

    public void setJob(String name, String user, String number) {

        this.name = name;
        this.user = user;
        this.number = number;
    }

    public void addKey(Integer key) {
        keys.add(key);
    }

    public void addListener(JobLogListener listener) {

        if (listeners == null) {
            listeners = new ArrayList<JobLogListener>();
        }

        listeners.add(listener);
    }

    public boolean execute() {
        return execute(BUFFER_SIZE);
    }

    public boolean execute(int bufferSize) {

        try {

            MessageSelectionInformation messageSelectionInformation = new MessageSelectionInformation(getSystem());
            messageSelectionInformation.setJob(name, user, number);
            for (Integer key : keys) {
                messageSelectionInformation.addField(key.intValue());
            }

            if (!execute(createParameterList(bufferSize, messageSelectionInformation))) {
                return false;
            }

            OpenListInformation openListInformation = new OpenListInformation(getSystem());
            openListInformation.setBytes(getParameterList()[2].getOutputData());

            requestHandle = openListInformation.getRequestHandle();
            totalNumberOfRecords = openListInformation.getTotalRecords();
            if (OpenListInformation.STATUS_PENDING.equals(openListInformation.getListStatusIndicator())) {
                isPending = true;
            } else {
                isPending = false;
            }

            if (listeners != null) {
                processReceivedRecords(openListInformation);
            }

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    public boolean isPending() {
        return isPending;
    }

    public String getRequestHandle() {
        return requestHandle;
    }

    public int getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    private void processReceivedRecords(OpenListInformation openListInformation) throws CharConversionException, UnsupportedEncodingException {

        OLJL0100 oljl0100 = new OLJL0100(getSystem());
        oljl0100.setBytes(getParameterList()[0].getOutputData());

        for (int i = 0; i < openListInformation.getRecordsReturned(); i++) {
            if (i == 0) {
                oljl0100.setOffset(0);
            } else {
                oljl0100.setOffset(oljl0100.getOffsetToNextEntry());
            }

            notifyListeners(oljl0100);
        }

    }

    private void notifyListeners(OLJL0100 oljl0100) {

        for (JobLogListener listener : listeners) {
            listener.addNewEntry(oljl0100);
        }
    }

    /**
     * Produces the parameter list for calling the QUSRMBRD API.
     */
    protected ProgramParameter[] createParameterList(int length, MessageSelectionInformation messageSelectionInformation) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[7];
        parameterList[0] = new ProgramParameter(length); // Receiver
        parameterList[1] = produceIntegerParameter(length); // Length
        parameterList[2] = new ProgramParameter(80); // List information
        parameterList[3] = produceIntegerParameter(NUMBER_OF_RECORDS); // Records
        parameterList[4] = produceByteParameter(messageSelectionInformation.getBytes());
        parameterList[5] = produceIntegerParameter(messageSelectionInformation.getLength());
        parameterList[6] = produceByteParameter(new APIErrorCode().getBytes());

        return parameterList;
    }

}
