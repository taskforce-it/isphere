/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class SPLF_setUser {

    public int run(AS400 _as400, String _user) {

        int errno = 0;

        try {

            ProgramCallDocument pcml = new ProgramCallDocument(_as400, "biz.isphere.core.spooledfiles.SPLF_setUser", this.getClass().getClassLoader());

            pcml.setValue("SPLF_setUser.user", _user);

            boolean rc = pcml.callProgram("SPLF_setUser");

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("SPLF_setUser");
                for (int idx = 0; idx < msgs.length; idx++) {
                    System.out.println(msgs[idx].getID() + " - " + msgs[idx].getText());
                }
                System.out.println("*** Call to SPLF_setUser failed. See messages above ***");

                errno = -1;

            } else {

                errno = 1;

            }

        } catch (PcmlException e) {

            errno = -1;

            // System.out.println(e.getLocalizedMessage());
            // e.printStackTrace();
            // System.out.println("*** Call to SPLF_setUser failed. ***");
            // return null;

        }

        return errno;

    }

}