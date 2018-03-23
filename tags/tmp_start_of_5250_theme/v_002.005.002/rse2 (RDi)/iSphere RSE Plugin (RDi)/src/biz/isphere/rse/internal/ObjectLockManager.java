/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.rse.services.clientserver.messages.SystemMessageException;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.AbstractObjectLockManager;
import biz.isphere.core.internal.ObjectLock;
import biz.isphere.rse.Messages;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.api.ISeriesMessage;

public class ObjectLockManager extends AbstractObjectLockManager {

    public ObjectLockManager(int waitSeconds) {
        super(waitSeconds);
    }
    
    @Override
    protected String[] allocateObject(ObjectLock objectLock, int lockWaitTime) {
        
        return executeCommand(objectLock.getConnectionName(), objectLock.getAllocateCommand(lockWaitTime));
    }

    @Override
    protected String[] deallocateObject(ObjectLock objectLock) {

        return executeCommand(objectLock.getConnectionName(), objectLock.getDeallocateCommand());
    }

    protected String[] executeCommand(String connectionName, String command) {

        IBMiConnection connection = IBMiConnection.getConnection(connectionName);
        if (connection == null) {
            return new String[] { Messages.Failed_to_get_connection_colon + " " + connectionName }; //$NON-NLS-1$
        }

        ISeriesMessage[] cmdMessages = null;

        try {

            cmdMessages = connection.runCommand(command);
            if (cmdMessages == null) {
                cmdMessages = new ISeriesMessage[] {};
            }

        } catch (SystemMessageException e) {
            ISpherePlugin.logError(e.getMessage(), e);
            return new String[] { e.getLocalizedMessage() };
        }

        String[] errorMessages = new String[cmdMessages.length];
        for (int i = 0; i < cmdMessages.length; i++) {
            ISeriesMessage cmdMessage = cmdMessages[i];
            errorMessages[i] = cmdMessage.getMessageID() + ": " + cmdMessage.getMessageText(); //$NON-NLS-1$
        }
        return errorMessages;
    }
}
