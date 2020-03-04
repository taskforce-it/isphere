/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.retrieve.message;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

/**
 * Sender ID of messages of data queues that have been created with parameter
 * SENDERID set to *YES.
 * 
 * @author Thomas Raddatz
 */
public class RDQM0200SenderID extends APIFormat {

    private static final String OVERFLOW = "*OVERFLOW"; //$NON-NLS-1$
    
    private static final String JOB_NAME = "jobName"; //$NON-NLS-1$
    private static final String JOB_USER = "jobUser"; //$NON-NLS-1$
    private static final String JOB_NUMBER = "jobNumber"; //$NON-NLS-1$
    private static final String JOB_CURRENT_USER = "jobCurrentUser"; //$NON-NLS-1$
    
    public static final int LENGTH_OF_SENDER_ID = 36;

    private int maxLength;
    
    /**
     * Constructs a RDQM0200SenderID object.
     * 
     * @param system - System that is used to create the converters
     * @param bytes - bytes that have be returned by the QMHRDQM API
     * @param offset - offset from the beginning of 'bytes' to this message
     *        entry
     * @param keyLength - length of the message key
     * @throws UnsupportedEncodingException
     */
    public RDQM0200SenderID(AS400 system, int offset, RDQM0200 rdqm0200) throws UnsupportedEncodingException {
        super(system, "RDQM0200SenderID"); //$NON-NLS-1$
        setOffset(offset);

        createStructure();

        setBytes(rdqm0200.getBytes());
        
        this.maxLength = rdqm0200.getMaximumMessageTextLengthRequested();
    }

    /**
     * Return the name of the job that sent the message to the data queue.
     * 
     * @return job name
     * @throws UnsupportedEncodingException
     */
    public String getJobName() throws UnsupportedEncodingException {
        if (isOverflow(JOB_NAME)) {
            return OVERFLOW;
        }
        return getCharValue(JOB_NAME).trim();
    }

    /**
     * Return the user of the job that sent the message to the data queue.
     * 
     * @return job user name
     * @throws UnsupportedEncodingException
     */
    public String getJobUser() throws UnsupportedEncodingException {
        if (isOverflow(JOB_USER)) {
            return OVERFLOW;
        }
        return getCharValue(JOB_USER).trim();
    }

    /**
     * Return the number of the job that sent the message to the data queue.
     * 
     * @return job number
     * @throws UnsupportedEncodingException
     */
    public String getJobNumber() throws UnsupportedEncodingException {
        if (isOverflow(JOB_NUMBER)) {
            return OVERFLOW;
        }
        return getCharValue(JOB_NUMBER).trim();
    }

    /**
     * Returns the current user of the job that sent the message to the data
     * queue.
     * 
     * @return current job user
     * @throws UnsupportedEncodingException
     */
    public String getJobCurrentUserName() throws UnsupportedEncodingException {
        if (isOverflow(JOB_CURRENT_USER)) {
            return OVERFLOW;
        }
        return getCharValue(JOB_CURRENT_USER).trim();
    }

    /**
     * Creates the sub fields of the API format.
     */
    private void createStructure() {

        addCharField(JOB_NAME, 0, 10);
        addCharField(JOB_USER, 10, 10);
        addCharField(JOB_NUMBER, 20, 6);
        addCharField(JOB_CURRENT_USER, 26, 10);
    }

    private boolean isOverflow(String fieldName) {
        return isOverflow(fieldName, maxLength);
    }
    
    @Override
    public String toString() {

        try {
            return getJobNumber() + "/" + getJobUser() + "/" + getJobName() + " [" + getJobCurrentUserName() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        } catch (UnsupportedEncodingException e) {
            return e.getLocalizedMessage();
        }
    }
}
