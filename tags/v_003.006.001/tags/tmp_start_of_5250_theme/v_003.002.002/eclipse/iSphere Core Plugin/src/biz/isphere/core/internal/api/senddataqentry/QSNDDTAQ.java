/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.senddataqentry;

import java.beans.PropertyVetoException;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

public class QSNDDTAQ extends APIProgramCallDocument {

    private String dataQueue;
    private String library;

    public QSNDDTAQ(AS400 system) throws PropertyVetoException {
        super(system, "QSNDDTAQ", "QSYS");
    }

    public void setDataQueue(String name, String library) {

        this.dataQueue = name;
        this.library = library;

    }

    public boolean execute(Object data) {
        return execute(null, data);
    }

    public boolean execute(Object key, Object data) {

        validateInputData(data);

        try {

            if (!execute(createParameterList(key, data))) {
                return false;
            }

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    private void validateInputData(Object object) {

        if (object instanceof String) {
            return;
        }

        if (object instanceof byte[]) {
            return;
        }

        throw new IllegalArgumentException("Invalid input data: " + object.getClass().getName());
    }

    /**
     * Produces the parameter list for calling the QSZRTVPR API.
     */
    protected ProgramParameter[] createParameterList(Object key, Object data) throws Exception {

        ProgramParameter[] parameterList;
        if (key == null) {
            parameterList = new ProgramParameter[4];
        } else {
            parameterList = new ProgramParameter[6];
        }

        parameterList[0] = produceStringParameter(dataQueue, 10);
        parameterList[1] = produceStringParameter(library, 10);

        if (data instanceof String) {
            String string = (String)data;
            parameterList[2] = producePackedDecimalParameter(string.length(), 5, 0);
            parameterList[3] = produceStringParameter(string, string.length());
        } else {
            byte[] bytes = (byte[])data;
            parameterList[2] = producePackedDecimalParameter(bytes.length, 5, 0);
            parameterList[3] = produceByteParameter(bytes);
        }

        if (parameterList.length >= 6) {
            if (key instanceof String) {
                String string = (String)key;
                parameterList[4] = producePackedDecimalParameter(string.length(), 3, 0);
                parameterList[5] = produceStringParameter(string, string.length());
            } else {
                byte[] bytes = (byte[])key;
                parameterList[4] = producePackedDecimalParameter(bytes.length, 3, 0);
                parameterList[5] = produceByteParameter(bytes);
            }
        }

        return parameterList;
    }
}
