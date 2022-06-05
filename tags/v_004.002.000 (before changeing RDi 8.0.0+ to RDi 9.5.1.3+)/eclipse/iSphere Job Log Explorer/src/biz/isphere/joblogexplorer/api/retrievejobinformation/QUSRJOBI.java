/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.api.retrievejobinformation;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

/**
 * Retrieve Job Information (QUSRJOBI) API
 */
public class QUSRJOBI extends APIProgramCallDocument {

    private String name;
    private String user;
    private String number;

    public QUSRJOBI(AS400 system) {
        super(system, "QUSRJOBI", "QSYS"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void setJob(String name) {
        setJob(name, "", ""); //$NON-NLS-1$//$NON-NLS-2$
    }

    public void setJob(String name, String user, String number) {

        this.name = name;
        this.user = user;
        this.number = number;
    }

    public boolean execute(JOBI0400 jobi0400) {

        try {

            if (!execute(createParameterList(jobi0400))) {
                return false;
            }

            jobi0400.setBytes(getParameterList()[0].getOutputData());
            jobi0400.loadValues();

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * Produces the parameter list for calling the QUSRMBRD API.
     */
    protected ProgramParameter[] createParameterList(JOBI0400 jobi0400) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[6];
        parameterList[0] = new ProgramParameter(jobi0400.getLength()); // Receiver
        parameterList[1] = produceIntegerParameter(jobi0400.getLength()); // Length
        parameterList[2] = produceStringParameter("JOBI0400", 8); // Format name //$NON-NLS-1$
        parameterList[3] = produceQualifiedJobName(name, user, number); // qJob
        parameterList[4] = produceStringParameter("", 16); // Int. job ID //$NON-NLS-1$
        parameterList[5] = produceByteParameter(new APIErrorCode().getBytes());

        return parameterList;
    }

}
