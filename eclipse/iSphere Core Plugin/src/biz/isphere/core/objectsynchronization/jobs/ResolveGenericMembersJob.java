/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.core.runtime.SubMonitor;

import biz.isphere.base.internal.SqlHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.objectsynchronization.CompareOptions;
import biz.isphere.core.objectsynchronization.SYNCMBR_resolveGenericMembers;

/**
 * This class resolves generic members stored in file SYNCMBRW.
 * 
 * @see {@link StartCompareMembersJob}
 */
public class ResolveGenericMembersJob extends AbstractCompareMembersJob {

    private static final int THREAD_WAIT_TIMEOUT_IN_MILLISECONDS = 30000;

    public ResolveGenericMembersJob(SubMonitor monitor, CompareMembersSharedJobValues sharedValues) {
        super(monitor, sharedValues);
    }

    protected int getNumWorkItems() {
        return 2;
    }

    @Override
    protected void execute(SubMonitor monitor) {

        SubMonitor subMonitor = split(monitor, 2);

        try {

            CompareMembersSharedJobValues sharedValues = getSharedValues();
            CompareOptions compareOptions = getSharedValues().getCompareOptions();

            consume(subMonitor, Messages.bind(Messages.Task_Resolving_generic_items, 0));
            resolveGenericMembers(subMonitor, sharedValues.getLeftHandle(), sharedValues.getLeftConnectionName(), SyncMbrMode.LEFT_SYSTEM,
                compareOptions.getMemberFilter(), compareOptions.isRegEx());

            consume(subMonitor, Messages.bind(Messages.Task_Resolving_generic_items, 0));
            resolveGenericMembers(subMonitor, sharedValues.getRightHandle(), sharedValues.getRightConnectionName(), SyncMbrMode.RIGHT_SYSTEM,
                compareOptions.getMemberFilter(), compareOptions.isRegEx());

        } finally {
            done(subMonitor);
        }
    }

    private void resolveGenericMembers(SubMonitor subMonitor, int handle, String connectionName, SyncMbrMode mode, String memberFilter,
        boolean isRegEx) {

        if (!initialize(connectionName)) {
            return;
        }

        try {

            if (subMonitor.isCanceled()) {
                return;
            }

            if (handle == ERROR_HANDLE) {
                return;
            }

            if (!setCurrentLibrary()) {
                return;
            }

            StatusObserverThread statusObserverThread = new StatusObserverThread(subMonitor, handle, mode);
            statusObserverThread.start();

            new SYNCMBR_resolveGenericMembers().run(getSystem(), handle, mode.mode(), memberFilter, isRegEx);

            statusObserverThread.joinChecked(THREAD_WAIT_TIMEOUT_IN_MILLISECONDS);

        } finally {
            restoreCurrentLibrary();
        }

    }

    private class StatusObserverThread extends Thread {

        private SubMonitor monitor;
        private int handle;
        private SyncMbrMode mode;

        private PreparedStatement preparedStatementSelect;

        private int countMembersResolved;
        private int countMembersFound;
        private String statusCanceled;

        public StatusObserverThread(SubMonitor monitor, int handle, SyncMbrMode mode) {
            this.monitor = monitor;
            this.handle = handle;
            this.mode = mode;
        }

        private void initialize() throws SQLException {

            SqlHelper sqlHelper = getSqlHelper();

            preparedStatementSelect = getJdbcConnection().prepareStatement(
                "SELECT XSCNTMBR, XSCNTFND, XSCNL FROM " + sqlHelper.getObjectName(getISphereLibrary(), "SYNCMBRS") + " WHERE XSHDL = ?");
            preparedStatementSelect.setInt(1, handle);
        }

        private boolean getStatus() {

            boolean isAlive = true;
            ResultSet resultSet = null;

            try {

                resultSet = preparedStatementSelect.executeQuery();
                if (resultSet.next()) {
                    countMembersResolved = resultSet.getInt("XSCNTMBR");
                    countMembersFound = resultSet.getInt("XSCNTFND");
                    statusCanceled = resultSet.getString("XSCNL");
                    if (countMembersResolved == -1 || "*YES".equals(statusCanceled)) {
                        isAlive = false;
                    }
                } else {
                    isAlive = false;
                }

            } catch (Exception e) {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e1) {
                        ISpherePlugin.logError("*** ResolveGenericMembersJob: Could not close result set. ***", e1);
                    }
                }
                cancel(handle, e);
            }

            return isAlive;
        }

        private void cleanUp() {

            if (preparedStatementSelect != null) {
                try {
                    preparedStatementSelect.close();
                } catch (Exception e) {
                    cancel(handle, e);
                }
            }
        }

        @Override
        public void run() {

            try {

                initialize();

                boolean isAlive = true;
                while (isAlive && !monitor.isCanceled()) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }

                    isAlive = getStatus();
                    monitor.setTaskName(Messages.bind(Messages.Task_Resolving_generic_items, countMembersFound));
                }

            } catch (Exception e) {
                cancel(handle, e);
            } finally {
                cleanUp();
            }

        }

        public void joinChecked(long timeoutInMilliseconds) {
            try {
                join(timeoutInMilliseconds);
            } catch (InterruptedException e) {
            }
        }

    }
}
