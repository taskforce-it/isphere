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

public class SYNCMBR_clear {

    public int run(AS400 _as400, int handle) {

        int errno = 0;

        try {

            ProgramCallDocument pcml = new ProgramCallDocument(_as400, "biz.isphere.core.objectsynchronization.SYNCMBR_clear", //$NON-NLS-1$
                this.getClass().getClassLoader());

            pcml.setIntValue("SYNCMBR_clear.handle", handle);

            boolean rc = pcml.callProgram("SYNCMBR_clear");

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("SYNCMBR_clear");
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
                }
                ISpherePlugin.logError("*** Call to SYNCMBR_clear failed. See messages above ***", null); //$NON-NLS-1$

                errno = -1;

            } else {

                errno = 1;

            }

        } catch (PcmlException e) {

            errno = -1;
            ISpherePlugin.logError("*** Call to SYNCMBR_clear failed. See messages above ***", e); //$NON-NLS-1$
        }

        return errno;

    }

}