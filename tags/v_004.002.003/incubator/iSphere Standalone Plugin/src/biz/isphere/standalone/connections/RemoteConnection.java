/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.standalone.connections;

import java.sql.Connection;
import java.util.Properties;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.SecureAS400;

public class RemoteConnection {

    private String connectionName;
    private String hostName;
    private String userName;
    private String password;
    private boolean isOffline;

    private transient JdbcConnectionManager jdbcConnectionManager;
    private transient AS400 system;

    public RemoteConnection(String connectionName) {
        this.connectionName = connectionName;
        this.isOffline = false;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public boolean isKerberosAuthentication() {
        return false;
    }

    public void setOffline(boolean offline) {

        if (isConnected()) {
            disconnect();
        }

        this.isOffline = offline;
    }

    public boolean isConnected() {

        if (system != null && system.isConnected()) {
            return true;
        }

        return false;
    }

    public boolean connect() throws Exception {

        if (isOffline()) {
            return false;
        }

        if (isConnected()) {
            return true;
        }

        system = new AS400(hostName, userName, password);
        system.connectService(AS400.COMMAND);
        jdbcConnectionManager = new JdbcConnectionManager(this);
        boolean connected = system.isConnected(AS400.COMMAND);

        if (connected) {
            debugPrint("Connected to: " + getConnectionName());
        } else {
            debugPrint("Failed connecting to: " + getConnectionName());
        }

        return connected;
    }

    public void disconnect() {

        if (isConnected()) {
            debugPrint("Disconnecting: " + getConnectionName());
            jdbcConnectionManager.closeAllConnections();
            system.disconnectAllServices();
            system = null;
            jdbcConnectionManager = null;
        }
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AS400 getAS400ToolboxObject() {
        return system;
    }

    /**
     * Returns a JDBC connection.
     * 
     * @return RemoteConnection
     */
    public Connection getJdbcConnection() {
        return getJdbcConnection(null);
    }

    /**
     * Returns a JDBC connection.
     * 
     * @param properties - JDBC connection properties
     * @return RemoteConnection
     */
    public Connection getJdbcConnection(Properties properties) {

        if (properties == null) {
            properties = new Properties();
            properties.put("prompt", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.put("big decimal", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            if (getAS400ToolboxObject() instanceof SecureAS400) {
                properties.put("secure", "true"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        Connection jdbcConnection = jdbcConnectionManager.getJdbcConnection(properties);

        return jdbcConnection;
    }

    private void debugPrint(String debugMessage) {
        System.out.println(debugMessage);
    }

    @Override
    public String toString() {
        return connectionName + " (host=" + hostName + ", user=" + userName + ")";
    }
}
