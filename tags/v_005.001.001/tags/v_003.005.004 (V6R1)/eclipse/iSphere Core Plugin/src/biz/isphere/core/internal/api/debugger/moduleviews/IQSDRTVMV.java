/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.debugger.moduleviews;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

/**
 * This class is a wrapper for the iSphere Retrieve Module Views (QSDRTVMV) API.
 * 
 * @author Thomas Raddatz
 */
public class IQSDRTVMV extends APIProgramCallDocument {

    public static final String SDMV0100 = "SDMV0100";

    public IQSDRTVMV(AS400 system, String iSphereLibraryName) {
        super(system, "IQSDRTVMV", iSphereLibraryName);
    }

    public boolean execute(IQSDRTVMVResult result, String object, String library, String objectType, String module) {

        try {

            if (!execute(createParameterList(result, object, library, objectType, module))) {
                return false;
            }

            result.setObject(object);
            result.setLibrary(library);
            result.setObjectType(objectType);
            result.setBytes(getParameterList()[0].getOutputData());

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    protected ProgramParameter[] createParameterList(IQSDRTVMVResult result, String object, String library, String objectType, String module)
        throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[7];
        parameterList[0] = new ProgramParameter(result.getBytes().length); // io_rcvVar
        parameterList[1] = produceIntegerParameter(result.getBytes().length); // i_lenRcvVar
        parameterList[2] = produceStringParameter(result.getFormat(), 8); // i_format
        parameterList[3] = produceQualifiedObjectName(object, library); // i_qObj
        parameterList[4] = produceStringParameter(objectType, 10); // i_objType
        parameterList[5] = produceStringParameter(module, 10); // i_module
        parameterList[6] = produceByteParameter(new APIErrorCode().getBytes()); // io_errCode

        return parameterList;
    }
}
