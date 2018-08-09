/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.debugger.moduleviews;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

public class IQSDREGDV extends APIProgramCallDocument {

    public IQSDREGDV(AS400 system) {
        super(system, "IQSDREGDV", "ISPHEREDVP");
    }

    public IQSDREGDV(AS400 system, String connectionName) {
        super(system, "IQSDREGDV", IBMiHostContributionsHandler.getISphereLibrary(connectionName));
    }

    public boolean execute(DebuggerView debuggerView) {

        try {

            if (!execute(createParameterList(debuggerView))) {
                return false;
            }

            debuggerView.setId(getIntConverter().toInt(getParameterList()[0].getOutputData()));
            debuggerView.setLines(getIntConverter().toInt(getParameterList()[1].getOutputData()));

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    protected ProgramParameter[] createParameterList(DebuggerView debuggerView) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[9];
        parameterList[0] = produceIntegerParameter(-1); // o_viewid
        parameterList[1] = produceIntegerParameter(-1); // o_viewLines
        parameterList[2] = produceStringParameter("", 10); // o_rtnLib
        parameterList[3] = produceStringParameter("", 13); // o_viewTmstmp
        parameterList[4] = produceQualifiedObjectName(debuggerView.getObject(), debuggerView.getLibrary()); // i_qObj
        parameterList[5] = produceStringParameter(debuggerView.getObjectType(), 10); // i_objType
        parameterList[6] = produceStringParameter(debuggerView.getModule(), 10); // i_module
        parameterList[7] = produceIntegerParameter(debuggerView.getNumber()); // i_viewNumber
        parameterList[8] = produceByteParameter(new APIErrorCode().getBytes()); // io_errCode

        return parameterList;
    }
}
