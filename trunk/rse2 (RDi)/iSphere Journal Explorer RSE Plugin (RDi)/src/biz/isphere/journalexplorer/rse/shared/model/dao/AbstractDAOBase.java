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

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.journalexplorer.rse.Messages;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Date;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ProgramCall;

public abstract class AbstractDAOBase {
    // protected static final String properties = "thread used=false;
    // extendeddynamic=true; package criteria=select; package cache=true;";
    // //$NON-NLS-1$
    // protected static final String properties = "translate hex=binary;
    // prompt=false; extended dynamic=true; package cache=true"; //$NON-NLS-1$

    private String connectionName;
    private Connection connection;
    private String dateFormat;
    private String dateSeparator;
    private String timeSeparator;

    public AbstractDAOBase(String connectionName) throws Exception {

        this.connectionName = connectionName;

        if (getConnectionName() != null) {
            if (!IBMiHostContributionsHandler.isAvailable(getConnectionName())) {
                throw new Exception(Messages.bind(Messages.DAOBase_Connection_A_not_found, getConnectionName()));
            }
            if (!IBMiHostContributionsHandler.isConnected(getConnectionName())) {
                if (!IBMiHostContributionsHandler.connect(getConnectionName())) {
                    throw new Exception(Messages.bind(Messages.DAOBase_Failed_to_connect_to_A, getConnectionName()));
                }
            }

            ProgramCall call = new ProgramCall(IBMiHostContributionsHandler.getSystem(getConnectionName()));
            Job job = call.getServerJob();

            dateFormat = job.getDateFormat();

            if (dateFormat.startsWith("*")) { //$NON-NLS-1$
                dateFormat = dateFormat.substring(1);
            }

            dateSeparator = job.getDateSeparator();
            timeSeparator = job.getTimeSeparator();

            connection = IBMiHostContributionsHandler.getJdbcConnection(getConnectionName());
        } else
            throw new Exception(Messages.bind(Messages.DAOBase_Invalid_or_missing_connection_name_A, getConnectionName()));
    }

    public void destroy() {
    }

    protected Character getTimeSeparator() {
        return timeSeparator.charAt(0);
    }

    protected Character getDateSeparator() {
        return dateSeparator.charAt(0);
    }

    protected int getDateFormat() {
        return AS400Date.toFormat(dateFormat);
    }

    protected Connection getConnection() {
        return connection;
    }

    protected String getConnectionName() {
        return connectionName;
    }

    protected AS400 getSystem() throws Exception {
        return IBMiHostContributionsHandler.getSystem(getConnectionName());
    }
}
