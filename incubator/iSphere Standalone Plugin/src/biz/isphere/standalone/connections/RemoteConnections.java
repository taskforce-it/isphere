/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.standalone.connections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.as400.access.AS400;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.standalone.exceptions.ConnectionExistException;
import biz.isphere.standalone.exceptions.ConnectionNotFoundException;

public final class RemoteConnections {

    /**
     * The instance of this Singleton class.
     */
    private static RemoteConnections instance;

    /**
     * List of available connections.
     */
    private Map<String, RemoteConnection> connections;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private RemoteConnections() {
        this.connections = new HashMap<String, RemoteConnection>();
        loadConnections();
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static RemoteConnections getInstance() {
        if (instance == null) {
            instance = new RemoteConnections();
        }
        return instance;
    }

    public boolean hasConnection(String connectionName) {
        return connections.containsKey(connectionName);
    }

    public RemoteConnection getConnection(String connectionName) {
        return connections.get(connectionName);
    }

    public AS400 getSystem(String connectionName) {

        RemoteConnection connection = getConnection(connectionName);
        if (connection == null) {
            return null;
        }

        return connection.getAS400ToolboxObject();
    }

    public String[] getConnectionNames() {
        List<String> connectionNames = new LinkedList<String>();
        for (String connectionName : connections.keySet()) {
            connectionNames.add(connectionName);
        }
        return connectionNames.toArray(new String[connectionNames.size()]);
    }

    public synchronized RemoteConnection addConnection(RemoteConnection connection) throws ConnectionExistException {
        if (connections.containsKey(connection.getConnectionName())) {
            throw new ConnectionExistException(connection.getConnectionName());
        }
        return connections.put(connection.getConnectionName(), connection);
    }

    public synchronized void removeConnection(String connectionName) throws ConnectionExistException, ConnectionNotFoundException {
        if (!connections.containsKey(connectionName)) {
            throw new ConnectionNotFoundException(connectionName);
        }
        connections.remove(connectionName);
    }

    public void closeAllConnections() {
        debugPrint("Closing all connections...");
        for (RemoteConnection connection : connections.values()) {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }

    private void loadConnections() {
        RemoteConnection connection = new RemoteConnection("iSphere");
        String host = System.getProperty("HOST");
        connection.setHostName(host);
        connection.setUserName(System.getProperty("USER"));
        connection.setPassword(System.getProperty("PASSWORD"));
        connections.put(connection.getConnectionName(), connection);

        try {
            connection.connect();
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not connect to " + host + " ***", e);
        }
    }

    private void debugPrint(String debugMessage) {
        System.out.println(debugMessage);
    }
}
