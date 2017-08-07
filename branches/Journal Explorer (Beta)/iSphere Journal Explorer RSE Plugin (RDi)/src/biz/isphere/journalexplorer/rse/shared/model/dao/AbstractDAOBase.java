/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.model.dao;

import java.sql.Connection;

import biz.isphere.journalexplorer.rse.Messages;

import com.ibm.as400.access.AS400Date;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public abstract class AbstractDAOBase {
    protected static final String properties = "thread used=false;extendeddynamic=true;package criteria=select;package cache=true;"; //$NON-NLS-1$

    protected IBMiConnection ibmiConnection;
    private Connection connection;
    private String dateFormat;
    private String dateSeparator;

    public AbstractDAOBase(String connectionName) throws Exception {
        if (connectionName != null) {
            this.ibmiConnection = IBMiConnection.getConnection(connectionName);
            if (!ibmiConnection.isConnected()) {
                if (!ibmiConnection.connect()) {
                    throw new Exception(Messages.DAOBase_ConnectionNotStablished);
                }
            }

            this.dateFormat = this.ibmiConnection.getQSYSJobSubSystem().getServerJob(null).getInternationalProperties().getDateFormat();
            if (this.dateFormat.startsWith("*")) {
                this.dateFormat = this.dateFormat.substring(1);
            }
            this.dateSeparator = this.ibmiConnection.getQSYSJobSubSystem().getServerJob(null).getInternationalProperties().getDateSeparator();
            this.connection = ibmiConnection.getJDBCConnection("", true); //$NON-NLS-1$
            this.connection.setAutoCommit(false);
        } else
            throw new Exception(Messages.DAOBase_InvalidConnectionObject);
    }

    public void destroy() {
    }

    protected int getDateFormat() {
        return AS400Date.toFormat(dateFormat);
    }
    
    protected Connection getConnection() {
        return connection;
    }

    protected String getConnectionName() {
        return ibmiConnection.getConnectionName();
    }
}
