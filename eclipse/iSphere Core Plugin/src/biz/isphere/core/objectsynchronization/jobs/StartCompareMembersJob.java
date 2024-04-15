/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.jobs;

import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.runtime.SubMonitor;

import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objectsynchronization.SYNCMBR_getHandle;

/**
 * This class is the first job of the synchronize members task. It stores the
 * objects (file or library) that are compared in file SYNCMBRW.
 * <p>
 * The compare members jobs are executed in the following sequence:
 * <ol>
 * <li>{@link StartCompareMembersJob}
 * <li>{@link ResolveGenericFilesJob}
 * <li>{@link ResolveGenericMembersJob}
 * <li>{@link LoadCompareMembersJob}
 * <li>{@link FinishCompareMembersJob}
 * </ol>
 */
public class StartCompareMembersJob extends AbstractCompareMembersJob {

    private static final String ALL_OBJECTS = "*"; //$NON-NLS-1$

    private RemoteObject leftObject;
    private RemoteObject rightObject;
    private String objectType;

    public StartCompareMembersJob(SubMonitor monitor, CompareMembersSharedJobValues sharedValues, RemoteObject leftObject, RemoteObject rightObject) {
        super(monitor, sharedValues);

        this.leftObject = leftObject;
        this.rightObject = rightObject;

        this.objectType = leftObject.getObjectType();
    }

    protected int getNumWorkItems() {
        return 2;
    }

    protected void execute(SubMonitor monitor) {

        SubMonitor subMonitor = split(monitor, 2);

        try {

            CompareMembersSharedJobValues sharedValues = getSharedValues();

            sharedValues.setLeftHandle(null, ERROR_HANDLE);
            sharedValues.setRightHandle(null, ERROR_HANDLE);

            consume(subMonitor, Messages.Task_Preparing);
            int handle = doSystem(subMonitor, leftObject.getConnectionName(), SyncMbrMode.LEFT_SYSTEM);

            if (handle != ERROR_HANDLE) {
                sharedValues.setLeftHandle(leftObject.getConnectionName(), handle);

                consume(subMonitor, Messages.Task_Preparing);
                handle = doSystem(subMonitor, rightObject.getConnectionName(), SyncMbrMode.RIGHT_SYSTEM);

                if (handle != ERROR_HANDLE) {
                    sharedValues.setRightHandle(rightObject.getConnectionName(), handle);
                } else {
                    ISpherePlugin.logError("*** Could not get SYNCMBR handle for *RIGHT system ***", null); //$NON-NLS-1$
                }
            } else {
                ISpherePlugin.logError("*** Could not get SYNCMBR handle for *LEFT system ***", null); //$NON-NLS-1$
            }

        } finally {
            done(subMonitor);
        }
    }

    private int doSystem(SubMonitor subMonitor, String connectionName, SyncMbrMode mode) {

        if (!initialize(connectionName)) {
            return ERROR_HANDLE;
        }

        try {

            if (subMonitor.isCanceled()) {
                return ERROR_HANDLE;
            }

            if (!setCurrentLibrary()) {
                return ERROR_HANDLE;
            }

            int handle = new SYNCMBR_getHandle().run(getSystem());
            if (handle <= 0) {
                return ERROR_HANDLE;
            }

            String sqlInsert;
            if (SyncMbrMode.LEFT_SYSTEM.equals(mode)) {
                sqlInsert = getSqlInsertLeftSystems(handle);
            } else if (SyncMbrMode.RIGHT_SYSTEM.equals(mode)) {
                sqlInsert = getSqlInsertRightSystems(handle);
            } else {
                throw new IllegalArgumentException("Unsupported mode value: " + mode.mode()); //$NON-NLS-1$
            }

            Statement statementInsert = null;

            try {
                statementInsert = getJdbcConnection().createStatement();
                statementInsert.executeUpdate(sqlInsert);
            } catch (SQLException e) {
                ISpherePlugin.logError("*** Could not insert compare elements into SYNCMBRW ***", e); //$NON-NLS-1$
            }

            if (statementInsert != null) {
                try {
                    statementInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return handle;

        } finally {
            restoreCurrentLibrary();
        }
    }

    private String getSqlInsertLeftSystems(int handle) {

        StringBuffer sqlInsert = new StringBuffer();
        sqlInsert
            .append("INSERT INTO " + getSqlHelper().getObjectName(getISphereLibrary(), "SYNCMBRW") + " (XWHDL, XWMBR, XWLEFTLIB, XWLEFTFILE) VALUES"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        sqlInsert.append("('"); //$NON-NLS-1$
        sqlInsert.append(handle);
        sqlInsert.append("', '"); //$NON-NLS-1$
        sqlInsert.append(ALL_OBJECTS); // All members.

        if (ISeries.FILE.equals(objectType)) {
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(leftObject.getLibrary()); // Left library.
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(leftObject.getName()); // Left source file.
        } else if (ISeries.LIB.equals(objectType)) {
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(leftObject.getName()); // Left library.
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(ALL_OBJECTS); // All left source files.
        } else {
            throw new IllegalArgumentException("Incorrect object type: " + objectType); //$NON-NLS-1$
        }

        sqlInsert.append("')"); //$NON-NLS-1$

        return sqlInsert.toString();
    }

    private String getSqlInsertRightSystems(int handle) {

        StringBuffer sqlInsert = new StringBuffer();
        sqlInsert
            .append("INSERT INTO " + getSqlHelper().getObjectName(getISphereLibrary(), "SYNCMBRW") + " (XWHDL, XWMBR, XWRGHTLIB, XWRGHTFILE) VALUES"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        sqlInsert.append("('"); //$NON-NLS-1$
        sqlInsert.append(handle);
        sqlInsert.append("', '"); //$NON-NLS-1$
        sqlInsert.append(ALL_OBJECTS); // All members.

        if (ISeries.FILE.equals(objectType)) {
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(rightObject.getLibrary()); // Right library.
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(rightObject.getName()); // Right source file.
        } else if (ISeries.LIB.equals(objectType)) {
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(rightObject.getName()); // Right library.
            sqlInsert.append("', '"); //$NON-NLS-1$
            sqlInsert.append(ALL_OBJECTS); // All right files.
        } else {
            throw new IllegalArgumentException("Incorrect object type: " + objectType); //$NON-NLS-1$
        }

        sqlInsert.append("')"); //$NON-NLS-1$

        return sqlInsert.toString();
    }

    @Override
    protected boolean isSameSystem() {

        boolean isSameSystem;
        if (IBMiHelper.isSameSystem(leftObject.getSystem(), rightObject.getSystem())) {
            isSameSystem = true;
        } else {
            isSameSystem = false;
        }

        getSharedValues().setSameSystem(isSameSystem);

        return isSameSystem;
    }
}