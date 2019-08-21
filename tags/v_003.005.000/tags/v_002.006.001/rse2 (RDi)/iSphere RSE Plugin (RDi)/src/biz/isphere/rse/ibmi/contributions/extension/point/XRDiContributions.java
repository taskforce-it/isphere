/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibmi.contributions.extension.point;

import java.sql.Connection;

import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class XRDiContributions implements IIBMiHostContributions {

    public AS400 getSystem(String connectionName) {

        return getSystem(null, connectionName);
    }

    public AS400 getSystem(String profile, String connectionName) {

        IBMiConnection connection = getConnection(profile, connectionName);
        if (connection == null) {
            return null;
        }

        try {
            return connection.getAS400ToolboxObject();
        } catch (Throwable e) {
            return null;
        }
    }

    public Connection getJdbcConnection(String connectionName) {

        return getJdbcConnection(null, connectionName);
    }

    public Connection getJdbcConnection(String profile, String connectionName) {

        IBMiConnection connection = getConnection(profile, connectionName);
        if (connection == null) {
            return null;
        }

        try {
            return connection.getJDBCConnection(null, false);
        } catch (Throwable e) {
            return null;
        }
    }

    private IBMiConnection getConnection(String profile, String connectionName) {

        if (profile == null) {
            return IBMiConnection.getConnection(connectionName);
        }

        return IBMiConnection.getConnection(profile, connectionName);
    }
}
