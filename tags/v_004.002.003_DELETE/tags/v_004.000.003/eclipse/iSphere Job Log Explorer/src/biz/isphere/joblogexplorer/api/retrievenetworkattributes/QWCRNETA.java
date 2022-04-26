/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.retrievenetworkattributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

/**
 * Retrieve Network Attributes (QWCRNETA) API
 */
public class QWCRNETA extends APIProgramCallDocument {

    private Set<Key> keys;
    private QWCRNETA_Result result;

    public enum Key {
        JOBACN ("JOBACN", 10), //$NON-NLS-1$
        LCLLOCNAME ("LCLLOCNAME", 8), //$NON-NLS-1$
        MSGQ ("MSGQ", 20), //$NON-NLS-1$
        OUTQ ("OUTQ", 20), //$NON-NLS-1$
        SYSNAME ("SYSNAME", 8); //$NON-NLS-1$

        private String key;
        private int length;

        private Key(String key, int length) {
            this.key = key;
            this.length = length;
        }

        public String value() {
            return key;
        }

        public int length() {
            return length;
        }
    }

    public QWCRNETA(AS400 system) {
        super(system, "QWCRNETA", "QSYS"); //$NON-NLS-1$ //$NON-NLS-2$

        keys = new HashSet<Key>();
    }

    public void addKey(Key key) {
        keys.add(key);
    }

    public boolean execute() {
        return doExecute(null);
    }

    public boolean execute(Key key) {
        return doExecute(key);
    }

    private boolean doExecute(Key key) {

        try {

            if (key != null) {
                keys.clear();
                addKey(key);
            }

            if (!execute(createParameterList(keys))) {
                // Xystem.out.println(getErrorMessage());
                return false;
            }

            result = new QWCRNETA_Result(getSystem());
            result.setBytes(getParameterList()[0].getOutputData());

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    public String getCharValue() {

        if (keys.size() != 1) {
            throw new IllegalAccessError("*** Do not use that method when retrieving more than one attribute ***"); //$NON-NLS-1$
        }

        Key key = keys.iterator().next();

        return getCharValue(key);
    }

    public String getCharValue(Key key) {

        try {

            QWCRNETA_Attribute attribute = result.getAttribute(key.value());
            if (attribute != null) {
                return attribute.getCharData();
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Failed to retrieve network attribute value ***", e); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * Produces the parameter list for calling the QUSRMBRD API.
     */
    protected ProgramParameter[] createParameterList(Set<Key> keys) throws Exception {

        // Calculate size according to the API description
        List<String> networkKeys = new ArrayList<String>();
        int totalLength = 0;
        for (Iterator<Key> iterator = keys.iterator(); iterator.hasNext();) {
            Key key = iterator.next();
            totalLength = totalLength + key.length() + 24;
            networkKeys.add(key.value());
        }
        totalLength = totalLength + 4;

        ProgramParameter[] parameterList = new ProgramParameter[5];
        parameterList[0] = new ProgramParameter(totalLength); // Receiver
        parameterList[1] = produceIntegerParameter(totalLength); // Length
        parameterList[2] = produceIntegerParameter(keys.size()); // Num attrs
        parameterList[3] = produceStringArrayParameter(networkKeys.toArray(new String[networkKeys.size()]), 10, false); // keys
        parameterList[4] = produceByteParameter(new APIErrorCode().getBytes());

        return parameterList;
    }
}
