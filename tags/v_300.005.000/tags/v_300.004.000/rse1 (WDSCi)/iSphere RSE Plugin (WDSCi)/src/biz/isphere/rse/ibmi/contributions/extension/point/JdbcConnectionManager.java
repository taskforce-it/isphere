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
import java.util.Properties;
import java.util.Map.Entry;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCDriver;
import com.ibm.etools.iseries.core.ISeriesSystemToolbox;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.as400cmdsubsys.CmdSubSystem;
import com.ibm.etools.systems.subsystems.CommunicationsEvent;
import com.ibm.etools.systems.subsystems.ICommunicationsListener;

public class JdbcConnectionManager implements ICommunicationsListener {

    private ISeriesConnection ISeriesConnection;
    private Map<String, Connection> jdbcConnections;

    public JdbcConnectionManager(ISeriesConnection ISeriesConnection) {

        this.ISeriesConnection = ISeriesConnection;
        this.jdbcConnections = new HashMap<String, Connection>();

        CmdSubSystem cmdSubSystem = ISeriesConnection.getISeriesCmdSubSystem();
        ISeriesSystemToolbox connectorService = (ISeriesSystemToolbox)cmdSubSystem.getSystem();
        connectorService.addCommunicationsListener(this);
    }

    public void communicationsStateChange(CommunicationsEvent ce) {

        if (ce.getState() == CommunicationsEvent.AFTER_CONNECT) {

            // TODO:

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
                jdbcConnection = getKerberosJdbcConnection(ISeriesConnection, properties);
            } else if (isISphereJdbcConnectionManager()) {
                jdbcConnection = getISphereJdbcConnection(ISeriesConnection, properties);
            }
        } else {
            jdbcConnection = getStandardIBMiJdbcConnection(ISeriesConnection, properties);
        }

        return jdbcConnection;
    }

    public boolean isKerberosAuthentication() {
        return IBMiHostContributionsHandler.isKerberosAuthentication();
    }

    private boolean isISphereJdbcConnectionManager() {
        return Preferences.getInstance().isISphereJdbcConnectionManager();
    }

    private Connection getStandardIBMiJdbcConnection(ISeriesConnection ISeriesConnection, Properties properties) {

        Connection jdbcConnection = null;

        try {

            jdbcConnection = ISeriesConnection.getJDBCConnection(null, false);

        } catch (Throwable e) {
            return null;
        }

        return jdbcConnection;
    }

    private Connection getISphereJdbcConnection(ISeriesConnection ISeriesConnection, Properties properties) {
        return getKerberosJdbcConnection(ISeriesConnection, properties);
    }

    private Connection getKerberosJdbcConnection(ISeriesConnection ISeriesConnection, Properties properties) {

        Connection jdbcConnection = getJdbcConnectionFromCache(ISeriesConnection, properties);
        if (jdbcConnection == null) {
            jdbcConnection = produceJDBCConnection(ISeriesConnection, properties);
        }

        return jdbcConnection;
    }

    private Connection produceJDBCConnection(ISeriesConnection ISeriesConnection, Properties properties) {

        Connection jdbcConnection = null;
        AS400JDBCDriver as400JDBCDriver = null;

        try {

            try {

                as400JDBCDriver = (AS400JDBCDriver)DriverManager.getDriver("jdbc:as400");

            } catch (SQLException e) {

                as400JDBCDriver = new AS400JDBCDriver();
                DriverManager.registerDriver(as400JDBCDriver);

            }

            AS400 system = ISeriesConnection.getAS400ToolboxObject(null);
            jdbcConnection = as400JDBCDriver.connect(system, properties, null);

            addConnectionToCache(ISeriesConnection, properties, jdbcConnection);

        } catch (Throwable e) {
        }

        return jdbcConnection;
    }

    private Connection getJdbcConnectionFromCache(ISeriesConnection ISeriesConnection, Properties properties) {

        String connectionKey = getConnectionKey(ISeriesConnection, properties);

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

    private void addConnectionToCache(ISeriesConnection ISeriesConnection, Properties properties, Connection jdbcConnection) {
        jdbcConnections.put(getConnectionKey(ISeriesConnection, properties), jdbcConnection);
    }

    private String getConnectionKey(ISeriesConnection ISeriesConnection, Properties properties) {
        return ISeriesConnection.getConnectionName() + "|" + propertiesAsString(properties); //$NON-NLS-1$
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
