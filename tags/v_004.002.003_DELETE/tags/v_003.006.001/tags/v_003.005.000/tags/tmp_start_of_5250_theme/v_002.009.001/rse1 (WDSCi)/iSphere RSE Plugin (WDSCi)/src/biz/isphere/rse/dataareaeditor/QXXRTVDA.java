/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataareaeditor;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.PcmlProgramCallDocument;
import biz.isphere.core.internal.exception.ErrorLoggedException;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;

public class QXXRTVDA {

    private AS400 system;
    private String dataArea;
    private String library;

    public QXXRTVDA(AS400 anAS400, String aLibrary, String aDataArea) {
        this.system = anAS400;
        this.dataArea = aDataArea;
        this.library = aLibrary;
    }

    public byte[] run(int length) throws Exception {

        PcmlProgramCallDocument pcml = new PcmlProgramCallDocument(system, "biz.isphere.rse.dataareaeditor.QXXRTVDA", getClass().getClassLoader()); //$NON-NLS-1$
        pcml.setQualifiedObjectName("QXXRTVDA.dataArea", library, dataArea); //$NON-NLS-1$
        pcml.setValue("QXXRTVDA.start", new Integer(1)); //$NON-NLS-1$
        pcml.setValue("QXXRTVDA.length", new Integer(length)); //$NON-NLS-1$

        boolean rc = pcml.callProgram("QXXRTVDA"); //$NON-NLS-1$

        if (rc == false) {

            AS400Message[] msgs = pcml.getMessageList("QXXRTVDA"); //$NON-NLS-1$
            for (int idx = 0; idx < msgs.length; idx++) {
                ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
            }
            String message = "*** Call to QXXRTVDA failed. See previous messages ***";
            throw new ErrorLoggedException(message);

        } else {

            byte[] bytes = (byte[])pcml.getValue("QXXRTVDA.value"); //$NON-NLS-1$
            return bytes;
        }
    }
}
