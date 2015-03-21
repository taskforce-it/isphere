/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.retrieveproductinformation;

import java.beans.PropertyVetoException;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

public class QSZRTVPR extends APIProgramCallDocument {

    public QSZRTVPR(AS400 system) throws PropertyVetoException {
        super(system, "QSZRTVPR", "QSYS");
    }

    public boolean execute(PRDR0000 productLoadInformation, PRDI0100 productInformation) {

        try {

            if (!execute(createParameterList(productLoadInformation, productInformation))) {
                return false;
            }

            productLoadInformation.setBytes(getParameterList()[0].getOutputData());

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * Produces the parameter list for calling the QSZRTVPR API.
     */
    protected ProgramParameter[] createParameterList(PRDR0000 productLoadInformation, PRDI0100 productInformation) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[5];
        parameterList[0] = new ProgramParameter(productLoadInformation.getLength());
        parameterList[1] = produceIntegerParameter(productLoadInformation.getLength());
        parameterList[2] = produceStringParameter(productLoadInformation.getName(), 8);
        parameterList[3] = produceByteParameter(productInformation.getBytes());
        parameterList[4] = produceByteParameter(new APIErrorCode().getBytes());

        return parameterList;
    }

    public static void main(String[] args) {

        try {

            AS400 system = new AS400("ghentw.gfd.de", "WEBUSER", "WEBUSER");
            PRDI0100 prdi0100 = new PRDI0100(system);
            PRDR0100 prdr0100 = new PRDR0100(system);
            QSZRTVPR main = new QSZRTVPR(system);
            if (main.execute(prdr0100, prdi0100)){
                System.out.println(prdr0100.getReleaseLevel());
            } else {
                System.out.println(main.getMessageList()[0]);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
