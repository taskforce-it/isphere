/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataspace.rse;

import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.RemoteObject;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CharacterDataArea;

/**
 * Class that serves as a universal data provider for data area and user space
 * objects.
 * <p>
 * In terms of iSphere these objects are called 'data space objects', which is a
 * made-up of 'Data Area' and 'User Space'.
 */
public class WrappedDataSpace extends AbstractWrappedDataSpace {

    public WrappedDataSpace(AS400 as400, RemoteObject remoteObject) throws Exception {
        super(as400, remoteObject);
    }

    public WrappedDataSpace(RemoteObject remoteObject) throws Exception {
        super(remoteObject);
    }

    protected AS400 getSystem(String qualifiedConnectionName) throws Exception {
        AS400 system = IBMiHostContributionsHandler.getSystem(qualifiedConnectionName);
        return system;
    }

    protected byte[] loadCharacterDataAreaBytes(CharacterDataArea characterDataArea) throws Exception {
        byte[] bytes = new byte[characterDataArea.getLength()];
        characterDataArea.read(bytes, 0, 0, characterDataArea.getLength());
        return bytes;
    }

    protected void saveCharacterDataAreaBytes(CharacterDataArea characterDataArea, byte[] bytes) throws Exception {
        characterDataArea.write(bytes, 0, 0, characterDataArea.getLength());
    }
}
