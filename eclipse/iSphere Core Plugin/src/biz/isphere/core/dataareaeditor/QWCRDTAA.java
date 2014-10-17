/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor;

import biz.isphere.core.internal.APIProgramCallDocument;

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

            APIProgramCallDocument pcml = new APIProgramCallDocument(anAS400, "biz.isphere.core.dataareaeditor.QWCRDTAA", getClass().getClassLoader());
            pcml.setQualifiedObjectName("QWCRDTAA.dataArea", aLibrary, aDataArea);
            pcml.setValue("QWCRDTAA.receiverLength", new Integer((pcml.getOutputsize("QWCRDTAA.receiver"))));

            boolean rc = pcml.callProgram("QWCRDTAA");

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("QWCRDTAA");
                for (int idx = 0; idx < msgs.length; idx++) {
                    System.out.println(msgs[idx].getID() + " - " + msgs[idx].getText());
                }
                System.out.println("*** Call to QWCRDTAA failed. See messages above ***");
                return null;

            } else {
                type = pcml.getStringValue("QWCRDTAA.receiver.type", 0);
            }

        } catch (PcmlException e) {

//             System.out.println(e.getLocalizedMessage());
//             e.printStackTrace();
//             System.out.println("*** Call to QWCRDTAA failed. ***");
//             return null;

        }

        return type;
    }

}
