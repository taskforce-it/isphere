/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.standalone.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCDriver;

import biz.isphere.core.ISpherePlugin;

public class JdbcConnectionManager {

    private RemoteConnection ibmiConnection;
    private Map<String, Connection> jdbcConnections;

    public JdbcConnectionManager(RemoteConnection ibmiConnection) {
        this.ibmiConnection = ibmiConnection;
        this.jdbcConnections = new HashMap<String, Connection>();
    }

    public void closeAllConnections() {
        for (Connection connection : jdbcConnections.values()) {
            closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {

        try {
            if (!connection.isClosed()) {
                connection.close();
                debugPrint("Closed JDBC connection of connection: " + ibmiConnection.toString());
            }
        } catch (SQLException e) {
            ISpherePlugin.logError("*** Could not close JDBC connection ***", e);
        }
    }

    public Connection getJdbcConnection(Properties properties) {

        Connection jdbcConnection = getISphereJdbcConnection(ibmiConnection, properties);

        return jdbcConnection;
    }

    public static boolean isKerberosAuthentication() {
        return false;
    }

    private Connection getISphereJdbcConnection(RemoteConnection ibmiConnection, Properties properties) {

        Connection jdbcConnection = getJdbcConnectionFromCache(ibmiConnection, properties);
        if (jdbcConnection == null) {
            jdbcConnection = produceJDBCConnection(ibmiConnection, properties);
            debugPrint("Produced connection: " + ibmiConnection.toString());
        } else {
            debugPrint("Reused connection: " + ibmiConnection.toString());
        }

        return jdbcConnection;
    }

    private Connection produceJDBCConnection(RemoteConnection ibmiConnection, Properties properties) {

        Connection jdbcConnection = null;
        AS400JDBCDriver as400JDBCDriver = null;

        try {

            try {

                as400JDBCDriver = (AS400JDBCDriver)DriverManager.getDriver("jdbc:as400");

            } catch (SQLException e) {

                as400JDBCDriver = new AS400JDBCDriver();
                DriverManager.registerDriver(as400JDBCDriver);

            }

            AS400 system = ibmiConnection.getAS400ToolboxObject();
            jdbcConnection = as400JDBCDriver.connect(system, properties, null);

            addConnectionToCache(ibmiConnection, properties, jdbcConnection);

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not produce JDBC connection ***", e);
        }

        return jdbcConnection;
    }

    private Connection getJdbcConnectionFromCache(RemoteConnection ibmiConnection, Properties properties) {

        String connectionKey = getConnectionKey(ibmiConnection, properties);

        Connection jdbcConnection = jdbcConnections.get(connectionKey);
        if (jdbcConnection == null) {
            return null;
        }

        try {

            if (jdbcConnection.isClosed()) {
                jdbcConnection = null;
            }

        } catch (SQLException e) {
            jdbcConnection = null;
        }

        if (jdbcConnection == null) {
            jdbcConnections.remove(connectionKey);
        }

        return jdbcConnection;
    }

    private void addConnectionToCache(RemoteConnection ibmiConnection, Properties properties, Connection jdbcConnection) {
        jdbcConnections.put(getConnectionKey(ibmiConnection, properties), jdbcConnection);
    }

    private String getConnectionKey(RemoteConnection ibmiConnection, Properties properties) {
        return ibmiConnection.getConnectionName() + "|" + propertiesAsString(properties); //$NON-NLS-1$
    }

    private String propertiesAsString(Properties properties) {

        StringBuilder buffer = new StringBuilder();

        for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey() instanceof String) {
                if (entry.getValue() instanceof String) {
                    buffer.append((String)entry.getKey());
                    buffer.append("="); //$NON-NLS-1$
                    buffer.append((String)entry.getValue());
                    buffer.append(";"); //$NON-NLS-1$
                }
            }
        }

        return buffer.toString();
    }

    private void debugPrint(String debugMessage) {
        System.out.println(debugMessage);
    }
}
