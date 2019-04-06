/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class FNDSTR_getHandle {

    public int run(AS400 _as400) {

        int handle = 0;

        try {

            ProgramCallDocument pcml = new ProgramCallDocument(_as400, "biz.isphere.core.sourcefilesearch.FNDSTR_getHandle", this.getClass()
                .getClassLoader());

            boolean rc = pcml.callProgram("FNDSTR_getHandle");

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("FNDSTR_getHandle");
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null);
                }
                ISpherePlugin.logError("*** Call to FNDSTR_getHandle failed. See messages above ***", null);

                handle = -1;

            } else {

                handle = pcml.getIntValue("FNDSTR_getHandle.handle");

            }

        } catch (PcmlException e) {

            handle = -1;

            // Xystem.out.println(e.getLocalizedMessage());
            // e.printStackTrace();
            // Xystem.out.println("*** Call to FNDSTR_getHandle failed. ***");
            // return null;
            ISpherePlugin.logError("*** Call to FNDSTR_getHandle failed. See messages above ***", e);
        }

        return handle;

    }

}