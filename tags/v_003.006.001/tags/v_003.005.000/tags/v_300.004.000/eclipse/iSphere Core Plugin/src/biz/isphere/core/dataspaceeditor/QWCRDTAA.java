/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.PcmlProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;

public class QWCRDTAA {

    public String getType(AS400 anAS400, String aLibrary, String aDataArea) {
        return run(anAS400, aLibrary, aDataArea);
    }

    private String run(AS400 anAS400, String aLibrary, String aDataArea) {
        String type = null;

        try {

            PcmlProgramCallDocument pcml = new PcmlProgramCallDocument(anAS400,
                "biz.isphere.core.dataspaceeditor.QWCRDTAA", getClass().getClassLoader()); //$NON-NLS-1$
            pcml.setQualifiedObjectName("QWCRDTAA.dataArea", aLibrary, aDataArea); //$NON-NLS-1$
            pcml.setValue("QWCRDTAA.receiverLength", new Integer((pcml.getOutputsize("QWCRDTAA.receiver")))); //$NON-NLS-1$ //$NON-NLS-2$

            boolean rc = pcml.callProgram("QWCRDTAA"); //$NON-NLS-1$

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("QWCRDTAA"); //$NON-NLS-1$
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
                }
                ISpherePlugin.logError("*** Call to QWCRDTAA failed. See previous messages ***", null); //$NON-NLS-1$
                return null;

            } else {
                type = pcml.getStringValue("QWCRDTAA.receiver.type", 0); //$NON-NLS-1$
            }

        } catch (PcmlException e) {

            // Xystem.out.println(e.getLocalizedMessage());
            // e.printStackTrace();
            // Xystem.out.println("*** Call to QWCRDTAA failed. ***");
            // return null;
            ISpherePlugin.logError("Failed calling the QWCRDTAA API.", e);

        }

        return type;
    }

}
