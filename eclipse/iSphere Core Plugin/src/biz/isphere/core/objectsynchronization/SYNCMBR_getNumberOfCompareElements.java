/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

import biz.isphere.core.ISpherePlugin;

public class SYNCMBR_getNumberOfCompareElements {

    public int run(AS400 _as400, int handle, String mode) {

        int numberOfSearchElements = 0;

        try {

            ProgramCallDocument pcml = new ProgramCallDocument(_as400, "biz.isphere.core.objectsynchronization.SYNCMBR_getNumberOfCompareElements",
                this.getClass().getClassLoader());

            pcml.setIntValue("SYNCMBR_getNumberOfCompareElements.handle", handle);
            pcml.setStringValue("SYNCMBR_getNumberOfCompareElements.mode", mode);

            boolean rc = pcml.callProgram("SYNCMBR_getNumberOfCompareElements");

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("SYNCMBR_getNumberOfCompareElements");
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null);
                }
                ISpherePlugin.logError("*** Call to SYNCMBR_getNumberOfCompareElements failed. See messages above ***", null);

                numberOfSearchElements = -1;

            } else {

                numberOfSearchElements = pcml.getIntValue("SYNCMBR_getNumberOfCompareElements.numberOfSearchElements");

            }

        } catch (PcmlException e) {

            numberOfSearchElements = -1;
            ISpherePlugin.logError("*** Call to SYNCMBR_getNumberOfCompareElements failed. See messages above ***", e);
        }

        return numberOfSearchElements;

    }

}