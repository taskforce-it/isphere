/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Vector;

import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.events.ISystemRemoteChangeEvents;
import org.eclipse.rse.core.model.ISystemRegistry;

public class SpooledFileMessageAction extends AbstractSpooledFileAction {

    @Override
    public String execute(SpooledFileResource spooledFileResource) {

        String message = spooledFileResource.getSpooledFile().replyMessage();

        if (message == null) {
            ISystemRegistry sr = RSECorePlugin.getTheSystemRegistry();
            Vector<SpooledFileResource> spooledFileVector = new Vector<SpooledFileResource>();
            spooledFileVector.addElement(spooledFileResource);
            sr.fireRemoteResourceChangeEvent(ISystemRemoteChangeEvents.SYSTEM_REMOTE_RESOURCE_CREATED, spooledFileVector, null, null, null, null);
        }

        return message;

    }

}