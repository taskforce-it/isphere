/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibmi.contributions.extension.point;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.rse.core.subsystems.CommunicationsEvent;
import org.eclipse.rse.core.subsystems.ICommunicationsListener;
import org.eclipse.rse.core.subsystems.IConnectorService;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.rse.ISphereRSEPlugin;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCDriver;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.commands.QSYSCommandSubSystem;

public class JdbcConnectionManager implements ICommunicationsListener {

    private IBMiConnection ibmiConnection;
    private Map<String, Connection> jdbcConnections;

    public JdbcConnectionManager(IBMiConnection ibmiConnection) {

        this.ibmiConnection = ibmiConnection;
        this.jdbcConnections = new HashMap<String, Connection>();

        QSYSCommandSubSystem cmdSubSystem = ibmiConnection.getCommandSubSystem();
        IConnectorService connectorService = cmdSubSystem.getConnectorService();
        connectorService.addCommunicationsListener(this);
    }

    public void communicationsStateChange(CommunicationsEvent ce) {

        if (ce.getState() == CommunicationsEvent.AFTER_CONNECT) {

            // Nothing to do here (so far).

        } else if (ce.getState() == CommunicationsEvent.BEFORE_DISCONNECT) {

            Collection<Connection> tJdbcConnections = jdbcConnections.values();
            for (Connection connection : tJdbcConnections) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            jdbcConnections.clear();
        }
    }

    public boolean isPassiveCommunicationsListener() {
        return true;
    }

    public Connection getJdbcConnection(Properties properties) {

        Connection jdbcConnection = null;

        if (isISphereJdbcConnectionManager()) {
            if (isKerberosAuthentication()) {
                jdbcConnection = getKerberosJdbcConnection(ibmiConnection, properties);
            } else if (isISphereJdbcConnectionManager()) {
                jdbcConnection = getISphereJdbcConnection(ibmiConnection, properties);
            }
        } else {
            jdbcConnection = getStandardIBMiJdbcConnection(ibmiConnection, properties);
        }

        return jdbcConnection;
    }

    public boolean isKerberosAuthentication() {
        return IBMiHostContributionsHandler.isKerberosAuthentication();
    }

    private boolean isISphereJdbcConnectionManager() {
        return Preferences.getInstance().isISphereJdbcConnectionManager();
    }

    private Connection getStandardIBMiJdbcConnection(IBMiConnection ibmiConnection, Properties properties) {

        Connection jdbcConnection = null;

        try {

            jdbcConnection = ibmiConnection.getJDBCConnection(null, false);

        } catch (Throwable e) {
            return null;
        }

        return jdbcConnection;
    }

    private Connection getISphereJdbcConnection(IBMiConnection ibmiConnection, Properties properties) {
        return getKerberosJdbcConnection(ibmiConnection, properties);
    }

    private Connection getKerberosJdbcConnection(IBMiConnection ibmiConnection, Properties properties) {

        Connection jdbcConnection = getJdbcConnectionFromCache(ibmiConnection, properties);
        if (jdbcConnection == null) {
            jdbcConnection = produceJDBCConnection(ibmiConnection, properties);
        }

        return jdbcConnection;
    }

    private Connection produceJDBCConnection(IBMiConnection ibmiConnection, Properties properties) {

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
            ISphereRSEPlugin.logError("*** Could not produce JDBC connection ***", e);
        }

        return jdbcConnection;
    }

    private Connection getJdbcConnectionFromCache(IBMiConnection ibmiConnection, Properties properties) {

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

    private void addConnectionToCache(IBMiConnection ibmiConnection, Properties properties, Connection jdbcConnection) {
        jdbcConnections.put(getConnectionKey(ibmiConnection, properties), jdbcConnection);
    }

    private String getConnectionKey(IBMiConnection ibmiConnection, Properties properties) {
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
}
