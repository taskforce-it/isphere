/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.eclipse.core.runtime.SubMonitor;

import com.ibm.as400.access.AS400;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.SqlHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;

/**
 * This class is the base class for all worker jobs that take part when
 * comparing members in the <i>iSphere Synchronize Members</i> editor.
 */
public abstract class AbstractCompareMembersJob {

    protected static final int ERROR_HANDLE = -1;

    private SubMonitor monitor;
    private CompareMembersSharedJobValues sharedValues;

    private String connectionName;
    private AS400 system;
    private String currentLibrary;
    private String iSphereLibrary;
    private Connection jdbcConnection;
    private SqlHelper sqlHelper;

    private boolean isDone;

    /**
     * Produces a new compare members job.
     * 
     * @param monitor - progress monitor
     * @param sharedValues - values shared between compare jobs
     */
    public AbstractCompareMembersJob(SubMonitor monitor, CompareMembersSharedJobValues sharedValues) {

        this.monitor = monitor;
        this.sharedValues = sharedValues;

        this.isDone = false;
    }

    /**
     * Initializes the compare job. This method must be called first.
     * 
     * @param connectionName - name of the connection the objects reside on
     */
    protected boolean initialize(String connectionName) {

        if (connectionName == null) {
            return false;
        }

        if (connectionName.equals(this.connectionName)) {
            return true;
        }

        this.connectionName = connectionName;
        this.system = IBMiHostContributionsHandler.getSystem(connectionName);
        this.currentLibrary = null;
        this.iSphereLibrary = null;
        this.jdbcConnection = null;
        this.sqlHelper = null;

        return true;
    }

    protected CompareMembersSharedJobValues getSharedValues() {
        return sharedValues;
    }

    protected boolean isSameSystem() {
        return getSharedValues().isSameSystem();
    }

    protected boolean setCurrentLibrary() {

        try {

            currentLibrary = ISphereHelper.getCurrentLibrary(system);
            ISphereHelper.setCurrentLibrary(system, getISphereLibrary());
            return true;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not set current library.", e); //$NON-NLS-1$
            return false;
        }
    }

    protected void restoreCurrentLibrary() {

        if (currentLibrary == null) {
            return;
        }

        try {
            ISphereHelper.setCurrentLibrary(system, currentLibrary);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not restore current library to: " + currentLibrary + " ***", e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    protected AS400 getSystem() {
        return system;
    }

    protected String getISphereLibrary() {

        if (iSphereLibrary == null) {
            iSphereLibrary = ISpherePlugin.getISphereLibrary(connectionName);
        }

        return iSphereLibrary;
    }

    protected Connection getJdbcConnection() {

        if (jdbcConnection == null) {
            jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(connectionName);
        }

        return jdbcConnection;
    }

    protected SqlHelper getSqlHelper() {

        if (sqlHelper == null) {
            sqlHelper = new SqlHelper(getJdbcConnection());
        }

        return sqlHelper;
    }

    public void run() {
        if (isDone) {
            return;
        }
        execute(monitor);
        isDone = true;
    }

    protected SubMonitor split(SubMonitor subMonitor, int numSubTasks) {

        if (subMonitor == null) {
            return null;
        }

        if (!subMonitor.isCanceled()) {
            subMonitor = subMonitor.newChild(numSubTasks);
        }

        return subMonitor;
    }

    protected void consume(SubMonitor subMonitor, String subTaskName) {

        if (subMonitor == null) {
            return;
        }

        subMonitor.setTaskName(subTaskName);
        if (!subMonitor.isCanceled()) {
            subMonitor.worked(1);
        }
    }

    protected void done(SubMonitor subMonitor) {

        if (subMonitor == null) {
            return;
        }

        subMonitor.done();
    }

    protected void cancel(int handle, Exception e) {

        cancelHostJob(handle);

        monitor.setCanceled(true);
        ISpherePlugin.logError("*** Resolving members: Unexpected error. ***", e);
        MessageDialogAsync.displayBlockingError(ExceptionHelper.getLocalizedMessage(e));
    }

    protected void cancelHostJob(int handle) {

        SqlHelper sqlHelper = getSqlHelper();

        PreparedStatement preparedStatementUpdate = null;
        try {
            preparedStatementUpdate = getJdbcConnection()
                .prepareStatement("UPDATE " + sqlHelper.getObjectName(getISphereLibrary(), "SYNCMBRS") + " SET XSCNL = '*YES' WHERE XSHDL = ?");
            preparedStatementUpdate.setInt(1, handle);
            preparedStatementUpdate.executeUpdate();
        } catch (SQLException e) {
            ISpherePlugin.logError("*** Could not cancel host job of load synchronize members job ***", e);
        }
        if (preparedStatementUpdate != null) {
            try {
                preparedStatementUpdate.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

    }

    protected abstract int getNumWorkItems();

    protected abstract void execute(SubMonitor monitor);
}
