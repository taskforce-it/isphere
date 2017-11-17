/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.internal;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.PcmlProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.data.PcmlException;

public class QSYRUSRI {

    private PcmlProgramCallDocument pcml;

    public void retrieveUserProfile(AS400 anAS400, String aUserProfile) {
        run(anAS400, aUserProfile);
    }

    private void run(AS400 anAS400, String aUserProfile) {

        try {

            pcml = new PcmlProgramCallDocument(anAS400, "biz.isphere.messagesubsystem.internal.QSYRUSRI", getClass().getClassLoader()); //$NON-NLS-1$
            pcml.setValue("QSYRUSRI.userProfile", aUserProfile); //$NON-NLS-1$
            pcml.setValue("QSYRUSRI.receiverLength", new Integer((pcml.getOutputsize("QSYRUSRI.receiver")))); //$NON-NLS-1$ //$NON-NLS-2$

            boolean rc = pcml.callProgram("QSYRUSRI"); //$NON-NLS-1$

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("QSYRUSRI"); //$NON-NLS-1$
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
                }
                ISpherePlugin.logError("*** Call to QSYRUSRI failed. See previous messages ***", null); //$NON-NLS-1$

            }

        } catch (PcmlException e) {
            ISpherePlugin.logError("Failed calling the QSYRUSRI API.", e); //$NON-NLS-1$
        }
    }

    public String getMessageQueuePath() throws PcmlException {

        String name = pcml.getStringValue("QSYRUSRI.receiver.msgQ", 0); //$NON-NLS-1$
        String library = pcml.getStringValue("QSYRUSRI.receiver.msgQLib", 0); //$NON-NLS-1$
        QSYSObjectPathName pathName = new QSYSObjectPathName(library, name, "MSGQ"); //$NON-NLS-1$

        return pathName.getPath();
    }
}
