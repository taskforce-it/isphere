/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.model;

import org.eclipse.rse.services.clientserver.messages.SystemMessageException;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;

public class ConnectionDelegate {

    private IBMiConnection connection;

    public boolean isConnected() {
        return connection.isConnected();
    }

    public String connect() {

        try {

            connection.connect();
            return null;

        } catch (SystemMessageException e) {
            return ExceptionHelper.getLocalizedMessage(e);
        }
    }

    public ConnectionDelegate(Object connection) {
        this.connection = (IBMiConnection)connection;
    }

    public static boolean instanceOf(Object object) {
        return (object instanceof IBMiConnection);
    }

    public static Object[] getConnections() {
        return IBMiConnection.getConnections();
    }

    public static Object getConnection(String connectionName) {
        return IBMiConnection.getConnection(connectionName);
    }

    public static String getConnectionName(Object connection) {
        return ((IBMiConnection)connection).getConnectionName();
    }

    public String getConnectionName() {
        return connection.getConnectionName();
    }

}
