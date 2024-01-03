/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class XFNDSTR_clear {

    public int run(AS400 _as400, int handle) {

        int errno = 0;

        try {

            ProgramCallDocument pcml = new ProgramCallDocument(_as400, "biz.isphere.core.messagefilesearch.XFNDSTR_clear", this.getClass()
                .getClassLoader());

            pcml.setIntValue("XFNDSTR_clear.handle", handle);

            boolean rc = pcml.callProgram("XFNDSTR_clear");

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("XFNDSTR_clear");
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null);
                }
                ISpherePlugin.logError("*** Call to XFNDSTR_clear failed. See messages above ***", null);

                errno = -1;

            } else {

                errno = 1;

            }

        } catch (PcmlException e) {

            errno = -1;

            // Xystem.out.println(e.getLocalizedMessage());
            // e.printStackTrace();
            // Xystem.out.println("*** Call to XFNDSTR_clear failed. ***");
            // return null;
            ISpherePlugin.logError("*** Call to XFNDSTR_clear failed. See messages above ***", e);
        }

        return errno;

    }

}