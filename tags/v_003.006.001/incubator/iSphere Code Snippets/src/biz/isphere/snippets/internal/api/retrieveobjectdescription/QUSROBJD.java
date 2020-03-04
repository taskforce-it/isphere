/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.snippets.internal.api.retrieveobjectdescription;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.PcmlProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.data.PcmlException;

/**
 * Retrieve Object Description (QUSROBJD) API.
 */
public class QUSROBJD {

    /**
     * Special values for the library name.
     */
    public static final String LIBRARY_LIBL = "*LIBL"; //$NON-NLS-1$
    public static final String LIBRARY_CURLIB = "*CURLIB"; //$NON-NLS-1$

    /**
     * Special values for the Auxiliary storage pool (ASP) search type.
     */
    public static final String ASP_SEARCH_TYPE_ASP = "*ASP"; //$NON-NLS-1$
    public static final String ASP_SEARCH_TYPE_ASPGRP = "*ASPGRP"; //$NON-NLS-1$

    /**
     * Special values for the Auxiliary storage pool (ASP) device name.
     */
    public static final String ASP_DEVICE_CURRENT = "*"; //$NON-NLS-1$

    private AS400 system;
    private PcmlProgramCallDocument pcml;

    public QUSROBJD(AS400 system) {
        this.system = system;
    }

    public OBJD0200 retrieveObjectDescription(String library, String object, String type) {
        if (type.startsWith("*")) { //$NON-NLS-1$
            type = type.substring(1);
        }
        return run(new QSYSObjectPathName(library, object, type));
    }

    public OBJD0200 retrieveObjectDescription(QSYSObjectPathName pathName) {
        return run(pathName);
    }

    private OBJD0200 run(QSYSObjectPathName pathName) {

        OBJD0200 objd0200 = new OBJD0200();

        try {

            pcml = new PcmlProgramCallDocument(system, "biz.isphere.core.internal.api.retrieveobjectdescription.QUSROBJD", getClass() //$NON-NLS-1$
                .getClassLoader()); //$NON-NLS-1$
            pcml.setQualifiedObjectName("QUSROBJD.objectName", pathName.getLibraryName(), pathName.getObjectName()); //$NON-NLS-1$
            pcml.setValue("QUSROBJD.objectType", getObjectType(pathName)); //$NON-NLS-1$

            boolean rc = pcml.callProgram("QUSROBJD"); //$NON-NLS-1$

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("QUSROBJD"); //$NON-NLS-1$
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
                    objd0200.setErrorMEssage(msgs[idx]);
                    if (msgs[idx].getType() == AS400Message.ESCAPE) {
                        break;
                    }
                }
                ISpherePlugin.logError("*** Call to QUSROBJD failed. See previous messages ***", null); //$NON-NLS-1$

            } else {

                objd0200.setBytesReturned(pcml.getIntValue("QUSROBJD.receiver.bytesReturned")); //$NON-NLS-1$
                objd0200.setBytesAvailable(pcml.getIntValue("QUSROBJD.receiver.bytesAvailable")); //$NON-NLS-1$

                if (objd0200.getBytesReturned() > 8) {
                    objd0200.setObjectName(pcml.getStringValue("QUSROBJD.receiver.objectName")); //$NON-NLS-1$
                    objd0200.setLibraryName(pcml.getStringValue("QUSROBJD.receiver.libraryName")); //$NON-NLS-1$
                    objd0200.setObjectType(pcml.getStringValue("QUSROBJD.receiver.objectType")); //$NON-NLS-1$
                    objd0200.setLibraryReturned(pcml.getStringValue("QUSROBJD.receiver.libraryReturned")); //$NON-NLS-1$

                    // objd0200.setAuxiliaryStoragePoolASPNumber(auxiliaryStoragePoolASPNumber);
                    //
                    // objd0200.setObjectOwner(objectOwner)
                    // objd0200.setObjectDomain(objectDomain)
                    //
                    // objd0200.setCreationTime(creationTime)
                    // objd0200.setLastChangedTime(lastChangedTime)
                    //
                    // objd0200.setExtendedObjectAttriute(pcml.getStringValue(""));
                    // objd0200.setTextDescription(textDescription)
                    // objd0200.setSourceFile(sourceFile)
                    // objd0200.setSourceFileLibrary(libraryName)
                    // objd0200.setSourceMember(sourceMember)
                }
            }

        } catch (PcmlException e) {
            ISpherePlugin.logError("Failed calling the QUSROBJD API.", e); //$NON-NLS-1$
        }

        return objd0200;
    }

    private Object getObjectType(QSYSObjectPathName pathName) {
        return "*" + pathName.getObjectType(); //$NON-NLS-1$
    }

    public static void main(String[] args) {

        String hostname = System.getProperty("isphere.junit.as400"); //$NON-NLS-1$
        String user = System.getProperty("isphere.junit.username"); //$NON-NLS-1$
        String password = System.getProperty("isphere.junit.password"); //$NON-NLS-1$

        AS400 system = new AS400(hostname, user, password);

        OBJD0200 objd0200 = null;
        QUSROBJD main = new QUSROBJD(system);
        objd0200 = main.retrieveObjectDescription(new QSYSObjectPathName("*LIBL", "RADDATZ", "LIB")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (objd0200.getErrorMessage() != null) {
            System.out.println("Error: " + objd0200.getErrorMessage().getText()); //$NON-NLS-1$
        } else {
            System.out.println("Library returned: " + objd0200.getLibraryReturned()); //$NON-NLS-1$
        }

        objd0200 = main.retrieveObjectDescription("*LIBL", "XRADDATZ", "LIB"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        if (objd0200.getErrorMessage() != null) {
            System.out.println("Error: " + objd0200.getErrorMessage().getText()); //$NON-NLS-1$
        } else {
            System.out.println("Library returned: " + objd0200.getLibraryReturned()); //$NON-NLS-1$
        }

    }
}
